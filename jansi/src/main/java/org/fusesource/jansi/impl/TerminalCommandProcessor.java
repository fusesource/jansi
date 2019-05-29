package org.fusesource.jansi.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

/**
 * processor for executing terminal commands
 *
 * used as delegate inside AnsiOutputStream (instead of extending class)
 */
public class TerminalCommandProcessor {

    protected Writer out;

    public TerminalCommandProcessor(Writer out) {
        this.out = out;
    }

    /**
     * Process <code>CSI u</code> ANSI code, corresponding to <code>RCP – Restore Cursor Position</code>
     * @throws IOException IOException
     */
    public void processRestoreCursorPosition() throws IOException {
    }

    /**
     * Process <code>CSI s</code> ANSI code, corresponding to <code>SCP – Save Cursor Position</code>
     * @throws IOException IOException
     */
    public void processSaveCursorPosition() throws IOException {
    }

    /**
     * Process <code>CSI L</code> ANSI code, corresponding to <code>IL – Insert Line</code>
     * @param optionInt option
     * @throws IOException IOException
     * @since 1.16
     */
    public void processInsertLine(int optionInt) throws IOException {
    }

    /**
     * Process <code>CSI M</code> ANSI code, corresponding to <code>DL – Delete Line</code>
     * @param optionInt option
     * @throws IOException IOException
     * @since 1.16
     */
    public void processDeleteLine(int optionInt) throws IOException {
    }

    /**
     * Process <code>CSI n T</code> ANSI code, corresponding to <code>SD – Scroll Down</code>
     * @param optionInt option
     * @throws IOException IOException
     */
    public void processScrollDown(int optionInt) throws IOException {
    }

    /**
     * Process <code>CSI n U</code> ANSI code, corresponding to <code>SU – Scroll Up</code>
     * @param optionInt option
     * @throws IOException IOException
     */
    public void processScrollUp(int optionInt) throws IOException {
    }

    public static final int ERASE_SCREEN_TO_END = 0;
    public static final int ERASE_SCREEN_TO_BEGINING = 1;
    public static final int ERASE_SCREEN = 2;

    /**
     * Process <code>CSI n J</code> ANSI code, corresponding to <code>ED – Erase in Display</code>
     * @param eraseOption eraseOption
     * @throws IOException IOException
     */
    public void processEraseScreen(int eraseOption) throws IOException {
    }

    public static final int ERASE_LINE_TO_END = 0;
    public static final int ERASE_LINE_TO_BEGINING = 1;
    public static final int ERASE_LINE = 2;

    /**
     * Process <code>CSI n K</code> ANSI code, corresponding to <code>ED – Erase in Line</code>
     * @param eraseOption eraseOption
     * @throws IOException IOException
     */
    public void processEraseLine(int eraseOption) throws IOException {
    }

    public static final int ATTRIBUTE_INTENSITY_BOLD = 1; //     Intensity: Bold
    public static final int ATTRIBUTE_INTENSITY_FAINT = 2; //     Intensity; Faint     not widely supported
    public static final int ATTRIBUTE_ITALIC = 3; //     Italic; on     not widely supported. Sometimes treated as inverse.
    public static final int ATTRIBUTE_UNDERLINE = 4; //     Underline; Single
    public static final int ATTRIBUTE_BLINK_SLOW = 5; //     Blink; Slow     less than 150 per minute
    public static final int ATTRIBUTE_BLINK_FAST = 6; //     Blink; Rapid     MS-DOS ANSI.SYS; 150 per minute or more
    public static final int ATTRIBUTE_NEGATIVE_ON = 7; //     Image; Negative     inverse or reverse; swap foreground and background
    public static final int ATTRIBUTE_CONCEAL_ON = 8; //     Conceal on
    public static final int ATTRIBUTE_UNDERLINE_DOUBLE = 21; //     Underline; Double     not widely supported
    public static final int ATTRIBUTE_INTENSITY_NORMAL = 22; //     Intensity; Normal     not bold and not faint
    public static final int ATTRIBUTE_UNDERLINE_OFF = 24; //     Underline; None
    public static final int ATTRIBUTE_BLINK_OFF = 25; //     Blink; off
    @Deprecated
    public static final int ATTRIBUTE_NEGATIVE_Off = 27; //     Image; Positive
    public static final int ATTRIBUTE_NEGATIVE_OFF = 27; //     Image; Positive
    public static final int ATTRIBUTE_CONCEAL_OFF = 28; //     Reveal     conceal off

