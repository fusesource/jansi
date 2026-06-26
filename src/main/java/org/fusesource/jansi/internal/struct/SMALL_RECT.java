package org.fusesource.jansi.internal.struct;

/**
 * Maps to the Win32 SMALL_RECT structure.
 *
 * <p>Defines the coordinates of the upper-left and lower-right corners of
 * a rectangle, used to describe a screen buffer region.</p>
 */
public class SMALL_RECT {
    /** Column coordinate of the upper-left corner. Win32: Left. */
    public short left;
    /** Row coordinate of the upper-left corner. Win32: Top. */
    public short top;
    /** Column coordinate of the lower-right corner. Win32: Right. */
    public short right;
    /** Row coordinate of the lower-right corner. Win32: Bottom. */
    public short bottom;
    /** Size of this structure in bytes as reported by the native layer. */
    public static int SIZEOF;

    /**
     * Calculates the width of the rectangle.
     */
    public int width() {
        return right - left + 1;
    }

    /**
     * Calculates the height of the rectangle.
     */
    public int height() {
        return bottom - top + 1;
    }

    /**
     * Creates a deep copy of this rectangle.
     */
    public SMALL_RECT copy() {
        SMALL_RECT c = new SMALL_RECT();
        c.left = this.left;
        c.top = this.top;
        c.right = this.right;
        c.bottom = this.bottom;
        return c;
    }

    @Override
    public String toString() {
        return "[(" + left + "," + top + ")-(" + right + "," + bottom + ")]";
    }
}