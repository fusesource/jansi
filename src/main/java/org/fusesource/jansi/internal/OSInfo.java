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
 *  Copyright 2008 Taro L. Saito
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * Provides OS name and architecture name.
 */
public class OSInfo {

    public static final String X86 = "x86";
    public static final String X86_64 = "x86_64";
    public static final String IA64_32 = "ia64_32";
    public static final String IA64 = "ia64";
    public static final String PPC = "ppc";
    public static final String PPC64 = "ppc64";
    public static final String ARM64 = "arm64";
    public static final String RISCV64 = "riscv64";

    public static void main(String[] args) {
        if (args.length >= 1) {
            if ("--os".equals(args[0])) {
                System.out.print(getOSName());
                return;
            } else if ("--arch".equals(args[0])) {
                System.out.print(getArchName());
                return;
            }
        }

        System.out.print(getNativeLibFolderPathForCurrentOS());
    }

    private static String mapArchName(String arch) {
        switch (arch.toLowerCase(Locale.ROOT)) {
                // x86 mappings
            case X86:
            case "i386":
            case "i486":
            case "i586":
            case "i686":
            case "pentium":
                return X86;

                // x86_64 mappings
            case X86_64:
            case "amd64":
            case "em64t":
            case "universal": // Needed for openjdk7 in Mac
                return X86_64;

                // Itenium 64-bit mappings
            case IA64:
            case "ia64w":
                return IA64;

                // Itenium 32-bit mappings, usually an HP-UX construct
            case IA64_32:
            case "ia64n":
                return IA64_32;

                // PowerPC mappings
            case PPC:
            case "power":
            case "powerpc":
            case "power_pc":
            case "power_rs":
                return PPC;

                // TODO: PowerPC 64bit mappings
            case PPC64:
            case "power64":
            case "powerpc64":
            case "power_pc64":
            case "power_rs64":
                return PPC64;

                // aarch64 mappings
            case "aarch64":
                return ARM64;

                // riscv64 mappings
            case RISCV64:
                return RISCV64;

            default:
                return null;
        }
    }

    public static String getNativeLibFolderPathForCurrentOS() {
        return getOSName() + "/" + getArchName();
    }

    public static String getOSName() {
        return translateOSNameToFolderName(System.getProperty("os.name"));
    }

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win");
    }

    public static boolean isAndroid() {
        return System.getProperty("java.runtime.name", "")
                .toLowerCase(Locale.ROOT)
                .contains("android");
    }

    public static boolean isAlpine() {
        try {
            for (String line : Files.readAllLines(Paths.get("/etc/os-release"))) {
                if (line.startsWith("ID") && line.toLowerCase(Locale.ROOT).contains("alpine")) {
                    return true;
                }
            }
        } catch (Throwable ignored) {
        }

        return false;
    }

    public static boolean isInImageCode() {
        return System.getProperty("org.graalvm.nativeimage.imagecode") != null;
    }

    static String getHardwareName() {
        try {
            Process p = Runtime.getRuntime().exec("uname -m");
            p.waitFor();

            InputStream in = p.getInputStream();
            try {
                return readFully(in);
            } finally {
                in.close();
            }
        } catch (Throwable e) {
            System.err.println("Error while running uname -m: " + e.getMessage());
            return "unknown";
        }
    }

    private static String readFully(InputStream in) throws IOException {
        int readLen = 0;
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        byte[] buf = new byte[32];
        while ((readLen = in.read(buf, 0, buf.length)) >= 0) {
            b.write(buf, 0, readLen);
        }
        return b.toString();
    }

    static String resolveArmArchType() {
        if (System.getProperty("os.name").contains("Linux")) {
            String armType = getHardwareName();
            // armType (uname -m) can be armv5t, armv5te, armv5tej, armv5tejl, armv6, armv7, armv7l, aarch64, i686
            if (armType.startsWith("armv6")) {
                // Raspberry PI
                return "armv6";
            } else if (armType.startsWith("armv7")) {
                // Generic
                return "armv7";
            } else if (armType.startsWith("armv5")) {
                // Use armv5, soft-float ABI
                return "arm";
            } else if (armType.equals("aarch64")) {
                // Use arm64
                return "arm64";
            }

            // Java 1.8 introduces a system property to determine armel or armhf
            // http://bugs.java.com/bugdatabase/view_bug.do?bug_id=8005545
            String abi = System.getProperty("sun.arch.abi");
            if (abi != null && abi.startsWith("gnueabihf")) {
                return "armv7";
            }
        }
        // Use armv5, soft-float ABI
        return "arm";
    }

    public static String getArchName() {
        String osArch = System.getProperty("os.arch");
        // For Android
        if (isAndroid()) {
            return "android-arm";
        }

        if (osArch.startsWith("arm")) {
            osArch = resolveArmArchType();
        } else {
            String arch = mapArchName(osArch);
            if (arch != null) {
                return arch;
            }
        }
        return translateArchNameToFolderName(osArch);
    }

    static String translateOSNameToFolderName(String osName) {
        if (osName.contains("Windows")) {
            return "Windows";
        } else if (osName.contains("Mac") || osName.contains("Darwin")) {
            return "Mac";
            //        } else if (isAlpine()) {
            //            return "Linux-Alpine";
        } else if (osName.contains("Linux")) {
            return "Linux";
        } else if (osName.contains("AIX")) {
            return "AIX";
        } else {
            return osName.replaceAll("\\W", "");
        }
    }

    static String translateArchNameToFolderName(String archName) {
        return archName.replaceAll("\\W", "");
    }
}
