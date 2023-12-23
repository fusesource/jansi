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
package org.fusesource.jansi.io;

/**
 * Helper class for dealing with color rounding.
 * This is a simplified version of the JLine's one at
 *   https://github.com/jline/jline3/blob/a24636dc5de83baa6b65049e8215fb372433b3b1/terminal/src/main/java/org/jline/utils/Colors.java
 */
public class Colors {

    /**
     * Default 256 colors palette
     */
    // spotless:off
    public static final int[] DEFAULT_COLORS_256 = {
            // 16 ansi
            0x000000, 0x800000, 0x008000, 0x808000, 0x000080, 0x800080, 0x008080, 0xc0c0c0,
            0x808080, 0xff0000, 0x00ff00, 0xffff00, 0x0000ff, 0xff00ff, 0x00ffff, 0xffffff,

            // 6x6x6 color cube
            0x000000, 0x00005f, 0x000087, 0x0000af, 0x0000d7, 0x0000ff,
            0x005f00, 0x005f5f, 0x005f87, 0x005faf, 0x005fd7, 0x005fff,
            0x008700, 0x00875f, 0x008787, 0x0087af, 0x0087d7, 0x0087ff,
            0x00af00, 0x00af5f, 0x00af87, 0x00afaf, 0x00afd7, 0x00afff,
            0x00d700, 0x00d75f, 0x00d787, 0x00d7af, 0x00d7d7, 0x00d7ff,
            0x00ff00, 0x00ff5f, 0x00ff87, 0x00ffaf, 0x00ffd7, 0x00ffff,

            0x5f0000, 0x5f005f, 0x5f0087, 0x5f00af, 0x5f00d7, 0x5f00ff,
            0x5f5f00, 0x5f5f5f, 0x5f5f87, 0x5f5faf, 0x5f5fd7, 0x5f5fff,
            0x5f8700, 0x5f875f, 0x5f8787, 0x5f87af, 0x5f87d7, 0x5f87ff,
            0x5faf00, 0x5faf5f, 0x5faf87, 0x5fafaf, 0x5fafd7, 0x5fafff,
            0x5fd700, 0x5fd75f, 0x5fd787, 0x5fd7af, 0x5fd7d7, 0x5fd7ff,
            0x5fff00, 0x5fff5f, 0x5fff87, 0x5fffaf, 0x5fffd7, 0x5fffff,

            0x870000, 0x87005f, 0x870087, 0x8700af, 0x8700d7, 0x8700ff,
            0x875f00, 0x875f5f, 0x875f87, 0x875faf, 0x875fd7, 0x875fff,
            0x878700, 0x87875f, 0x878787, 0x8787af, 0x8787d7, 0x8787ff,
            0x87af00, 0x87af5f, 0x87af87, 0x87afaf, 0x87afd7, 0x87afff,
            0x87d700, 0x87d75f, 0x87d787, 0x87d7af, 0x87d7d7, 0x87d7ff,
            0x87ff00, 0x87ff5f, 0x87ff87, 0x87ffaf, 0x87ffd7, 0x87ffff,

            0xaf0000, 0xaf005f, 0xaf0087, 0xaf00af, 0xaf00d7, 0xaf00ff,
            0xaf5f00, 0xaf5f5f, 0xaf5f87, 0xaf5faf, 0xaf5fd7, 0xaf5fff,
            0xaf8700, 0xaf875f, 0xaf8787, 0xaf87af, 0xaf87d7, 0xaf87ff,
            0xafaf00, 0xafaf5f, 0xafaf87, 0xafafaf, 0xafafd7, 0xafafff,
            0xafd700, 0xafd75f, 0xafd787, 0xafd7af, 0xafd7d7, 0xafd7ff,
            0xafff00, 0xafff5f, 0xafff87, 0xafffaf, 0xafffd7, 0xafffff,

            0xd70000, 0xd7005f, 0xd70087, 0xd700af, 0xd700d7, 0xd700ff,
            0xd75f00, 0xd75f5f, 0xd75f87, 0xd75faf, 0xd75fd7, 0xd75fff,
            0xd78700, 0xd7875f, 0xd78787, 0xd787af, 0xd787d7, 0xd787ff,
            0xd7af00, 0xd7af5f, 0xd7af87, 0xd7afaf, 0xd7afd7, 0xd7afff,
            0xd7d700, 0xd7d75f, 0xd7d787, 0xd7d7af, 0xd7d7d7, 0xd7d7ff,
            0xd7ff00, 0xd7ff5f, 0xd7ff87, 0xd7ffaf, 0xd7ffd7, 0xd7ffff,

            0xff0000, 0xff005f, 0xff0087, 0xff00af, 0xff00d7, 0xff00ff,
            0xff5f00, 0xff5f5f, 0xff5f87, 0xff5faf, 0xff5fd7, 0xff5fff,
            0xff8700, 0xff875f, 0xff8787, 0xff87af, 0xff87d7, 0xff87ff,
            0xffaf00, 0xffaf5f, 0xffaf87, 0xffafaf, 0xffafd7, 0xffafff,
            0xffd700, 0xffd75f, 0xffd787, 0xffd7af, 0xffd7d7, 0xffd7ff,
            0xffff00, 0xffff5f, 0xffff87, 0xffffaf, 0xffffd7, 0xffffff,

            // 24 grey ramp
            0x080808, 0x121212, 0x1c1c1c, 0x262626, 0x303030, 0x3a3a3a, 0x444444, 0x4e4e4e,
            0x585858, 0x626262, 0x6c6c6c, 0x767676, 0x808080, 0x8a8a8a, 0x949494, 0x9e9e9e,
            0xa8a8a8, 0xb2b2b2, 0xbcbcbc, 0xc6c6c6, 0xd0d0d0, 0xdadada, 0xe4e4e4, 0xeeeeee,
    };
    // spotless:on

