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

import static org.fusesource.jansi.internal.CLibrary.STDERR_FILENO;
import static org.fusesource.jansi.internal.CLibrary.STDOUT_FILENO;
import static org.fusesource.jansi.internal.CLibrary.isatty;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;

/**
 * Provides consistent access to an ANSI aware console PrintStream.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 * @since 1.0
 * @see #wrapPrintStream(PrintStream, int)
 */
public class AnsiConsole {

    public static final PrintStream system_out = System.out;
    public static final PrintStream out;

    public static final PrintStream system_err = System.err;
    public static final PrintStream err;

    static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("win");

    static final boolean IS_CYGWIN = IS_WINDOWS
            && System.getenv("PWD") != null
            && System.getenv("PWD").startsWith("/")
            && !"cygwin".equals(System.getenv("TERM"));

    static final boolean IS_MINGW = IS_WINDOWS
            && System.getenv("MSYSTEM") != null
            && System.getenv("MSYSTEM").startsWith("MINGW");

    private static JansiOutputType jansiOutputType;
    static final JansiOutputType JANSI_STDOUT_TYPE;
    static final JansiOutputType JANSI_STDERR_TYPE;
    static {
        out = wrapSystemOut(system_out);
        JANSI_STDOUT_TYPE = jansiOutputType;
        err = wrapSystemErr(system_err);
        JANSI_STDERR_TYPE = jansiOutputType;
    }

    private static int installed;

    private AnsiConsole() {
    }

    @Deprecated
    public static OutputStream wrapOutputStream(final OutputStream stream) {
        try {
            return wrapOutputStream(stream, STDOUT_FILENO);
        } catch (Throwable ignore) {
            return wrapOutputStream(stream, 1);
        }
    }

    public static PrintStream wrapSystemOut(final PrintStream ps) {
        try {
            return wrapPrintStream(ps, STDOUT_FILENO);
        } catch (Throwable ignore) {
            return wrapPrintStream(ps, 1);
        }
    }

    @Deprecated
    public static OutputStream wrapErrorOutputStream(final OutputStream stream) {
        try {
            return wrapOutputStream(stream, STDERR_FILENO);
        } catch (Throwable ignore) {
            return wrapOutputStream(stream, 2);
        }
    }

    public static PrintStream wrapSystemErr(final PrintStream ps) {
        try {
            return wrapPrintStream(ps, STDERR_FILENO);
        } catch (Throwable ignore) {
            return wrapPrintStream(ps, 2);
        }
    }

    @Deprecated
    public static OutputStream wrapOutputStream(final OutputStream stream, int fileno) {

        // If the jansi.passthrough property is set, then don't interpret
        // any of the ansi sequences.
        if (Boolean.getBoolean("jansi.passthrough")) {
            jansiOutputType = JansiOutputType.PASSTHROUGH;
            return stream;
        }

        // If the jansi.strip property is set, then we just strip the
        // the ansi escapes.
        if (Boolean.getBoolean("jansi.strip")) {
            jansiOutputType = JansiOutputType.STRIP_ANSI;
            return new AnsiOutputStream(stream);
        }

        if (IS_WINDOWS && !IS_CYGWIN && !IS_MINGW) {

            // On windows we know the console does not interpret ANSI codes..
            try {
                jansiOutputType = JansiOutputType.WINDOWS;
                return new WindowsAnsiOutputStream(stream);
            } catch (Throwable ignore) {
                // this happens when JNA is not in the path.. or
                // this happens when the stdout is being redirected to a file.
            }

            // Use the ANSIOutputStream to strip out the ANSI escape sequences.
            jansiOutputType = JansiOutputType.STRIP_ANSI;
            return new AnsiOutputStream(stream);
        }

        // We must be on some Unix variant, including Cygwin or MSYS(2) on Windows...
        try {
            // If the jansi.force property is set, then we force to output
            // the ansi escapes for piping it into ansi color aware commands (e.g. less -r)
            boolean forceColored = Boolean.getBoolean("jansi.force");
            // If we can detect that stdout is not a tty.. then setup
            // to strip the ANSI sequences..
            if (!forceColored && isatty(fileno) == 0) {
                jansiOutputType = JansiOutputType.STRIP_ANSI;
                return new AnsiOutputStream(stream);
            }
        } catch (Throwable ignore) {
            // These errors happen if the JNI lib is not available for your platform.
            // But since we are on ANSI friendly platform, assume the user is on the console.
        }

        // By default we assume your Unix tty can handle ANSI codes.
        // Just wrap it up so that when we get closed, we reset the
        // attributes.
        jansiOutputType = JansiOutputType.RESET_ANSI_AT_CLOSE;
        return new FilterOutputStream(stream) {
            @Override
            public void close() throws IOException {
                write(AnsiOutputStream.RESET_CODE);
                flush();
                super.close();
            }
        };
    }

