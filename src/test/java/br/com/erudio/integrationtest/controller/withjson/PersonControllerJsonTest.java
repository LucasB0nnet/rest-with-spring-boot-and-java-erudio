package br.com.erudio.integrationtest.controller.withjson;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.erudio.config.TestConfig;
import br.com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.erudio.integrationtests.testcontainers.dto.AccountCredencialsDTO;
import br.com.erudio.integrationtests.testcontainers.dto.PersonDTO;
import br.com.erudio.integrationtests.testcontainers.dto.TokenDTO;
import br.com.erudio.integrationtests.testcontainers.dto.wrapper.WrapperPersonDTO;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Especifica que a ordem será determinada pela anotação
class PersonControllerJsonTest extends AbstractIntegrationTest {

	private static RequestSpecification specification;
	private static ObjectMapper objectMapper;
	private static PersonDTO personDTO;
	private static TokenDTO tokenDTO;

	@BeforeAll
	static void setUpBeforeClass() {
		objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		personDTO = new PersonDTO();
		tokenDTO = new TokenDTO();
		mockPerson();
	}
	
	@Test
	@Order(0)
	void testSignIn() {
		AccountCredencialsDTO credentials = new  AccountCredencialsDTO("leandro", "admin123");
		tokenDTO = RestAssured
				.given()
				.basePath("/auth/signin")
				.port(TestConfig.SERVER_PORT)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(credentials)
				.when()
				.post()
				.then().statusCode(200)
				.extract()
				.body()
				.as(TokenDTO.class);
		
		assertNotNull(tokenDTO.getAccessToken());
		assertNotNull(tokenDTO.getRefreshToken());
		
	}

