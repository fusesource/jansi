We gladly accept patches if you can find ways to improve, tune or fix
${project_name} in some way.

Most IDEs can create nice patches now very easily. e.g. in Eclipse just
right click on a file/directory and select `Team -> Create Patch`. Then
just save the patch as a file and then submit it. (You may have to click
on `Team -> Share...` first to enable the Subversion options).
Incidentally if you are an Eclipse user you should install the
[subclipse](http://subclipse.tigris.org/) plugin.

If you're a command line person try the following to create the patch

    diff -u Main.java.orig Main.java >> patchfile.txt

or
    svn diff Main.java >> patchfile.txt

### Submitting patches

The easiest way to submit a patch is to create a new issue at our [Issue
Tracker](${project_issue_url}), attach the patch, tick the Patch
Attached button on the issue then fire off an email to the mailing
lists.
