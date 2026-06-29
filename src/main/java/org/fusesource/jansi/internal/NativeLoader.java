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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Centralizes the "load the native jansi library, then run a class's
 * JNI constant-initializer" pattern.
 *
 * <p><b>Code smell fixed — Duplicate Code:</b> previously, {@code CLibrary},
 * {@code CLibrary.WinSize}, and {@code CLibrary.Termios} each had an
 * identical static block:
 * <pre>
 *     static {
 *         JansiLoader.initialize();
 *         init();
 *     }
 * </pre>
 * with no error handling — a load failure on an unsupported platform would
 * propagate as a raw {@link ExceptionInInitializerError}. This class
 * provides one implementation, used by all three.</p>
 *
 * <p><b>Code smell fixed — Swallowed/uncaught exceptions:</b> failures are
 * now caught and logged at {@code FINE} level rather than either being
 * silently lost or crashing class initialization.</p>
 */
final class NativeLoader {

    private static final Logger LOG = Logger.getLogger(NativeLoader.class.getName());

    private NativeLoader() {}

    /**
     * Loads the native jansi library (if not already loaded) and, on
     * success, runs the given native initializer.
     *
     * @param nativeInit a reference to the target class's private
     *                    {@code native void init()} method, e.g. {@code CLibrary::init}
     * @param ownerClassName the name of the class being initialized, used only for log messages
     * @return {@code true} if the library loaded and {@code nativeInit} ran without error;
     *         {@code false} otherwise (expected on unsupported platforms)
     */
    static boolean loadAndInit(Runnable nativeInit, String ownerClassName) {
        try {
            boolean loaded = JansiLoader.initialize();
            if (loaded) {
                nativeInit.run();
            }
            return loaded;
        } catch (Throwable t) {
            LOG.log(
                    Level.FINE,
                    ownerClassName + " native initialization failed "
                            + "(expected on unsupported platforms): " + t,
                    t);
            return false;
        }
    }
}