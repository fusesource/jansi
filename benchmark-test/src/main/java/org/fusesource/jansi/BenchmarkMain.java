package org.fusesource.jansi;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.Color.RED;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

import org.fusesource.jansi.Ansi.Color;

public class BenchmarkMain {

    boolean interactive = false;
    int outerLoop = 5;
    int innerLoop = 100;

    public static void main(String[] args) throws IOException {
        BenchmarkMain app = new BenchmarkMain();
        app.parseArgs(args);
        app.run();
    }

    public void parseArgs(String[] args) {
        for(int i = 0; i < args.length; i++) {
            String a = args[i];
            if ("-i".equals(a) || "--interactive".equals(a)) {
                interactive = true;
            } else if ("--outerLoop".equals(a)) {
                outerLoop = Integer.parseInt(args[++i]);
            } else if ("--innerLoop".equals(a)) {
                outerLoop = Integer.parseInt(args[++i]);
            } else if ("--help".equals(a)) {
                printHelpUsage();
            } else {
                System.err.println("Unrecognized argument " + a);
                printHelpUsage();
            }
        }
    }

    private void printHelpUsage() {
        System.out.println("Usage: java -cp benchmark-test.jar org.fusesource.jansi.BenchmarkMain " +
                "-i|--interactive" +
                " --outerLoop <number,default:5>" +
                " --innerLoop <number,default:100>");
    }

    public void run() throws IOException {
        AnsiMain.main();

        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

        // first run without Jansi installed
        long millisStdoutBefore = benchSysout(outerLoop, innerLoop);

        // install => change System.out .. now performances are impacted
        AnsiConsole.systemInstall(); 

        System.out.println( ansi().eraseScreen().fg(RED).a("Hello").fg(GREEN).a(" World").reset() );

        for(;;) {
            long millisJansi = benchJansi(outerLoop, innerLoop);
            long millisStdout = benchSysout(outerLoop, innerLoop);

            long millisJansiBigLines = benchJansiBigLines(outerLoop / 2, innerLoop / 10);
            long millisStdoutBigLines = benchSysoutBigLines(outerLoop / 2, innerLoop / 10);

            long millisJansiNoFlush = benchJansiNoFlush(outerLoop, innerLoop);
            long millisStdoutNoFlush = benchSysoutNoFlush(outerLoop, innerLoop);

            long overheadAnsiJAnsiPrintStream = benchOverheadAnsiJansiPrintStream(outerLoop, innerLoop);
            long overheadRawJAnsiPrintStream = benchOverheadRawJansiPrintStream(outerLoop, innerLoop);

            int countFlushAnsiJansiPrintStream = countFlushAnsiJansiPrintStream(outerLoop, innerLoop);
            int countFlushRawJansiPrintStream = countFlushRawJansiPrintStream(outerLoop, innerLoop);
            
            System.out.println(ansi().eraseScreen());
            System.out.println("loop Stdout Before " + outerLoop + "x" + innerLoop + " took " + 
                    millisStdoutBefore + "ms");

            System.out.println("loop Jansi " + outerLoop + "x" + innerLoop + " took " + 
                    millisJansi + "ms");
            System.out.println("loop Stdout " + outerLoop + "x" + innerLoop + " took " + 
                    millisStdout + "ms");

            System.out.println("loop Jansi BigLines " + outerLoop + "x" + innerLoop + " took " + 
                    millisJansiBigLines + "ms");
            System.out.println("loop Stdout BigLines " + outerLoop + "x" + innerLoop + " took " + 
                    millisStdoutBigLines + "ms");

            System.out.println("loop Jansi NoFlush " + outerLoop + "x" + innerLoop + " took " + 
                    millisJansiNoFlush + "ms");
            System.out.println("loop Stdout NoFlush " + outerLoop + "x" + innerLoop + " took " + 
                    millisStdoutNoFlush + "ms");

            System.out.println("loop Overhead ansi + JansiPrintSteam " + outerLoop + "x" + innerLoop + " took " + 
                    overheadAnsiJAnsiPrintStream + "ms");
            System.out.println("loop Overhead Raw JansiPrintSteam " + outerLoop + "x" + innerLoop + " took " + 
                    overheadRawJAnsiPrintStream + "ms");

            System.out.println("count flush() ansi+JansiPrintStream " + outerLoop + "x" + innerLoop + " : " + countFlushAnsiJansiPrintStream);
            System.out.println("count flush() raw+JansiPrintStream " + outerLoop + "x" + innerLoop + " : " + countFlushRawJansiPrintStream);
            
            if (!interactive) {
                break;
            }
            System.out.println("enter 'q' to exit, jansi,stdout,jansiNoFlush,stdoutNoFlush,overheadJansiPrintStream,overheadRawJansiPrintStream or any other to repeat");
            String line = stdin.readLine();
            if ("q".equals(line)) {
                break;
            }
            if (line == null) {
                continue;
            }
            if (line.equals("jansi")) { 
                benchJansi(outerLoop, innerLoop*10);
            } else if (line.equals("stdout")) {
                benchSysout(outerLoop, innerLoop*10);
            } else if (line.equals("jansiNoFlush")) {
                benchJansiNoFlush(outerLoop, innerLoop*10);
            } else if (line.equals("stdoutNoFlush")) {
                benchSysoutNoFlush(outerLoop, innerLoop*10);
            } else if (line.equals("overheadJansiPrintStream")) {
                benchOverheadAnsiJansiPrintStream(outerLoop, innerLoop*10);
            } else if (line.equals("overheadRawJansiPrintStream")) {
                benchOverheadRawJansiPrintStream(outerLoop, innerLoop*10);
            }
        }

        AnsiConsole.systemUninstall();
    }

