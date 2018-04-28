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
 * @author Hervé Boutemy
 * @since 1.17
 * @see #filter(int)
 */
public class FilterPrintStream extends PrintStream
{
    private static final String NEWLINE = System.getProperty("line.separator");
    protected final PrintStream ps;

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

    @Override
    public void write(int data)
    {
        if (filter(data))
        {
            ps.write(data);
        }
    }

    @Override
    public void write(byte[] buf, int off, int len)
    {
        for (int i = 0; i < len; i++)
        {
            write(buf[off + i]);
        }
    }

    @Override
    public boolean checkError()
    {
        return super.checkError() || ps.checkError();
    }

    @Override
    public void close()
    {
        super.close();
        ps.close();
    }

    @Override
    public void flush()
    {
        super.flush();
        ps.flush();
    }

    private void write(char buf[]) {
        for (char c : buf)
        {
            if (filter(c))
            {
                ps.print(c);
            }
        }
    }

    private void write(String s) {
        char[] buf = new char[s.length()];
        s.getChars(0, s.length(), buf, 0);
        write(buf);
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
        write(String.valueOf(c));
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
        write(s);
    }

    @Override
    public void print(String s) {
        if (s == null) {
            s = "null";
        }
        write(s);
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
