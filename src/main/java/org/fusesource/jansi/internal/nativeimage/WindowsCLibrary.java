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

import org.fusesource.jansi.internal.AnsiConsoleSupport;
import org.graalvm.nativeimage.StackValue;
import org.graalvm.nativeimage.c.type.VoidPointer;

final class WindowsCLibrary implements AnsiConsoleSupport.CLibrary {

    private static final int FILE_TYPE_CHAR = 0x0002;

    @Override
    public short getTerminalWidth(int fd) {
        throw new UnsupportedOperationException("Windows does not support ioctl");
    }

    @Override
    public int isTty(int fd) {
        // check if fd is a pipe
        VoidPointer h = Kernel32._get_osfhandle(fd);
        int t = Kernel32.GetFileType(h);
        if (t == FILE_TYPE_CHAR) {
            // check that this is a real tty because the /dev/null
            // and /dev/zero streams are also of type FILE_TYPE_CHAR
            return Kernel32.GetConsoleMode(h, StackValue.get(Integer.BYTES));
        }

        // TODO
        return 0;
    }
}
