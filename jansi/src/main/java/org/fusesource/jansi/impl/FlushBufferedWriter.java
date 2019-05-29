package org.fusesource.jansi.impl;

import java.io.IOException;
import java.io.Writer;

/**
 * in-memory flushing char writer
 * 
 * similar to java.io.BufferedWriter, but without lock
 * and with flushBuffer() that flush the buffer to the underlying output
 * without calling flush() on the underlying writer.
 */
public class FlushBufferedWriter extends InMemoryCharWriter {

    protected Writer underlying;

    public FlushBufferedWriter(Writer underlying, int initialSize) {
        super(initialSize);
        this.underlying = underlying;
    }

    public void flushBuffer() throws IOException {
        super.writeTo(underlying);
        super.count = 0;
    }

    public void flush() throws IOException {
        super.writeTo(underlying);
        super.count = 0;
        underlying.flush();
    }

}
