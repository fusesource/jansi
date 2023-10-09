/*
 * Copyright (C) 2009-2023 the original author(s).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fusesource.jansi;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import org.fusesource.jansi.internal.OSInfo;
import org.fusesource.jansi.internal.stty.Stty;
import org.fusesource.jansi.io.AnsiOutputStream;
import org.fusesource.jansi.io.AnsiProcessor;
import org.fusesource.jansi.io.FastBufferedOutputStream;

import static org.fusesource.jansi.internal.AnsiConsoleSupport.CLibrary.STDERR_FILENO;
import static org.fusesource.jansi.internal.AnsiConsoleSupport.CLibrary.STDOUT_FILENO;
import static org.fusesource.jansi.internal.AnsiConsoleSupportHolder.getCLibrary;
import static org.fusesource.jansi.internal.AnsiConsoleSupportHolder.getKernel32;

/**
 * Provides consistent access to an ANSI aware console PrintStream or an ANSI codes stripping PrintStream
 * if not on a terminal (see
 * <a href="http://fusesource.github.io/jansi/documentation/native-api/index.html?org/fusesource/jansi/internal/CLibrary.html">Jansi native
 * CLibrary isatty(int)</a>).
 * <p>The native library used is named <code>jansi</code> and is loaded using <a href="http://fusesource.github.io/hawtjni/">HawtJNI</a> Runtime
 * <a href="http://fusesource.github.io/hawtjni/documentation/api/index.html?org/fusesource/hawtjni/runtime/Library.html"><code>Library</code></a>
 *
 * @since 1.0
 * @see #systemInstall()
 * @see #out()
 * @see #err()
 * @see #ansiStream(boolean) for more details on ANSI mode selection
 */
public class AnsiConsole {

    /**
     * The default mode which Jansi will use, can be either <code>force</code>, <code>strip</code>
     * or <code>default</code> (the default).
     * If this property is set, it will override <code>jansi.passthrough</code>,
     * <code>jansi.strip</code> and <code>jansi.force</code> properties.
     */
    public static final String JANSI_MODE = "jansi.mode";
    /**
     * Jansi mode specific to the standard output stream.
     */
    public static final String JANSI_OUT_MODE = "jansi.out.mode";
    /**
     * Jansi mode specific to the standard error stream.
     */
    public static final String JANSI_ERR_MODE = "jansi.err.mode";

    /**
     * Jansi mode value to strip all ansi sequences.
     */
    public static final String JANSI_MODE_STRIP = "strip";
    /**
     * Jansi mode value to force ansi sequences to the stream even if it's not a terminal.
     */
    public static final String JANSI_MODE_FORCE = "force";
    /**
     * Jansi mode value that output sequences if on a terminal, else strip them.
     */
    public static final String JANSI_MODE_DEFAULT = "default";

    /**
     * The default color support that Jansi will use, can be either <code>16</code>,
     * <code>256</code> or <code>truecolor</code>.  If not set, JANSI will try to
     * autodetect the number of colors supported by the terminal by checking the
     * <code>COLORTERM</code> and <code>TERM</code> variables.
     */
    public static final String JANSI_COLORS = "jansi.colors";
    /**
     * Jansi colors specific to the standard output stream.
     */
    public static final String JANSI_OUT_COLORS = "jansi.out.colors";
    /**
     * Jansi colors specific to the standard error stream.
     */
    public static final String JANSI_ERR_COLORS = "jansi.err.colors";

    /**
     * Force the use of 16 colors. When using a 256-indexed color, or an RGB
     * color, the color will be rounded to the nearest one from the 16 palette.
     */
    public static final String JANSI_COLORS_16 = "16";
    /**
     * Force the use of 256 colors. When using an RGB color, the color will be
     * rounded to the nearest one from the standard 256 palette.
     */
    public static final String JANSI_COLORS_256 = "256";
    /**
     * Force the use of 24-bit colors.
     */
    public static final String JANSI_COLORS_TRUECOLOR = "truecolor";

