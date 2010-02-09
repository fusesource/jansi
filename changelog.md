![Jansi][logo]
===========

[Jansi 1.2][1_2], released 2010-02-09
----------------------------------
* Improved Java Docs
* Better windows ansi handling of: erase screen and line and move to col
* new method: Ansi.newline()
* Fixed missing return statement in cursor up case.. 
* Reset the attributes when the ansi output stream is closed on unix.

[Jansi 1.1][1_1], released 2009-11-23
----------------------------------
* AnsiRender can now be used in a static way and made easier to use with the Ansi builder.
* Merged [Jason Dillon's Fork](http://github.com/jdillon/jansi/tree/bb86e0e79bec850167ddfd8c4a86fb9ffef704e5): 
	* Pluggable ASNI support detection
	* ANSI builder can be configured to not generate ANSI escapes.
	* AnsiRender provides an easier way to generate escape sequences.
* [JANSI-5](http://fusesource.com/issues/browse/JANSI-5): Attribute Reset escape should respect original console colors
* [JANSI-4](http://fusesource.com/issues/browse/JANSI-4): Restore command console after closing wrapped OutputStream on Windows.
* [JANSI-1](http://fusesource.com/issues/browse/JANSI-1): Added extensions for colors and other attributes to Ansi builder. 

[Jansi 1.0][1_0], released 2009-08-25
----------------------------------

* Initial Release

[1_2]: http://jansi.fusesource.org/repo/release/org/fusesource/jansi/jansi/1.2
[1_1]: http://jansi.fusesource.org/repo/release/org/fusesource/jansi/jansi/1.1
[1_0]: http://jansi.fusesource.org/repo/release/org/fusesource/jansi/jansi/1.0
[logo]: http://jansi.fusesource.org/images/project-logo.png "Jansi"
