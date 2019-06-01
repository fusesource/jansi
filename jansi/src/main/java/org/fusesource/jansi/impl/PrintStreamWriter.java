package org.fusesource.jansi.impl;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;

/**
 * bridge from java.io.Writer to java.io.PrintStream
 */
public class PrintStreamWriter extends Writer {

    private final PrintStream out;

    public PrintStreamWriter(PrintStream out) {
        super();
        this.out = out;
    }

    @Override
    public void write(int c) throws IOException {
        out.write(c);
    }

    @Override
    public void write(char[] cbuf) throws IOException {
        out.print(cbuf);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        if (off == 0 && cbuf.length == len) {
            out.print(cbuf);
        } else {
            char[] copy = new char[len];
            System.arraycopy(cbuf, off, copy, 0, len);
            out.print(copy);
        }
    }

    @Override
    public void write(String str) throws IOException {
        out.print(str);
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }

}