    private static long benchJansi(int outerLoop, int innerLoop) {
        long nanos = 0;
        long prevInnerMillis = 0;
        for(int i = 0; i < outerLoop; i++) {
            long innerNanosStart = System.nanoTime();
            for(int j = 0; j < innerLoop; j++) {
                System.out.println( ansi().fg(RED).a("Hello")
                            .fg(GREEN).a(" World")
                            .fg(Color.DEFAULT).a(" " + i)
                            .fg(Color.BLUE).a(" " + j)
                            .fg(Color.DEFAULT).a(" loop took " + prevInnerMillis + "ms")
                        );
            }
            long innerNanos = System.nanoTime() - innerNanosStart;
            nanos += innerNanos;
            prevInnerMillis = TimeUnit.NANOSECONDS.toMillis(innerNanos);
            // System.out.println( ansi().eraseScreen()); 
        }

        System.out.println(ansi().eraseScreen());
        System.out.println("loop " + outerLoop + " took " + 
                TimeUnit.NANOSECONDS.toMillis(nanos) + "ms");
        return TimeUnit.NANOSECONDS.toMillis(nanos);
    }

    private static long benchSysout(int outerLoop, int innerLoop) {
        long nanos = 0;
        long prevInnerMillis = 0;
        for(int i = 0; i < outerLoop; i++) {
            long innerNanosStart = System.nanoTime();
            for(int j = 0; j < innerLoop; j++) {
                System.out.println("Hello World"
                        + " " + i
                        + " " + j
                        + " loop took " + prevInnerMillis + "ms"
                        );
            }
            long innerNanos = System.nanoTime() - innerNanosStart;
            nanos += innerNanos;
            prevInnerMillis = TimeUnit.NANOSECONDS.toMillis(innerNanos);
        }

        System.out.println(ansi().eraseScreen());
        System.out.println("loop " + outerLoop + " took " + 
                TimeUnit.NANOSECONDS.toMillis(nanos) + "ms");
        return TimeUnit.NANOSECONDS.toMillis(nanos);
    }

    // Benchmark for Big Lines (more than 500 chars..)
    // ------------------------------------------------------------------------
    
    private static long benchJansiBigLines(int outerLoop, int innerLoop) {
        String bigLine = "";
        for(int i = 0; i < 50; i++) {
            bigLine += ansi().fg(RED).a("Hello")
                    .fg(GREEN).a(" World ... BIG Line .. ")
                    .fg(Color.DEFAULT).toString();
        }
        long nanos = 0;
        long prevInnerMillis = 0;
        for(int i = 0; i < outerLoop; i++) {
            long innerNanosStart = System.nanoTime();
            for(int j = 0; j < innerLoop; j++) {
                System.out.println(bigLine);
                System.out.println(" loop took " + prevInnerMillis + "ms");
            }
            long innerNanos = System.nanoTime() - innerNanosStart;
            nanos += innerNanos;
            prevInnerMillis = TimeUnit.NANOSECONDS.toMillis(innerNanos);
            // System.out.println( ansi().eraseScreen()); 
        }

        System.out.println(ansi().eraseScreen());
        System.out.println("loop Big Lines " + outerLoop + " took " + 
                TimeUnit.NANOSECONDS.toMillis(nanos) + "ms");
        return TimeUnit.NANOSECONDS.toMillis(nanos);
    }

