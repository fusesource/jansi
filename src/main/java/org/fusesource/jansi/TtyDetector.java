package org.fusesource.jansi;

import java.util.logging.Logger;
import static org.fusesource.jansi.internal.CLibrary.isatty;

final class TtyDetector {

    private static final Logger LOG = Logger.getLogger(TtyDetector.class.getName());

    private TtyDetector() {}

    static TtyResult detectTty(int fd) {
        try {
            boolean isAtty = isatty(fd) != 0;
            isAtty = adjustForDumbTerminal(isAtty);
            return new TtyResult(isAtty, false);
        } catch (Throwable e) {
            LOG.fine("JNI TTY detection failed for fd=" + fd + ", falling back to non-TTY: " + e);
            return new TtyResult(false, true);
        }
    }

    private static boolean adjustForDumbTerminal(boolean isAtty) {
        if (!isAtty) {
            return false;
        }
        String term = System.getenv("TERM");
        String emacs = System.getenv("INSIDE_EMACS");
        if ("dumb".equals(term) && emacs != null && !emacs.contains("comint")) {
            return false;
        }
        return true;
    }
}