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
import java.io.IOError;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Locale;

import org.fusesource.jansi.internal.CLibrary;

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
     * @deprecated this field will be made private in a future release, use {@link #sysOut()} instead
     */
    @Deprecated
    public static PrintStream system_out = System.out;
    /**
     * @deprecated this field will be made private in a future release, use {@link #out()} instead
     */
    @Deprecated
    public static AnsiPrintStream out;
    /**
     * @deprecated this field will be made private in a future release, use {@link #sysErr()} instead
     */
    @Deprecated
    public static PrintStream system_err = System.err;
    /**
     * @deprecated this field will be made private in a future release, use {@link #err()} instead
     */
    @Deprecated
    public static AnsiPrintStream err;

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


    static {
        if (getBoolean(JANSI_EAGER)) {
            initStreams();
        }
    }

    private static boolean initialized;
    private static int installed;
    private static int virtualProcessing;

    private AnsiConsole() {
    }

    private static AnsiPrintStream ansiStream(boolean stdout) {
        final OutputStream out = new FastBufferedOutputStream(new FileOutputStream(stdout ? FileDescriptor.out : FileDescriptor.err));

        String enc = System.getProperty(stdout ? "sun.stdout.encoding" : "sun.stderr.encoding");

        final boolean isatty;
        boolean isAtty;
        boolean withException;
        try {
            // If we can detect that stdout is not a tty.. then setup
            // to strip the ANSI sequences..
            isAtty = isatty(stdout ? CLibrary.STDOUT_FILENO : CLibrary.STDERR_FILENO) != 0;
            withException = false;
        } catch (Throwable ignore) {
            // These errors happen if the JNI lib is not available for your platform.
            // But since we are on ANSI friendly platform, assume the user is on the console.
            isAtty = false;
            withException = true;
        }
        isatty = isAtty;

        final AnsiProcessor processor;
        final AnsiProcessorType processorType;
        final AnsiOutputStream.IoRunnable installer;
        final AnsiOutputStream.IoRunnable uninstaller;
        if (!isatty) {
            processor = null;
            processorType = withException ? AnsiProcessorType.Unsupported : AnsiProcessorType.Redirected;
            installer = uninstaller = null;
        }
        else if (IS_WINDOWS) {
            final long console = GetStdHandle(stdout ? STD_OUTPUT_HANDLE : STD_ERROR_HANDLE);
            final int[] mode = new int[1];
            if (GetConsoleMode(console, mode) != 0
                    && SetConsoleMode(console, mode[0] | ENABLE_VIRTUAL_TERMINAL_PROCESSING) != 0) {
                SetConsoleMode(console, mode[0]); // set it back for now, but we know it works
                processor = null;
                processorType = AnsiProcessorType.VirtualTerminal;
                installer = new AnsiOutputStream.IoRunnable() {
                    @Override
                    public void run() throws IOException {
                        virtualProcessing++;
                        SetConsoleMode(console, mode[0] | ENABLE_VIRTUAL_TERMINAL_PROCESSING);
                    }
                };
                uninstaller = new AnsiOutputStream.IoRunnable() {
                    @Override
                    public void run() throws IOException {
                        if (--virtualProcessing == 0) {
                            SetConsoleMode(console, mode[0]);
                        }
                    }
                };
            }
            else if (IS_CONEMU || IS_CYGWIN || IS_MSYSTEM) {
                // ANSI-enabled ConEmu, Cygwin or MSYS(2) on Windows...
                processor = null;
                processorType = AnsiProcessorType.Native;
                installer = uninstaller = null;
            }
            else {
                // On Windows, when no ANSI-capable terminal is used, we know the console does not natively interpret ANSI
                // codes but we can use jansi Kernel32 API for console
                AnsiProcessor proc;
                AnsiProcessorType type;
                try {
                    proc = new WindowsAnsiProcessor(out, stdout);
                    type = AnsiProcessorType.Emulation;
                } catch (Throwable ignore) {
                    // this happens when the stdout is being redirected to a file.
                    // Use the AnsiProcessor to strip out the ANSI escape sequences.
                    proc = new AnsiProcessor(out);
                    type = AnsiProcessorType.Unsupported;
                }
                processor = proc;
                processorType = type;
                installer = uninstaller = null;
            }
        }

        // We must be on some Unix variant...
        else {
            // ANSI-enabled ConEmu, Cygwin or MSYS(2) on Windows...
            processor = null;
            processorType = AnsiProcessorType.Native;
            installer = uninstaller = null;
        }

        AnsiMode ansiMode;

        // If the jansi.jansiMode property is set, use it
        String jansiMode = System.getProperty(stdout ? JANSI_OUT_MODE : JANSI_ERR_MODE, System.getProperty(JANSI_MODE));
        if (JANSI_MODE_FORCE.equals(jansiMode)) {
            ansiMode = AnsiMode.Force;
        } else if (JANSI_MODE_STRIP.equals(jansiMode)) {
            ansiMode = AnsiMode.Strip;
        } else if (jansiMode != null) {
            ansiMode = isatty ? AnsiMode.Default : AnsiMode.Strip;
        }

        // If the jansi.passthrough property is set, then don't interpret
        // any of the ansi sequences.
        else if (getBoolean(JANSI_PASSTHROUGH)) {
            ansiMode = AnsiMode.Force;
        }

        // If the jansi.strip property is set, then we just strip the
        // the ansi escapes.
        else if (getBoolean(JANSI_STRIP)) {
            ansiMode = AnsiMode.Strip;
        }

        // If the jansi.force property is set, then we force to output
        // the ansi escapes for piping it into ansi color aware commands (e.g. less -r)
        else if (getBoolean(JANSI_FORCE)) {
            ansiMode = AnsiMode.Force;
        }

        else {
            ansiMode = isatty ? AnsiMode.Default : AnsiMode.Strip;
        }

        // If the jansi.noreset property is not set, reset the attributes
        // when the stream is closed
        boolean resetAtUninstall = !getBoolean(JANSI_NORESET);

        Charset cs = Charset.defaultCharset();
        if (enc != null) {
            try {
                cs = Charset.forName(enc);
            } catch (UnsupportedCharsetException e) {
            }
        }
        return newPrintStream(new AnsiOutputStream(out, ansiMode, processor, processorType, cs,
                installer, uninstaller, resetAtUninstall), cs.name());
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
    public static AnsiPrintStream out() {
        initStreams();
        return out;
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
        return err;
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
    synchronized static public void systemInstall() {
        installed++;
        if (installed == 1) {
            initStreams();
            try {
                out.install();
                err.install();
            } catch (IOException e) {
                throw new IOError(e);
            }
            System.setOut(out);
            System.setErr(err);
        }
    }

    /**
     * check if the streams have been installed or not
     */
    synchronized public static boolean isInstalled() {
        return installed > 0;
    }

    /**
     * undo a previous {@link #systemInstall()}.  If {@link #systemInstall()} was called
     * multiple times, {@link #systemUninstall()} must be called the same number of times before
     * it is actually uninstalled.
     */
    synchronized public static void systemUninstall() {
        installed--;
        if (installed == 0) {
            try {
                out.uninstall();
                err.uninstall();
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
    synchronized static void initStreams()
    {
        if ( !initialized )
        {
            out = ansiStream(true);
            err = ansiStream(false);
            initialized = true;
        }
    }

    ;
}
