package org.fusesource.jansi;

import java.io.CharArrayWriter;
import java.io.IOException;

/**
 * Spy Mock class for checking calls to Writer
 */
public class SpyCharArrayWriter extends CharArrayWriter {

    public int countFlush;
    public int countWriteChar;
    public int countWriteCharArray;
    public int countWriteString;
    public int countWriteStringFrag;
    public int countAppendChar;
    public int countAppendCharSequence;
    public int countAppendCharSequenceFrag;

    public void resetCounts() {
        countFlush = countWriteChar = countWriteCharArray = 0;
        countWriteString = countWriteStringFrag = 0;
        countAppendChar = countAppendCharSequence = countAppendCharSequenceFrag = 0;
    }

    public int getSumCounts() {
        return countFlush + countWriteChar + countWriteCharArray +
                countWriteString + countWriteStringFrag +
                countAppendChar + countAppendCharSequence + countAppendCharSequenceFrag;
    }

    public int getCountFlush() {
        return countFlush;
    }

    public int getCountWriteChar() {
        return countWriteChar;
    }

    public int getCountWriteCharArray() {
        return countWriteCharArray;
    }

    public int getCountWriteString() {
        return countWriteString;
    }

    public int getCountWriteStringFrag() {
        return countWriteStringFrag;
    }

    public int getCountAppendChar() {
        return countAppendChar;
    }

    public int getCountAppendCharSequence() {
        return countAppendCharSequence;
    }

    public int getCountAppendCharSequenceFrag() {
        return countAppendCharSequenceFrag;
    }

    @Override
    public void flush() {
        countFlush++;
        super.flush();
    }

    @Override
    public void write(int c) {
        countWriteChar++;
        super.write(c);
    }

    @Override
    public void write(char[] c, int off, int len) {
        countWriteCharArray++;
        super.write(c, off, len);
    }

    @Override
    public void write(char[] cbuf) throws IOException {
        countWriteCharArray++;
        super.write(cbuf);
    }

    @Override
    public void write(String str) throws IOException {
        countWriteString++;
        super.write(str);
    }

    @Override
    public void write(String str, int off, int len) {
        countWriteStringFrag++;
        super.write(str, off, len);
    }

    @Override
    public CharArrayWriter append(char c) {
        countAppendChar++;
        return super.append(c);
    }

    @Override
    public CharArrayWriter append(CharSequence csq) {
        countAppendCharSequence++;
        return super.append(csq);
    }

    @Override
    public CharArrayWriter append(CharSequence csq, int start, int end) {
        countAppendCharSequenceFrag++;
        return super.append(csq, start, end);
    }

}