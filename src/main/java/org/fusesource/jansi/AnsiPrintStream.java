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
package org.fusesource.jansi;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.fusesource.jansi.io.AnsiOutputStream;

/**
 * Simple PrintStream holding an AnsiOutputStream.
 * This allows changing the mode in which the underlying AnsiOutputStream operates.
 */
public class AnsiPrintStream extends PrintStream {

    public AnsiPrintStream(AnsiOutputStream out, boolean autoFlush) {
        super(out, autoFlush);
    }

    public AnsiPrintStream(AnsiOutputStream out, boolean autoFlush, String encoding)
            throws UnsupportedEncodingException {
        super(out, autoFlush, encoding);
    }

    protected AnsiOutputStream getOut() {
        return (AnsiOutputStream) out;
    }

    public AnsiType getType() {
        return getOut().getType();
    }

    public AnsiColors getColors() {
        return getOut().getColors();
    }

    public AnsiMode getMode() {
        return getOut().getMode();
    }

    public void setMode(AnsiMode ansiMode) {
        getOut().setMode(ansiMode);
    }

    public boolean isResetAtUninstall() {
        return getOut().isResetAtUninstall();
    }

    public void setResetAtUninstall(boolean resetAtClose) {
        getOut().setResetAtUninstall(resetAtClose);
    }

    /**
     * Returns the width of the terminal associated with this stream or 0.
     * @since 2.2
     */
    public int getTerminalWidth() {
        return getOut().getTerminalWidth();
    }

    public void install() throws IOException {
        getOut().install();
    }

    public void uninstall() throws IOException {
        // If the system output stream has been closed, out should be null, so avoid a NPE
        AnsiOutputStream out = getOut();
        if (out != null) {
            out.uninstall();
        }
    }

    @Override
    public String toString() {
        return "AnsiPrintStream{"
                + "type=" + getType()
                + ", colors=" + getColors()
                + ", mode=" + getMode()
                + ", resetAtUninstall=" + isResetAtUninstall()
                + "}";
    }
}
