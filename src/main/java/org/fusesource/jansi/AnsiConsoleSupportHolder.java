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

import org.fusesource.jansi.ffm.AnsiConsoleSupportFfm;
import org.fusesource.jansi.internal.AnsiConsoleSupportJni;

import static org.fusesource.jansi.AnsiConsole.JANSI_PROVIDERS;
import static org.fusesource.jansi.AnsiConsole.JANSI_PROVIDERS_DEFAULT;
import static org.fusesource.jansi.AnsiConsole.JANSI_PROVIDER_FFM;
import static org.fusesource.jansi.AnsiConsole.JANSI_PROVIDER_JNI;

class AnsiConsoleSupportHolder {
    static volatile AnsiConsoleSupport instance;

    static AnsiConsoleSupport get() {
        if (instance == null) {
            synchronized (AnsiConsoleSupportHolder.class) {
                if (instance == null) {
                    instance = doGet();
                }
            }
        }
        return instance;
    }

    static AnsiConsoleSupport doGet() {
        RuntimeException error = new RuntimeException("Unable to create AnsiConsoleSupport provider");
        String[] providers =
                System.getProperty(JANSI_PROVIDERS, JANSI_PROVIDERS_DEFAULT).split(",");
        for (String provider : providers) {
            try {
                if (JANSI_PROVIDER_FFM.equals(provider)) {
                    return new AnsiConsoleSupportFfm();
                } else if (JANSI_PROVIDER_JNI.equals(provider)) {
                    return new AnsiConsoleSupportJni();
                }
            } catch (Throwable t) {
                error.addSuppressed(t);
            }
        }
        throw error;
    }
}
