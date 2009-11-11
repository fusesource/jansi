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

import java.util.ArrayList;

/**
 * Provides a fluent API for generating ANSI escape sequences.
 * 
 * @author chirino
 */
final public class Ansi {

	private static final char FIRST_ESC_CHAR = 27;
	private static final char SECOND_ESC_CHAR = '[';

	public enum Color {
		BLACK(0, "BLACK"), 
		RED(1, "RED"), 
		GREEN(2, "GREEN"), 
		YELLOW(3, "YELLOW"), 
		BLUE(4, "BLUE"), 
		MAGENTA(5, "MAGENTA"), 
		CYAN(6, "CYAN"), 
		WHITE(7,"WHITE"),
		DEFAULT(9,"DEFAULT");

		private final int value;
		private final String name;

		Color(int index, String name) {
			this.value = index;
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

		public int value() {
			return value;
		}

		public int fg() {
			return value + 30;
		}

		public int bg() {
			return value + 40;
		}
		
		public int fgBright() {
			return value + 90;
		}
		
		public int bgBright() {
			return value + 100;
		}
	};

	public enum Attribute {
		RESET						(  0, "RESET"), 
		INTENSITY_BOLD				(  1, "INTENSITY_BOLD"), 
		INTENSITY_FAINT				(  2, "INTENSITY_FAINT"), 
		ITALIC						(  3, "ITALIC_ON"), 
		UNDERLINE					(  4, "UNDERLINE_ON"), 
		BLINK_SLOW					(  5, "BLINK_SLOW"), 
		BLINK_FAST					(  6, "BLINK_FAST"), 
		NEGATIVE_ON					(  7, "NEGATIVE_ON"), 
		CONCEAL_ON					(  8, "CONCEAL_ON"),
		STRIKETHROUGH_ON			(  9, "STRIKETHROUGH_ON"),
		UNDERLINE_DOUBLE			( 21, "UNDERLINE_DOUBLE"), 
		INTENSITY_BOLD_OFF			( 22, "INTENSITY_BOLD_OFF"),
		ITALIC_OFF					( 23, "ITALIC_OFF"),
		UNDERLINE_OFF				( 24, "UNDERLINE_OFF"), 
		BLINK_OFF					( 25, "BLINK_OFF"), 
		NEGATIVE_OFF				( 27, "NEGATIVE_OFF"), 
		CONCEAL_OFF					( 28, "CONCEAL_OFF"),
		STRIKETHROUGH_OFF			( 29, "STRIKETHROUGH_OFF");
		
		private final int value;
		private final String name;

		Attribute(int index, String name) {
			this.value = index;
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

		public int value() {
			return value;
		}

	};

	public enum Erase {
		FORWARD(0, "FORWARD"),
		BACKWARD(1, "BACKWARD"), 
		ALL(2, "ALL"); 

		private final int value;
		private final String name;

		Erase(int index, String name) {
			this.value = index;
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

		public int value() {
			return value;
		}
	};


	private final StringBuilder builder;
	private final ArrayList<Integer> attributeOptions = new ArrayList<Integer>(5);
	
	public Ansi() {
		this(new StringBuilder());
	}
	public Ansi(int size) {
		this(new StringBuilder(size));
	}
	public Ansi(StringBuilder builder) {
		this.builder = builder;
	}

	public static Ansi ansi() {
		return new Ansi();
	}
	public static Ansi ansi(StringBuilder builder) {
		return new Ansi(builder);
	}
	public static Ansi ansi(int size) {
		return new Ansi(size);
	}

	public Ansi fg(Color color) {
		attributeOptions.add(color.fg());
		return this;
	}

	public Ansi bg(Color color) {
		attributeOptions.add(color.bg());
		return this;
	}

	public Ansi a(Attribute attribute) {
		attributeOptions.add(attribute.value());
		return this;
	}
	
	public Ansi cursor(final int x, final int y) {
		return appendEscapeSequence('H', x, y);
	}

	public Ansi cursorUp(final int y) {
		return appendEscapeSequence('A', y);
	}

	public Ansi cursorDown(final int y) {
		return appendEscapeSequence('B', y);
	}

