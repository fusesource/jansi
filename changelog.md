# ![Jansi](http://fusesource.github.io/jansi/images/project-logo.png)
<!-- git log --pretty=format:'* [`%h`](https://github.com/fusesource/jansi/commit/%H) %s' -->

## [Jansi 2.3.4][2_3_4], released 2021-07-23
[2_3_4]: https://repo.maven.apache.org/maven2/org/fusesource/jansi/jansi/2.3.4

* [`09722b7`](https://github.com/fusesource/jansi/commit/09722b7cccc8a99f14ac1656db3072dbeef34478) Disable colors when running inside emacs without comint, fixes #205
* [`e019a75`](https://github.com/fusesource/jansi/commit/e019a75ee267a53a48d02ac983266cdf2b4e16a9) Add missing colors to AnsiRender, fixes #213
* [`01d68f0`](https://github.com/fusesource/jansi/commit/01d68f03c6b69c323a05738efec1fd4404c0f0f8) Fix Ansi outputting escape sequences when disabled, fixes #215
* [`3ba11e9`](https://github.com/fusesource/jansi/commit/3ba11e9d324f81b359229715ceacadcb9137ff46) Avoid possible NPE, fixes #214

## [Jansi 2.3.3][2_3_3], released 2021-06-11
[2_3_3]: https://repo.maven.apache.org/maven2/org/fusesource/jansi/jansi/2.3.3

* [`e4d58d9`](https://github.com/fusesource/jansi/commit/e4d58d94eab5ecbc2f466978a5006835cf728da7) Jansi fails to start on Mac arm, fixes #207

## [Jansi 2.3.2][2_3_2], released 2021-03-16
[2_3_2]: https://repo.maven.apache.org/maven2/org/fusesource/jansi/jansi/2.3.2

* [`629fdaf`](https://github.com/fusesource/jansi/commit/629fdafae88a1b468b9d43f6d9cd30cebc881ff5) Fix sigsegv in chdir / setenv
* [`ffc9fbb`](https://github.com/fusesource/jansi/commit/ffc9fbbb9ac18cf8e299c1ab8fefe3ccee6fea4b) Add a simple test for CLibrary.setenv/chdir
* [`35bd6b5`](https://github.com/fusesource/jansi/commit/35bd6b5c6e23a5f47c7afe7fbb16fd0dc760e7ad) Add CI build
* [`65e93ed`](https://github.com/fusesource/jansi/commit/65e93ed8d8ee6d93252d5b6f8f3702a3c0cbb299) Use correct scm urls, fixes #197
* [`86bd1c3`](https://github.com/fusesource/jansi/commit/86bd1c3552f6b446feabf25ed195cd26e574cdf4) fix Maven Central badge

## [Jansi 2.3.1][2_3_1], released 2021-02-11
[2_3_1]: https://repo.maven.apache.org/maven2/org/fusesource/jansi/jansi/2.3.1

* [`753a7f1`](https://github.com/fusesource/jansi/commit/753a7f18ba80f1c99fd65474813a350553b25ed8) Fix completely broken windows code for chdir/setenv

## [Jansi 2.3.0][2_3_0], released 2021-02-11
[2_3_0]: https://repo.maven.apache.org/maven2/org/fusesource/jansi/jansi/2.3.0

* [`6d61a76`](https://github.com/fusesource/jansi/commit/6d61a763ebc4a97399e57c38e7c2e0eac7b8cf3c) Add setenv and chdir methods to the CLibrary, fixes #196
* [`abca999`](https://github.com/fusesource/jansi/commit/abca99945574f468768e2648a459eba0ad42f122) Improve build reproductibility, fixes #192

## [Jansi 2.2.0][2_2_0], released 2021-01-20
[2_2_0]: https://repo.maven.apache.org/maven2/org/fusesource/jansi/jansi/2.2.0

* [`d8934d8`](https://github.com/fusesource/jansi/commit/d8934d82bd64a3899f2c6ca2eb838dc3f79e0646) Output of Jansi is corrupted in cygwin console on Windows 7, fixes #190
* [`6ace12e`](https://github.com/fusesource/jansi/commit/6ace12ec4b355425565552f0ae5d393469be3e50) Remove unused import
* [`0b1e2ec`](https://github.com/fusesource/jansi/commit/0b1e2ecf1e0fff0fbb5ff4e146ff720bacc97ac2) Add a getTerminalWidth method, fixes #175
* [`8a27841`](https://github.com/fusesource/jansi/commit/8a278418a3fb92f9e220e8a0a55100b72414b8e1) Add @since tags on new methods
* [`faf9331`](https://github.com/fusesource/jansi/commit/faf93318be3cda0b3a3894ae9d2032835601bc73) Bring back the AnsiRenderer, fixes #184
* [`0a06ceb`](https://github.com/fusesource/jansi/commit/0a06ceb8710f97e42e15999d7b90d6634533e745) Force removal of unneeded OSGi headers
* [`94439a9`](https://github.com/fusesource/jansi/commit/94439a9467d790743ff3a0bf7dbe186fb043c97b) Create javadoc for the native methods
* [`6097e80`](https://github.com/fusesource/jansi/commit/6097e8076332295bf832d784e580100c02f6e703) Fix javadoc syntax
* [`254ddf2`](https://github.com/fusesource/jansi/commit/254ddf229c6c48b5ce38ae3cf4325d111aaaf1eb) Fix OSGi exports
* [`4a530b1`](https://github.com/fusesource/jansi/commit/4a530b1b550e81b0d478e125636d2d21e02be747) Merge pull request #188 from romge/apply-method
* [`30cd5a9`](https://github.com/fusesource/jansi/commit/30cd5a9a3126c251333a93675bdf586719aa78f7) Update readme.md
* [`399473a`](https://github.com/fusesource/jansi/commit/399473a583018927ef4a4dcaa2aa14502bb96885) Handle SGRs with multiple options
* [`f8fa335`](https://github.com/fusesource/jansi/commit/f8fa335b932058a2dc1dc685722abec953097d6e) Mapping negative absolute positions to the first column/row
* [`8e1b67b`](https://github.com/fusesource/jansi/commit/8e1b67b3ed7fe0d4cc3765c099f29fe8d9e1995e) Generalize cursor movement
* [`2f93859`](https://github.com/fusesource/jansi/commit/2f93859ec3046920aedad7dfbb5f8935187e68b2) Add Ansi.apply method
* [`d386c6a`](https://github.com/fusesource/jansi/commit/d386c6afa62e7c5dc883e7158577caf175eb0d3a) configure _reproducible for maven-bundle-plugin #185
* [`b8f80e4`](https://github.com/fusesource/jansi/commit/b8f80e47f8a7ad5570ac5007e7b0438e78e7fd81) update scm tag

## [Jansi 2.1.1][2_1_1], released 2020-12-15
[2_1_1]: https://repo.maven.apache.org/maven2/org/fusesource/jansi/jansi/2.1.1

* [`9a84c14`](https://github.com/fusesource/jansi/commit/9a84c14dc6614976e387dadd997d8889d45cdca6) Report the error in the exception, fixes #183
* [`b9348fc`](https://github.com/fusesource/jansi/commit/b9348fc702a6800f47c072f80abc757a0496495f) Restore binary compatibility with previous versions
* [`f5a9b92`](https://github.com/fusesource/jansi/commit/f5a9b92cfaa98f3623946e5ffaf5f73e8b71a777) add faint to test table
* [`39616d3`](https://github.com/fusesource/jansi/commit/39616d3acc2a313de68c95d1df6bd1a1f59f8713) make build reproducible

## [Jansi 2.1.0][2_1_0], released 2020-12-07
[2_1_0]: https://repo.maven.apache.org/maven2/org/fusesource/jansi/jansi/2.1.0

* [`235b653`](https://github.com/fusesource/jansi/commit/235b653bbb57101dca7db3ff2f880115d781bc38) 256 colors and truecolor support
* [`7dc9c1b`](https://github.com/fusesource/jansi/commit/7dc9c1b553542a6d7bca4faaf0966fd1547a4327) Rename AnsiProcessorType to AnsiType
* [`5f87f09`](https://github.com/fusesource/jansi/commit/5f87f097602f3800ac3afd916bc7d63db483af72) Remove jdk 1.7 specific method, as we're still on jdk 1.6
* [`322c4d9`](https://github.com/fusesource/jansi/commit/322c4d9790e5410988ab3a165d4c9c998e2b7daf) Move IO implementation classes to a separate package
* [`04d771c`](https://github.com/fusesource/jansi/commit/04d771c3c7f2a4c29301191e65e7ce3cdeb92918) Move IO implementation classes to a separate package
* [`2babe3d`](https://github.com/fusesource/jansi/commit/2babe3dfa5506f5348d77e9eb3f0d89d7ae0eea0) Pass the console handle directly to the WindowsAnsiProcessor
* [`cc7c8d9`](https://github.com/fusesource/jansi/commit/cc7c8d988c766487f04b067834e7bd49db9306f1) Change test into an assertion as mapLibraryName should never return null
* [`63bd892`](https://github.com/fusesource/jansi/commit/63bd892b2bdfc253ec119a57bdd42df5e80fd859) Improve dynamism so that the ansi mode can be set per stream and after initialization, #178
* [`8c681e9`](https://github.com/fusesource/jansi/commit/8c681e900758716824152242fd881a20645b6bd3) Add a (manuel) test
* [`9477b53`](https://github.com/fusesource/jansi/commit/9477b530f9c91fdf374ae28d6f704d24163ad4f3) Expose a isInstalled() method
* [`8be96cc`](https://github.com/fusesource/jansi/commit/8be96cc05d46da91a8bb0bd84b03c5b0baa3c8ed) Fix typo in AnsiProcessor name
* [`f43e32b`](https://github.com/fusesource/jansi/commit/f43e32b8dd924d48b924fee2dd6edc615baf46e9) Remove references to old fusesource web site
* [`a00711c`](https://github.com/fusesource/jansi/commit/a00711c34177d1f5a82c3f4e01a0fa09ce9f5bdc) Avoid possible flushing problems when displaying the logo
* [`9f43faa`](https://github.com/fusesource/jansi/commit/9f43faa66139ccc19e53136895280d8b13be3616) Avoid hardcoded numbers
* [`23e71d0`](https://github.com/fusesource/jansi/commit/23e71d0abaefc4d7c8a5df1230cc2b3cdaa9c858) Fix test to identify pipes on cygwin/msys, fixes #179
* [`0926754`](https://github.com/fusesource/jansi/commit/092675487c91133144d4f1b81b5cf69a444883d3) Fix ansi stream state after an exception is thrown, fixes #30
* [`c77ec6c`](https://github.com/fusesource/jansi/commit/c77ec6c5e48c89ae2b641aa58e50b207d3edeaf0) Just use a plain random to avoid a dependency on SecureRandom
* [`090132d`](https://github.com/fusesource/jansi/commit/090132d8160e50ef0c9d4b85990b32744f14e439) Movement with cursorUpLine/cursorDownLine don't work on windows, fixes #140
* [`2e84084`](https://github.com/fusesource/jansi/commit/2e840849f842c74943718ec291acecfb4556a9ed) jansi parent dependency upgrade, fixes #154
* [`8420b9c`](https://github.com/fusesource/jansi/commit/8420b9c381fc41f36561f8dc453b5ba6765947e3) Ansi should implement java.lang.Appendable, fixes #168
* [`70adaeb`](https://github.com/fusesource/jansi/commit/70adaebb1d8d38c9851b4de1c846441d753c8152) Check values when emitting ansi sequences for cursor movement, fixes #172
* [`0171ecb`](https://github.com/fusesource/jansi/commit/0171ecba47d28cfb467b04a87617205d41072fa3) Allow the re-initialization of streams, fixes #178
* [`5ce085b`](https://github.com/fusesource/jansi/commit/5ce085b7e3acc9a3a95d42f40a49be0cec301947) Merge pull request #177 from MrDOS/improvement/use-freebsd-container-working-directory
* [`816da96`](https://github.com/fusesource/jansi/commit/816da962a3e592e933a82d57c1a295ced6520d3f) Use the FreeBSD container working directory.
* [`4731663`](https://github.com/fusesource/jansi/commit/47316638568da54154821373f3e3d63388a481dc) Fix changelog

## [Jansi 2.0.1][2_0_1], released 2020-10-28
[2_0_1]: https://repo.maven.apache.org/maven2/org/fusesource/jansi/jansi/2.0.1

* [`282568f`](https://github.com/fusesource/jansi/commit/282568f5abe188aeebd04b0c8714d2e7f0f77762) Make sure to include the jansi.properties file in native mode, use regex for libs

## [Jansi 2.0][2_0], released 2020-10-26
[2_0]: https://repo.maven.apache.org/maven2/org/fusesource/jansi/jansi/2.0

* [`8d65958`](https://github.com/fusesource/jansi/commit/8d659588bc7861397073532a926cafc9fb58c08c) Remove old pom
* [`3d828cb`](https://github.com/fusesource/jansi/commit/3d828cb998c13e404de7db0d984d4c945682232b) Try to be more resilient when verifying the extracted library
* [`8bafdf7`](https://github.com/fusesource/jansi/commit/8bafdf7dbec790d7180863b033936f1d0e85136f) Add resources for graalvm support
* [`71b5164`](https://github.com/fusesource/jansi/commit/71b5164523ee19d687f0d404122c615002b66291) Clean a bit the native build part
* [`3ac28bb`](https://github.com/fusesource/jansi/commit/3ac28bb72c44e3a6a8c021610a1d218e415b7a37) Upgrade to junit 5
* [`146b5f9`](https://github.com/fusesource/jansi/commit/146b5f9e8d9e8cb2a8fee6d90e3fea32cb45b549) Reorganize to a single module
* [`4d24cd8`](https://github.com/fusesource/jansi/commit/4d24cd8e62556c8a3254c9a31831e72143f17910) Remove hardly used stuff
* [`aa0c419`](https://github.com/fusesource/jansi/commit/aa0c4194b41f6ee0aeb83922d4cc39f585df78ac) Include jansi native
* [`f252f0a`](https://github.com/fusesource/jansi/commit/f252f0a6cdd6223582201202971b94b05a7ce56a) Switch to 2.0-SNAPSHOT
* [`672c0f9`](https://github.com/fusesource/jansi/commit/672c0f9bf3adc9847d0a1b5f881c028604000f7b) Forgot one call to new PrintStream
* [`8283aaf`](https://github.com/fusesource/jansi/commit/8283aafa1a7870488e302c7499b9e127ae9265ba) Fix possible encoding issues in the new AnsiNoSyncOutputStream
* [`182b884`](https://github.com/fusesource/jansi/commit/182b884d73d950e6d370e70162f92b0fd4b79372) Fix attributes used when erasing on windows, fixes #160
* [`e12ff97`](https://github.com/fusesource/jansi/commit/e12ff979f0e5ede59b477733349c6f333a2c0314) Merge pull request #173 from fusesource/dependabot/maven/jansi/junit-junit-4.13.1
* [`8370e29`](https://github.com/fusesource/jansi/commit/8370e29bb5cda83ccab1ab472debde59deb22844) Use a different enum when using the ENABLE_VIRTUAL_TERMINAL_PROCESSING on windows
* [`7be32c5`](https://github.com/fusesource/jansi/commit/7be32c54b3fdd0c60d5930657b56334031ff32cc) Do use the new variables
* [`ef60bc5`](https://github.com/fusesource/jansi/commit/ef60bc5bf53cdb29ec706517d2eec5c988d55238) Use constants for properties, allow by-passing the new system
* [`11b52d2`](https://github.com/fusesource/jansi/commit/11b52d20a93763653f9327939431453556f50c4f) Fix logo
* [`57aa84d`](https://github.com/fusesource/jansi/commit/57aa84d4120395922458f61f20e741198f051087) Fix windows support, do not use reflection while creating the system out/err print streams
* [`07916c1`](https://github.com/fusesource/jansi/commit/07916c1af9769ded99ce6770db1348db4e062921) Optimize system print streams when processing ANSI sequences
* [`f6e8a9a`](https://github.com/fusesource/jansi/commit/f6e8a9a5527805c102761dec14000f4dcbd7c32e) Remove unneeded boxing/unboxing
* [`753c662`](https://github.com/fusesource/jansi/commit/753c6625e528ef9245ba469d004ca8e2661a010a) Bump junit from 4.7 to 4.13.1 in /jansi
* [`1461096`](https://github.com/fusesource/jansi/commit/146109610a85892efdad4f50a1a974d9d6ac21ee) fixed typos giving ANSI output inconsistent with Ansi method name
* [`c2aaada`](https://github.com/fusesource/jansi/commit/c2aaadaff3b8f5e34d6767585f6ba668fa2ba651) introduce jansi-native
* [`f9e85d0`](https://github.com/fusesource/jansi/commit/f9e85d0c48713743f671d59253060d3e8cb8e462) #151 extracted more common code to AnsiProcessor
* [`7e6bd5b`](https://github.com/fusesource/jansi/commit/7e6bd5b2a5f10a32ec50e3d85b2c452328bdbac1) #151 extracted AnsiProcessor and WindowsAnsiProcessor
* [`3b65176`](https://github.com/fusesource/jansi/commit/3b6517625edd9b66ef22ce9109b1746d27ead8a9) typo...
* [`48489d0`](https://github.com/fusesource/jansi/commit/48489d09c1731b8afb9e9cad94688b248fe8fcf3) #153 fixed buffer size
* [`0e7a582`](https://github.com/fusesource/jansi/commit/0e7a5822f56dfa4345ac74ebfeacf8fe4207845b) example artifact has better maven-metadata than jansi
* [`adb9e24`](https://github.com/fusesource/jansi/commit/adb9e24fccac4107f705327cd8fe28739c5f6d12) enhance memory usage using buffer, or avoiding alloc
* [`2f866c4`](https://github.com/fusesource/jansi/commit/2f866c4cf31b8ae44e005515326ad9b2819a394f) #146 add support for ConEmu (used by cmder)
* [`fe749a5`](https://github.com/fusesource/jansi/commit/fe749a526f1c1d6925e60fd6deb939f54732cad7) prepare for next development iteration
* [`da13459`](https://github.com/fusesource/jansi/commit/da13459c758e94f31ac6a24c48fac3ed5abab5c9) fixed links to commit
* [`07702b4`](https://github.com/fusesource/jansi/commit/07702b455d23034523c2f5be0e52dda82973699e) prepare 1.18 site publication

## [Jansi 1.18][1_18], released 2019-04-02
[1_18]: https://repo.maven.apache.org/maven2/org/fusesource/jansi/jansi/1.18

* [`d52d37d`](https://github.com/fusesource/jansi/commit/d52d37df44a7262240cd1e3d471025ca255ce7f9) Switch to hawtjni 1.17
* [`bbd72d6`](https://github.com/fusesource/jansi/commit/bbd72d6e26f4e70a20a5736a496f9cb9d354ca3f) Fix error messages, #134
* [`3101eeb`](https://github.com/fusesource/jansi/commit/3101eeb7208d190f171a6fbded45bd984f0606dd) Provide an alternative way to get jansi version
* [`5a5e8cb`](https://github.com/fusesource/jansi/commit/5a5e8cbfae657900bd7d1caa55e2978086394e5d) #130 added Automatic-Module-Name to manifest for Java 9 auto-module
* [`1c4b015`](https://github.com/fusesource/jansi/commit/1c4b015d16432295e786dc4f806cdd077c85043e) Merge branch 'master' of https://github.com/fusesource/jansi.git
* [`70ff98d`](https://github.com/fusesource/jansi/commit/70ff98d5cbd5fb005d8a44ed31050388b256f9c6) switch to 1.18-SNAPSHOT # Conflicts: #     example/pom.xml #       jansi/pom.xml # pom.xml
* [`d5f60ed`](https://github.com/fusesource/jansi/commit/d5f60edf0f8c15c0c888e2aa5980c023ef1c8c9e) updated changelog for 1.17.1 release
* [`44b1ebf`](https://github.com/fusesource/jansi/commit/44b1ebf7c877f6b14aaa31f55b8e4ee47350d991) added Maven central icon
* [`b1ef765`](https://github.com/fusesource/jansi/commit/b1ef7659b711e5c94272b3802d9e0ed6feebda62) #98 added link to HawtJNI Runtime Library class documentation
* [`0507042`](https://github.com/fusesource/jansi/commit/0507042f44562316339b27fe997ad6456e674cf4) fixed compiler warning
* [`7ae726d`](https://github.com/fusesource/jansi/commit/7ae726d549de8d78cf425b1a3d2c022eb1b1cd75) improved javadoc
* [`272f395`](https://github.com/fusesource/jansi/commit/272f395e5eb7a38f075ec0a125d75f8c19795ca4) added details on redirection to a file
* [`e2ac629`](https://github.com/fusesource/jansi/commit/e2ac629b77614be325c6603ce804578ddb11749c) #124 detect console handle from stderr separately from stdout
* [`3a9c8fb`](https://github.com/fusesource/jansi/commit/3a9c8fbeae40b18268af3d4d87fb81084233ff9f) switch to 1.18-SNAPSHOT
* [`bca4c36`](https://github.com/fusesource/jansi/commit/bca4c365eaa8492297e9d0fc02c6abc1fdd5d4b1) updated changelog for 1.17.1 release
* [`b602f13`](https://github.com/fusesource/jansi/commit/b602f1301073cc22e60348d89807f37313bd5454) added Maven central icon
* [`a463253`](https://github.com/fusesource/jansi/commit/a46325307f51194ca7dd007d2042dc38bc61af0d) #98 added link to HawtJNI Runtime Library class documentation
* [`6af32f4`](https://github.com/fusesource/jansi/commit/6af32f434126611cbe8f362bca2488e4f5e72859) fixed compiler warning
* [`49e8293`](https://github.com/fusesource/jansi/commit/49e829380f599f79b8c82b3c8ea7aae56918ba02) improved javadoc
* [`3302b8f`](https://github.com/fusesource/jansi/commit/3302b8f4d9276d0f1c1f3698bc5f6440d5dab5a8) added details on redirection to a file
* [`9338527`](https://github.com/fusesource/jansi/commit/93385273035124d3541e90e3a15568371aa2f474) #124 detect console handle from stderr separately from stdout

## [Jansi 1.17.1][1_17_1], released 2018-04-16
[1_17_1]: https://repo.maven.apache.org/maven2/org/fusesource/jansi/jansi/1.17.1

* [`2a505ba`](https://github.com/fusesource/jansi/commit/2a505ba1494f0768311ecc80aef963e7076daaf5) improved javadoc to link to Jansi native
* [`1e0f706`](https://github.com/fusesource/jansi/commit/1e0f7068465dddf4c18fa282923b06df52d705d7) Revert "Fix Thread test to run test in additional threads"
* [`8918099`](https://github.com/fusesource/jansi/commit/89180993494a176ea8c7dfbb1250f81d693637cb) Fix Thread test to run test in additional threads
* [`8329e31`](https://github.com/fusesource/jansi/commit/8329e31c722e726f59a73654b8425b8ef5015760) Update documentation about cursor move
* [`27c99e1`](https://github.com/fusesource/jansi/commit/27c99e10add97ae49e464cc8279eead19e76df9d) fixed invalid buffer size when copying to PrintStream
* [`0728c6d`](https://github.com/fusesource/jansi/commit/0728c6dbc9b3124bd6a9c63b6b642346a6339e79) fixed incorrect implementation in case of ansi.strip
* [`2142202`](https://github.com/fusesource/jansi/commit/21422020a424d229db83f4d32fa77c55dc51a352) added explanations on results expected for specific situations
* [`1a77e3c`](https://github.com/fusesource/jansi/commit/1a77e3cb816ff366e6ec811c3fb27a36a5a72916) #119 detect Git bash with TERM=xterm since BASH is not visible
* [`abe94bf`](https://github.com/fusesource/jansi/commit/abe94bf243ddb3a43fe173c8e08d105451b03efc) #114 prepare 1.17.1 release
* [`73c621f`](https://github.com/fusesource/jansi/commit/73c621ff7f2d0fd8c7e5c23a814359c34fdab012) on MSYSTEM=MINGW, only BASH is natively ANSI aware, not Git CMD #119
* [`48b0be5`](https://github.com/fusesource/jansi/commit/48b0be5eaa8a0b2cb8d27173d902d1da5f3ba9d6) improved diagnostic output
* [`68c5810`](https://github.com/fusesource/jansi/commit/68c5810336c606e938c0b70409276c3f53e3b568) Javadoc quick fix for warnings and errors
* [`e45e466`](https://github.com/fusesource/jansi/commit/e45e4665538ba9234f5ee5d7b06d78d6a03deda3) Synchronization to protect against problems while analyzing Ansi codes resulting from multithreading
* [`0645365`](https://github.com/fusesource/jansi/commit/06453651594188403f28614097c8598c3bf387e6) update changelog.md for 1.17 release
* [`cd34211`](https://github.com/fusesource/jansi/commit/cd342119ced2e348245d6cb59990401f382ffbe6) Pass RESET_CODE via filter while closing PrintStream
* [`14b601c`](https://github.com/fusesource/jansi/commit/14b601ccaa4a24a5554407a8bc7faf45b43b622f) prepare Jansi 1.17 site

## [Jansi 1.17][1_17], released 2018-02-02
[1_17]: https://repo.maven.apache.org/maven2/org/fusesource/jansi/jansi/1.17

* [`74c300d`](https://github.com/fusesource/jansi/commit/74c300d4d14d18d33876f4daca937adec91f5e13) Upgrade to hawtjni 1.16
* [`3f47f7e`](https://github.com/fusesource/jansi/commit/3f47f7e36d49c4a42d51d04a682382f8450fefdc) Upgrade to jansi-native 1.8
* [`2964a2f`](https://github.com/fusesource/jansi/commit/2964a2fbb50525cf8571a5a04e8363c8f131015d) deprecated WindowsAnsiOutputStream: use WindowsAnsiPrintStream
* [`8447fda`](https://github.com/fusesource/jansi/commit/8447fdafb0426a02cc1f9117ff34d7f80bd81ab1) optimization: use valueOf instead of constructor
* [`6251669`](https://github.com/fusesource/jansi/commit/62516698e86e0c6d55f3f9174c93f4943f1aed2f) added info on native library location and auto-extract
* [`007b935`](https://github.com/fusesource/jansi/commit/007b93526da79a6601107e1f6270d8e2cbb0c3a4) fixed typos
* [`346b788`](https://github.com/fusesource/jansi/commit/346b788d7f6d57f29319161157ceb481d72cfe70) added jansi-native API docs
* [`259700a`](https://github.com/fusesource/jansi/commit/259700a4daf9859a42bedf76c4df1159955c8a5f) 3.0.0 is not released yet...
* [`47e2e99`](https://github.com/fusesource/jansi/commit/47e2e99bd9afa0d88936b1c9f255dbf4fcc1db4e) removed duplicate line (already in include)
* [`16d9210`](https://github.com/fusesource/jansi/commit/16d921095bdc2e93b05bf0db8beea69cacb4b18b) improved/fixed javadoc
* [`4b24c09`](https://github.com/fusesource/jansi/commit/4b24c0928d8c233cd7dc7c7077ad4d4ddb82cb2e) Filter out escape sequence 'character set' select
* [`5f8eb45`](https://github.com/fusesource/jansi/commit/5f8eb45b376c77b6f72b65b1afe485c322f49198) Correct support for the bright colors on windows - regression fix
* [`5ac2049`](https://github.com/fusesource/jansi/commit/5ac2049c23e4d091bf48a531b450adfdb2d4ef7d) [#95](http://github.com/fusesource/jansi/issues/95) add comments on expected diffs between Print and Output Streams
* [`40631bd`](https://github.com/fusesource/jansi/commit/40631bdf503473054eefd27b3c2301bfcbe9643d) Merge pull request [#95](http://github.com/fusesource/jansi/issues/95) from hboutemy/printstream
* [`3b2eab6`](https://github.com/fusesource/jansi/commit/3b2eab623277d4e6f2dc3b9e355ed6bb9dbbf84f) Merge pull request [#99](http://github.com/fusesource/jansi/issues/99) from jycchoi/master
* [`f7a84bf`](https://github.com/fusesource/jansi/commit/f7a84bf2e1dff352c83089282ff1b1aedc20f901) Merge pull request [#84](http://github.com/fusesource/jansi/issues/84) from hboutemy/website-1.16
* [`5294c87`](https://github.com/fusesource/jansi/commit/5294c8728929f8c97c373141227242df8bfe7e1b) Correct support for the bright colors on windows - regression fix
* [`d340856`](https://github.com/fusesource/jansi/commit/d340856e78925269ecc76cd16c29850fa9ca01e0) add AnsiPrintStream and FilterPrintStream to avoid encoding issues
* [`769ebe0`](https://github.com/fusesource/jansi/commit/769ebe03655169b2eb380600912a55ac88b13efb) Merge pull request [#86](http://github.com/fusesource/jansi/issues/86) from hboutemy/javadoc
* [`3ce6987`](https://github.com/fusesource/jansi/commit/3ce698726b353dd06acaa76777c6c2451415b850) Merge pull request [#92](http://github.com/fusesource/jansi/issues/92) from pmhahn/parse-sgr0
* [`29dff3c`](https://github.com/fusesource/jansi/commit/29dff3cb48615c3d480cd400774d69ce724a9b84) Merge pull request [#88](http://github.com/fusesource/jansi/issues/88) from hboutemy/executable
* [`b11eb3e`](https://github.com/fusesource/jansi/commit/b11eb3e421bff15e22cbc650256ed8f707b57fd9) added basic color rendering tests
* [`cf69386`](https://github.com/fusesource/jansi/commit/cf69386f4010a05b5b122195957dd7f09b0d92ad) added explicit result of AnsiConsole system install on stdout&stderr
* [`0484150`](https://github.com/fusesource/jansi/commit/0484150d01bc05e4f832fe6ee094fa7f46205338) diagnose isatty for both stdout and stderr
* [`e35a57f`](https://github.com/fusesource/jansi/commit/e35a57f5fb17dbe3ac33040e436c12c96c613f81) 'isatty' check added to test
* [`ef2d858`](https://github.com/fusesource/jansi/commit/ef2d858448215ef8639663c186653c501ddfb932) Filter out escape sequence 'character set' select
* [`257b1de`](https://github.com/fusesource/jansi/commit/257b1de6336d3a6848e6739e958ee1d7bd77a712) added main class to jansi.jar to help diagnose issues or configs
* [`f33497c`](https://github.com/fusesource/jansi/commit/f33497c0117b556e39fe806581e717292e6862ef) improved/fixed javadoc
* [`b5a840e`](https://github.com/fusesource/jansi/commit/b5a840ecc600ed1cb6c3a9434e31b2d64c7d63cc) link to central for releases download
* [`d3d8488`](https://github.com/fusesource/jansi/commit/d3d848848db654d756ade20bb0391422f31e9a05) prepared website publication for 1.16, with link to changelog

## [Jansi 1.16][1_16], released 2017-05-04
[1_16]: https://repo.maven.apache.org/maven2/org/fusesource/jansi/jansi/1.16

* [`65c537c`](https://github.com/fusesource/jansi/commit/65c537c00f57565b2794d57472ec24e36ac2420a) Upgrade to released versions of hawtjni and jansi-native
* [`7746c55`](https://github.com/fusesource/jansi/commit/7746c55160eb1d5fa1e820c838e6d540380e8c66) Update changelog.md
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
[1_2]: https://repo.maven.apache.org/maven2/org/fusesource/jansi/jansi/1.2

* Improved Java Docs.
* Better windows ANSI handling of: erase screen and line and move to col.
* New method: Ansi.newline().
* Fixed missing return statement in cursor up case. 
* Reset the attributes when the ANSI output stream is closed on unix.

## [Jansi 1.1][1_1], released 2009-11-23
[1_1]: https://repo.maven.apache.org/maven2/org/fusesource/jansi/jansi/1.1

* AnsiRender can now be used in a static way and made easier to use with the ANSI builder.
* Merged [Jason Dillon's Fork](http://github.com/jdillon/jansi/tree/bb86e0e79bec850167ddfd8c4a86fb9ffef704e5): 
	* Pluggable ANSI support detection.
	* ANSI builder can be configured to not generate ANSI escapes.
	* AnsiRender provides an easier way to generate escape sequences.
* [JANSI-5]: Attribute Reset escape should respect original console colors.
* [JANSI-4]: Restore command console after closing wrapped OutputStream on Windows.
* [JANSI-1]: Added extensions for colors and other attributes to Ansi builder. 

## [Jansi 1.0][1_0], released 2009-08-25
[1_0]: https://repo.maven.apache.org/maven2/org/fusesource/jansi/jansi/1.0

* Initial Release.
