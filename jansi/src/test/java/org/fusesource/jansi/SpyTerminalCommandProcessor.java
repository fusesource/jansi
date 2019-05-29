package org.fusesource.jansi;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import org.fusesource.jansi.impl.TerminalCommandProcessor;
import org.fusesource.jansi.impl.TerminalType;

/**
 * Spy Mock class for checking calls to TerminalCommandProcessor
 */
public class SpyTerminalCommandProcessor extends TerminalCommandProcessor {

    public static final TerminalType SPY_TERM = new TerminalType() {
        @Override
        public TerminalCommandProcessor create(Writer output) throws IOException {
            return new SpyTerminalCommandProcessor(output);
        }
    };

    public int countProcessRestoreCursorPosition;
    public int countProcessSaveCursorPosition;
    public int countProcessInsertLine;
    public int countProcessDeleteLine;
    public int countProcessScrollDown;
    public int countProcessScrollUp;
    public int countProcessEraseScreen;
    public int countProcessEraseLine;
    public int countProcessSetAttribute;
    public int countProcessSetForegroundColor;
    public int countProcessSetForegroundColor2;
    public int countProcessSetForegroundColorExt;
    public int countProcessSetForegroundColorExt2;
    public int countProcessSetBackgroundColor;
    public int countProcessSetBackgroundColor2;
    public int countProcessSetBackgroundColorExt;
    public int countProcessSetBackgroundColorExt2;
    public int countProcessDefaultTextColor;
    public int countProcessDefaultBackgroundColor;
    public int countProcessAttributeRest;
    public int countProcessCursorTo;
    public int countProcessCursorToColumn;
    public int countProcessCursorUpLine;
    public int countProcessCursorDownLine;
    public int countProcessCursorLeft;
    public int countProcessCursorRight;
    public int countProcessCursorDown;
    public int countProcessCursorUp;
    public int countProcessUnknownExtension;
    public int countProcessChangeIconNameAndWindowTitle;
    public int countProcessChangeIconName;
    public int countProcessChangeWindowTitle;
    public int countProcessUnknownOperatingSystemCommand;
    public int countProcessCharsetSelect;

    public SpyTerminalCommandProcessor(Writer out) {
        super(out);
    }

    public void restCounts() {
        countProcessRestoreCursorPosition = 0;
        countProcessSaveCursorPosition = 0;
        countProcessInsertLine = 0;
        countProcessDeleteLine = 0;
        countProcessScrollDown = 0;
        countProcessScrollUp = 0;
        countProcessEraseScreen = 0;
        countProcessEraseLine = 0;
        countProcessSetAttribute = 0;
        countProcessSetForegroundColor = 0;
        countProcessSetForegroundColor2 = 0;
        countProcessSetForegroundColorExt = 0;
        countProcessSetForegroundColorExt2 = 0;
        countProcessSetBackgroundColor = 0;
        countProcessSetBackgroundColor2 = 0;
        countProcessSetBackgroundColorExt = 0;
        countProcessSetBackgroundColorExt2 = 0;
        countProcessDefaultTextColor = 0;
        countProcessDefaultBackgroundColor = 0;
        countProcessAttributeRest = 0;
        countProcessCursorTo = 0;
        countProcessCursorToColumn = 0;
        countProcessCursorUpLine = 0;
        countProcessCursorDownLine = 0;
        countProcessCursorLeft = 0;
        countProcessCursorRight = 0;
        countProcessCursorDown = 0;
        countProcessCursorUp = 0;
        countProcessUnknownExtension = 0;
        countProcessChangeIconNameAndWindowTitle = 0;
        countProcessChangeIconName = 0;
        countProcessChangeWindowTitle = 0;
        countProcessUnknownOperatingSystemCommand = 0;
        countProcessCharsetSelect = 0;
    }

    public int getSumCounts() {
        return countProcessRestoreCursorPosition +
                countProcessSaveCursorPosition +
                countProcessInsertLine +
                countProcessDeleteLine +
                countProcessScrollDown +
                countProcessScrollUp +
                countProcessEraseScreen +
                countProcessEraseLine +
                countProcessSetAttribute +
                countProcessSetForegroundColor +
                countProcessSetForegroundColor2 +
                countProcessSetForegroundColorExt +
                countProcessSetForegroundColorExt2 +
                countProcessSetBackgroundColor +
                countProcessSetBackgroundColor2 +
                countProcessSetBackgroundColorExt +
                countProcessSetBackgroundColorExt2 +
                countProcessDefaultTextColor +
                countProcessDefaultBackgroundColor +
                countProcessAttributeRest +
                countProcessCursorTo +
                countProcessCursorToColumn +
                countProcessCursorUpLine +
                countProcessCursorDownLine +
                countProcessCursorLeft +
                countProcessCursorRight +
                countProcessCursorDown +
                countProcessCursorUp +
                countProcessUnknownExtension +
                countProcessChangeIconNameAndWindowTitle +
                countProcessChangeIconName +
                countProcessChangeWindowTitle +
                countProcessUnknownOperatingSystemCommand +
                countProcessCharsetSelect;
    }

    @Override
    public void processRestoreCursorPosition() throws IOException {
        countProcessRestoreCursorPosition++;
        super.processRestoreCursorPosition();
    }

