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
package org.fusesource.jansi.impl;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;

/**
 * A PrintStream implementation
 * for safely printing/formatting (without throws-checked IOException)
 * and delegating to an underlying java.io.Writer,
 * instead of interpreting Charset to send to an OutputStream.
 *
 * It extends java.io.PrintStream, for compatibility type re-assignment, but override most methods.
 *
 * All character-based methods are internally delegated to the Writer,
 * whereas byte-based methods are by-passing directly to the underlying Writer-OutputStream directly.
 *
 * This class is threadsafe (all public methods are synchronized)
 *
 */
public class WriterPrintStream extends PrintStream
{
    private static final char[] NEWLINE = System.getProperty("line.separator").toCharArray();

    private static final int TMP_STR_TO_CHAR_BUFFER_LENGTH = 400;

    protected final WriterOrDirectOutputStream underlyingWriterOrOutput;
    protected final OutputStream underlyingOutputStream; // helper for = underlyingWriterOrOutput.getWriter();
    protected final Writer underlyingWriter; // helper for = underlyingWriterOrOutput.getWriter();

    protected final boolean autoFlush;
    // protected final boolean autoFlushNewLine;

    private final char[] strToCharBuffer = new char[TMP_STR_TO_CHAR_BUFFER_LENGTH];

    public WriterPrintStream(
            WriterOrDirectOutputStream underlyingWriterOrOutput,
            boolean autoFlush)
    {
        super( new OutputStream() {

            @Override
            public void write(int b) throws IOException {
                throw new RuntimeException("Direct OutputStream use forbidden: must go through delegate PrintStream");
            }

        });
        this.underlyingWriterOrOutput = underlyingWriterOrOutput;
        this.underlyingWriter = underlyingWriterOrOutput.getWriter();
        this.underlyingOutputStream = underlyingWriterOrOutput.getOutputStream();
        this.autoFlush = autoFlush;
    }

    @Override
    public boolean checkError()
    {
        synchronized (this) {
            return super.checkError();
        }
    }

    @Override
    public void close()
    {
        super.close();
        try {
            underlyingWriter.close();
        } catch (IOException e) {
            super.setError();
        }
    }

    @Override
    public void flush()
    {
        synchronized (this) {
            super.flush();
            try {
                underlyingWriterOrOutput.flush();
            } catch (IOException e) {
                super.setError();
            }
        }
    }

    // Part to override OutputStream methods on byte writing
    // ------------------------------------------------------------------------
    
    /**
     * PrintStream extends OutputStream => accept low-level byte writing
     * Assuming content is not char, but byte??
     */
    @Override
    public void write(int byteData)
    {
        synchronized (this) {
//            ensureOpen();
            try {
                underlyingWriterOrOutput.flushWriterBuffer();
                underlyingOutputStream.write(byteData);
            }
            catch (InterruptedIOException x) {
                Thread.currentThread().interrupt();
            }
            catch (IOException x) {
                setError();
            }
            
        }
    }

    /**
     * PrintStream extends OutputStream => accept low-level byte writing
     * Assuming content is not chars, but byte??
     */
    @Override
    public void write(byte[] buf, int off, int len)
    {
        synchronized (this) {
//                ensureOpen();
            try {
                underlyingWriterOrOutput.flushWriterBuffer();
                underlyingOutputStream.write(buf, off, len);
            }
            catch (InterruptedIOException x) {
                Thread.currentThread().interrupt();
            }
            catch (IOException x) {
                setError();
            }
        }
    }

    // override PrintStream write(*) methods on char[],String writing
    // ------------------------------------------------------------------------

    private void write(char cbuf[]) {
        try {
            underlyingWriter.write(cbuf);
            
            if (autoFlush) {
                boolean hasNewLine = cbuf[cbuf.length-1] == '\n';
                if (!hasNewLine) {
                    for (int i = 0; i < cbuf.length; i++) {
                        if (cbuf[i] == '\n') {
                            hasNewLine = true;
                            break;
                        }
                    }
                }
                if (hasNewLine) {
                    out.flush();
                }
            }
        } catch (IOException e) {
            super.setError();
        }
    }

    private void write(String s) {
        try {
            char[] buf = (s.length() < strToCharBuffer.length)? strToCharBuffer : new char[s.length()];
            s.getChars(0, s.length(), buf, 0);
            write(buf);

            if (autoFlush && (s.endsWith("\n") || s.indexOf('\n') >= 0)) {
              out.flush();
            }
        }
        catch (InterruptedIOException x) {
            Thread.currentThread().interrupt();
        }
        catch (IOException x) {
            super.setError();
        }
    }

    private void newLine() {
        write(NEWLINE);
    }

    // override PrintStream print(*) methods for formatting basic types to String, then write
    // ------------------------------------------------------------------------

    @Override
    public void print(boolean b) {
        synchronized (this) {
            write(b ? "true" : "false");
        }
    }

    @Override
    public void print(char c) {
        synchronized (this) {
            try {
                underlyingWriter.append(c);
            } catch (InterruptedIOException x) {
                Thread.currentThread().interrupt();
            }
            catch (IOException x) {
                super.setError();
            }
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


    // override PrintStream println(*) methods
    // ------------------------------------------------------------------------

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

}