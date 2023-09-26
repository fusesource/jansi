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
package org.fusesource.jansi.ffm;

import org.fusesource.jansi.AnsiConsoleSupport;

public class AnsiConsoleSupportFfm implements AnsiConsoleSupport {
    @Override
    public String getProviderName() {
        return "ffm";
    }

    @Override
    public CLibrary getCLibrary() {
        throw new UnsupportedOperationException("ffm provider is only supported on java >= 21");
    }

    @Override
    public Kernel32 getKernel32() {
        throw new UnsupportedOperationException("ffm provider is only supported on java >= 21");
    }
}
