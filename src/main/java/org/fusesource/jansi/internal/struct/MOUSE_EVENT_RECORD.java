package org.fusesource.jansi.internal.struct;

/**
 * Maps to the Win32 {@code MOUSE_EVENT_RECORD} structure embedded in {@link INPUT_RECORD}.
 *
 * @see <a href="https://docs.microsoft.com/en-us/windows/console/mouse-event-record-str">
 * MOUSE_EVENT_RECORD (MSDN)</a>
 */
public class MOUSE_EVENT_RECORD {
    /** Mouse cursor position in screen buffer coordinates. Win32: {@code dwMousePosition}. */
    public COORD mousePosition  = new COORD();
    /** State of mouse buttons. Win32: {@code dwButtonState}. */
    public int   buttonState;
    /** State of control keys. Win32: {@code dwControlKeyState}. */
    public int   controlKeyState;
    /** Type of mouse event. Win32: {@code dwEventFlags}. */
    public int   eventFlags;
    /** Size of this structure in bytes. */
    public static int SIZEOF;

    /** Mouse control key flag: the CAPS LOCK key is toggled on. */
    public static int CAPSLOCK_ON;
    /** Mouse control key flag: the ENHANCED KEY flag. */
    public static int ENHANCED_KEY;
    /** Mouse control key flag: left ALT key is pressed. */
    public static int LEFT_ALT_PRESSED;
    /** Mouse control key flag: left CTRL key is pressed. */
    public static int LEFT_CTRL_PRESSED;
    /** Mouse control key flag: NUM LOCK is on. */
    public static int NUMLOCK_ON;
    /** Mouse control key flag: right ALT key is pressed. */
    public static int RIGHT_ALT_PRESSED;
    /** Mouse control key flag: right CTRL key is pressed. */
    public static int RIGHT_CTRL_PRESSED;
    /** Mouse control key flag: SCROLL LOCK is on. */
    public static int SCROLLLOCK_ON;
    /** Mouse control key flag: SHIFT key is pressed. */
    public static int SHIFT_PRESSED;
}