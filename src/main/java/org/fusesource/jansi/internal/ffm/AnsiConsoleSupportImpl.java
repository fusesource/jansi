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
package org.fusesource.jansi.internal.ffm;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

import org.fusesource.jansi.internal.AnsiConsoleSupport;
import org.fusesource.jansi.internal.OSInfo;
import org.fusesource.jansi.io.AnsiProcessor;

import static org.fusesource.jansi.internal.ffm.Kernel32.*;

public final class AnsiConsoleSupportImpl implements AnsiConsoleSupport {

    public AnsiConsoleSupportImpl() {}

    public AnsiConsoleSupportImpl(boolean checkNativeAccess) {
        if (checkNativeAccess && !AnsiConsoleSupportImpl.class.getModule().isNativeAccessEnabled()) {
            throw new UnsupportedOperationException(
                    "Native access is not enabled for the current module: " + AnsiConsoleSupportImpl.class.getModule());
        }
    }

    @Override
    public String getProviderName() {
        return "ffm";
    }

    @Override
    public CLibrary getCLibrary() {
        if (OSInfo.isWindows()) {
            return new WindowsCLibrary();
        } else {
            return new PosixCLibrary();
        }
    }

    @Override
    public Kernel32 getKernel32() {
        return new Kernel32() {
            @Override
            public int isTty(long console) {
                int[] mode = new int[1];
                return getConsoleMode(console, mode);
            }

            @Override
            public int getTerminalWidth(long console) {
                try (Arena arena = Arena.ofConfined()) {
                    CONSOLE_SCREEN_BUFFER_INFO info = new CONSOLE_SCREEN_BUFFER_INFO(arena);
                    GetConsoleScreenBufferInfo(MemorySegment.ofAddress(console), info);
                    return info.windowWidth();
                }
            }

            @Override
            public long getStdHandle(boolean stdout) {
                return GetStdHandle(stdout ? STD_OUTPUT_HANDLE : STD_ERROR_HANDLE)
                        .address();
            }

            @Override
            public int getConsoleMode(long console, int[] mode) {
                try (Arena arena = Arena.ofConfined()) {
                    MemorySegment written = arena.allocate(ValueLayout.JAVA_INT);
                    int res = GetConsoleMode(MemorySegment.ofAddress(console), written);
                    mode[0] = written.getAtIndex(ValueLayout.JAVA_INT, 0);
                    return res;
                }
            }

            @Override
            public int setConsoleMode(long console, int mode) {
                return SetConsoleMode(MemorySegment.ofAddress(console), mode);
            }

            @Override
            public int getLastError() {
                return GetLastError();
            }

            @Override
            public String getErrorMessage(int errorCode) {
                return org.fusesource.jansi.internal.ffm.Kernel32.getErrorMessage(errorCode);
            }

            @Override
            public AnsiProcessor newProcessor(OutputStream os, long console) throws IOException {
                return new WindowsAnsiProcessor(os, MemorySegment.ofAddress(console));
            }
        };
    }
}
