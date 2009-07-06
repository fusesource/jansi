package org.fusesource.jansi;

import static org.fusesource.jansi.Kernel32.FORMAT_MESSAGE_FROM_SYSTEM;
import static org.fusesource.jansi.Kernel32.KERNEL32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public class WindowsSupport {
	
	public static String getLastErrorMessage() {
		int errorCode = Native.getLastError();
		Memory buffer = new Memory(160);
		KERNEL32.FormatMessageW(FORMAT_MESSAGE_FROM_SYSTEM, Pointer.NULL, errorCode, 0, buffer, (int)buffer.getSize());
		return buffer.getString(0, true).trim();
	}
	
}