    @Override
    public void processSaveCursorPosition() throws IOException {
        countProcessSaveCursorPosition++;
        super.processSaveCursorPosition();
    }

    @Override
    public void processInsertLine(int optionInt) throws IOException {
        countProcessInsertLine++;
        super.processInsertLine(optionInt);
    }

    @Override
    public void processDeleteLine(int optionInt) throws IOException {
        countProcessDeleteLine++;
        super.processDeleteLine(optionInt);
    }

    @Override
    public void processScrollDown(int optionInt) throws IOException {
        countProcessScrollDown++;
        super.processScrollDown(optionInt);
    }

    @Override
    public void processScrollUp(int optionInt) throws IOException {
        countProcessScrollUp++;
        super.processScrollUp(optionInt);
    }

    @Override
    public void processEraseScreen(int eraseOption) throws IOException {
        countProcessEraseScreen++;
        super.processEraseScreen(eraseOption);
    }

    @Override
    public void processEraseLine(int eraseOption) throws IOException {
        countProcessEraseLine++;
        super.processEraseLine(eraseOption);
    }

    @Override
    public void processSetAttribute(int attribute) throws IOException {
        countProcessSetAttribute++;
        super.processSetAttribute(attribute);
    }

    @Override
    public void processSetForegroundColor(int color) throws IOException {
        countProcessSetForegroundColor++;
        super.processSetForegroundColor(color);
    }

    @Override
    public void processSetForegroundColor(int color, boolean bright) throws IOException {
        countProcessSetForegroundColor2++;
        super.processSetForegroundColor(color, bright);
    }

    @Override
    public void processSetForegroundColorExt(int paletteIndex) throws IOException {
        countProcessSetForegroundColorExt++;
        super.processSetForegroundColorExt(paletteIndex);
    }

    @Override
    public void processSetForegroundColorExt(int r, int g, int b) throws IOException {
        countProcessSetForegroundColorExt2++;
        super.processSetForegroundColorExt(r, g, b);
    }

    @Override
    public void processSetBackgroundColor(int color) throws IOException {
        countProcessSetBackgroundColor++;
        super.processSetBackgroundColor(color);
    }

    @Override
    public void processSetBackgroundColor(int color, boolean bright) throws IOException {
        countProcessSetBackgroundColor2++;
        super.processSetBackgroundColor(color, bright);
    }

    @Override
    public void processSetBackgroundColorExt(int paletteIndex) throws IOException {
        countProcessSetBackgroundColorExt++;
        super.processSetBackgroundColorExt(paletteIndex);
    }

    @Override
    public void processSetBackgroundColorExt(int r, int g, int b) throws IOException {
        countProcessSetBackgroundColorExt2++;
        super.processSetBackgroundColorExt(r, g, b);
    }

    @Override
    public void processDefaultTextColor() throws IOException {
        countProcessDefaultTextColor++;
        super.processDefaultTextColor();
    }

    @Override
    public void processDefaultBackgroundColor() throws IOException {
        countProcessDefaultBackgroundColor++;
        super.processDefaultBackgroundColor();
    }

    @Override
    public void processAttributeRest() throws IOException {
        countProcessAttributeRest++;
        super.processAttributeRest();
    }

    @Override
    public void processCursorTo(int row, int col) throws IOException {
        countProcessCursorTo++;
        super.processCursorTo(row, col);
    }

    @Override
    public void processCursorToColumn(int x) throws IOException {
        countProcessCursorToColumn++;
        super.processCursorToColumn(x);
    }

    @Override
    public void processCursorUpLine(int count) throws IOException {
        countProcessCursorUpLine++;
        super.processCursorUpLine(count);
    }

    @Override
    public void processCursorDownLine(int count) throws IOException {
        countProcessCursorDownLine++;
        super.processCursorDownLine(count);
    }

    @Override
    public void processCursorLeft(int count) throws IOException {
        countProcessCursorLeft++;
        super.processCursorLeft(count);
    }

    @Override
    public void processCursorRight(int count) throws IOException {
        countProcessCursorRight++;
        super.processCursorRight(count);
    }

    @Override
    public void processCursorDown(int count) throws IOException {
        countProcessCursorDown++;
        super.processCursorDown(count);
    }

    @Override
    public void processCursorUp(int count) throws IOException {
        countProcessCursorUp++;
        super.processCursorUp(count);
    }

    @Override
    public void processUnknownExtension(ArrayList<Object> options, int command) {
        countProcessUnknownExtension++;
        super.processUnknownExtension(options, command);
    }

    @Override
    public void processChangeIconNameAndWindowTitle(String label) {
        countProcessChangeIconNameAndWindowTitle++;
        super.processChangeIconNameAndWindowTitle(label);
    }

    @Override
    public void processChangeIconName(String label) {
        countProcessChangeIconName++;
        super.processChangeIconName(label);
    }

    @Override
    public void processChangeWindowTitle(String label) {
        countProcessChangeWindowTitle++;
        super.processChangeWindowTitle(label);
    }

    @Override
    public void processUnknownOperatingSystemCommand(int command, String param) {
        countProcessUnknownOperatingSystemCommand++;
        super.processUnknownOperatingSystemCommand(command, param);
    }

    @Override
    public void processCharsetSelect(int set, char seq) {
        countProcessCharsetSelect++;
        super.processCharsetSelect(set, seq);
    }
    
}
