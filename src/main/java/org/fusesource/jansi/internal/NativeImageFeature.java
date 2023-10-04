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

import org.fusesource.jansi.AnsiConsole;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeClassInitialization;
import org.graalvm.nativeimage.hosted.RuntimeSystemProperties;

public class NativeImageFeature implements Feature {
    @Override
    public String getURL() {
        return "https://github.com/fusesource/jansi";
    }

    @Override
    public void duringSetup(DuringSetupAccess access) {
        RuntimeClassInitialization.initializeAtBuildTime(AnsiConsoleSupportHolder.class);

        String providers = System.getProperty(AnsiConsole.JANSI_PROVIDERS);
        if (providers != null) {
            try {
                RuntimeSystemProperties.register(AnsiConsole.JANSI_PROVIDERS, providers);
            } catch (Throwable ignored) {
                // GraalVM version < 23.0
                // No need to worry as we select the provider at build time
            }
        }

        String provider = AnsiConsoleSupportHolder.getProviderName();

        if (provider == null || provider.equals(AnsiConsole.JANSI_PROVIDER_JNI)) {
            String jansiNativeLibraryName = System.mapLibraryName("jansi");
            if (jansiNativeLibraryName.endsWith(".dylib")) {
                jansiNativeLibraryName = jansiNativeLibraryName.replace(".dylib", ".jnilib");
            }

            String packagePath = JansiLoader.class.getPackage().getName().replace('.', '/');

            try {
                Class<?> moduleClass = Class.forName("java.lang.Module");
                Class<?> rraClass = Class.forName("org.graalvm.nativeimage.hosted.RuntimeResourceAccess");

                Object module = Class.class.getMethod("getModule").invoke(JansiLoader.class);
                rraClass.getMethod("addResource", moduleClass, String.class)
                        .invoke(
                                null,
                                module,
                                String.format(
                                        "%s/native/%s/%s",
                                        packagePath,
                                        OSInfo.getNativeLibFolderPathForCurrentOS(),
                                        jansiNativeLibraryName));

            } catch (Throwable ignored) {
                // GraalVM version < 22.3
                // Users need to manually add the JNI library as resources
            }
        } else if (provider.equals(AnsiConsole.JANSI_PROVIDER_FFM)) {
            try {
                // FFM is only available in JDK 21+, so we need to compile it separately
                Class.forName("org.fusesource.jansi.internal.ffm.NativeImageDowncallRegister")
                        .getMethod("registerForDowncall")
                        .invoke(null);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }
}
