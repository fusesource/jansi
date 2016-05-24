/*
 * Copyright (C) 2009 the original author or authors.
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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the {@link AnsiRenderWriter} class.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class AnsiRenderWriterTest {
    private ByteArrayOutputStream baos;

    private AnsiRenderWriter out;

    @Before
    public void setUp() {
        baos = new ByteArrayOutputStream();
        out = new AnsiRenderWriter(baos);
    }

    @After
    public void tearDown() {
        out = null;
        baos = null;
    }

    @Test
    public void testRenderNothing() {
        out.print("foo");
        out.flush();

        String result = new String(baos.toByteArray());
        assertEquals("foo", result);
    }
}