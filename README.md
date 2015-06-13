# scala-jdbc
A small library that makes working with JDBC databases a lot easier.

Pro's:
- Small library,
- Small number of well known dependencies (typesafe-config, commons-dbcp, scala-arm and your JDBC driver of choice),    
- Very accessible to Java/Scala programmers that just want to do [SQL](https://en.wikipedia.org/wiki/SQL),
- If you want to create reactive applications or advanced features (like async/streaming) please use [Slick 3.0](http://slick.typesafe.com) and don't use this library.

# Dependency
To include the JDBC library, add the following lines to your build.sbt file:

```
resolvers += "dnvriend at bintray" at "http://dl.bintray.com/dnvriend/maven"

libraryDependencies += "com.github.dnvriend" %% "scala-jdbc" % "1.0.0"
```
    
# Usage
## Configure:
Put the following in your `application.conf`. The name `docker` is a database configuration that will be used when creating a `JdbcConnection`.

```
jdbc-connection {
  docker {
    username           = "docker"
    password           = "docker"
    driverClassName    = "org.postgresql.Driver"
    url                = "jdbc:postgresql://boot2docker:5432/docker"
    initialSize        = 1
    maxIdle            = 1
    maxActive          = 1
  }
}
```

## Setting up a connection:
A connection can be created by creating an `implicit val` which is a JdbcConfig with JdbcConnection. Set the name of the
database configuration to whatever you used in `application.conf`. In the example, the name is `docker`. When using `akka`
the config can be loaded from the default `ActorSystem`.

```scala
import com.github.dnvriend.jdbc._
implicit val conn: JdbcConnection = new JdbcConfig with JdbcConnection {
 override def name: String = "docker"
 override def config: Config = ConfigFactory.defaultApplication()
}
```

## Example using in a repository:
Using the [Repository Pattern](https://lostechies.com/jimmybogard/2009/09/03/ddd-repository-implementation-patterns/) and 
a little bit of FP, and some Scala trickeries, we can create an example PersonRepository that will (un)marshal case classes
for us from/to a database table, with the added bonus that everything works based on plain old SQL queries. You can execute
every query you wish, create joins, unions, and everything can be converted to a Seq of case classes.
  
```scala
import java.sql.ResultSet

import scala.util.Try

object PersonRepository {
  // case classes and (un)marshaller(s) could be put in separate objects/traits
  case class Person(id: String, firstName: String, lastName: String, created: Option[String] = None)

  implicit def rowToExport(row: ResultSet): Person =
    Person(row.str("ID"), row.str("FIRST_NAME"), row.str("LAST_NAME"), row.dateTimeStrOpt("CREATED"))
  
  def savePerson(person: Person)(implicit conn: JdbcConnection): Try[Int] =
    conn.executeUpdate(
      q"""
         |INSERT INTO PERSONS
         |(
         |  ID,
         |  FIRST_NAME,
         |  LAST_NAME,
         |  CREATED
         |)
         | VALUES
         |(
         | ${person.id},
         | ${person.firstName},
         | ${person.lastName},
         | CURRENT_TIMESTAMP
         |)
       """.stripMargin)

  def persons(limit: Int = Int.MaxValue, offset: Int = 0)(implicit conn: JdbcConnection): Try[Seq[Person]] =
    conn.mapQuery(q"SELECT * FROM PERSONS ORDER BY CREATED DESC LIMIT $limit OFFSET $offset")

  def person(id: String)(implicit conn: JdbcConnection): Try[Option[Person]] =
    conn.mapSingle(q"SELECT * FROM PERSONS WHERE ID = $id")

  def clear()(implicit conn: JdbcConnection): Try[Int] =
    conn.executeUpdate("TRUNCATE PERSONS")

  def create()(implicit conn: JdbcConnection): Try[Int] =
    conn.executeUpdate(
      q"""
         |CREATE TABLE PERSONS
         |(
         | ID CHAR(36) NOT NULL PRIMARY KEY,
         | FIRST_NAME VARCHAR(255),
         | LAST_NAME VARCHAR(255),
         | CREATED TIMESTAMP NOT NULL
         |)
       """.stripMargin)

  def drop()(implicit conn: JdbcConnection): Try[Int] =
    conn.executeUpdate("DROP TABLE PERSONS")
}
```