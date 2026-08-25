/*
 * Copyright (C) 2009-2023 the original author(s).
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
package org.fusesource.jansi.internal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JansiLoaderTest {

    @Test
    public void testLoadJansi() {
        JansiLoader.initialize();
    }

    @Test
    public void cleanupDeletesStaleJansiLibsWithoutListingAllTempEntries(@TempDir Path tempDir) throws IOException {
        String version = JansiLoader.getVersion();
        String prefix = "jansi-" + version + "-";

        // Decoys that must survive cleanup (would force a full temp-dir listing with listFiles).
        for (int i = 0; i < 1000; i++) {
            Files.createFile(tempDir.resolve("unrelated-entry-" + i));
        }

        Path staleLib = Files.createFile(tempDir.resolve(prefix + "deadbeef-lib.so"));
        Path lockedLib = Files.createFile(tempDir.resolve(prefix + "cafebabe-lib.so"));
        Files.createFile(tempDir.resolve(lockedLib.getFileName().toString() + ".lck"));

        String previousTmpDir = System.setProperty("jansi.tmpdir", tempDir.toString());
        try {
            JansiLoader.cleanup();
        } finally {
            restoreProperty("jansi.tmpdir", previousTmpDir);
        }

        assertFalse(Files.exists(staleLib), "stale jansi lib without lock file should be removed");
        assertTrue(Files.exists(lockedLib), "jansi lib with lock file should be kept");
        assertTrue(Files.exists(tempDir.resolve("unrelated-entry-0")), "non-jansi temp entries must be untouched");
    }

    private static void restoreProperty(String key, String previousValue) {
        if (previousValue == null) {
            System.clearProperty(key);
        } else {
            System.setProperty(key, previousValue);
        }
    }
}
