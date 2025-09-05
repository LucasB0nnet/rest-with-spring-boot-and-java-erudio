package br.com.erudio.controller.docs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import br.com.erudio.data.dto.V1.PersonDTOV1;
import br.com.erudio.data.dto.V1.request.EmailRequestDTO;
import br.com.erudio.exception.EmailSendException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;

public interface EmailControllerDocs {

	@Operation(summary = "Send an E-mail!",
			description = "Sends an e-mail by providing details, subject and body",
					tags = {"E-mail"},
					responses = {@ApiResponse(responseCode = "200",content = @Content),
					@ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = PersonDTOV1.class))),
					@ApiResponse(responseCode = "400", description = "No Bad Request", content = @Content),
					@ApiResponse(responseCode = "401", description = "No Authorized", content = @Content),
					@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
			})
	ResponseEntity<String> sendEmailWithAttachment(EmailRequestDTO dto) throws AddressException, MessagingException, EmailSendException;
	
	@Operation(summary = "Send an E-mail with attachment!",
			description = "Sends an e-mail attachment by providing details, subject and body",
					tags = {"E-mail"},
					responses = {@ApiResponse(responseCode = "200",content = @Content),
					@ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = PersonDTOV1.class))),
					@ApiResponse(responseCode = "400", description = "No Bad Request", content = @Content),
					@ApiResponse(responseCode = "401", description = "No Authorized", content = @Content),
					@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
			})
	ResponseEntity<String> sendEmailWithAttachment(String emailRequestJSon, MultipartFile multipartFile) throws AddressException, MessagingException;
}
