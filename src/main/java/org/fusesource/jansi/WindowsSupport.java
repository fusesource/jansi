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

import org.fusesource.jansi.internal.Kernel32;

/**
 * @deprecated Use org.fusesource.jansi.internal.Kernel32 if needed
 */
@Deprecated
public class WindowsSupport {

    @Deprecated
    public static String getLastErrorMessage() {
        // Get the raw integer error code first
        int errorCode = Kernel32.GetLastError();
        return getErrorMessage(errorCode);
    }

    @Deprecated
    public static String getErrorMessage(int errorCode) {
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize]; // Buffer for UTF-16LE characters

        // Ask Windows to format the integer error code into a human-readable string
        int charsWritten = Kernel32.FormatMessageW(
                Kernel32.FORMAT_MESSAGE_FROM_SYSTEM,
                0,
                errorCode,
                0,
                buffer,
                bufferSize,
                null
        );

        if (charsWritten == 0) {
            return "Unknown error code: " + errorCode;
        }

        // Convert the native byte array back into a standard Java String
        // Multiplied by 2 because UTF-16LE uses 2 bytes per character
        return new String(buffer, 0, charsWritten * 2, java.nio.charset.StandardCharsets.UTF_16LE).trim();
    }
}