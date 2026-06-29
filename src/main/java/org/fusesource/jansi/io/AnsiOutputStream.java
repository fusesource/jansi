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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.fusesource.jansi.AnsiColors;
import org.fusesource.jansi.AnsiMode;
import org.fusesource.jansi.AnsiType;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * An ANSI print stream extracts ANSI escape codes written to
 * an output stream and calls corresponding AnsiProcessor.process* methods.
 * This particular class is not synchronized for improved performance.
 *
 * <p>For more information about ANSI escape codes, see
 * <a href="http://en.wikipedia.org/wiki/ANSI_escape_code">Wikipedia article</a>
 *
 * @since 1.0
 * @see AnsiProcessor
 */
public class AnsiOutputStream extends FilterOutputStream {

    public static final byte[] RESET_CODE = "\033[0m".getBytes(US_ASCII);

    @FunctionalInterface
    public interface IoRunnable {
        void run() throws IOException;
    }

    @FunctionalInterface
    public interface WidthSupplier {
        int getTerminalWidth();
    }

    public static class ZeroWidthSupplier implements WidthSupplier {
        @Override
        public int getTerminalWidth() {
            return 0;
        }
    }

    private static final int LOOKING_FOR_FIRST_ESC_CHAR = 0;
    private static final int LOOKING_FOR_SECOND_ESC_CHAR = 1;
    private static final int LOOKING_FOR_NEXT_ARG = 2;
    private static final int LOOKING_FOR_STR_ARG_END = 3;
    private static final int LOOKING_FOR_INT_ARG_END = 4;
    private static final int LOOKING_FOR_OSC_COMMAND = 5;
    private static final int LOOKING_FOR_OSC_COMMAND_END = 6;
    private static final int LOOKING_FOR_OSC_PARAM = 7;
    private static final int LOOKING_FOR_ST = 8;
    private static final int LOOKING_FOR_CHARSET = 9;

    // Character Constants
    private static final int FIRST_ESC_CHAR = 27;
    private static final int SECOND_ESC_CHAR = '[';
    private static final int SECOND_OSC_CHAR = ']';
    private static final int BEL = 7;
    private static final int SECOND_ST_CHAR = '\\';
    private static final int SECOND_CHARSET0_CHAR = '(';
    private static final int SECOND_CHARSET1_CHAR = ')';

    private static final int MAX_ESCAPE_SEQUENCE_LENGTH = 100;
    private final byte[] buffer = new byte[MAX_ESCAPE_SEQUENCE_LENGTH];
    private final ArrayList<Object> options = new ArrayList<>();

    private AnsiProcessor ap;
    private int pos = 0;
    private int startOfValue;
    private int state = LOOKING_FOR_FIRST_ESC_CHAR;

    private final Charset cs;
    private final WidthSupplier width;
    private final AnsiProcessor processor;
    private final AnsiType type;
    private final AnsiColors colors;
    private final IoRunnable installer;
    private final IoRunnable uninstaller;

    private AnsiMode mode;
    private boolean resetAtUninstall;

    public AnsiOutputStream(
            OutputStream os,
            WidthSupplier width,
            AnsiMode mode,
            AnsiProcessor processor,
            AnsiType type,
            AnsiColors colors,
            Charset cs,
            IoRunnable installer,
            IoRunnable uninstaller,
            boolean resetAtUninstall) {
        super(os);
        this.width = width;
        this.processor = processor;
        this.type = type;
        this.colors = colors;
        this.installer = installer;
        this.uninstaller = uninstaller;
        this.resetAtUninstall = resetAtUninstall;
        this.cs = cs;
        setMode(mode);
    }

    public int getTerminalWidth() {
        return width.getTerminalWidth();
    }

    public AnsiType getType() {
        return type;
    }

    public AnsiColors getColors() {
        return colors;
    }

    public AnsiMode getMode() {
        return mode;
    }

    public void setMode(AnsiMode mode) {
        this.mode = mode;
        if (mode == AnsiMode.Strip) {
            this.ap = new AnsiProcessor(out);
        } else if (mode == AnsiMode.Force || processor == null) {
            this.ap = new ColorsAnsiProcessor(out, colors);
        } else {
            this.ap = processor;
        }
    }

    public boolean isResetAtUninstall() {
        return resetAtUninstall;
    }

    public void setResetAtUninstall(boolean resetAtUninstall) {
        this.resetAtUninstall = resetAtUninstall;
    }

    @Override
    public void write(int data) throws IOException {
        switch (state) {
            case LOOKING_FOR_FIRST_ESC_CHAR:
                handleFirstEscChar(data);
                break;
            case LOOKING_FOR_SECOND_ESC_CHAR:
                handleSecondEscChar(data);
                break;
            case LOOKING_FOR_NEXT_ARG:
                handleNextArg(data);
                break;
            case LOOKING_FOR_INT_ARG_END:
                handleIntArgEnd(data);
                break;
            case LOOKING_FOR_STR_ARG_END:
                handleStrArgEnd(data);
                break;
            case LOOKING_FOR_OSC_COMMAND:
                handleOscCommand(data);
                break;
            case LOOKING_FOR_OSC_COMMAND_END:
                handleOscCommandEnd(data);
                break;
            case LOOKING_FOR_OSC_PARAM:
                handleOscParam(data);
                break;
            case LOOKING_FOR_ST:
                handleSt(data);
                break;
            case LOOKING_FOR_CHARSET:
                options.add((char) data);
                executeAndReset(() -> ap.processCharsetSelect(options));
                break;
            default:
                reset(false);
                break;
        }

        if (pos >= buffer.length) {
            reset(false);
        }
    }

