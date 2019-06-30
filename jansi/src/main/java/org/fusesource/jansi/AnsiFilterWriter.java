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

/**
 * A ANSI print stream extracts ANSI escape codes written to 
 * an output stream and calls corresponding <code>AnsiProcessor.process*</code> methods.
 *
 * <p>For more information about ANSI escape codes, see
 * <a href="http://en.wikipedia.org/wiki/ANSI_escape_code">Wikipedia article</a>
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 * @author Joris Kuipers
 * @since 1.0
 * @see AnsiProcessor
 * @see AnsiPrintStream
 */
public class AnsiFilterWriter extends FilterWriter { // expected diff with AnsiPrintStream.java

    protected final AnsiProcessor ap;

    public static final char[] RESET_CODE = "\033[0m".toCharArray(); // expected diff with AnsiPrintStream.java

    private static final int TMP_WRITE_BUFFER_LENGTH = 400;


    public AnsiFilterWriter(Writer os, AnsiProcessor ap) { // expected diff with AnsiPrintStream.java
        super(os); // expected diff with AnsiPrintStream.java
        this.ap = ap;
    }

    public AnsiFilterWriter(Writer os) { // expected diff with AnsiPrintStream.java
        this(os, new AnsiProcessor(os)); // expected diff with AnsiPrintStream.java
    }

    private final static int MAX_ESCAPE_SEQUENCE_LENGTH = 100;
    private final char[] buffer = new char[MAX_ESCAPE_SEQUENCE_LENGTH];
    private int pos = 0;
    private int startOfValue;
    private final ArrayList<Object> options = new ArrayList<Object>();

    private final char[] strToCharBuffer = new char[TMP_WRITE_BUFFER_LENGTH];

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


    // override java.io.Writer methods to force filtering char by char
    // ------------------------------------------------------------------------

    /**
     * Override java.io.Writer, so interpret data as a char ... not as a byte from OutputStream/PrintStream
     */
    @Override
    public void write(int c) throws IOException {
        char ch = (char) c;
        if (filterChar(ch)) {
            out.write(ch);
        }
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        final int max = off + len;
        for (int i = off; i < max; i++) {
            char ch = cbuf[i];
            if (filterChar(ch)) {
                out.write(ch);
            }
        }
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        char[] buf = (len < strToCharBuffer.length)? strToCharBuffer : new char[len];
        str.getChars(off, off + len, buf, 0);
        write(buf, 0, len);
    }

    // Internal
    // ------------------------------------------------------------------------

    protected boolean filterChar(char data) throws IOException {
        switch (state) {
            case LOOKING_FOR_FIRST_ESC_CHAR:
                if (data == FIRST_ESC_CHAR) {
                    buffer[pos++] = data;
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
                    reset(ap.processEscapeCommand(options, data)); // expected diff with AnsiOutputStream.java
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
                        reset(ap.processEscapeCommand(options, data)); // expected diff with AnsiOutputStream.java
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
                        reset(ap.processEscapeCommand(options, data)); // expected diff with AnsiOutputStream.java
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
                    reset(ap.processOperatingSystemCommand(options));
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
                    reset(ap.processOperatingSystemCommand(options));
                } else {
                    state = LOOKING_FOR_OSC_PARAM;
                }
                break;

            case LOOKING_FOR_CHARSET:
                options.add(Character.valueOf((char) data));
                reset(ap.processCharsetSelect(options));
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
            out.write(buffer, 0, pos); // expected diff with AnsiPrintStream.java
        }
        pos = 0;
        startOfValue = 0;
        options.clear();
        state = LOOKING_FOR_FIRST_ESC_CHAR;
    }

    @Override
    public void close() throws IOException { // expected diff with AnsiPrintStream.java
        write(RESET_CODE); // expected diff with AnsiPrintStream.java
        flush();
        super.close();
    }

}
