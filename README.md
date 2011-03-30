# PioText - A transit tracker for Lewis & Clark College

PioText is a transit tracker for Lewis & Clark College.  It
provides the school shuttle schedule through text messages.  For now,
PioText serves the schedule from a file, but eventually it will use GPS
geolocation to get realtime schedule updates.

We're well underway now, and the service will be deployed sometime
Spring Semester, 2011.  In the meantime, there is a closed beta going
on.

If you would like to know more about the project contact github user
javins.

**Developers:**  If you want to compile/test/build/run piotxt, you'll
need a Java 1.6+, and Apache Ant. All other required libraries are 
bundled in the lib/ directory.

    ant compile

will build the project.  You must build before trying to run the project.

    ant run

or

    java -jar build/piotxt.jar

will run the server.  You must have your google voice username and
password in resources/secure.properties to sucessfully run the program.