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
package org.fusesource.jansi.internal.nativeimage;

import org.fusesource.jansi.AnsiConsole;
import org.fusesource.jansi.internal.AnsiConsoleSupport;
import org.fusesource.jansi.internal.OSInfo;
import org.fusesource.jansi.internal.stty.SttyCLibrary;
import org.graalvm.nativeimage.c.type.CTypeConversion;

public final class AnsiConsoleSupportImpl extends AnsiConsoleSupport {

    public AnsiConsoleSupportImpl() {
        super(AnsiConsole.JANSI_PROVIDER_NATIVE_IMAGE);

        if (!OSInfo.isInImageCode()) {
            throw new UnsupportedOperationException("This provider is only available in native images");
        }

        if (OSInfo.isWindows()) {
            throw new UnsupportedOperationException("This provider is currently unavailable on Windows");
        }
    }

    @Override
    protected CLibrary createCLibrary() {
        String stdoutTty = CTypeConversion.toJavaString(PosixCLibrary.ttyname(CLibrary.STDOUT_FILENO));
        String stderrTty = CTypeConversion.toJavaString(PosixCLibrary.ttyname(CLibrary.STDERR_FILENO));

        return new SttyCLibrary(stdoutTty, stderrTty) {
            @Override
            public int isTty(int fd) {
                return PosixCLibrary.isatty(fd);
            }
        };
    }

    @Override
    protected Kernel32 createKernel32() {
        throw new UnsupportedOperationException();
    }
}
