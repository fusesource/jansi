= Jansi

* http://jansi.fusesource.org/

== Description

Jansi is a small java library that allows you to use ANSI escape 
sequences to format your console output which works even on windows. 

== Features

* Implements ANSI escape colorization/handling that is missing on the
  Windows platform.
* Strips ANSI escape sequences if process output is is being
  redirected and not attached to a terminal.
* Easy to use Ansi escape sequence builder API.

== Synopsis

Most unix terminals support rendering ANSI escape sequences when Java
sends them via System.out, but when this is done on Windows, they don't
get interpreted and you get garbage on the console. Furthermore, even
when on Unix, when process output is being redirected to a file, you
typically don't want to output escape sequences to the file since most
file viewers and editors will not properly display the escape
sequences.

Jansi detects and abstracts the ANSI support provided by the attached
terminal. When your Java application uses Jansi, it can always assume
that standard out and error streams support ANSI sequences. Depending on
the platform and if the application output is attached to a real
terminal, Jansi will do one of the following with the ANSI escape
sequences that it receives:

* Pass them through untouched
* Filter them out
* Use platform specific APIs to implement the terminal commands
  represented by the escape sequence

== Example Usage

Enabling the Jansi ANSI support into your application is as simple as doing a simple static method call:

  import org.fusesource.jansi;
  ...
  AnsiConsole.systemInstall();

Disabling it is also done via a static method:

  AnsiConsole.systemUninstall();

It is safe to call those methods multiple times, they keep track of how
many times systemInstall has been called and only uninstalls when the
systemUninstall method is called a corresponding number of times.

