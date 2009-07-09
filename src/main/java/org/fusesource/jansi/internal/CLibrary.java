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

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * Interface to access some low level POSIX functions.
 * 
 * @author chirino
 */
public interface CLibrary extends Library {

	public static CLibrary CLIBRARY = (CLibrary) Native.loadLibrary("c", CLibrary.class);

	public static final int STDIN_FILENO=0;
	public static final int STDOUT_FILENO=1;
	public static final int STDERR_FILENO=2;
	
    public int isatty(int fd);


}