    /**
     * process <code>SGR</code> other than <code>0</code> (reset), <code>30-39</code> (foreground),
     * <code>40-49</code> (background), <code>90-97</code> (foreground high intensity) or
     * <code>100-107</code> (background high intensity)
     * @param attribute attribute
     * @throws IOException IOException
     * @see #processAttributeRest()
     * @see #processSetForegroundColor(int)
     * @see #processSetForegroundColor(int, boolean)
     * @see #processSetForegroundColorExt(int)
     * @see #processSetForegroundColorExt(int, int, int)
     * @see #processDefaultTextColor()
     * @see #processDefaultBackgroundColor()
     */
    public void processSetAttribute(int attribute) throws IOException {
    }

    public static final int BLACK = 0;
    public static final int RED = 1;
    public static final int GREEN = 2;
    public static final int YELLOW = 3;
    public static final int BLUE = 4;
    public static final int MAGENTA = 5;
    public static final int CYAN = 6;
    public static final int WHITE = 7;

    /**
     * process <code>SGR 30-37</code> corresponding to <code>Set text color (foreground)</code>.
     * @param color the text color
     * @throws IOException IOException
     */
    public void processSetForegroundColor(int color) throws IOException {
        processSetForegroundColor(color, false);
    }

    /**
     * process <code>SGR 30-37</code> or <code>SGR 90-97</code> corresponding to
     * <code>Set text color (foreground)</code> either in normal mode or high intensity.
     * @param color the text color
     * @param bright is high intensity?
     * @throws IOException IOException
     */
    public void processSetForegroundColor(int color, boolean bright) throws IOException {
    }

    /**
     * process <code>SGR 38</code> corresponding to <code>extended set text color (foreground)</code>
     * with a palette of 255 colors.
     * @param paletteIndex the text color in the palette
     * @throws IOException IOException
     */
    public void processSetForegroundColorExt(int paletteIndex) throws IOException {
    }

    /**
     * process <code>SGR 38</code> corresponding to <code>extended set text color (foreground)</code>
     * with a 24 bits RGB definition of the color.
     * @param r red
     * @param g green
     * @param b blue
     * @throws IOException IOException
     */
    public void processSetForegroundColorExt(int r, int g, int b) throws IOException {
    }

    /**
     * process <code>SGR 40-47</code> corresponding to <code>Set background color</code>.
     * @param color the background color
     * @throws IOException IOException
     */
    public void processSetBackgroundColor(int color) throws IOException {
        processSetBackgroundColor(color, false);
    }

    /**
     * process <code>SGR 40-47</code> or <code>SGR 100-107</code> corresponding to
     * <code>Set background color</code> either in normal mode or high intensity.
     * @param color the background color
     * @param bright is high intensity?
     * @throws IOException IOException
     */
    public void processSetBackgroundColor(int color, boolean bright) throws IOException {
    }

    /**
     * process <code>SGR 48</code> corresponding to <code>extended set background color</code>
     * with a palette of 255 colors.
     * @param paletteIndex the background color in the palette
     * @throws IOException IOException
     */
    public void processSetBackgroundColorExt(int paletteIndex) throws IOException {
    }

