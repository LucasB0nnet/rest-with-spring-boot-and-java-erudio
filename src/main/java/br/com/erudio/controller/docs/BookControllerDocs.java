package br.com.erudio.controller.docs;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import br.com.erudio.data.dto.V1.BookDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

public interface BookControllerDocs {

	@Operation(summary = "Find a Book.",
			description = "Find a specific Book by your ID.",
			tags = {"Book"},
			responses = {
					@ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = BookDTO.class))),
					@ApiResponse(responseCode = "204", description = "No Content", content = @Content),
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
					@ApiResponse(responseCode = "400", description = "No Bad Request", content = @Content),
					@ApiResponse(responseCode = "401", description = "No Authorized", content = @Content),
					@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
			})
	BookDTO findById(Long id);

	@Operation(summary = "Find all Book.",
			description = "Find all Book.",
			tags = {"Book"},
			responses = {
					@ApiResponse(responseCode = "200",
							content = {
									@Content(
											mediaType = MediaType.APPLICATION_JSON_VALUE,
											array = @ArraySchema(schema = @Schema(implementation = BookDTO.class)))
							}),
					@ApiResponse(responseCode = "204", description = "No Content", content = @Content),
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
					@ApiResponse(responseCode = "400", description = "No Bad Request", content = @Content),
					@ApiResponse(responseCode = "401", description = "No Authorized", content = @Content),
					@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
			})
	ResponseEntity<PagedModel<EntityModel<BookDTO>>> findAll(Integer page, Integer size, String direction, String filter);

	@Operation(summary = "Create a Book.",
			description = "Create a new Book.",
			tags = {"Book"},
			responses = {
					@ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = BookDTO.class))),
					@ApiResponse(responseCode = "204", description = "No Content", content = @Content),
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
					@ApiResponse(responseCode = "400", description = "No Bad Request", content = @Content),
					@ApiResponse(responseCode = "401", description = "No Authorized", content = @Content),
					@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
			})
	BookDTO create(BookDTO bookDTO);

	@Operation(summary = "Update a Book.",
			description = "Update a Book.",
			tags = {"Book"},
			responses = {
					@ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = BookDTO.class))),
					@ApiResponse(responseCode = "204", description = "No Content", content = @Content),
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
					@ApiResponse(responseCode = "400", description = "No Bad Request", content = @Content),
					@ApiResponse(responseCode = "401", description = "No Authorized", content = @Content),
					@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
			})
	BookDTO update(BookDTO bookDTO);

	@Operation(summary = "Delete a Book.",
			description = "Delete a specific Book by your ID.",
			tags = {"Book"},
			responses = {
					@ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = BookDTO.class))),
					@ApiResponse(responseCode = "204", description = "No Content", content = @Content),
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
					@ApiResponse(responseCode = "400", description = "No Bad Request", content = @Content),
					@ApiResponse(responseCode = "401", description = "No Authorized", content = @Content),
					@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
			})
	ResponseEntity<?> delete(Long id);

}