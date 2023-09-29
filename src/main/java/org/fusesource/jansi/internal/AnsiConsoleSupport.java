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

import java.io.IOException;
import java.io.OutputStream;

import org.fusesource.jansi.io.AnsiProcessor;

public interface AnsiConsoleSupport {

    interface CLibrary {

        int STDOUT_FILENO = 1;
        int STDERR_FILENO = 2;

        short getTerminalWidth(int fd);

        int isTty(int fd);
    }

    interface Kernel32 {

        int isTty(long console);

        int getTerminalWidth(long console);

        long getStdHandle(boolean stdout);

        int getConsoleMode(long console, int[] mode);

        int setConsoleMode(long console, int mode);

        int getLastError();

        String getErrorMessage(int errorCode);

        AnsiProcessor newProcessor(OutputStream os, long console) throws IOException;
    }

    String getProviderName();

    CLibrary getCLibrary();

    Kernel32 getKernel32();
}
