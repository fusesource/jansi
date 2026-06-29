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

import java.util.logging.Logger;
import org.fusesource.jansi.internal.struct.*;

/**
 * Windows-only JNI bridge to the Win32 Kernel32 console API.
 *
 * <p>This class exposes a curated subset of the Win32 console functions needed
 * by jansi to control terminal colors, cursor position, screen clearing, and
 * Virtual Terminal Processing on Windows 10/11. All methods are native and
 * loaded from the bundled {@code jansi} JNI library via {@link JansiLoader}.</p>
 *
 * <p>On non-Windows platforms this class is present in the JAR but the native
 * library will not load; callers should check {@link #LOADED} before invoking
 * any native method.</p>
 *
 * @author Hiram Chirino
 * @since 1.0
 * @see CLibrary
 * @see <a href="https://docs.microsoft.com/en-us/windows/console/console-functions">
 * Win32 Console Functions (MSDN)</a>
 */
public class Kernel32 {

    private static final Logger LOG = Logger.getLogger(Kernel32.class.getName());

    public static final boolean LOADED;

    static {
        boolean loaded = false;
        try {
            JansiLoader.initialize();
            init();
            loaded = true;
        } catch (Throwable e) {
            LOG.fine("Kernel32 native library could not be loaded "
                    + "(expected on non-Windows platforms): " + e);
        }
        LOADED = loaded;
    }

    private static native void init();

    private Kernel32() {}

    public static int STD_INPUT_HANDLE;
    public static int STD_OUTPUT_HANDLE;
    public static int STD_ERROR_HANDLE;
    public static long INVALID_HANDLE_VALUE;

    public static short FOREGROUND_BLUE;
    public static short FOREGROUND_GREEN;
    public static short FOREGROUND_RED;
    public static short FOREGROUND_INTENSITY;

    public static short BACKGROUND_BLUE;
    public static short BACKGROUND_GREEN;
    public static short BACKGROUND_RED;
    public static short BACKGROUND_INTENSITY;

    public static short COMMON_LVB_LEADING_BYTE;
    public static short COMMON_LVB_TRAILING_BYTE;
    public static short COMMON_LVB_GRID_HORIZONTAL;
    public static short COMMON_LVB_GRID_LVERTICAL;
    public static short COMMON_LVB_GRID_RVERTICAL;
    public static short COMMON_LVB_REVERSE_VIDEO;
    public static short COMMON_LVB_UNDERSCORE;

    public static int ENABLE_VIRTUAL_TERMINAL_PROCESSING;
    public static int ENABLE_PROCESSED_OUTPUT;
    public static int ENABLE_WRAP_AT_EOL_OUTPUT;

    public static int FORMAT_MESSAGE_FROM_SYSTEM;

    public static short KEY_EVENT;
    public static short MOUSE_EVENT;
    public static short WINDOW_BUFFER_SIZE_EVENT;
    public static short FOCUS_EVENT;
    public static short MENU_EVENT;

    public static native long GetStdHandle(int nStdHandle);

    public static native int GetConsoleMode(long hConsoleHandle, int[] lpMode);

    public static native int SetConsoleMode(long hConsoleHandle, int dwMode);

    public static native int GetConsoleScreenBufferInfo(
            long hConsoleOutput, CONSOLE_SCREEN_BUFFER_INFO lpConsoleScreenBufferInfo);

    public static native int SetConsoleTextAttribute(long hConsoleOutput, short wAttributes);

    public static native int SetConsoleTitleW(byte[] lpConsoleTitle);

    public static native int SetConsoleCursorPosition(long hConsoleOutput, COORD dwCursorPosition);

    public static native int FillConsoleOutputCharacterW(
            long hConsoleOutput, char cCharacter, int nLength,
            COORD dwWriteCoord, int[] lpNumberOfCharsWritten);

    public static native int FillConsoleOutputAttribute(
            long hConsoleOutput, short wAttribute, int nLength,
            COORD dwWriteCoord, int[] lpNumberOfAttrsWritten);

    public static native int ScrollConsoleScreenBuffer(
            long hConsoleOutput, SMALL_RECT lpScrollRectangle,
            SMALL_RECT lpClipRectangle, COORD dwDestinationOrigin, CHAR_INFO lpFill);

    public static native int WriteConsoleW(
            long hConsoleOutput, char[] lpBuffer, int nNumberOfCharsToWrite,
            int[] lpNumberOfCharsWritten, long lpReserved);

    public static native int CloseHandle(long hObject);

    public static native int GetLastError();

    public static native int GetConsoleOutputCP();

    public static native int FormatMessageW(
            int dwFlags, long lpSource, int dwMessageId, int dwLanguageId,
            byte[] lpBuffer, int nSize, long[] va_list);

    public static native int ReadConsoleInputW(
            long hConsoleInput, long lpBuffer, int nLength, int[] lpNumberOfEventsRead);

    public static native int PeekConsoleInputW(
            long hConsoleInput, long lpBuffer, int nLength, int[] lpNumberOfEventsRead);

    public static native int GetNumberOfConsoleInputEvents(
            long hConsoleInput, int[] lpNumberOfEvents);

    public static native int FlushConsoleInputBuffer(long hConsoleInput);

    public static INPUT_RECORD[] readConsoleInputHelper(
            long hConsoleInput, int count, boolean wait) throws java.io.IOException {
        int[] eventsRead = new int[1];
        INPUT_RECORD[] records = new INPUT_RECORD[count];
        return records;
    }

    public static INPUT_RECORD[] readConsoleKeyInput(
            long hConsoleInput, int count, boolean wait) throws java.io.IOException {
        while (true) {
            INPUT_RECORD[] events = readConsoleInputHelper(hConsoleInput, count, wait);
            java.util.List<INPUT_RECORD> keyEvents = new java.util.ArrayList<>();
            for (INPUT_RECORD event : events) {
                if (event.eventType == KEY_EVENT) {
                    keyEvents.add(event);
                }
            }
            if (!keyEvents.isEmpty() || !wait) {
                return keyEvents.toArray(new INPUT_RECORD[0]);
            }
        }
    }
}