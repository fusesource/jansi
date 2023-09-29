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

/*--------------------------------------------------------------------------
 *  Copyright 2007 Taro L. Saito
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *--------------------------------------------------------------------------*/

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.fusesource.jansi.AnsiConsole;

/**
 * Set the system properties, org.jansi.lib.path, org.jansi.lib.name,
 * appropriately so that jansi can find *.dll, *.jnilib and
 * *.so files, according to the current OS (win, linux, mac).
 * <p>
 * The library files are automatically extracted from this project's package
 * (JAR).
 * <p>
 * usage: call {@link #initialize()} before using Jansi.
 */
public class JansiLoader {

    private static boolean loaded = false;
    private static String nativeLibraryPath;
    private static String nativeLibrarySourceUrl;

    /**
     * Loads Jansi native library.
     *
     * @return True if jansi native library is successfully loaded; false
     * otherwise.
     */
    public static synchronized boolean initialize() {
        // only cleanup before the first extract
        if (!loaded) {
            Thread cleanup = new Thread(JansiLoader::cleanup, "cleanup");
            cleanup.setPriority(Thread.MIN_PRIORITY);
            cleanup.setDaemon(true);
            cleanup.start();
        }
        try {
            loadJansiNativeLibrary();
        } catch (Exception e) {
            if (!Boolean.parseBoolean(System.getProperty(AnsiConsole.JANSI_GRACEFUL, "true"))) {
                throw new RuntimeException(
                        "Unable to load jansi native library. You may want set the `jansi.graceful` system property to true to be able to use Jansi on your platform",
                        e);
            }
        }
        return loaded;
    }

    public static String getNativeLibraryPath() {
        return nativeLibraryPath;
    }

    public static String getNativeLibrarySourceUrl() {
        return nativeLibrarySourceUrl;
    }

    private static File getTempDir() {
        return new File(System.getProperty("jansi.tmpdir", System.getProperty("java.io.tmpdir")));
    }

    /**
     * Deleted old native libraries e.g. on Windows the DLL file is not removed
     * on VM-Exit (bug #80)
     */
    static void cleanup() {
        String tempFolder = getTempDir().getAbsolutePath();
        File dir = new File(tempFolder);

        File[] nativeLibFiles = dir.listFiles(new FilenameFilter() {
            private final String searchPattern = "jansi-" + getVersion();

            public boolean accept(File dir, String name) {
                return name.startsWith(searchPattern) && !name.endsWith(".lck");
            }
        });
        if (nativeLibFiles != null) {
            for (File nativeLibFile : nativeLibFiles) {
                File lckFile = new File(nativeLibFile.getAbsolutePath() + ".lck");
                if (!lckFile.exists()) {
                    try {
                        nativeLibFile.delete();
                    } catch (SecurityException e) {
                        System.err.println("Failed to delete old native lib" + e.getMessage());
                    }
                }
            }
        }
    }

    private static int readNBytes(InputStream in, byte[] b) throws IOException {
        int n = 0;
        int len = b.length;
        while (n < len) {
            int count = in.read(b, n, len - n);
            if (count <= 0) break;
            n += count;
        }
        return n;
    }

    private static String contentsEquals(InputStream in1, InputStream in2) throws IOException {
        byte[] buffer1 = new byte[8192];
        byte[] buffer2 = new byte[8192];
        int numRead1;
        int numRead2;
        while (true) {
            numRead1 = readNBytes(in1, buffer1);
            numRead2 = readNBytes(in2, buffer2);
            if (numRead1 > 0) {
                if (numRead2 <= 0) {
                    return "EOF on second stream but not first";
                }
                if (numRead2 != numRead1) {
                    return "Read size different (" + numRead1 + " vs " + numRead2 + ")";
                }
                // Otherwise same number of bytes read
                if (!Arrays.equals(buffer1, buffer2)) {
                    return "Content differs";
                }
                // Otherwise same bytes read, so continue ...
            } else {
                // Nothing more in stream 1 ...
                if (numRead2 > 0) {
                    return "EOF on first stream but not second";
                } else {
                    return null;
                }
            }
        }
    }

