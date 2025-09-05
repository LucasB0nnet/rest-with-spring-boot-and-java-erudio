package br.com.erudio.repository;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.erudio.model.Person;

@ExtendWith(SpringExtension.class)// integra o Spring framework com o test, carrega o contexto do spring
@DataJpaTest // carrega apenas o repositorio e entidades e contexto de banco de dados
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // O banco de dados real seja usado no test
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonRepositoryTest extends AbstractIntegrationTest{

	@Autowired
	PersonRepository personRepository;
	private static Person person;
	
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		person = new Person();
	}


	@Test
	@Order(1)
	void testFindPersonByName() {
		Pageable pageable = PageRequest.of(0, 12, Sort.by(Direction.ASC, "firstName"));
		person = personRepository.findPersonByName("luc", pageable).getContent().get(0); 
		
		assertNotNull(person);
		assertNotNull(person.getId());
		assertEquals("Lucas",person.getFirstName());
		assertEquals("Bonnet",person.getLastName());
		assertEquals("Curitiba - PR - Brasil",person.getAddress());
		assertEquals("male",person.getGender());
		assertTrue(person.getEnabled());
	}
	
	@Test
	@Order(2)
	void testDisablePerson() {
		Long id = person.getId();
		personRepository.disablePerson(id);
		
		var result = personRepository.findById(id);
		person = result.get();
		
		assertNotNull(person);
		assertNotNull(person.getId());
		assertEquals("Lucas",person.getFirstName());
		assertEquals("Bonnet",person.getLastName());
		assertEquals("Curitiba - PR - Brasil",person.getAddress());
		assertEquals("male",person.getGender());
		assertFalse(person.getEnabled());
	}

}
