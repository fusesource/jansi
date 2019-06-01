/*
 * Copyright (C) 2009-2019 the original author(s).
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
package org.fusesource.jansi.impl;

import java.io.UnsupportedEncodingException;

import static org.fusesource.jansi.internal.Kernel32.FORMAT_MESSAGE_FROM_SYSTEM;
import static org.fusesource.jansi.internal.Kernel32.FormatMessageW;
import static org.fusesource.jansi.internal.Kernel32.GetLastError;

public class WindowsSupport {

    public static String getLastErrorMessage() {
        int errorCode = GetLastError();
        return getErrorMessage(errorCode);
    }

    public static String getErrorMessage(int errorCode) {
        int bufferSize = 160;
        byte data[] = new byte[bufferSize];
        FormatMessageW(FORMAT_MESSAGE_FROM_SYSTEM, 0, errorCode, 0, data, bufferSize, null);
        try {
            return new String(data, "UTF-16LE").trim();
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

}
