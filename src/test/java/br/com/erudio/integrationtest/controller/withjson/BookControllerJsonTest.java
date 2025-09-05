package br.com.erudio.integrationtest.controller.withjson;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
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
import br.com.erudio.integrationtests.testcontainers.dto.BookDTO;
import br.com.erudio.integrationtests.testcontainers.dto.TokenDTO;
import br.com.erudio.integrationtests.testcontainers.dto.wrapper.WrapperBookDTO;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerJsonTest extends AbstractIntegrationTest {

	private static RequestSpecification specification;
	private static ObjectMapper objectMapper;
	private static BookDTO bookDTO;
	private static TokenDTO tokenDTO;

	@BeforeAll
	static void setUpBeforeClass() {
		objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		bookDTO = new BookDTO();
		tokenDTO = new TokenDTO();
		mockBook();
		specification = new RequestSpecBuilder()// Inicia a construção de uma especificação de requisição
			    
			    // Adiciona um cabeçalho (header) com chave e valor definidos em TestConfig
			    .addHeader(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.ORIGIN_LOCAL) 
			    
			    // Define o caminho base para as requisições (prefixo do endpoint)
			    .setBasePath("/api/book/v1") 
			    
			    // Define a porta do servidor para as requisições
			    .setPort(TestConfig.SERVER_PORT) 
			    
			    // Adiciona um filtro para logar todos os detalhes da requisição
			    .addFilter(new RequestLoggingFilter(LogDetail.ALL)) 
			    
			    // Adiciona um filtro para logar todos os detalhes da resposta
			    .addFilter(new ResponseLoggingFilter(LogDetail.ALL)) 
			    
			    // Finaliza a construção e retorna o objeto pronto
			    .build();
	}
	
	@Test
    @Order(0)
    void signin() {
        AccountCredencialsDTO credentials =
                new AccountCredencialsDTO("leandro", "admin123");

        tokenDTO = RestAssured.given()
                .basePath("/auth/signin")
                .port(TestConfig.SERVER_PORT)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(credentials)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(TokenDTO.class);


        specification = new RequestSpecBuilder()
                .addHeader(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.ORIGIN_ERUDIO)
                .addHeader(TestConfig.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenDTO.getAccessToken())
                .setBasePath("/api/book/v1")
                .setPort(TestConfig.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        assertNotNull(tokenDTO.getAccessToken());
        assertNotNull(tokenDTO.getRefreshToken());
    }

	
	@Test
	@Order(1)
	void testFindByIdTest() throws JsonMappingException, JsonProcessingException {
		
		var content =RestAssured
			    // Cria a requisição usando a especificação pré-definida (headers, baseURL, etc.)
			    .given(specification) 
			    
			    // Define que o corpo da requisição (se tivesse) seria no formato JSON
			    .contentType(MediaType.APPLICATION_JSON_VALUE) 
			    
			    // Define um parâmetro de caminho chamado "id" com o valor vindo de personDTO.getId()
			    .pathParam("id", 15) 
			    
			    
			    // Executa a requisição GET no endpoint usando o parâmetro de caminho
			    .when().get("{id}") 
			    
			    // Verifica se o status da resposta foi 200 (OK)
			    .then().statusCode(200) 
			    
			    // Extrai o corpo da resposta
			    .extract().body()
			    
			    // Converte o corpo da resposta para String
			    .asString();
		
		// Converte a string JSON recebida na variável "content" para um objeto da classe BookDTO
		BookDTO createdBook = objectMapper.readValue(content, BookDTO.class);
		
		
		assertNotNull(createdBook.getId());
		assertTrue(createdBook.getId()>0);
		
		assertEquals(15, createdBook.getId());
		assertEquals("Aguinaldo Aragon Fernandes e Vladimir Ferraz de Abreu", createdBook.getAuthor());
		assertEquals("Implantando a governança de TI", createdBook.getTitle());
		assertEquals(54.0, createdBook.getPrice());
		
	}
	
	@Test
	@Order(2)
	void findAllTest() throws JsonMappingException, JsonProcessingException {
		var content = RestAssured.given(specification)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.queryParam("page", 1)
				.queryParam("size", 5)
				.queryParam("description", "asc")
				.when().get("/all")
				.then().statusCode(200)
				.extract().body().asString();
		
		WrapperBookDTO bookDTO2 = objectMapper.readValue(content, WrapperBookDTO.class);
		List<BookDTO> books = bookDTO2.getEmbeddedDTO().getBooks();
		
		BookDTO bookOne = books.get(1);
		
		bookDTO = bookOne;
		
		assertNotNull(bookOne.getId());
		assertTrue(bookOne.getId()>0);
		
		assertEquals(66, bookOne.getId());
		assertEquals("Andrew Hunt e David Thomas", bookOne.getAuthor());
		assertEquals(105.75, bookOne.getPrice());
		assertEquals("The Pragmatic Programmer", bookOne.getTitle());
		
		BookDTO bookThree = books.get(2);

		bookDTO = bookThree;

		assertNotNull(bookThree.getId());
		assertTrue(bookThree.getId() > 0);

		assertEquals(71, bookThree.getId());
		assertEquals("Andrew Hunt e David Thomas", bookThree.getAuthor());
		assertEquals(94.82, bookThree.getPrice());
		assertEquals("The Pragmatic Programmer", bookThree.getTitle());
		
		
	}
	
	@Test
	@Order(3)
	void CreateBookTest() throws JsonMappingException, JsonProcessingException {
		mockBook();
		var content = RestAssured.given(specification)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(bookDTO)
				.when().post()
				.then().statusCode(200)
				.extract().body().asString();
		
		BookDTO dto = objectMapper.readValue(content, BookDTO.class);
		bookDTO = dto;
		
		assertNotNull(dto.getId());
		assertTrue(dto.getId()>0);
		assertEquals("Lucas Bonnet", dto.getAuthor());
		assertEquals(250.00, dto.getPrice());
		assertEquals("My Life", dto.getTitle());
	}
	
	@Test
	@Order(4)
	void updateBookTest() throws JsonMappingException, JsonProcessingException {
		bookDTO.setAuthor("Jacqueline Mello");
		
		var content = RestAssured.given(specification)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(bookDTO)
				.when().put()
				.then().statusCode(200)
				.extract().body().asString();
		
		BookDTO dto = objectMapper.readValue(content, BookDTO.class);
		bookDTO = dto;
		
		assertNotNull(dto.getId());
		assertTrue(dto.getId()>0);
		assertEquals("Jacqueline Mello", dto.getAuthor());
		assertEquals(250.00, dto.getPrice());
		assertEquals("My Life", dto.getTitle());
		
	}
	
	@Test
	@Order(5)
	void deleteBookTest() {
		RestAssured.given(specification).pathParam("id", bookDTO.getId())
		.when().delete("/{id}").then().statusCode(204);
	}

	private static void mockBook() {
		bookDTO.setAuthor("Lucas Bonnet");
		bookDTO.setLaunchDate(new Date());
		bookDTO.setPrice(250.00);
		bookDTO.setTitle("My Life");
	}
}
