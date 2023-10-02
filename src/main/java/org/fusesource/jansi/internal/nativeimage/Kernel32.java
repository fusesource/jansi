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

import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.Platforms;
import org.graalvm.nativeimage.c.function.CFunction;
import org.graalvm.nativeimage.c.function.CLibrary;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CIntPointer;
import org.graalvm.nativeimage.c.type.VoidPointer;
import org.graalvm.nativeimage.c.type.WordPointer;

@Platforms(value = Platform.WINDOWS.class)
@CLibrary("Kernel32")
final class Kernel32 {
    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    static native VoidPointer GetStdHandle(int nStdHandle);

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    static native int FormatMessageW(
            int dwFlags,
            VoidPointer lpSource,
            int dwMessageId,
            int dwLanguageId,
            WordPointer lpBuffer,
            int nSize,
            VoidPointer Arguments);

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    static native int SetConsoleTextAttribute(VoidPointer hConsoleOutput, short wAttributes);

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    static native int SetConsoleMode(VoidPointer hConsoleHandle, int dwMode);

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    static native int GetConsoleMode(VoidPointer hConsoleHandle, CIntPointer lpMode);

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    static native int SetConsoleTitleW(CCharPointer lpConsoleTitle);

    static void f() {}
}
