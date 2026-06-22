package org.fusesource.jansi;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.fusesource.jansi.internal.CLibrary;
import org.fusesource.jansi.internal.CLibrary.WinSize;
import org.fusesource.jansi.internal.struct.CONSOLE_SCREEN_BUFFER_INFO;
import org.fusesource.jansi.internal.MingwSupport;
import org.fusesource.jansi.io.AnsiOutputStream;
import org.fusesource.jansi.io.AnsiProcessor;
import org.fusesource.jansi.io.FastBufferedOutputStream;
import org.fusesource.jansi.io.WindowsAnsiProcessor;

import static org.fusesource.jansi.internal.CLibrary.ioctl;
import static org.fusesource.jansi.internal.Kernel32.GetConsoleMode;
import static org.fusesource.jansi.internal.Kernel32.GetConsoleScreenBufferInfo;
import static org.fusesource.jansi.internal.Kernel32.GetStdHandle;
import static org.fusesource.jansi.internal.Kernel32.SetConsoleMode;

final class AnsiStreamBuilder {

    private static final Logger LOG = Logger.getLogger(AnsiStreamBuilder.class.getName());

    static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("win");
    static final boolean IS_CYGWIN = IS_WINDOWS && System.getenv("PWD") != null && System.getenv("PWD").startsWith("/");
    static final boolean IS_MSYSTEM = IS_WINDOWS && System.getenv("MSYSTEM") != null
            && (System.getenv("MSYSTEM").startsWith("MINGW") || System.getenv("MSYSTEM").equals("MSYS"));
    static final boolean IS_CONEMU = IS_WINDOWS && System.getenv("ConEmuPID") != null;
    static final int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 0x0004;

    private static final AtomicInteger virtualProcessing = new AtomicInteger(0);

    private AnsiStreamBuilder() {}

    static AnsiPrintStream ansiStream(StreamTarget target) {
        final OutputStream rawOut = new FastBufferedOutputStream(new FileOutputStream(target.fileDescriptor));

        TtyResult tty = TtyDetector.detectTty(target.fd);

        final StreamConfig config;
        if (!tty.isAtty) {
            AnsiType type = tty.hadException ? AnsiType.Unsupported : AnsiType.Redirected;
            config = new StreamConfig(null, type, null, null, new AnsiOutputStream.ZeroWidthSupplier());
        } else if (IS_WINDOWS) {
            config = buildWindowsStream(rawOut, target);
        } else {
            config = buildUnixStream(target.fd);
        }

        AnsiMode mode = AnsiPropertyResolver.resolveMode(target.modeProperty, tty.isAtty);
        AnsiColors colors = AnsiPropertyResolver.resolveColors(target.colorsProperty);
        Charset cs = AnsiPropertyResolver.resolveCharset(target);

        boolean resetAtUninstall = config.type != AnsiType.Unsupported && !AnsiPropertyResolver.getBoolean(AnsiConsole.JANSI_NORESET);

        return newPrintStream(
                new AnsiOutputStream(
                        rawOut, config.width, mode, config.processor, config.type,
                        colors, cs, config.installer, config.uninstaller, resetAtUninstall),
                cs.name());
    }

    private static StreamConfig buildUnixStream(final int fd) {
        AnsiOutputStream.WidthSupplier width = () -> {
            WinSize sz = new WinSize();
            ioctl(fd, CLibrary.TIOCGWINSZ, sz);
            return sz.ws_col;
        };
        return new StreamConfig(null, AnsiType.Native, null, null, width);
    }

    private static StreamConfig buildWindowsStream(OutputStream out, StreamTarget target) {
        final long console = GetStdHandle(target.stdHandle);
        final int[] mode = new int[1];
        final boolean isConsole = GetConsoleMode(console, mode) != 0;

        final AnsiOutputStream.WidthSupplier kernel32Width = () -> {
            CONSOLE_SCREEN_BUFFER_INFO info = new CONSOLE_SCREEN_BUFFER_INFO();
            GetConsoleScreenBufferInfo(console, info);
            return info.windowWidth();
        };

        if (isConsole && SetConsoleMode(console, mode[0] | ENABLE_VIRTUAL_TERMINAL_PROCESSING) != 0) {
            SetConsoleMode(console, mode[0]);
            return new StreamConfig(
                    null, AnsiType.VirtualTerminal,
                    () -> {
                        synchronized (AnsiConsole.class) {
                            virtualProcessing.getAndIncrement();
                            SetConsoleMode(console, mode[0] | ENABLE_VIRTUAL_TERMINAL_PROCESSING);
                        }
                    },
                    () -> {
                        synchronized (AnsiConsole.class) {
                            if (virtualProcessing.decrementAndGet() == 0) {
                                SetConsoleMode(console, mode[0]);
                            }
                        }
                    },
                    kernel32Width);
        }

        if ((IS_CONEMU || IS_CYGWIN || IS_MSYSTEM) && !isConsole) {
            MingwSupport mingw = new MingwSupport();
            String name = mingw.getConsoleName(target.isStdout());
            AnsiOutputStream.WidthSupplier mingwWidth =
                    (name != null && !name.isEmpty()) ? () -> mingw.getTerminalWidth(name) : () -> -1;
            return new StreamConfig(null, AnsiType.Native, null, null, mingwWidth);
        }

        AnsiProcessor processor;
        AnsiType type;
        try {
            processor = new WindowsAnsiProcessor(out, console);
            type = AnsiType.Emulation;
        } catch (Throwable e) {
            LOG.fine("WindowsAnsiProcessor unavailable (stream likely redirected): " + e);
            processor = new AnsiProcessor(out);
            type = AnsiType.Unsupported;
        }
        return new StreamConfig(processor, type, null, null, kernel32Width);
    }

    private static AnsiPrintStream newPrintStream(AnsiOutputStream ansiOut, String enc) {
        if (enc != null) {
            try {
                return new AnsiPrintStream(ansiOut, true, enc);
            } catch (UnsupportedEncodingException e) {
                LOG.fine("Unsupported encoding '" + enc + "' for AnsiPrintStream: " + e.getMessage());
            }
        }
        return new AnsiPrintStream(ansiOut, true);
    }
}