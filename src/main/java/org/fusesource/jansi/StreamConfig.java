package org.fusesource.jansi;

import org.fusesource.jansi.io.AnsiOutputStream;
import org.fusesource.jansi.io.AnsiProcessor;

final class StreamConfig {
    final AnsiProcessor processor;
    final AnsiType type;
    final AnsiOutputStream.IoRunnable installer;
    final AnsiOutputStream.IoRunnable uninstaller;
    final AnsiOutputStream.WidthSupplier width;

    StreamConfig(
            AnsiProcessor processor,
            AnsiType type,
            AnsiOutputStream.IoRunnable installer,
            AnsiOutputStream.IoRunnable uninstaller,
            AnsiOutputStream.WidthSupplier width) {
        this.processor = processor;
        this.type = type;
        this.installer = installer;
        this.uninstaller = uninstaller;
        this.width = width;
    }
}