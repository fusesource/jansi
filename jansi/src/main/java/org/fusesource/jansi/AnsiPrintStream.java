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

import java.io.IOException;
import java.io.PrintStream; // expected diff with AnsiOutputStream.java
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A ANSI print stream extracts ANSI escape codes written to 
 * a print stream and calls corresponding <code>AnsiProcessor.process*</code> methods.
 *
 * <p>For more information about ANSI escape codes, see
 * <a href="http://en.wikipedia.org/wiki/ANSI_escape_code">Wikipedia article</a>
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 * @author Joris Kuipers
 * @since 1.7
 * @see AnsiProcessor
 * @see AnsiOutputStream
 */
public class AnsiPrintStream extends FilterPrintStream { // expected diff with AnsiOutputStream.java

    private final AnsiProcessor ap;
    
    public static final String RESET_CODE = "\033[0m"; // expected diff with AnsiOutputStream.java

    public AnsiPrintStream(PrintStream ps, AnsiProcessor ap) { // expected diff with AnsiOutputStream.java
        super(ps); // expected diff with AnsiOutputStream.java
        this.ap = ap;
    }

    public AnsiPrintStream(PrintStream ps) { // expected diff with AnsiOutputStream.java
        this(ps, new AnsiProcessor(ps)); // expected diff with AnsiOutputStream.java
    }

    private final static int MAX_ESCAPE_SEQUENCE_LENGTH = 100;
    private final byte[] buffer = new byte[MAX_ESCAPE_SEQUENCE_LENGTH];
    private int pos = 0;
    private int startOfValue;
    private final ArrayList<Object> options = new ArrayList<Object>();

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

    int state = LOOKING_FOR_FIRST_ESC_CHAR;

    private static final int FIRST_ESC_CHAR = 27;
    private static final int SECOND_ESC_CHAR = '[';
    private static final int SECOND_OSC_CHAR = ']';
    private static final int BEL = 7;
    private static final int SECOND_ST_CHAR = '\\';
    private static final int SECOND_CHARSET0_CHAR = '(';
    private static final int SECOND_CHARSET1_CHAR = ')';

