package org.fusesource.jansi;

final class TtyResult {
    final boolean isAtty;
    final boolean hadException;

    TtyResult(boolean isAtty, boolean hadException) {
        this.isAtty = isAtty;
        this.hadException = hadException;
    }
}