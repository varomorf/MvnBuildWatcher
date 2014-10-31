MvnBuildWatcher
===============

A simple GUI for executing/watching big MVN builds by analyzing its output.

It relies on the 'M2_HOME environment' variable meaning it MUST be defined it for it to work 
properly (although you'll be able to override it via properties).

To launch a build you just specify a file with each line containing the directory where to launch 
the build and the goals and options to be passed to maven, separated by a semicolon.

Ej.

    /foo/bar/dir1;clean install -DskipTests
    /foo/bar/dir2;clean install
    /foo/bar/dir3;clean deploy -Pprofile1

Once the file is defined, you just load the file on the app and it will launch each build one after
the other, while it analyzes the output from the build showing you the status of said build.
