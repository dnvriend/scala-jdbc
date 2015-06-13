name := "scala-jdbc"

version := "1.0.0"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq (
  "com.typesafe" % "config" % "1.3.0",
  "commons-dbcp" % "commons-dbcp" % "1.4",
  "com.jsuereth" %% "scala-arm" % "1.4",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41" % "test"
)

publishMavenStyle := true

licenses += ("Apache-2.0", url("http://opensource.org/licenses/apache2.0.php"))
