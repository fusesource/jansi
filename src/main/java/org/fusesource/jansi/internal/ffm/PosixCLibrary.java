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

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;

import org.fusesource.jansi.internal.AnsiConsoleSupport;

final class PosixCLibrary implements AnsiConsoleSupport.CLibrary {
    private static final int TIOCGWINSZ;
    private static final GroupLayout wsLayout;
    private static final MethodHandle ioctl;
    private static final VarHandle ws_col;
    private static final MethodHandle isatty;

    static {
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Linux")) {
            String arch = System.getProperty("os.arch");
            boolean isMipsPpcOrSparc = arch.startsWith("mips") || arch.startsWith("ppc") || arch.startsWith("sparc");
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
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment segment = arena.allocate(wsLayout);
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
}
