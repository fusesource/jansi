package org.fusesource.jansi;

import java.io.FileDescriptor;
import static org.fusesource.jansi.internal.Kernel32.STD_ERROR_HANDLE;
import static org.fusesource.jansi.internal.Kernel32.STD_OUTPUT_HANDLE;

enum StreamTarget {
    STDOUT(
            1,
            FileDescriptor.out,
            "stdout.encoding",
            "sun.stdout.encoding",
            AnsiConsole.JANSI_OUT_MODE,
            AnsiConsole.JANSI_OUT_COLORS,
            STD_OUTPUT_HANDLE),
    STDERR(
            2,
            FileDescriptor.err,
            "stderr.encoding",
            "sun.stderr.encoding",
            AnsiConsole.JANSI_ERR_MODE,
            AnsiConsole.JANSI_ERR_COLORS,
            STD_ERROR_HANDLE);

    final int fd;
    final FileDescriptor fileDescriptor;
    final String encodingProperty;
    final String legacyEncodingProperty;
    final String modeProperty;
    final String colorsProperty;
    final int stdHandle;

    StreamTarget(
            int fd,
            FileDescriptor fileDescriptor,
            String encodingProperty,
            String legacyEncodingProperty,
            String modeProperty,
            String colorsProperty,
            int stdHandle) {
        this.fd = fd;
        this.fileDescriptor = fileDescriptor;
        this.encodingProperty = encodingProperty;
        this.legacyEncodingProperty = legacyEncodingProperty;
        this.modeProperty = modeProperty;
        this.colorsProperty = colorsProperty;
        this.stdHandle = stdHandle;
    }

    boolean isStdout() {
        return this == STDOUT;
    }
}