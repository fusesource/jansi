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
package org.fusesource.jansi.internal.jni;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.fusesource.jansi.internal.AnsiConsoleSupport;
import org.fusesource.jansi.io.AnsiProcessor;
import org.fusesource.jansi.io.WindowsAnsiProcessor;

import static org.fusesource.jansi.internal.Kernel32.FORMAT_MESSAGE_FROM_SYSTEM;
import static org.fusesource.jansi.internal.Kernel32.FormatMessageW;
import static org.fusesource.jansi.internal.Kernel32.GetConsoleMode;
import static org.fusesource.jansi.internal.Kernel32.GetConsoleScreenBufferInfo;
import static org.fusesource.jansi.internal.Kernel32.GetLastError;
import static org.fusesource.jansi.internal.Kernel32.GetStdHandle;
import static org.fusesource.jansi.internal.Kernel32.STD_ERROR_HANDLE;
import static org.fusesource.jansi.internal.Kernel32.STD_OUTPUT_HANDLE;
import static org.fusesource.jansi.internal.Kernel32.SetConsoleMode;

public final class AnsiConsoleSupportImpl implements AnsiConsoleSupport {

    @Override
    public String getProviderName() {
        return "jni";
    }

    @Override
    public CLibrary getCLibrary() {
        return new CLibrary() {
            @Override
            public short getTerminalWidth(int fd) {
                return org.fusesource.jansi.internal.CLibrary.getTerminalWidth(fd);
            }

            @Override
            public int isTty(int fd) {
                return org.fusesource.jansi.internal.CLibrary.isatty(fd);
            }
        };
    }

    @Override
    public Kernel32 getKernel32() {
        return new Kernel32() {
            @Override
            public int isTty(long console) {
                int[] mode = new int[1];
                return GetConsoleMode(console, mode);
            }

            @Override
            public int getTerminalWidth(long console) {
                org.fusesource.jansi.internal.Kernel32.CONSOLE_SCREEN_BUFFER_INFO info =
                        new org.fusesource.jansi.internal.Kernel32.CONSOLE_SCREEN_BUFFER_INFO();
                GetConsoleScreenBufferInfo(console, info);
                return info.windowWidth();
            }

            public long getStdHandle(boolean stdout) {
                return GetStdHandle(stdout ? STD_OUTPUT_HANDLE : STD_ERROR_HANDLE);
            }

            @Override
            public int getConsoleMode(long console, int[] mode) {
                return GetConsoleMode(console, mode);
            }

            @Override
            public int setConsoleMode(long console, int mode) {
                return SetConsoleMode(console, mode);
            }

            @Override
            public int getLastError() {
                return GetLastError();
            }

            @Override
            public String getErrorMessage(int errorCode) {
                int bufferSize = 160;
                byte[] data = new byte[bufferSize];
                FormatMessageW(FORMAT_MESSAGE_FROM_SYSTEM, 0, errorCode, 0, data, bufferSize, null);
                return new String(data, StandardCharsets.UTF_16LE).trim();
            }

            @Override
            public AnsiProcessor newProcessor(OutputStream os, long console) throws IOException {
                return new WindowsAnsiProcessor(os, console);
            }
        };
    }
}