    /**
     * process <code>SGR 48</code> corresponding to <code>extended set background color</code>
     * with a 24 bits RGB definition of the color.
     * @param r red
     * @param g green
     * @param b blue
     * @throws IOException IOException
     */
    public void processSetBackgroundColorExt(int r, int g, int b) throws IOException {
    }

    /**
     * process <code>SGR 39</code> corresponding to <code>Default text color (foreground)</code>
     * @throws IOException IOException
     */
    public void processDefaultTextColor() throws IOException {
    }

    /**
     * process <code>SGR 49</code> corresponding to <code>Default background color</code>
     * @throws IOException IOException
     */
    public void processDefaultBackgroundColor() throws IOException {
    }

    /**
     * process <code>SGR 0</code> corresponding to <code>Reset / Normal</code>
     * @throws IOException IOException
     */
    public void processAttributeRest() throws IOException {
    }

    /**
     * process <code>CSI n ; m H</code> corresponding to <code>CUP – Cursor Position</code> or
     * <code>CSI n ; m f</code> corresponding to <code>HVP – Horizontal and Vertical Position</code>
     * @param row row
     * @param col column
     * @throws IOException IOException
     */
    public void processCursorTo(int row, int col) throws IOException {
    }

    /**
     * process <code>CSI n G</code> corresponding to <code>CHA – Cursor Horizontal Absolute</code>
     * @param x the column
     * @throws IOException IOException
     */
    public void processCursorToColumn(int x) throws IOException {
    }

    /**
     * process <code>CSI n F</code> corresponding to <code>CPL – Cursor Previous Line</code>
     * @param count line count
     * @throws IOException IOException
     */
    public void processCursorUpLine(int count) throws IOException {
    }

    /**
     * process <code>CSI n E</code> corresponding to <code>CNL – Cursor Next Line</code>
     * @param count line count
     * @throws IOException IOException
     */
    public void processCursorDownLine(int count) throws IOException {
        // Poor mans impl..
        for (int i = 0; i < count; i++) {
            out.write('\n');
        }
    }

    /**
     * process <code>CSI n D</code> corresponding to <code>CUB – Cursor Back</code>
     * @param count numer of characters to move left
     * @throws IOException IOException
     */
    public void processCursorLeft(int count) throws IOException {
    }

    /**
     * process <code>CSI n C</code> corresponding to <code>CUF – Cursor Forward</code>
     * @param count number of characters to move on
     * @throws IOException IOException
     */
    public void processCursorRight(int count) throws IOException {
        // Poor mans impl..
        for (int i = 0; i < count; i++) {
            out.write(' ');
        }
    }

    /**
     * process <code>CSI n B</code> corresponding to <code>CUD – Cursor Down</code>
     * @param count numer of line
     * @throws IOException IOException
     */
    public void processCursorDown(int count) throws IOException {
    }

    /**
     * process <code>CSI n A</code> corresponding to <code>CUU – Cursor Up</code>
     * @param count number of lines
     * @throws IOException IOException
     */
    public void processCursorUp(int count) throws IOException {
    }

    public void processUnknownExtension(ArrayList<Object> options, int command) {
    }

    /**
     * process <code>OSC 0;text BEL</code> corresponding to <code>Change Window and Icon label</code>
     * @param label window label
     */
    public void processChangeIconNameAndWindowTitle(String label) {
        processChangeIconName(label);
        processChangeWindowTitle(label);
    }

    /**
     * process <code>OSC 1;text BEL</code> corresponding to <code>Change Icon label</code>
     * @param label icon label name
     */
    public void processChangeIconName(String label) {
    }

    /**
     * process <code>OSC 2;text BEL</code> corresponding to <code>Change Window title</code>
     * @param label window label
     */
    public void processChangeWindowTitle(String label) {
    }

    /**
     * Process unknown <code>OSC</code> command.
     * @param command command
     * @param param command param
     */
    public void processUnknownOperatingSystemCommand(int command, String param) {
    }

    public void processCharsetSelect(int set, char seq) {
    }

}
