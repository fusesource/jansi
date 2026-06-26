package org.fusesource.jansi.internal.struct;

/**
 * Maps to the Win32 {@code CHAR_INFO} structure.
 *
 * <p>Specifies a Unicode or ANSI character and its attributes in a
 * console screen buffer cell. Used by {@link Kernel32#ScrollConsoleScreenBuffer}
 * and related functions.</p>
 *
 * @see <a href="https://docs.microsoft.com/en-us/windows/console/char-info-str">CHAR_INFO (MSDN)</a>
 */
public class CHAR_INFO {
    /** The Unicode character value. Win32: {@code Char.UnicodeChar}. */
    public char  uChar;
    /** The character attributes (color flags). Win32: {@code Attributes}. */
    public short attributes;
    /** Size of this structure in bytes. */
    public static int SIZEOF;
}