    /**
     * If the <code>jansi.passthrough</code> system property is set to true, will not perform any transformation
     * and any ansi sequence will be conveyed without any modification.
     *
     * @deprecated use {@link #JANSI_MODE} instead
     */
    @Deprecated
    public static final String JANSI_PASSTHROUGH = "jansi.passthrough";
    /**
     * If the <code>jansi.strip</code> system property is set to true, and <code>jansi.passthrough</code>
     * is not enabled, all ansi sequences will be stripped before characters are written to the output streams.
     *
     * @deprecated use {@link #JANSI_MODE} instead
     */
    @Deprecated
    public static final String JANSI_STRIP = "jansi.strip";
    /**
     * If the <code>jansi.force</code> system property is set to true, and neither <code>jansi.passthrough</code>
     * nor <code>jansi.strip</code> are set, then ansi sequences will be printed to the output stream.
     * This forces the behavior which is by default dependent on the output stream being a real console: if the
     * output stream is redirected to a file or through a system pipe, ansi sequences are disabled by default.
     *
     * @deprecated use {@link #JANSI_MODE} instead
     */
    @Deprecated
    public static final String JANSI_FORCE = "jansi.force";
    /**
     * If the <code>jansi.eager</code> system property is set to true, the system streams will be eagerly
     * initialized, else the initialization is delayed until {@link #out()}, {@link #err()} or {@link #systemInstall()}
     * is called.
     *
     * @deprecated this property has been added but only for backward compatibility.
     * @since 2.1
     */
    @Deprecated()
    public static final String JANSI_EAGER = "jansi.eager";
    /**
     * If the <code>jansi.noreset</code> system property is set to true, the attributes won't be
     * reset when the streams are uninstalled.
     */
    public static final String JANSI_NORESET = "jansi.noreset";
    /**
     * If the <code>jansi.graceful</code> system property is set to false, any exception that occurs
     * during the initialization will cause the library to report this exception and fail. The default
     * behavior is to behave gracefully and fall back to pure emulation on posix systems.
     */
    public static final String JANSI_GRACEFUL = "jansi.graceful";

    /**
     * The {@code jansi.providers} system property can be set to control which internal provider
     * will be used.  If this property is not set, the {@code ffm} provider will be used if available,
     * else the {@code jni} one will be used.  If set, this property is interpreted as a comma
     * separated list of provider names to try in order.
     */
    public static final String JANSI_PROVIDERS = "jansi.providers";
    /**
     * The name of the {@code jni} provider.
     */
    public static final String JANSI_PROVIDER_JNI = "jni";
    /**
     * The name of the {@code ffm} provider.
     */
    public static final String JANSI_PROVIDER_FFM = "ffm";
    /**
     * The name of the {@code stty} provider.
     */
    public static final String JANSI_PROVIDER_STTY = "stty";

    /**
     * The name of the {@code native-image} provider.
     * <p>This provider uses the
     * <a href="https://www.graalvm.org/latest/reference-manual/native-image/native-code-interoperability/C-API/">Native Image C API</a>
     * to call native functions, so it is only available when building to native image.
     * Additionally, this provider currently does not support Windows.
     * <p>Note: This is not the only provider available on Native Image,
     * and it is usually recommended to use ffm or jni provider.
     * This provider is mainly used when building static native images linked to musl libc.
     */
    public static final String JANSI_PROVIDER_NATIVE_IMAGE = "native-image";

    /**
     * @deprecated this field will be made private in a future release, use {@link #sysOut()} instead
     */
    @Deprecated
    public static PrintStream system_out = System.out;
    /**
     * @deprecated this field will be made private in a future release, use {@link #out()} instead
     */
    @Deprecated
    public static PrintStream out;
    /**
     * @deprecated this field will be made private in a future release, use {@link #sysErr()} instead
     */
    @Deprecated
    public static PrintStream system_err = System.err;
    /**
     * @deprecated this field will be made private in a future release, use {@link #err()} instead
     */
    @Deprecated
    public static PrintStream err;

