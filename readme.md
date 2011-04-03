# PioText - A transit tracker for Lewis & Clark College

PioText is a transit tracker for Lewis & Clark College.  It provides the
school shuttle schedule through text messages.

We're well underway now, and the service will be deployed sometime
Spring Semester, 2011.  In the meantime, there is a closed beta going
on.

##Developers

Feel free grab a copy and do whatever you'd like with it.  To contact
the team, check out https://github.com/javins/piotxt/contributors.

If you want to compile/test/build/run piotxt, you'll need a Java 1.6+
and Apache Ant. All other required libraries are bundled in the `lib/`
directory.

    ant build

will build the project.  You must build before trying to run the project.

    ant run

or

    java -jar build/piotxt.jar

will run the server.

If run from a console, you should be prompted for a google voice 
username and password.  To avoid having to re-enter login credentials
every time, copy `resources/secure.properties.example` to
`resources/secure.properties` and edit the `gv_user` and `gv_pass`
lines.