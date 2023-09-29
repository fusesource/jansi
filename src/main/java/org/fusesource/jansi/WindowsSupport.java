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

import org.fusesource.jansi.internal.AnsiConsoleSupportHolder;

public class WindowsSupport {

    public static String getLastErrorMessage() {
        int errorCode = AnsiConsoleSupportHolder.getKernel32().getLastError();
        return getErrorMessage(errorCode);
    }

    public static String getErrorMessage(int errorCode) {
        return AnsiConsoleSupportHolder.getKernel32().getErrorMessage(errorCode);
    }
}
