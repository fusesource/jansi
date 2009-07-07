/**
 * Copyright (C) 2009, Progress Software Corporation and/or its 
 * subsidiaries or affiliates.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fusesource.jansi;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.BLACK;
import static org.fusesource.jansi.Ansi.Color.BLUE;
import junit.framework.TestCase;

public class AnsiTest extends TestCase {
	
	static final public String ESC=((char)27)+"[";
	
	public void testAnsi() {
		String expected = "Hello World: "+ESC+"34;40m125";
		String actual = ansi().a("Hello").a(" World: ").fg(BLUE).bg(BLACK).a(125L).toString();
		assertEquals(expected, actual);
	}
	
}
