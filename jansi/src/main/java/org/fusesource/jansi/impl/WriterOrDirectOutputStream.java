package org.fusesource.jansi.impl;

import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

/**
 * helper class to hold both a (char[]) Writer 
 * and the underlying (byte[]) OutputStream
 *
 * Notice that the Writer contains internally a buffer, that need to be flushed
 * when switching from OutputStream / Writer
 * 
 * <PRE>
 * OutputSteam       +--------------+
 *    byte[] =>    --| ----------\  |    OutputStream
 * Writer            |            +-| ==>  byte[]
 *    char[] =>    --| - to byte /  |
 *                   +--------------+
 * </PRE>
 */
public class WriterOrDirectOutputStream extends FilterOutputStream {

    private final Writer writer;
    private final SupportFlushBuffer[] flushChain;

    public static interface SupportFlushBuffer {
        public void flushBuffer() throws IOException;
    }

    public WriterOrDirectOutputStream(
            OutputStream underlyingOutputStream,
            Charset charset) {
        super(underlyingOutputStream);
        OutputStreamWriter2 streamWriter =
                new OutputStreamWriter2(underlyingOutputStream, charset);
        this.writer = streamWriter;
        this.flushChain = new SupportFlushBuffer[] { streamWriter }; 
    }

    private WriterOrDirectOutputStream(OutputStream out, Writer writer, SupportFlushBuffer[] flushChain) {
        super(out);
        this.writer = writer;
        this.flushChain = flushChain;
    }

    public static interface WriterWrapper { // java8: Function<Writer,Writer>
        public Writer wrap(Writer writerToFilter);
    }

    public static WriterOrDirectOutputStream wrapWriterChain(
            WriterOrDirectOutputStream src,
            WriterWrapper writerWrapper
            ) {
        Writer newWriter = writerWrapper.wrap(src.getWriter());
        if (newWriter == null || newWriter == src.getWriter()) {
            return src; // not wrapped
        }
        SupportFlushBuffer[] newFlushChain = src.flushChain;
        if (newWriter instanceof SupportFlushBuffer) {
            newFlushChain = new SupportFlushBuffer[src.flushChain.length+1];
            newFlushChain[0] = (SupportFlushBuffer) newWriter;
            System.arraycopy(src.flushChain, 0, newFlushChain, 1, src.flushChain.length);
        }
        return new WriterOrDirectOutputStream(src.out, newWriter, newFlushChain);
    }

    /**
     * helper for <code>new + wrapWriterChain()</code>
     * <PRE>
     * OutputSteam       +-------------------------+
     *    byte[] =>    --|----------------------\  |    OutputStream
     * Writer            |                       +-| ==>  byte[]
     *    char[] =>    --|-WriterFilter- to byte / |
     *                   +-------------------------+
     *                          /\
     *                   create  |
     *                       WriterWrapper
     * </PRE>
     */
    public static WriterOrDirectOutputStream createAndWrap(
            OutputStream underlyingOutputStream,
            Charset charset,
            WriterWrapper writerWrapper) {
        WriterOrDirectOutputStream tmp =
                new WriterOrDirectOutputStream(underlyingOutputStream, charset);
        return wrapWriterChain(tmp, writerWrapper);
    }

    public OutputStream getOutputStream() {
        // flushBuffer(); .. should be called from caller before using outputStream
        return super.out;
    }

    public Writer getWriter() {
        return writer;
    }

    @Override
    public void close() throws IOException {
        flushWriterBuffer();
        super.close();
    }

    @Override
    public void flush() throws IOException {
        flushWriterBuffer();
        super.flush();
    }

    public void flushWriterBuffer() throws IOException {
        for(SupportFlushBuffer e : flushChain) {
            e.flushBuffer();
        }
    }

    // ------------------------------------------------------------------------
    
    /**
     * Adapted from java.io.OutputStreamWriter
     * modified for public 
     */
    private static class OutputStreamWriter2 extends Writer implements SupportFlushBuffer {

        private final StreamEncoder2 se;

        public OutputStreamWriter2(OutputStream out, Charset charset) {
            super(out);
            se = StreamEncoder2.forOutputStreamWriter(out, this, charset);
        }
    
        /**
         * Flushes the output buffer to the underlying byte stream, 
         * without flushing the byte stream itself. 
         */
        public void flushBuffer() throws IOException {
            se.flushBuffer();
        }

        @Override
        public void write(int c) throws IOException {
            se.write(c);
            se.flushBuffer(); // ??
        }

        @Override
        public void write(char cbuf[], int off, int len) throws IOException {
            se.write(cbuf, off, len);
            se.flushBuffer(); // ??
        }
    
        @Override
        public void write(String str, int off, int len) throws IOException {
            se.write(str, off, len);
            se.flushBuffer(); // ??
        }
    
        @Override
        public void flush() throws IOException {
            se.flush();
        }
    
        @Override
        public void close() throws IOException {
            se.close();
        }
    }

    // ------------------------------------------------------------------------
    
    /**
     * Adapted from sun/nio/cs/StreamEncoder
     * modified for removal of synchronized lock, isOpen checks 
     *
     */
    private static class StreamEncoder2 extends Writer
    {

        private static final int DEFAULT_BYTE_BUFFER_SIZE = 8192;

        // Factories for java.io.OutputStreamWriter
        public static StreamEncoder2 forOutputStreamWriter(OutputStream out,
                                                          Object lock,
                                                          Charset charset) {
            return new StreamEncoder2(out, lock, charset);
        }


        // -- Public methods corresponding to those in OutputStreamWriter --

        // All synchronization and state/argument checking is done in these public
        // methods; the concrete stream-encoder subclasses defined below need not
        // do any such checking.

