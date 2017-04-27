# ![Jansi](http://fusesource.github.io/jansi/images/project-logo.png)

## [Jansi 1.15][1_15], released 2017-03-17
[1_15]: http://repo.fusesource.com/nexus/content/groups/public/org/fusesource/jansi/jansi/1.15

## [Jansi 1.14][1_14], released 2016-10-04
[1_14]: http://repo.fusesource.com/nexus/content/groups/public/org/fusesource/jansi/jansi/1.14

## [Jansi 1.13][1_13], released 2016-06-15
[1_13]: http://repo.fusesource.com/nexus/content/groups/public/org/fusesource/jansi/jansi/1.13

## [Jansi 1.12][1_12], released 2016-04-27
[1_12]: http://repo.fusesource.com/nexus/content/groups/public/org/fusesource/jansi/jansi/1.12

## [Jansi 1.11][1_11], released 2013-05-13
[1_11]: http://repo.fusesource.com/nexus/content/groups/public/org/fusesource/jansi/jansi/1.11

* Upgraded to the latest hawtjni version.

## [Jansi 1.10][1_10], released 2013-03-25
[1_10]: http://repo.fusesource.com/nexus/content/groups/public/org/fusesource/jansi/jansi/1.10

* Upgraded to the latest jansi native release (1.5).

## [Jansi 1.9][1_9], released 2012-06-04
[1_9]: http://repo.fusesource.com/nexus/content/groups/public/org/fusesource/jansi/jansi/1.9

* Added HtmlAnsiOutputStream that converts ANSI output to HTML.
* Fixed handling of default text and background color.

## [Jansi 1.8][1_8], released 2012-02-15
[1_8]: http://repo.fusesource.com/nexus/content/groups/public/org/fusesource/jansi/jansi/1.8

* Updated bundled native libraries:
  * Windows: Added support for isatty and link against the system msvcrt.dll (so no need for VC redistributables).
* Add some helper methods to turn bold on and off.
* If the jansi.passthrough system property is set, then Jansi will not interpret any of the ANSI sequences.

## [Jansi 1.7][1_7], released 2011-09-21
[1_7]: http://repo.fusesource.com/nexus/content/groups/public/org/fusesource/jansi/jansi/1.7

* Updated bundled native libraries:
  * Windows: Adding support for PeekConsoleInputW, FlushConsoleInputBuffer so that CTRL-C can be handled by jline. Discarding mouse events on readConsoleInput.
  * Linux: Built against glib 2.0 to be compatible with more versions of Linux.
  
## [Jansi 1.6][1_6], released 2011-06-19
[1_6]: http://repo.fusesource.com/nexus/content/groups/public/org/fusesource/jansi/jansi/1.6

* Upgrade to HawtJNI 1.2 to pick up a fix to support 32 and 64 bit JVMs on a single machine.
* Add copy constructor for Ansi class.
* Port website doco to use Scalate instead of webgen.

## [Jansi 1.5][1_5], released 2010-11-04
[1_5]: http://repo.fusesource.com/nexus/content/groups/public/org/fusesource/jansi/jansi/1.5

* Support for parsing Operating System Command (OSC) control sequences.
* Windows: added support for setting the console title through an OSC command, like on xterm.
* Added option to strip ANSI escapes if the 'jansi.strip' system property is set to true.

## [Jansi 1.4][1_4], released 2010-07-15
[1_4]: http://repo.fusesource.com/nexus/content/groups/public/org/fusesource/jansi/jansi/1.4

* JNI libs are now bundled in the Jansi jar.
* Windows: added support for save and restore of cursor position, fixed bug in processCursorTo.

## [Jansi 1.3][1_3], released 2010-03-08
[1_3]: http://repo.fusesource.com/nexus/content/groups/public/org/fusesource/jansi/jansi/1.3

* Switched to a HawtJNI generated native library instead of using JNA to access native functions.

## [Jansi 1.2.1][1_2_1], released 2010-03-08
[1_2_1]: http://repo.fusesource.com/nexus/content/groups/public/org/fusesource/jansi/jansi/1.2.1

* Released to Maven Central.

## [Jansi 1.2][1_2], released 2010-02-09
[1_2]: http://jansi.fusesource.org/repo/release/org/fusesource/jansi/jansi/1.2

* Improved Java Docs.
* Better windows ANSI handling of: erase screen and line and move to col.
* New method: Ansi.newline().
* Fixed missing return statement in cursor up case. 
* Reset the attributes when the ANSI output stream is closed on unix.

## [Jansi 1.1][1_1], released 2009-11-23
[1_1]: http://jansi.fusesource.org/repo/release/org/fusesource/jansi/jansi/1.1

* AnsiRender can now be used in a static way and made easier to use with the ANSI builder.
* Merged [Jason Dillon's Fork](http://github.com/jdillon/jansi/tree/bb86e0e79bec850167ddfd8c4a86fb9ffef704e5): 
	* Pluggable ANSI support detection.
	* ANSI builder can be configured to not generate ANSI escapes.
	* AnsiRender provides an easier way to generate escape sequences.
* [JANSI-5](http://fusesource.com/issues/browse/JANSI-5): Attribute Reset escape should respect original console colors.
* [JANSI-4](http://fusesource.com/issues/browse/JANSI-4): Restore command console after closing wrapped OutputStream on Windows.
* [JANSI-1](http://fusesource.com/issues/browse/JANSI-1): Added extensions for colors and other attributes to Ansi builder. 

## [Jansi 1.0][1_0], released 2009-08-25
[1_0]: http://jansi.fusesource.org/repo/release/org/fusesource/jansi/jansi/1.0

* Initial Release.
