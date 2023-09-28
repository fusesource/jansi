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
package org.fusesource.jansi.ffm;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;

import org.fusesource.jansi.AnsiConsoleSupport;
import org.fusesource.jansi.io.AnsiProcessor;

import static org.fusesource.jansi.ffm.Kernel32.*;

public class AnsiConsoleSupportFfm implements AnsiConsoleSupport {
    @Override
    public String getProviderName() {
        return "ffm";
    }

    @Override
    public CLibrary getCLibrary() {
        return new CLibrary() {
            static final int TIOCGWINSZ;
            static final GroupLayout wsLayout;
            static final MethodHandle ioctl;
            static final VarHandle ws_col;
            static final MethodHandle isatty;

            static {
                String osName = System.getProperty("os.name");
                if (osName.startsWith("Linux")) {
                    String arch = System.getProperty("os.arch");
                    boolean isMipsPpcOrSparc =
                            arch.startsWith("mips") || arch.startsWith("ppc") || arch.startsWith("sparc");
                    TIOCGWINSZ = isMipsPpcOrSparc ? 0x40087468 : 0x00005413;
                } else if (osName.startsWith("Solaris") || osName.startsWith("SunOS")) {
                    int _TIOC = ('T' << 8);
                    TIOCGWINSZ = (_TIOC | 104);
                } else if (osName.startsWith("Mac") || osName.startsWith("Darwin")) {
                    TIOCGWINSZ = 0x40087468;
                } else if (osName.startsWith("FreeBSD")) {
                    TIOCGWINSZ = 0x40087468;
                } else {
                    throw new UnsupportedOperationException();
                }

                wsLayout = MemoryLayout.structLayout(
                        ValueLayout.JAVA_SHORT.withName("ws_row"),
                        ValueLayout.JAVA_SHORT.withName("ws_col"),
                        ValueLayout.JAVA_SHORT,
                        ValueLayout.JAVA_SHORT);
                ws_col = wsLayout.varHandle(MemoryLayout.PathElement.groupElement("ws_col"));
                Linker linker = Linker.nativeLinker();
                ioctl = linker.downcallHandle(
                        linker.defaultLookup().find("ioctl").get(),
                        FunctionDescriptor.of(
                                ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_LONG, ValueLayout.ADDRESS),
                        Linker.Option.firstVariadicArg(2));
                isatty = linker.downcallHandle(
                        linker.defaultLookup().find("isatty").get(),
                        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT));
            }

            @Override
            public short getTerminalWidth(int fd) {
                try (Arena session = Arena.ofConfined()) {
                    MemorySegment segment = session.allocate(wsLayout);
                    int res = (int) ioctl.invoke(fd, (long) TIOCGWINSZ, segment);
                    return (short) ws_col.get(segment);
                } catch (Throwable e) {
                    throw new RuntimeException("Unable to ioctl(TIOCGWINSZ)", e);
                }
            }

            @Override
            public int isTty(int fd) {
                try {
                    return (int) isatty.invoke(fd);
                } catch (Throwable e) {
                    throw new RuntimeException("Unable to call isatty", e);
                }
            }
        };
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
                CONSOLE_SCREEN_BUFFER_INFO info = new CONSOLE_SCREEN_BUFFER_INFO();
                GetConsoleScreenBufferInfo(MemorySegment.ofAddress(console), info);
                return info.windowWidth();
            }

            @Override
            public long getStdHandle(boolean stdout) {
                return GetStdHandle(stdout ? STD_OUTPUT_HANDLE : STD_ERROR_HANDLE)
                        .address();
            }

            @Override
            public int getConsoleMode(long console, int[] mode) {
                try (Arena session = Arena.ofConfined()) {
                    MemorySegment written = session.allocate(ValueLayout.JAVA_INT);
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
                int bufferSize = 160;
                MemorySegment data = Arena.ofAuto().allocate(bufferSize);
                FormatMessageW(FORMAT_MESSAGE_FROM_SYSTEM, null, errorCode, 0, data, bufferSize, null);
                return data.getUtf8String(0).trim();
            }

            @Override
            public AnsiProcessor newProcessor(OutputStream os, long console) throws IOException {
                return new WindowsAnsiProcessor(os, MemorySegment.ofAddress(console));
            }
        };
    }
}
