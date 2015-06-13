# scala-jdbc
A small library that makes working with JDBC databases a lot easier.

Pro's:
- Small library,
- Very accessible to Java/Scala programmers that just want to do SQL
- For advanced features please use Slick 3.0

# Dependency
To include the JDBC plugin into your sbt project, add the following lines to your build.sbt file:

    resolvers += "dnvriend at bintray" at "http://dl.bintray.com/dnvriend/maven"

    libraryDependencies += "com.github.dnvriend" %% "scala-jdbc" % "1.0.0"