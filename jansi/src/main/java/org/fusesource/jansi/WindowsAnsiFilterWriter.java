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

import static org.fusesource.jansi.internal.Kernel32.BACKGROUND_BLUE;
import static org.fusesource.jansi.internal.Kernel32.BACKGROUND_GREEN;
import static org.fusesource.jansi.internal.Kernel32.BACKGROUND_RED;
import static org.fusesource.jansi.internal.Kernel32.FOREGROUND_BLUE;
import static org.fusesource.jansi.internal.Kernel32.FOREGROUND_GREEN;
import static org.fusesource.jansi.internal.Kernel32.FOREGROUND_RED;
import static org.fusesource.jansi.internal.Kernel32.GetStdHandle;
import static org.fusesource.jansi.internal.Kernel32.STD_ERROR_HANDLE;
import static org.fusesource.jansi.internal.Kernel32.STD_OUTPUT_HANDLE;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.fusesource.jansi.internal.Kernel32.CONSOLE_SCREEN_BUFFER_INFO;

/**
 * A Windows ANSI escape processor, that uses JNA to access native platform
 * API's to change the console attributes.
 *
 * @since 1.0
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 * @author Joris Kuipers
 * @see WindowsAnsiPrintStream
 * @see WindowsAnsiProcessor
 * @deprecated use {@link WindowsAnsiPrintStream}, which does not suffer from encoding issues
 */
public final class WindowsAnsiFilterWriter extends AnsiFilterWriter { // expected diff with WindowsAnsiPrintStream.java

    public WindowsAnsiFilterWriter(Writer out, boolean stdout) throws IOException { // expected diff with WindowsAnsiPrintStream.java
        super(out, new WindowsAnsiProcessor(out, stdout)); // expected diff with WindowsAnsiPrintStream.java
    }

    public WindowsAnsiFilterWriter(Writer out) throws IOException { // expected diff with WindowsAnsiPrintStream.java
        this(out, true); // expected diff with WindowsAnsiPrintStream.java
    }
}
