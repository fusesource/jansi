/*
 * Copyright (C) 2009-2017 the original author(s).
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
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import static org.fusesource.jansi.internal.CLibrary.isatty;
import static org.fusesource.jansi.internal.Kernel32.GetConsoleMode;
import static org.fusesource.jansi.internal.Kernel32.GetStdHandle;
import static org.fusesource.jansi.internal.Kernel32.STD_ERROR_HANDLE;
import static org.fusesource.jansi.internal.Kernel32.STD_OUTPUT_HANDLE;
import static org.fusesource.jansi.internal.Kernel32.SetConsoleMode;

/**
 * Provides consistent access to an ANSI aware console PrintStream or an ANSI codes stripping PrintStream
 * if not on a terminal (see 
 * <a href="http://fusesource.github.io/jansi/documentation/native-api/index.html?org/fusesource/jansi/internal/CLibrary.html">Jansi native
 * CLibrary isatty(int)</a>).
 * <p>The native library used is named <code>jansi</code> and is loaded using <a href="http://fusesource.github.io/hawtjni/">HawtJNI</a> Runtime
 * <a href="http://fusesource.github.io/hawtjni/documentation/api/index.html?org/fusesource/hawtjni/runtime/Library.html"><code>Library</code></a>
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 * @since 1.0
 * @see #systemInstall()
 * @see #ansiStream(boolean) for more details on ANSI mode selection
 */
public class AnsiConsole {

    public static final PrintStream system_out = System.out;
    public static final PrintStream out;

    public static final PrintStream system_err = System.err;
    public static final PrintStream err;

    static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("win");

    static final boolean IS_CYGWIN = IS_WINDOWS
            && System.getenv("PWD") != null
            && System.getenv("PWD").startsWith("/");

    static final boolean IS_MSYSTEM = IS_WINDOWS
            && System.getenv("MSYSTEM") != null
            && (System.getenv("MSYSTEM").startsWith("MINGW")
                || System.getenv("MSYSTEM").equals("MSYS"));

    static final boolean IS_CONEMU = IS_WINDOWS
            && System.getenv("ConEmuPID") != null;

    static final int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 0x0004;

    public static final String JANSI_PASSTHROUGH = "jansi.passthrough";
    public static final String JANSI_STRIP = "jansi.strip";
    public static final String JANSI_FORCE = "jansi.force";


    private static JansiOutputType jansiOutputType;
    static final JansiOutputType JANSI_STDOUT_TYPE;
    static final JansiOutputType JANSI_STDERR_TYPE;

    static {
        out = ansiStream(true);
        JANSI_STDOUT_TYPE = jansiOutputType;
        err = ansiStream(false);
        JANSI_STDERR_TYPE = jansiOutputType;
    }

    private static int installed;

    private AnsiConsole() {
    }

    private static PrintStream ansiStream(boolean stdout) {
        OutputStream out = new FastBufferedOutputStream(new FileOutputStream(stdout ? FileDescriptor.out : FileDescriptor.err));

        String enc = System.getProperty(stdout ? "sun.stdout.encoding" : "sun.stderr.encoding");

        // If the jansi.passthrough property is set, then don't interpret
        // any of the ansi sequences.
        if (getBoolean(JANSI_PASSTHROUGH)) {
            jansiOutputType = JansiOutputType.PASSTHROUGH;
            return newPrintStream(out, enc);
        }

        // If the jansi.strip property is set, then we just strip the
        // the ansi escapes.
        if (getBoolean(JANSI_STRIP)) {
            jansiOutputType = JansiOutputType.STRIP_ANSI;
            return newPrintStream(new AnsiOutputStream(out, new AnsiProcessor(out), enc), enc);
        }

        if (IS_WINDOWS) {
            long console = GetStdHandle(stdout ? STD_OUTPUT_HANDLE : STD_ERROR_HANDLE);
            int[] mode = new int[1];
            if (GetConsoleMode(console, mode) != 0
                    && SetConsoleMode(console, mode[0] | ENABLE_VIRTUAL_TERMINAL_PROCESSING) != 0) {
                jansiOutputType = JansiOutputType.VIRTUAL_TERMINAL;
                return newPrintStream(out, enc);
            }
        }

        if (IS_WINDOWS && !(IS_CONEMU || IS_CYGWIN || IS_MSYSTEM)) {

            // On Windows, when no ANSI-capable terminal is used, we know the console does not natively interpret ANSI
            // codes but we can use jansi-native Kernel32 API for console
            try {
                jansiOutputType = JansiOutputType.WINDOWS;
                return newPrintStream(new AnsiOutputStream(out, new WindowsAnsiProcessor(out, stdout), enc), enc);
            } catch (Throwable ignore) {
                // this happens when JNA is not in the path.. or
                // this happens when the stdout is being redirected to a file.
            }

            // Use the ANSIOutputStream to strip out the ANSI escape sequences.
            jansiOutputType = JansiOutputType.STRIP_ANSI;
            return newPrintStream(new AnsiOutputStream(out, new AnsiProcessor(out), enc), enc);
        }

        // We must be on some Unix variant or ANSI-enabled ConEmu, Cygwin or MSYS(2) on Windows...
        try {
            // If the jansi.force property is set, then we force to output
            // the ansi escapes for piping it into ansi color aware commands (e.g. less -r)
            boolean forceColored = getBoolean(JANSI_FORCE);
            // If we can detect that stdout is not a tty.. then setup
            // to strip the ANSI sequences..
            if (!forceColored && isatty(stdout ? 1 : 2) == 0) {
                jansiOutputType = JansiOutputType.STRIP_ANSI;
                return newPrintStream(new AnsiOutputStream(out, new AnsiProcessor(out), enc), enc);
            }
        } catch (Throwable ignore) {
            // These errors happen if the JNI lib is not available for your platform.
            // But since we are on ANSI friendly platform, assume the user is on the console.
        }

        // By default we assume your Unix tty can handle ANSI codes.
        // Just wrap it up so that when we get closed, we reset the
        // attributes.
        jansiOutputType = JansiOutputType.RESET_ANSI_AT_CLOSE;
        return newPrintStream(out, enc, AnsiOutputStream.RESET_CODE);
    }

