package br.com.erudio.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

	@Bean
	OpenAPI custonOPenApi() {
		return new OpenAPI().info(new Info()
				.title("REST API's RESTfull from 0 whith Java, Spring Boot, KUbernetes and Docker").version("v1")
				.description("REST API's RESTfull from 0 whith Java, Spring Boot, KUbernetes and Docker"));
	}

	@Bean
	public GroupedOpenApi personOPenApi() {
		return 	GroupedOpenApi.builder()
				.group("People")
				.displayName("Management Person")
				.packagesToScan("br.com.erudio.controller.person")
				.build();
	}
	
	@Bean
	public GroupedOpenApi bookOPenApi() {
		return 	GroupedOpenApi.builder()
				.group("Books")
				.displayName("Management Book")
				.packagesToScan("br.com.erudio.controller.book")
				.build();
	}
	
	@Bean
	public GroupedOpenApi fileOPenApi() {
		return 	GroupedOpenApi.builder()
				.group("Files")
				.displayName("Management File")
				.packagesToScan("br.com.erudio.controller.file")
				.build();
	}
	
	@Bean
	public GroupedOpenApi emailOPenApi() {
		return 	GroupedOpenApi.builder()
				.group("Email")
				.displayName("Management Email")
				.packagesToScan("br.com.erudio.controller.email")
				.build();
	}
	
	@Bean
	public GroupedOpenApi jwtOPenApi() {
		return 	GroupedOpenApi.builder()
				.group("Authentication")
				.displayName("Authentication")
				.packagesToScan("br.com.erudio.controller.auth")
				.build();
	}
}

