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

import java.util.Arrays;
import java.util.List;

import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.Platforms;
import org.graalvm.nativeimage.c.CContext;

@Platforms(Platform.WINDOWS.class)
public class WindowsDirectives implements CContext.Directives {

    private static final String[] HEADERS = {
        "<stdlib.h>", "<string.h>", "<windows.h>", "<conio.h>", "<io.h>", "<intrin.h>"
    };

    private static final String[] LIBS = {"Kernel32"};

    @Override
    public boolean isInConfiguration() {
        return Platform.includedIn(Platform.WINDOWS.class);
    }

    @Override
    public List<String> getHeaderFiles() {
        return Arrays.asList(HEADERS);
    }

    @Override
    public List<String> getLibraries() {
        return Arrays.asList(LIBS);
    }
}
