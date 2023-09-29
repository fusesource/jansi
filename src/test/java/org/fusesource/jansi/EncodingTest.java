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
package org.fusesource.jansi;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import org.fusesource.jansi.io.AnsiOutputStream;
import org.fusesource.jansi.io.AnsiProcessor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EncodingTest {

    @Test
    public void testEncoding8859() throws UnsupportedEncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final AtomicReference<String> newLabel = new AtomicReference<>();
        PrintStream ansi = new AnsiPrintStream(
                new AnsiOutputStream(
                        baos,
                        null,
                        AnsiMode.Default,
                        new AnsiProcessor(baos) {
                            @Override
                            protected void processChangeWindowTitle(String label) {
                                newLabel.set(label);
                            }
                        },
                        AnsiType.Emulation,
                        AnsiColors.TrueColor,
                        StandardCharsets.ISO_8859_1,
                        null,
                        null,
                        false),
                true,
                "ISO-8859-1");

        ansi.print("\033]0;un bon café\007");
        ansi.flush();
        assertEquals("un bon café", newLabel.get());
    }

    @Test
    public void testEncodingUtf8() throws UnsupportedEncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final AtomicReference<String> newLabel = new AtomicReference<>();
        PrintStream ansi = new PrintStream(
                new AnsiOutputStream(
                        baos,
                        null,
                        AnsiMode.Default,
                        new AnsiProcessor(baos) {
                            @Override
                            protected void processChangeWindowTitle(String label) {
                                newLabel.set(label);
                            }
                        },
                        AnsiType.Emulation,
                        AnsiColors.TrueColor,
                        StandardCharsets.UTF_8,
                        null,
                        null,
                        false),
                true,
                "UTF-8");

        ansi.print("\033]0;ひらがな\007");
        ansi.flush();
        assertEquals("ひらがな", newLabel.get());
    }
}