        public void flushBuffer() throws IOException {
            implFlushBuffer();
        }

        public void write(int c) throws IOException {
            char cbuf[] = new char[1];
            cbuf[0] = (char) c;
            write(cbuf, 0, 1);
        }

        public void write(char cbuf[], int off, int len) throws IOException {
            if ((off < 0) || (off > cbuf.length) || (len < 0) ||
                ((off + len) > cbuf.length) || ((off + len) < 0)) {
                throw new IndexOutOfBoundsException();
            } else if (len == 0) {
                return;
            }
            implWrite(cbuf, off, len);
        }

        public void write(String str, int off, int len) throws IOException {
            /* Check the len before creating a char buffer */
            if (len < 0)
                throw new IndexOutOfBoundsException();
            char cbuf[] = new char[len];
            str.getChars(off, off + len, cbuf, 0);
            write(cbuf, 0, len);
        }

        public void flush() throws IOException {
            implFlush();
        }

        public void close() throws IOException {
            implClose();
        }


        // -- Charset-based stream encoder impl --

        private Charset cs;
        private CharsetEncoder encoder;
        private ByteBuffer bb;

        // Exactly one of these is non-null
        private final OutputStream out;
        private WritableByteChannel ch;

        // Leftover first char in a surrogate pair
        private boolean haveLeftoverChar = false;
        private char leftoverChar;
        private CharBuffer lcb = null;

        private StreamEncoder2(OutputStream out, Object lock, Charset cs) {
            this(out, lock,
             cs.newEncoder()
             .onMalformedInput(CodingErrorAction.REPLACE)
             .onUnmappableCharacter(CodingErrorAction.REPLACE));
        }

        private StreamEncoder2(OutputStream out, Object lock, CharsetEncoder enc) {
            super(lock);
            this.out = out;
            this.ch = null;
            this.cs = enc.charset();
            this.encoder = enc;

            // This path disabled until direct buffers are faster
            if (false && out instanceof FileOutputStream) {
                    ch = ((FileOutputStream)out).getChannel();
            if (ch != null)
                        bb = ByteBuffer.allocateDirect(DEFAULT_BYTE_BUFFER_SIZE);
            }
                if (ch == null) {
            bb = ByteBuffer.allocate(DEFAULT_BYTE_BUFFER_SIZE);
            }
        }

        private StreamEncoder2(WritableByteChannel ch, CharsetEncoder enc, int mbc) {
            this.out = null;
            this.ch = ch;
            this.cs = enc.charset();
            this.encoder = enc;
            this.bb = ByteBuffer.allocate(mbc < 0
                                      ? DEFAULT_BYTE_BUFFER_SIZE
                                      : mbc);
        }

        private void writeBytes() throws IOException {
            bb.flip();
            int lim = bb.limit();
            int pos = bb.position();
            assert (pos <= lim);
            int rem = (pos <= lim ? lim - pos : 0);

                if (rem > 0) {
            if (ch != null) {
                if (ch.write(bb) != rem)
                    assert false : rem;
            } else {
                out.write(bb.array(), bb.arrayOffset() + pos, rem);
            }
            }
            bb.clear();
            }

        private void flushLeftoverChar(CharBuffer cb, boolean endOfInput)
            throws IOException
        {
            if (!haveLeftoverChar && !endOfInput)
                return;
            if (lcb == null)
                lcb = CharBuffer.allocate(2);
            else
                lcb.clear();
            if (haveLeftoverChar)
                lcb.put(leftoverChar);
            if ((cb != null) && cb.hasRemaining())
                lcb.put(cb.get());
            lcb.flip();
            while (lcb.hasRemaining() || endOfInput) {
                CoderResult cr = encoder.encode(lcb, bb, endOfInput);
                if (cr.isUnderflow()) {
                    if (lcb.hasRemaining()) {
                        leftoverChar = lcb.get();
                        if (cb != null && cb.hasRemaining())
                            flushLeftoverChar(cb, endOfInput);
                        return;
                    }
                    break;
                }
                if (cr.isOverflow()) {
                    assert bb.position() > 0;
                    writeBytes();
                    continue;
                }
                cr.throwException();
            }
            haveLeftoverChar = false;
        }

        void implWrite(char cbuf[], int off, int len)
            throws IOException
        {
            CharBuffer cb = CharBuffer.wrap(cbuf, off, len);

            if (haveLeftoverChar)
            flushLeftoverChar(cb, false);

            while (cb.hasRemaining()) {
            CoderResult cr = encoder.encode(cb, bb, false);
            if (cr.isUnderflow()) {
               assert (cb.remaining() <= 1) : cb.remaining();
               if (cb.remaining() == 1) {
                    haveLeftoverChar = true;
                    leftoverChar = cb.get();
                }
                break;
            }
            if (cr.isOverflow()) {
                assert bb.position() > 0;
                writeBytes();
                continue;
            }
            cr.throwException();
            }
        }

        void implFlushBuffer() throws IOException {
            if (bb.position() > 0)
            writeBytes();
        }

        void implFlush() throws IOException {
            implFlushBuffer();
            if (out != null)
            out.flush();
        }

        void implClose() throws IOException {
            flushLeftoverChar(null, true);
            try {
                for (;;) {
                    CoderResult cr = encoder.flush(bb);
                    if (cr.isUnderflow())
                        break;
                    if (cr.isOverflow()) {
                        assert bb.position() > 0;
                        writeBytes();
                        continue;
                    }
                    cr.throwException();
                }

                if (bb.position() > 0)
                    writeBytes();
                if (ch != null)
                    ch.close();
                else
                    out.close();
            } catch (IOException x) {
                encoder.reset();
                throw x;
            }
        }

    }

}
