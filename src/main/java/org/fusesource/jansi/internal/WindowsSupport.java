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

import static org.fusesource.jansi.internal.Kernel32.*;

import org.fusesource.jansi.internal.Kernel32.CONSOLE_SCREEN_BUFFER_INFO;

/**
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class WindowsSupport {
	
	public static String getLastErrorMessage() {
		int errorCode = GetLastError();
		int bufferSize = 160;
		byte data[] = new byte[bufferSize]; 
		FormatMessageW(FORMAT_MESSAGE_FROM_SYSTEM, 0, errorCode, 0, data, bufferSize, null);
		return new String(data);
	}
	
    //////////////////////////////////////////////////////////////////////////
    //
    // The following helper methods are for jline 
    //
    //////////////////////////////////////////////////////////////////////////
    
    public static int readByte() {
        return _getch();
    }
    
    public static int getConsoleMode() {
        long hConsole = GetStdHandle (STD_INPUT_HANDLE);
        if (hConsole == INVALID_HANDLE_VALUE)
            return -1;
        int mode[] = new int[1];
        if (GetConsoleMode (hConsole, mode)==0)
            return -1;
        return mode[0];
    }

    public static void setConsoleMode(int mode) {
        long hConsole = GetStdHandle (STD_INPUT_HANDLE);
        if (hConsole == INVALID_HANDLE_VALUE)
            return;
        SetConsoleMode (hConsole, mode);
    }
    
    public static int getWindowsTerminalWidth() {
        long outputHandle = GetStdHandle (STD_OUTPUT_HANDLE);
        CONSOLE_SCREEN_BUFFER_INFO info = new CONSOLE_SCREEN_BUFFER_INFO(); 
        GetConsoleScreenBufferInfo (outputHandle, info);
        return info.windowWidth();        
    }

    public static int getWindowsTerminalHeight() {
        long outputHandle = GetStdHandle (STD_OUTPUT_HANDLE);
        CONSOLE_SCREEN_BUFFER_INFO info = new CONSOLE_SCREEN_BUFFER_INFO(); 
        GetConsoleScreenBufferInfo (outputHandle, info);
        return info.windowHeight();        
    }
}
