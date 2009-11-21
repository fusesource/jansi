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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link AnsiString}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class AnsiStringTest
{
    @Test
    public void testNotEncoded() throws Exception {
        AnsiString as = new AnsiString("foo");
        assertEquals("foo", as.getEncoded());
        assertEquals("foo", as.getPlain());
        assertEquals(3, as.length());
    }

    @Test
    public void testEncoded() throws Exception {
        AnsiString as = new AnsiString(Ansi.ansi().a(Ansi.Attribute.INTENSITY_BOLD).a("foo").reset().toString());
        assertEquals("foo", as.getPlain());
        assertEquals(3, as.length());
    }
}