    private static long benchSysoutBigLines(int outerLoop, int innerLoop) {
        String bigLine = "";
        for(int i = 0; i < 50; i++) {
            bigLine += "Hello World ... BIG Line .. ";
        }
        long nanos = 0;
        long prevInnerMillis = 0;
        for(int i = 0; i < outerLoop; i++) {
            long innerNanosStart = System.nanoTime();
            for(int j = 0; j < innerLoop; j++) {
                System.out.println(bigLine);
                System.out.println(" loop took " + prevInnerMillis + "ms");
            }
            long innerNanos = System.nanoTime() - innerNanosStart;
            nanos += innerNanos;
            prevInnerMillis = TimeUnit.NANOSECONDS.toMillis(innerNanos);
        }
        System.out.println(ansi().eraseScreen());
        System.out.println("loop " + outerLoop + " took " + 
                TimeUnit.NANOSECONDS.toMillis(nanos) + "ms");
        return TimeUnit.NANOSECONDS.toMillis(nanos);
    }

    // Benchmark test using print() instead of println() .. so supposedly not flushing
    // ------------------------------------------------------------------------

    private static long benchJansiNoFlush(int outerLoop, int innerLoop) {
        long nanos = 0;
        long prevInnerMillis = 0;
        for(int i = 0; i < outerLoop; i++) {
            long innerNanosStart = System.nanoTime();
            for(int j = 0; j < innerLoop; j++) {
                System.out.print( ansi().fg(RED).a("Hello")
                            .fg(GREEN).a(" World")
                            .fg(Color.DEFAULT).a(" NoFlush " + i)
                            .fg(Color.BLUE).a(" " + j)
                            .fg(Color.DEFAULT).a(" loop took " + prevInnerMillis + "ms")
                            .fg(Color.DEFAULT).a('\n')
                        );
            }
            long innerNanos = System.nanoTime() - innerNanosStart;
            nanos += innerNanos;
            prevInnerMillis = TimeUnit.NANOSECONDS.toMillis(innerNanos);
            // System.out.println( ansi().eraseScreen()); 
            System.out.flush();
        }
        System.out.println(ansi().eraseScreen());
        System.out.println("loop " + outerLoop + " took " + 
                TimeUnit.NANOSECONDS.toMillis(nanos) + "ms");
        return TimeUnit.NANOSECONDS.toMillis(nanos);
    }

    private static long benchSysoutNoFlush(int outerLoop, int innerLoop) {
        long nanos = 0;
        long prevInnerMillis = 0;
        for(int i = 0; i < outerLoop; i++) {
            long innerNanosStart = System.nanoTime();
            for(int j = 0; j < innerLoop; j++) {
                System.out.print("Hello World NoFlush"
                        + " " + i
                        + " " + j
                        + " loop took " + prevInnerMillis + "ms"
                        + "\n");
            }
            long innerNanos = System.nanoTime() - innerNanosStart;
            nanos += innerNanos;
            prevInnerMillis = TimeUnit.NANOSECONDS.toMillis(innerNanos);
            System.out.flush();
        }
        
        System.out.println(ansi().eraseScreen());
        System.out.println("loop " + outerLoop + " took " + 
                TimeUnit.NANOSECONDS.toMillis(nanos) + "ms");
        return TimeUnit.NANOSECONDS.toMillis(nanos);
    }

    // Benchmark using /dev/null
    // ------------------------------------------------------------------------
    
    /**
     * benchmark for overhead of <code>org.fusesource.jansi.AnsiPrintStream</code>, 
     * but connecting it to a /dev/null java equivalent
     * so without actually writing to stdout with system calls.
     */
    private static long benchOverheadAnsiJansiPrintStream(int outerLoop, int innerLoop) {
        NullOutputStream devNull = new NullOutputStream();
        PrintStream ansiPrintStream = new AnsiPrintStream(new PrintStream(devNull));

        long nanos = 0;
        long prevInnerMillis = 0;
        for(int i = 0; i < outerLoop; i++) {
            long innerNanosStart = System.nanoTime();
            for(int j = 0; j < innerLoop; j++) {
                ansiPrintStream.println( ansi().fg(RED).a("Hello")
                        .fg(GREEN).a(" World")
                        .fg(Color.DEFAULT).a(" NoFlush " + i)
                        .fg(Color.BLUE).a(" " + j)
                        .fg(Color.DEFAULT).a(" loop took " + prevInnerMillis + "ms")
                        );
            }
            long innerNanos = System.nanoTime() - innerNanosStart;
            nanos += innerNanos;
            prevInnerMillis = TimeUnit.NANOSECONDS.toMillis(innerNanos);
        }

        ansiPrintStream.close();
        System.out.println(ansi().eraseScreen());
        System.out.println("loop benchOverheadJansiPrintStream " + outerLoop + "x" + innerLoop + " took " + 
                TimeUnit.NANOSECONDS.toMillis(nanos) + "ms");
        return TimeUnit.NANOSECONDS.toMillis(nanos);
    }