    /**
     * Try to find the width of the console for this process.
     * Both output and error streams will be checked to determine the width.
     * A value of 0 is returned if the width can not be determined.
     * @since 2.2
     */
    public static int getTerminalWidth() {
        int w = out().getTerminalWidth();
        if (w <= 0) {
            w = err().getTerminalWidth();
        }
        return w;
    }

    static final boolean IS_WINDOWS = OSInfo.isWindows();

    static final boolean IS_CYGWIN =
            IS_WINDOWS && System.getenv("PWD") != null && System.getenv("PWD").startsWith("/");

    static final boolean IS_MSYSTEM = IS_WINDOWS
            && System.getenv("MSYSTEM") != null
            && (System.getenv("MSYSTEM").startsWith("MINGW")
                    || System.getenv("MSYSTEM").equals("MSYS"));

    static final boolean IS_CONEMU = IS_WINDOWS && System.getenv("ConEmuPID") != null;

    static final int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 0x0004;

    static {
        if (getBoolean(JANSI_EAGER)) {
            initStreams();
        }
    }

    private static boolean initialized;
    private static int installed;
    private static int virtualProcessing;

    private AnsiConsole() {}

    private static AnsiPrintStream ansiStream(boolean stdout) {
        FileDescriptor descriptor = stdout ? FileDescriptor.out : FileDescriptor.err;
        final OutputStream out = new FastBufferedOutputStream(new FileOutputStream(descriptor));

        String enc = System.getProperty(stdout ? "stdout.encoding" : "stderr.encoding");
        if (enc == null) {
            enc = System.getProperty(stdout ? "sun.stdout.encoding" : "sun.stderr.encoding");
        }

        final boolean isatty;
        boolean isAtty;
        boolean withException;
        final int fd = stdout ? STDOUT_FILENO : STDERR_FILENO;
        try {
            // If we can detect that stdout is not a tty, then setup
            // to strip the ANSI sequences...
            isAtty = getCLibrary().isTty(fd) != 0;
            String term = System.getenv("TERM");
            String emacs = System.getenv("INSIDE_EMACS");
            if (isAtty && "dumb".equals(term) && emacs != null && !emacs.contains("comint")) {
                isAtty = false;
            }
            withException = false;
        } catch (Throwable e) {
            // These errors happen if there are no available providers.
            // If the jansi.graceful system property is set to true, fall back to pure emulation.
            if (Boolean.parseBoolean(System.getProperty(AnsiConsole.JANSI_GRACEFUL, "true"))) {
                isAtty = false;
                withException = true;
            } else {
                throw e;
            }
        }
        isatty = isAtty;

        final AnsiOutputStream.WidthSupplier width;
        final AnsiProcessor processor;
        final AnsiType type;
        final AnsiOutputStream.IoRunnable installer;
        final AnsiOutputStream.IoRunnable uninstaller;
        if (!isatty) {
            processor = null;
            type = withException ? AnsiType.Unsupported : AnsiType.Redirected;
            installer = uninstaller = null;
            width = new AnsiOutputStream.ZeroWidthSupplier();
        } else if (IS_WINDOWS) {
            final long console = getKernel32().getStdHandle(stdout);
            final int[] mode = new int[1];
            final boolean isConsole = getKernel32().getConsoleMode(console, mode) != 0;
            if (isConsole && getKernel32().setConsoleMode(console, mode[0] | ENABLE_VIRTUAL_TERMINAL_PROCESSING) != 0) {
                // set it back for now, but we know it works
                getKernel32().setConsoleMode(console, mode[0]);
                processor = null;
                type = AnsiType.VirtualTerminal;
                installer = () -> {
                    virtualProcessing++;
                    getKernel32().setConsoleMode(console, mode[0] | ENABLE_VIRTUAL_TERMINAL_PROCESSING);
                };
                uninstaller = () -> {
                    if (--virtualProcessing == 0) {
                        getKernel32().setConsoleMode(console, mode[0]);
                    }
                };
                width = () -> getKernel32().getTerminalWidth(console);
            } else if ((IS_CONEMU || IS_CYGWIN || IS_MSYSTEM) && !isConsole) {
                // ANSI-enabled ConEmu, Cygwin or MSYS(2) on Windows...
                processor = null;
                type = AnsiType.Native;
                installer = uninstaller = null;
                String name = Stty.getConsoleName(stdout);
                if (name != null && !name.isEmpty()) {
                    width = () -> Stty.getTerminalWidth(name);
                } else {
                    width = () -> -1;
                }
            } else {
                // On Windows, when no ANSI-capable terminal is used, we know the console does not natively interpret
                // ANSI
                // codes but we can use jansi Kernel32 API for console
                AnsiProcessor proc;
                AnsiType ttype;
                try {
                    proc = getKernel32().newProcessor(out, console);
                    ttype = AnsiType.Emulation;
                } catch (Throwable ignore) {
                    // this happens when the stdout is being redirected to a file.
                    // Use the AnsiProcessor to strip out the ANSI escape sequences.
                    proc = new AnsiProcessor(out);
                    ttype = AnsiType.Unsupported;
                }
                processor = proc;
                type = ttype;
                installer = uninstaller = null;
                width = () -> getKernel32().getTerminalWidth(console);
            }
        }

        // We must be on some Unix variant...
        else {
            // ANSI-enabled ConEmu, Cygwin or MSYS(2) on Windows...
            processor = null;
            type = AnsiType.Native;
            installer = uninstaller = null;
            width = () -> getCLibrary().getTerminalWidth(fd);
        }

        AnsiMode mode;

        // If the jansi.mode property is set, use it
        String jansiMode = System.getProperty(stdout ? JANSI_OUT_MODE : JANSI_ERR_MODE, System.getProperty(JANSI_MODE));
        if (JANSI_MODE_FORCE.equals(jansiMode)) {
            mode = AnsiMode.Force;
        } else if (JANSI_MODE_STRIP.equals(jansiMode)) {
            mode = AnsiMode.Strip;
        } else if (jansiMode != null) {
            mode = isatty ? AnsiMode.Default : AnsiMode.Strip;
        }

        // If the jansi.passthrough property is set, then don't interpret
        // any of the ansi sequences.
        else if (getBoolean(JANSI_PASSTHROUGH)) {
            mode = AnsiMode.Force;
        }

        // If the jansi.strip property is set, then we just strip the
        // the ansi escapes.
        else if (getBoolean(JANSI_STRIP)) {
            mode = AnsiMode.Strip;
        }

        // If the jansi.force property is set, then we force to output
        // the ansi escapes for piping it into ansi color aware commands (e.g. less -r)
        else if (getBoolean(JANSI_FORCE)) {
            mode = AnsiMode.Force;
        } else {
            mode = isatty ? AnsiMode.Default : AnsiMode.Strip;
        }

        AnsiColors colors;

        String colorterm, term;
        // If the jansi.colors property is set, use it
        String jansiColors =
                System.getProperty(stdout ? JANSI_OUT_COLORS : JANSI_ERR_COLORS, System.getProperty(JANSI_COLORS));
        if (JANSI_COLORS_TRUECOLOR.equals(jansiColors)) {
            colors = AnsiColors.TrueColor;
        } else if (JANSI_COLORS_256.equals(jansiColors)) {
            colors = AnsiColors.Colors256;
        } else if (jansiColors != null) {
            colors = AnsiColors.Colors16;
        }

        // If the COLORTERM env variable contains "truecolor" or "24bit", assume true color support
        // see https://gist.github.com/XVilka/8346728#true-color-detection
        else if ((colorterm = System.getenv("COLORTERM")) != null
                && (colorterm.contains("truecolor") || colorterm.contains("24bit"))) {
            colors = AnsiColors.TrueColor;
        }

        // check the if TERM contains -direct
        else if ((term = System.getenv("TERM")) != null && term.contains("-direct")) {
            colors = AnsiColors.TrueColor;
        }

        // check the if TERM contains -256color
        else if (term != null && term.contains("-256color")) {
            colors = AnsiColors.Colors256;
        }

        // else defaults to 16 colors
        else {
            colors = AnsiColors.Colors16;
        }

        // If the jansi.noreset property is not set, reset the attributes
        // when the stream is closed
        boolean resetAtUninstall = type != AnsiType.Unsupported && !getBoolean(JANSI_NORESET);

        Charset cs = Charset.defaultCharset();
        if (enc != null) {
            try {
                cs = Charset.forName(enc);
            } catch (UnsupportedCharsetException e) {
            }
        }
        return newPrintStream(
                new AnsiOutputStream(
                        out, width, mode, processor, type, colors, cs, installer, uninstaller, resetAtUninstall),
                cs.name());
    }

