package org.fusesource.jansi.internal.struct;

/**
 * Maps to the Win32 {@code CONSOLE_SCREEN_BUFFER_INFO} structure.
 *
 * <p>Contains information about a console screen buffer: its size,
 * the cursor position, the current text attributes, and the size of
 * the visible window within the buffer.</p>
 *
 * @see <a href="https://docs.microsoft.com/en-us/windows/console/console-screen-buffer-info-str">
 * CONSOLE_SCREEN_BUFFER_INFO (MSDN)</a>
 */
public class CONSOLE_SCREEN_BUFFER_INFO {
    /** Size of the console screen buffer in columns (X) and rows (Y). Win32: {@code dwSize}. */
    public COORD     size           = new COORD();
    /** Position of the cursor within the screen buffer. Win32: {@code dwCursorPosition}. */
    public COORD     cursorPosition = new COORD();
    /** Character attributes for characters written to the buffer. Win32: {@code wAttributes}. */
    public short     attributes;
    /** Coordinates of the console window within the screen buffer. Win32: {@code srWindow}. */
    public SMALL_RECT window        = new SMALL_RECT();
    /** Maximum size of the console window given the screen and font size. Win32: {@code dwMaximumWindowSize}. */
    public COORD     maximumWindowSize = new COORD();
    /** Size of this structure in bytes. */
    public static int SIZEOF;

    /**
     * Returns the number of columns in the currently visible console window.
     *
     * <p>Equivalent to {@code srWindow.Right - srWindow.Left + 1} in Win32.</p>
     *
     * @return visible window width in character columns
     */
    public int windowWidth() {
        return window.right - window.left + 1;
    }

    /**
     * Returns the number of rows in the currently visible console window.
     *
     * <p>Equivalent to {@code srWindow.Bottom - srWindow.Top + 1} in Win32.</p>
     *
     * @return visible window height in character rows
     */
    public int windowHeight() {
        return window.bottom - window.top + 1;
    }
}