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
package org.fusesource.jansi.internal.stty;

import org.fusesource.jansi.internal.AnsiConsoleSupport;

public class SttyCLibrary implements AnsiConsoleSupport.CLibrary {

    private final String stdoutTty;
    private final String stderrTty;

    public SttyCLibrary(String stdoutTty, String stderrTty) {
        this.stdoutTty = stdoutTty;
        this.stderrTty = stderrTty;
    }

    @Override
    public short getTerminalWidth(int fd) {
        String ttyName = null;
        if (fd == STDOUT_FILENO) {
            ttyName = stdoutTty;
        } else if (fd == STDERR_FILENO) {
            ttyName = stderrTty;
        }

        if (ttyName == null || ttyName.isEmpty()) {
            return 0;
        }

        int width = Stty.getTerminalWidth(ttyName);
        return width >= 0 && width <= Short.MAX_VALUE ? (short) width : 0;
    }

    @Override
    public int isTty(int fd) {
        return (fd == STDOUT_FILENO && stdoutTty != null) || (fd == STDERR_FILENO && stderrTty != null) ? 1 : 0;
    }
}
