/*
 * Copyright (C) 2009-2017 the original author(s).
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
package org.fusesource.jansi;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

import org.fusesource.jansi.impl.FlushBufferedWriter;
import org.fusesource.jansi.impl.TerminalCommandProcessor;
import org.fusesource.jansi.impl.TerminalType;

/**
 * A ANSI output writer filters out ANSI escape codes
 * and calls corresponding <code>process*</code> methods.
 *
 * <p>For more information about ANSI escape codes, see
 * <a href="http://en.wikipedia.org/wiki/ANSI_escape_code">Wikipedia article</a>
 *
 * <p>This class just filters out the escape codes so that they are not
 * sent out to the underlying OutputStream: <code>process*</code> methods
 * are empty. Subclasses should actually perform the ANSI escape behaviors
 * by implementing active code in <code>process*</code> methods.
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 * @author Joris Kuipers
 * @since 1.0
 */
public class AnsiFilterWriter extends FilterWriter {

    public static final char[] RESET_CODE = "\033[0m".toCharArray();

    private static final int TMP_WRITE_BUFFER_LENGTH = 400;
    private static final int MAX_ESCAPE_SEQUENCE_LENGTH = 100;

    // enum constants for state
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

    // enum constants for escape chars
    private static final int FIRST_ESC_CHAR = 27;
    private static final int SECOND_ESC_CHAR = '[';
    private static final int SECOND_OSC_CHAR = ']';
    private static final int BEL = 7;
    private static final int SECOND_ST_CHAR = '\\';
    private static final int SECOND_CHARSET0_CHAR = '(';
    private static final int SECOND_CHARSET1_CHAR = ')';

    private final char[] strToCharBuffer = new char[TMP_WRITE_BUFFER_LENGTH];
    private final char[] buffer = new char[MAX_ESCAPE_SEQUENCE_LENGTH];
    private int pos = 0;
    private int startOfValue;
    private final ArrayList<Object> options = new ArrayList<Object>();

    protected int state = LOOKING_FOR_FIRST_ESC_CHAR;

    /** processor to delegate commands */
    protected TerminalCommandProcessor terminalCommandProcessor;

    public AnsiFilterWriter(Writer os) {
        super(new FlushBufferedWriter(os, TMP_WRITE_BUFFER_LENGTH));
        if (terminalCommandProcessor == null) {
            throw new IllegalArgumentException();
        }
        this.terminalCommandProcessor = new TerminalCommandProcessor(out);
    }

    public AnsiFilterWriter(Writer os, TerminalType terminalType) {
        super(new FlushBufferedWriter(os, TMP_WRITE_BUFFER_LENGTH));
        if (terminalType == null) {
            throw new IllegalArgumentException();
        }
        try {
            // may throws IOException ? move code from constructor to an init method
            this.terminalCommandProcessor = terminalType.create(out);
        } catch(IOException ex) {
            throw new RuntimeException("Failed", ex);
        }
        if (terminalCommandProcessor == null) {
            throw new IllegalStateException("null terminalCommandProcessor");
        }
    }

    public TerminalCommandProcessor getTerminalCommandProcessor() {
        return terminalCommandProcessor;
    }

    @Override
    public void close() throws IOException {
        write(RESET_CODE);
        flush();
        super.close();
    }

    protected FlushBufferedWriter bufferedOut() {
        return (FlushBufferedWriter) super.out;
    }

    // override java.io.Writer methods to force filtering char by char
    // ------------------------------------------------------------------------

    @Override
    public void write(int c) throws IOException {
        char ch = (char) c;
        if (filterChar(ch)) {
            FlushBufferedWriter bufferedOut = bufferedOut();
            bufferedOut.write(ch);
            bufferedOut.flushBuffer();
        }
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        FlushBufferedWriter bufferedOut = bufferedOut();
        final int max = off + len;
        for (int i = off; i < max; i++) {
            char ch = cbuf[i];
            if (filterChar(ch)) {
                bufferedOut.write(ch);
            }
        }
        bufferedOut.flushBuffer();
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        char[] buf = (len < strToCharBuffer.length)? strToCharBuffer : new char[len];
        str.getChars(off, off + len, buf, 0);
        write(buf, 0, len);
    }

    // Internal: interpretation of ANSI escape chars
    // ------------------------------------------------------------------------

