/**
 *  Copyright (C) 2008 Progress Software, Inc. All rights reserved.
 *  http://fusesource.com
 *
 *  The software in this package is published under the terms of the AGPL license
 *  a copy of which has been included with this distribution in the license.txt file.
 */
package org.fusesource.jansi.internal;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Interface to access some low level Windows kernel functions.
 * 
 * @author chirino
 */
public interface Kernel32 extends StdCallLibrary {

	public static Kernel32 KERNEL32 = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);

	
	/**
	 * http://msdn.microsoft.com/en-us/library/ms686311%28VS.85%29.aspx
	 */
	public static class SMALL_RECT extends Structure {
		static public class ByValue extends SMALL_RECT implements Structure.ByValue { }
		static public class ByReference extends SMALL_RECT implements Structure.ByReference { }

		public short left;
		public short top;
		public short right;
		public short bottom;
	}
	
	/**
	 * see http://msdn.microsoft.com/en-us/library/ms686047%28VS.85%29.aspx
	 * @param consoleOutput
	 * @param attributes
	 * @return
	 */
	int SetConsoleTextAttribute(Pointer consoleOutput, short attributes);

	public static class COORD extends Structure {
		static public class ByValue extends COORD implements Structure.ByValue { }
		static public class ByReference extends COORD implements Structure.ByReference { }

		public short x;
		public short y;
		
		public ByValue copy() {
			ByValue copy = new ByValue();
			copy.x=x;
			copy.y=y;
			return copy;
		}
	}
	
	/**
	 * http://msdn.microsoft.com/en-us/library/ms682093%28VS.85%29.aspx
	 */
	public static class CONSOLE_SCREEN_BUFFER_INFO extends Structure {		
		static public class ByValue extends CONSOLE_SCREEN_BUFFER_INFO implements Structure.ByValue { }
		static public class ByReference extends CONSOLE_SCREEN_BUFFER_INFO implements Structure.ByReference { }

		public COORD      size = new COORD();
		public COORD      cursorPosition = new COORD();
		public short      attributes;
		public SMALL_RECT srWindow = new SMALL_RECT();
		public COORD      maximumWindowSize = new COORD();		
	}
	
	
	/**
	 * see: http://msdn.microsoft.com/en-us/library/ms724211%28VS.85%29.aspx
	 * 
	 * @param handle
	 * @return
	 */
	int CloseHandle(Pointer handle);

	public static final int FORMAT_MESSAGE_FROM_SYSTEM = 0x1000;

	/**
	 * 
	 * @param flags
	 * @param source
	 * @param messageId
	 * @param languageId
	 * @param buffer
	 * @param size
	 * @param arguments
	 * @return
	 */
	int FormatMessageW(int flags, Pointer source, int messageId,
			int languageId, Memory buffer, int size, Object... arguments);
	
	
	/**
	 * See: http://msdn.microsoft.com/en-us/library/ms683171%28VS.85%29.aspx
	 * @param consoleOutput
	 * @param consoleScreenBufferInfo
	 * @return
	 */
	int GetConsoleScreenBufferInfo(Pointer consoleOutput, CONSOLE_SCREEN_BUFFER_INFO.ByReference consoleScreenBufferInfo);

	static final int STD_INPUT_HANDLE = -10;
	static final int STD_OUTPUT_HANDLE=-11;
	static final int STD_ERROR_HANDLE=-12;
	
	/**
	 * see: http://msdn.microsoft.com/en-us/library/ms683231%28VS.85%29.aspx
	 * @param stdHandle
	 * @return
	 */
	Pointer GetStdHandle(int stdHandle);

	// see: http://msdn.microsoft.com/en-us/library/ms682088%28VS.85%29.aspx#_win32_character_attributes
    // and: http://doc.ddart.net/msdn/header/include/wincon.h.html
	static final short  FOREGROUND_BLUE      = 0x0001;
	static final short  FOREGROUND_GREEN     = 0x0002;
	static final short  FOREGROUND_RED       = 0x0004;
	static final short  FOREGROUND_INTENSITY = 0x0008;
	static final short  BACKGROUND_BLUE      = 0x0010;
	static final short  BACKGROUND_GREEN     = 0x0020;
	static final short  BACKGROUND_RED       = 0x0040;
	static final short  BACKGROUND_INTENSITY = 0x0080;
	static final short  COMMON_LVB_LEADING_BYTE    = 0x0100;
	static final short  COMMON_LVB_TRAILING_BYTE   = 0x0200;
	static final short  COMMON_LVB_GRID_HORIZONTAL = 0x0400;
	static final short  COMMON_LVB_GRID_LVERTICAL  = 0x0800;
	static final short  COMMON_LVB_GRID_RVERTICAL  = 0x1000;
	static final short  COMMON_LVB_REVERSE_VIDEO   = 0x4000;
	static final short  COMMON_LVB_UNDERSCORE      = (short) 0x8000;

	/**
	 * http://msdn.microsoft.com/en-us/library/ms686025%28VS.85%29.aspx
	 * @param consoleOutput
	 * @param cursorPosition
	 * @return
	 */
	int SetConsoleCursorPosition(Pointer consoleOutput, COORD.ByValue cursorPosition);

}