    /**
     * Wrap PrintStream applying rules in following order:<ul>
     * <li>if <code>jansi.passthrough</code> is <code>true</code>, don't wrap but just passthrough (console is
     * expected to natively support ANSI escape codes),</li>
     * <li>if <code>jansi.strip</code> is <code>true</code>, just strip ANSI escape codes inconditionally,</li>
     * <li>if OS is Windows and terminal is not Cygwin or Mingw, wrap as WindowsAnsiPrintStream to process ANSI escape codes,</li>
     * <li>if file descriptor is a terminal (see <code>isatty(int)</code>) or <code>jansi.force</code> is <code>true</code>,
     * just passthrough,</li>
     * <li>else strip ANSI escape codes (not a terminal).</li>
     * </ul>
     * 
     * @param ps original PrintStream to wrap
     * @param fileno file descriptor
     * @return wrapped PrintStream depending on OS and system properties
     * @since 1.17
     */
    public static PrintStream wrapPrintStream(final PrintStream ps, int fileno) {

        // If the jansi.passthrough property is set, then don't interpret
        // any of the ansi sequences.
        if (Boolean.getBoolean("jansi.passthrough")) {
            jansiOutputType = JansiOutputType.PASSTHROUGH;
            return ps;
        }

        // If the jansi.strip property is set, then we just strip the
        // the ansi escapes.
        if (Boolean.getBoolean("jansi.strip")) {
            jansiOutputType = JansiOutputType.STRIP_ANSI;
            return new AnsiPrintStream(ps);
        }

        if (IS_WINDOWS && !IS_CYGWIN && !IS_MINGW) {

            // On windows we know the console does not interpret ANSI codes..
            try {
                jansiOutputType = JansiOutputType.WINDOWS;
                return new WindowsAnsiPrintStream(ps);
            } catch (Throwable ignore) {
                // this happens when JNA is not in the path.. or
                // this happens when the stdout is being redirected to a file.
            }

            // Use the AnsiPrintStream to strip out the ANSI escape sequences.
            jansiOutputType = JansiOutputType.STRIP_ANSI;
            return new AnsiPrintStream(ps);
        }

        // We must be on some Unix variant, including Cygwin or MSYS(2) on Windows...
        try {
            // If the jansi.force property is set, then we force to output
            // the ansi escapes for piping it into ansi color aware commands (e.g. less -r)
            boolean forceColored = Boolean.getBoolean("jansi.force");
            // If we can detect that stdout is not a tty.. then setup
            // to strip the ANSI sequences..
            if (!forceColored && isatty(fileno) == 0) {
                jansiOutputType = JansiOutputType.STRIP_ANSI;
                return new AnsiPrintStream(ps);
            }
        } catch (Throwable ignore) {
            // These errors happen if the JNI lib is not available for your platform.
            // But since we are on ANSI friendly platform, assume the user is on the console.
        }

        // By default we assume your Unix tty can handle ANSI codes.
        // Just wrap it up so that when we get closed, we reset the
        // attributes.
        jansiOutputType = JansiOutputType.RESET_ANSI_AT_CLOSE;
        return new FilterPrintStream(ps) {
            @Override
            public void close() {
                ps.print(AnsiPrintStream.RESET_CODE);
                ps.flush();
                super.close();
            }
        };
    }

    /**
     * If the standard out natively supports ANSI escape codes, then this just
     * returns System.out, otherwise it will provide an ANSI aware PrintStream
     * which strips out the ANSI escape sequences or which implement the escape
     * sequences.
     *
     * @return a PrintStream which is ANSI aware.
     * @see #wrapPrintStream(PrintStream, int)
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
     * @see #wrapPrintStream(PrintStream, int)
     */
    public static PrintStream err() {
        return err;
    }

    /**
     * Install Console.out to System.out.
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
     * multiple times, it {@link #systemUninstall()} must call the same number of times before
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
        PASSTHROUGH, // just pass through, ANSI escape codes are supposed to be supported by terminal
        STRIP_ANSI, // strip ANSI escape codes (since not a terminal)
        WINDOWS, // detect ANSI escape codes and transform Jansi-supported ones into a Windows API to get desired effect
                 // (since ANSI escape codes are not natively supported by Windows terminals like cmd.exe or PowerShell)
        RESET_ANSI_AT_CLOSE // like pass through but reset ANSI attributes when closing
    };
}