    /**
     * Extracts and loads the specified library file to the target folder
     *
     * @param libFolderForCurrentOS Library path.
     * @param libraryFileName       Library name.
     * @param targetFolder          Target folder.
     * @return
     */
    private static boolean extractAndLoadLibraryFile(
            String libFolderForCurrentOS, String libraryFileName, String targetFolder) {
        String nativeLibraryFilePath = libFolderForCurrentOS + "/" + libraryFileName;
        // Include architecture name in temporary filename in order to avoid conflicts
        // when multiple JVMs with different architectures running at the same time
        String uuid = randomUUID();
        String extractedLibFileName = String.format("jansi-%s-%s-%s", getVersion(), uuid, libraryFileName);
        String extractedLckFileName = extractedLibFileName + ".lck";

        File extractedLibFile = new File(targetFolder, extractedLibFileName);
        File extractedLckFile = new File(targetFolder, extractedLckFileName);

        try {
            // Extract a native library file into the target directory
            try (InputStream in = JansiLoader.class.getResourceAsStream(nativeLibraryFilePath)) {
                if (!extractedLckFile.exists()) {
                    new FileOutputStream(extractedLckFile).close();
                }
                Files.copy(in, extractedLibFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } finally {
                // Delete the extracted lib file on JVM exit.
                extractedLibFile.deleteOnExit();
                extractedLckFile.deleteOnExit();
            }

            // Set executable (x) flag to enable Java to load the native library
            extractedLibFile.setReadable(true);
            extractedLibFile.setWritable(true);
            extractedLibFile.setExecutable(true);

            // Check whether the contents are properly copied from the resource folder
            try (InputStream nativeIn = JansiLoader.class.getResourceAsStream(nativeLibraryFilePath)) {
                try (InputStream extractedLibIn = new FileInputStream(extractedLibFile)) {
                    String eq = contentsEquals(nativeIn, extractedLibIn);
                    if (eq != null) {
                        throw new RuntimeException(String.format(
                                "Failed to write a native library file at %s because %s", extractedLibFile, eq));
                    }
                }
            }

            // Load library
            if (loadNativeLibrary(extractedLibFile)) {
                nativeLibrarySourceUrl =
                        JansiLoader.class.getResource(nativeLibraryFilePath).toExternalForm();
                return true;
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    private static String randomUUID() {
        return Long.toHexString(new Random().nextLong());
    }

    /**
     * Loads native library using the given path and name of the library.
     *
     * @param libPath Path of the native library.
     * @return True for successfully loading; false otherwise.
     */
    private static boolean loadNativeLibrary(File libPath) {
        if (libPath.exists()) {
            try {
                String path = libPath.getAbsolutePath();
                System.load(path);
                nativeLibraryPath = path;
                return true;
            } catch (UnsatisfiedLinkError e) {
                if (!libPath.canExecute()) {
                    // NOTE: this can be tested using something like:
                    // docker run --rm --tmpfs /tmp -v $PWD:/jansi openjdk:11 java -jar
                    // /jansi/target/jansi-xxx-SNAPSHOT.jar
                    System.err.printf(
                            "Failed to load native library:%s. The native library file at %s is not executable, "
                                    + "make sure that the directory is mounted on a partition without the noexec flag, or set the "
                                    + "jansi.tmpdir system property to point to a proper location.  osinfo: %s%n",
                            libPath.getName(), libPath, OSInfo.getNativeLibFolderPathForCurrentOS());
                } else {
                    System.err.printf(
                            "Failed to load native library:%s. osinfo: %s%n",
                            libPath.getName(), OSInfo.getNativeLibFolderPathForCurrentOS());
                }
                System.err.println(e);
                return false;
            }

        } else {
            return false;
        }
    }

    /**
     * Loads jansi library using given path and name of the library.
     *
     * @throws
     */
    private static void loadJansiNativeLibrary() throws Exception {
        if (loaded) {
            return;
        }

        List<String> triedPaths = new LinkedList<String>();

        // Try loading library from library.jansi.path library path */
        String jansiNativeLibraryPath = System.getProperty("library.jansi.path");
        String jansiNativeLibraryName = System.getProperty("library.jansi.name");
        if (jansiNativeLibraryName == null) {
            jansiNativeLibraryName = System.mapLibraryName("jansi");
            assert jansiNativeLibraryName != null;
            if (jansiNativeLibraryName.endsWith(".dylib")) {
                jansiNativeLibraryName = jansiNativeLibraryName.replace(".dylib", ".jnilib");
            }
        }

        if (jansiNativeLibraryPath != null) {
            String withOs = jansiNativeLibraryPath + "/" + OSInfo.getNativeLibFolderPathForCurrentOS();
            if (loadNativeLibrary(new File(withOs, jansiNativeLibraryName))) {
                loaded = true;
                return;
            } else {
                triedPaths.add(withOs);
            }

            if (loadNativeLibrary(new File(jansiNativeLibraryPath, jansiNativeLibraryName))) {
                loaded = true;
                return;
            } else {
                triedPaths.add(jansiNativeLibraryPath);
            }
        }

        // Load the os-dependent library from the jar file
        String packagePath = JansiLoader.class.getPackage().getName().replace('.', '/');
        jansiNativeLibraryPath =
                String.format("/%s/native/%s", packagePath, OSInfo.getNativeLibFolderPathForCurrentOS());
        boolean hasNativeLib = hasResource(jansiNativeLibraryPath + "/" + jansiNativeLibraryName);

        if (hasNativeLib) {
            // temporary library folder
            String tempFolder = getTempDir().getAbsolutePath();
            // Try extracting the library from jar
            if (extractAndLoadLibraryFile(jansiNativeLibraryPath, jansiNativeLibraryName, tempFolder)) {
                loaded = true;
                return;
            } else {
                triedPaths.add(jansiNativeLibraryPath);
            }
        }

        // As a last resort try from java.library.path
        String javaLibraryPath = System.getProperty("java.library.path", "");
        for (String ldPath : javaLibraryPath.split(File.pathSeparator)) {
            if (ldPath.isEmpty()) {
                continue;
            }
            if (loadNativeLibrary(new File(ldPath, jansiNativeLibraryName))) {
                loaded = true;
                return;
            } else {
                triedPaths.add(ldPath);
            }
        }

        throw new Exception(String.format(
                "No native library found for os.name=%s, os.arch=%s, paths=[%s]",
                OSInfo.getOSName(), OSInfo.getArchName(), String.join(File.pathSeparator, triedPaths)));
    }

    private static boolean hasResource(String path) {
        return JansiLoader.class.getResource(path) != null;
    }

    /**
     * @return The major version of the jansi library.
     */
    public static int getMajorVersion() {
        String[] c = getVersion().split("\\.");
        return (c.length > 0) ? Integer.parseInt(c[0]) : 1;
    }

    /**
     * @return The minor version of the jansi library.
     */
    public static int getMinorVersion() {
        String[] c = getVersion().split("\\.");
        return (c.length > 1) ? Integer.parseInt(c[1]) : 0;
    }

    /**
     * @return The version of the jansi library.
     */
    public static String getVersion() {

        URL versionFile = JansiLoader.class.getResource("/org/fusesource/jansi/jansi.properties");

        String version = "unknown";
        try {
            if (versionFile != null) {
                Properties versionData = new Properties();
                versionData.load(versionFile.openStream());
                version = versionData.getProperty("version", version);
                version = version.trim().replaceAll("[^0-9.]", "");
            }
        } catch (IOException e) {
            System.err.println(e);
        }
        return version;
    }
}
