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
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.fusesource.jansi.impl.TerminalCommandProcessor;
import org.fusesource.jansi.impl.TerminalType;

/**
 * @author <a href="http://code.dblock.org">Daniel Doubrovkine</a>
 */
public class HtmlAnsiOutputStream extends AnsiOutputStream {

    private boolean concealOn = false;

    @Override
    public void close() throws IOException {
        closeAttributes();
        super.close();
    }

    private static final String[] ANSI_COLOR_MAP = {"black", "red",
            "green", "yellow", "blue", "magenta", "cyan", "white",};

    private static final byte[] BYTES_QUOT = "&quot;".getBytes();
    private static final byte[] BYTES_AMP = "&amp;".getBytes();
    private static final byte[] BYTES_LT = "&lt;".getBytes();
    private static final byte[] BYTES_GT = "&gt;".getBytes();

    public HtmlAnsiOutputStream(OutputStream os) {
        super(os, HTML_TERM); // can not create inner class from constructor, set outerOs below
        ((InnerTerminalCommandProcessor) super.terminalCommandProcessor).outer = this;
    }

    private final List<String> closingAttributes = new ArrayList<String>();

    private void write(String s) throws IOException {
        super.out.write(s.getBytes());
    }

    private void writeAttribute(String s) throws IOException {
        write("<" + s + ">");
        closingAttributes.add(0, s.split(" ", 2)[0]);
    }

    private void closeAttributes() throws IOException {
        for (String attr : closingAttributes) {
            write("</" + attr + ">");
        }
        closingAttributes.clear();
    }

    public void write(int data) throws IOException {
        switch (data) {
            case 34: // "
                out.write(BYTES_QUOT);
                break;
            case 38: // &
                out.write(BYTES_AMP);
                break;
            case 60: // <
                out.write(BYTES_LT);
                break;
            case 62: // >
                out.write(BYTES_GT);
                break;
            default:
                super.write(data);
        }
    }

    public void writeLine(byte[] buf, int offset, int len) throws IOException {
        write(buf, offset, len);
        closeAttributes();
    }

    private static final TerminalType HTML_TERM = new TerminalType() {
        @Override
        public TerminalCommandProcessor create(Writer output) throws IOException {
            return new InnerTerminalCommandProcessor(output);
        }
    };

    /**
     * inner callback TerminalCommandProcessor for handling commands as html
     */
    private static class InnerTerminalCommandProcessor extends TerminalCommandProcessor {
        private HtmlAnsiOutputStream outer;

        private InnerTerminalCommandProcessor(Writer out) {
            super(out);
        }

        @Override
        public void processSetAttribute(int attribute) throws IOException {
            switch (attribute) {
                case ATTRIBUTE_CONCEAL_ON:
                    outer.write("\u001B[8m");
                    outer.concealOn = true;
                    break;
                case ATTRIBUTE_INTENSITY_BOLD:
                    outer.writeAttribute("b");
                    break;
                case ATTRIBUTE_INTENSITY_NORMAL:
                    outer.closeAttributes();
                    break;
                case ATTRIBUTE_UNDERLINE:
                    outer.writeAttribute("u");
                    break;
                case ATTRIBUTE_UNDERLINE_OFF:
                    outer.closeAttributes();
                    break;
                case ATTRIBUTE_NEGATIVE_ON:
                    break;
                case ATTRIBUTE_NEGATIVE_OFF:
                    break;
                default:
                    break;
            }
        }
    
        @Override
        public void processAttributeRest() throws IOException {
            if (outer.concealOn) {
                outer.write("\u001B[0m");
                outer.concealOn = false;
            }
            outer.closeAttributes();
        }
    
        @Override
        public void processSetForegroundColor(int color, boolean bright) throws IOException {
            outer.writeAttribute("span style=\"color: " + ANSI_COLOR_MAP[color] + ";\"");
        }
    
        @Override
        public void processSetBackgroundColor(int color, boolean bright) throws IOException {
            outer.writeAttribute("span style=\"background-color: " + ANSI_COLOR_MAP[color] + ";\"");
        }
    }

}