    protected boolean filterChar(char data) throws IOException  {
        switch (state) {
            case LOOKING_FOR_FIRST_ESC_CHAR:
                if (data == FIRST_ESC_CHAR) {
                    buffer[pos++] = (char) data;
                    state = LOOKING_FOR_SECOND_ESC_CHAR;
                    return false;
                }
                return true;

            case LOOKING_FOR_SECOND_ESC_CHAR:
                buffer[pos++] = data;
                if (data == SECOND_ESC_CHAR) {
                    state = LOOKING_FOR_NEXT_ARG;
                } else if (data == SECOND_OSC_CHAR) {
                    state = LOOKING_FOR_OSC_COMMAND;
                } else if (data == SECOND_CHARSET0_CHAR) {
                    options.add(Integer.valueOf(0));
                    state = LOOKING_FOR_CHARSET;
                } else if (data == SECOND_CHARSET1_CHAR) {
                    options.add(Integer.valueOf(1));
                    state = LOOKING_FOR_CHARSET;
                } else {
                    reset(false);
                }
                break;

            case LOOKING_FOR_NEXT_ARG:
                buffer[pos++] = data;
                if ('"' == data) {
                    startOfValue = pos - 1;
                    state = LOOKING_FOR_STR_ARG_END;
                } else if ('0' <= data && data <= '9') {
                    startOfValue = pos - 1;
                    state = LOOKING_FOR_INT_ARG_END;
                } else if (';' == data) {
                    options.add(null);
                } else if ('?' == data) {
                    options.add('?');
                } else if ('=' == data) {
                    options.add('=');
                } else {
                    reset(processEscapeCommand(options, data));
                }
                break;
            default:
                break;

            case LOOKING_FOR_INT_ARG_END:
                buffer[pos++] = data;
                if (!('0' <= data && data <= '9')) {
                    String strValue = new String(buffer, startOfValue, (pos - 1) - startOfValue);
                    Integer value = Integer.valueOf(strValue);
                    options.add(value);
                    if (data == ';') {
                        state = LOOKING_FOR_NEXT_ARG;
                    } else {
                        reset(processEscapeCommand(options, data));
                    }
                }
                break;

            case LOOKING_FOR_STR_ARG_END:
                buffer[pos++] = data;
                if ('"' != data) {
                    String value = new String(buffer, startOfValue, (pos - 1) - startOfValue);
                    options.add(value);
                    if (data == ';') {
                        state = LOOKING_FOR_NEXT_ARG;
                    } else {
                        reset(processEscapeCommand(options, data));
                    }
                }
                break;

            case LOOKING_FOR_OSC_COMMAND:
                buffer[pos++] = data;
                if ('0' <= data && data <= '9') {
                    startOfValue = pos - 1;
                    state = LOOKING_FOR_OSC_COMMAND_END;
                } else {
                    reset(false);
                }
                break;

            case LOOKING_FOR_OSC_COMMAND_END:
                buffer[pos++] = data;
                if (';' == data) {
                    String strValue = new String(buffer, startOfValue, (pos - 1) - startOfValue);
                    Integer value = Integer.valueOf(strValue);
                    options.add(value);
                    startOfValue = pos;
                    state = LOOKING_FOR_OSC_PARAM;
                } else if ('0' <= data && data <= '9') {
                    // already pushed digit to buffer, just keep looking
                } else {
                    // oops, did not expect this
                    reset(false);
                }
                break;

            case LOOKING_FOR_OSC_PARAM:
                buffer[pos++] = data;
                if (BEL == data) {
                    String value = new String(buffer, startOfValue, (pos - 1) - startOfValue);
                    options.add(value);
                    reset(processOperatingSystemCommand(options));
                } else if (FIRST_ESC_CHAR == data) {
                    state = LOOKING_FOR_ST;
                } else {
                    // just keep looking while adding text
                }
                break;

            case LOOKING_FOR_ST:
                buffer[pos++] = data;
                if (SECOND_ST_CHAR == data) {
                    String value = new String(buffer, startOfValue, (pos - 2) - startOfValue);
                    options.add(value);
                    reset(processOperatingSystemCommand(options));
                } else {
                    state = LOOKING_FOR_OSC_PARAM;
                }
                break;

            case LOOKING_FOR_CHARSET:
                options.add(Character.valueOf(data));
                reset(processCharsetSelect(options));
                break;
        }

        // Is it just too long?
        if (pos >= buffer.length) {
            reset(false);
        }
        return false;
    }

