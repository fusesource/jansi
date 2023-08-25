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
package org.fusesource.jansi.io;

import java.io.IOException;
import java.io.OutputStream;

import org.fusesource.jansi.WindowsSupport;
import org.fusesource.jansi.internal.Kernel32.CONSOLE_SCREEN_BUFFER_INFO;
import org.fusesource.jansi.internal.Kernel32.COORD;

import static org.fusesource.jansi.internal.Kernel32.BACKGROUND_BLUE;
import static org.fusesource.jansi.internal.Kernel32.BACKGROUND_GREEN;
import static org.fusesource.jansi.internal.Kernel32.BACKGROUND_INTENSITY;
import static org.fusesource.jansi.internal.Kernel32.BACKGROUND_RED;
import static org.fusesource.jansi.internal.Kernel32.CHAR_INFO;
import static org.fusesource.jansi.internal.Kernel32.FOREGROUND_BLUE;
import static org.fusesource.jansi.internal.Kernel32.FOREGROUND_GREEN;
import static org.fusesource.jansi.internal.Kernel32.FOREGROUND_INTENSITY;
import static org.fusesource.jansi.internal.Kernel32.FOREGROUND_RED;
import static org.fusesource.jansi.internal.Kernel32.FillConsoleOutputAttribute;
import static org.fusesource.jansi.internal.Kernel32.FillConsoleOutputCharacterW;
import static org.fusesource.jansi.internal.Kernel32.GetConsoleScreenBufferInfo;
import static org.fusesource.jansi.internal.Kernel32.GetStdHandle;
import static org.fusesource.jansi.internal.Kernel32.SMALL_RECT;
import static org.fusesource.jansi.internal.Kernel32.STD_ERROR_HANDLE;
import static org.fusesource.jansi.internal.Kernel32.STD_OUTPUT_HANDLE;
import static org.fusesource.jansi.internal.Kernel32.ScrollConsoleScreenBuffer;
import static org.fusesource.jansi.internal.Kernel32.SetConsoleCursorPosition;
import static org.fusesource.jansi.internal.Kernel32.SetConsoleTextAttribute;
import static org.fusesource.jansi.internal.Kernel32.SetConsoleTitle;

/**
 * A Windows ANSI escape processor, that uses JNA to access native platform
 * API's to change the console attributes (see
 * <a href="http://fusesource.github.io/jansi/documentation/native-api/index.html?org/fusesource/jansi/internal/Kernel32.html">Jansi native Kernel32</a>).
 * <p>The native library used is named <code>jansi</code> and is loaded using <a href="http://fusesource.github.io/hawtjni/">HawtJNI</a> Runtime
 * <a href="http://fusesource.github.io/hawtjni/documentation/api/index.html?org/fusesource/hawtjni/runtime/Library.html"><code>Library</code></a>
 *
 * @since 1.19
 */
public final class WindowsAnsiProcessor extends AnsiProcessor {

    private final long console;

    private static final short FOREGROUND_BLACK = 0;
    private static final short FOREGROUND_YELLOW = (short) (FOREGROUND_RED | FOREGROUND_GREEN);
    private static final short FOREGROUND_MAGENTA = (short) (FOREGROUND_BLUE | FOREGROUND_RED);
    private static final short FOREGROUND_CYAN = (short) (FOREGROUND_BLUE | FOREGROUND_GREEN);
    private static final short FOREGROUND_WHITE = (short) (FOREGROUND_RED | FOREGROUND_GREEN | FOREGROUND_BLUE);

    private static final short BACKGROUND_BLACK = 0;
    private static final short BACKGROUND_YELLOW = (short) (BACKGROUND_RED | BACKGROUND_GREEN);
    private static final short BACKGROUND_MAGENTA = (short) (BACKGROUND_BLUE | BACKGROUND_RED);
    private static final short BACKGROUND_CYAN = (short) (BACKGROUND_BLUE | BACKGROUND_GREEN);
    private static final short BACKGROUND_WHITE = (short) (BACKGROUND_RED | BACKGROUND_GREEN | BACKGROUND_BLUE);

    private static final short[] ANSI_FOREGROUND_COLOR_MAP = {
        FOREGROUND_BLACK,
        FOREGROUND_RED,
        FOREGROUND_GREEN,
        FOREGROUND_YELLOW,
        FOREGROUND_BLUE,
        FOREGROUND_MAGENTA,
        FOREGROUND_CYAN,
        FOREGROUND_WHITE,
    };

