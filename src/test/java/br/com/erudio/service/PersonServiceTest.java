/**
 * 
 */
package br.com.erudio.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;

import br.com.erudio.data.dto.V1.BookDTO;
import br.com.erudio.data.dto.V1.PersonDTOV1;
import br.com.erudio.exception.RequiredObjectIsNullException;
import br.com.erudio.integrationtests.testcontainers.dto.PersonDTO;
import br.com.erudio.mapper.ObjectMapper;
import br.com.erudio.model.Person;
import br.com.erudio.repository.PersonRepository;
import br.com.erudio.unitetestes.mapper.mocks.MockPerson;


/**
 * 
 */
@TestInstance(value = Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

	MockPerson input;

	@InjectMocks
	private PersonService service;

	@Mock
	PersonRepository personRepository;
	
	@Mock
    private PagedResourcesAssembler<PersonDTOV1> assembler;

	@BeforeEach
	void setUp() {
		input = new MockPerson();
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testCreatedWithNullPerson() {
		Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
			service.create(null);
		});

		String expetedeMessage = "It is not  allowed to persist a null object!";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expetedeMessage));
	}

	@Test
	void testUpdateWithNullPerson() {
		Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
			service.update(null);
		});

		String expetedeMessage = "It is not  allowed to persist a null object!";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expetedeMessage));
	}

	@Test
	void update() {
		Person person = input.mockEntity(1);
		Person persisted = person;
		persisted.setId(1L);

		PersonDTOV1 dto = input.mockDTO(1);

		when(personRepository.findById(1L)).thenReturn(Optional.of(person));
		when(personRepository.save(person)).thenReturn(persisted);

		var result = service.update(dto);

		assertNotNull(result);
		assertNotNull(result.getId());
		assertNotNull(result.getLinks());

		assertNotNull(result.getLinks().stream().anyMatch(link -> link.getRel().value().equals("self")
				&& link.getHref().endsWith("/api/person/v1/1") && link.getType().equals("GET")));

		assertNotNull(result.getLinks().stream().anyMatch(link -> link.getRel().value().equals("findAll")
				&& link.getHref().endsWith("/api/person/v1") && link.getType().equals("GET")));

		assertNotNull(result.getLinks().stream().anyMatch(link -> link.getRel().value().equals("create")
				&& link.getHref().endsWith("/api/person/v1") && link.getType().equals("POST")));

		assertNotNull(result.getLinks().stream().anyMatch(link -> link.getRel().value().equals("update")
				&& link.getHref().endsWith("/api/person/v1") && link.getType().equals("PUT")));

		assertNotNull(result.getLinks().stream().anyMatch(link -> link.getRel().value().equals("delete")
				&& link.getHref().endsWith("/api/person/v1/1") && link.getType().equals("DELETE")));

		assertEquals("Address Test1", result.getAddress());
		assertEquals("First Name Test1", result.getFirstName());
		assertEquals("Last Name Test1", result.getLastName());
		assertEquals("Female", result.getGender());
	}

	@Test
	void findById() {
		Person person = input.mockEntity(1);
		person.setId(1L);
		when(personRepository.findById(1L)).thenReturn(Optional.of(person));

		var result = service.findById(1L);

		assertNotNull(result);
		assertNotNull(result.getId());
		assertNotNull(result.getLinks());

		assertNotNull(result.getLinks().stream().anyMatch(link -> link.getRel().value().equals("self")
				&& link.getHref().endsWith("/api/person/v1/1") && link.getType().equals("GET")));

		assertNotNull(result.getLinks().stream().anyMatch(link -> link.getRel().value().equals("findAll")
				&& link.getHref().endsWith("/api/person/v1") && link.getType().equals("GET")));

		assertNotNull(result.getLinks().stream().anyMatch(link -> link.getRel().value().equals("create")
				&& link.getHref().endsWith("/api/person/v1") && link.getType().equals("POST")));

		assertNotNull(result.getLinks().stream().anyMatch(link -> link.getRel().value().equals("update")
				&& link.getHref().endsWith("/api/person/v1") && link.getType().equals("PUT")));

		assertNotNull(result.getLinks().stream().anyMatch(link -> link.getRel().value().equals("delete")
				&& link.getHref().endsWith("/api/person/v1/1") && link.getType().equals("DELETE")));

		assertEquals("Address Test1", result.getAddress());
		assertEquals("First Name Test1", result.getFirstName());
		assertEquals("Last Name Test1", result.getLastName());
		assertEquals("Female", result.getGender());
	}

	@Test
	void create() {
		Person person = input.mockEntity(1);
		Person persisted = person;
		persisted.setId(1L);
		PersonDTOV1 dto = input.mockDTO(1);
		when(personRepository.save(person)).thenReturn(persisted);

		var result = service.create(dto);

		assertNotNull(result);
		assertNotNull(result.getId());
		assertNotNull(result.getLinks());

		assertNotNull(result.getLinks().stream().anyMatch(link -> link.getRel().value().equals("self")
				&& link.getHref().endsWith("/api/person/v1/1") && link.getType().equals("GET")));

		assertNotNull(result.getLinks().stream().anyMatch(link -> link.getRel().value().equals("findAll")
				&& link.getHref().endsWith("/api/person/v1") && link.getType().equals("GET")));

		assertNotNull(result.getLinks().stream().anyMatch(link -> link.getRel().value().equals("create")
				&& link.getHref().endsWith("/api/person/v1") && link.getType().equals("POST")));

		assertNotNull(result.getLinks().stream().anyMatch(link -> link.getRel().value().equals("update")
				&& link.getHref().endsWith("/api/person/v1") && link.getType().equals("PUT")));

		assertNotNull(result.getLinks().stream().anyMatch(link -> link.getRel().value().equals("delete")
				&& link.getHref().endsWith("/api/person/v1/1") && link.getType().equals("DELETE")));

		assertEquals("Address Test1", result.getAddress());
		assertEquals("First Name Test1", result.getFirstName());
		assertEquals("Last Name Test1", result.getLastName());
		assertEquals("Female", result.getGender());
	}

	@Test
	void delete() {
		Person person = input.mockEntity(1);
		person.setId(1L);

		when(personRepository.findById(1L)).thenReturn(Optional.of(person));

		service.delete(1L);

		verify(personRepository, times(1)).findById(anyLong());
		verify(personRepository, times(1)).delete(any(Person.class));
		verifyNoMoreInteractions(personRepository);
	}

	@Test
	void findAll() {

		Pageable pageable = PageRequest.of(0, 5, Sort.by(Direction.ASC, "firstName"));

	    List<Person> list = input.mockEntityList();
	    Page<Person> page = new PageImpl<>(list, pageable, list.size());

	    when(personRepository.findAll(pageable)).thenReturn(page);
	    
	    List<EntityModel<PersonDTOV1>> entityModels = list.stream()
	            .map(p -> EntityModel.of(ObjectMapper.parseObject(p, PersonDTOV1.class)))
	            .toList();

	    PagedModel<EntityModel<PersonDTOV1>> expected = PagedModel.of(
	            entityModels,
	            new PagedModel.PageMetadata(page.getSize(), page.getNumber(), page.getTotalElements())
	        );

	    when(assembler.toModel(any(Page.class), any(Link.class))).thenReturn(expected);

	    // when
	    PagedModel<EntityModel<PersonDTOV1>> people = service.findAll(pageable);


		assertNotNull(people);
		assertEquals(14, people.getContent().size());

		var personOne = people.getContent().stream().toList().get(1);

		assertNotNull(personOne);
		assertNotNull(personOne.getContent().getId());
		assertNotNull(personOne.getLinks());

		assertNotNull(personOne.getLinks().stream().anyMatch(link -> link.getRel().value().equals("self")
				&& link.getHref().endsWith("/api/person/v1/1") && link.getType().equals("GET")));

		assertNotNull(personOne.getLinks().stream().anyMatch(link -> link.getRel().value().equals("findAll")
				&& link.getHref().endsWith("/api/person/v1") && link.getType().equals("GET")));

		assertNotNull(personOne.getLinks().stream().anyMatch(link -> link.getRel().value().equals("create")
				&& link.getHref().endsWith("/api/person/v1") && link.getType().equals("POST")));

		assertNotNull(personOne.getLinks().stream().anyMatch(link -> link.getRel().value().equals("update")
				&& link.getHref().endsWith("/api/person/v1") && link.getType().equals("PUT")));

		assertNotNull(personOne.getLinks().stream().anyMatch(link -> link.getRel().value().equals("delete")
				&& link.getHref().endsWith("/api/person/v1/1") && link.getType().equals("DELETE")));

		assertEquals("Address Test1", personOne.getContent().getAddress());
		assertEquals("First Name Test1", personOne.getContent().getFirstName());
		assertEquals("Last Name Test1", personOne.getContent().getLastName());
		assertEquals("Female", personOne.getContent().getGender());
		

	
	}
	

}
