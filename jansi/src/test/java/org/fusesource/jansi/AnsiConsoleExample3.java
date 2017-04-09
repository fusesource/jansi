package org.fusesource.jansi;
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

import java.io.IOException;
import java.nio.charset.Charset;

import org.fusesource.jansi.AnsiConsole;

/**
 * Quick test to show issues with non-ascii characters on Windows when using AnsiConsole.
 * Not really battle-tested for any Windows encoding, but you can update
 */
public class AnsiConsoleExample3 {

	private final static String NON_ASCII = "éèä";
    private AnsiConsoleExample3() {
    }

    public static void main(String[] args) throws IOException {
    	System.out.println("Platform: " + System.getProperty("os.name"));
    	System.out.println("Platform encoding: " + Charset.defaultCharset());
    	System.out.println("System.out: " + NON_ASCII);
    	AnsiConsole.out.println("AnsiConsole.out: " + NON_ASCII);
    }

}
