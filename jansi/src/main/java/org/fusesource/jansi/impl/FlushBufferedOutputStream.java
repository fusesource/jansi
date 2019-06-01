package org.fusesource.jansi.impl;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * buffered java.io.OutputStream with flushBuffer()
 *
 * similar to java.io.ByteArrayOutputStream / BufferedOutputSteam,
 * and with flushBuffer() that flush the buffer to the underlying output
 * without calling flush() on the underlying writer.
 */
public class FlushBufferedOutputStream extends FilterOutputStream {

    protected byte buf[];

    protected int count;

    public FlushBufferedOutputStream(OutputStream out, int size) {
        super(out);
        if (size <= 0) {
            throw new IllegalArgumentException("Buffer size <= 0");
        }
        buf = new byte[size];
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
        buf[count++] = (byte)b;
    }

    @Override
    public void write(byte b[], int off, int len) throws IOException {
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