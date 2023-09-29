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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MingwSupport {

    private final String STTY_COMMAND;
    private final String TTY_COMMAND;
    private final Pattern[] columnsPatterns;

    public MingwSupport() {
        String tty = null;
        String stty = null;
        String path = System.getenv("PATH");
        if (path != null) {
            String[] paths = path.split(File.pathSeparator);
            for (String p : paths) {
                File ttyFile = new File(p, "tty.exe");
                if (tty == null && ttyFile.canExecute()) {
                    tty = ttyFile.getAbsolutePath();
                }
                File sttyFile = new File(p, "stty.exe");
                if (stty == null && sttyFile.canExecute()) {
                    stty = sttyFile.getAbsolutePath();
                }
            }
        }
        if (tty == null) {
            tty = "tty.exe";
        }
        if (stty == null) {
            stty = "stty.exe";
        }
        TTY_COMMAND = tty;
        STTY_COMMAND = stty;
        // Compute patterns
        columnsPatterns = getPatterns("columns");
    }

    public String getConsoleName(boolean stdout) {
        try {
            Process p = new ProcessBuilder(TTY_COMMAND)
                    .redirectInput(getRedirect(stdout ? FileDescriptor.out : FileDescriptor.err))
                    .start();
            String result = waitAndCapture(p);
            if (p.exitValue() == 0) {
                return result.trim();
            }
        } catch (Throwable t) {
            if ("java.lang.reflect.InaccessibleObjectException"
                    .equals(t.getClass().getName())) {
                System.err.println("MINGW support requires --add-opens java.base/java.lang=ALL-UNNAMED");
            }
            // ignore
        }
        return null;
    }

    public int getTerminalWidth(String name) {
        try {
            Process p = new ProcessBuilder(STTY_COMMAND, "-F", name, "-a").start();
            String result = waitAndCapture(p);
            if (p.exitValue() != 0) {
                throw new IOException("Error executing '" + STTY_COMMAND + "': " + result);
            }
            for (Pattern pattern : columnsPatterns) {
                Matcher matcher = pattern.matcher(result);
                if (matcher.find()) {
                    return Integer.parseInt(matcher.group(1));
                }
            }
            throw new IOException("Unable to parse columns");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Pattern[] getPatterns(String name) {
        return new Pattern[] {
            Pattern.compile("\\b(\\d+)\\s+" + name + "\\b"),
            Pattern.compile("\\b" + name + "\\s+(\\d+)\\b"),
            Pattern.compile("\\b" + name + "\\s*=\\s*(\\d+)\\b")
        };
    }

    private static String waitAndCapture(Process p) throws IOException, InterruptedException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try (InputStream in = p.getInputStream();
                InputStream err = p.getErrorStream();
                OutputStream out = p.getOutputStream()) {
            int c;
            while ((c = in.read()) != -1) {
                bout.write(c);
            }
            while ((c = err.read()) != -1) {
                bout.write(c);
            }
            p.waitFor();
        }

        return bout.toString();
    }

    /**
     * This requires --add-opens java.base/java.lang=ALL-UNNAMED
     */
    private ProcessBuilder.Redirect getRedirect(FileDescriptor fd) throws ReflectiveOperationException {
        // This is not really allowed, but this is the only way to redirect the output or error stream
        // to the input.  This is definitely not something you'd usually want to do, but in the case of
        // the `tty` utility, it provides a way to get
        Class<?> rpi = Class.forName("java.lang.ProcessBuilder$RedirectPipeImpl");
        Constructor<?> cns = rpi.getDeclaredConstructor();
        cns.setAccessible(true);
        ProcessBuilder.Redirect input = (ProcessBuilder.Redirect) cns.newInstance();
        Field f = rpi.getDeclaredField("fd");
        f.setAccessible(true);
        f.set(input, fd);
        return input;
    }
}
