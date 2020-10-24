/*
 * Copyright (C) 2009-2017 the original author(s).
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

import static org.fusesource.jansi.Ansi.ansi;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.Properties;

import org.fusesource.jansi.internal.CLibrary;
import org.fusesource.jansi.internal.JansiLoader;

import static org.fusesource.jansi.internal.CLibrary.isatty;

/**
 * Main class for the library, providing executable jar to diagnose Jansi setup.
 * <p>If no system property is set and output is sent to a terminal (no redirect to a file):
 * <ul>
 * <li>any terminal on any Unix should get <code>RESET_ANSI_AT_CLOSE</code> mode,</li>
 * <li>on Windows, Git-bash or Cygwin terminals should get <code>RESET_ANSI_AT_CLOSE</code> mode also, since they
 * support natively ANSI escape sequences like any Unix terminal,</li>
 * <li>on Windows, cmd.exe, PowerShell or Git-cmd terminals should get <code>WINDOWS</code> mode.</li>
 * </ul>
 * If stdout is redirected to a file (<code>&gt; out.txt</code>), System.out should switch to <code>STRIP_ANSI</code>.
 * Same for stderr redirection (<code>2&gt; err.txt</code>) which should affect System.err mode.
 * <p>The results will vary if you play with <code>jansi.passthrough</code>, <code>jansi.strip</code> or
 * <code>jansi.force</code> system property, or if you redirect output to a file.
 * <p>If you have a specific situation that is not covered, please report precise conditions to reproduce
 * the issue and ideas on how to detect precisely the affected situation.
 * @see AnsiConsole
 */
public class AnsiMain {
    public static void main(String... args) throws IOException {
        System.out.println("Jansi " + getJansiVersion());

        System.out.println();

        // info on native library
        System.out.println("library.jansi.path= " + System.getProperty("library.jansi.path", ""));
        System.out.println("library.jansi.version= " + System.getProperty("library.jansi.version", ""));
        JansiLoader.initialize();
        System.out.println("Jansi native library loaded from " + JansiLoader.getNativeLibraryPath());
        if (JansiLoader.getNativeLibrarySourceUrl() != null) {
            System.out.println("   which was auto-extracted from " + JansiLoader.getNativeLibrarySourceUrl());
        }

        System.out.println();

        System.out.println("os.name= " + System.getProperty("os.name") + ", "
                        + "os.version= " + System.getProperty("os.version") + ", "
                        + "os.arch= " + System.getProperty("os.arch"));
        System.out.println("file.encoding= " + System.getProperty("file.encoding"));
        System.out.println("java.version= " + System.getProperty("java.version") + ", "
                        + "java.vendor= " + System.getProperty("java.vendor") + ","
                        + " java.home= " + System.getProperty("java.home"));

        System.out.println();

        System.out.println(AnsiConsole.JANSI_PASSTHROUGH + "= " + AnsiConsole.getBoolean(AnsiConsole.JANSI_PASSTHROUGH));
        System.out.println(AnsiConsole.JANSI_STRIP + "= " + AnsiConsole.getBoolean(AnsiConsole.JANSI_STRIP));
        System.out.println(AnsiConsole.JANSI_FORCE + "= " + AnsiConsole.getBoolean(AnsiConsole.JANSI_FORCE));
        System.out.println(Ansi.DISABLE + "= " + AnsiConsole.getBoolean(Ansi.DISABLE));

        System.out.println();

        System.out.println("IS_WINDOWS: " + AnsiConsole.IS_WINDOWS);
        if (AnsiConsole.IS_WINDOWS) {
            System.out.println("IS_CONEMU: " + AnsiConsole.IS_CONEMU);
            System.out.println("IS_CYGWIN: " + AnsiConsole.IS_CYGWIN);
            System.out.println("IS_MSYSTEM: " + AnsiConsole.IS_MSYSTEM);
        }

        System.out.println();

        diagnoseTty(false); // System.out
        diagnoseTty(true);  // System.err

        AnsiConsole.systemInstall();

        System.out.println();

        System.out.println("Resulting Jansi modes for stout/stderr streams:");
        System.out.println("  - System.out: " + AnsiConsole.JANSI_STDOUT_TYPE);
        System.out.println("  - System.err: " + AnsiConsole.JANSI_STDERR_TYPE);
        System.out.println("modes description:");
        int n = 1;
        for (AnsiConsole.JansiOutputType type: AnsiConsole.JansiOutputType.values()) {
            System.out.println("  - " + n++ + ". " + type + ": " + type.getDescription());
        }

        try {
            System.out.println();

            testAnsi(false);
            testAnsi(true);

            if (args.length == 0) {
                printJansiLogoDemo();
                return;
            }

            System.out.println();

            if (args.length == 1) {
                File f = new File(args[0]);
                if (f.exists())
                {
                    // write file content
                    System.out.println(ansi().bold().a("\"" + args[0] + "\" content:").reset());
                    writeFileContent(f);
                    return;
                }
            }

            // write args without Jansi then with Jansi AnsiConsole
            System.out.println(ansi().bold().a("original args:").reset());
            int i = 1;
            for (String arg: args) {
                AnsiConsole.system_out.print(i++ + ": ");
                AnsiConsole.system_out.println(arg);
            }

            System.out.println(ansi().bold().a("Jansi filtered args:").reset());
            i = 1;
            for (String arg: args) {
                System.out.print(i++ + ": ");
                System.out.println(arg);
            }
        } finally {
            AnsiConsole.systemUninstall();
        }
    }