    private static final short[] ANSI_BACKGROUND_COLOR_MAP = {
        BACKGROUND_BLACK,
        BACKGROUND_RED,
        BACKGROUND_GREEN,
        BACKGROUND_YELLOW,
        BACKGROUND_BLUE,
        BACKGROUND_MAGENTA,
        BACKGROUND_CYAN,
        BACKGROUND_WHITE,
    };

    private final CONSOLE_SCREEN_BUFFER_INFO info = new CONSOLE_SCREEN_BUFFER_INFO();
    private final short originalColors;

    private boolean negative;
    private short savedX = -1;
    private short savedY = -1;

    public WindowsAnsiProcessor(OutputStream ps, long console) throws IOException {
        super(ps);
        this.console = console;
        getConsoleInfo();
        originalColors = info.attributes;
    }

    public WindowsAnsiProcessor(OutputStream ps, boolean stdout) throws IOException {
        this(ps, GetStdHandle(stdout ? STD_OUTPUT_HANDLE : STD_ERROR_HANDLE));
    }

    public WindowsAnsiProcessor(OutputStream ps) throws IOException {
        this(ps, true);
    }

    private void getConsoleInfo() throws IOException {
        os.flush();
        if (GetConsoleScreenBufferInfo(console, info) == 0) {
            throw new IOException("Could not get the screen info: " + WindowsSupport.getLastErrorMessage());
        }
        if (negative) {
            info.attributes = invertAttributeColors(info.attributes);
        }
    }

    private void applyAttribute() throws IOException {
        os.flush();
        short attributes = info.attributes;
        if (negative) {
            attributes = invertAttributeColors(attributes);
        }
        if (SetConsoleTextAttribute(console, attributes) == 0) {
            throw new IOException(WindowsSupport.getLastErrorMessage());
        }
    }

    private short invertAttributeColors(short attributes) {
        // Swap the the Foreground and Background bits.
        int fg = 0x000F & attributes;
        fg <<= 4;
        int bg = 0X00F0 & attributes;
        bg >>= 4;
        attributes = (short) ((attributes & 0xFF00) | fg | bg);
        return attributes;
    }

    private void applyCursorPosition() throws IOException {
        if (SetConsoleCursorPosition(console, info.cursorPosition.copy()) == 0) {
            throw new IOException(WindowsSupport.getLastErrorMessage());
        }
    }

    @Override
    protected void processEraseScreen(int eraseOption) throws IOException {
        getConsoleInfo();
        int[] written = new int[1];
        switch (eraseOption) {
            case ERASE_SCREEN:
                COORD topLeft = new COORD();
                topLeft.x = 0;
                topLeft.y = info.window.top;
                int screenLength = info.window.height() * info.size.x;
                FillConsoleOutputAttribute(console, info.attributes, screenLength, topLeft, written);
                FillConsoleOutputCharacterW(console, ' ', screenLength, topLeft, written);
                break;
            case ERASE_SCREEN_TO_BEGINING:
                COORD topLeft2 = new COORD();
                topLeft2.x = 0;
                topLeft2.y = info.window.top;
                int lengthToCursor = (info.cursorPosition.y - info.window.top) * info.size.x + info.cursorPosition.x;
                FillConsoleOutputAttribute(console, info.attributes, lengthToCursor, topLeft2, written);
                FillConsoleOutputCharacterW(console, ' ', lengthToCursor, topLeft2, written);
                break;
            case ERASE_SCREEN_TO_END:
                int lengthToEnd = (info.window.bottom - info.cursorPosition.y) * info.size.x
                        + (info.size.x - info.cursorPosition.x);
                FillConsoleOutputAttribute(console, info.attributes, lengthToEnd, info.cursorPosition.copy(), written);
                FillConsoleOutputCharacterW(console, ' ', lengthToEnd, info.cursorPosition.copy(), written);
                break;
            default:
                break;
        }
    }