    /**
     * Resets all state to continue with regular parsing
     * @param skipBuffer if current buffer should be skipped or written to out
     * @throws IOException
     */
    private void reset(boolean skipBuffer) throws IOException { // expected diff with AnsiPrintStream.java
        if (!skipBuffer) {
            out.write(buffer, 0, pos);
        }
        pos = 0;
        startOfValue = 0;
        options.clear();
        state = LOOKING_FOR_FIRST_ESC_CHAR;
    }

    /**
     * Helper for processEscapeCommand() to iterate over integer options
     * @param  optionsIterator  the underlying iterator
     * @throws IOException      if no more non-null values left
     */
    private int getNextOptionInt(Iterator<Object> optionsIterator) throws IOException {
        for (;;) {
            if (!optionsIterator.hasNext())
                throw new IllegalArgumentException();
            Object arg = optionsIterator.next();
            if (arg != null)
                return (Integer) arg;
        }
    }

    /**
     *
     * @param options
     * @param command
     * @return true if the escape command was processed.
     */
    private boolean processEscapeCommand(ArrayList<Object> options, int command) throws IOException {
        try {
            switch (command) {
                case 'A':
                    terminalCommandProcessor.processCursorUp(optionInt(options, 0, 1));
                    return true;
                case 'B':
                    terminalCommandProcessor.processCursorDown(optionInt(options, 0, 1));
                    return true;
                case 'C':
                    terminalCommandProcessor.processCursorRight(optionInt(options, 0, 1));
                    return true;
                case 'D':
                    terminalCommandProcessor.processCursorLeft(optionInt(options, 0, 1));
                    return true;
                case 'E':
                    terminalCommandProcessor.processCursorDownLine(optionInt(options, 0, 1));
                    return true;
                case 'F':
                    terminalCommandProcessor.processCursorUpLine(optionInt(options, 0, 1));
                    return true;
                case 'G':
                    terminalCommandProcessor.processCursorToColumn(optionInt(options, 0));
                    return true;
                case 'H':
                case 'f':
                    terminalCommandProcessor.processCursorTo(optionInt(options, 0, 1), optionInt(options, 1, 1));
                    return true;
                case 'J':
                    terminalCommandProcessor.processEraseScreen(optionInt(options, 0, 0));
                    return true;
                case 'K':
                    terminalCommandProcessor.processEraseLine(optionInt(options, 0, 0));
                    return true;
                case 'L':
                    terminalCommandProcessor.processInsertLine(optionInt(options, 0, 1));
                    return true;
                case 'M':
                    terminalCommandProcessor.processDeleteLine(optionInt(options, 0, 1));
                    return true;
                case 'S':
                    terminalCommandProcessor.processScrollUp(optionInt(options, 0, 1));
                    return true;
                case 'T':
                    terminalCommandProcessor.processScrollDown(optionInt(options, 0, 1));
                    return true;
                case 'm':
                    // Validate all options are ints...
                    for (Object next : options) {
                        if (next != null && next.getClass() != Integer.class) {
                            throw new IllegalArgumentException();
                        }
                    }

                    int count = 0;
                    Iterator<Object> optionsIterator = options.iterator();
                    while (optionsIterator.hasNext()) {
                        Object next = optionsIterator.next();
                        if (next != null) {
                            count++;
                            int value = (Integer) next;
                            if (30 <= value && value <= 37) {
                                terminalCommandProcessor.processSetForegroundColor(value - 30);
                            } else if (40 <= value && value <= 47) {
                                terminalCommandProcessor.processSetBackgroundColor(value - 40);
                            } else if (90 <= value && value <= 97) {
                                terminalCommandProcessor.processSetForegroundColor(value - 90, true);
                            } else if (100 <= value && value <= 107) {
                                terminalCommandProcessor.processSetBackgroundColor(value - 100, true);
                            } else if (value == 38 || value == 48) {
                                // extended color like `esc[38;5;<index>m` or `esc[38;2;<r>;<g>;<b>m`
                                int arg2or5 = getNextOptionInt(optionsIterator);
                                if (arg2or5 == 2) {
                                    // 24 bit color style like `esc[38;2;<r>;<g>;<b>m`
                                    int r = getNextOptionInt(optionsIterator);
                                    int g = getNextOptionInt(optionsIterator);
                                    int b = getNextOptionInt(optionsIterator);
                                    if (r >= 0 && r <= 255 && g >= 0 && g <= 255 && b >= 0 && b <= 255) {
                                        if (value == 38)
                                            terminalCommandProcessor.processSetForegroundColorExt(r, g, b);
                                        else
                                            terminalCommandProcessor.processSetBackgroundColorExt(r, g, b);
                                    } else {
                                        throw new IllegalArgumentException();
                                    }
                                }
                                else if (arg2or5 == 5) {
                                    // 256 color style like `esc[38;5;<index>m`
                                    int paletteIndex = getNextOptionInt(optionsIterator);
                                    if (paletteIndex >= 0 && paletteIndex <= 255) {
                                        if (value == 38)
                                            terminalCommandProcessor.processSetForegroundColorExt(paletteIndex);
                                        else
                                            terminalCommandProcessor.processSetBackgroundColorExt(paletteIndex);
                                    } else {
                                        throw new IllegalArgumentException();
                                    }
                                }
                                else {
                                    throw new IllegalArgumentException();
                                }
                            } else {
                                switch (value) {
                                    case 39:
                                        terminalCommandProcessor.processDefaultTextColor();
                                        break;
                                    case 49:
                                        terminalCommandProcessor.processDefaultBackgroundColor();
                                        break;
                                    case 0:
                                        terminalCommandProcessor.processAttributeRest();
                                        break;
                                    default:
                                        terminalCommandProcessor.processSetAttribute(value);
                                }
                            }
                        }
                    }
                    if (count == 0) {
                        terminalCommandProcessor.processAttributeRest();
                    }
                    return true;
                case 's':
                    terminalCommandProcessor.processSaveCursorPosition();
                    return true;
                case 'u':
                    terminalCommandProcessor.processRestoreCursorPosition();
                    return true;

                default:
                    if ('a' <= command && 'z' <= command) {
                        terminalCommandProcessor.processUnknownExtension(options, command);
                        return true;
                    }
                    if ('A' <= command && 'Z' <= command) {
                        terminalCommandProcessor.processUnknownExtension(options, command);
                        return true;
                    }
                    return false;
            }
        } catch (IllegalArgumentException ignore) {
        }
        return false;
    }

