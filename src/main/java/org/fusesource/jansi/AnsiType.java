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

/**
 * Processor type.
 *
 * @since 2.1
 */
public enum AnsiType {

    Native("Supports ansi sequences natively"),
    Unsupported("Ansi sequences are stripped out"),
    VirtualTerminal("Supported through windows virtual terminal"),
    Emulation("Emulated through using windows API console commands"),
    Redirected("The stream is redirected to a file or a pipe");

    private final String description;

    AnsiType(String description) {
        this.description = description;
    }

    String getDescription() {
        return description;
    }
}
