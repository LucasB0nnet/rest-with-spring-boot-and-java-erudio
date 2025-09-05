package br.com.erudio.integrationtest.controller.withjson;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import br.com.erudio.config.TestConfig;
import br.com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.erudio.integrationtests.testcontainers.dto.AccountCredencialsDTO;
import br.com.erudio.integrationtests.testcontainers.dto.TokenDTO;
import io.restassured.RestAssured;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Especifica que a ordem será determinada pela anotação
class AuthControllerTest extends AbstractIntegrationTest {

	private static TokenDTO tokenDTO;

	@BeforeAll
	static void setUpBeforeClass() {
		tokenDTO = new TokenDTO();
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
	@Order(2)
	void testRefresh() {
		tokenDTO = RestAssured
				.given()
				.basePath("/auth/refresh")
				.port(TestConfig.SERVER_PORT)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.pathParam("username", tokenDTO.getUserName())
				.header(TestConfig.HEADER_PARAM_AUTHORIZATION, "Bearer "+tokenDTO.getRefreshToken())
				.when()
				.put("{username}")
				.then().statusCode(200)
				.extract()
				.body()
				.as(TokenDTO.class);
		
		assertNotNull(tokenDTO.getAccessToken());
		assertNotNull(tokenDTO.getRefreshToken());
	}


}
