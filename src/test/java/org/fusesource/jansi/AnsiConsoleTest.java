package org.fusesource.jansi;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;

public class AnsiConsoleTest {

	public static void main(String[] args) throws IOException {
		AnsiConsole.systemInstall();
        PrintStream out = System.out;
        FileInputStream f = new FileInputStream("src/test/resources/jansi.ans");
        int c;
        while( (c=f.read())>=0 ) {
        	out.write(c);
        }
        f.close();
	}
	
}
