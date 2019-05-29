package org.fusesource.jansi.impl;

import java.io.IOException;
import java.io.Writer;

public abstract class TerminalType {

    public abstract TerminalCommandProcessor create(Writer output) throws IOException;

    public static final TerminalType DEFAULT = new TerminalType() {
        @Override
        public TerminalCommandProcessor create(Writer output)  throws IOException {
            return new TerminalCommandProcessor(output);
        }
    };

    public static TerminalType windows(boolean stdout) {
        return (stdout)? WINDOWS_STDOUT : WINDOWS_STDERR;
    }

    public static final TerminalType WINDOWS_STDOUT = new TerminalType() {
        @Override
        public TerminalCommandProcessor create(Writer output)  throws IOException {
            return new WindowsTerminalCommandProcessor(output, true);
        }
    };
    public static final TerminalType WINDOWS_STDERR = new TerminalType() {
        @Override
        public TerminalCommandProcessor create(Writer output)  throws IOException {
            return new WindowsTerminalCommandProcessor(output, false);
        }
    };

}
