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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Support for MINGW terminals.
 * <p>
 * Those terminals do not use the underlying Windows terminal and there's no C library available
 * in these environments. We have to rely on calling {@code stty.exe} and {@code tty.exe} to obtain
 * the terminal name and width.
 */
public class MingwSupport {

    private static final Pattern COLUMNS_PATTERNS = Pattern.compile("\\d+ (\\d+)");

    public static Optional<Integer> getTerminalWidth() {
        try {
            Optional<String> terminalName = getTerminalName();
            if (terminalName.isPresent()) {
                Process sttyProcess = new ProcessBuilder("stty.exe", "-F", terminalName.get(), "size").start();
                CharSequence result = waitAndCapture(sttyProcess);
                Matcher matcher = COLUMNS_PATTERNS.matcher(result);
                if (matcher.find()) {
                    return Optional.of(Integer.valueOf(matcher.group(1)));
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return Optional.empty();
    }

    private static Optional<String> getTerminalName() throws IOException, InterruptedException {
        Process ttyProcess = new ProcessBuilder("tty.exe")
                .redirectInput(ProcessBuilder.Redirect.INHERIT)
                .start();
        CharSequence result = waitAndCapture(ttyProcess);
        if (ttyProcess.exitValue() == 0) {
            return Optional.of(result.toString());
        }
        return Optional.empty();
    }

    private static CharSequence waitAndCapture(Process process) throws IOException, InterruptedException {
        StringBuilder result = new StringBuilder();
        try (BufferedReader br =
                new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.US_ASCII))) {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                result.append(line);
            }
        }
        process.waitFor();
        return result;
    }
}
