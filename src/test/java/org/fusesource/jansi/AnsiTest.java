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

import org.fusesource.jansi.Ansi.Color;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the {@link Ansi} class.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class AnsiTest {
    @Test
    public void testSetEnabled() throws Exception {
        Ansi.setEnabled(false);
        new Thread() {
            @Override
            public void run() {
                assertEquals(false, Ansi.isEnabled());
            }
        }.run();

        Ansi.setEnabled(true);
        new Thread() {
            @Override
            public void run() {
                assertEquals(true, Ansi.isEnabled());
            }
        }.run();
    }

    @Test
    public void testClone() throws CloneNotSupportedException {
        Ansi ansi = Ansi.ansi().a("Some text").bg(Color.BLACK).fg(Color.WHITE);
        Ansi clone = new Ansi(ansi);

        assertEquals(ansi.a("test").reset().toString(), clone.a("test").reset().toString());
    }

    @Test
    public void testApply() {
        assertEquals("test", Ansi.ansi().apply(new Ansi.Consumer() {
            public void apply(Ansi ansi) {
                ansi.a("test");
            }
        }).toString());
    }

    @ParameterizedTest
    @CsvSource({
        "-1,-1,ESC[1;1H", "-1,0,ESC[1;1H", "-1,1,ESC[1;1H", "-1,2,ESC[1;2H",
        "0,-1,ESC[1;1H", "0,0,ESC[1;1H", "0,1,ESC[1;1H", "0,2,ESC[1;2H",
        "1,-1,ESC[1;1H", "1,0,ESC[1;1H", "1,1,ESC[1;1H", "1,2,ESC[1;2H",
        "2,-1,ESC[2;1H", "2,0,ESC[2;1H", "2,1,ESC[2;1H", "2,2,ESC[2;2H"
    })
    public void testCursor(int x, int y, String expected) {
        assertAnsi(expected, new Ansi().cursor(x, y));
    }

    @ParameterizedTest
    @CsvSource({"-1,ESC[1G", "0,ESC[1G", "1,ESC[1G", "2,ESC[2G"})
    public void testCursorToColumn(int x, String expected) {
        assertAnsi(expected, new Ansi().cursorToColumn(x));
    }

    @ParameterizedTest
    @CsvSource({"-2,ESC[2B", "-1,ESC[1B", "0,''", "1,ESC[1A", "2,ESC[2A"})
    public void testCursorUp(int y, String expected) {
        assertAnsi(expected, new Ansi().cursorUp(y));
    }

    @ParameterizedTest
    @CsvSource({"-2,ESC[2A", "-1,ESC[1A", "0,''", "1,ESC[1B", "2,ESC[2B"})
    public void testCursorDown(int y, String expected) {
        assertAnsi(expected, new Ansi().cursorDown(y));
    }

    @ParameterizedTest
    @CsvSource({"-2,ESC[2D", "-1,ESC[1D", "0,''", "1,ESC[1C", "2,ESC[2C"})
    public void testCursorRight(int x, String expected) {
        assertAnsi(expected, new Ansi().cursorRight(x));
    }

    @ParameterizedTest
    @CsvSource({"-2,ESC[2C", "-1,ESC[1C", "0,''", "1,ESC[1D", "2,ESC[2D"})
    public void testCursorLeft(int x, String expected) {
        assertAnsi(expected, new Ansi().cursorLeft(x));
    }

    @ParameterizedTest
    @CsvSource({
        "-2,-2,ESC[2DESC[2A", "-2,-1,ESC[2DESC[1A", "-2,0,ESC[2D", "-2,1,ESC[2DESC[1B", "-2,2,ESC[2DESC[2B",
        "-1,-2,ESC[1DESC[2A", "-1,-1,ESC[1DESC[1A", "-1,0,ESC[1D", "-1,1,ESC[1DESC[1B", "-1,2,ESC[1DESC[2B",
        "0,-2,ESC[2A", "0,-1,ESC[1A", "0,0,''", "0,1,ESC[1B", "0,2,ESC[2B",
        "1,-2,ESC[1CESC[2A", "1,-1,ESC[1CESC[1A", "1,0,ESC[1C", "1,1,ESC[1CESC[1B", "1,2,ESC[1CESC[2B",
        "2,-2,ESC[2CESC[2A", "2,-1,ESC[2CESC[1A", "2,0,ESC[2C", "2,1,ESC[2CESC[1B", "2,2,ESC[2CESC[2B"
    })
    public void testCursorMove(int x, int y, String expected) {
        assertAnsi(expected, new Ansi().cursorMove(x, y));
    }

    @Test
    public void testCursorDownLine() {
        assertAnsi("ESC[E", new Ansi().cursorDownLine());
    }

    @ParameterizedTest
    @CsvSource({"-2,ESC[2F", "-1,ESC[1F", "0,ESC[0E", "1,ESC[1E", "2,ESC[2E"})
    public void testCursorDownLine(int n, String expected) {
        assertAnsi(expected, new Ansi().cursorDownLine(n));
    }

    @Test
    public void testCursorUpLine() {
        assertAnsi("ESC[F", new Ansi().cursorUpLine());
    }

    @ParameterizedTest
    @CsvSource({"-2,ESC[2E", "-1,ESC[1E", "0,ESC[0F", "1,ESC[1F", "2,ESC[2F"})
    public void testCursorUpLine(int n, String expected) {
        assertAnsi(expected, new Ansi().cursorUpLine(n));
    }

    @Test
    public void testColorDisabled() {
        Ansi.setEnabled(false);
        try {
            assertEquals("test", Ansi.ansi().fg(32).a("t").fgRgb(0).a("e").bg(24).a("s").bgRgb(100).a("t").toString());
        } finally {
            Ansi.setEnabled(true);
        }
    }

    private static void assertAnsi(String expected, Ansi actual) {
        assertEquals(expected.replace("ESC", "\033"), actual.toString());
    }
}