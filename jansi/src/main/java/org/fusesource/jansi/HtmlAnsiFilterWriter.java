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
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="http://code.dblock.org">Daniel Doubrovkine</a>
 */
public class HtmlAnsiFilterWriter extends AnsiFilterWriter {

    public void close() throws IOException {
        closeAttributes();
        super.close();
    }

    private static final String[] ANSI_COLOR_MAP = {"black", "red",
            "green", "yellow", "blue", "magenta", "cyan", "white",};

    private static final char[] BYTES_QUOT = "&quot;".toCharArray();
    private static final char[] BYTES_AMP = "&amp;".toCharArray();
    private static final char[] BYTES_LT = "&lt;".toCharArray();
    private static final char[] BYTES_GT = "&gt;".toCharArray();

    public HtmlAnsiFilterWriter(Writer out) {
        super(out, new Processor(out));
        ((Processor) ap).haos = this;
    }

    private final List<String> closingAttributes = new ArrayList<String>();

    public void writeLine(char[] buf, int offset, int len) throws IOException {
        write(buf, offset, len);
        closeAttributes();
    }

    // override java.io.Writer methods to filter and html-escape chars one by one
    // ------------------------------------------------------------------------

    @Override
    public void write(int data) throws IOException {
        if (filterChar((char) data)) {
            writeEscapeChar((char) data);
        }
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        final int max = off + len;
        for (int i = off; i < max; i++) {
            char ch = cbuf[i];
            if (filterChar(ch)) {
                writeEscapeChar(ch);
            }
        }
    }

    // Internal
    // ------------------------------------------------------------------------

    private void writeAttribute(String s) throws IOException {
        doWriteHtml("<" + s + ">");
        closingAttributes.add(0, s.split(" ", 2)[0]);
    }

    private void closeAttributes() throws IOException {
        for (String attr : closingAttributes) {
            doWriteHtml("</" + attr + ">");
        }
        closingAttributes.clear();
    }

    protected void doWriteHtml(String htmlText) throws IOException {
        super.out.write(htmlText);
    }

    protected void doWriteHtml(char[] htmlText) throws IOException {
        super.out.write(htmlText);
    }

    private void writeEscapeChar(int data) throws IOException {
        switch (data) {
            case 34: // "
                doWriteHtml(BYTES_QUOT);
                break;
            case 38: // &
                doWriteHtml(BYTES_AMP);
                break;
            case 60: // <
                doWriteHtml(BYTES_LT);
                break;
            case 62: // >
                doWriteHtml(BYTES_GT);
                break;
            default:
                super.out.write(data);
        }
    }

    private static class Processor extends AnsiProcessor {
        private boolean concealOn = false;

        HtmlAnsiFilterWriter haos;

        Processor(Writer os) {
            super(os);
        }

        @Override
        protected void processSetAttribute(int attribute) throws IOException {
            switch (attribute) {
                case ATTRIBUTE_CONCEAL_ON:
                    haos.write("\u001B[8m");
                    concealOn = true;
                    break;
                case ATTRIBUTE_INTENSITY_BOLD:
                    haos.writeAttribute("b");
                    break;
                case ATTRIBUTE_INTENSITY_NORMAL:
                    haos.closeAttributes();
                    break;
                case ATTRIBUTE_UNDERLINE:
                    haos.writeAttribute("u");
                    break;
                case ATTRIBUTE_UNDERLINE_OFF:
                    haos.closeAttributes();
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
        protected void processAttributeRest() throws IOException {
            if (concealOn) {
                haos.write("\u001B[0m");
                concealOn = false;
            }
            haos.closeAttributes();
        }

        @Override
        protected void processSetForegroundColor(int color, boolean bright) throws IOException {
            haos.writeAttribute("span style=\"color: " + ANSI_COLOR_MAP[color] + ";\"");
        }

        @Override
        protected void processSetBackgroundColor(int color, boolean bright) throws IOException {
            haos.writeAttribute("span style=\"background-color: " + ANSI_COLOR_MAP[color] + ";\"");
        }

    }
}
