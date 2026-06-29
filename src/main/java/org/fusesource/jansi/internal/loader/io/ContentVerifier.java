package org.fusesource.jansi.internal.loader.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class ContentVerifier {

    private static final int BUFFER_SIZE = 8192;

    private ContentVerifier() {}

    public static void verifyExtractedContents(
            InputStream jarStream,
            File extractedLibFile) throws IOException {

        if (jarStream == null) {
            throw new IOException("Original resource stream is null.");
        }

        try (InputStream fileStream = new FileInputStream(extractedLibFile)) {
            String mismatch = streamContentsEqual(jarStream, fileStream);
            if (mismatch != null) {
                throw new RuntimeException(
                        "Native library extraction verification failed for "
                                + extractedLibFile + ": " + mismatch);
            }
        }
    }

    private static String streamContentsEqual(InputStream s1, InputStream s2) throws IOException {
        byte[] buf1 = new byte[BUFFER_SIZE];
        byte[] buf2 = new byte[BUFFER_SIZE];
        while (true) {
            int n1 = readFully(s1, buf1);
            int n2 = readFully(s2, buf2);

            if (n1 > 0 && n2 <= 0) return "EOF on second stream but not first";
            if (n1 <= 0 && n2 > 0) return "EOF on first stream but not second";
            if (n1 <= 0) return null;
            if (n1 != n2) return "Read size different (" + n1 + " vs " + n2 + ")";
            if (!Arrays.equals(buf1, 0, n1, buf2, 0, n2)) return "Content differs";
        }
    }

    private static int readFully(InputStream in, byte[] buf) throws IOException {
        int total = 0;
        int remaining = buf.length;
        while (remaining > 0) {
            int n = in.read(buf, total, remaining);
            if (n <= 0) break;
            total += n;
            remaining -= n;
        }
        return total;
    }
}