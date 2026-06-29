package org.fusesource.jansi.internal.struct;

/**
 * Maps to the Win32 {@code KEY_EVENT_RECORD} structure embedded in {@link INPUT_RECORD}.
 *
 * @see <a href="https://docs.microsoft.com/en-us/windows/console/key-event-record-str">
 * KEY_EVENT_RECORD (MSDN)</a>
 */
public class KEY_EVENT_RECORD {
    /** {@code true} if the key is being pressed; {@code false} if released. Win32: {@code bKeyDown}. */
    public boolean keyDown;
    /** Number of times the key was pressed. Win32: {@code wRepeatCount}. */
    public short   repeatCount;
    /** Virtual-key code. Win32: {@code wVirtualKeyCode}. */
    public short   virtualKeyCode;
    /** Virtual scan code. Win32: {@code wVirtualScanCode}. */
    public short   virtualScanCode;
    /** Unicode character for the key. Win32: {@code uChar.UnicodeChar}. */
    public char    uChar;
    /** Bitmask of active control key states. Win32: {@code dwControlKeyState}. */
    public int     controlKeyState;
    /** Size of this structure in bytes. */
    public static int SIZEOF;
}