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

import java.io.PrintStream;

public class InstallUninstallTest {

    public static void main(String[] args) {
        print(System.out, "Lorem ipsum");
        print(System.err, "dolor sit amet");
        AnsiConsole.systemInstall();
        print(System.out, "consectetur adipiscing elit");
        print(System.err, "sed do eiusmod");
        AnsiConsole.out().setMode(AnsiMode.Strip);
        AnsiConsole.err().setMode(AnsiMode.Strip);
        print(System.out, "tempor incididunt ut");
        print(System.err, "labore et dolore");
        AnsiConsole.systemUninstall();
        print(System.out, "magna aliqua.");
        print(System.err, "Ut enim ad ");
    }

    private static void print(PrintStream stream, String text) {
        int half = text.length() / 2;
        stream.print(text.substring(0, half));
        stream.println(Ansi.ansi().fg(Ansi.Color.GREEN).a(text.substring(half)).reset());
    }
}
