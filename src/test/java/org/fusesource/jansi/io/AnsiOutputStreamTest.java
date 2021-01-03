package org.fusesource.jansi.io;

import org.fusesource.jansi.AnsiColors;
import org.fusesource.jansi.AnsiMode;
import org.fusesource.jansi.AnsiType;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AnsiOutputStreamTest {
    @Test
    void canHandleSgrsWithMultipleOptions() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final AnsiOutputStream ansiOutput = new AnsiOutputStream(baos, AnsiMode.Strip, null, AnsiType.Emulation, AnsiColors.TrueColor, Charset.forName("UTF-8"), null, null, false);
        ansiOutput.write("\u001B[33mbanana_1  |\u001B[0m 19:59:14.353\u001B[0;38m [debug] Lager installed handler {lager_file_backend,\"banana.log\"} into lager_event\u001B[0m\n".getBytes());
        assertEquals("banana_1  | 19:59:14.353 [debug] Lager installed handler {lager_file_backend,\"banana.log\"} into lager_event\n", baos.toString());
    }
}
