package org.fusesource.jansi;

import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

public class Main {
    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        System.out.println( ansi().eraseScreen().fg(RED).a("Hello").fg(GREEN).a(" World").reset() );
        AnsiConsole.systemUninstall();
    }
}
