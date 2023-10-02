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

import java.nio.charset.StandardCharsets;

import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.Platforms;
import org.graalvm.nativeimage.StackValue;
import org.graalvm.nativeimage.c.CContext;
import org.graalvm.nativeimage.c.function.CFunction;
import org.graalvm.nativeimage.c.struct.CField;
import org.graalvm.nativeimage.c.struct.CPointerTo;
import org.graalvm.nativeimage.c.struct.CStruct;
import org.graalvm.nativeimage.c.type.CIntPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion;
import org.graalvm.nativeimage.c.type.VoidPointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.graalvm.word.PointerBase;
import org.graalvm.word.WordFactory;

@Platforms(Platform.WINDOWS.class)
@CContext(WindowsDirectives.class)
final class Kernel32 {

    public static final int FORMAT_MESSAGE_FROM_SYSTEM = 0x00001000;

    public static final int INVALID_HANDLE_VALUE = -1;
    public static final int STD_INPUT_HANDLE = -10;
    public static final int STD_OUTPUT_HANDLE = -11;
    public static final int STD_ERROR_HANDLE = -12;

    public static final int ENABLE_PROCESSED_INPUT = 0x0001;
    public static final int ENABLE_LINE_INPUT = 0x0002;
    public static final int ENABLE_ECHO_INPUT = 0x0004;
    public static final int ENABLE_WINDOW_INPUT = 0x0008;
    public static final int ENABLE_MOUSE_INPUT = 0x0010;
    public static final int ENABLE_INSERT_MODE = 0x0020;
    public static final int ENABLE_QUICK_EDIT_MODE = 0x0040;
    public static final int ENABLE_EXTENDED_FLAGS = 0x0080;

    public static final int RIGHT_ALT_PRESSED = 0x0001;
    public static final int LEFT_ALT_PRESSED = 0x0002;
    public static final int RIGHT_CTRL_PRESSED = 0x0004;
    public static final int LEFT_CTRL_PRESSED = 0x0008;
    public static final int SHIFT_PRESSED = 0x0010;

    public static final int FOREGROUND_BLUE = 0x0001;
    public static final int FOREGROUND_GREEN = 0x0002;
    public static final int FOREGROUND_RED = 0x0004;
    public static final int FOREGROUND_INTENSITY = 0x0008;
    public static final int BACKGROUND_BLUE = 0x0010;
    public static final int BACKGROUND_GREEN = 0x0020;
    public static final int BACKGROUND_RED = 0x0040;
    public static final int BACKGROUND_INTENSITY = 0x0080;

    // Button state
    public static final int FROM_LEFT_1ST_BUTTON_PRESSED = 0x0001;
    public static final int RIGHTMOST_BUTTON_PRESSED = 0x0002;
    public static final int FROM_LEFT_2ND_BUTTON_PRESSED = 0x0004;
    public static final int FROM_LEFT_3RD_BUTTON_PRESSED = 0x0008;
    public static final int FROM_LEFT_4TH_BUTTON_PRESSED = 0x0010;

    // Event flags
    public static final int MOUSE_MOVED = 0x0001;
    public static final int DOUBLE_CLICK = 0x0002;
    public static final int MOUSE_WHEELED = 0x0004;
    public static final int MOUSE_HWHEELED = 0x0008;

    // Event types
    public static final short KEY_EVENT = 0x0001;
    public static final short MOUSE_EVENT = 0x0002;
    public static final short WINDOW_BUFFER_SIZE_EVENT = 0x0004;
    public static final short MENU_EVENT = 0x0008;
    public static final short FOCUS_EVENT = 0x0010;

    @CPointerTo(nameOfCType = "wchar_t")
    public interface WCharPointer extends PointerBase {}

    @CStruct("COORD")
    public interface COORD extends PointerBase {
        @CField("X")
        short getX();

        @CField("Y")
        short getY();
    }

    @CStruct("SMALL_RECT")
    public interface SmallRect extends PointerBase {
        @CField("Left")
        short getLeft();

        @CField("Top")
        short getTop();

        @CField("Right")
        short getRight();

        @CField("Bottom")
        short getBottom();
    }

