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
package org.fusesource.jansi.internal.nativeimage;

import java.io.IOException;
import java.io.OutputStream;

import org.fusesource.jansi.internal.AnsiConsoleSupport;
import org.fusesource.jansi.internal.OSInfo;
import org.fusesource.jansi.io.AnsiProcessor;
import org.graalvm.nativeimage.StackValue;
import org.graalvm.nativeimage.c.type.CIntPointer;
import org.graalvm.word.WordFactory;

import static org.fusesource.jansi.internal.nativeimage.Kernel32.*;

public class AnsiConsoleSupportImpl implements AnsiConsoleSupport {
    @Override
    public String getProviderName() {
        return "nativeimage";
    }

    @Override
    public CLibrary getCLibrary() {
        if (OSInfo.isWindows()) {
            return new WindowsCLibrary();
        } else {
            throw new UnsupportedOperationException(); // TODO
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
                ConsoleScreenBufferInfo info = StackValue.get(ConsoleScreenBufferInfo.class);
                GetConsoleScreenBufferInfo(WordFactory.pointer(console), (ConsoleScreenBufferInfoPointer) info);

                SmallRect window = info.getSrWindow();
                return Short.toUnsignedInt(window.getRight()) - Short.toUnsignedInt(window.getLeft()) + 1;
            }

            @Override
            public long getStdHandle(boolean stdout) {
                return GetStdHandle(stdout ? STD_OUTPUT_HANDLE : STD_ERROR_HANDLE)
                        .rawValue();
            }

            @Override
            public int getConsoleMode(long console, int[] mode) {
                CIntPointer written = StackValue.get(Integer.BYTES);
                int res = GetConsoleMode(WordFactory.pointer(console), written);
                mode[0] = written.read();
                return res;
            }

            @Override
            public int setConsoleMode(long console, int mode) {
                return SetConsoleMode(WordFactory.pointer(console), mode);
            }

            @Override
            public int getLastError() {
                return GetLastError();
            }

            @Override
            public String getErrorMessage(int errorCode) {
                return org.fusesource.jansi.internal.nativeimage.Kernel32.getErrorMessage(errorCode);
            }

            @Override
            public AnsiProcessor newProcessor(OutputStream os, long console) throws IOException {
                throw new UnsupportedOperationException(); // TODO
            }
        };
    }
}
