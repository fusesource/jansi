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
package org.fusesource.jansi.internal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Kernel32Test {

    @Test
    @EnabledOnOs(OS.WINDOWS)
    public void testErrorMessage() {
        int errorCode = 500;
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int charsWritten = Kernel32.FormatMessageW(
                Kernel32.FORMAT_MESSAGE_FROM_SYSTEM,
                0,
                errorCode,
                0,
                buffer,
                bufferSize,
                null
        );

        assertTrue(charsWritten > 0, "FormatMessageW should return characters for a valid error code.");

        String msg = new String(buffer, 0, charsWritten * 2, java.nio.charset.StandardCharsets.UTF_16LE).trim();

        assertEquals("User profile cannot be loaded.", msg);
    }
}