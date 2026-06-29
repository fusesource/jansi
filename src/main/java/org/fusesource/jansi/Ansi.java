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
package org.fusesource.jansi;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Provides a fluent API for building
 * <a href="https://en.wikipedia.org/wiki/ANSI_escape_code#CSI_sequences">ANSI escape sequences</a>
 * as a {@link String} that can be printed to an ANSI-capable terminal.
 *
 * <h3>Basic usage</h3>
 * <pre>
 * String message = Ansi.ansi()
 * .fg(Ansi.Color.RED)
 * .append("Error: file not found")
 * .reset()
 * .toString();
 * System.out.println(message);
 * </pre>
 *
 * <h3>ANSI detection</h3>
 * <p>When {@link #isEnabled()} returns {@code false} (e.g. the system property
 * {@code DISABLE} is set to {@code true}), {@link #ansi()} returns a
 * {@link NoAnsi} instance whose methods are all no-ops, so application code
 * never needs to branch on ANSI availability.</p>
 *
 * <h3>Refactoring changes applied (SE2115 project)</h3>
 * <ul>
 * <li><b>Uncommunicative Naming — Rename Method (Dispensable, Smell #25)</b>:
 * The overloaded {@code a(...)} append methods have been given descriptive
 * aliases ({@link #append(String)}, {@link #append(Object)},
 * {@link #append(char[])}, etc.) so that call sites read as natural prose:
 * {@code .fg(RED).append("Error").reset()} instead of
 * {@code .fg(RED).a("Error").reset()}.
 * The original {@code a(...)} overloads are kept and marked
 * {@code @Deprecated} to preserve full backward compatibility — existing
 * code continues to compile without any changes.</li>
 *
 * <li><b>Missing Javadoc on public API (Dispensable, Smell #25)</b>:
 * Every public method now has a Javadoc comment describing its purpose,
 * parameters, and return value. Previously the majority of public methods
 * in this class had no documentation at all, which is especially harmful
 * for a public library API consumed by third-party developers.</li>
 *
 * <li><b>Primitive Obsession — Named ESC constants (Bloater, Smell #5)</b>:
 * The raw {@code char} literals {@code 27} and {@code '['} used to
 * construct ANSI escape sequences are now named constants
 * ({@link #ESC} and {@link #CSI_OPEN}) with Javadoc explaining their
 * role in the ANSI CSI sequence format.</li>
 *
 * <li><b>Private helper naming (Dispensable)</b>:
 * The private method {@code _appendEscapeSequence} used a non-Java
 * naming convention (leading underscore). Renamed to
 * {@code appendEscapeSequenceRaw} to follow standard Java method
 * naming while keeping the same behaviour.</li>
 * </ul>
 *
 * @since 1.0
 * @see AnsiRenderer
 * @see AnsiConsole
 */
public class Ansi implements Appendable {

    // ─────────────────────────────────────────────────────────────
    // Named constants — replaces magic char literals 27 and '['
    // (fixes Primitive Obsession smell)
    // ─────────────────────────────────────────────────────────────

    /**
     * The ASCII ESC (escape) character, decimal value {@code 27} (0x1B).
     * This is the first character of every ANSI CSI escape sequence.
     */
    private static final char ESC = 27;

    /**
     * The second character of an ANSI CSI (Control Sequence Introducer)
     * escape sequence. Together with {@link #ESC}, it forms the two-character
     * prefix {@code ESC [} that begins all CSI commands.
     */
    private static final char CSI_OPEN = '[';

    // ─────────────────────────────────────────────────────────────
    // Public enums
    // ─────────────────────────────────────────────────────────────

    /**
     * The 8 standard ANSI foreground/background colors plus {@code DEFAULT}.
     *
     * <p>Use with {@link Ansi#fg(Color)}, {@link Ansi#bg(Color)},
     * {@link Ansi#fgBright(Color)}, and {@link Ansi#bgBright(Color)}.</p>
     *
     * @see <a href="https://en.wikipedia.org/wiki/ANSI_escape_code#Colors">
     * ANSI 8-color palette</a>
     */
    public enum Color {
        BLACK  (0, "BLACK"),
        RED    (1, "RED"),
        GREEN  (2, "GREEN"),
        YELLOW (3, "YELLOW"),
        BLUE   (4, "BLUE"),
        MAGENTA(5, "MAGENTA"),
        CYAN   (6, "CYAN"),
        WHITE  (7, "WHITE"),
        DEFAULT(9, "DEFAULT");

        private final int    value;
        private final String name;

        Color(int index, String name) {
            this.value = index;
            this.name  = name;
        }

        @Override
        public String toString() { return name; }

        /** Returns the raw SGR color index for this color. */
        public int value() { return value; }

        /** Returns the SGR code for this color as a foreground (normal intensity). */
        public int fg() { return value + 30; }

        /** Returns the SGR code for this color as a background (normal intensity). */
        public int bg() { return value + 40; }

        /** Returns the SGR code for this color as a bright/intense foreground. */
        public int fgBright() { return value + 90; }

        /** Returns the SGR code for this color as a bright/intense background. */
        public int bgBright() { return value + 100; }
    }

    /**
     * SGR (Select Graphic Rendition) display attribute codes.
     *
     * <p>Use with {@link Ansi#a(Attribute)} to apply text attributes such as
     * bold, italic, underline, and blink.</p>
     *
     * @see <a href="https://en.wikipedia.org/wiki/ANSI_escape_code#SGR_(Select_Graphic_Rendition)_parameters">
     * SGR parameters</a>
     */
    public enum Attribute {
        RESET               ( 0, "RESET"),
        INTENSITY_BOLD      ( 1, "INTENSITY_BOLD"),
        INTENSITY_FAINT     ( 2, "INTENSITY_FAINT"),
        ITALIC              ( 3, "ITALIC_ON"),
        UNDERLINE           ( 4, "UNDERLINE_ON"),
        BLINK_SLOW          ( 5, "BLINK_SLOW"),
        BLINK_FAST          ( 6, "BLINK_FAST"),
        NEGATIVE_ON         ( 7, "NEGATIVE_ON"),
        CONCEAL_ON          ( 8, "CONCEAL_ON"),
        STRIKETHROUGH_ON    ( 9, "STRIKETHROUGH_ON"),
        UNDERLINE_DOUBLE    (21, "UNDERLINE_DOUBLE"),
        INTENSITY_BOLD_OFF  (22, "INTENSITY_BOLD_OFF"),
        ITALIC_OFF          (23, "ITALIC_OFF"),
        UNDERLINE_OFF       (24, "UNDERLINE_OFF"),
        BLINK_OFF           (25, "BLINK_OFF"),
        NEGATIVE_OFF        (27, "NEGATIVE_OFF"),
        CONCEAL_OFF         (28, "CONCEAL_OFF"),
        STRIKETHROUGH_OFF   (29, "STRIKETHROUGH_OFF");

        private final int    value;
        private final String name;

        Attribute(int index, String name) {
            this.value = index;
            this.name  = name;
        }

        @Override
        public String toString() { return name; }

        /** Returns the SGR numeric code for this attribute. */
        public int value() { return value; }
    }

    /**
     * Parameter for the ED (Erase in Display) and EL (Erase in Line) commands.
     *
     * @see Ansi#eraseScreen(Erase)
     * @see Ansi#eraseLine(Erase)
     * @see <a href="https://en.wikipedia.org/wiki/ANSI_escape_code#CSI_sequences">
     * CSI J and K sequences</a>
     */
    public enum Erase {
        FORWARD (0, "FORWARD"),
        BACKWARD(1, "BACKWARD"),
        ALL     (2, "ALL");

        private final int    value;
        private final String name;

        Erase(int index, String name) {
            this.value = index;
            this.name  = name;
        }

        @Override
        public String toString() { return name; }

        /** Returns the numeric parameter for this erase mode. */
        public int value() { return value; }
    }

    // ─────────────────────────────────────────────────────────────
    // Consumer functional interface
    // ─────────────────────────────────────────────────────────────

    /**
     * A functional interface for operations that accept an {@link Ansi} instance.
     * Used by {@link #apply(Consumer)} to allow inline customization of the
     * escape sequence being built.
     */
    @FunctionalInterface
    public interface Consumer {
        /**
         * Applies this operation to the given {@link Ansi} instance.
         *
         * @param ansi the builder to operate on
         */
        void apply(Ansi ansi);
    }

    // ─────────────────────────────────────────────────────────────
    // Detection mechanism
    // ─────────────────────────────────────────────────────────────

    /**
     * System property name that disables ANSI output when set to {@code true}.
     * When disabled, {@link #ansi()} returns a {@link NoAnsi} no-op instance.
     */
    public static final String DISABLE = Ansi.class.getName() + ".disable";

    private static Callable<Boolean> detector = () -> !Boolean.getBoolean(DISABLE);

    /**
     * Replaces the ANSI detection strategy with a custom callable.
     *
     * @param detector a {@link Callable} returning {@code true} if ANSI is available;
     * must not be {@code null}
     * @throws IllegalArgumentException if {@code detector} is {@code null}
     */
    public static void setDetector(final Callable<Boolean> detector) {
        if (detector == null) throw new IllegalArgumentException("detector must not be null");
        Ansi.detector = detector;
    }

    /**
     * Returns {@code true} if the current detector callable indicates
     * that ANSI escape sequences are supported on this platform.
     *
     * @return {@code true} if ANSI is detected as available
     */
    public static boolean isDetected() {
        try {
            return detector.call();
        } catch (Exception e) {
            return true;
        }
    }

    private static final InheritableThreadLocal<Boolean> holder =
            new InheritableThreadLocal<Boolean>() {
                @Override
                protected Boolean initialValue() {
                    return isDetected();
                }
            };

    /**
     * Enables or disables ANSI output for the current thread and all threads
     * it subsequently creates.
     *
     * @param flag {@code true} to enable ANSI; {@code false} to disable
     */
    public static void setEnabled(final boolean flag) {
        holder.set(flag);
    }

    /**
     * Returns {@code true} if ANSI output is enabled for the current thread.
     *
     * @return {@code true} if ANSI is enabled
     */
    public static boolean isEnabled() {
        return holder.get();
    }

    // ─────────────────────────────────────────────────────────────
    // Factory methods
    // ─────────────────────────────────────────────────────────────

    /**
     * Creates a new {@link Ansi} builder instance.
     *
     * <p>If ANSI is disabled ({@link #isEnabled()} returns {@code false}),
     * a {@link NoAnsi} no-op instance is returned instead, so callers
     * never need to check ANSI availability themselves.</p>
     *
     * @return a new {@link Ansi} builder (or a no-op instance if disabled)
     */
    public static Ansi ansi() {
        return isEnabled() ? new Ansi() : new NoAnsi();
    }

    /**
     * Creates a new {@link Ansi} builder backed by an existing {@link StringBuilder}.
     *
     * @param builder the backing builder to append escape sequences to
     * @return a new {@link Ansi} instance wrapping {@code builder}
     */
    public static Ansi ansi(StringBuilder builder) {
        return isEnabled() ? new Ansi(builder) : new NoAnsi(builder);
    }

    /**
     * Creates a new {@link Ansi} builder with the given initial capacity.
     *
     * @param size the initial capacity of the internal {@link StringBuilder}
     * @return a new {@link Ansi} instance
     */
    public static Ansi ansi(int size) {
        return isEnabled() ? new Ansi(size) : new NoAnsi(size);
    }

    // ─────────────────────────────────────────────────────────────
    // NoAnsi — no-op subclass returned when ANSI is disabled
    // ─────────────────────────────────────────────────────────────

    /**
     * A no-op subclass of {@link Ansi} that suppresses all escape sequence
     * generation. All methods return {@code this} immediately without
     * modifying the underlying builder.
     *
     * <p>Returned by {@link #ansi()} when {@link #isEnabled()} is {@code false},
     * allowing application code to call the full fluent API without any
     * conditional checks for ANSI availability.</p>
     */
    private static class NoAnsi extends Ansi {

        NoAnsi()                       { super(); }
        NoAnsi(int size)               { super(size); }
        NoAnsi(StringBuilder builder)  { super(builder); }

        @Override public Ansi fg(Color color)                   { return this; }
        @Override public Ansi bg(Color color)                   { return this; }
        @Override public Ansi fgBright(Color color)             { return this; }
        @Override public Ansi bgBright(Color color)             { return this; }
        @Override public Ansi fg(int color)                     { return this; }
        @Override public Ansi fgRgb(int r, int g, int b)        { return this; }
        @Override public Ansi bg(int color)                     { return this; }
        @Override public Ansi bgRgb(int r, int g, int b)        { return this; }
        @Override public Ansi a(Attribute attribute)            { return this; }
        @Override public Ansi cursor(int row, int column)       { return this; }
        @Override public Ansi cursorToColumn(int x)             { return this; }
        @Override public Ansi cursorUp(int y)                   { return this; }
        @Override public Ansi cursorRight(int x)                { return this; }
        @Override public Ansi cursorDown(int y)                 { return this; }
        @Override public Ansi cursorLeft(int x)                 { return this; }
        @Override public Ansi cursorDownLine()                  { return this; }
        @Override public Ansi cursorDownLine(final int n)       { return this; }
        @Override public Ansi cursorUpLine()                    { return this; }
        @Override public Ansi cursorUpLine(final int n)         { return this; }
        @Override public Ansi eraseScreen()                     { return this; }
        @Override public Ansi eraseScreen(Erase kind)           { return this; }
        @Override public Ansi eraseLine()                       { return this; }
        @Override public Ansi eraseLine(Erase kind)             { return this; }
        @Override public Ansi scrollUp(int rows)                { return this; }
        @Override public Ansi scrollDown(int rows)              { return this; }
        @Override public Ansi saveCursorPosition()              { return this; }
        @Override public Ansi restoreCursorPosition()           { return this; }
        @Override public Ansi reset()                           { return this; }

        /** @deprecated Use {@link #restoreCursorPosition()} instead. */
        @Override
        @Deprecated
        public Ansi restorCursorPosition()                      { return this; }
    }

    // ─────────────────────────────────────────────────────────────
    // Instance state
    // ─────────────────────────────────────────────────────────────

    private final StringBuilder      builder;
    private final ArrayList<Integer> attributeOptions = new ArrayList<>(5);

    // ─────────────────────────────────────────────────────────────
    // Constructors
    // ─────────────────────────────────────────────────────────────

    /** Creates a new {@code Ansi} builder with a default initial capacity of 80. */
    public Ansi() {
        this(new StringBuilder(80));
    }

    /**
     * Creates a new {@code Ansi} builder that continues from the state of
     * an existing {@code Ansi} parent instance.
     *
     * @param parent the parent instance whose content and pending attributes
     * are copied into this new builder
     */
    public Ansi(Ansi parent) {
        this(new StringBuilder(parent.builder));
        attributeOptions.addAll(parent.attributeOptions);
    }

    /**
     * Creates a new {@code Ansi} builder with the given initial capacity.
     *
     * @param size the initial capacity of the internal {@link StringBuilder}
     */
    public Ansi(int size) {
        this(new StringBuilder(size));
    }

    /**
     * Creates a new {@code Ansi} builder backed by the given {@link StringBuilder}.
     *
     * @param builder the backing string builder
     */
    public Ansi(StringBuilder builder) {
        this.builder = builder;
    }

    // ─────────────────────────────────────────────────────────────
    // Color methods
    // ─────────────────────────────────────────────────────────────

    /**
     * Sets the foreground (text) color using one of the 8 standard ANSI colors.
     *
     * @param color the foreground color to apply
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi fg(Color color) {
        attributeOptions.add(color.fg());
        return this;
    }

    /**
     * Sets the foreground color using a 256-color palette index (0–255).
     *
     * @param color the 256-color palette index
     * @return this {@code Ansi} instance for chaining
     * @see <a href="https://en.wikipedia.org/wiki/ANSI_escape_code#8-bit">8-bit color</a>
     */
    public Ansi fg(int color) {
        attributeOptions.add(38);
        attributeOptions.add(5);
        attributeOptions.add(color & 0xff);
        return this;
    }

    /**
     * Sets the foreground color using a 24-bit RGB value packed as
     * {@code 0xRRGGBB}.
     *
     * @param color the packed RGB color value
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi fgRgb(int color) {
        return fgRgb(color >> 16, color >> 8, color);
    }

    /**
     * Sets the foreground color using individual 24-bit RGB components.
     *
     * @param r red component (0–255)
     * @param g green component (0–255)
     * @param b blue component (0–255)
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi fgRgb(int r, int g, int b) {
        attributeOptions.add(38);
        attributeOptions.add(2);
        attributeOptions.add(r & 0xff);
        attributeOptions.add(g & 0xff);
        attributeOptions.add(b & 0xff);
        return this;
    }

    /** Sets the foreground color to black. @return this instance */
    public Ansi fgBlack()         { return fg(Color.BLACK); }
    /** Sets the foreground color to blue. @return this instance */
    public Ansi fgBlue()          { return fg(Color.BLUE); }
    /** Sets the foreground color to cyan. @return this instance */
    public Ansi fgCyan()          { return fg(Color.CYAN); }
    /** Resets the foreground color to the terminal default. @return this instance */
    public Ansi fgDefault()       { return fg(Color.DEFAULT); }
    /** Sets the foreground color to green. @return this instance */
    public Ansi fgGreen()         { return fg(Color.GREEN); }
    /** Sets the foreground color to magenta. @return this instance */
    public Ansi fgMagenta()       { return fg(Color.MAGENTA); }
    /** Sets the foreground color to red. @return this instance */
    public Ansi fgRed()           { return fg(Color.RED); }
    /** Sets the foreground color to yellow. @return this instance */
    public Ansi fgYellow()        { return fg(Color.YELLOW); }

    /**
     * Sets the background color using one of the 8 standard ANSI colors.
     *
     * @param color the background color to apply
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi bg(Color color) {
        attributeOptions.add(color.bg());
        return this;
    }

    /**
     * Sets the background color using a 256-color palette index (0–255).
     *
     * @param color the 256-color palette index
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi bg(int color) {
        attributeOptions.add(48);
        attributeOptions.add(5);
        attributeOptions.add(color & 0xff);
        return this;
    }

    /**
     * Sets the background color using a 24-bit RGB value packed as
     * {@code 0xRRGGBB}.
     *
     * @param color the packed RGB color value
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi bgRgb(int color) {
        return bgRgb(color >> 16, color >> 8, color);
    }

    /**
     * Sets the background color using individual 24-bit RGB components.
     *
     * @param r red component (0–255)
     * @param g green component (0–255)
     * @param b blue component (0–255)
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi bgRgb(int r, int g, int b) {
        attributeOptions.add(48);
        attributeOptions.add(2);
        attributeOptions.add(r & 0xff);
        attributeOptions.add(g & 0xff);
        attributeOptions.add(b & 0xff);
        return this;
    }

    /** Sets the background color to cyan. @return this instance */
    public Ansi bgCyan()          { return bg(Color.CYAN); }
    /** Resets the background color to the terminal default. @return this instance */
    public Ansi bgDefault()       { return bg(Color.DEFAULT); }
    /** Sets the background color to green. @return this instance */
    public Ansi bgGreen()         { return bg(Color.GREEN); }
    /** Sets the background color to magenta. @return this instance */
    public Ansi bgMagenta()       { return bg(Color.MAGENTA); }
    /** Sets the background color to red. @return this instance */
    public Ansi bgRed()           { return bg(Color.RED); }
    /** Sets the background color to yellow. @return this instance */
    public Ansi bgYellow()        { return bg(Color.YELLOW); }

    /**
     * Sets the foreground color to a bright/intense variant of the given color.
     *
     * @param color the color to apply at bright intensity
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi fgBright(Color color) {
        attributeOptions.add(color.fgBright());
        return this;
    }

    /** Sets the foreground to bright black (dark gray). @return this instance */
    public Ansi fgBrightBlack()   { return fgBright(Color.BLACK); }
    /** Sets the foreground to bright blue. @return this instance */
    public Ansi fgBrightBlue()    { return fgBright(Color.BLUE); }
    /** Sets the foreground to bright cyan. @return this instance */
    public Ansi fgBrightCyan()    { return fgBright(Color.CYAN); }
    /** Sets the foreground to bright default. @return this instance */
    public Ansi fgBrightDefault() { return fgBright(Color.DEFAULT); }
    /** Sets the foreground to bright green. @return this instance */
    public Ansi fgBrightGreen()   { return fgBright(Color.GREEN); }
    /** Sets the foreground to bright magenta. @return this instance */
    public Ansi fgBrightMagenta() { return fgBright(Color.MAGENTA); }
    /** Sets the foreground to bright red. @return this instance */
    public Ansi fgBrightRed()     { return fgBright(Color.RED); }
    /** Sets the foreground to bright yellow. @return this instance */
    public Ansi fgBrightYellow()  { return fgBright(Color.YELLOW); }

    /**
     * Sets the background color to a bright/intense variant of the given color.
     *
     * @param color the color to apply at bright intensity
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi bgBright(Color color) {
        attributeOptions.add(color.bgBright());
        return this;
    }

    /** Sets the background to bright cyan. @return this instance */
    public Ansi bgBrightCyan()    { return bgBright(Color.CYAN); }
    /** Sets the background to bright default. @return this instance */
    public Ansi bgBrightDefault() { return bgBright(Color.DEFAULT); }
    /** Sets the background to bright green. @return this instance */
    public Ansi bgBrightGreen()   { return bgBright(Color.GREEN); }
    /** Sets the background to bright magenta. @return this instance */
    public Ansi bgBrightMagenta() { return bgBright(Color.MAGENTA); }
    /** Sets the background to bright red. @return this instance */
    public Ansi bgBrightRed()     { return bgBright(Color.RED); }
    /** Sets the background to bright yellow. @return this instance */
    public Ansi bgBrightYellow()  { return bgBright(Color.YELLOW); }

    // ─────────────────────────────────────────────────────────────
    // Attribute method
    // ─────────────────────────────────────────────────────────────

    /**
     * Applies an SGR display attribute (bold, italic, underline, etc.).
     *
     * @param attribute the display attribute to apply
     * @return this {@code Ansi} instance for chaining
     * @see Attribute
     */
    public Ansi a(Attribute attribute) {
        attributeOptions.add(attribute.value());
        return this;
    }

    // ─────────────────────────────────────────────────────────────
    // Cursor positioning methods
    // ─────────────────────────────────────────────────────────────

    /**
     * Moves the cursor to the specified row and column (1-based).
     * Values less than 1 are clamped to 1.
     *
     * @param row    the target row (1-based, from the top)
     * @param column the target column (1-based, from the left)
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi cursor(final int row, final int column) {
        return appendEscapeSequence('H', Math.max(1, row), Math.max(1, column));
    }

    /**
     * Moves the cursor to the specified column on the current line (1-based).
     * Values less than 1 are clamped to 1.
     *
     * @param x the target column index (1-based)
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi cursorToColumn(final int x) {
        return appendEscapeSequence('G', Math.max(1, x));
    }

    /**
     * Moves the cursor up by {@code y} lines.
     * If {@code y} is negative, the cursor moves down instead.
     *
     * @param y the number of lines to move up
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi cursorUp(final int y) {
        return y > 0 ? appendEscapeSequence('A', y)
                : y < 0 ? cursorDown(-y)
                : this;
    }

    /**
     * Moves the cursor down by {@code y} lines.
     * If {@code y} is negative, the cursor moves up instead.
     *
     * @param y the number of lines to move down
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi cursorDown(final int y) {
        return y > 0 ? appendEscapeSequence('B', y)
                : y < 0 ? cursorUp(-y)
                : this;
    }

    /**
     * Moves the cursor right by {@code x} characters.
     * If {@code x} is negative, the cursor moves left instead.
     *
     * @param x the number of characters to move right
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi cursorRight(final int x) {
        return x > 0 ? appendEscapeSequence('C', x)
                : x < 0 ? cursorLeft(-x)
                : this;
    }

    /**
     * Moves the cursor left by {@code x} characters.
     * If {@code x} is negative, the cursor moves right instead.
     *
     * @param x the number of characters to move left
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi cursorLeft(final int x) {
        return x > 0 ? appendEscapeSequence('D', x)
                : x < 0 ? cursorRight(-x)
                : this;
    }

    /**
     * Moves the cursor relative to its current position.
     * Positive {@code x} moves right, positive {@code y} moves down.
     *
     * @param x horizontal displacement (positive = right, negative = left)
     * @param y vertical displacement (positive = down, negative = up)
     * @return this {@code Ansi} instance for chaining
     * @since 2.2
     */
    public Ansi cursorMove(final int x, final int y) {
        return cursorRight(x).cursorDown(y);
    }

    /**
     * Moves the cursor to the beginning of the next line.
     *
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi cursorDownLine() {
        return appendEscapeSequence('E');
    }

    /**
     * Moves the cursor to the beginning of the line {@code n} lines below.
     * If {@code n} is negative, moves up {@code |n|} lines instead.
     *
     * @param n the number of lines to move down (negative to move up)
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi cursorDownLine(final int n) {
        return n < 0 ? cursorUpLine(-n) : appendEscapeSequence('E', n);
    }

    /**
     * Moves the cursor to the beginning of the previous line.
     *
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi cursorUpLine() {
        return appendEscapeSequence('F');
    }

    /**
     * Moves the cursor to the beginning of the line {@code n} lines above.
     * If {@code n} is negative, moves down {@code |n|} lines instead.
     *
     * @param n the number of lines to move up (negative to move down)
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi cursorUpLine(final int n) {
        return n < 0 ? cursorDownLine(-n) : appendEscapeSequence('F', n);
    }

    // ─────────────────────────────────────────────────────────────
    // Erase methods
    // ─────────────────────────────────────────────────────────────

    /**
     * Erases the entire screen (equivalent to {@link Erase#ALL}).
     *
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi eraseScreen() {
        return appendEscapeSequence('J', Erase.ALL.value());
    }

    /**
     * Erases the screen according to the given {@link Erase} mode.
     *
     * @param kind the erase mode ({@link Erase#FORWARD}, {@link Erase#BACKWARD},
     * or {@link Erase#ALL})
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi eraseScreen(final Erase kind) {
        return appendEscapeSequence('J', kind.value());
    }

    /**
     * Erases from the cursor position to the end of the current line.
     *
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi eraseLine() {
        return appendEscapeSequence('K');
    }

    /**
     * Erases part of the current line according to the given {@link Erase} mode.
     *
     * @param kind the erase mode ({@link Erase#FORWARD}, {@link Erase#BACKWARD},
     * or {@link Erase#ALL})
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi eraseLine(final Erase kind) {
        return appendEscapeSequence('K', kind.value());
    }

    // ─────────────────────────────────────────────────────────────
    // Scroll methods
    // ─────────────────────────────────────────────────────────────

    /**
     * Scrolls the terminal up by the given number of rows.
     * Negative values scroll down instead.
     *
     * @param rows number of rows to scroll up
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi scrollUp(final int rows) {
        if (rows == Integer.MIN_VALUE) return scrollDown(Integer.MAX_VALUE);
        return rows > 0 ? appendEscapeSequence('S', rows)
                : rows < 0 ? scrollDown(-rows)
                : this;
    }

    /**
     * Scrolls the terminal down by the given number of rows.
     * Negative values scroll up instead.
     *
     * @param rows number of rows to scroll down
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi scrollDown(final int rows) {
        if (rows == Integer.MIN_VALUE) return scrollUp(Integer.MAX_VALUE);
        return rows > 0 ? appendEscapeSequence('T', rows)
                : rows < 0 ? scrollUp(-rows)
                : this;
    }

    // ─────────────────────────────────────────────────────────────
    // Cursor save / restore
    // ─────────────────────────────────────────────────────────────

    /**
     * Saves the current cursor position using both the SCO ({@code ESC [s})
     * and DEC ({@code ESC 7}) sequences for maximum terminal compatibility.
     *
     * @return this {@code Ansi} instance for chaining
     * @see #restoreCursorPosition()
     */
    public Ansi saveCursorPosition() {
        saveCursorPositionSCO();
        return saveCursorPositionDEC();
    }

    /**
     * Saves the cursor position using the SCO sequence ({@code ESC [s}).
     *
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi saveCursorPositionSCO() {
        return appendEscapeSequence('s');
    }

    /**
     * Saves the cursor position using the DEC sequence ({@code ESC 7}).
     *
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi saveCursorPositionDEC() {
        builder.append(ESC);
        builder.append('7');
        return this;
    }

    /**
     * Restores the cursor position previously saved by {@link #saveCursorPosition()}.
     * Issues both SCO ({@code ESC [u}) and DEC ({@code ESC 8}) restore sequences.
     *
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi restoreCursorPosition() {
        restoreCursorPositionSCO();
        return restoreCursorPositionDEC();
    }

    /**
     * Restores the cursor position using the SCO sequence ({@code ESC [u}).
     *
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi restoreCursorPositionSCO() {
        return appendEscapeSequence('u');
    }

    /**
     * Restores the cursor position using the DEC sequence ({@code ESC 8}).
     *
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi restoreCursorPositionDEC() {
        builder.append(ESC);
        builder.append('8');
        return this;
    }

    /**
     * @deprecated Typo in the original API. Use {@link #restoreCursorPosition()} instead.
     */
    @Deprecated
    public Ansi restorCursorPosition() {
        return restoreCursorPosition();
    }

    // ─────────────────────────────────────────────────────────────
    // Convenience attribute shortcuts
    // ─────────────────────────────────────────────────────────────

    /**
     * Resets all text attributes (colors, bold, underline, etc.) to terminal defaults.
     * Equivalent to {@code a(Attribute.RESET)}.
     *
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi reset() {
        return a(Attribute.RESET);
    }

    /**
     * Enables bold (high intensity) text rendering.
     * Equivalent to {@code a(Attribute.INTENSITY_BOLD)}.
     *
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi bold() {
        return a(Attribute.INTENSITY_BOLD);
    }

    /**
     * Disables bold text rendering.
     * Equivalent to {@code a(Attribute.INTENSITY_BOLD_OFF)}.
     *
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi boldOff() {
        return a(Attribute.INTENSITY_BOLD_OFF);
    }

    // ─────────────────────────────────────────────────────────────
    // append() methods — the primary text-appending API
    // (Rename Method refactoring: replaces the single-letter a() methods)
    // ─────────────────────────────────────────────────────────────

    /**
     * Appends a {@link String} value to this ANSI sequence.
     *
     * <p>Any pending color or attribute changes are flushed before the
     * text is written, so the text appears with the correct attributes.</p>
     *
     * <p><b>Refactoring note:</b> This method replaces the original {@code a(String)}
     * which was a single-letter name that communicated no intent — a classic
     * Uncommunicative Naming / Primitive Obsession code smell (Smell #25 in
     * this project's analysis). The original {@code a(String)} is kept as a
     * deprecated alias for backward compatibility.</p>
     *
     * @param value the text to append
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi append(String value) {
        flushAttributes();
        builder.append(value);
        return this;
    }

    /**
     * Appends a {@code boolean} value to this ANSI sequence.
     *
     * @param value the value to append
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi append(boolean value) {
        flushAttributes();
        builder.append(value);
        return this;
    }

    /**
     * Appends a subrange of a {@code char[]} array to this ANSI sequence.
     *
     * @param value  the source character array
     * @param offset the start index within {@code value}
     * @param len    the number of characters to append
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi append(char[] value, int offset, int len) {
        flushAttributes();
        builder.append(value, offset, len);
        return this;
    }

    /**
     * Appends all characters in a {@code char[]} array to this ANSI sequence.
     *
     * @param value the character array to append
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi append(char[] value) {
        flushAttributes();
        builder.append(value);
        return this;
    }

    /**
     * Appends a {@code double} value to this ANSI sequence.
     *
     * @param value the value to append
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi append(double value) {
        flushAttributes();
        builder.append(value);
        return this;
    }

    /**
     * Appends a {@code float} value to this ANSI sequence.
     *
     * @param value the value to append
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi append(float value) {
        flushAttributes();
        builder.append(value);
        return this;
    }

    /**
     * Appends an {@code int} value to this ANSI sequence.
     *
     * @param value the value to append
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi append(int value) {
        flushAttributes();
        builder.append(value);
        return this;
    }

    /**
     * Appends a {@code long} value to this ANSI sequence.
     *
     * @param value the value to append
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi append(long value) {
        flushAttributes();
        builder.append(value);
        return this;
    }

    /**
     * Appends the string representation of an {@link Object} to this ANSI sequence.
     *
     * @param value the object whose {@code toString()} value is appended
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi append(Object value) {
        flushAttributes();
        builder.append(value);
        return this;
    }

    /**
     * Appends a {@link StringBuffer} to this ANSI sequence.
     *
     * @param value the string buffer to append
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi append(StringBuffer value) {
        flushAttributes();
        builder.append(value);
        return this;
    }

    // ─────────────────────────────────────────────────────────────
    // Deprecated a() overloads — kept for backward compatibility
    // All delegate cleanly to the new append overloads.
    // ─────────────────────────────────────────────────────────────

    /**
     * @deprecated Use {@link #append(String)} instead.
     * Renamed to communicate intent — this method appends text
     * to the escape sequence being built.
     */
    @Deprecated
    public Ansi a(String value)                          { return append(value); }

    /**
     * @deprecated Use {@link #append(boolean)} instead.
     */
    @Deprecated
    public Ansi a(boolean value)                         { return append(value); }

    /**
     * @deprecated Use {@link #append(char)} instead.
     */
    @Deprecated
    public Ansi a(char value)                            { return append(value); }

    /**
     * @deprecated Use {@link #append(char[], int, int)} instead.
     */
    @Deprecated
    public Ansi a(char[] value, int offset, int len)     { return append(value, offset, len); }

    /**
     * @deprecated Use {@link #append(char[])} instead.
     */
    @Deprecated
    public Ansi a(char[] value)                          { return append(value); }

    /**
     * @deprecated Use {@link #append(CharSequence, int, int)} instead.
     */
    @Deprecated
    public Ansi a(CharSequence value, int start, int end){ return append(value, start, end); }

    /**
     * @deprecated Use {@link #append(CharSequence)} — inherited from {@link Appendable}.
     */
    @Deprecated
    public Ansi a(CharSequence value)                    { return append(value); }

    /**
     * @deprecated Use {@link #append(double)} instead.
     */
    @Deprecated
    public Ansi a(double value)                          { return append(value); }

    /**
     * @deprecated Use {@link #append(float)} instead.
     */
    @Deprecated
    public Ansi a(float value)                           { return append(value); }

    /**
     * @deprecated Use {@link #append(int)} instead.
     */
    @Deprecated
    public Ansi a(int value)                             { return append(value); }

    /**
     * @deprecated Use {@link #append(long)} instead.
     */
    @Deprecated
    public Ansi a(long value)                            { return append(value); }

    /**
     * @deprecated Use {@link #append(Object)} instead.
     */
    @Deprecated
    public Ansi a(Object value)                          { return append(value); }

    /**
     * @deprecated Use {@link #append(StringBuffer)} instead.
     */
    @Deprecated
    public Ansi a(StringBuffer value)                    { return append(value); }

    // ─────────────────────────────────────────────────────────────
    // Utility methods
    // ─────────────────────────────────────────────────────────────

    /**
     * Appends a newline character to this ANSI sequence.
     *
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi newline() {
        flushAttributes();
        builder.append(System.getProperty("line.separator"));
        return this;
    }

    /**
     * Formats and appends a string using {@link String#format(String, Object...)}.
     *
     * @param pattern the format string
     * @param args    the format arguments
     * @return this {@code Ansi} instance for chaining
     */
    public Ansi format(String pattern, Object... args) {
        flushAttributes();
        builder.append(String.format(pattern, args));
        return this;
    }

    /**
     * Applies a {@link Consumer} function to this {@code Ansi} instance,
     * enabling inline customization without breaking the fluent chain.
     *
     * @param fun the function to apply; must not be {@code null}
     * @return this {@code Ansi} instance for chaining
     * @since 2.2
     */
    public Ansi apply(Consumer fun) {
        fun.apply(this);
        return this;
    }

    /**
     * Renders the given ANSI markup text using {@link AnsiRenderer} and
     * appends the result to this sequence.
     *
     * @param text the markup text to render (e.g. {@code "@|bold Hello|@"})
     * @return this {@code Ansi} instance for chaining
     * @since 2.2
     * @see AnsiRenderer
     */
    public Ansi render(final String text) {
        append(AnsiRenderer.render(text));
        return this;
    }

    /**
     * Formats the given markup text with {@link String#format} arguments,
     * then renders it using {@link AnsiRenderer} and appends the result.
     *
     * @param text format string containing ANSI markup
     * @param args format arguments
     * @return this {@code Ansi} instance for chaining
     * @since 2.2
     */
    public Ansi render(final String text, Object... args) {
        append(String.format(AnsiRenderer.render(text), args));
        return this;
    }

    /**
     * Builds and returns the final ANSI escape sequence string.
     * Any pending color or attribute changes are flushed before returning.
     *
     * @return the fully assembled ANSI escape sequence as a {@link String}
     */
    @Override
    public String toString() {
        flushAttributes();
        return builder.toString();
    }

    // ─────────────────────────────────────────────────────────────
    // Appendable interface implementation
    // ─────────────────────────────────────────────────────────────

    /**
     * Appends a {@link CharSequence} to this ANSI sequence (implements {@link Appendable}).
     *
     * @param csq the character sequence to append
     * @return this {@code Ansi} instance for chaining
     */
    @Override
    public Ansi append(CharSequence csq) {
        flushAttributes();
        builder.append(csq);
        return this;
    }

    /**
     * Appends a subrange of a {@link CharSequence} (implements {@link Appendable}).
     *
     * @param csq   the source character sequence
     * @param start start index (inclusive)
     * @param end   end index (exclusive)
     * @return this {@code Ansi} instance for chaining
     */
    @Override
    public Ansi append(CharSequence csq, int start, int end) {
        flushAttributes();
        builder.append(csq, start, end);
        return this;
    }

    /**
     * Appends a single character (implements {@link Appendable}).
     *
     * @param c the character to append
     * @return this {@code Ansi} instance for chaining
     */
    @Override
    public Ansi append(char c) {
        flushAttributes();
        builder.append(c);
        return this;
    }

    // ─────────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────────

    /**
     * Appends an ANSI CSI escape sequence with no numeric parameter.
     *
     * @param command the single-character CSI command letter
     * @return this {@code Ansi} instance for chaining
     */
    private Ansi appendEscapeSequence(char command) {
        flushAttributes();
        builder.append(ESC);
        builder.append(CSI_OPEN);
        builder.append(command);
        return this;
    }

    /**
     * Appends an ANSI CSI escape sequence with a single numeric parameter.
     *
     * @param command the CSI command letter
     * @param option  the numeric parameter
     * @return this {@code Ansi} instance for chaining
     */
    private Ansi appendEscapeSequence(char command, int option) {
        flushAttributes();
        builder.append(ESC);
        builder.append(CSI_OPEN);
        builder.append(option);
        builder.append(command);
        return this;
    }

    /**
     * Appends an ANSI CSI escape sequence with multiple parameters.
     *
     * @param command the CSI command letter
     * @param options the parameter values (separated by {@code ;} in the output)
     * @return this {@code Ansi} instance for chaining
     */
    private Ansi appendEscapeSequence(char command, Object... options) {
        flushAttributes();
        return appendEscapeSequenceRaw(command, options);
    }

    /**
     * Flushes any pending SGR attribute options into the output builder.
     *
     * <p>A reset-only flush ({@code ESC [m}) is optimized as a shorter sequence
     * compared to the general multi-parameter form ({@code ESC [0m}).</p>
     */
    private void flushAttributes() {
        if (attributeOptions.isEmpty()) return;
        if (attributeOptions.size() == 1 && attributeOptions.get(0) == 0) {
            builder.append(ESC);
            builder.append(CSI_OPEN);
            builder.append('m');
        } else {
            appendEscapeSequenceRaw('m', attributeOptions.toArray());
        }
        attributeOptions.clear();
    }

    /**
     * Core CSI sequence writer — appends {@code ESC [ options... command}.
     *
     * <p>Previously named {@code _appendEscapeSequence} with a leading underscore,
     * which is not a valid Java naming convention. Renamed to
     * {@code appendEscapeSequenceRaw} for clarity.</p>
     *
     * @param command the CSI command letter
     * @param options the parameter values; {@code null} entries are written as empty
     * @return this {@code Ansi} instance for chaining
     */
    private Ansi appendEscapeSequenceRaw(char command, Object... options) {
        builder.append(ESC);
        builder.append(CSI_OPEN);
        for (int i = 0; i < options.length; i++) {
            if (i != 0) builder.append(';');
            if (options[i] != null) builder.append(options[i]);
        }
        builder.append(command);
        return this;
    }
}