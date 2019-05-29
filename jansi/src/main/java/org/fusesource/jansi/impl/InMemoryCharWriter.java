package org.fusesource.jansi.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

/**
 * in-memory array char writer
 *
 * similar to java.io.CharArrayWriter, 
 * but without lock and allowing access to underlying array
 */
public class InMemoryCharWriter extends Writer {

    protected char buf[];

    protected int count;

    public InMemoryCharWriter(int initialSize) {
        buf = new char[initialSize];
    }

    public int size() {
        return count;
    }

    public char[] getBuf() {
        return buf;
    }

    @Override
    public void write(int c) {
        int newcount = count + 1;
        if (newcount > buf.length) {
            buf = Arrays.copyOf(buf, Math.max(buf.length << 1, newcount));
        }
        buf[count] = (char)c;
        count = newcount;
    }

    @Override
    public void write(char c[], int off, int len) {
        if ((off < 0) || (off > c.length) || (len < 0) ||
            ((off + len) > c.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }
        int newcount = count + len;
        if (newcount > buf.length) {
            buf = Arrays.copyOf(buf, Math.max(buf.length << 1, newcount));
        }
        System.arraycopy(c, off, buf, count, len);
        count = newcount;
    }

    @Override
    public void write(String str, int off, int len) {
        int newcount = count + len;
        if (newcount > buf.length) {
            buf = Arrays.copyOf(buf, Math.max(buf.length << 1, newcount));
        }
        str.getChars(off, off + len, buf, count);
        count = newcount;
    }

    public void writeTo(Writer out) throws IOException {
        out.write(buf, 0, count);
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void close() {
    }

}