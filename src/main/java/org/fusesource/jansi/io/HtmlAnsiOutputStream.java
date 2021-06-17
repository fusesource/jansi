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
package org.fusesource.jansi.io;

import org.fusesource.jansi.AnsiColors;
import org.fusesource.jansi.AnsiConsole;
import org.fusesource.jansi.AnsiMode;
import org.fusesource.jansi.AnsiType;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * A optimized/edited {@link HtmlAnsiOutputStream} for ansi 2.x and above. <br>
 * @author <a href="https://github.com/Osiris-Team">Osiris Team</a>
 * @author <a href="http://code.dblock.org">Daniel Doubrovkine</a>
 */
public class HtmlAnsiOutputStream extends AnsiOutputStream {

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
        super(os, // With jdk8 the below could be simplified to:
                // AnsiConsole.out()::getTerminalWidth
                new WidthSupplier() {
                    @Override
                    public int getTerminalWidth() {
                        return Integer.MAX_VALUE;
                    }
                },
                AnsiMode.Default,
                null,
                AnsiType.Native,
                AnsiColors.Colors16,
                Charset.defaultCharset(),
                null,
                null,
                true);
        AnsiToHtmlProcessor ansiToHtmlProcessor = new AnsiToHtmlProcessor(AnsiConsole.out());
        ansiToHtmlProcessor.setHtmlAnsiOutputStream(this);
        this.setAp(ansiToHtmlProcessor);
        this.setProcessor(ansiToHtmlProcessor);
    }

    private final List<String> closingAttributes = new ArrayList<>();

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

    private static class AnsiToHtmlProcessor extends AnsiProcessor {
        private boolean concealOn = false;
        private HtmlAnsiOutputStream haos;

        AnsiToHtmlProcessor(OutputStream os) {
            super(os);
        }

        public HtmlAnsiOutputStream getHtmlAnsiOutputStream() {
            return haos;
        }

        public void setHtmlAnsiOutputStream(HtmlAnsiOutputStream haos) {
            this.haos = haos;
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
        protected void processAttributeReset() throws IOException {
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