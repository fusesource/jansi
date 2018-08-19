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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

/**
 * Tests for the {@link Ansi} class.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class AnsiTest {

    class AnsiTestTask implements Callable<Boolean> {
        private boolean enableAnsi;
        private String message;

        AnsiTestTask(boolean enableAnsi, String message) {
            this.enableAnsi = enableAnsi;
            this.message = message;
        }

        @Override
        public Boolean call() {
            boolean currentStatus = Ansi.isEnabled();
            Ansi.setEnabled( enableAnsi );
            System.out.println(Ansi.ansi().a("Ansi enabled before Test " + currentStatus + " " + Thread.currentThread().getName() +
                            ": Test message: ").fgBlue().a(message).reset());
            assertEquals(enableAnsi, Ansi.isEnabled() );
            return true;
        }
    }

    @Test
    public void testSetEnabled() throws InterruptedException {
        boolean status = Ansi.isEnabled();

        Ansi.setEnabled(true);
        ExecutorService executor = Executors.newCachedThreadPool();

        System.out.println(Ansi.ansi().fgBlue().a("Ansi test thread submit").reset());

        Future<Boolean> futureDisabled = executor.submit(new AnsiTestTask(false, "ANSI disabled"));
        Future<Boolean> futureEnabled = executor.submit(new AnsiTestTask(true, "ANSI enabled"));

        try {
            futureEnabled.get();
        } catch (ExecutionException e) {
            fail("Enabled test: "+e.getCause().getMessage());
        }

        try {
            futureDisabled.get();
        } catch (ExecutionException e) {
            fail("Disabled test: "+e.getCause().getMessage());
        }

        executor.shutdown();

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