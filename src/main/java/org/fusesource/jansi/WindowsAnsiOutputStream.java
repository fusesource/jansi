/**
 *  Copyright (C) 2008 Progress Software, Inc. All rights reserved.
 *  http://fusesource.com
 *
 *  The software in this package is published under the terms of the AGPL license
 *  a copy of which has been included with this distribution in the license.txt file.
 */
package org.fusesource.jansi;

import static org.fusesource.jansi.Kernel32.BACKGROUND_BLUE;
import static org.fusesource.jansi.Kernel32.BACKGROUND_GREEN;
import static org.fusesource.jansi.Kernel32.BACKGROUND_RED;
import static org.fusesource.jansi.Kernel32.FOREGROUND_BLUE;
import static org.fusesource.jansi.Kernel32.FOREGROUND_GREEN;
import static org.fusesource.jansi.Kernel32.FOREGROUND_RED;
import static org.fusesource.jansi.Kernel32.KERNEL32;

import java.io.IOException;
import java.io.OutputStream;

import org.fusesource.jansi.Kernel32.CONSOLE_SCREEN_BUFFER_INFO;

import com.sun.jna.Pointer;

public final class WindowsAnsiOutputStream extends AnsiOutputStream {
	
	private static final Pointer console = KERNEL32.GetStdHandle(Kernel32.STD_OUTPUT_HANDLE);

    private static final short FOREGROUND_BLACK   = 0;
    private static final short FOREGROUND_YELLOW  = FOREGROUND_RED|FOREGROUND_GREEN;    
    private static final short FOREGROUND_MAGENTA = FOREGROUND_BLUE|FOREGROUND_RED;    
    private static final short FOREGROUND_CYAN    = FOREGROUND_BLUE|FOREGROUND_GREEN;
    private static final short FOREGROUND_WHITE   = FOREGROUND_RED|FOREGROUND_GREEN|FOREGROUND_BLUE;

    private static final short BACKGROUND_BLACK   = 0;
    private static final short BACKGROUND_YELLOW  = BACKGROUND_RED|BACKGROUND_GREEN;    
    private static final short BACKGROUND_MAGENTA = BACKGROUND_BLUE|BACKGROUND_RED;    
    private static final short BACKGROUND_CYAN    = BACKGROUND_BLUE|BACKGROUND_GREEN;
    private static final short BACKGROUND_WHITE   = BACKGROUND_RED|BACKGROUND_GREEN|BACKGROUND_BLUE;
    
    private static final short ANSI_FOREGROUND_COLOR_MAP[] = {
    	FOREGROUND_BLACK,
    	FOREGROUND_RED,
    	FOREGROUND_GREEN,
    	FOREGROUND_YELLOW,
    	FOREGROUND_BLUE,
    	FOREGROUND_MAGENTA,
    	FOREGROUND_CYAN,
    	FOREGROUND_WHITE,
    };
        
    private static final short ANSI_BACKGROUND_COLOR_MAP[] = {
    	BACKGROUND_BLACK,
    	BACKGROUND_RED,
    	BACKGROUND_GREEN,
    	BACKGROUND_YELLOW,
    	BACKGROUND_BLUE,
    	BACKGROUND_MAGENTA,
    	BACKGROUND_CYAN,
    	BACKGROUND_WHITE,
    };
	
	private final CONSOLE_SCREEN_BUFFER_INFO.ByReference info = new CONSOLE_SCREEN_BUFFER_INFO.ByReference();

	private boolean negative;
	
	public WindowsAnsiOutputStream(OutputStream os) throws IOException {
		super(os);
		getConsoleInfo();
	}

	private void getConsoleInfo() throws IOException {
		out.flush();
		if( KERNEL32.GetConsoleScreenBufferInfo(console, info) == 0 ) {
			throw new IOException("Could not get the screen info: "+WindowsSupport.getLastErrorMessage());
		}
		if( negative ) {
			info.attributes = invertAttributeColors(info.attributes); 
		}
	}	
	
