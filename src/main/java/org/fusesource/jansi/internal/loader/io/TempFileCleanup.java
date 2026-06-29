package org.fusesource.jansi.internal.loader.io;

import java.io.File;
import java.util.logging.Logger;

public class TempFileCleanup {

    private static final Logger LOG = Logger.getLogger(TempFileCleanup.class.getName());

    private TempFileCleanup() {}

    public static void cleanup(File tempDir, String version) {
        final String searchPattern = "jansi-" + version;

        File[] staleLibFiles = tempDir.listFiles(
                (dir, name) -> name.startsWith(searchPattern) && !name.endsWith(".lck"));

        if (staleLibFiles == null) {
            return;
        }

        for (File staleLib : staleLibFiles) {
            File lockFile = new File(staleLib.getAbsolutePath() + ".lck");
            if (!lockFile.exists()) {
                try {
                    staleLib.delete();
                } catch (SecurityException e) {
                    LOG.fine("Could not delete stale native lib "
                            + staleLib.getName() + ": " + e.getMessage());
                }
            }
        }
    }
}