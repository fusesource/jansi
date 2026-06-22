package org.fusesource.jansi;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.logging.Logger;

final class AnsiPropertyResolver {

    private static final Logger LOG = Logger.getLogger(AnsiPropertyResolver.class.getName());

    private AnsiPropertyResolver() {}

    static AnsiMode resolveMode(String streamModeKey, boolean isAtty) {
        String jansiMode = System.getProperty(streamModeKey, System.getProperty(AnsiConsole.JANSI_MODE));

        if (jansiMode != null) {
            if (AnsiConsole.JANSI_MODE_FORCE.equals(jansiMode)) {
                return AnsiMode.Force;
            } else if (AnsiConsole.JANSI_MODE_STRIP.equals(jansiMode)) {
                return AnsiMode.Strip;
            } else {
                return isAtty ? AnsiMode.Default : AnsiMode.Strip;
            }
        }
        return resolveLegacyMode(isAtty);
    }

    private static AnsiMode resolveLegacyMode(boolean isAtty) {
        if (getBoolean(AnsiConsole.JANSI_PASSTHROUGH)) {
            return AnsiMode.Force;
        }
        if (getBoolean(AnsiConsole.JANSI_STRIP)) {
            return AnsiMode.Strip;
        }
        if (getBoolean(AnsiConsole.JANSI_FORCE)) {
            return AnsiMode.Force;
        }
        return isAtty ? AnsiMode.Default : AnsiMode.Strip;
    }

    static AnsiColors resolveColors(String streamColorsKey) {
        String jansiColors = System.getProperty(streamColorsKey, System.getProperty(AnsiConsole.JANSI_COLORS));

        if (AnsiConsole.JANSI_COLORS_TRUECOLOR.equals(jansiColors)) {
            return AnsiColors.TrueColor;
        } else if (AnsiConsole.JANSI_COLORS_256.equals(jansiColors)) {
            return AnsiColors.Colors256;
        } else if (jansiColors != null) {
            return AnsiColors.Colors16;
        }
        return detectColorsFromEnvironment();
    }

    private static AnsiColors detectColorsFromEnvironment() {
        String colorterm = System.getenv("COLORTERM");
        if (colorterm != null && (colorterm.contains("truecolor") || colorterm.contains("24bit"))) {
            return AnsiColors.TrueColor;
        }
        String term = System.getenv("TERM");
        if (term != null && term.contains("-direct")) {
            return AnsiColors.TrueColor;
        }
        if (term != null && term.contains("-256color")) {
            return AnsiColors.Colors256;
        }
        return AnsiColors.Colors16;
    }

    static Charset resolveCharset(StreamTarget target) {
        String enc = System.getProperty(target.encodingProperty);
        if (enc == null) {
            enc = System.getProperty(target.legacyEncodingProperty);
        }
        if (enc != null) {
            try {
                return Charset.forName(enc);
            } catch (UnsupportedCharsetException e) {
                LOG.fine("Unsupported charset '" + enc + "' for " + target + ", using default charset: " + e.getMessage());
            }
        }
        return Charset.defaultCharset();
    }

    static boolean getBoolean(String name) {
        try {
            String val = System.getProperty(name);
            return val != null && (val.isEmpty() || Boolean.parseBoolean(val));
        } catch (IllegalArgumentException e) {
            LOG.fine("Invalid boolean property '" + name + "': " + e.getMessage());
            return false;
        }
    }
}