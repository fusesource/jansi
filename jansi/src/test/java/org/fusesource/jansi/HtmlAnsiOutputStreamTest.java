/*
 * Copyright (C) 2009 the original author(s).
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

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="http://code.dblock.org">Daniel Doubrovkine</a>
 */
public class HtmlAnsiOutputStreamTest {

    private HtmlAnsiOutputStream hos;
    private ByteArrayOutputStream os;

    @Before
    public void setUp() {
    	os = new ByteArrayOutputStream();
        hos = new HtmlAnsiOutputStream(os);
    }

    @After
    public void tearDown() {
        hos = null;
        os = null;
    }


	@Test
	public void testNoMarkup() throws IOException {
		assertEquals(colorize("line"), "line");
	}

	@Test
	public void testClear() throws IOException {
		assertEquals(colorize("[0m[K"), "");
		assertEquals(colorize("[0mhello world"), "hello world");
	}

	@Test
	public void testBold() throws IOException {
		assertEquals(colorize("[1mhello world"), "<b>hello world</b>");
	}

	@Test
	public void testGreen() throws IOException {
		assertEquals(colorize("[32mhello world"), 
			"<span style=\"color: green;\">hello world</span>");
	}

	@Test
	public void testGreenOnWhite() throws IOException {
		assertEquals(colorize("[47;32mhello world"),
			"<span style=\"background-color: white;\"><span style=\"color: green;\">hello world</span></span>");
	}
	
	@Test
	public void testResetOnOpen() throws IOException {
		assertEquals(colorize("[0;31;49mred[0m"),
			"<span style=\"color: red;\">red</span>");
	}

	private String colorize(String text) throws IOException {
		hos.write(text.getBytes());
		hos.close();
		return os.toString();
	}
}
