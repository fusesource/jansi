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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.fusesource.jansi.AnsiColors;
import org.fusesource.jansi.AnsiMode;
import org.fusesource.jansi.AnsiType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AnsiOutputStreamTest {

    @Test
    void canHandleSgrsWithMultipleOptions() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final AnsiOutputStream ansiOutput = new AnsiOutputStream(
                baos,
                null,
                AnsiMode.Strip,
                null,
                AnsiType.Emulation,
                AnsiColors.TrueColor,
                Charset.forName("UTF-8"),
                null,
                null,
                false);
        ansiOutput.write(
                ("\u001B[33mbanana_1  |\u001B[0m 19:59:14.353\u001B[0;38m [debug] A message\u001B[0m\n").getBytes());
        assertEquals("banana_1  | 19:59:14.353 [debug] A message\n", baos.toString());
    }
}
