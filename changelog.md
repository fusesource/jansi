# ![Jansi](http://fusesource.github.io/jansi/images/project-logo.png)

## [Jansi 1.16][1_16], released 2017-05-04
[1_16]: https://repo.maven.apache.org/maven2/org/fusesource/jansi/jansi/1.16

* [`90fb161`](https://github.com/fusesource/jansi/commit/90fb1619323ae32f9ff5b982fdcbb8939d1970b2) Update changelog
* [`18368e3`](https://github.com/fusesource/jansi/commit/18368e3adfe77574e7201693df7c557f6a6ec717) Support insert / delete lines ansi sequences
* [`52fba5a`](https://github.com/fusesource/jansi/commit/52fba5a1d826893cee4c06c2d957e608a0dae70b) Fix inverted colors
* [`ae16025`](https://github.com/fusesource/jansi/commit/ae1602525024e84bedd13b709ed29611e6c04e1d) Add changelog for 1.16
* [`708591c`](https://github.com/fusesource/jansi/commit/708591cf695f4f226611719dda3f53a3226a1159) Merge pull request [#81](http://github.com/fusesource/jansi/issues/81) from hboutemy/reporting
* [`1eb47a7`](https://github.com/fusesource/jansi/commit/1eb47a7337f96e7386b50d71bb6cc44fc79c2e5e) updated reporting configuration
* [`c6830ac`](https://github.com/fusesource/jansi/commit/c6830ac1e54b1f8fd3985c5fe595a835ad56046a) Use english locale when converting to lower case
* [`3f72c94`](https://github.com/fusesource/jansi/commit/3f72c94161b851ca8306caa85f37289bfe21ea84) Update changelog.md
* [`8c2902a`](https://github.com/fusesource/jansi/commit/8c2902a13753fa3eb3468b47a18ef69d6234ee3a) Upgrade some plugins
* [`36d386d`](https://github.com/fusesource/jansi/commit/36d386de3a3518ef62318037a501c1e63877a69a) Merge pull request [#82](http://github.com/fusesource/jansi/issues/82) from hboutemy/javadoc
* [`bb3d538`](https://github.com/fusesource/jansi/commit/bb3d538315c44f799d34fd3426f6c91c8e8dfc55) ANSI output stripping does not work if TERM is xterm, fixes [#83](http://github.com/fusesource/jansi/issues/83)
* [`3c82c33`](https://github.com/fusesource/jansi/commit/3c82c332a278b6931d4cc41b4bf6040e545beb42) Provide FreeBSD native support by default, fixes [#56](http://github.com/fusesource/jansi/issues/56)
* [`e73f297`](https://github.com/fusesource/jansi/commit/e73f297d0fff573f1eb1571bdfb934dfd43b4d83) Make AnsiOutputStream#write synchronized to avoid possible problems
* [`97750d6`](https://github.com/fusesource/jansi/commit/97750d693edd77d9219f7229ba5f622a2a1d85cf) Harcode the reset code to avoid having the AnsiOutputStream depending on Ansi, fix typo
* [`228563e`](https://github.com/fusesource/jansi/commit/228563e1f5a40518758cdf525c503675ed82fca4) Avoid the charset lookup
* [`08e2c4a`](https://github.com/fusesource/jansi/commit/08e2c4a059f5060a77ebcd114b998679903f3661) Correct support for the bright colors on windows
* [`e3748a2`](https://github.com/fusesource/jansi/commit/e3748a2386856ed2ee04c1ec6be0dc300e5b9823) improved javadoc
* [`dbf2e8c`](https://github.com/fusesource/jansi/commit/dbf2e8cfa71593b2347bf0364d72357861924946) Add a few methods to be able to render code names more easily, fixes [#14](http://github.com/fusesource/jansi/issues/14)
* [`2616142`](https://github.com/fusesource/jansi/commit/2616142fda4425d779ac94a3d9bfa76412021b23) Fix encoding problem on AnsiConsole.out on windows, fixes [#79](http://github.com/fusesource/jansi/issues/79)
* [`a9ceddb`](https://github.com/fusesource/jansi/commit/a9ceddb00d1f42411d2342b33c0150f0fe281837) Merge pull request [#78](http://github.com/fusesource/jansi/issues/78) from hboutemy/scm-publish
* [`8824843`](https://github.com/fusesource/jansi/commit/8824843f27fd8f8e6b69b67c52fa9197a3fdf2c8) Merge pull request [#76](http://github.com/fusesource/jansi/issues/76) from hboutemy/site-1.15
* [`de1c836`](https://github.com/fusesource/jansi/commit/de1c836172bc9bb97db1efe9760cbeea667acedc) use scm-publish plugin to deploy site to gh-pages
* [`4702c58`](https://github.com/fusesource/jansi/commit/4702c58cfa41b544f8df85f505f4134f278f71b3) site enhancements for 1.15 release

## [Jansi 1.15][1_15], released 2017-03-17
[1_15]: https://repo.maven.apache.org/maven2/org/fusesource/jansi/jansi/1.15

* [`1e70152`](https://github.com/fusesource/jansi/commit/1e701521ac6a4dec202a3dcf608744724dbb3e06) Merge remote-tracking branch 'dblock/readme-colors'
* [`a8cda38`](https://github.com/fusesource/jansi/commit/a8cda38de363ac11d6a422a1838a5c372e1e0e21) Code cleanup
* [`91669be`](https://github.com/fusesource/jansi/commit/91669be63a7d701f0a94cdb36bf74c680f9163ad) Fix typo
* [`27d9311`](https://github.com/fusesource/jansi/commit/27d93118085830ad0a0b48182548ab3a7b617c00) Update headers
* [`4864636`](https://github.com/fusesource/jansi/commit/4864636f0077c56ff1063564f8cc0fcb8a905f5b) Make method public and don't make statement unnecessarily nested within else clause.
* [`fd67379`](https://github.com/fusesource/jansi/commit/fd67379889295b03fe0f446024d75b14f14ab800) Refactor org.fusesource.jansi.AnsiRenderer.render(String) into a new method org.fusesource.jansi.AnsiRenderer.render(String, Appendable). I want to use this from Log4j.
* [`fd83740`](https://github.com/fusesource/jansi/commit/fd83740450016361a139cc5a01f9a49a5d91a6cb) Merge pull request [#70](http://github.com/fusesource/jansi/issues/70) from lacasseio/gradle-issue-882
* [`471902d`](https://github.com/fusesource/jansi/commit/471902da8b431fb521cfdf8e5d4a26571adb93b3) Merge pull request [#72](http://github.com/fusesource/jansi/issues/72) from Joe-Merten/ExtColors
* [`87da527`](https://github.com/fusesource/jansi/commit/87da5271311a7e4123392139c366a6b8c7ed6750) Merge branch 'hboutemy-APIdoc'
* [`0acd7e5`](https://github.com/fusesource/jansi/commit/0acd7e5c10fa16946de218016bbd0241182809ef) fixed API doc generation
* [`99020cc`](https://github.com/fusesource/jansi/commit/99020cc46ccfce5f80db2e615f00101df6532851) added support for extended colors (xterm 256 and 24 bit rgb)
* [`38a24fa`](https://github.com/fusesource/jansi/commit/38a24fa29fa774719ea4a9a7a80797ec49a92aba) Avoid `processCursorDown` overflow on Y axis

## [Jansi 1.14][1_14], released 2016-10-04
[1_14]: https://repo.maven.apache.org/maven2/org/fusesource/jansi/jansi/1.14

* [`eeda18c`](https://github.com/fusesource/jansi/commit/eeda18cb05122abe48b284dca969e2c060a0c009) Merge pull request [#59](http://github.com/fusesource/jansi/issues/59) from sschuberth/master
* [`b84df55`](https://github.com/fusesource/jansi/commit/b84df55b6c6a0688cf8ad790d4a3ac952ea6b9d0) Merge pull request [#65](http://github.com/fusesource/jansi/issues/65) from jbonofre/NATIVE_FIX
* [`074c23b`](https://github.com/fusesource/jansi/commit/074c23b482048f837adbbd2a6cd1162178a2fa19) Deal with UnsatisfiedLinkError: when native lib can't be loaded, jansi can deal with this case.
* [`5a3a670`](https://github.com/fusesource/jansi/commit/5a3a670c911e4491195b6d162d456dead1497824) Make isXterm() also detect xterm-color and friends
* [`fad337e`](https://github.com/fusesource/jansi/commit/fad337edcf1a58e2f9a0c0f329e74f9b608419c2) Improve fix for issue [#55](http://github.com/fusesource/jansi/issues/55).  If we can't load that natives for any reason, fallback to better defaults.

## [Jansi 1.13][1_13], released 2016-06-15
[1_13]: https://repo.maven.apache.org/maven2/org/fusesource/jansi/jansi/1.13

* [`a7ec77c`](https://github.com/fusesource/jansi/commit/a7ec77c0ad5cb05f8becdb035fd7fbac9992ae3e) Updating website bits.
* [`55c3817`](https://github.com/fusesource/jansi/commit/55c381767a2bd6f744f796c7b435afbf02c74a99) Merge pull request [#50](http://github.com/fusesource/jansi/issues/50) from sschuberth/master
* [`08fc17a`](https://github.com/fusesource/jansi/commit/08fc17afea46a348631facb26ef64ddfb0dd92b8) Merge pull request [#52](http://github.com/fusesource/jansi/issues/52) from hboutemy/master
* [`e180ab1`](https://github.com/fusesource/jansi/commit/e180ab10d58fbe5cd0624cb6fca443449df1a982) Add the README's example as a separate project
* [`25ed28f`](https://github.com/fusesource/jansi/commit/25ed28fb5f4f03a480017da242e7dacf8afb81fc) Rename isCygwin() to isXterm()
* [`a54d2b5`](https://github.com/fusesource/jansi/commit/a54d2b5b3bc91561e158837beb84eba85e75c8b1) Merge pull request [#54](http://github.com/fusesource/jansi/issues/54) from ChristianSchulte/master
* [`823ee46`](https://github.com/fusesource/jansi/commit/823ee46d7034784d15d130a15a7ae45874d261fb) java.lang.UnsatisfiedLinkError: Could not load library. Reasons: [no jansi64-1.12 in java.library.path, no jansi-1.12 in java.library.path, no jansi in java.library.path]
* [`affe709`](https://github.com/fusesource/jansi/commit/affe709be8ce2c6fad21f85618bc30a05e182e03) make ansi(int) and ansi(StringBuilder) static methods consistent with ansi(), ie support NoAnsi
* [`03e7a2a`](https://github.com/fusesource/jansi/commit/03e7a2a8703dd0ddf012643e533b493fdf0c5275) Merge pull request [#51](http://github.com/fusesource/jansi/issues/51) from jdillon/normalize-formatting
* [`519410f`](https://github.com/fusesource/jansi/commit/519410f1cb1d2ca8f7920f62ab2ef4926a1e31d1) normalize formatting using default idea settings
* [`adeb16e`](https://github.com/fusesource/jansi/commit/adeb16e28a1fcea2360f5485542f21f62664809d) Update readme.md
* [`59546ea`](https://github.com/fusesource/jansi/commit/59546ea434edb437a0ab904605f1817f6a895e03) Update readme.md
* [`b9f61a2`](https://github.com/fusesource/jansi/commit/b9f61a235471253bdefabe7f8167d5271f247f39) Update readme.md

## [Jansi 1.12][1_12], released 2016-04-27
[1_12]: https://repo.maven.apache.org/maven2/org/fusesource/jansi/jansi/1.12

* [`5d4eb66`](https://github.com/fusesource/jansi/commit/5d4eb665e428a63851ab2b51bad7c3487803e841) Fixing dep id.
* [`3949775`](https://github.com/fusesource/jansi/commit/3949775bb2df312d720fa22439efbea2b14e6ef4) Update parent pom.
* [`ec777e7`](https://github.com/fusesource/jansi/commit/ec777e737a508e7cd1aa477a9d2681c6d0638bad) Switch to released jansi-native artifacts.
* [`5dcfc1a`](https://github.com/fusesource/jansi/commit/5dcfc1ad5bec9f21c02c0839cdd325d8560b2d59) Update to use new style of native artifacts.
* [`a7a0120`](https://github.com/fusesource/jansi/commit/a7a0120a299b8c052b392ebd6c8ef173a36df0f6) Switching to sonatype mvn repo.
* [`daff2c9`](https://github.com/fusesource/jansi/commit/daff2c9384bf939273dc9969805b07f52461ea5b) Build against jansi-native 1.6-SNAPSHOT
* [`5aa64b1`](https://github.com/fusesource/jansi/commit/5aa64b112c1544363deae81688b97cf56cc37c19) Merge pull request [#46](http://github.com/fusesource/jansi/issues/46) from DevFactory/release/multiple-code-improvements-fix-1
* [`23afd0e`](https://github.com/fusesource/jansi/commit/23afd0e094a6c4de0fb26f4b370ee6add944faee) Merge pull request [#15](http://github.com/fusesource/jansi/issues/15) from garydgregory/better-Ansi
* [`236d35f`](https://github.com/fusesource/jansi/commit/236d35fadb9cf3ff75f90236f2e6aec45c69ba55) Merge pull request [#45](http://github.com/fusesource/jansi/issues/45) from sschuberth/master
* [`1f0e856`](https://github.com/fusesource/jansi/commit/1f0e856398b4ee4b304bd61e17399205412c7c66) Multiple code improvements - squid:SwitchLastCaseIsDefaultCheck, squid:S1197, squid:S1118
* [`65d955b`](https://github.com/fusesource/jansi/commit/65d955bf0aa8ae480ee5d8eefdd8057ff7471eca) Detect Cygwin, including the MSYS(2) forks
* [`704633f`](https://github.com/fusesource/jansi/commit/704633fd36399437ebea59dded84324a9f09616a) Fix compatibility with jansi 1.11
* [`bc4e70a`](https://github.com/fusesource/jansi/commit/bc4e70af8739231cd60502bb72377302925564e0) Upgrade to jansi-native 1.6-SNAPSHOT
* [`4a018a5`](https://github.com/fusesource/jansi/commit/4a018a520f9baf4b09f0c3577aa871c9c74a17b8) Export the internal package so that Kernel32 and CLibrary can be used in OSGi
* [`620c446`](https://github.com/fusesource/jansi/commit/620c44697fea322181f402cd325998b871afee00) Fill console attributes when erasing the screen on windows to not leave unwanted backgrounds on the screen
* [`bf3b544`](https://github.com/fusesource/jansi/commit/bf3b544a172a57e62412dff9043faa428ee31b2f) Make sure bright colors are not completely ignored on windows
* [`c69c78b`](https://github.com/fusesource/jansi/commit/c69c78b2f3855255b0432162d2e94f9d84951b0b) Fix some javadocs warnings
* [`2400c7a`](https://github.com/fusesource/jansi/commit/2400c7acd9941a8974258804b63876512f6b4bf7) Merge pull request [#35](https://github.com/fusesource/jansi/issues/35) from udalov/patch-1
* [`0d398a5`](https://github.com/fusesource/jansi/commit/0d398a57e70f2654631dbcd7adc34bc85428b172) Check isatty() separately for stdout and stderr
* [`c3b76bc`](https://github.com/fusesource/jansi/commit/c3b76bcad382f6a76cfb1534e6b068ee53774554) Merge pull request [#26](http://github.com/fusesource/jansi/issues/26) from ghquant/fix-Ansi-DefaultColors-Windows
* [`5f202de`](https://github.com/fusesource/jansi/commit/5f202def7a3cfb2584b442bcdce0ed2d6600b76e) Merge pull request [#20](http://github.com/fusesource/jansi/issues/20) from tksk/master
* [`fc87486`](https://github.com/fusesource/jansi/commit/fc87486cea26655e7c6eda77b1b582e2bf416f33) [#10](http://github.com/fusesource/jansi/issues/10): Fixed broken links in readme file.
* [`239255c`](https://github.com/fusesource/jansi/commit/239255ca6221da5bf453065cc8ddbd45b2e4a406) [#17](http://github.com/fusesource/jansi/issues/17): Fixed typos, and @deprecated methods with typo.
* [`489c4d3`](https://github.com/fusesource/jansi/commit/489c4d3a74f93dbd17bb8927feac4ba82dfbb23c) Merge pull request [#21](http://github.com/fusesource/jansi/issues/21) from jdbernard/master
* [`20a1ebb`](https://github.com/fusesource/jansi/commit/20a1ebb2a15003cc290d461f6da30e9787b1f6f1) Merge pull request [#13](http://github.com/fusesource/jansi/issues/13) from garydgregory/turkish-fix
* [`682f9c3`](https://github.com/fusesource/jansi/commit/682f9c3eb4df5e95ac6383d20bddcd788cccaacd) implemented missing functions to set the default text and background colors on Windows
* [`0d05930`](https://github.com/fusesource/jansi/commit/0d059308450dbe9a122b61b7a9424b12ed25cf79) Added additional escape sequences defined in ECMA-48.
* [`2ef5976`](https://github.com/fusesource/jansi/commit/2ef5976879123accb0f9adc22c0014eb0310e298) support jansi.force property to force ansi escapes
* [`9bab505`](https://github.com/fusesource/jansi/commit/9bab505bfbd57c8511ef3641c6d0fe41c92302dd) Merge pull request [#19](http://github.com/fusesource/jansi/issues/19) from xuwei-k/patch-1

## [Jansi 1.11][1_11], released 2013-05-13
[1_11]: https://repo.maven.apache.org/maven2/org/fusesource/jansi/jansi/1.11

* Upgraded to the latest hawtjni version.

## [Jansi 1.10][1_10], released 2013-03-25
[1_10]: https://repo.maven.apache.org/maven2/org/fusesource/jansi/jansi/1.10

* Upgraded to the latest jansi native release (1.5).

## [Jansi 1.9][1_9], released 2012-06-04
[1_9]: https://repo.maven.apache.org/maven2/org/fusesource/jansi/jansi/1.9

* Added HtmlAnsiOutputStream that converts ANSI output to HTML.
* Fixed handling of default text and background color.

## [Jansi 1.8][1_8], released 2012-02-15
[1_8]: https://repo.maven.apache.org/maven2/org/fusesource/jansi/jansi/1.8

* Updated bundled native libraries:
  * Windows: Added support for isatty and link against the system msvcrt.dll (so no need for VC redistributables).
* Add some helper methods to turn bold on and off.
* If the jansi.passthrough system property is set, then Jansi will not interpret any of the ANSI sequences.

## [Jansi 1.7][1_7], released 2011-09-21
[1_7]: https://repo.maven.apache.org/maven2/org/fusesource/jansi/jansi/1.7

* Updated bundled native libraries:
  * Windows: Adding support for PeekConsoleInputW, FlushConsoleInputBuffer so that CTRL-C can be handled by jline. Discarding mouse events on readConsoleInput.
  * Linux: Built against glib 2.0 to be compatible with more versions of Linux.
  
## [Jansi 1.6][1_6], released 2011-06-19
[1_6]: https://repo.maven.apache.org/maven2/org/fusesource/jansi/jansi/1.6

* Upgrade to HawtJNI 1.2 to pick up a fix to support 32 and 64 bit JVMs on a single machine.
* Add copy constructor for Ansi class.
* Port website doco to use Scalate instead of webgen.

## [Jansi 1.5][1_5], released 2010-11-04
[1_5]: https://repo.maven.apache.org/maven2/org/fusesource/jansi/jansi/1.5

* Support for parsing Operating System Command (OSC) control sequences.
* Windows: added support for setting the console title through an OSC command, like on xterm.
* Added option to strip ANSI escapes if the 'jansi.strip' system property is set to true.

## [Jansi 1.4][1_4], released 2010-07-15
[1_4]: https://repo.maven.apache.org/maven2/org/fusesource/jansi/jansi/1.4

* JNI libs are now bundled in the Jansi jar.
* Windows: added support for save and restore of cursor position, fixed bug in processCursorTo.

## [Jansi 1.3][1_3], released 2010-03-08
[1_3]: https://repo.maven.apache.org/maven2/org/fusesource/jansi/jansi/1.3

* Switched to a HawtJNI generated native library instead of using JNA to access native functions.

## [Jansi 1.2.1][1_2_1], released 2010-03-08
[1_2_1]: https://repo.maven.apache.org/maven2/org/fusesource/jansi/jansi/1.2.1

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
