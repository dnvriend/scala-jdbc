package com.github.dnvriend.jdbc

import com.github.dnvriend.jdbc.PersonRepository.Person

class PersonRepoTest extends TestSpec {

  "PersonRepo" should "save" in {
    (1 to 20).foreach { _ =>
      PersonRepository.savePerson(person) should be a 'success
    }
    PersonRepository.persons().success.value.size shouldBe 20
  }

  it should "get" in {
    val person = Person("1", "John", "Doe")
    PersonRepository.savePerson(person) should be a 'success
    PersonRepository.person("1").success.value.value mustBe {
      case Person("1", "John", "Doe", _) =>
    }
  }
}
