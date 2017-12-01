package org.fusesource.jansi;

import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.Test;

public class FilterPrintStreamTest
{
    @Test
    public void testPrintMethods()
        throws Exception
    {
        PrintStream ps = new FilterPrintStream(System.out);
        ps.println("String");
        ps.println('€');
        ps.flush();
    }

    @Test
    public void testWrite()
        throws Exception
    {
        OutputStream os = new FilterPrintStream(System.out);
        os.write('A');
        os.write('B');
        os.write("€".getBytes() );
        os.flush();
    }
}