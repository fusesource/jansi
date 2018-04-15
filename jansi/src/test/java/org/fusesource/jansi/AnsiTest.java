/*
 * Copyright (C) 2009-2018 the original author(s).
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
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for the {@link Ansi} class.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class AnsiTest {
    @Test
    public void testSetEnabled() throws InterruptedException {
        boolean status = Ansi.isEnabled();

        Ansi.setEnabled(false);
        Thread threadDisabled = new Thread() {
            @Override
            public void run() {
                System.out.println(Ansi.ansi().fgRed().a("ANSI disabled").reset());
                assertFalse( Ansi.isEnabled() );
            }
        };

        Ansi.setEnabled(true);
        Thread threadEnabled =new Thread() {
            @Override
            public void run() {
                System.out.println(Ansi.ansi().fgBlue().a("ANSI enabled").reset());
                assertTrue( Ansi.isEnabled() );
            }
        };

        Ansi.setEnabled(false);
        System.out.println(Ansi.ansi().fgBlue().a("Ansi test thread start").reset());

        threadDisabled.start();
        threadEnabled.start();

        threadEnabled.join();
        threadDisabled.join();

        // restore orginal status
        Ansi.setEnabled( status );
    }

    @Test
    public void testClone() {
        Ansi ansi = Ansi.ansi().a("Some text").bg(Color.BLACK).fg(Color.WHITE);
        Ansi clone = new Ansi(ansi);

        assertEquals(ansi.a("test").reset().toString(), clone.a("test").reset().toString());
    }
}