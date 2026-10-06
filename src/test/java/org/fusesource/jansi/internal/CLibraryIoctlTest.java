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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * CVE-2026-8484: {@link CLibrary#ioctl(int, long, int[])} must not let the kernel write past
 * the end of the java array, and must keep accepting every request.
 */
public class CLibraryIoctlTest {

    private static final boolean LINUX =
            System.getProperty("os.name").toLowerCase().contains("linux");

    /** Linux {@code TCGETS}: writes the kernel {@code struct termios} (36 bytes), it does not encode its size. */
    private static final long TCGETS = 0x5401L;

    /** {@code FIONREAD}: writes an {@code int}. */
    private static final long FIONREAD = LINUX ? 0x541BL : 0x4004667FL;

    private static int slave = -1;

    @BeforeAll
    public static void openPty() {
        assumeTrue(CLibrary.LOADED && CLibrary.TIOCGWINSZ != 0, "ioctl is not available on this platform");
        int[] master = new int[1];
        int[] aslave = new int[1];
        assumeTrue(
                CLibrary.openpty(master, aslave, null, null, new CLibrary.WinSize((short) 24, (short) 80)) == 0,
                "openpty is not available");
        slave = aslave[0];
    }

    @Test
    public void testWinSizeIntoArrayTooSmall() {
        // a struct winsize is 8 bytes: only the first 4 fit in the array
        for (int i = 0; i < 1000; i++) {
            int[] params = new int[] {-1};
            assertEquals(0, CLibrary.ioctl(slave, CLibrary.TIOCGWINSZ, params));
            assertNotEquals(-1, params[0]);
        }
    }

    @Test
    public void testWinSizeIntoLargeArray() {
        int[] params = new int[] {0, 0, 0xCAFE};
        assertEquals(0, CLibrary.ioctl(slave, CLibrary.TIOCGWINSZ, params));
        CLibrary.WinSize ws = new CLibrary.WinSize();
        assertEquals(0, CLibrary.ioctl(slave, CLibrary.TIOCGWINSZ, ws));
        assertEquals(24, ws.ws_row);
        assertEquals(80, ws.ws_col);
        assertNotEquals(0, params[0]);
        assertEquals(0xCAFE, params[2]);
    }

    @Test
    public void testTermiosIntoArrayTooSmall() {
        assumeTrue(LINUX, "TCGETS is Linux only");
        // the kernel struct termios is 36 bytes: it used to be written 32 bytes past the array
        for (int i = 0; i < 1000; i++) {
            int[] params = new int[1];
            assertEquals(0, CLibrary.ioctl(slave, TCGETS, params));
        }
    }

    @Test
    public void testOtherRequestsStillWork() {
        int[] params = new int[] {-1};
        assertEquals(0, CLibrary.ioctl(slave, FIONREAD, params));
        assertEquals(0, params[0]);
    }
}
