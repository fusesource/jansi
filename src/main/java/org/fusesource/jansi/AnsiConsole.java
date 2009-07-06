package org.fusesource.jansi;

import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * Provides consistent access to an ANSI aware console PrintStream.
 * 
 * @author chirino
 */
public class AnsiConsole {

	private static final PrintStream system_out = System.out;
	public static final PrintStream out = createAnsiConsoleOut();
	private static boolean installed;

	
	private static boolean stdoutHasNativeAnsiSupport() {
		String os = System.getProperty("os.name");
		if( os.startsWith("Windows") ) {
			return false;
		}
		
		// TODO: Improve Unix Support
		// Most Unix terminals have native ANSI support, but it depends
		// on the connected terminal.  Would need to dive deeper
		// to figure out if the stdout is connected to a terminal that 
		// does support ANSI.
		return true;
	}

	private static PrintStream createAnsiConsoleOut() {
		if( stdoutHasNativeAnsiSupport() ) {
			return system_out;
		}
		
		// Try to dynamically load the WindowsANSIOutputStream.  This may fail due to:
		//  * JNA not being in the path
		//  * Not being attached to a console
		try {
			Class<?> clazz = AnsiOutputStream.class.getClassLoader().loadClass("org.fusesource.jansi.WindowsAnsiOutputStream");
			Constructor<?> constructor = clazz.getConstructor(new Class []{OutputStream.class});
			AnsiOutputStream out =  (AnsiOutputStream) constructor.newInstance(new Object[]{system_out}); 
			return new PrintStream(out);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		// Use the ANSIOutputStream to strip out the ANSI escape sequences.
		AnsiOutputStream out = new AnsiOutputStream(system_out);
		return new PrintStream(out);
	}

	/**
	 * If the standard out natively supports ANSI escape codes, then this just 
	 * returns System.out, otherwise it will provide an ANSI aware PrintStream
	 * which strips out the ANSI escape sequences or which implement the escape
	 * sequences.
	 * 
	 * @return a PrintStream which is ANSI aware.
	 */
	public static PrintStream out() {
		return out;
	}
	
	/**
	 * Install Console.out to System.out
	 * @return true if successfully installed.
	 */
	public static boolean systemInstall() {
		if( !installed && System.out != out ) {
			try {
				Field field = System.class.getField("out");
				field.setAccessible(true);
				field.set(null, out);
				installed=true;
				return true;
			} catch (Throwable e) {
			}
		}
		return false;
	}
	
	/**
	 * undo a previous {@link #systemInstall()}
	 * @return true if successfully uninstalled.
	 */
	public static boolean systemUninstall() {
		if( installed ) {
			try {
				Field field = System.class.getField("out");
				field.setAccessible(true);
				field.set(null, system_out);
				installed=false;
				return true;
			} catch (Throwable e) {
			}
		}
		return false;
	}
	
}