    private static AnsiPrintStream newPrintStream(AnsiOutputStream out, String enc) {
        if (enc != null) {
            try {
                return new AnsiPrintStream(out, true, enc);
            } catch (UnsupportedEncodingException e) {
            }
        }
        return new AnsiPrintStream(out, true);
    }

    static boolean getBoolean(String name) {
        boolean result = false;
        try {
            String val = System.getProperty(name);
            result = val.isEmpty() || Boolean.parseBoolean(val);
        } catch (IllegalArgumentException | NullPointerException ignored) {
        }
        return result;
    }

    /**
     * If the standard out natively supports ANSI escape codes, then this just
     * returns System.out, otherwise it will provide an ANSI aware PrintStream
     * which strips out the ANSI escape sequences or which implement the escape
     * sequences.
     *
     * @return a PrintStream which is ANSI aware.
     */
    public static AnsiPrintStream out() {
        initStreams();
        return (AnsiPrintStream) out;
    }

    /**
     * Access to the original System.out stream before ansi streams were installed.
     *
     * @return the originial System.out print stream
     */
    public static PrintStream sysOut() {
        return system_out;
    }

    /**
     * If the standard out natively supports ANSI escape codes, then this just
     * returns System.err, otherwise it will provide an ANSI aware PrintStream
     * which strips out the ANSI escape sequences or which implement the escape
     * sequences.
     *
     * @return a PrintStream which is ANSI aware.
     */
    public static AnsiPrintStream err() {
        initStreams();
        return (AnsiPrintStream) err;
    }