    @CStruct("CONSOLE_SCREEN_BUFFER_INFO")
    public interface ConsoleScreenBufferInfo extends PointerBase {
        @CField("dwSize")
        COORD getDwSize();

        @CField("dwCursorPosition")
        COORD getDwCursorPosition();

        @CField("wAttributes")
        short getWAttributes();

        @CField("srWindow")
        SmallRect getSrWindow();

        @CField("dwMaximumWindowSize")
        COORD getDwMaximumWindowSize();
    }

    @CStruct("CHAR_INFO")
    public interface CharInfo extends PointerBase {
        @CField("UnicodeChar")
        char getUnicodeChar();

        @CField("AsciiChar")
        byte getAsciiChar();

        @CField("Attributes")
        short getAttributes();
    }

    @CPointerTo(SmallRect.class)
    public interface SmallRectPointer extends PointerBase {}

    @CPointerTo(ConsoleScreenBufferInfo.class)
    public interface ConsoleScreenBufferInfoPointer extends PointerBase {}

    @CPointerTo(CharInfo.class)
    public interface CharInfoPointer extends PointerBase {}

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    public static native VoidPointer GetStdHandle(int nStdHandle);

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    public static native int FormatMessageW(
            int dwFlags,
            VoidPointer lpSource,
            int dwMessageId,
            int dwLanguageId,
            WordPointer lpBuffer,
            int nSize,
            VoidPointer Arguments);

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    public static native int SetConsoleTextAttribute(VoidPointer hConsoleOutput, short wAttributes);

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    public static native int SetConsoleMode(VoidPointer hConsoleHandle, int dwMode);

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    public static native int GetConsoleMode(VoidPointer hConsoleHandle, CIntPointer lpMode);

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    public static native int SetConsoleTitleW(WCharPointer lpConsoleTitle);

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    public static native int SetConsoleCursorPosition(VoidPointer hConsoleOutput, COORD dwCursorPosition);

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    public static native int FillConsoleOutputCharacterW(
            VoidPointer hConsoleOutput,
            char cCharacter, // TODO: ?
            int nLength,
            COORD dwWriteCoord,
            CIntPointer lpNumberOfCharsWritten);

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    public static native int FillConsoleOutputAttribute(
            VoidPointer hConsoleOutput,
            short wAttribute,
            int nLength,
            COORD dwWriteCoord,
            CIntPointer lpNumberOfAttrsWritten);

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    public static native int WriteConsoleW(
            VoidPointer hConsoleOutput,
            WCharPointer lpBuffer,
            int nNumberOfCharsToWrite,
            CIntPointer lpNumberOfCharsWritten,
            VoidPointer lpReserved);

    //    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    //    public static native int ReadConsoleInputW(
    //            VoidPointer hConsoleInput, VoidPointer lpBuffer, int nLength, CIntPointer lpNumberOfEventsRead);

    //    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    //    public static native int PeekConsoleInputW(
    //            VoidPointer hConsoleInput, VoidPointer lpBuffer, int nLength, CIntPointer lpNumberOfEventsRead);

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    public static native int GetConsoleScreenBufferInfo(
            VoidPointer hConsoleOutput, ConsoleScreenBufferInfoPointer lpConsoleScreenBufferInfo);

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    public static native int ScrollConsoleScreenBuffer(
            VoidPointer hConsoleOutput,
            SmallRectPointer lpScrollRectangle,
            SmallRectPointer lpClipRectangle,
            COORD dwDestinationOrigin,
            CharInfoPointer lpFill);

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    public static native int GetLastError();

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    public static native int GetFileType(VoidPointer hFile);

    @CFunction(transition = CFunction.Transition.NO_TRANSITION)
    public static native VoidPointer _get_osfhandle(int fd);

    public static String getErrorMessage(int errorCode) {
        int bufferSize = 160;
        WordPointer data = StackValue.get(bufferSize);
        FormatMessageW(
                FORMAT_MESSAGE_FROM_SYSTEM,
                WordFactory.nullPointer(),
                errorCode,
                0,
                data,
                bufferSize,
                WordFactory.nullPointer());

        byte[] arr = new byte[bufferSize];
        CTypeConversion.asByteBuffer(data, bufferSize).get(arr);
        return new String(arr, StandardCharsets.UTF_16LE).trim();
    }

    static void f() {}
}
