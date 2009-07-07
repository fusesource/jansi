package org.fusesource.jansi;

import java.io.FileInputStream;
import java.io.IOException;

public class AnsiConsoleExample {

	public static void main(String[] args) throws IOException {
        String file = "src/test/resources/jansi.ans";
        if( args.length>0  )
        	file = args[0];

        // Allows us to disable ANSI processing. 
        if( "true".equals(System.getProperty("jansi", "true")) ) {
        	AnsiConsole.systemInstall();
        }
        
		FileInputStream f = new FileInputStream(file);
        int c;
        while( (c=f.read())>=0 ) {
        	System.out.write(c);
        }
        f.close();
	}
	
}
