package br.com.erudio.controller.docs;


import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import br.com.erudio.data.dto.V1.PersonDTOV1;
import br.com.erudio.file.export.mediatypes.MediaTypes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface PersonControllerDocs {
	
	@Operation(summary = "Exporting a Person Data as PDF.",
			description = "Exporte a specific Person Data as PDF.",
					tags = {"People"},
					responses = {
							@ApiResponse(responseCode = "200",
									content = @Content(mediaType = MediaTypes.APPLICATION_PDF_VALUE)),
					@ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = PersonDTOV1.class))),
					@ApiResponse(responseCode = "204", description = "No Content", content = @Content),
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
					@ApiResponse(responseCode = "400", description = "No Bad Request", content = @Content),
					@ApiResponse(responseCode = "401", description = "No Authorized", content = @Content),
					@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
			})
	ResponseEntity<Resource> export(Long id, HttpServletRequest request);

	@Operation(summary = "Find a Person.",
			description = "Find a specific Person by your ID.",
			tags = {"People"},
			responses = {
					@ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = PersonDTOV1.class))),
					@ApiResponse(responseCode = "204", description = "No Content", content = @Content),
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
					@ApiResponse(responseCode = "400", description = "No Bad Request", content = @Content),
					@ApiResponse(responseCode = "401", description = "No Authorized", content = @Content),
					@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
			})
	PersonDTOV1 findById(Long id);

	@Operation(summary = "Find all People.",
			description = "Find all People.",
			tags = {"People"},
			responses = {
					@ApiResponse(responseCode = "200",
							content = {
									@Content(
											mediaType = MediaType.APPLICATION_JSON_VALUE,
											array = @ArraySchema(schema = @Schema(implementation = PersonDTOV1.class)))
							}),
					@ApiResponse(responseCode = "204", description = "No Content", content = @Content),
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
					@ApiResponse(responseCode = "400", description = "No Bad Request", content = @Content),
					@ApiResponse(responseCode = "401", description = "No Authorized", content = @Content),
					@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
			})
	ResponseEntity<PagedModel<EntityModel<PersonDTOV1>>> findAll(Integer page, Integer size, String direction);
	
	@Operation(summary = "Export  People.",
			description = "Export a Page of People in XLSX and CSV format.",
			tags = {"People"},
			responses = {
					@ApiResponse(responseCode = "200",
							content = {
									@Content(mediaType = MediaTypes.APPLICATION_XLSX_VALUE),
									@Content(mediaType = MediaTypes.APPLICATION_CSV_VALUE)
							}),
					@ApiResponse(responseCode = "204", description = "No Content", content = @Content),
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
					@ApiResponse(responseCode = "400", description = "No Bad Request", content = @Content),
					@ApiResponse(responseCode = "401", description = "No Authorized", content = @Content),
					@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
			})
	ResponseEntity<Resource> exportPage(Integer page, Integer size, String direction, HttpServletRequest request);
	
	@Operation(summary = "Massive People Creation.",
			description = "Massive People Creation With upload of XLSX or CSV.",
			tags = {"People"},
			responses = {
					@ApiResponse(responseCode = "200",
							content = {
									@Content(schema = @Schema(implementation = PersonDTOV1.class))
							}),
					@ApiResponse(responseCode = "204", description = "No Content", content = @Content),
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
					@ApiResponse(responseCode = "400", description = "No Bad Request", content = @Content),
					@ApiResponse(responseCode = "401", description = "No Authorized", content = @Content),
					@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
			})
	List<PersonDTOV1> importerAll(MultipartFile multipartFile);

	@Operation(summary = "Find People by Name.",
			description = "Find People by Name.",
			tags = {"People"},
			responses = {
					@ApiResponse(responseCode = "200",
							content = {
									@Content(
											mediaType = MediaType.APPLICATION_JSON_VALUE,
											array = @ArraySchema(schema = @Schema(implementation = PersonDTOV1.class)))
							}),
					@ApiResponse(responseCode = "204", description = "No Content", content = @Content),
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
					@ApiResponse(responseCode = "400", description = "No Bad Request", content = @Content),
					@ApiResponse(responseCode = "401", description = "No Authorized", content = @Content),
					@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
			})
	ResponseEntity<PagedModel<EntityModel<PersonDTOV1>>> findPersonByName(String firstName, Integer page, Integer size, String direction);
	
	@Operation(summary = "Create a Person.",
			description = "Create a new Person.",
			tags = {"People"},
			responses = {
					@ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = PersonDTOV1.class))),
					@ApiResponse(responseCode = "204", description = "No Content", content = @Content),
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
					@ApiResponse(responseCode = "400", description = "No Bad Request", content = @Content),
					@ApiResponse(responseCode = "401", description = "No Authorized", content = @Content),
					@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
			})
	PersonDTOV1 create(PersonDTOV1 person);

	@Operation(summary = "Update a Person.",
			description = "Update a Person.",
			tags = {"People"},
			responses = {
					@ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = PersonDTOV1.class))),
					@ApiResponse(responseCode = "204", description = "No Content", content = @Content),
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
					@ApiResponse(responseCode = "400", description = "No Bad Request", content = @Content),
					@ApiResponse(responseCode = "401", description = "No Authorized", content = @Content),
					@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
			})
	PersonDTOV1 update(PersonDTOV1 person);

	@Operation(summary = "Disable a Person.", description = "Disable a specific Person by your ID.", tags = {
			"People" }, responses = {
					@ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = PersonDTOV1.class))),
					@ApiResponse(responseCode = "204", description = "No Content", content = @Content),
					@ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
					@ApiResponse(responseCode = "400", description = "No Bad Request", content = @Content),
					@ApiResponse(responseCode = "401", description = "No Authorized", content = @Content),
					@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content) })
	PersonDTOV1 disablePerson(Long id);
	
	@Operation(summary = "Delete a Person.", description = "Delete a specific Person by your ID.", tags = {
	"People" }, responses = {
			@ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = PersonDTOV1.class))),
			@ApiResponse(responseCode = "204", description = "No Content", content = @Content),
			@ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
			@ApiResponse(responseCode = "400", description = "No Bad Request", content = @Content),
			@ApiResponse(responseCode = "401", description = "No Authorized", content = @Content),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content) })
	ResponseEntity<?> delete(Long id);

}