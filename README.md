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
    
# Usage
Setting up a connection:

```scala
import com.github.dnvriend.jdbc._
implicit val conn: JdbcConnection = new JdbcConfig with JdbcConnection {
 override def name: String = "docker"
 override def config: Config = ConfigFactory.defaultApplication()
}
```

Using in a Repository:

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