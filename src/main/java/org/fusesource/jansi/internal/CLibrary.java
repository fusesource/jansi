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
package org.fusesource.jansi.internal;

import static org.fusesource.hawtjni.runtime.FieldFlag.CONSTANT;
import static org.fusesource.hawtjni.runtime.MethodFlag.CONSTANT_INITIALIZER;

import org.fusesource.hawtjni.runtime.JniField;
import org.fusesource.hawtjni.runtime.JniMethod;
import org.fusesource.hawtjni.runtime.Library;

/**
 * Interface to access some low level POSIX functions.
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class CLibrary {
	
    private static final Library LIBRARY = new Library("jansi", CLibrary.class);    
	static {
        LIBRARY.load();
        init();
	}

    @JniMethod(flags={CONSTANT_INITIALIZER})
    private static final native void init();

    @JniField(flags={CONSTANT})
	public static int STDIN_FILENO;
    @JniField(flags={CONSTANT})
	public static int STDOUT_FILENO;
    @JniField(flags={CONSTANT})
	public static int STDERR_FILENO;

    @JniField(flags={CONSTANT}, accessor="1", conditional="defined(HAVE_ISATTY)")
	public static boolean HAVE_ISATTY;	    
    @JniMethod(conditional="defined(HAVE_ISATTY)")
    public static final native int isatty(int fd);


}