	@Test
	@Order(3)
	void testFindById() throws JsonMappingException, JsonProcessingException {

		specification = new RequestSpecBuilder()// criação da requisição
				.addHeader(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.ORIGIN_LOCAL)// adiciona um Header do tipo Origin
																					// e sua respectiva
																					// url(https://localhost:8080) que é
																					// aceita!
				.addHeader(TestConfig.HEADER_PARAM_AUTHORIZATION, "Bearer "+tokenDTO.getRefreshToken())
				.setBasePath("/api/person/v1")// seta a url base e o id mocado ou criado pela base de dados
				.setPort(TestConfig.SERVER_PORT)// adiciona a porta criada 8889 classe teste.config
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))// cria no log toda o request e informa para melhor
																	// entendimento
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))// cria no log todo o response e informa para melhor
																	// entendimento
				.build();// controi a especificação passada

		var content = RestAssured.given(specification).contentType(MediaType.APPLICATION_JSON_VALUE)
				.pathParam("id", personDTO.getId()).when().get("{id}").then().statusCode(200).extract().body()
				.asString();

		PersonDTO createdPerson = objectMapper.readValue(content, PersonDTO.class);
		personDTO = createdPerson;

		assertNotNull(createdPerson.getId());
		assertTrue(createdPerson.getId() > 0);

		assertEquals("Lucas", createdPerson.getFirstName());
		assertEquals("Alves", createdPerson.getLastName());
		assertEquals("Curitiba - PR", createdPerson.getAddress());
		assertEquals("Male", createdPerson.getGender());
		assertTrue(createdPerson.getEnabled());
	}

	@Test
	@Order(1)
	void testCreate() throws JsonMappingException, JsonProcessingException {

		// Criação da especificação da requisição (Request Specification)
		specification = new RequestSpecBuilder()

				// Adiciona um cabeçalho personalizado na requisição HTTP.
				// "HEADER_PARAM_ORIGIN" pode ser algo como "Origin" e "ORIGIN_ERUDIO" um valor
				// autorizado, por exemplo.
				.addHeader(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.ORIGIN_ERUDIO)
				
				.addHeader(TestConfig.HEADER_PARAM_AUTHORIZATION, "Bearer "+tokenDTO.getRefreshToken())

				// Define o caminho base da API que será testada, neste caso "/api/person/v1"
				.setBasePath("/api/person/v1")

				// Define a porta do servidor onde a API está sendo executada
				.setPort(TestConfig.SERVER_PORT)

				// Adiciona um filtro para logar todos os detalhes da requisição (útil para
				// depuração)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))

				// Adiciona um filtro para logar todos os detalhes da resposta (útil para
				// depuração)
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))

				// Constrói a especificação com todas as configurações acima
				.build();

		// Envia uma requisição HTTP do tipo POST usando a especificação previamente
		// definida
		var content = RestAssured.given(specification) // Usa a especificação configurada (headers, porta, basePath
														// etc.)
				.contentType(MediaType.APPLICATION_JSON_VALUE) // Define o tipo de conteúdo como JSON (application/json)
				.body(personDTO) // Define o corpo da requisição. "personDTOV1" é provavelmente um objeto que
									// representa os dados de uma pessoa
				.when().post() // Envia uma requisição HTTP POST para a URL base definida na specification
				.then().statusCode(200) // Verifica se o status da resposta HTTP é 200 (OK)
				.extract().body().asString(); // Extrai o corpo da resposta como uma string e armazena na variável
												// "content"

		PersonDTO createdPerson = objectMapper.readValue(content, PersonDTO.class);
		personDTO = createdPerson;

		assertNotNull(createdPerson.getId());
		assertTrue(createdPerson.getId() > 0);

		assertEquals("Lucas", createdPerson.getFirstName());
		assertEquals("Bonnet", createdPerson.getLastName());
		assertEquals("Curitiba - PR", createdPerson.getAddress());
		assertEquals("Male", createdPerson.getGender());
		assertTrue(createdPerson.getEnabled());
	}

	@Test
	@Order(2)
	void testUpdate() throws JsonMappingException, JsonProcessingException {

		personDTO.setLastName("Alves");

		// Criação da especificação da requisição (Request Specification)
		specification = new RequestSpecBuilder()

				// Adiciona um cabeçalho personalizado na requisição HTTP.
				// "HEADER_PARAM_ORIGIN" pode ser algo como "Origin" e "ORIGIN_ERUDIO" um valor
				// autorizado, por exemplo.
				.addHeader(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.ORIGIN_ERUDIO)
				
				.addHeader(TestConfig.HEADER_PARAM_AUTHORIZATION, "Bearer "+tokenDTO.getRefreshToken())

				// Define o caminho base da API que será testada, neste caso "/api/person/v1"
				.setBasePath("/api/person/v1")

				// Define a porta do servidor onde a API está sendo executada
				.setPort(TestConfig.SERVER_PORT)

				// Adiciona um filtro para logar todos os detalhes da requisição (útil para
				// depuração)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))

				// Adiciona um filtro para logar todos os detalhes da resposta (útil para
				// depuração)
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))

				// Constrói a especificação com todas as configurações acima
				.build();

		// Envia uma requisição HTTP do tipo POST usando a especificação previamente
		// definida
		var content = RestAssured.given(specification) // Usa a especificação configurada (headers, porta, basePath
														// etc.)
				.contentType(MediaType.APPLICATION_JSON_VALUE) // Define o tipo de conteúdo como JSON (application/json)
				.body(personDTO) // Define o corpo da requisição. "personDTOV1" é provavelmente um objeto que
									// representa os dados de uma pessoa
				.when().put() // Envia uma requisição HTTP POST para a URL base definida na specification
				.then().statusCode(200) // Verifica se o status da resposta HTTP é 200 (OK)
				.extract().body().asString(); // Extrai o corpo da resposta como uma string e armazena na variável
												// "content"

		PersonDTO createdPerson = objectMapper.readValue(content, PersonDTO.class);
		personDTO = createdPerson;

		assertNotNull(createdPerson.getId());
		assertTrue(createdPerson.getId() > 0);

		assertEquals("Lucas", createdPerson.getFirstName());
		assertEquals("Alves", createdPerson.getLastName());
		assertEquals("Curitiba - PR", createdPerson.getAddress());
		assertEquals("Male", createdPerson.getGender());
		assertTrue(createdPerson.getEnabled());

	}

	@Test
	@Order(4)
	void testDisable() throws JsonMappingException, JsonProcessingException {

		var content = RestAssured.given(specification).contentType(MediaType.APPLICATION_JSON_VALUE)
				.pathParam("id", personDTO.getId()).when().patch("{id}").then().statusCode(200).extract().body()
				.asString();

		PersonDTO createdPerson = objectMapper.readValue(content, PersonDTO.class);
		personDTO = createdPerson;

		assertNotNull(createdPerson.getId());
		assertTrue(createdPerson.getId() > 0);

		assertEquals("Lucas", createdPerson.getFirstName());
		assertEquals("Alves", createdPerson.getLastName());
		assertEquals("Curitiba - PR", createdPerson.getAddress());
		assertEquals("Male", createdPerson.getGender());
		assertFalse(createdPerson.getEnabled());
	}

	@Test
	@Order(5)
	void testDelete() throws JsonMappingException, JsonProcessingException {

		specification = new RequestSpecBuilder()// criação da requisição
				.addHeader(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.ORIGIN_LOCAL)// adiciona um Header do tipo Origin
																					// e sua respectiva
																					// url(https://localhost:8080) que é
																					// aceita!
				.addHeader(TestConfig.HEADER_PARAM_AUTHORIZATION, "Bearer "+tokenDTO.getRefreshToken())
				.setBasePath("/api/person/v1")// seta a url base e o id mocado ou criado pela base de dados
				.setPort(TestConfig.SERVER_PORT)// adiciona a porta criada 8889 classe teste.config
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))// cria no log toda o request e informa para melhor
																	// entendimento
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))// cria no log todo o response e informa para melhor
																	// entendimento
				.build();// controi a especificação passada

		RestAssured.given(specification).pathParam("id", personDTO.getId()).when().delete("{id}").then()
				.statusCode(204);

	}

	@Test
	@Order(6)
	void testFindAll() throws JsonMappingException, JsonProcessingException {

		specification = new RequestSpecBuilder()// criação da requisição
				.addHeader(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.ORIGIN_LOCAL)// adiciona um Header do tipo Origin
																					// e sua respectiva
																					// url(https://localhost:8080) que é
																					// aceita!
				.addHeader(TestConfig.HEADER_PARAM_AUTHORIZATION, "Bearer "+tokenDTO.getRefreshToken())
				.setBasePath("/api/person/v1/all")// seta a url base e o id mocado ou criado pela base de dados
				.setPort(TestConfig.SERVER_PORT)// adiciona a porta criada 8889 classe teste.config
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))// cria no log toda o request e informa para melhor
																	// entendimento
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))// cria no log todo o response e informa para melhor
																	// entendimento
				.build();// controi a especificação passada

		var content = RestAssured.given(specification).accept(MediaType.APPLICATION_JSON_VALUE).queryParam("page", 3, "size", 12, "direction", "asc").when().get().then()
				.statusCode(200).extract().body().asString();

		WrapperPersonDTO wrapper = objectMapper.readValue(content, WrapperPersonDTO.class);
		List<PersonDTO> list = wrapper.getEmbedded().getPeople();

		PersonDTO personOne = list.get(0);
		personDTO = personOne;

		assertNotNull(personOne.getId());
		assertTrue(personOne.getId() > 0);

		assertEquals("Allin", personOne.getFirstName());
		assertEquals("Otridge", personOne.getLastName());
		assertEquals("09846 Independence Center", personOne.getAddress());
		assertEquals("Male", personOne.getGender());
		assertFalse(personOne.getEnabled());

		PersonDTO personTwo = list.get(2);
		personDTO = personTwo;

		assertNotNull(personTwo.getId());
		assertTrue(personTwo.getId() > 0);

		assertEquals("Allyn", personTwo.getFirstName());
		assertEquals("Josh", personTwo.getLastName());
		assertEquals("119 Declaration Lane", personTwo.getAddress());
		assertEquals("Female", personTwo.getGender());
		assertFalse(personTwo.getEnabled());
	}
	
	@Test
	@Order(7)
	void testFindByPersonName() throws JsonMappingException, JsonProcessingException {

		specification = new RequestSpecBuilder()// criação da requisição
				.addHeader(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.ORIGIN_LOCAL)// adiciona um Header do tipo Origin
																					// e sua respectiva
																					// url(https://localhost:8080) que é
																					// aceita!
				.addHeader(TestConfig.HEADER_PARAM_AUTHORIZATION, "Bearer "+tokenDTO.getRefreshToken())
				.setBasePath("/api/person/v1")// seta a url base e o id mocado ou criado pela base de dados
				.setPort(TestConfig.SERVER_PORT)// adiciona a porta criada 8889 classe teste.config
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))// cria no log toda o request e informa para melhor
																	// entendimento
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))// cria no log todo o response e informa para melhor
																	// entendimento
				.build();// controi a especificação passada
		
		var content = RestAssured.given(specification).accept(MediaType.APPLICATION_JSON_VALUE)
				.pathParam("firstName", "and")
				.queryParam("page", 0, "size", 12, "direction", "asc").when()
				.get("findPersonByName/{firstName}").then()
				.statusCode(200).extract().body().asString();

		WrapperPersonDTO wrapper = objectMapper.readValue(content, WrapperPersonDTO.class);
		System.out.println("JSON recebido:\n" + content);
		List<PersonDTO> list = wrapper.getEmbedded().getPeople();

		PersonDTO personOne = list.get(1);
		personDTO = personOne;

		assertNotNull(personOne.getId());
		assertTrue(personOne.getId() > 0);

		assertEquals("Andrey", personOne.getFirstName());
		assertEquals("Climar", personOne.getLastName());
		assertEquals("77478 Northridge Point", personOne.getAddress());
		assertEquals("Male", personOne.getGender());
		assertTrue(personOne.getEnabled());

		PersonDTO personTwo = list.get(2);
		personDTO = personTwo;

		assertNotNull(personTwo.getId());
		assertTrue(personTwo.getId() > 0);

		assertEquals("Bertrando", personTwo.getFirstName());
		assertEquals("Becconsall", personTwo.getLastName());
		assertEquals("35 Dryden Junction", personTwo.getAddress());
		assertEquals("Male", personTwo.getGender());
		assertTrue(personTwo.getEnabled());
	}

	private static void mockPerson() {
		personDTO.setFirstName("Lucas");
		personDTO.setLastName("Bonnet");
		personDTO.setAddress("Curitiba - PR");
		personDTO.setGender("Male");
		personDTO.setEnabled(true);
		personDTO.setPhotoUrl("https://pub.erudio.com.br/meus-cursos");
		personDTO.setProfileUrl("https://pub.erudio.com.br/meus-cursos");
	}

}
