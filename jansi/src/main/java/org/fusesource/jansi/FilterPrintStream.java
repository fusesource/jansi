/*
 * Copyright (C) 2009-2017 the original author(s).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fusesource.jansi;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.fusesource.jansi.impl.FlushBufferedWriter;
import org.fusesource.jansi.impl.PrintStreamWriter;

/**
 * A PrintStream filtering to another PrintStream, without making any assumption about encoding.
 * 
 * @author Hervé Boutemy
 * @since 1.17
 * @see #filter(int)
 */
public class FilterPrintStream extends PrintStream
{
    private static final String NEWLINE = System.getProperty("line.separator");

    private static final int TMP_STR_TO_CHAR_BUFFER_LENGTH = 400;

    private static final int BUFFER_LENGTH = 300;

    private final char[] strToCharBuffer = new char[TMP_STR_TO_CHAR_BUFFER_LENGTH];

    protected final FlushBufferedWriter bufferedOut;
    protected final PrintStream underlyingPrintStreamForError;

    public FilterPrintStream(final PrintStream ps)
    {
        super( new OutputStream() {

            @Override
            public void write(int b) throws IOException {
                throw new RuntimeException("Direct OutputStream use forbidden: must go through delegate PrintStream");
            }
            
        });
        PrintStreamWriter psOut = new PrintStreamWriter(ps);
        this.bufferedOut = new FlushBufferedWriter(psOut, BUFFER_LENGTH);
        this.underlyingPrintStreamForError = ps;
    }

    /**
     * Filter the content
     * @param data character to filter
     * @return <code>true</code> if the data is not filtered then has to be printed to delegate PrintStream
     */
    protected boolean filter(char data) throws IOException
    {
        return true;
    }

    @Override
    public void write(int data)
    {
        // TODO this method override OutputStream, so receive byte
        // => should not try to intepret as char !!
        // otherwise get encoding errors..
        try {
            if (filter((char) data))  // HACK should not cast byte -> char !!
            {
                bufferedOut.write(data);
                bufferedOut.flushBuffer();
            }
        } catch (IOException e) {
            setError();
        }
    }

    @Override
    public void write(byte[] buf, int off, int len)
    {
        // TODO this method override OutputStream, so receive byte
        // => should not try to intepret as char !!
        // otherwise get encoding errors..
        try {
            final int max = off + len;
            for (int i = off; i < max; i++)
            {
                byte data = buf[i];
                if (filter((char) data)) { // HACK should not cast byte -> char !!
                    bufferedOut.write(data);
                }
            }
            bufferedOut.flushBuffer();
        } catch (IOException e) {
            setError();
        }
    }

    @Override
    public boolean checkError()
    {
        return super.checkError() || underlyingPrintStreamForError.checkError();
    }

    @Override
    public void close()
    {
        try {
            bufferedOut.close();
        } catch (IOException e) {
            setError();
        }
    }

    @Override
    public void flush()
    {
        try {
            bufferedOut.flush();
        } catch (IOException e) {
            setError();
        }
    }

    private void write(char buf[], int len) {
	try {
            for (char c : buf)
            {
                if (len-- <= 0) {
                    break;
                }
                if (filter(c)) {
                    bufferedOut.write(c);
                }
            }
            bufferedOut.flushBuffer();
        } catch (IOException e) {
            setError();
        }
    }

    private void write(String s) {
        int len = s.length();
        char[] buf = (len < strToCharBuffer.length)? strToCharBuffer : new char[len];
        s.getChars(0, len, buf, 0);
        write(buf, len);
    }

    private void newLine() {
        write(NEWLINE);
    }

    /* Methods that do not terminate lines */

    @Override
    public void print(boolean b) {
        write(b ? "true" : "false");
    }

    @Override
    public void print(char c) {
        // optim for: write(String.valueOf(c));
        try {
            if (filter(c)) {
                bufferedOut.write(c);
            }
            bufferedOut.flushBuffer();
        } catch (IOException e) {
            setError();
        }
    }

    @Override
    public void print(int i) {
        write(String.valueOf(i));
    }

    @Override
    public void print(long l) {
        write(String.valueOf(l));
    }

    @Override
    public void print(float f) {
        write(String.valueOf(f));
    }

    @Override
    public void print(double d) {
        write(String.valueOf(d));
    }

    @Override
    public void print(char s[]) {
        write(s, s.length);
    }

    @Override
    public void print(String s) {
        if (s == null) {
            s = "null";
        }
        write(s);
        flush();
    }

    @Override
    public void print(Object obj) {
        write(String.valueOf(obj));
    }


    /* Methods that do terminate lines */

    @Override
    public void println() {
        newLine();
    }

    @Override
    public void println(boolean x) {
        synchronized (this) {
            print(x);
            newLine();
        }
    }

    @Override
    public void println(char x) {
        synchronized (this) {
            print(x);
            newLine();
        }
    }

    @Override
    public void println(int x) {
        synchronized (this) {
            print(x);
            newLine();
        }
    }

    @Override
    public void println(long x) {
        synchronized (this) {
            print(x);
            newLine();
        }
    }

    @Override
    public void println(float x) {
        synchronized (this) {
            print(x);
            newLine();
        }
    }

    @Override
    public void println(double x) {
        synchronized (this) {
            print(x);
            newLine();
        }
    }

    @Override
    public void println(char x[]) {
        synchronized (this) {
            print(x);
            newLine();
        }
    }

    @Override
    public void println(String x) {
        synchronized (this) {
            print(x);
            newLine();
        }
    }

    @Override
    public void println(Object x) {
        String s = String.valueOf(x);
        synchronized (this) {
            print(s);
            newLine();
        }
    }
}