    /**
     *
     * @param options
     * @return true if the operating system command was processed.
     */
    private boolean processOperatingSystemCommand(ArrayList<Object> options) throws IOException {
        int command = optionInt(options, 0);
        String label = (String) options.get(1);
        // for command > 2 label could be composed (i.e. contain ';'), but we'll leave
        // it to processUnknownOperatingSystemCommand implementations to handle that
        try {
            switch (command) {
                case 0:
                    terminalCommandProcessor.processChangeIconNameAndWindowTitle(label);
                    return true;
                case 1:
                    terminalCommandProcessor.processChangeIconName(label);
                    return true;
                case 2:
                    terminalCommandProcessor.processChangeWindowTitle(label);
                    return true;

                default:
                    // not exactly unknown, but not supported through dedicated process methods:
                    terminalCommandProcessor.processUnknownOperatingSystemCommand(command, label);
                    return true;
            }
        } catch (IllegalArgumentException ignore) {
        }
        return false;
    }

    /**
     * Process character set sequence.
     * @param options set of options
     * @return true if the charcter set select command was processed.
     */
    private boolean processCharsetSelect(ArrayList<Object> options) {
        int set = optionInt(options, 0);
        char seq = ((Character) options.get(1)).charValue();
        terminalCommandProcessor.processCharsetSelect(set, seq);
        return true;
    }

    private int optionInt(ArrayList<Object> options, int index) {
        if (options.size() <= index)
            throw new IllegalArgumentException();
        Object value = options.get(index);
        if (value == null)
            throw new IllegalArgumentException();
        if (!value.getClass().equals(Integer.class))
            throw new IllegalArgumentException();
        return (Integer) value;
    }

    private int optionInt(ArrayList<Object> options, int index, int defaultValue) {
        if (options.size() > index) {
            Object value = options.get(index);
            if (value == null) {
                return defaultValue;
            }
            return (Integer) value;
        }
        return defaultValue;
    }


}
