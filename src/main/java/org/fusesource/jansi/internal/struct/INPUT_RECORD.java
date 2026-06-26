package org.fusesource.jansi.internal.struct;

/**
 * Maps to the Win32 {@code INPUT_RECORD} structure.
 *
 * <p>Describes an input event in the console input buffer. The
 * {@link #eventType} field indicates which union member is active.</p>
 *
 * @see <a href="https://docs.microsoft.com/en-us/windows/console/input-record-str">
 * INPUT_RECORD (MSDN)</a>
 */
public class INPUT_RECORD {
    /** Identifies the type of input event. One of the {@code KEY_EVENT},
     * {@code MOUSE_EVENT}, etc. constants. Win32: {@code EventType}. */
    public short                   eventType;
    /** Keyboard event data (valid when {@link #eventType} == {@link Kernel32#KEY_EVENT}). */
    public KEY_EVENT_RECORD        keyEvent              = new KEY_EVENT_RECORD();
    /** Mouse event data (valid when {@link #eventType} == {@link Kernel32#MOUSE_EVENT}). */
    public MOUSE_EVENT_RECORD      mouseEvent            = new MOUSE_EVENT_RECORD();
    /** Window resize data. */
    public WINDOW_BUFFER_SIZE_RECORD windowBufferSizeEvent = new WINDOW_BUFFER_SIZE_RECORD();
    /** Menu event data (internal use). */
    public MENU_EVENT_RECORD       menuEvent             = new MENU_EVENT_RECORD();
    /** Focus event data. */
    public FOCUS_EVENT_RECORD      focusEvent            = new FOCUS_EVENT_RECORD();
    /** Size of this structure in bytes. */
    public static int SIZEOF;
}