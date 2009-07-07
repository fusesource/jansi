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