    /**
     * benchmark for overhead of <code>org.fusesource.jansi.AnsiPrintStream</code>, 
     * but connecting it to a /dev/null java equivalent
     * so without actually writing to stdout with system calls.
     */
    private static long benchOverheadRawJansiPrintStream(int outerLoop, int innerLoop) {
        OutputStream devNull = new NullOutputStream();
        PrintStream ansiPrintStream = new AnsiPrintStream(new PrintStream(devNull));
        
        long nanos = 0;
        long prevInnerMillis = 0;
        for(int i = 0; i < outerLoop; i++) {
            long innerNanosStart = System.nanoTime();
            for(int j = 0; j < innerLoop; j++) {
                ansiPrintStream.print("Hello World NoFlush"
                        + " " + i
                        + " " + j
                        + " loop took " + prevInnerMillis + "ms"
                        + "\n");
            }
            long innerNanos = System.nanoTime() - innerNanosStart;
            nanos += innerNanos;
            prevInnerMillis = TimeUnit.NANOSECONDS.toMillis(innerNanos);
        }

        ansiPrintStream.close();
        System.out.println(ansi().eraseScreen());
        System.out.println("loop benchOverheadJansiPrintStream " + outerLoop + "x" + innerLoop + " took " + 
                TimeUnit.NANOSECONDS.toMillis(nanos) + "ms");
        return TimeUnit.NANOSECONDS.toMillis(nanos);
    }

    private static class NullOutputStream extends OutputStream {

        @Override
        public void write(int b) throws IOException {
            // do nothing
        }

        @Override
        public void write(byte[] b) throws IOException {
            // do nothing
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            // do nothing
        }

        @Override
        public void flush() throws IOException {
            // do nothing
        }

        @Override
        public void close() throws IOException {
            super.close();
        }
        
    }

    // Benchmark using /dev/null and counting numbers of underlying flushs
    // ------------------------------------------------------------------------

    /**
     * count calls to <code>flush()</code> when using ansi() text formatting, 
     * filtered with <code>AnsiPrintStream</code>
     */
    private static int countFlushAnsiJansiPrintStream(int outerLoop, int innerLoop) {
        FlushCallsCountNullOutputStream countFlushDevNull = new FlushCallsCountNullOutputStream();
        PrintStream ansiPrintStream = new AnsiPrintStream(new PrintStream(countFlushDevNull));

        long prevInnerMillis = 0;
        for(int i = 0; i < outerLoop; i++) {
            for(int j = 0; j < innerLoop; j++) {
                ansiPrintStream.println( ansi().fg(RED).a("Hello")
                        .fg(GREEN).a(" World")
                        .fg(Color.DEFAULT).a(" NoFlush " + i)
                        .fg(Color.BLUE).a(" " + j)
                        .fg(Color.DEFAULT).a(" loop took " + prevInnerMillis + "ms")
                        );
            }
        }

        ansiPrintStream.close();
        int resCount = countFlushDevNull.getCountFlush();
        System.out.println("count flush() ansi+JansiPrintStream " + outerLoop + "x" + innerLoop + " : " + resCount);
        return resCount;
    }

    /**
     * count calls to <code>flush()</code> when using RAW text, 
     * filtered with <code>AnsiPrintStream</code>
     */
    private static int countFlushRawJansiPrintStream(int outerLoop, int innerLoop) {
        FlushCallsCountNullOutputStream countFlushDevNull = new FlushCallsCountNullOutputStream();
        PrintStream ansiPrintStream = new AnsiPrintStream(new PrintStream(countFlushDevNull));
        
        long prevInnerMillis = 0;
        for(int i = 0; i < outerLoop; i++) {
            for(int j = 0; j < innerLoop; j++) {
                ansiPrintStream.println("Hello World NoFlush"
                        + " " + i
                        + " " + j
                        + " loop took " + prevInnerMillis + "ms"
                        + "\n");
            }
        }

        ansiPrintStream.close();
        int resCount = countFlushDevNull.getCountFlush();
        System.out.println("count flush() raw + JansiPrintStream " + outerLoop + "x" + innerLoop + " : " + resCount);
        return resCount;
    }

    private static class FlushCallsCountNullOutputStream extends NullOutputStream {
        int countFlush;
        
        @Override
        public void flush() throws IOException {
            this.countFlush++;
        }

        public int getCountFlush() {
            return countFlush;
        }

        public void resetCountFlush() {
            this.countFlush = 0;
        }

    }

}