    @Override
    protected synchronized boolean filter(int data) { // expected diff with AnsiOutputStream.java
        switch (state) {
            case LOOKING_FOR_FIRST_ESC_CHAR:
                if (data == FIRST_ESC_CHAR) {
                    buffer[pos++] = (byte) data;
                    state = LOOKING_FOR_SECOND_ESC_CHAR;
                    return false; // expected diff with AnsiOutputStream.java
                }
                return true; // expected diff with AnsiOutputStream.java

            case LOOKING_FOR_SECOND_ESC_CHAR:
                buffer[pos++] = (byte) data;
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
                buffer[pos++] = (byte) data;
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
                buffer[pos++] = (byte) data;
                if (!('0' <= data && data <= '9')) {
                    String strValue = new String(buffer, startOfValue, (pos - 1) - startOfValue, Charset.defaultCharset());
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
                buffer[pos++] = (byte) data;
                if ('"' != data) {
                    String value = new String(buffer, startOfValue, (pos - 1) - startOfValue, Charset.defaultCharset());
                    options.add(value);
                    if (data == ';') {
                        state = LOOKING_FOR_NEXT_ARG;
                    } else {
                        reset(processEscapeCommand(options, data));
                    }
                }
                break;

            case LOOKING_FOR_OSC_COMMAND:
                buffer[pos++] = (byte) data;
                if ('0' <= data && data <= '9') {
                    startOfValue = pos - 1;
                    state = LOOKING_FOR_OSC_COMMAND_END;
                } else {
                    reset(false);
                }
                break;

            case LOOKING_FOR_OSC_COMMAND_END:
                buffer[pos++] = (byte) data;
                if (';' == data) {
                    String strValue = new String(buffer, startOfValue, (pos - 1) - startOfValue, Charset.defaultCharset());
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
                buffer[pos++] = (byte) data;
                if (BEL == data) {
                    String value = new String(buffer, startOfValue, (pos - 1) - startOfValue, Charset.defaultCharset());
                    options.add(value);
                    reset(processOperatingSystemCommand(options));
                } else if (FIRST_ESC_CHAR == data) {
                    state = LOOKING_FOR_ST;
                } else {
                    // just keep looking while adding text
                }
                break;

            case LOOKING_FOR_ST:
                buffer[pos++] = (byte) data;
                if (SECOND_ST_CHAR == data) {
                    String value = new String(buffer, startOfValue, (pos - 2) - startOfValue, Charset.defaultCharset());
                    options.add(value);
                    reset(processOperatingSystemCommand(options));
                } else {
                    state = LOOKING_FOR_OSC_PARAM;
                }
                break;

            case LOOKING_FOR_CHARSET:
                options.add(Character.valueOf((char) data));
                reset(processCharsetSelect(options));
                break;
        }

        // Is it just too long?
        if (pos >= buffer.length) {
            reset(false);
        }
        return false; // expected diff with AnsiOutputStream.java
    }

    /**
     * Resets all state to continue with regular parsing
     * @param skipBuffer if current buffer should be skipped or written to out
     */
    private void reset(boolean skipBuffer) { // expected diff with AnsiOutputStream.java
        if (!skipBuffer) {
            ps.write(buffer, 0, pos); // expected diff with AnsiOutputStream.java
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
    private boolean processEscapeCommand(ArrayList<Object> options, int command) { // expected diff with AnsiOutputStream.java
        try {
            switch (command) {
                case 'A':
                    ap.processCursorUp(optionInt(options, 0, 1));
                    return true;
                case 'B':
                    ap.processCursorDown(optionInt(options, 0, 1));
                    return true;
                case 'C':
                    ap.processCursorRight(optionInt(options, 0, 1));
                    return true;
                case 'D':
                    ap.processCursorLeft(optionInt(options, 0, 1));
                    return true;
                case 'E':
                    ap.processCursorDownLine(optionInt(options, 0, 1));
                    return true;
                case 'F':
                    ap.processCursorUpLine(optionInt(options, 0, 1));
                    return true;
                case 'G':
                    ap.processCursorToColumn(optionInt(options, 0));
                    return true;
                case 'H':
                case 'f':
                    ap.processCursorTo(optionInt(options, 0, 1), optionInt(options, 1, 1));
                    return true;
                case 'J':
                    ap.processEraseScreen(optionInt(options, 0, 0));
                    return true;
                case 'K':
                    ap.processEraseLine(optionInt(options, 0, 0));
                    return true;
                case 'L':
                    ap.processInsertLine(optionInt(options, 0, 1));
                    return true;
                case 'M':
                    ap.processDeleteLine(optionInt(options, 0, 1));
                    return true;
                case 'S':
                    ap.processScrollUp(optionInt(options, 0, 1));
                    return true;
                case 'T':
                    ap.processScrollDown(optionInt(options, 0, 1));
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
                                ap.processSetForegroundColor(value - 30);
                            } else if (40 <= value && value <= 47) {
                                ap.processSetBackgroundColor(value - 40);
                            } else if (90 <= value && value <= 97) {
                                ap.processSetForegroundColor(value - 90, true);
                            } else if (100 <= value && value <= 107) {
                                ap.processSetBackgroundColor(value - 100, true);
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
                                            ap.processSetForegroundColorExt(r, g, b);
                                        else
                                            ap.processSetBackgroundColorExt(r, g, b);
                                    } else {
                                        throw new IllegalArgumentException();
                                    }
                                }
                                else if (arg2or5 == 5) {
                                    // 256 color style like `esc[38;5;<index>m`
                                    int paletteIndex = getNextOptionInt(optionsIterator);
                                    if (paletteIndex >= 0 && paletteIndex <= 255) {
                                        if (value == 38)
                                            ap.processSetForegroundColorExt(paletteIndex);
                                        else
                                            ap.processSetBackgroundColorExt(paletteIndex);
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
                                        ap.processDefaultTextColor();
                                        break;
                                    case 49:
                                        ap.processDefaultBackgroundColor();
                                        break;
                                    case 0:
                                        ap.processAttributeRest();
                                        break;
                                    default:
                                        ap.processSetAttribute(value);
                                }
                            }
                        }
                    }
                    if (count == 0) {
                        ap.processAttributeRest();
                    }
                    return true;
                case 's':
                    ap.processSaveCursorPosition();
                    return true;
                case 'u':
                    ap.processRestoreCursorPosition();
                    return true;

                default:
                    if ('a' <= command && 'z' <= command) {
                        ap.processUnknownExtension(options, command);
                        return true;
                    }
                    if ('A' <= command && 'Z' <= command) {
                        ap.processUnknownExtension(options, command);
                        return true;
                    }
                    return false;
            }
        } catch (IllegalArgumentException ignore) {
        } catch (IOException ioe) { // expected diff with AnsiOutputStream.java
            setError(); // expected diff with AnsiOutputStream.java
        }
        return false;
    }

    /**
     *
     * @param options
     * @return true if the operating system command was processed.
     */
    private boolean processOperatingSystemCommand(ArrayList<Object> options) { // expected diff with AnsiOutputStream.java
        int command = optionInt(options, 0);
        String label = (String) options.get(1);
        // for command > 2 label could be composed (i.e. contain ';'), but we'll leave
        // it to processUnknownOperatingSystemCommand implementations to handle that
        try {
            switch (command) {
                case 0:
                    ap.processChangeIconNameAndWindowTitle(label);
                    return true;
                case 1:
                    ap.processChangeIconName(label);
                    return true;
                case 2:
                    ap.processChangeWindowTitle(label);
                    return true;

                default:
                    // not exactly unknown, but not supported through dedicated process methods:
                    ap.processUnknownOperatingSystemCommand(command, label);
                    return true;
            }
        } catch (IllegalArgumentException ignore) {
        }
        return false;
    }

    /**
     * Process character set sequence.
     * @param options options
     * @return true if the charcter set select command was processed.
     */
    private boolean processCharsetSelect(ArrayList<Object> options) {
        int set = optionInt(options, 0);
        char seq = ((Character) options.get(1)).charValue();
        ap.processCharsetSelect(set, seq);
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

    @Override
    public void close() { // expected diff with AnsiOutputStream.java
        print(RESET_CODE); // expected diff with AnsiOutputStream.java
        flush();
        super.close();
    }

}
