package org.fusesource.jansi.internal.struct;

/**
 * Maps to the Win32 {@code MENU_EVENT_RECORD} structure (for internal use only).
 */
public class MENU_EVENT_RECORD {
    /** Win32: {@code dwCommandId}. */
    public int commandId;
    /** Size of this structure in bytes. */
    public static int SIZEOF;
}