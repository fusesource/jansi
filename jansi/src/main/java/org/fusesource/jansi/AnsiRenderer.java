/*
 * Copyright (C) 2009 the original author(s).
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

import java.util.Locale;

import org.fusesource.jansi.Ansi.Attribute;
import org.fusesource.jansi.Ansi.Color;

/**
 * Renders ANSI color escape-codes in strings by parsing out some special syntax to pick up the correct fluff to use.
 *
 * <p/>
 * The syntax for embedded ANSI codes is:
 *
 * <pre>
 *   <tt>@|</tt><em>code</em>(<tt>,</tt><em>code</em>)* <em>text</em><tt>|@</tt>
 * </pre>
 *
 * Examples:
 *
 * <pre>
 *   <tt>@|bold Hello|@</tt>
 * </pre>
 *
 * <pre>
 *   <tt>@|bold,red Warning!|@</tt>
 * </pre>
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 * @since 1.1
 */
public class AnsiRenderer
{
    public static enum Code
    {
        //
        // TODO: Find a better way to keep Code in sync with Color/Attribute/Erase
        //
        
        // Background Colors
        BG_BLACK(Color.BLACK, true),
        BG_BLUE(Color.BLUE, true),
        BG_CYAN(Color.CYAN, true),
        BG_GREEN(Color.GREEN, true),
        BG_MAGENTA(Color.MAGENTA, true),
        BG_RED(Color.RED, true),
        BG_WHITE(Color.WHITE, true),
        BG_YELLOW(Color.YELLOW, true),

        // Colors
        BLACK(Color.BLACK),
        BLINK_FAST(Attribute.BLINK_FAST),
        BLINK_OFF(Attribute.BLINK_OFF),
        BLINK_SLOW(Attribute.BLINK_SLOW),
        BLUE(Color.BLUE),
        // Aliases
        BOLD(Attribute.INTENSITY_BOLD),
        CONCEAL_OFF(Attribute.CONCEAL_OFF),
        CONCEAL_ON(Attribute.CONCEAL_ON),

        CYAN(Color.CYAN),
        FAINT(Attribute.INTENSITY_FAINT),
        // Foreground Colors
        FG_BLACK(Color.BLACK, false),
        FG_BLUE(Color.BLUE, false),
        FG_CYAN(Color.CYAN, false),
        FG_GREEN(Color.GREEN, false),
        FG_MAGENTA(Color.MAGENTA, false),
        FG_RED(Color.RED, false),

        FG_WHITE(Color.WHITE, false),
        FG_YELLOW(Color.YELLOW, false),
        GREEN(Color.GREEN),
        INTENSITY_BOLD(Attribute.INTENSITY_BOLD),
        INTENSITY_FAINT(Attribute.INTENSITY_FAINT),
        ITALIC(Attribute.ITALIC),
        MAGENTA(Color.MAGENTA),
        NEGATIVE_OFF(Attribute.NEGATIVE_OFF),
        NEGATIVE_ON(Attribute.NEGATIVE_ON),
        RED(Color.RED),
        // Attributes
        RESET(Attribute.RESET),
        UNDERLINE(Attribute.UNDERLINE),
        UNDERLINE_DOUBLE(Attribute.UNDERLINE_DOUBLE),
        UNDERLINE_OFF(Attribute.UNDERLINE_OFF),

        WHITE(Color.WHITE),
        YELLOW(Color.YELLOW),;

        private final boolean background;

        @SuppressWarnings("unchecked")
        private final Enum n;

        @SuppressWarnings("unchecked")
        private Code(final Enum n) {
            this(n, false);
        }

        @SuppressWarnings("unchecked")
        private Code(final Enum n, boolean background) {
            this.n = n;
            this.background = background;
        }

        public Attribute getAttribute() {
            return (Attribute) n;
        }

        public Ansi.Color getColor() {
            return (Ansi.Color) n;
        }

        public boolean isAttribute() {
            return n instanceof Attribute;
        }

        public boolean isBackground() {
            return background;
        }

        public boolean isColor() {
            return n instanceof Ansi.Color;
        }
    }

    public static final String BEGIN_TOKEN = "@|";

    private static final int BEGIN_TOKEN_LEN = 2;

    public static final String CODE_LIST_SEPARATOR = ",";

    public static final String CODE_TEXT_SEPARATOR = " ";

    public static final String END_TOKEN = "|@";

    private static final int END_TOKEN_LEN = 2;

    /**
     * Renders {@link Code} names on the given Ansi.
     * 
     * TODO Consider making public.
     * 
     * @param ansi The Ansi to render upon
     * @param codeNames The code names to render
     * @since 1.10
     */
    private static Ansi render(Ansi ansi, final String... codeNames)
    {
        for (String codeName : codeNames) {
            render(ansi, codeName);
        }
        return ansi;
    }

    /**
     * Renders a {@link Code} name on the given Ansi.
     * 
     * TODO Consider making public.
     * 
     * @param ansi The Ansi to render upon
     * @param codeName The code name to render
     * @since 1.10
     */
    private static Ansi render(Ansi ansi, String codeName)
    {
        Code code = Code.valueOf(codeName.toUpperCase(Locale.ENGLISH));
        if (code.isColor()) {
            if (code.isBackground()) {
                ansi = ansi.bg(code.getColor());
            }
            else {
                ansi = ansi.fg(code.getColor());
            }
        }
        else if (code.isAttribute()) {
            ansi = ansi.a(code.getAttribute());
        }
        return ansi;
    }

    static public String render(final String input) throws IllegalArgumentException {
        StringBuffer buff = new StringBuffer();

        int i = 0;
        int j, k;

        while (true) {
            j = input.indexOf(BEGIN_TOKEN, i);
            if (j == -1) {
                if (i == 0) {
                    return input;
                }
                else {
                    buff.append(input.substring(i, input.length()));
                    return buff.toString();
                }
            }
            else {
                buff.append(input.substring(i, j));
                k = input.indexOf(END_TOKEN, j);

                if (k == -1) {
                    return input;
                }
                else {
                    j += BEGIN_TOKEN_LEN;
                    String spec = input.substring(j, k);

                    String[] items = spec.split(CODE_TEXT_SEPARATOR, 2);
                    if (items.length == 1) {
                        return input;
                    }
                    String replacement = render(items[1], items[0].split(CODE_LIST_SEPARATOR));

                    buff.append(replacement);

                    i = k + END_TOKEN_LEN;
                }
            }
        }
    }

    /**
     * Renders text using the {@link Code} names.
     * 
     * TODO Consider making public.
     * 
     * @param text The text to render
     * @param codeNames The code names to render
     * @since 1.10
     */
    static private String render(final String text, final String... codeNames) {
        Ansi ansi = render(Ansi.ansi(), codeNames);
        return ansi.a(text).reset().toString();
    }

    /**
     * Renders {@link Code} names as an ANSI escape string.
     * 
     * @param codeNames The code names to render
     * @return an ANSI escape string.
     * @since 1.10
     */
    public static String renderCodeNames(final String codeNames)
    {
        return render(new Ansi(), codeNames.split("\\s")).toString();
    }

    public static boolean test(final String text) {
        return text != null && text.contains(BEGIN_TOKEN);
    }
}
