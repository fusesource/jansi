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

import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeForeignAccess;

import static java.lang.foreign.ValueLayout.*;

public final class NativeImageFeatures implements Feature {

    private static void registerForDowncall(MemoryLayout resLayout, MemoryLayout... argLayouts) {
        RuntimeForeignAccess.registerForDowncall(FunctionDescriptor.of(resLayout, argLayouts));
    }

    @Override
    public void duringSetup(DuringSetupAccess access) {
        if (Platform.includedIn(Platform.WINDOWS.class)) {
            final OfShort C_SHORT$LAYOUT = JAVA_SHORT;
            final OfInt C_INT$LAYOUT = JAVA_INT;
            final AddressLayout C_POINTER$LAYOUT = ADDRESS;

            StructLayout COORD$LAYOUT =
                    MemoryLayout.structLayout(C_SHORT$LAYOUT.withName("x"), C_SHORT$LAYOUT.withName("y"));

            // WaitForSingleObject
            registerForDowncall(C_INT$LAYOUT, C_POINTER$LAYOUT, C_INT$LAYOUT);
            // GetStdHandle
            registerForDowncall(C_POINTER$LAYOUT, C_INT$LAYOUT);
            // FormatMessageW
            registerForDowncall(
                    C_INT$LAYOUT,
                    C_INT$LAYOUT,
                    C_POINTER$LAYOUT,
                    C_INT$LAYOUT,
                    C_INT$LAYOUT,
                    C_POINTER$LAYOUT,
                    C_INT$LAYOUT,
                    C_POINTER$LAYOUT);
            // SetConsoleTextAttribute
            registerForDowncall(C_INT$LAYOUT, C_POINTER$LAYOUT, C_SHORT$LAYOUT);
            // SetConsoleMode
            registerForDowncall(C_INT$LAYOUT, C_POINTER$LAYOUT, C_INT$LAYOUT);
            // GetConsoleMode
            registerForDowncall(C_INT$LAYOUT, C_POINTER$LAYOUT, C_POINTER$LAYOUT);
            // SetConsoleTitleW
            registerForDowncall(C_INT$LAYOUT, C_POINTER$LAYOUT);
            // SetConsoleCursorPosition
            registerForDowncall(C_INT$LAYOUT, C_POINTER$LAYOUT, COORD$LAYOUT);
            // FillConsoleOutputCharacterW
            registerForDowncall(
                    C_INT$LAYOUT, C_POINTER$LAYOUT, C_SHORT$LAYOUT, C_INT$LAYOUT, COORD$LAYOUT, C_POINTER$LAYOUT);
            // FillConsoleOutputAttribute
            registerForDowncall(
                    C_INT$LAYOUT, C_POINTER$LAYOUT, C_SHORT$LAYOUT, C_INT$LAYOUT, COORD$LAYOUT, C_POINTER$LAYOUT);
            // WriteConsoleW
            registerForDowncall(
                    C_INT$LAYOUT, C_POINTER$LAYOUT, C_POINTER$LAYOUT, C_INT$LAYOUT, C_POINTER$LAYOUT, C_POINTER$LAYOUT);
            // ReadConsoleInputW
            registerForDowncall(C_INT$LAYOUT, C_POINTER$LAYOUT, C_POINTER$LAYOUT, C_INT$LAYOUT, C_POINTER$LAYOUT);
            // PeekConsoleInputW
            registerForDowncall(C_INT$LAYOUT, C_POINTER$LAYOUT, C_POINTER$LAYOUT, C_INT$LAYOUT, C_POINTER$LAYOUT);
            // GetConsoleScreenBufferInfo
            registerForDowncall(C_INT$LAYOUT, C_POINTER$LAYOUT, C_POINTER$LAYOUT);
            // ScrollConsoleScreenBuffer
            registerForDowncall(
                    C_INT$LAYOUT, C_POINTER$LAYOUT, C_POINTER$LAYOUT, C_POINTER$LAYOUT, COORD$LAYOUT, C_POINTER$LAYOUT);
            // GetLastError
            registerForDowncall(C_INT$LAYOUT);
            // GetFileType
            registerForDowncall(C_INT$LAYOUT, C_POINTER$LAYOUT);
            // _get_osfhandle
            registerForDowncall(C_POINTER$LAYOUT, C_INT$LAYOUT);
            // NtQueryObject
            registerForDowncall(JAVA_INT, ADDRESS, JAVA_INT, ADDRESS, JAVA_LONG, ADDRESS);
        } else if (Platform.includedIn(Platform.LINUX.class) || Platform.includedIn(Platform.DARWIN.class)) {
            // ioctl
            registerForDowncall(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_LONG, ValueLayout.ADDRESS);
            // isatty
            registerForDowncall(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);
        } else {
            throw new UnsupportedOperationException("Unsupported platform");
        }
    }
}