    /**
     * Access to the original System.err stream before ansi streams were installed.
     *
     * @return the originial System.err print stream
     */
    public static PrintStream sysErr() {
        return system_err;
    }

    /**
     * Install <code>AnsiConsole.out()</code> to <code>System.out</code> and
     * <code>AnsiConsole.err()</code> to <code>System.err</code>.
     * @see #systemUninstall()
     */
    public static synchronized void systemInstall() {
        if (installed == 0) {
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
        installed++;
    }

    /**
     * check if the streams have been installed or not
     */
    public static synchronized boolean isInstalled() {
        return installed > 0;
    }

    /**
     * undo a previous {@link #systemInstall()}.  If {@link #systemInstall()} was called
     * multiple times, {@link #systemUninstall()} must be called the same number of times before
     * it is actually uninstalled.
     */
    public static synchronized void systemUninstall() {
        installed--;
        if (installed == 0) {
            try {
                ((AnsiPrintStream) out).uninstall();
                ((AnsiPrintStream) err).uninstall();
            } catch (IOException e) {
                throw new IOError(e);
            }
            initialized = false;
            System.setOut(system_out);
            System.setErr(system_err);
        }
    }

    /**
     * Initialize the out/err ansi-enabled streams
     */
    static synchronized void initStreams() {
        if (!initialized) {
            out = ansiStream(true);
            err = ansiStream(false);
            initialized = true;
        }
    }
}
