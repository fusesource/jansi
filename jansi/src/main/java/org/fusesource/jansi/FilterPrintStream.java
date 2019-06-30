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

/**
 * A PrintStream filtering to another PrintStream, without making any assumption about encoding.
 * 
 * This class is threadsafe (all public methods are synchronized)
 * @author Hervé Boutemy
 * @since 1.17
 * @see #filter(int)
 */
public class FilterPrintStream extends PrintStream
{
    private static final String NEWLINE = System.getProperty("line.separator");

    private static final int TMP_STR_TO_CHAR_BUFFER_LENGTH = 400;

    protected final PrintStream ps;

    private final char[] strToCharBuffer = new char[TMP_STR_TO_CHAR_BUFFER_LENGTH];

    public FilterPrintStream(PrintStream ps)
    {
        super( new OutputStream() {

            @Override
            public void write(int b) throws IOException {
                throw new RuntimeException("Direct OutputStream use forbidden: must go through delegate PrintStream");
            }
            
        });
        this.ps = ps;
    }

    /**
     * Filter the content
     * @param data character to filter
     * @return <code>true</code> if the data is not filtered then has to be printed to delegate PrintStream
     */
    protected boolean filter(int data)
    {
        return true;
    }

    protected void doWrite(int data) {
        if (filter(data))
        {
            ps.write(data);
        }
    }

    @Override
    public void write(int data)
    {
        synchronized (this) {
            doWrite(data);
        }
    }

    @Override
    public void write(byte[] buf, int off, int len)
    {
        synchronized (this) {
            for (int i = off, max=off + len; i < max; i++) {
                doWrite(buf[i]);
            }
        }
    }

    @Override
    public boolean checkError()
    {
        synchronized (this) {
            return super.checkError() || ps.checkError();
        }
    }

    @Override
    public void close()
    {
        synchronized (this) {
            super.close();
            ps.close();
        }
    }

    @Override
    public void flush()
    {
        synchronized (this) {
            super.flush();
            ps.flush();
        }
    }

    private void write(char buf[], int len) {
        for (char c : buf)
        {
            if (len-- <= 0) {
                return;
            }
            if (filter(c)) {
                ps.print(c);
            }
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
        synchronized (this) {
            write(b ? "true" : "false");
        }
    }

    @Override
    public void print(char c) {
        synchronized (this) {
            // was char->String->char !
            // write(String.valueOf(c));
            doWrite(c);
        }
    }

    @Override
    public void print(int i) {
        String text = String.valueOf(i);
        synchronized (this) {
            write(text);
        }
    }

    @Override
    public void print(long l) {
        String text = String.valueOf(l);
        synchronized (this) {
            write(text);
        }
    }

    @Override
    public void print(float f) {
        String text = String.valueOf(f);
        synchronized (this) {
            write(text);
        }
    }

    @Override
    public void print(double d) {
        String text = String.valueOf(d);
        synchronized (this) {
            write(text);
        }
    }

    @Override
    public void print(char s[]) {
        synchronized (this) {
            write(s);
        }
    }

    @Override
    public void print(String s) {
        if (s == null) {
            s = "null";
        }
        synchronized (this) {
            write(s);
        }
    }

    @Override
    public void print(Object obj) {
        String text = String.valueOf(obj);
        synchronized (this) {
            write(text);
        }
    }


    /* Methods that do terminate lines */

    @Override
    public void println() {
        synchronized (this) {
            newLine();
        }
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

    @Override
    public PrintStream append(CharSequence csq, int start, int end) {
        String text = (csq != null)? csq.subSequence(start, end).toString() : "null";
        synchronized (this) {
            write(text);
        }
        return this;
    }

}