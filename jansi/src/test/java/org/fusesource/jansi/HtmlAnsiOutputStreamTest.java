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

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.IOException;

import org.junit.Test;

/**
 * @author <a href="http://code.dblock.org">Daniel Doubrovkine</a>
 */
public class HtmlAnsiOutputStreamTest {

    @Test
    public void testNoMarkup() throws IOException {
        assertEquals("line", colorize("line"));
    }

    @Test
    public void testClear() throws IOException {
        assertEquals("", colorize("[0m[K"));
        assertEquals("hello world", colorize("[0mhello world"));
    }

    @Test
    public void testBold() throws IOException {
        assertEquals("<b>hello world</b>", colorize("[1mhello world"));
    }

    @Test
    public void testGreen() throws IOException {
        assertEquals("<span style=\"color: green;\">hello world</span>",
                colorize("[32mhello world"));
    }

    @Test
    public void testGreenOnWhite() throws IOException {
        assertEquals("<span style=\"background-color: white;\"><span style=\"color: green;\">hello world</span></span>",
                colorize("[47;32mhello world"));
    }

    @Test
    public void testEscapeHtml() throws IOException {
        assertEquals("&quot;", colorize("\""));
        assertEquals("&amp;", colorize("&"));
        assertEquals("&lt;", colorize("<"));
        assertEquals("&gt;", colorize(">"));
        assertEquals("&quot;&amp;&lt;&gt;", colorize("\"&<>"));
    }

    @Test
    public void testResetOnOpen() throws IOException {
        assertEquals("<span style=\"color: red;\">red</span>",
                colorize("[0;31;49mred[0m"));
    }

    @Test
    public void testUTF8Character() throws IOException {
        assertEquals("<b>\u3053\u3093\u306b\u3061\u306f</b>",
                colorize("[1m\u3053\u3093\u306b\u3061\u306f"));
    }

    @Test
    public void testResetCharacterSet() throws IOException {
        assertEquals(colorize("(\033(0)"), "()");
        assertEquals(colorize("(\033)0)"), "()");
    }

    private String colorize(String text) throws IOException {
        CharArrayWriter os = new CharArrayWriter();
        HtmlAnsiOutputWriter hos = new HtmlAnsiOutputWriter(os);
        hos.write(text);
        hos.close();
        return os.toString();
    }
}
