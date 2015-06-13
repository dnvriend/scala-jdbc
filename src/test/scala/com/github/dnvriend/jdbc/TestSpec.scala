package com.github.dnvriend.jdbc

import java.util.UUID

import com.github.dnvriend.jdbc.PersonRepository.Person
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest._

class TestSpec extends FlatSpec with Matchers with TryValues with OptionValues with BeforeAndAfterEach {
  implicit val conn: JdbcConnection = new JdbcConfig with JdbcConnection {
    override def name: String = "docker"
    override def config: Config = ConfigFactory.defaultApplication()
  }

  implicit class MustBeWord[T](self: T) {
    def mustBe(pf: PartialFunction[T, Unit]): Unit =
      if(!pf.isDefinedAt(self)) throw new TestFailedException("Unexpected: " + self, 0)
  }

  def randomId: String = UUID.randomUUID.toString

  val id: String = randomId

  def person = Person(randomId, "John", "Doe")

  override protected def beforeEach(): Unit = {
    (for {
      dropped <- PersonRepository.drop()
      created <- PersonRepository.create()
      truncated <- PersonRepository.clear()
    } yield { dropped + created + truncated }) should be a 'success
  }
}