    @Override
    protected void processEraseLine(int eraseOption) throws IOException {
        getConsoleInfo();
        int[] written = new int[1];
        switch (eraseOption) {
            case ERASE_LINE:
                COORD leftColCurrRow = info.cursorPosition.copy();
                leftColCurrRow.x = 0;
                FillConsoleOutputAttribute(console, info.attributes, info.size.x, leftColCurrRow, written);
                FillConsoleOutputCharacterW(console, ' ', info.size.x, leftColCurrRow, written);
                break;
            case ERASE_LINE_TO_BEGINING:
                COORD leftColCurrRow2 = info.cursorPosition.copy();
                leftColCurrRow2.x = 0;
                FillConsoleOutputAttribute(console, info.attributes, info.cursorPosition.x, leftColCurrRow2, written);
                FillConsoleOutputCharacterW(console, ' ', info.cursorPosition.x, leftColCurrRow2, written);
                break;
            case ERASE_LINE_TO_END:
                int lengthToLastCol = info.size.x - info.cursorPosition.x;
                FillConsoleOutputAttribute(
                        console, info.attributes, lengthToLastCol, info.cursorPosition.copy(), written);
                FillConsoleOutputCharacterW(console, ' ', lengthToLastCol, info.cursorPosition.copy(), written);
                break;
            default:
                break;
        }
    }

    @Override
    protected void processCursorLeft(int count) throws IOException {
        getConsoleInfo();
        info.cursorPosition.x = (short) Math.max(0, info.cursorPosition.x - count);
        applyCursorPosition();
    }

    @Override
    protected void processCursorRight(int count) throws IOException {
        getConsoleInfo();
        info.cursorPosition.x = (short) Math.min(info.window.width(), info.cursorPosition.x + count);
        applyCursorPosition();
    }

    @Override
    protected void processCursorDown(int count) throws IOException {
        getConsoleInfo();
        info.cursorPosition.y = (short) Math.min(Math.max(0, info.size.y - 1), info.cursorPosition.y + count);
        applyCursorPosition();
    }

    @Override
    protected void processCursorUp(int count) throws IOException {
        getConsoleInfo();
        info.cursorPosition.y = (short) Math.max(info.window.top, info.cursorPosition.y - count);
        applyCursorPosition();
    }

    @Override
    protected void processCursorTo(int row, int col) throws IOException {
        getConsoleInfo();
        info.cursorPosition.y = (short) Math.max(info.window.top, Math.min(info.size.y, info.window.top + row - 1));
        info.cursorPosition.x = (short) Math.max(0, Math.min(info.window.width(), col - 1));
        applyCursorPosition();
    }

    @Override
    protected void processCursorToColumn(int x) throws IOException {
        getConsoleInfo();
        info.cursorPosition.x = (short) Math.max(0, Math.min(info.window.width(), x - 1));
        applyCursorPosition();
    }

    @Override
    protected void processCursorUpLine(int count) throws IOException {
        getConsoleInfo();
        info.cursorPosition.x = 0;
        info.cursorPosition.y = (short) Math.max(info.window.top, info.cursorPosition.y - count);
        applyCursorPosition();
    }

    @Override
    protected void processCursorDownLine(int count) throws IOException {
        getConsoleInfo();
        info.cursorPosition.x = 0;
        info.cursorPosition.y = (short) Math.max(info.window.top, info.cursorPosition.y + count);
        applyCursorPosition();
    }

    @Override
    protected void processSetForegroundColor(int color, boolean bright) throws IOException {
        info.attributes = (short) ((info.attributes & ~0x0007) | ANSI_FOREGROUND_COLOR_MAP[color]);
        if (bright) {
            info.attributes |= FOREGROUND_INTENSITY;
        }
        applyAttribute();
    }

    @Override
    protected void processSetForegroundColorExt(int paletteIndex) throws IOException {
        int round = Colors.roundColor(paletteIndex, 16);
        processSetForegroundColor(round >= 8 ? round - 8 : round, round >= 8);
    }

    @Override
    protected void processSetForegroundColorExt(int r, int g, int b) throws IOException {
        int round = Colors.roundRgbColor(r, g, b, 16);
        processSetForegroundColor(round >= 8 ? round - 8 : round, round >= 8);
    }

    @Override
    protected void processSetBackgroundColor(int color, boolean bright) throws IOException {
        info.attributes = (short) ((info.attributes & ~0x0070) | ANSI_BACKGROUND_COLOR_MAP[color]);
        if (bright) {
            info.attributes |= BACKGROUND_INTENSITY;
        }
        applyAttribute();
    }