	public Ansi cursorRight(final int x) {
		return appendEscapeSequence('C', x);
	}

	public Ansi cursorLeft(final int x) {
		return appendEscapeSequence('D', x);
	}

	public Ansi eraseScreen() {
		return appendEscapeSequence('J',Erase.ALL.value());
	}

	public Ansi eraseScreen(final Erase kind) {
		return appendEscapeSequence('J', kind.value());
	}

	public Ansi eraseLine() {
		return appendEscapeSequence('K');
	}

	public Ansi eraseLine(final Erase kind) {
		return appendEscapeSequence('K', kind.value());
	}

	public Ansi scrollUp(final int rows) {
		return appendEscapeSequence('S', rows);
	}

	public Ansi scrollDown(final int rows) {
		return appendEscapeSequence('T', rows);
	}

	public Ansi saveCursorPosition() {
		return appendEscapeSequence('s');
	}

	public Ansi restorCursorPosition() {
		return appendEscapeSequence('u');
	}

	public Ansi reset() {
		return a(Attribute.RESET);
	}

	public Ansi a(String value) {
		fushAtttributes();		
		builder.append(value);
		return this;
	}

	public Ansi a(boolean value) {
		fushAtttributes();		
		builder.append(value);
		return this;
	}

	public Ansi a(char value) {
		fushAtttributes();		
		builder.append(value);
		return this;
	}

	public Ansi a(char[] value, int offset, int len) {
		fushAtttributes();		
		builder.append(value, offset, len);
		return this;
	}

	public Ansi a(char[] value) {
		fushAtttributes();		
		builder.append(value);
		return this;
	}

	public Ansi a(CharSequence value, int start, int end) {
		fushAtttributes();		
		builder.append(value, start, end);
		return this;
	}

	public Ansi a(CharSequence value) {
		fushAtttributes();		
		builder.append(value);
		return this;
	}

	public Ansi a(double value) {
		fushAtttributes();		
		builder.append(value);
		return this;
	}

	public Ansi a(float value) {
		fushAtttributes();		
		builder.append(value);
		return this;
	}

	public Ansi a(int value) {
		fushAtttributes();		
		builder.append(value);
		return this;
	}

	public Ansi a(long value) {
		fushAtttributes();		
		builder.append(value);
		return this;
	}

	public Ansi a(Object value) {
		fushAtttributes();		
		builder.append(value);
		return this;
	}

	public Ansi a(StringBuffer value) {
		fushAtttributes();		
		builder.append(value);
		return this;
	}

	@Override
	public String toString() {
		fushAtttributes();		
		return builder.toString();
	}

	///////////////////////////////////////////////////////////////////
	// Private Helper Methods
	///////////////////////////////////////////////////////////////////
	
	private Ansi appendEscapeSequence(char command) {
		fushAtttributes();
		builder.append(FIRST_ESC_CHAR);
		builder.append(SECOND_ESC_CHAR);
		builder.append(command);
		return this;
	}
	
	private Ansi appendEscapeSequence(char command, int option) {
		fushAtttributes();
		builder.append(FIRST_ESC_CHAR);
		builder.append(SECOND_ESC_CHAR);
		builder.append(option);
		builder.append(command);
		return this;
	}
	
	private Ansi appendEscapeSequence(char command, Object... options) {
		fushAtttributes();
		return _appendEscapeSequence(command, options);
	}

	
	private void fushAtttributes() {
		if( attributeOptions.isEmpty() )
			return;
		if (attributeOptions.size() == 1 && attributeOptions.get(0) == 0) {
			builder.append(FIRST_ESC_CHAR);
			builder.append(SECOND_ESC_CHAR);
			builder.append('m');
		} else {
			_appendEscapeSequence('m', attributeOptions.toArray());
		}
		attributeOptions.clear();
	}
	
	private Ansi _appendEscapeSequence(char command, Object... options) {
		builder.append(FIRST_ESC_CHAR);
		builder.append(SECOND_ESC_CHAR);
		int size = options.length;
		for (int i = 0; i < size; i++) {
			if (i != 0) {
				builder.append(';');
			}
			if (options[i] != null) {
				builder.append(options[i]);
			}
		}
		builder.append(command);
		return this;
	}
	
}
