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
                ignored.printStackTrace(); // TODO: for debug, should be deleted before merging
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

    public static String getProviderName() {
        return PROVIDER.getProviderName();
    }

    private static final class LibraryHolder {
        static final AnsiConsoleSupport.CLibrary CLIBRARY;
        static final AnsiConsoleSupport.Kernel32 KERNEL32;
        static final Throwable ERR;

        static {
            AnsiConsoleSupport.CLibrary clib = null;
            AnsiConsoleSupport.Kernel32 kernel32 = null;
            Throwable err = null;

            if (PROVIDER != null) {
                try {
                    clib = PROVIDER.getCLibrary();
                    kernel32 = OSInfo.isWindows() ? PROVIDER.getKernel32() : null;
                } catch (Throwable e) {
                    err = e;
                }
            } else {
                err = AnsiConsoleSupportHolder.ERR;
            }

            CLIBRARY = clib;
            KERNEL32 = kernel32;
            ERR = err;
        }
    }

    public static AnsiConsoleSupport.CLibrary getCLibrary() {
        if (LibraryHolder.CLIBRARY == null) {
            throw new RuntimeException("Unable to get the instance of CLibrary", LibraryHolder.ERR);
        }

        return LibraryHolder.CLIBRARY;
    }

    public static AnsiConsoleSupport.Kernel32 getKernel32() {
        if (LibraryHolder.KERNEL32 == null) {
            if (OSInfo.isWindows()) {
                throw new RuntimeException("Unable to get the instance of Kernel32", ERR);
            } else {
                throw new UnsupportedOperationException("Not Windows");
            }
        }

        return LibraryHolder.KERNEL32;
    }
}