    @Override
    protected void processSetBackgroundColorExt(int paletteIndex) throws IOException {
        int round = Colors.roundColor(paletteIndex, 16);
        processSetBackgroundColor(round >= 8 ? round - 8 : round, round >= 8);
    }

    @Override
    protected void processSetBackgroundColorExt(int r, int g, int b) throws IOException {
        int round = Colors.roundRgbColor(r, g, b, 16);
        processSetBackgroundColor(round >= 8 ? round - 8 : round, round >= 8);
    }

    @Override
    protected void processDefaultTextColor() throws IOException {
        info.attributes = (short) ((info.attributes & ~0x000F) | (originalColors & 0xF));
        info.attributes = (short) (info.attributes & ~FOREGROUND_INTENSITY);
        applyAttribute();
    }

    @Override
    protected void processDefaultBackgroundColor() throws IOException {
        info.attributes = (short) ((info.attributes & ~0x00F0) | (originalColors & 0xF0));
        info.attributes = (short) (info.attributes & ~BACKGROUND_INTENSITY);
        applyAttribute();
    }

    @Override
    protected void processAttributeReset() throws IOException {
        info.attributes = (short) ((info.attributes & ~0x00FF) | originalColors);
        this.negative = false;
        applyAttribute();
    }

    @Override
    protected void processSetAttribute(int attribute) throws IOException {
        switch (attribute) {
            case ATTRIBUTE_INTENSITY_BOLD:
                info.attributes = (short) (info.attributes | FOREGROUND_INTENSITY);
                applyAttribute();
                break;
            case ATTRIBUTE_INTENSITY_NORMAL:
                info.attributes = (short) (info.attributes & ~FOREGROUND_INTENSITY);
                applyAttribute();
                break;

                // Yeah, setting the background intensity is not underlining.. but it's best we can do
                // using the Windows console API
            case ATTRIBUTE_UNDERLINE:
                info.attributes = (short) (info.attributes | BACKGROUND_INTENSITY);
                applyAttribute();
                break;
            case ATTRIBUTE_UNDERLINE_OFF:
                info.attributes = (short) (info.attributes & ~BACKGROUND_INTENSITY);
                applyAttribute();
                break;

            case ATTRIBUTE_NEGATIVE_ON:
                negative = true;
                applyAttribute();
                break;
            case ATTRIBUTE_NEGATIVE_OFF:
                negative = false;
                applyAttribute();
                break;
            default:
                break;
        }
    }

    @Override
    protected void processSaveCursorPosition() throws IOException {
        getConsoleInfo();
        savedX = info.cursorPosition.x;
        savedY = info.cursorPosition.y;
    }

    @Override
    protected void processRestoreCursorPosition() throws IOException {
        // restore only if there was a save operation first
        if (savedX != -1 && savedY != -1) {
            os.flush();
            info.cursorPosition.x = savedX;
            info.cursorPosition.y = savedY;
            applyCursorPosition();
        }
    }

    @Override
    protected void processInsertLine(int optionInt) throws IOException {
        getConsoleInfo();
        SMALL_RECT scroll = info.window.copy();
        scroll.top = info.cursorPosition.y;
        COORD org = new COORD();
        org.x = 0;
        org.y = (short) (info.cursorPosition.y + optionInt);
        CHAR_INFO info = new CHAR_INFO();
        info.attributes = originalColors;
        info.unicodeChar = ' ';
        if (ScrollConsoleScreenBuffer(console, scroll, scroll, org, info) == 0) {
            throw new IOException(WindowsSupport.getLastErrorMessage());
        }
    }

    @Override
    protected void processDeleteLine(int optionInt) throws IOException {
        getConsoleInfo();
        SMALL_RECT scroll = info.window.copy();
        scroll.top = info.cursorPosition.y;
        COORD org = new COORD();
        org.x = 0;
        org.y = (short) (info.cursorPosition.y - optionInt);
        CHAR_INFO info = new CHAR_INFO();
        info.attributes = originalColors;
        info.unicodeChar = ' ';
        if (ScrollConsoleScreenBuffer(console, scroll, scroll, org, info) == 0) {
            throw new IOException(WindowsSupport.getLastErrorMessage());
        }
    }

    @Override
    protected void processChangeWindowTitle(String label) {
        SetConsoleTitle(label);
    }
}
