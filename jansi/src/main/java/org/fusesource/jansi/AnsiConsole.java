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

import static org.fusesource.jansi.internal.CLibrary.STDOUT_FILENO;
import static org.fusesource.jansi.internal.CLibrary.isatty;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Provides consistent access to an ANSI aware console PrintStream.
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 * @since 1.0
 */
public class AnsiConsole {

	public static final PrintStream system_out = System.out;
    public static final PrintStream out = new PrintStream(wrapOutputStream(system_out));

    public static final PrintStream system_err = System.err;
    public static final PrintStream err = new PrintStream(wrapOutputStream(system_err));

    private static int installed;

	public static OutputStream wrapOutputStream(final OutputStream stream) {

        // If the jansi.passthrough property is set, then don't interpret
        // any of the ansi sequences.
        if( Boolean.getBoolean("jansi.passthrough") ) {
          return stream;
        }

        // If the jansi.strip property is set, then we just strip the
        // the ansi escapes.
        if( Boolean.getBoolean("jansi.strip") ) {
          return new AnsiOutputStream(stream);
        }

		String os = System.getProperty("os.name");
		if( os.startsWith("Windows") ) {
			
			// On windows we know the console does not interpret ANSI codes..
			try {
				return new WindowsAnsiOutputStream(stream);
			} catch (Throwable ignore) {
				// this happens when JNA is not in the path.. or
				// this happens when the stdout is being redirected to a file.
			}
			
			// Use the ANSIOutputStream to strip out the ANSI escape sequences.
			return new AnsiOutputStream(stream);
		}
		
		// We must be on some unix variant..
		try {
			// If we can detect that stdout is not a tty.. then setup
			// to strip the ANSI sequences..
			int rc = isatty(STDOUT_FILENO);
			if( rc==0 ) {
				return new AnsiOutputStream(stream);
			}
			
        // These erros happen if the JNI lib is not available for your platform.
        } catch (NoClassDefFoundError ignore) {
		} catch (UnsatisfiedLinkError ignore) {
		}

		// By default we assume your Unix tty can handle ANSI codes.
		// Just wrap it up so that when we get closed, we reset the 
		// attributes.
		return new FilterOutputStream(stream) {
		    @Override
		    public void close() throws IOException {
		        write(AnsiOutputStream.REST_CODE);
		        flush();
		        super.close();
		    }
		};
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
	 * If the standard out natively supports ANSI escape codes, then this just
	 * returns System.err, otherwise it will provide an ANSI aware PrintStream
	 * which strips out the ANSI escape sequences or which implement the escape
	 * sequences.
	 *
	 * @return a PrintStream which is ANSI aware.
	 */
    public static PrintStream err() {
        return err;
    }
	
	/**
	 * Install Console.out to System.out.
	 */
	synchronized static public void systemInstall() {
		installed++;
		if( installed==1 ) {
			System.setOut(out);
            System.setErr(err);
		}
	}
	
	/**
	 * undo a previous {@link #systemInstall()}.  If {@link #systemInstall()} was called 
	 * multiple times, it {@link #systemUninstall()} must call the same number of times before
	 * it is actually uninstalled.
	 */
	synchronized public static void systemUninstall() {
		installed--;
		if( installed==0 ) {
			System.setOut(system_out);
            System.setErr(system_err);
		}
	}
	
}
