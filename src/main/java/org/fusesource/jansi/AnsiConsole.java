package org.fusesource.jansi;

import java.io.IOError;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Provides consistent access to an ANSI-aware console PrintStream, or an
 * ANSI-stripping PrintStream when not running on a terminal.
 *
 * <p>Call {@link #systemInstall()} once at application startup to replace
 * {@code System.out} and {@code System.err} with ANSI-aware streams.
 * Call {@link #systemUninstall()} when done. Both methods are reference-counted,
 * so multiple libraries can safely install/uninstall independently.</p>
 */
public class AnsiConsole {

    public static final String JANSI_MODE = "jansi.mode";
    public static final String JANSI_OUT_MODE = "jansi.out.mode";
    public static final String JANSI_ERR_MODE = "jansi.err.mode";

    public static final String JANSI_MODE_STRIP = "strip";
    public static final String JANSI_MODE_FORCE = "force";
    public static final String JANSI_MODE_DEFAULT = "default";

    public static final String JANSI_COLORS = "jansi.colors";
    public static final String JANSI_OUT_COLORS = "jansi.out.colors";
    public static final String JANSI_ERR_COLORS = "jansi.err.colors";

    public static final String JANSI_COLORS_16 = "16";
    public static final String JANSI_COLORS_256 = "256";
    public static final String JANSI_COLORS_TRUECOLOR = "truecolor";

    public static final String JANSI_NORESET = "jansi.noreset";
    public static final String JANSI_GRACEFUL = "jansi.graceful";

    @Deprecated public static final String JANSI_PASSTHROUGH = "jansi.passthrough";
    @Deprecated public static final String JANSI_STRIP = "jansi.strip";
    @Deprecated public static final String JANSI_FORCE = "jansi.force";

    @Deprecated public static PrintStream system_out = System.out;
    @Deprecated public static PrintStream out;
    @Deprecated public static PrintStream system_err = System.err;
    @Deprecated public static PrintStream err;

    private static PrintStream savedSystemOut = System.out;
    private static PrintStream savedSystemErr = System.err;

    private static final AtomicInteger installCount = new AtomicInteger(0);
    private static boolean initialized = false;

    private AnsiConsole() {}

    public static AnsiPrintStream out() {
        initStreams();
        return (AnsiPrintStream) out;
    }

    public static PrintStream sysOut() {
        return savedSystemOut;
    }

    public static AnsiPrintStream err() {
        initStreams();
        return (AnsiPrintStream) err;
    }

    public static PrintStream sysErr() {
        return savedSystemErr;
    }

    public static int getTerminalWidth() {
        int w = out().getTerminalWidth();
        if (w <= 0) {
            w = err().getTerminalWidth();
        }
        return w;
    }

    public static synchronized void systemInstall() {
        if (installCount.get() == 0) {
            initStreams();
            try {
                ((AnsiPrintStream) out).install();
                ((AnsiPrintStream) err).install();
            } catch (IOException e) {
                throw new IOError(e);
            }
            System.setOut(out);
            System.setErr(err);
        }
        installCount.incrementAndGet();
    }

    public static synchronized boolean isInstalled() {
        return installCount.get() > 0;
    }

    public static synchronized void systemUninstall() {
        if (installCount.decrementAndGet() == 0) {
            try {
                ((AnsiPrintStream) out).uninstall();
                ((AnsiPrintStream) err).uninstall();
            } catch (IOException e) {
                throw new IOError(e);
            }
            initialized = false;
            System.setOut(savedSystemOut);
            System.setErr(savedSystemErr);
        }
    }

    static synchronized void initStreams() {
        if (!initialized) {
            out = AnsiStreamBuilder.ansiStream(StreamTarget.STDOUT);
            err = AnsiStreamBuilder.ansiStream(StreamTarget.STDERR);
            initialized = true;
        }
    }
}