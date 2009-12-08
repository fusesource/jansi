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

import static org.fusesource.jansi.internal.Kernel32.BACKGROUND_BLUE;
import static org.fusesource.jansi.internal.Kernel32.BACKGROUND_GREEN;
import static org.fusesource.jansi.internal.Kernel32.BACKGROUND_RED;
import static org.fusesource.jansi.internal.Kernel32.FOREGROUND_BLUE;
import static org.fusesource.jansi.internal.Kernel32.FOREGROUND_GREEN;
import static org.fusesource.jansi.internal.Kernel32.FOREGROUND_RED;
import static org.fusesource.jansi.internal.Kernel32.KERNEL32;

import java.io.IOException;
import java.io.OutputStream;

import org.fusesource.jansi.internal.Kernel32;
import org.fusesource.jansi.internal.WindowsSupport;
import org.fusesource.jansi.internal.Kernel32.CONSOLE_SCREEN_BUFFER_INFO;
import org.fusesource.jansi.internal.Kernel32.COORD;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

/**
 * A Windows ANSI escape processor, uses JNA to access native platform
 * API's to change the console attributes.
 * 
 * @since 1.0
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
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
    private final short originalColors;
    
	private boolean negative;
	
	public WindowsAnsiOutputStream(OutputStream os) throws IOException {
		super(os);
		getConsoleInfo();
		originalColors = info.attributes;
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
	
 	@Override
 	public void close() throws IOException {
 	    out.flush();
 		info.attributes = originalColors;
 		this.negative = false;
 		applyAttribute();
 		super.close();
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
	protected void processEraseScreen(int eraseOption) throws IOException {
		getConsoleInfo();
		IntByReference written = new IntByReference();
		switch(eraseOption) {
		case ERASE_SCREEN:
			COORD.ByValue topLeft = new COORD.ByValue();
			topLeft.x = 0;
			topLeft.y = info.window.top;
			int screenLength = info.window.height() * info.size.x;
			KERNEL32.FillConsoleOutputCharacterW(console, ' ', screenLength, topLeft, written);
			break;
		case ERASE_SCREEN_TO_BEGINING:
			COORD.ByValue topLeft2 = new COORD.ByValue();
			topLeft2.x = 0;
			topLeft2.y = info.window.top;
			int lengthToCursor = (info.cursorPosition.y - info.window.top) * info.size.x 
				+ info.cursorPosition.x;
			KERNEL32.FillConsoleOutputCharacterW(console, ' ', lengthToCursor, topLeft2, written);
			break;
		case ERASE_SCREEN_TO_END:
			int lengthToEnd = (info.window.bottom - info.cursorPosition.y) * info.size.x + 
				(info.size.x - info.cursorPosition.x);
			KERNEL32.FillConsoleOutputCharacterW(console, ' ', lengthToEnd, info.cursorPosition.copy(), written);
		}		
	}
	
	@Override
	protected void processEraseLine(int eraseOption) throws IOException {
		getConsoleInfo();
		IntByReference written = new IntByReference();
		switch(eraseOption) {
		case ERASE_LINE:
			COORD.ByValue leftColCurrRow = info.cursorPosition.copy();
			leftColCurrRow.x = 0;
			KERNEL32.FillConsoleOutputCharacterW(console, ' ', info.size.x, leftColCurrRow, written);
			break;
		case ERASE_LINE_TO_BEGINING:
			COORD.ByValue leftColCurrRow2 = info.cursorPosition.copy();
			leftColCurrRow2.x = 0;
			KERNEL32.FillConsoleOutputCharacterW(console, ' ', info.cursorPosition.x, leftColCurrRow2, written);
			break;
		case ERASE_LINE_TO_END:
			int lengthToLastCol = info.size.x - info.cursorPosition.x;
			KERNEL32.FillConsoleOutputCharacterW(console, ' ', lengthToLastCol, info.cursorPosition.copy(), written);
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
		info.cursorPosition.x = (short)Math.min(info.window.width(), info.cursorPosition.x+count);
		applyCursorPosition();		
	}
	
	@Override
	protected void processCursorDown(int count) throws IOException {
		getConsoleInfo();
		info.cursorPosition.y = (short) Math.min(info.size.y, info.cursorPosition.y+count);
		applyCursorPosition();		
	}
	
	@Override
	protected void processCursorUp(int count) throws IOException {
		getConsoleInfo();
		info.cursorPosition.y = (short) Math.max(info.window.top, info.cursorPosition.y-count);
		applyCursorPosition();		
	}
	
	@Override
	protected void processCursorTo(int x, int y) throws IOException {
		getConsoleInfo();
		info.cursorPosition.y = (short) Math.max(info.window.top, Math.min(info.size.y, info.window.top+y-1));
		info.cursorPosition.x = (short) Math.max(0, Math.min(info.window.width(), x-1));
		applyCursorPosition();		
	}

	@Override
	protected void processCursorToColumn(int x) throws IOException {
		getConsoleInfo();
		info.cursorPosition.x = (short) Math.max(0, Math.min(info.window.width(), x-1));
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
		info.attributes = (short)((info.attributes & ~0x00FF ) | originalColors);
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