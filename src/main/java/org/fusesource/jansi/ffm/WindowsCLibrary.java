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

import org.fusesource.jansi.AnsiConsoleSupport;

final class WindowsCLibrary implements AnsiConsoleSupport.CLibrary {

    @Override
    public short getTerminalWidth(int fd) {
        throw new UnsupportedOperationException("Windows does not support ioctl");
    }

    @Override
    public int isTty(int fd) {
        return 0; // TODO
    }
}
