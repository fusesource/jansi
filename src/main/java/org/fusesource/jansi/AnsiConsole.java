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
	 * Defines an interface which installs the System.out field.  This
	 * impl fails due to the System.out field being static.  Derived classes
	 * can implement JVM specific hacks to get around that problem. 
	 *  
	 * @author chirino
	 */
	public static class SystemOutInstaller {
		protected PrintStream old_out = system_out;
		protected PrintStream new_out = out;
		
		protected Field getOutField() throws NoSuchFieldException {
			Field field = System.class.getField("out");
			field.setAccessible(true);
			return field;
		}
		
		public void install() throws Exception {
			getOutField().set(null, new_out);
		}

		public void uninstall() throws Exception {
			getOutField().set(null, old_out);
		}
	}

	private static SystemOutInstaller creatInstaller() {
		// Try to load the Sun JVM specific installers first..
		try {
			Class<?> clazz = SystemOutInstaller.class.getClassLoader().loadClass("org.fusesource.jansi.SunSystemOutInstaller");
			return (SystemOutInstaller)clazz.newInstance();
		} catch (Throwable ignore) {
		}
		return new SystemOutInstaller();
	}


	/**
	 * Install Console.out to System.out.  Since System.out is declared
	 * as 'static final', the success of this call is highly dependent 
	 * on the JVM being used.
	 * 
	 * @return true if successfully installed.
	 */
	public static boolean systemInstall() {
		if( !installed && System.out != out ) {
			try {
				SystemOutInstaller installer = creatInstaller();
				installer.install();
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
				SystemOutInstaller installer = creatInstaller();
				installer.uninstall();
				installed=false;
				return true;
			} catch (Throwable e) {
			}
		}
		return false;
	}
	
}
