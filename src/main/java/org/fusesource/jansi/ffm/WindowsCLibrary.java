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

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

import org.fusesource.jansi.AnsiConsoleSupport;

import static java.lang.foreign.ValueLayout.*;

final class WindowsCLibrary implements AnsiConsoleSupport.CLibrary {

    private static final int FILE_TYPE_CHAR = 0x0002;

    private static final int ObjectNameInformation = 1;

    private static final MethodHandle _get_osfhandle;
    private static final MethodHandle GetFileType;
    private static final MethodHandle NtQueryObject;
    private static final VarHandle UNICODE_STRING_LENGTH;
    private static final VarHandle UNICODE_STRING_BUFFER;

    static {
        _get_osfhandle = Kernel32.downcallHandle("_get_osfhandle", FunctionDescriptor.of(ADDRESS, JAVA_INT));
        GetFileType = Kernel32.downcallHandle("GetFileType", FunctionDescriptor.of(JAVA_INT, ADDRESS));

        MethodHandle ntQueryObjectHandle = null;
        try {
            SymbolLookup ntDll = SymbolLookup.libraryLookup("ntdll", Arena.ofAuto());

            ntQueryObjectHandle = ntDll.find("NtQueryObject")
                    .map(addr -> Linker.nativeLinker()
                            .downcallHandle(
                                    addr,
                                    FunctionDescriptor.of(JAVA_INT, ADDRESS, JAVA_INT, ADDRESS, JAVA_LONG, ADDRESS)))
                    .orElse(null);
        } catch (Throwable ignored) {
        }

        NtQueryObject = ntQueryObjectHandle;

        StructLayout unicodeStringLayout = MemoryLayout.structLayout(
                JAVA_SHORT.withName("Length"), JAVA_SHORT.withName("MaximumLength"), ADDRESS.withName("Buffer"));

        UNICODE_STRING_LENGTH = unicodeStringLayout.varHandle(PathElement.groupElement("Length"));
        UNICODE_STRING_BUFFER = unicodeStringLayout.varHandle(PathElement.groupElement("Buffer"));
    }

    @Override
    public short getTerminalWidth(int fd) {
        throw new UnsupportedOperationException("Windows does not support ioctl");
    }

    @Override
    public int isTty(int fd) {
        try (Arena session = Arena.ofConfined()) {
            // check if fd is a pipe
            MemorySegment h = (MemorySegment) _get_osfhandle.invokeExact(fd);
            int t = (int) GetFileType.invokeExact(h);
            if (t == FILE_TYPE_CHAR) {
                // check that this is a real tty because the /dev/null
                // and /dev/zero streams are also of type FILE_TYPE_CHAR
                return Kernel32.GetConsoleMode(h, MemorySegment.NULL);
            }

            if (NtQueryObject == null) {
                return 0;
            }

            final int BUFFER_SIZE = 1024;

            MemorySegment buffer = session.allocate(BUFFER_SIZE);
            MemorySegment result = session.allocate(JAVA_LONG);

            int res = (int) NtQueryObject.invokeExact(h, ObjectNameInformation, buffer, BUFFER_SIZE - 2, result);
            if (res != 0) {
                return 0;
            }

            int stringLength = Short.toUnsignedInt((Short) UNICODE_STRING_LENGTH.get(buffer));
            MemorySegment stringBuffer = ((MemorySegment) UNICODE_STRING_BUFFER.get(buffer)).reinterpret(stringLength);

            byte[] array = new byte[stringLength];
            MemorySegment.copy(stringBuffer, JAVA_BYTE, 0, array, 0, stringLength);

            String str = new String(
                    array,
                    ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN
                            ? StandardCharsets.UTF_16LE
                            : StandardCharsets.UTF_16BE);

            if (str.startsWith("msys-") || str.startsWith("cygwin-") || str.startsWith("-pty")) {
                return 1;
            }

            return 0;
        } catch (Throwable e) {
            throw new AssertionError("should not reach here", e);
        }
    }
}
