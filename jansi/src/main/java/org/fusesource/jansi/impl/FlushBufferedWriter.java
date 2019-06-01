package org.fusesource.jansi.impl;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * buffered java.io.Writer with flushBuffer()
 *
 * similar to java.io.BufferedWriter,
 * and with flushBuffer() that flush the buffer to the underlying output
 * without calling flush() on the underlying.
 */
public class FlushBufferedWriter extends FilterWriter {

    protected char buf[];

    protected int count;

    public FlushBufferedWriter(Writer out, int size) {
        super(out);
        if (size <= 0) {
            throw new IllegalArgumentException("Buffer size <= 0");
        }
        buf = new char[size];
    }

    /** Flush the internal buffer */
    public void flushBuffer() throws IOException {
        if (count > 0) {
            out.write(buf, 0, count);
            count = 0;
        }
    }

    @Override
    public void write(int b) throws IOException {
        if (count >= buf.length) {
            flushBuffer();
        }
        buf[count++] = (char)b;
    }

    @Override
    public void write(char b[], int off, int len) throws IOException {
        if (len >= buf.length) {
            flushBuffer();
            out.write(b, off, len);
            return;
        }
        if (len > buf.length - count) {
            flushBuffer();
        }
        System.arraycopy(b, off, buf, count, len);
        count += len;
    }

    @Override
    public synchronized void flush() throws IOException {
        flushBuffer();
        out.flush();
    }

}