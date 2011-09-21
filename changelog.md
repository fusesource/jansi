# ![Jansi](http://jansi.fusesource.org/images/project-logo.png)

## [Jansi 1.7][1_7], released 2011-09-21
[1_7]: http://repo.fusesource.com/nexus/content/groups/public/org/fusesource/jansi/jansi/1.7

* Updated bundled native libraries:
  * Windows: Adding support for PeekConsoleInputW, FlushConsoleInputBuffer so that CTRL-C can be handled by jline. Discarding mouse events on readConsoleInput.
  * Linux: Built against glib 2.0 to be compatible with more versions of Linux.
  
## [Jansi 1.6][1_6], released 2011-06-19
[1_6]: http://repo.fusesource.com/nexus/content/groups/public/org/fusesource/jansi/jansi/1.6

* Upgrade to HawtJNI 1.2 to pick up a fix to support 32 and 64 bit JVMs on a single machine
* Add copy constructor for Ansi class.
* Port website doco to use Scalate instead of webgen

## [Jansi 1.5][1_5], released 2010-11-04
[1_5]: http://repo.fusesource.com/nexus/content/groups/public/org/fusesource/jansi/jansi/1.5

* Support for parsing Operating System Command (OSC) control sequences
* Windows: added support for setting the console title through an OSC command, like on xterm
* add option to strip ansi escapes if the 'jansi.strip' system property is set to true.

## [Jansi 1.4][1_4], released 2010-07-15
[1_4]: http://repo.fusesource.com/nexus/content/groups/public/org/fusesource/jansi/jansi/1.4

* JNI libs are now bundled in the jansi jar
* Windows: added support for save and restore of cursor position, fixed bug in processCursorTo

## [Jansi 1.3][1_3], released 2010-03-08
[1_3]: http://repo.fusesource.com/nexus/content/groups/public/org/fusesource/jansi/jansi/1.3

* Switched to a HawtJNI generated native library instead of using JNA to access native functions

## [Jansi 1.2.1][1_2_1], released 2010-03-08
[1_2_1]: http://repo.fusesource.com/nexus/content/groups/public/org/fusesource/jansi/jansi/1.2.1

* Released to Maven Central

## [Jansi 1.2][1_2], released 2010-02-09
[1_2]: http://jansi.fusesource.org/repo/release/org/fusesource/jansi/jansi/1.2

* Improved Java Docs
* Better windows ansi handling of: erase screen and line and move to col
* new method: Ansi.newline()
* Fixed missing return statement in cursor up case.. 
* Reset the attributes when the ansi output stream is closed on unix.

## [Jansi 1.1][1_1], released 2009-11-23
[1_1]: http://jansi.fusesource.org/repo/release/org/fusesource/jansi/jansi/1.1

* AnsiRender can now be used in a static way and made easier to use with the Ansi builder.
* Merged [Jason Dillon's Fork](http://github.com/jdillon/jansi/tree/bb86e0e79bec850167ddfd8c4a86fb9ffef704e5): 
	* Pluggable ANSI support detection
	* ANSI builder can be configured to not generate ANSI escapes.
	* AnsiRender provides an easier way to generate escape sequences.
* [JANSI-5](http://fusesource.com/issues/browse/JANSI-5): Attribute Reset escape should respect original console colors
* [JANSI-4](http://fusesource.com/issues/browse/JANSI-4): Restore command console after closing wrapped OutputStream on Windows.
* [JANSI-1](http://fusesource.com/issues/browse/JANSI-1): Added extensions for colors and other attributes to Ansi builder. 

## [Jansi 1.0][1_0], released 2009-08-25
[1_0]: http://jansi.fusesource.org/repo/release/org/fusesource/jansi/jansi/1.0

* Initial Release

