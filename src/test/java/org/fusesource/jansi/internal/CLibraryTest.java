/*
 * Copyright (C) 2009-2021 the original author(s).
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

import java.io.File;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.condition.OS;

@DisabledOnOs(OS.WINDOWS)
@DisabledOnJre({JRE.JAVA_15, JRE.JAVA_16})
public class CLibraryTest {

    @Test
    void testChdir() {
        File d = new File("target/tstDir");
        d.mkdirs();
        CLibrary.chdir(d.getAbsolutePath());
    }

    @Test
    void testSetenv() {
        CLibrary.setenv("MY_NAME", "myValue");
    }

}
