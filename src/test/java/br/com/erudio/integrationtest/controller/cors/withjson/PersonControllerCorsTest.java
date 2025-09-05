package br.com.erudio.integrationtest.controller.cors.withjson;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
import br.com.erudio.data.dto.V1.PersonDTOV1;
import br.com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.erudio.integrationtests.testcontainers.dto.AccountCredencialsDTO;
import br.com.erudio.integrationtests.testcontainers.dto.TokenDTO;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)//Especifica que a ordem será determinada pela anotação
class PersonControllerCorsTest extends AbstractIntegrationTest{

	private static RequestSpecification specification;
	private static ObjectMapper objectMapper;
	private static PersonDTOV1 personDTOV1;
	private static TokenDTO tokenDTO;
	
	@BeforeAll
	static void setUpBeforeClass(){
		objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		personDTOV1 = new PersonDTOV1();
		tokenDTO = new TokenDTO();
		mockPerson();
	}

	@Test
	@Order(1)
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
	@Order(4)
	void testFindById() throws JsonMappingException, JsonProcessingException {
		
		specification = new RequestSpecBuilder()//criação da requisição
				.addHeader(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.ORIGIN_LOCAL)//adiciona um Header do tipo Origin e sua respectiva url(https://localhost:8080) que é aceita!
				.addHeader(TestConfig.HEADER_PARAM_AUTHORIZATION, "Bearer "+tokenDTO.getAccessToken())
				.setBasePath("/api/person/v1")//seta a url base e o id mocado ou criado pela base de dados
				.setPort(TestConfig.SERVER_PORT)//adiciona a porta criada 8889 classe teste.config
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))//cria no log toda o request e informa para melhor entendimento
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))//cria no log todo o response e informa para melhor entendimento
			    .build();//controi a especificação passada
		
		var content = RestAssured.given(specification)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.pathParam("id", personDTOV1.getId())
				.when()
				.get("{id}")
				.then()
				.statusCode(200)
				.extract()
				.body().asString();
		
		PersonDTOV1 createdPerson = objectMapper.readValue(content, PersonDTOV1.class);
		personDTOV1 = createdPerson;
		
		assertNotNull(createdPerson.getId());
		assertNotNull(createdPerson.getFirstName());
		assertNotNull(createdPerson.getLastName());
		assertNotNull(createdPerson.getAddress());
		assertNotNull(createdPerson.getGender());
		assertTrue(createdPerson.getId()>0);
		
		assertEquals("Lucas",createdPerson.getFirstName());
		assertEquals("Bonnet",createdPerson.getLastName());
		assertEquals("Curitiba - PR",createdPerson.getAddress());
		assertEquals("Male",createdPerson.getGender());
		assertTrue(createdPerson.getEnabled());
	}
	
	@Test
	@Order(5)
	void testFindByIdWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
		specification = new RequestSpecBuilder()//criação da requisição
				.addHeader(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.ORIGIN_SEMERU)//adiciona um Header do tipo Origin e sua respectiva url(https://localhost:8080) que é aceita!
				.addHeader(TestConfig.HEADER_PARAM_AUTHORIZATION, "Bearer "+tokenDTO.getAccessToken())
				.setBasePath("/api/person/v1")//seta a url base
				.setPort(TestConfig.SERVER_PORT)//adiciona a porta criada 8889 classe teste.config
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))//cria no log toda o request e informa para melhor entendimento
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))//cria no log todo o response e informa para melhor entendimento
			    .build();//controi a especificação passada
		
		var content = RestAssured.given(specification) //especificações
				.contentType(MediaType.APPLICATION_JSON_VALUE)//qual é o tipo de dados que está sendo enviado ou esperado no caso é um application/json
				.pathParam("id", personDTOV1.getId())//resgata o id
				.when()//quando
				.get("{id}")//requisição get com o id
				.then()//então
				.statusCode(403)//tem que ter um retorno de 403
				.extract()
				.body().asString();//cria uma string como corpo da resposta
		
		assertEquals("Invalid CORS request",content);
	}

	@Test
	@Order(2)
	void testCreate() throws JsonMappingException, JsonProcessingException {
		
		// Criação da especificação da requisição (Request Specification)
		specification = new RequestSpecBuilder()
			
			// Adiciona um cabeçalho personalizado na requisição HTTP. 
			// "HEADER_PARAM_ORIGIN" pode ser algo como "Origin" e "ORIGIN_ERUDIO" um valor autorizado, por exemplo.
			.addHeader(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.ORIGIN_ERUDIO)
			
			.addHeader(TestConfig.HEADER_PARAM_AUTHORIZATION, "Bearer "+tokenDTO.getAccessToken())

			// Define o caminho base da API que será testada, neste caso "/api/person/v1"
			.setBasePath("/api/person/v1")

			// Define a porta do servidor onde a API está sendo executada
			.setPort(TestConfig.SERVER_PORT)

			// Adiciona um filtro para logar todos os detalhes da requisição (útil para depuração)
			.addFilter(new RequestLoggingFilter(LogDetail.ALL))

			// Adiciona um filtro para logar todos os detalhes da resposta (útil para depuração)
			.addFilter(new ResponseLoggingFilter(LogDetail.ALL))

			// Constrói a especificação com todas as configurações acima
			.build();
		
		// Envia uma requisição HTTP do tipo POST usando a especificação previamente definida
		var content = RestAssured
		    .given(specification) // Usa a especificação configurada (headers, porta, basePath etc.)
		    .contentType(MediaType.APPLICATION_JSON_VALUE) // Define o tipo de conteúdo como JSON (application/json)
		    .body(personDTOV1) // Define o corpo da requisição. "personDTOV1" é provavelmente um objeto que representa os dados de uma pessoa
		    .when().post() // Envia uma requisição HTTP POST para a URL base definida na specification
		    .then().statusCode(200) // Verifica se o status da resposta HTTP é 200 (OK)
		    .extract().body().asString(); // Extrai o corpo da resposta como uma string e armazena na variável "content"
		
		PersonDTOV1 createdPerson = objectMapper.readValue(content, PersonDTOV1.class);
		personDTOV1 = createdPerson;
		
		assertNotNull(createdPerson.getId());
		assertNotNull(createdPerson.getFirstName());
		assertNotNull(createdPerson.getLastName());
		assertNotNull(createdPerson.getAddress());
		assertNotNull(createdPerson.getGender());
		assertTrue(createdPerson.getId()>0);
		
		assertEquals("Lucas",createdPerson.getFirstName());
		assertEquals("Bonnet",createdPerson.getLastName());
		assertEquals("Curitiba - PR",createdPerson.getAddress());
		assertEquals("Male",createdPerson.getGender());
		assertTrue(createdPerson.getEnabled());
	}
	
	@Test
	@Order(3)
	void testCreateWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
		
		// Criação da especificação da requisição (Request Specification)
		specification = new RequestSpecBuilder()
			
			// Adiciona um cabeçalho personalizado na requisição HTTP. 
			// "HEADER_PARAM_ORIGIN" pode ser algo como "Origin" e "ORIGIN_ERUDIO" um valor autorizado, por exemplo.
			.addHeader(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.ORIGIN_SEMERU)
			
			.addHeader(TestConfig.HEADER_PARAM_AUTHORIZATION, "Bearer "+tokenDTO.getAccessToken())

			// Define o caminho base da API que será testada, neste caso "/api/person/v1"
			.setBasePath("/api/person/v1")

			// Define a porta do servidor onde a API está sendo executada
			.setPort(TestConfig.SERVER_PORT)

			// Adiciona um filtro para logar todos os detalhes da requisição (útil para depuração)
			.addFilter(new RequestLoggingFilter(LogDetail.ALL))

			// Adiciona um filtro para logar todos os detalhes da resposta (útil para depuração)
			.addFilter(new ResponseLoggingFilter(LogDetail.ALL))

			// Constrói a especificação com todas as configurações acima
			.build();
		
		// Envia uma requisição HTTP do tipo POST usando a especificação previamente definida
		var content = RestAssured
		    .given(specification) // Usa a especificação configurada (headers, porta, basePath etc.)
		    .contentType(MediaType.APPLICATION_JSON_VALUE) // Define o tipo de conteúdo como JSON (application/json)
		    .body(personDTOV1) // Define o corpo da requisição. "personDTOV1" é provavelmente um objeto que representa os dados de uma pessoa
		    .when().post() // Envia uma requisição HTTP POST para a URL base definida na specification
		    .then().statusCode(403) // Verifica se o status da resposta HTTP é 200 (OK)
		    .extract().body().asString(); // Extrai o corpo da resposta como uma string e armazena na variável "content"
		
		assertEquals("Invalid CORS request",content);
	}
	
	private static void mockPerson() {
		personDTOV1.setFirstName("Lucas");
		personDTOV1.setLastName("Bonnet");
		personDTOV1.setAddress("Curitiba - PR");
		personDTOV1.setGender("Male");
		personDTOV1.setEnabled(true);
	}

}
