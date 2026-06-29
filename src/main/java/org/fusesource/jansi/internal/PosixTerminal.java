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
 * Typed, null-safe convenience wrappers around {@link CLibrary}'s raw
 * native bindings.
 *
 * <p><b>Code smell fixed — Primitive Obsession:</b> previously, every
 * caller needing to know "is this a terminal?" had to write
 * {@code CLibrary.isatty(fd) == 1}, and every caller needing the terminal
 * width had to manually allocate a {@code WinSize}, call {@code ioctl},
 * and read {@code ws_col} — repeating the same low-level dance at each
 * call site.</p>
 *
 * <p>This lives in its own file rather than inside {@link CLibrary}
 * because it is plain Java logic with no native binding of its own; it
 * only calls methods {@link CLibrary} already exposes. Separating it
 * keeps "raw JNI bridge" and "convenient Java API" as two distinct
 * responsibilities (Single Responsibility) instead of mixed into one class.</p>
 */
public final class PosixTerminal {

    private PosixTerminal() {}

    /**
     * Returns {@code true} if {@code fd} is connected to a terminal.
     * Returns {@code false} (never throws) if the native library is
     * unavailable or the check fails for any reason.
     *
     * @param fd typically {@link CLibrary#STDOUT_FILENO} or {@link CLibrary#STDERR_FILENO}
     */
    public static boolean isTerminal(int fd) {
        if (!CLibrary.LOADED) {
            return false;
        }
        try {
            return CLibrary.isatty(fd) == 1;
        } catch (Throwable e) {
            return false;
        }
    }

    /**
     * Returns the terminal width in columns for {@code fd}, or {@code -1}
     * if it cannot be determined (native library unavailable, {@code fd}
     * is not a terminal, or the underlying {@code ioctl} call failed).
     *
     * @param fd typically {@link CLibrary#STDOUT_FILENO} or {@link CLibrary#STDERR_FILENO}
     */
    public static int getWidth(int fd) {
        if (!CLibrary.LOADED) {
            return -1;
        }
        try {
            CLibrary.WinSize size = new CLibrary.WinSize();
            int result = CLibrary.ioctl(fd, CLibrary.TIOCGWINSZ, size);
            return result == 0 ? size.ws_col : -1;
        } catch (Throwable e) {
            return -1;
        }
    }
}