package org.fusesource.jansi.internal.struct;

/**
 * Maps to the Win32 {@code WINDOW_BUFFER_SIZE_RECORD} structure.
 * Contains the new size of the console screen buffer after a resize event.
 */
public class WINDOW_BUFFER_SIZE_RECORD {
    /** The new size of the console screen buffer. Win32: {@code dwSize}. */
    public COORD size = new COORD();
    /** Size of this structure in bytes. */
    public static int SIZEOF;
}