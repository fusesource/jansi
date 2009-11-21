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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the {@link AnsiRenderer} class.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class AnsiRendererTest
{
    private AnsiRenderer renderer;

    @Before
    public void setUp() {
        Ansi.setEnabled(true);
        renderer = new AnsiRenderer();
    }

    @After
    public void tearDown() {
        renderer = null;
    }

    @Test
    public void testTest() throws Exception {
        assertFalse(AnsiRenderer.test("foo"));
        assertTrue(AnsiRenderer.test("@|foo|"));
        assertTrue(AnsiRenderer.test("@|foo"));
    }

    @Test
    public void testRender() {
        String str = renderer.render("@|bold foo|@");
        System.out.println(str);
        assertEquals(Ansi.ansi().a(Ansi.Attribute.INTENSITY_BOLD).a("foo").reset().toString(), str);
    }

    @Test
    public void testRender2() {
        String str = renderer.render("@|bold,red foo|@");
        System.out.println(str);
        assertEquals(Ansi.ansi().a(Ansi.Attribute.INTENSITY_BOLD).fg(Ansi.Color.RED).a("foo").reset().toString(), str);
    }

    @Test
    public void testRender3() {
        String str = renderer.render("@|bold,red foo bar baz|@");
        System.out.println(str);
        assertEquals(Ansi.ansi().a(Ansi.Attribute.INTENSITY_BOLD).fg(Ansi.Color.RED).a("foo bar baz").reset().toString(), str);
    }

    @Test
    public void testRender4() {
        String str = renderer.render("@|bold,red foo bar baz|@ ick @|bold,red foo bar baz|@");
        System.out.println(str);
        assertEquals(Ansi.ansi()
                .a(Ansi.Attribute.INTENSITY_BOLD).fg(Ansi.Color.RED).a("foo bar baz").reset()
                .a(" ick ")
                .a(Ansi.Attribute.INTENSITY_BOLD).fg(Ansi.Color.RED).a("foo bar baz").reset()
                .toString(), str);
    }

    @Test
    public void testRenderNothing() {
        assertEquals("foo", renderer.render("foo"));
    }

    @Test
    public void testRenderInvalidMissingEnd() {
        String str = renderer.render("@|bold foo");
        assertEquals("@|bold foo", str);
    }

    @Test
    public void testRenderInvalidMissingText() {
        String str = renderer.render("@|bold|@");
        assertEquals("@|bold|@", str);
    }
}