    public static int roundColor(int col, int max) {
        if (col >= max) {
            int c = DEFAULT_COLORS_256[col];
            col = roundColor(c, DEFAULT_COLORS_256, max);
        }
        return col;
    }

    public static int roundRgbColor(int r, int g, int b, int max) {
        return roundColor((r << 16) + (g << 8) + b, DEFAULT_COLORS_256, max);
    }

    private static int roundColor(int color, int[] colors, int max) {
        double best_distance = Integer.MAX_VALUE;
        int best_index = Integer.MAX_VALUE;
        for (int idx = 0; idx < max; idx++) {
            double d = cie76(color, colors[idx]);
            if (d <= best_distance) {
                best_index = idx;
                best_distance = d;
            }
        }
        return best_index;
    }

    private static double cie76(int c1, int c2) {
        return scalar(rgb2cielab(c1), rgb2cielab(c2));
    }

    private static double scalar(double[] c1, double[] c2) {
        return sqr(c1[0] - c2[0]) + sqr(c1[1] - c2[1]) + sqr(c1[2] - c2[2]);
    }

    private static double[] rgb(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        return new double[] {r / 255.0, g / 255.0, b / 255.0};
    }

    private static double[] rgb2cielab(int color) {
        return rgb2cielab(rgb(color));
    }

    private static double[] rgb2cielab(double[] rgb) {
        return xyz2lab(rgb2xyz(rgb));
    }

    private static double[] rgb2xyz(double[] rgb) {
        double vr = pivotRgb(rgb[0]);
        double vg = pivotRgb(rgb[1]);
        double vb = pivotRgb(rgb[2]);
        // http://www.brucelindbloom.com/index.html?Eqn_RGB_XYZ_Matrix.html
        double x = vr * 0.4124564 + vg * 0.3575761 + vb * 0.1804375;
        double y = vr * 0.2126729 + vg * 0.7151522 + vb * 0.0721750;
        double z = vr * 0.0193339 + vg * 0.1191920 + vb * 0.9503041;
        return new double[] {x, y, z};
    }

    private static double pivotRgb(double n) {
        return n > 0.04045 ? Math.pow((n + 0.055) / 1.055, 2.4) : n / 12.92;
    }

    private static double[] xyz2lab(double[] xyz) {
        double fx = pivotXyz(xyz[0]);
        double fy = pivotXyz(xyz[1]);
        double fz = pivotXyz(xyz[2]);
        double l = 116.0 * fy - 16.0;
        double a = 500.0 * (fx - fy);
        double b = 200.0 * (fy - fz);
        return new double[] {l, a, b};
    }

    private static final double epsilon = 216.0 / 24389.0;
    private static final double kappa = 24389.0 / 27.0;

    private static double pivotXyz(double n) {
        return n > epsilon ? Math.cbrt(n) : (kappa * n + 16) / 116;
    }

    private static double sqr(double n) {
        return n * n;
    }

    public static String rgbToAnsi(int r, int g, int b) {
        return String.format("\u001B[38;2;%d;%d;%dm", r, g, b);
    }
}