    private void handleFirstEscChar(int data) throws IOException {
        if (data == FIRST_ESC_CHAR) {
            buffer[pos++] = (byte) data;
            state = LOOKING_FOR_SECOND_ESC_CHAR;
        } else {
            out.write(data);
        }
    }

    private void handleSecondEscChar(int data) throws IOException {
        buffer[pos++] = (byte) data;
        if (data == SECOND_ESC_CHAR) {
            state = LOOKING_FOR_NEXT_ARG;
        } else if (data == SECOND_OSC_CHAR) {
            state = LOOKING_FOR_OSC_COMMAND;
        } else if (data == SECOND_CHARSET0_CHAR) {
            options.add(0);
            state = LOOKING_FOR_CHARSET;
        } else if (data == SECOND_CHARSET1_CHAR) {
            options.add(1);
            state = LOOKING_FOR_CHARSET;
        } else {
            reset(false);
        }
    }

    private void handleNextArg(int data) throws IOException {
        buffer[pos++] = (byte) data;
        if ('"' == data) {
            startOfValue = pos - 1;
            state = LOOKING_FOR_STR_ARG_END;
        } else if (Character.isDigit(data)) {
            startOfValue = pos - 1;
            state = LOOKING_FOR_INT_ARG_END;
        } else if (';' == data) {
            options.add(null);
        } else if ('?' == data) {
            options.add('?');
        } else if ('=' == data) {
            options.add('=');
        } else {
            executeAndReset(() -> ap.processEscapeCommand(options, data));
        }
    }

    private void handleIntArgEnd(int data) throws IOException {
        buffer[pos++] = (byte) data;
        if (!Character.isDigit(data)) {
            addCurrentValueAsInteger();
            if (data == ';') {
                state = LOOKING_FOR_NEXT_ARG;
            } else {
                executeAndReset(() -> ap.processEscapeCommand(options, data));
            }
        }
    }

    private void handleStrArgEnd(int data) throws IOException {
        buffer[pos++] = (byte) data;
        if ('"' != data) {
            addCurrentValueAsString();
            if (data == ';') {
                state = LOOKING_FOR_NEXT_ARG;
            } else {
                executeAndReset(() -> ap.processEscapeCommand(options, data));
            }
        }
    }

    private void handleOscCommand(int data) throws IOException {
        buffer[pos++] = (byte) data;
        if (Character.isDigit(data)) {
            startOfValue = pos - 1;
            state = LOOKING_FOR_OSC_COMMAND_END;
        } else {
            reset(false);
        }
    }

    private void handleOscCommandEnd(int data) throws IOException {
        buffer[pos++] = (byte) data;
        if (';' == data) {
            addCurrentValueAsInteger();
            startOfValue = pos;
            state = LOOKING_FOR_OSC_PARAM;
        } else if (!Character.isDigit(data)) {
            reset(false);
        }
    }

    private void handleOscParam(int data) throws IOException {
        buffer[pos++] = (byte) data;
        if (BEL == data) {
            addCurrentValueAsString();
            executeAndReset(() -> ap.processOperatingSystemCommand(options));
        } else if (FIRST_ESC_CHAR == data) {
            state = LOOKING_FOR_ST;
        }
    }

    private void handleSt(int data) throws IOException {
        buffer[pos++] = (byte) data;
        if (SECOND_ST_CHAR == data) {
            String value = new String(buffer, startOfValue, (pos - 2) - startOfValue, cs);
            options.add(value);
            executeAndReset(() -> ap.processOperatingSystemCommand(options));
        } else {
            state = LOOKING_FOR_OSC_PARAM;
        }
    }

    // --- Data Extraction Helpers ---

    private void addCurrentValueAsInteger() {
        String strValue = new String(buffer, startOfValue, (pos - 1) - startOfValue, US_ASCII);
        options.add(Integer.valueOf(strValue));
    }

    private void addCurrentValueAsString() {
        String value = new String(buffer, startOfValue, (pos - 1) - startOfValue, cs);
        options.add(value);
    }

    // --- Execution and State Reset ---

    @FunctionalInterface
    private interface ProcessorAction {
        boolean execute() throws IOException;
    }

    private void executeAndReset(ProcessorAction action) throws IOException {
        try {
            reset(ap != null && action.execute());
        } catch (RuntimeException e) {
            reset(true);
            throw e;
        }
    }

    private void reset(boolean skipBuffer) throws IOException {
        if (!skipBuffer) {
            out.write(buffer, 0, pos);
        }
        pos = 0;
        startOfValue = 0;
        options.clear();
        state = LOOKING_FOR_FIRST_ESC_CHAR;
    }

    // --- Lifecycle Methods ---

    public void install() throws IOException {
        if (installer != null) {
            installer.run();
        }
    }

    public void uninstall() throws IOException {
        if (resetAtUninstall && type != AnsiType.Redirected && type != AnsiType.Unsupported) {
            setMode(AnsiMode.Default);
            write(RESET_CODE);
            flush();
        }
        if (uninstaller != null) {
            uninstaller.run();
        }
    }

    @Override
    public void close() throws IOException {
        uninstall();
        super.close();
    }
}