	private void applyAttribute() throws IOException {
		out.flush();
		short attributes = info.attributes;
		if( negative ) {
			attributes = invertAttributeColors(attributes); 
		}
		if( KERNEL32.SetConsoleTextAttribute(console, attributes) == 0 ) {
			throw new IOException(WindowsSupport.getLastErrorMessage());
		}
	}

	private short invertAttributeColors(short attibutes) {
		// Swap the the Foreground and Background bits.
		int fg = 0x000F & attibutes;
		fg <<= 8;
		int bg = 0X00F0 * attibutes;
		bg >>=8;
		attibutes = (short) ((attibutes & 0xFF00) | fg | bg);
		return attibutes;
	}

	private void applyCursorPosition() throws IOException {
		if( KERNEL32.SetConsoleCursorPosition(console, info.cursorPosition.copy()) == 0 ) {
			throw new IOException(WindowsSupport.getLastErrorMessage());
		}
	}

	@Override
	protected void processCursorLeft(int count) throws IOException {
		getConsoleInfo();
		info.cursorPosition.x = (short) Math.max(0, info.cursorPosition.x-count);
		applyCursorPosition();		
	}

	@Override
	protected void processCursorRight(int count) throws IOException {
		getConsoleInfo();
		info.cursorPosition.x = (short)Math.min(info.maximumWindowSize.x, info.cursorPosition.x+count);
		applyCursorPosition();		
	}
	
	@Override
	protected void processCursorDown(int count) throws IOException {
		getConsoleInfo();
		info.cursorPosition.y = (short) Math.min(info.maximumWindowSize.y, info.cursorPosition.y+count);
		applyCursorPosition();		
	}
	
	@Override
	protected void processCursorUp(int count) throws IOException {
		getConsoleInfo();
		info.cursorPosition.y = (short) Math.max(0, info.cursorPosition.y-count);
		applyCursorPosition();		
	}
	
	@Override
	protected void processSetForegroundColor(int color) throws IOException {
		info.attributes = (short)((info.attributes & ~0x0007 ) | ANSI_FOREGROUND_COLOR_MAP[color]);
		applyAttribute();
	}

	@Override
	protected void processSetBackgroundColor(int color) throws IOException {
		info.attributes = (short)((info.attributes & ~0x0070 ) | ANSI_BACKGROUND_COLOR_MAP[color]);
		applyAttribute();
	}

	@Override
	protected void processAttributeRest() throws IOException {
		info.attributes = (short)((info.attributes & ~0x00FF ) | FOREGROUND_WHITE | BACKGROUND_BLACK);
		applyAttribute();
	}

	@Override
	protected void processSetAttribute(int attribute) throws IOException {
		switch(attribute) {
			case ATTRIBUTE_INTENSITY_BOLD:
				info.attributes = (short)(info.attributes | Kernel32.FOREGROUND_INTENSITY );
				applyAttribute();
				break;
			case ATTRIBUTE_INTENSITY_NORMAL:
				info.attributes = (short)(info.attributes & ~Kernel32.FOREGROUND_INTENSITY );
				applyAttribute();
				break;
			
			// Yeah, setting the background intensity is not underlining.. but it's best we can do 
			// using the Windows console API 
			case ATTRIBUTE_UNDERLINE:
				info.attributes = (short)(info.attributes | Kernel32.BACKGROUND_INTENSITY );
				applyAttribute();
				break;
			case ATTRIBUTE_UNDERLINE_OFF:
				info.attributes = (short)(info.attributes & ~Kernel32.BACKGROUND_INTENSITY );
				applyAttribute();
				break;
				
			case ATTRIBUTE_NEGATIVE_ON:
				negative = true;
				applyAttribute();
				break;
			case ATTRIBUTE_NEGATIVE_Off:
				negative = false;
				applyAttribute();
				break;
		}
	}
}