package org.fusesource.jansi;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class AnsiFilterWriterTest {

    private SpyCharArrayWriter spyOutput = new SpyCharArrayWriter();

    /** system under test */
    private AnsiFilterWriter sut = new AnsiFilterWriter(spyOutput, SpyTerminalCommandProcessor.SPY_TERM);

    private SpyTerminalCommandProcessor spyTerminal = (SpyTerminalCommandProcessor) sut.getTerminalCommandProcessor();

    // Test identical input/output (no escape ANSI codes)
    // ------------------------------------------------------------------------

    @Test
    public void testWrite_singleChar_ident() throws IOException {
        // Given
        // When
        sut.write((int) 'a');
        // Then
        Assert.assertEquals("a", spyOutput.toString());
        Assert.assertEquals(1, spyOutput.countWriteCharArray--); // only 1 char written, but using array..
        spyOutput.countWriteChar = 0;
        Assert.assertEquals(0, spyOutput.getSumCounts());
    }

    @Test
    public void testWrite_charArray_ident() throws IOException {
        // Given
        String text = "Hello World";
        char[] ch = text.toCharArray();
        // When
        sut.write(ch);
        // Then
        Assert.assertEquals(text, spyOutput.toString());
        Assert.assertEquals(1, spyOutput.countWriteCharArray);
        spyOutput.countWriteCharArray = 0;
        Assert.assertEquals(0, spyOutput.getSumCounts());
    }

    @Test
    public void testWrite_charArrayFrag_ident() throws IOException {
        // Given
        String text = "Hello World";
        char[] ch = ("xx" + text + "yy").toCharArray();
        // When
        sut.write(ch, 2, text.length());
        // Then
        Assert.assertEquals(text, spyOutput.toString());
        Assert.assertEquals(1, spyOutput.countWriteCharArray);
        spyOutput.countWriteCharArray = 0;
        Assert.assertEquals(0, spyOutput.getSumCounts());
    }

    @Test
    public void testWrite_string_ident() throws IOException {
        // Given
        String text = "Hello World";
        // When
        sut.write(text);
        // Then
        Assert.assertEquals(text, spyOutput.toString());
        Assert.assertEquals(1, spyOutput.countWriteCharArray); // calls output char[] for String
        spyOutput.countWriteCharArray = 0;
        Assert.assertEquals(0, spyOutput.getSumCounts());
    }

    @Test
    public void testWrite_stringFrag_ident() throws IOException {
        // Given
        String text = "Hello World";
        // When
        sut.write("xx" + text + "yy", 2, text.length());
        // Then
        Assert.assertEquals(text, spyOutput.toString());
        Assert.assertEquals(1, spyOutput.countWriteCharArray); // calls output char[] for String
        spyOutput.countWriteCharArray = 0;
        Assert.assertEquals(0, spyOutput.getSumCounts());
    }

    @Test
    public void testAppend_singleChar_ident() throws IOException {
        // Given
        // When
        sut.append('a');
        // Then
        Assert.assertEquals("a", spyOutput.toString());
        Assert.assertEquals(1, spyOutput.countWriteCharArray--); // only 1 char written, but using array..
        Assert.assertEquals(0, spyOutput.getSumCounts());
    }

    // Test with escape ANSI codes..
    // ------------------------------------------------------------------------

    @Test
    public void testWrite_string_ansi() throws IOException {
        // Given
        String ansiText = Ansi.ansi()
                .bgGreen().a("Hello")
                .reset().a(" ")
                .fgBlue().a("World")
                .reset().toString();
        String expectedText = "Hello World";
        // When
        sut.write(ansiText);
        // Then
        Assert.assertEquals(expectedText, spyOutput.toString());
        Assert.assertEquals(1, spyOutput.countWriteCharArray); // calls output char[] for String
        spyOutput.countWriteCharArray = 0;
        Assert.assertEquals(0, spyOutput.getSumCounts());

        Assert.assertEquals(2, spyTerminal.countProcessAttributeRest);
        spyTerminal.countProcessAttributeRest = 0;
        Assert.assertEquals(1, spyTerminal.countProcessSetBackgroundColor--);
        Assert.assertEquals(1, spyTerminal.countProcessSetBackgroundColor2--);
        Assert.assertEquals(1, spyTerminal.countProcessSetForegroundColor--);
        Assert.assertEquals(1, spyTerminal.countProcessSetForegroundColor2--);
        Assert.assertEquals(0, spyTerminal.getSumCounts());
    }

    // Test with escape ANSI TerminalProcessor codes..
    // ------------------------------------------------------------------------

    @Test
    public void testWrite_string_ansi_bgColor() throws IOException {
        // Given
        String ansiText = Ansi.ansi()
                .bgGreen().a("Hello")
                .reset()
                .a(" World")
                .toString();
        Assert.assertEquals("[42mHello[m World", ansiText);
        String expectedText = "Hello World";
        // When
        sut.write(ansiText);
        // Then
        Assert.assertEquals(expectedText, spyOutput.toString());
        Assert.assertEquals(1, spyOutput.countWriteCharArray); // calls output char[] for String
        spyOutput.countWriteCharArray = 0;
        Assert.assertEquals(0, spyOutput.getSumCounts());

        Assert.assertEquals(1, spyTerminal.countProcessAttributeRest--);
        Assert.assertEquals(1, spyTerminal.countProcessSetBackgroundColor--);
        Assert.assertEquals(1, spyTerminal.countProcessSetBackgroundColor2--);
        Assert.assertEquals(0, spyTerminal.getSumCounts());
    }


    @Test
    public void testWrite_string_ansi_fgColor() throws IOException {
        // Given
        String ansiText = Ansi.ansi()
                .fgGreen().a("Hello")
                .reset()
                .a(" World")
                .toString();
        Assert.assertEquals("[32mHello[m World", ansiText);
        String expectedText = "Hello World";
        // When
        sut.write(ansiText);
        // Then
        Assert.assertEquals(expectedText, spyOutput.toString());
        Assert.assertEquals(1, spyOutput.countWriteCharArray); // calls output char[] for String
        spyOutput.countWriteCharArray = 0;
        Assert.assertEquals(0, spyOutput.getSumCounts());

        Assert.assertEquals(1, spyTerminal.countProcessAttributeRest--);
        Assert.assertEquals(1, spyTerminal.countProcessSetForegroundColor--);
        Assert.assertEquals(1, spyTerminal.countProcessSetForegroundColor2--);
        Assert.assertEquals(0, spyTerminal.getSumCounts());
    }

    @Test
    public void testWrite_string_ansi_terminalCursorMove() throws IOException {
        // Given
        String ansiText = Ansi.ansi()
                .a("Hello")
                .cursorLeft(5)
                .a(" ")
                .cursorUp(5)
                .cursorDown(5)
                .cursorRight(5)
                .a("World")
                .toString();
        Assert.assertEquals("Hello[5D [5A[5B[5CWorld", ansiText);
        String expectedText = "Hello      World"; // NOTICE: moveCursorRight => insert ' ' (commented as "poor man implementation")  
        // When
        sut.write(ansiText);
        String resText = spyOutput.toString();
        // Then
        Assert.assertEquals(expectedText, resText);
        Assert.assertEquals(1, spyOutput.countWriteCharArray); // calls output char[] for String
        spyOutput.countWriteCharArray = 0;
        Assert.assertEquals(0, spyOutput.getSumCounts());

        Assert.assertEquals(1, spyTerminal.countProcessCursorLeft--);
        Assert.assertEquals(1, spyTerminal.countProcessCursorUp--);
        Assert.assertEquals(1, spyTerminal.countProcessCursorDown--);
        Assert.assertEquals(1, spyTerminal.countProcessCursorRight--);
        Assert.assertEquals(0, spyTerminal.getSumCounts());
    }

}