    private static PrintStream newPrintStream(OutputStream out, String enc) {
        return newPrintStream(out, enc, null);
    }

    private static PrintStream newPrintStream(OutputStream out, String enc, byte[] reset) {
        if (enc != null) {
            try {
                return new ResetAtClosePrintStream(out, enc, reset);
            } catch (UnsupportedEncodingException e) {
            }
        }
        return new ResetAtClosePrintStream(out, reset);
    }

    static class ResetAtClosePrintStream extends PrintStream {

        private final byte[] reset;

        public ResetAtClosePrintStream(OutputStream out, byte[] reset) {
            super(out, true);
            this.reset = reset;
        }

        public ResetAtClosePrintStream(OutputStream out, String encoding, byte[] reset) throws UnsupportedEncodingException {
            super(out, true, encoding);
            this.reset = reset;
        }

        @Override
        public void close() {
            if (reset != null) {
                write(reset, 0, reset.length);
            }
            super.close();
        }
    }

    static boolean getBoolean(String name) {
        boolean result = false;
        try {
            String val = System.getProperty(name);
            result = val.isEmpty() || Boolean.parseBoolean(val);
        } catch (IllegalArgumentException e) {
        } catch (NullPointerException e) {
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
    public static PrintStream out() {
        return out;
    }

    /**
     * If the standard out natively supports ANSI escape codes, then this just
     * returns System.err, otherwise it will provide an ANSI aware PrintStream
     * which strips out the ANSI escape sequences or which implement the escape
     * sequences.
     *
     * @return a PrintStream which is ANSI aware.
     */
    public static PrintStream err() {
        return err;
    }

    /**
     * Install <code>AnsiConsole.out</code> to <code>System.out</code> and
     * <code>AnsiConsole.err</code> to <code>System.err</code>.
     * @see #systemUninstall()
     */
    synchronized static public void systemInstall() {
        installed++;
        if (installed == 1) {
            System.setOut(out);
            System.setErr(err);
        }
    }

    /**
     * undo a previous {@link #systemInstall()}.  If {@link #systemInstall()} was called
     * multiple times, {@link #systemUninstall()} must be called the same number of times before
     * it is actually uninstalled.
     */
    synchronized public static void systemUninstall() {
        installed--;
        if (installed == 0) {
            System.setOut(system_out);
            System.setErr(system_err);
        }
    }

    /**
     * Type of output installed by AnsiConsole.
     */
    enum JansiOutputType {
        PASSTHROUGH("just pass through, ANSI escape codes are supposed to be supported by terminal"),
        RESET_ANSI_AT_CLOSE("like pass through but reset ANSI attributes when closing the stream"),
        STRIP_ANSI("strip ANSI escape codes, for example when output is not a terminal"),
        WINDOWS("detect ANSI escape codes and transform Jansi-supported ones into a Windows API to get desired effect" +
                " (since ANSI escape codes are not natively supported by Windows terminals like cmd.exe or PowerShell)"),
        VIRTUAL_TERMINAL("recent Windows support ANSI processing using a windows API call to configure the console in the correct mode");

        private final String description;

        private JansiOutputType(String description) {
            this.description = description;
        }

        String getDescription() {
            return description;
        }
    };
}
