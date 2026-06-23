package org.fusesource.jansi.internal.loader.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LibraryExtractor {

    private static final int BUFFER_SIZE = 8192;

    private LibraryExtractor() {}

    public static void extractLibraryToFile(
            InputStream jarStream,
            File extractedLibFile,
            File extractedLckFile) throws IOException {

        if (jarStream == null) {
            throw new IOException("Resource stream is null.");
        }

        try {
            if (!extractedLckFile.exists()) {
                new FileOutputStream(extractedLckFile).close();
            }
            try (OutputStream out = new FileOutputStream(extractedLibFile)) {
                copy(jarStream, out);
            }
        } finally {
            extractedLibFile.deleteOnExit();
            extractedLckFile.deleteOnExit();
        }
    }

    public static void setExecutablePermissions(File libFile) {
        libFile.setReadable(true);
        libFile.setWritable(true);
        libFile.setExecutable(true);
    }

    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[BUFFER_SIZE];
        int n;
        while ((n = in.read(buf)) > 0) {
            out.write(buf, 0, n);
        }
    }
}