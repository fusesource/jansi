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

import org.fusesource.jansi.Ansi.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the {@link Ansi} class.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class AnsiTest {
    @Test
    public void testSetEnabled() throws Exception {
        Ansi.setEnabled(false);
        new Thread() {
            @Override
            public void run() {
                assertEquals(false, Ansi.isEnabled());
            }
        }.run();

        Ansi.setEnabled(true);
        new Thread() {
            @Override
            public void run() {
                assertEquals(true, Ansi.isEnabled());
            }
        }.run();
    }

    @Test
    public void testClone() throws CloneNotSupportedException {
        Ansi ansi = Ansi.ansi().a("Some text").bg(Color.BLACK).fg(Color.WHITE);
        Ansi clone = new Ansi(ansi);

        assertEquals(ansi.a("test").reset().toString(), clone.a("test").reset().toString());
    }

    @Test
    public void testApply() {
        assertEquals("test", Ansi.ansi().apply(new Ansi.Consumer() {
            public void apply(Ansi ansi) {
                ansi.a("test");
            }
        }).toString());
    }
}