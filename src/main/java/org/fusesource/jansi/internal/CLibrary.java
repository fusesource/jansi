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
package org.fusesource.jansi.internal;

/**
 * JNI bridge to low level POSIX functions, loaded by
 * <a href="http://fusesource.github.io/hawtjni/">HawtJNI</a> Runtime
 * as the {@code jansi} native library.
 *
 * <p>Always check {@link #LOADED} before calling a native method directly.
 * For the common terminal-detection idioms, prefer {@link PosixTerminal}'s
 * typed helpers instead of calling {@link #isatty} / {@link #ioctl} directly.</p>
 *
 * <p><b>Note:</b> {@link WinSize} and {@link Termios} must remain nested
 * inside this class — the native library binds to them by their JNI nested
 * name ({@code CLibrary$WinSize}, {@code CLibrary$Termios}). Moving them to
 * top-level classes would break the native binding.</p>
 *
 * @see JansiLoader
 * @see PosixTerminal
 * @see Kernel32
 */
@SuppressWarnings("unused")
public class CLibrary {

    // ─────────────────────────────────────────────────────────
    // Initialization
    // ─────────────────────────────────────────────────────────

    /**
     * {@code true} if the native jansi library loaded successfully and the
     * native methods on this class are safe to call.
     */
    public static final boolean LOADED;

    static {
        LOADED = NativeLoader.loadAndInit(CLibrary::init, CLibrary.class.getName());
    }

    private static native void init();

    private CLibrary() {}

    // ─────────────────────────────────────────────────────────
    // Standard file descriptors
    // (fixed: hardcoded literals are now `final` — they are not
    // populated by native init(), so there's no reason they were mutable)
    // ─────────────────────────────────────────────────────────

    /** POSIX standard output file descriptor ({@code STDOUT_FILENO}). */
    public static final int STDOUT_FILENO = 1;

    /** POSIX standard error file descriptor ({@code STDERR_FILENO}). */
    public static final int STDERR_FILENO = 2;

    // ─────────────────────────────────────────────────────────
    // Feature flags — populated by native init() for the current platform
    // ─────────────────────────────────────────────────────────

    /** {@code true} if the platform provides {@code isatty()}. */
    public static boolean HAVE_ISATTY;

    /** {@code true} if the platform provides {@code ttyname()}. */
    public static boolean HAVE_TTYNAME;

    // ─────────────────────────────────────────────────────────
    // tcsetattr() "optional_actions" values
    // ─────────────────────────────────────────────────────────

    /** Apply terminal attribute changes immediately. */
    public static int TCSANOW;

    /** Apply changes only after all pending output has been transmitted. */
    public static int TCSADRAIN;

    /** Apply changes after pending output is transmitted, and discard pending input. */
    public static int TCSAFLUSH;

    // ─────────────────────────────────────────────────────────
    // ioctl / termios request codes — platform-specific, populated by native init()
    // ─────────────────────────────────────────────────────────

    /** Request code to get terminal attributes (alternate of {@code tcgetattr}). */
    public static long TIOCGETA;

    /** Request code to set terminal attributes (alternate of {@code tcsetattr}). */
    public static long TIOCSETA;

    /** Request code to get the current line discipline. */
    public static long TIOCGETD;

    /** Request code to set the current line discipline. */
    public static long TIOCSETD;

    /** Request code to get the terminal window size ({@code TIOCGWINSZ}). */
    public static long TIOCGWINSZ;

    /** Request code to set the terminal window size ({@code TIOCSWINSZ}). */
    public static long TIOCSWINSZ;

    // ─────────────────────────────────────────────────────────
    // Native methods
    // ─────────────────────────────────────────────────────────

    /**
     * Tests whether a file descriptor refers to a terminal.
     *
     * @param fd file descriptor
     * @return {@code 1} if {@code fd} is open and refers to a terminal; otherwise
     *         {@code 0}, with {@code errno} set to indicate the error
     * @see <a href="http://man7.org/linux/man-pages/man3/isatty.3.html">isatty(3)</a>
     */
    public static native int isatty(int fd);

    /**
     * Returns the pathname of the terminal associated with a file descriptor,
     * or {@code null} if {@code filedes} is not connected to one.
     *
     * @see <a href="http://man7.org/linux/man-pages/man3/ttyname.3.html">ttyname(3)</a>
     */
    public static native String ttyname(int filedes);

    /**
     * Finds an available pseudoterminal and returns file descriptors for the
     * master and slave sides.
     *
     * @param amaster master return value
     * @param aslave  slave return value
     * @param name    filename return value
     * @param termios optional pty attributes
     * @param winsize optional size
     * @return {@code 0} on success
     * @see <a href="http://man7.org/linux/man-pages/man3/openpty.3.html">openpty(3)</a>
     */
    public static native int openpty(int[] amaster, int[] aslave, byte[] name, Termios termios, WinSize winsize);

    /**
     * Gets the terminal attributes for {@code filedes} into {@code termios}.
     *
     * @return {@code 0} on success; {@code -1} on error
     */
    public static native int tcgetattr(int filedes, Termios termios);

    /**
     * Sets the terminal attributes for {@code filedes}.
     *
     * @param optional_actions one of {@link #TCSANOW}, {@link #TCSADRAIN}, {@link #TCSAFLUSH}
     * @return {@code 0} on success; {@code -1} on error
     */
    public static native int tcsetattr(int filedes, int optional_actions, Termios termios);

    /**
     * Generic device control call.
     *
     * @see <a href="http://man7.org/linux/man-pages/man3/ioctl.3p.html">ioctl(3p)</a>
     */
    public static native int ioctl(int filedes, long request, int[] params);

    /**
     * Device control call specialized for {@link WinSize}, typically used with
     * {@link #TIOCGWINSZ} / {@link #TIOCSWINSZ}.
     */
    public static native int ioctl(int filedes, long request, WinSize params);

    // ─────────────────────────────────────────────────────────
    // Nested structs — must stay nested; see class-level note above
    // ─────────────────────────────────────────────────────────

    /**
     * Maps to the POSIX {@code struct winsize}.
     *
     * @see <a href="http://man7.org/linux/man-pages/man4/tty_ioctl.4.html">ioctl_tty(4)</a>
     */
    public static class WinSize {

        static {
            NativeLoader.loadAndInit(WinSize::init, WinSize.class.getName());
        }

        private static native void init();

        public static int SIZEOF;

        public short ws_row;
        public short ws_col;
        public short ws_xpixel;
        public short ws_ypixel;

        public WinSize() {}

        public WinSize(short ws_row, short ws_col) {
            this.ws_row = ws_row;
            this.ws_col = ws_col;
        }

        @Override
        public String toString() {
            return "WinSize{rows=" + ws_row + ", cols=" + ws_col + "}";
        }
    }

    /**
     * Maps to the POSIX {@code struct termios}, describing the general
     * terminal interface used to control asynchronous communications ports.
     *
     * @see <a href="http://man7.org/linux/man-pages/man3/termios.3.html">termios(3)</a>
     */
    public static class Termios {

        static {
            NativeLoader.loadAndInit(Termios::init, Termios.class.getName());
        }

        private static native void init();

        public static int SIZEOF;

        public long c_iflag;
        public long c_oflag;
        public long c_cflag;
        public long c_lflag;
        public byte[] c_cc = new byte[32];
        public long c_ispeed;
        public long c_ospeed;

        @Override
        public String toString() {
            return "Termios{iflag=" + c_iflag + ", oflag=" + c_oflag + ", cflag=" + c_cflag + ", lflag=" + c_lflag
                    + "}";
        }
    }
}