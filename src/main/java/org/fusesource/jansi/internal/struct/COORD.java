package org.fusesource.jansi.internal.struct;

/**
 * Maps to the Win32 {@code COORD} structure.
 *
 * <p>Defines the coordinates of a character cell in a console screen buffer,
 * where the origin is at (0,0) in the upper-left corner.</p>
 *
 * @see <a href="https://docs.microsoft.com/en-us/windows/console/coord-str">COORD (MSDN)</a>
 */
public class COORD {
    /** Horizontal coordinate (column). Win32: {@code X}. */
    public short x;
    /** Vertical coordinate (row). Win32: {@code Y}. */
    public short y;
    /** Size of this structure in bytes as reported by the native layer. */
    public static int SIZEOF;

    /**
     * Returns a human-readable representation of this coordinate.
     *
     * @return string in the format {@code (x, y)}
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public COORD copy() {
        COORD c = new COORD();
        c.x = this.x;
        c.y = this.y;
        return c;
    }
}