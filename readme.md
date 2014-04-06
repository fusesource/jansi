# [![Jansi][logo]][Jansi]
[logo]: http://jansi.fusesource.org/images/project-logo.png "Jansi"

## Description

[Jansi][Jansi] is a small java library that allows you to use [ANSI escape
codes][ansi] to format your console output which works even on windows.

[Jansi]: http://jansi.fusesource.org/
[ansi]: http://en.wikipedia.org/wiki/ANSI_escape_code "Wikipedia"

## Features

* Implements ANSI escape colorization/handling that is missing on the Windows
  platform.
* Strips ANSI escape codes if process output is is being redirected and not
  attached to a terminal.
* Easy to use Ansi escape sequence builder API.

## Synopsis

Most unix terminals support rendering ANSI escape codes when Java sends them
via `System.out`, but when this is done on Windows, they don't get interpreted
and you get garbage on the console.

Furthermore, even when on Unix, when process output is being redirected to a
file, you typically don't want to output escape codes to the file since most
file viewers and editors will not properly display the escape codes.

Jansi detects and abstracts the ANSI support provided by the attached
terminal. When your Java application uses Jansi, it can always assume that
standard out and error streams support ANSI sequences. Depending on the
platform and if the application output is attached to a real terminal, Jansi
will do one of the following with the ANSI escape codes that it receives:

* Pass them through untouched
* Filter them out
* Use platform specific APIs to implement the terminal commands represented by
  the escape sequence

## Example Usage

Enabling the Jansi ANSI support into your application is as simple as doing a
simple static method call:

    import org.fusesource.jansi.AnsiConsole;
    ...
    AnsiConsole.systemInstall();

Disabling it is also done via a static method:

    AnsiConsole.systemUninstall();

It is safe to call those methods multiple times, they keep track of how many
times `systemInstall()` has been called and only uninstalls when the
`systemUninstall()` method is called a corresponding number of times.

Using the Ansi escape sequence builder:

```java
    import static org.fusesource.jansi.Ansi.*;
    import static org.fusesource.jansi.Ansi.Color.*;
    ...
    System.out.println( ansi().eraseScreen().fg(RED).a("Hello").fg(GREEN).a(" World").reset() );
```

The above will clear the screen, write `Hello` in red and `World` in green,
then reset the color attributes so that subsequent data printed to the stream
uses the default colors.

But there is an even simpler way to accomplish the above using the `render`
method:

```java
    System.out.println( ansi().eraseScreen().render("@|red Hello|@ @|green World|@") );
```

## Windows XP Users

The JNI library that JANSI uses depends on the "Microsoft Visual C++ 2008 SP1 Redistributable"
You can get a free copy from MS at:

http://www.microsoft.com/en-us/download/details.aspx?displaylang=en&id=5582

## Project Links

* [Project Home](http://jansi.fusesource.org/)
* [Release Downloads](http://jansi.fusesource.org/download/)
* [GitHub](https://github.com/fusesource/jansi)
* [Issue Tracker](https://github.com/fusesource/jansi/issues)

