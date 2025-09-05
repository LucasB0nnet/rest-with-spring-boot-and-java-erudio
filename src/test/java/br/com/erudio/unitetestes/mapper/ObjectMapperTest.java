package br.com.erudio.unitetestes.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.erudio.data.dto.V1.PersonDTOV1;
import br.com.erudio.mapper.ObjectMapper;
import br.com.erudio.model.Person;
import br.com.erudio.unitetestes.mapper.mocks.MockPerson;

public class ObjectMapperTest {
	
	MockPerson inputObject;

	@BeforeEach
	public void setUp() {
		inputObject = new MockPerson();
	}

	@Test
	public void parseEntityToDTOTest() {
		PersonDTOV1 output = ObjectMapper.parseObject(inputObject.mockEntity(), PersonDTOV1.class);
		assertEquals(Long.valueOf(0L), output.getId());
		assertEquals("First Name Test0", output.getFirstName());
		assertEquals("Last Name Test0", output.getLastName());
		assertEquals("Address Test0", output.getAddress());
		assertEquals("Male", output.getGender());
	}

	@Test
	public void parseEntityListToDTOListTest() {
		List<PersonDTOV1> outputList = ObjectMapper.parseListObject(inputObject.mockEntityList(), PersonDTOV1.class);
		PersonDTOV1 outputZero = outputList.get(0);

		assertEquals(Long.valueOf(0L), outputZero.getId());
		assertEquals("First Name Test0", outputZero.getFirstName());
		assertEquals("Last Name Test0", outputZero.getLastName());
		assertEquals("Address Test0", outputZero.getAddress());
		assertEquals("Male", outputZero.getGender());

		PersonDTOV1 outputSeven = outputList.get(7);

		assertEquals(Long.valueOf(7L), outputSeven.getId());
		assertEquals("First Name Test7", outputSeven.getFirstName());
		assertEquals("Last Name Test7", outputSeven.getLastName());
		assertEquals("Address Test7", outputSeven.getAddress());
		assertEquals("Female", outputSeven.getGender());

		PersonDTOV1 outputTwelve = outputList.get(12);

		assertEquals(Long.valueOf(12L), outputTwelve.getId());
		assertEquals("First Name Test12", outputTwelve.getFirstName());
		assertEquals("Last Name Test12", outputTwelve.getLastName());
		assertEquals("Address Test12", outputTwelve.getAddress());
		assertEquals("Male", outputTwelve.getGender());
	}

	@Test
	public void parseDTOToEntityTest() {
		Person output = ObjectMapper.parseObject(inputObject.mockDTO(), Person.class);
		assertEquals(Long.valueOf(0L), output.getId());
		assertEquals("First Name Test0", output.getFirstName());
		assertEquals("Last Name Test0", output.getLastName());
		assertEquals("Address Test0", output.getAddress());
		assertEquals("Male", output.getGender());
	}

	@Test
	public void parserDTOListToEntityListTest() {
		List<Person> outputList = ObjectMapper.parseListObject(inputObject.mockDTOList(), Person.class);
		Person outputZero = outputList.get(0);

		assertEquals(Long.valueOf(0L), outputZero.getId());
		assertEquals("First Name Test0", outputZero.getFirstName());
		assertEquals("Last Name Test0", outputZero.getLastName());
		assertEquals("Address Test0", outputZero.getAddress());
		assertEquals("Male", outputZero.getGender());

		Person outputSeven = outputList.get(7);

		assertEquals(Long.valueOf(7L), outputSeven.getId());
		assertEquals("First Name Test7", outputSeven.getFirstName());
		assertEquals("Last Name Test7", outputSeven.getLastName());
		assertEquals("Address Test7", outputSeven.getAddress());
		assertEquals("Female", outputSeven.getGender());

		Person outputTwelve = outputList.get(12);

		assertEquals(Long.valueOf(12L), outputTwelve.getId());
		assertEquals("First Name Test12", outputTwelve.getFirstName());
		assertEquals("Last Name Test12", outputTwelve.getLastName());
		assertEquals("Address Test12", outputTwelve.getAddress());
		assertEquals("Male", outputTwelve.getGender());
	}

}
