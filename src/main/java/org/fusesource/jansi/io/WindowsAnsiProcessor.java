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

import java.io.IOException;
import java.io.OutputStream;

/**
 * A Windows ANSI escape processor, that uses JNA to access native platform
 * API's to change the console attributes (see
 * <a href="http://fusesource.github.io/jansi/documentation/native-api/index.html?org/fusesource/jansi/internal/Kernel32.html">Jansi native Kernel32</a>).
 * <p>The native library used is named <code>jansi</code> and is loaded using <a href="http://fusesource.github.io/hawtjni/">HawtJNI</a> Runtime
 * <a href="http://fusesource.github.io/hawtjni/documentation/api/index.html?org/fusesource/hawtjni/runtime/Library.html"><code>Library</code></a>
 *
 * @since 1.19
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 * @author Joris Kuipers
 */
public final class WindowsAnsiProcessor extends org.fusesource.jansi.internal.WindowsAnsiProcessor {

    public WindowsAnsiProcessor(OutputStream ps, long console) throws IOException {
        super(ps, console);
    }

    public WindowsAnsiProcessor(OutputStream ps, boolean stdout) throws IOException {
        super(ps, stdout);
    }

    public WindowsAnsiProcessor(OutputStream ps) throws IOException {
        super(ps);
    }
}
