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
package org.fusesource.jansi.internal.stty;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fusesource.jansi.internal.OSInfo;

/**
 * Support for POSIX-compliant or MinGW terminals.
 * <p>
 * MinGW terminals do not use the underlying Windows terminal, and there's no CLibrary available
 * in the environments. We have to rely on calling {@code stty} and {@code tty} to
 * obtain the terminal name and width.
 */
public final class Stty {

    private static final String TTY_COMMAND;
    private static final String STTY_COMMAND;
    private static final String STTY_F_COMMAND;
    private static final boolean FOUND_STTY;
    private static final Pattern COLUMNS_PATTERN = Pattern.compile("\\d+ (\\d+)");

    static {
        boolean isWindows = OSInfo.isWindows();

        String tty = isWindows ? "tty.exe" : "tty";
        String stty = isWindows ? "stty.exe" : "stty";
        String path = System.getenv("PATH");
        boolean foundStty = false;
        if (path != null) {
            String[] paths = path.split(File.pathSeparator);

            for (String p : paths) {
                File ttyFile = new File(p, tty);
                if (ttyFile.canExecute()) {
                    tty = ttyFile.getAbsolutePath();
                    break;
                }
            }

            for (String p : paths) {
                File sttyFile = new File(p, stty);
                if (sttyFile.canExecute()) {
                    stty = sttyFile.getAbsolutePath();
                    foundStty = true;
                    break;
                }
            }
        }

        TTY_COMMAND = tty;
        STTY_COMMAND = stty;
        STTY_F_COMMAND = OSInfo.isMacOS() ? "-f" : "-F";
        FOUND_STTY = foundStty;
    }

    public static boolean isFoundStty() {
        return FOUND_STTY;
    }

    public static String getConsoleName(boolean stdout) {
        try {
            Process p = new ProcessBuilder(TTY_COMMAND)
                    .redirectInput(getRedirect(stdout ? FileDescriptor.out : FileDescriptor.err))
                    .start();
            String result = waitAndCapture(p);
            if (p.exitValue() == 0) {
                return result.trim();
            }
        } catch (Throwable t) {
            if (!OSInfo.isWindows() && !OSInfo.isMacOS()) {
                Path fd = Paths.get("/proc/self/fd/" + (stdout ? "1" : "2"));

                try {
                    Path target = Files.readSymbolicLink(fd);
                    String targetName = target.toString();
                    if (targetName.startsWith("/dev/tty") || targetName.startsWith("/dev/pts/")) {
                        return targetName;
                    } else if (targetName.startsWith("pipe:")
                            || targetName.equals("/dev/null")
                            || Files.isRegularFile(target)) {
                        return null;
                    }
                } catch (Throwable ignored) {
                }
            }

            if ("java.lang.reflect.InaccessibleObjectException"
                    .equals(t.getClass().getName())) {
                String moduleName = null;
                try {
                    Object module = Class.class.getMethod("getModule").invoke(Stty.class);
                    moduleName = (String) Class.forName("java.lang.Module")
                            .getMethod("getName")
                            .invoke(module);
                } catch (Throwable ignored) {
                }

                System.err.println("WARNING: MinGW support requires --add-opens=java.base/java.lang="
                        + (moduleName == null ? "ALL-UNNAMED" : moduleName));
            }
            // ignore
        }
        return null;
    }

    public static int getTerminalWidth(String name) {
        try {
            Process p = new ProcessBuilder(STTY_COMMAND, STTY_F_COMMAND, name, "size").start();
            String result = waitAndCapture(p);
            if (p.exitValue() != 0) {
                throw new IOException("Error executing '" + STTY_COMMAND + "': " + result);
            }
            Matcher matcher = COLUMNS_PATTERN.matcher(result.trim());
            if (matcher.matches()) {
                return Integer.parseInt(matcher.group(1));
            }
            throw new IOException("Unable to parse columns");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String waitAndCapture(Process p) throws IOException, InterruptedException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try (InputStream in = p.getInputStream();
                InputStream err = p.getErrorStream()) {
            int c;
            while ((c = in.read()) != -1) {
                bout.write(c);
            }
            while ((c = err.read()) != -1) {
                bout.write(c);
            }
            p.waitFor();
        }
        return bout.toString("UTF-8");
    }

    /**
     * This requires --add-opens java.base/java.lang=ALL-UNNAMED
     */
    private static ProcessBuilder.Redirect getRedirect(FileDescriptor fd) throws ReflectiveOperationException {
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

    private Stty() {}
}