    private static String getJansiVersion() {
        Package p = AnsiMain.class.getPackage();
        return ( p == null ) ? null : p.getImplementationVersion();
    }

    private static void diagnoseTty(boolean stderr) {
        int fd = stderr ? CLibrary.STDERR_FILENO : CLibrary.STDOUT_FILENO;
        int isatty = isatty(fd);

        System.out.println("isatty(STD" + (stderr ? "ERR" : "OUT") + "_FILENO): " + isatty + ", System."
            + (stderr ? "err" : "out") + " " + ((isatty == 0) ? "is *NOT*" : "is") + " a terminal");
    }

    private static void testAnsi(boolean stderr) {
        @SuppressWarnings( "resource" )
        PrintStream s = stderr ? System.err : System.out;
        s.print("test on System." + (stderr ? "err" : "out") + ":");
        for(Ansi.Color c: Ansi.Color.values()) {
            s.print(" " + ansi().fg(c) + c + ansi().reset());
        }
        s.println();
        s.print("            bright:");
        for(Ansi.Color c: Ansi.Color.values()) {
            s.print(" " + ansi().fgBright(c) + c + ansi().reset());
        }
        s.println();
        s.print("              bold:");
        for(Ansi.Color c: Ansi.Color.values()) {
            s.print(" " + ansi().bold().fg(c) + c + ansi().reset());
        }
        s.println();
    }

    private static String getPomPropertiesVersion(String path) throws IOException {
        InputStream in = AnsiMain.class.getResourceAsStream("/META-INF/maven/" + path + "/pom.properties");
        if (in == null) {
            return null;
        }
        try {
            Properties p = new Properties();
            p.load(in);
            return p.getProperty("version");
        } finally {
            closeQuietly(in);
        }
    }

    private static void printJansiLogoDemo() throws IOException {
        Reader in = new InputStreamReader(AnsiMain.class.getResourceAsStream("jansi.txt"), "UTF-8");
        try {
            char[] buf = new char[1024];
            int l = 0;
            while ((l = in.read(buf)) >= 0) {
                for(int i = 0; i < l; i++) {
                    System.out.print(buf[i]);
                }
            }
        } finally {
            closeQuietly(in);
        }
    }

    private static void writeFileContent(File f) throws IOException {
        InputStream in = new FileInputStream(f);
        try {
            byte[] buf = new byte[1024];
            int l = 0;
            while ((l = in.read(buf)) >= 0) {
                System.out.write(buf, 0, l);
            }
        } finally {
            closeQuietly(in);
        }
    }

    private static void closeQuietly(Closeable c) {
        try {
            c.close();
        } catch (IOException ioe) {
            ioe.printStackTrace(System.err);
        }
    }
}
