package br.com.erudio;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import br.com.erudio.config.TestConfig;
import br.com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import io.restassured.RestAssured;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class SwaggerIntegrationTest extends AbstractIntegrationTest{

	@Test
	void shouldDisplaySwaggerUIPage() {
	    // Envia uma requisição GET para a página do Swagger UI
	    var content = RestAssured
	        .given()
	            .basePath("/swagger-ui/index.html") // Caminho onde o Swagger UI é servido
	            .port(TestConfig.SERVER_PORT)       // Porta definida para o servidor no teste
	        .when()
	            .get()                              // Executa a requisição GET
	        .then()
	            .statusCode(200)                    // Verifica se a resposta HTTP foi 200 (OK)
	            .extract()
	            .body()
	            .asString();                        // Extrai o conteúdo da resposta como String

	    // Verifica se o conteúdo retornado contém o texto "Swagger UI"
	    assertTrue(content.contains("Swagger UI"));
	}

}	
