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

import static org.fusesource.jansi.AnsiConsole.JANSI_PROVIDERS;

public final class AnsiConsoleSupportHolder {

    static final AnsiConsoleSupport PROVIDER;

    static final Throwable ERR;

    private static AnsiConsoleSupport getDefaultProvider() {
        if (!OSInfo.isInImageCode()) {
            try {
                // Call the specialized constructor to check whether the module has native access enabled
                // If not, fallback to JNI to avoid the JDK printing warnings in stderr
                return (AnsiConsoleSupport) Class.forName("org.fusesource.jansi.internal.ffm.AnsiConsoleSupportImpl")
                        .getConstructor(boolean.class)
                        .newInstance(true);
            } catch (Throwable ignored) {
            }
        }

        return new org.fusesource.jansi.internal.jni.AnsiConsoleSupportImpl();
    }

    private static AnsiConsoleSupport findProvider(String providerList) {
        String[] providers = providerList.split(",");

        RuntimeException error = null;

        for (String provider : providers) {
            try {
                return (AnsiConsoleSupport)
                        Class.forName("org.fusesource.jansi.internal." + provider + ".AnsiConsoleSupportImpl")
                                .getConstructor()
                                .newInstance();
            } catch (Throwable t) {
                if (error == null) {
                    error = new RuntimeException("Unable to create AnsiConsoleSupport provider");
                }

                error.addSuppressed(t);
            }
        }

        // User does not specify any provider, falling back to the default
        if (error == null) {
            return getDefaultProvider();
        }

        throw error;
    }

    static {
        String providerList = System.getProperty(JANSI_PROVIDERS);

        AnsiConsoleSupport ansiConsoleSupport = null;
        Throwable err = null;

        try {
            if (providerList == null) {
                ansiConsoleSupport = getDefaultProvider();
            } else {
                ansiConsoleSupport = findProvider(providerList);
            }
        } catch (Throwable e) {
            err = e;
        }

        PROVIDER = ansiConsoleSupport;
        ERR = err;
    }

    public static AnsiConsoleSupport getProvider() {
        if (PROVIDER == null) {
            throw new RuntimeException("No provider available", ERR);
        }
        return PROVIDER;
    }

    public static String getProviderName() {
        return getProvider().getProviderName();
    }

    public static AnsiConsoleSupport.CLibrary getCLibrary() {
        return getProvider().getCLibrary();
    }

    public static AnsiConsoleSupport.Kernel32 getKernel32() {
        return getProvider().getKernel32();
    }
}
