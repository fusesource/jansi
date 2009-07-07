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
public class Ansi {
	
	private static final char FIRST_ESC_CHAR = 27;
	private static final char SECOND_ESC_CHAR = '[';
	
	public enum Color{
		BLACK(0, "BLACK"),
		RED(1, "RED"),
		GREEN(2, "GREEN"),
		YELLOW(3, "YELLOW"),
		BLUE(4, "BLUE"),
		MAGENTA(5, "MAGENTA"),
		CYAN(6, "CYAN"),
		WHITE(7, "WHITE");

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
			return value+30;
		}
		
		public int bg() {
			return value+40;
		}
	};
	
	
	public enum Attribute {
		
		INTENSITY_BOLD(1,"INTENSITY_BOLD"),  	
		INTENSITY_FAINT(2,"INTENSITY_FAINT"),
		ITALIC(3,"ITALIC"),
		UNDERLINE(4,"UNDERLINE"), 	
		BLINK_SLOW(5,"BLINK_SLOW"),
		BLINK_FAST(6,"BLINK_FAST"),
		NEGATIVE_ON(7,"NEGATIVE_ON"),
		CONCEAL_ON(8,"CONCEAL_ON"),
		UNDERLINE_DOUBLE(21,"UNDERLINE_DOUBLE"),
		INTENSITY_NORMAL(2,"INTENSITY_NORMAL"),
		UNDERLINE_OFF(24,"UNDERLINE_OFF"), 	
		BLINK_OFF(25,"BLINK_OFF"), 	
		NEGATIVE_OFF(27,"NEGATIVE_OFF"), 	
		CONCEAL_OFF(28,"CONCEAL_OFF");

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
		
	protected boolean isAttributeAnsi() {
		return false;
	}

	protected boolean isStringBuilderAnsi() {
		return false;
	}
	
	static private final class StringBuilderAnsi extends Ansi {
		private final Ansi previous;
		private final StringBuilder data = new StringBuilder();

		private StringBuilderAnsi(Ansi previous) {
			this.previous = previous;
		}

		@Override
		public void dump(StringBuilder builder) {
			previous.dump(builder);
			builder.append(data);
		}
		
		@Override
		protected boolean isStringBuilderAnsi() {
			return true;
		}
	}
	
	static abstract private class SimpleAnsi extends Ansi {
		protected final Ansi previous;
		private SimpleAnsi(Ansi previous) {
			this.previous = previous;
		}
		
	}

	static void dumpEscapeSequence(StringBuilder builder, char command, Object... options) {
		builder.append(FIRST_ESC_CHAR);
		builder.append(SECOND_ESC_CHAR);
		int size = options.length;
		for (int i = 0; i < size; i++) {
			if( i!=0 ) {
				builder.append(';');
			}
			builder.append(options[i]);
		}
		builder.append(command);
	}
	
	static private final class AttributeAnsi extends SimpleAnsi {
		private final ArrayList<Integer> options = new ArrayList<Integer>(5);

		private AttributeAnsi(Ansi previous) {
			super(previous);
		}

		@Override
		public void dump(StringBuilder builder) {
			previous.dump(builder);
			dumpEscapeSequence(builder, 'm', options.toArray());
		}
		
		@Override
		protected boolean isAttributeAnsi() {
			return true;
		}
	}
	
	public Ansi cursor(final int x, final int y) {
		return new SimpleAnsi(this) {
			@Override
			public void dump(StringBuilder builder) {
				previous.dump(builder);
				dumpEscapeSequence(builder, 'H', x, y);
			}
		};
	}

	public Ansi cursorUp(final int y) {
		return new SimpleAnsi(this) {
			@Override
			public void dump(StringBuilder builder) {
				previous.dump(builder);
				dumpEscapeSequence(builder, 'A', y);
			}
		};
	}

	public Ansi cursorDown(final int y) {
		return new SimpleAnsi(this) {
			@Override
			public void dump(StringBuilder builder) {
				previous.dump(builder);
				dumpEscapeSequence(builder, 'B', y);
			}
		};
	}
	public Ansi cursorRight(final int x) {
		return new SimpleAnsi(this) {
			@Override
			public void dump(StringBuilder builder) {
				previous.dump(builder);
				dumpEscapeSequence(builder, 'C', x);
			}
		};
	}

	public Ansi cursorLeft(final int x) {
		return new SimpleAnsi(this) {
			@Override
			public void dump(StringBuilder builder) {
				previous.dump(builder);
				dumpEscapeSequence(builder, 'D', x);
			}
		};
	}

	public Ansi fg(Color color) {
		AttributeAnsi rc = (AttributeAnsi) (isAttributeAnsi() ? this : new AttributeAnsi(this));
		rc.options.add(color.fg());
		return rc;
	}
	
	public Ansi bg(Color color) {
		AttributeAnsi rc = (AttributeAnsi) (isAttributeAnsi() ? this : new AttributeAnsi(this));
		rc.options.add(color.bg());
		return rc;
	}

	public Ansi a(Attribute attribute) {
		AttributeAnsi rc = (AttributeAnsi) (isAttributeAnsi() ? this : new AttributeAnsi(this));
		rc.options.add(attribute.value());
		return rc;
	}

	public Ansi a(String value) {
		StringBuilderAnsi rc = (StringBuilderAnsi) (isStringBuilderAnsi() ? this : new StringBuilderAnsi(this));
		rc.data.append(value);
		return rc;
	}

	public Ansi a(boolean value) {
		StringBuilderAnsi rc = (StringBuilderAnsi) (isStringBuilderAnsi() ? this : new StringBuilderAnsi(this));
		rc.data.append(value);
		return rc;
	}

	public Ansi a(char value) {
		StringBuilderAnsi rc = (StringBuilderAnsi) (isStringBuilderAnsi() ? this : new StringBuilderAnsi(this));
		rc.data.append(value);
		return rc;
	}

	public Ansi a(char[] value, int offset, int len) {
		StringBuilderAnsi rc = (StringBuilderAnsi) (isStringBuilderAnsi() ? this : new StringBuilderAnsi(this));
		rc.data.append(value, offset, len);
		return rc;
	}

	public Ansi a(char[] value) {
		StringBuilderAnsi rc = (StringBuilderAnsi) (isStringBuilderAnsi() ? this : new StringBuilderAnsi(this));
		rc.data.append(value);
		return rc;
	}

	public Ansi a(CharSequence value, int start, int end) {
		StringBuilderAnsi rc = (StringBuilderAnsi) (isStringBuilderAnsi() ? this : new StringBuilderAnsi(this));
		rc.data.append(value, start, end);
		return rc;
	}

	public Ansi a(CharSequence value) {
		StringBuilderAnsi rc = (StringBuilderAnsi) (isStringBuilderAnsi() ? this : new StringBuilderAnsi(this));
		rc.data.append(value);
		return rc;
	}

	public Ansi a(double value) {
		StringBuilderAnsi rc = (StringBuilderAnsi) (isStringBuilderAnsi() ? this : new StringBuilderAnsi(this));
		rc.data.append(value);
		return rc;
	}

	public Ansi a(float value) {
		StringBuilderAnsi rc = (StringBuilderAnsi) (isStringBuilderAnsi() ? this : new StringBuilderAnsi(this));
		rc.data.append(value);
		return rc;
	}

	public Ansi a(int value) {
		StringBuilderAnsi rc = (StringBuilderAnsi) (isStringBuilderAnsi() ? this : new StringBuilderAnsi(this));
		rc.data.append(value);
		return rc;
	}

	public Ansi a(long value) {
		StringBuilderAnsi rc = (StringBuilderAnsi) (isStringBuilderAnsi() ? this : new StringBuilderAnsi(this));
		rc.data.append(value);
		return rc;
	}

	public Ansi a(Object value) {
		StringBuilderAnsi rc = (StringBuilderAnsi) (isStringBuilderAnsi() ? this : new StringBuilderAnsi(this));
		rc.data.append(value);
		return rc;
	}

	public Ansi a(StringBuffer value) {
		StringBuilderAnsi rc = (StringBuilderAnsi) (isStringBuilderAnsi() ? this : new StringBuilderAnsi(this));
		rc.data.append(value);
		return rc;
	}
	
	
	public void dump(StringBuilder builder) {
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		dump(sb);
		return sb.toString();
	}
	
	public Ansi() {
	}

	public static Ansi ansi() {
		return ansi();
	}
	
}
