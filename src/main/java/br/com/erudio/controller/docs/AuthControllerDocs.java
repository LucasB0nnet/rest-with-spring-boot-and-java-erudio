package br.com.erudio.controller.docs;

import org.springframework.http.ResponseEntity;

import br.com.erudio.data.dto.V1.security.AccountCredencialsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

public interface AuthControllerDocs {

	@Operation(summary = "Authenticates an user and returns a token",
			description = "Validates user credentials and generates an access token for authentication.",
			tags = {"Authentication"},
			responses = {
					@ApiResponse(responseCode = "200", description = "Success", content = @Content),
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
					@ApiResponse(responseCode = "400", description = "No Bad Request", content = @Content),
					@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
					@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
			})
	ResponseEntity<?> signIn(AccountCredencialsDTO credencialsDTO);

	@Operation(summary = "Refresh Token for authenticated user and returns a token",
			description = "Validates user credentials and generates an access token for authentication.",
			tags = {"Authentication"},
			responses = {
					@ApiResponse(responseCode = "200", description = "Success", content = @Content),
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
					@ApiResponse(responseCode = "400", description = "No Bad Request", content = @Content),
					@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
					@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
			})
	ResponseEntity<?> refresh(String username, String refreshToken);

	@Operation(summary = "Create a User.",
			description = "Registers a new user in the system with the provided credentials.",
			tags = {"User Management"},
			responses = {
					@ApiResponse(responseCode = "201", description = "Created", content = @Content),
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
					@ApiResponse(responseCode = "400", description = "No Bad Request", content = @Content),
					@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
					@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
			})
	ResponseEntity<AccountCredencialsDTO> createUser(AccountCredencialsDTO credencials);

}