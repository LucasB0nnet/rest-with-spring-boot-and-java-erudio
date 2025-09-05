package br.com.erudio.controller.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.erudio.controller.docs.EmailControllerDocs;
import br.com.erudio.data.dto.V1.request.EmailRequestDTO;
import br.com.erudio.exception.EmailSendException;
import br.com.erudio.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;

@RestController
@RequestMapping("/api/email/v1")
public class EmailController implements EmailControllerDocs {

	@Autowired
	private EmailService  service;
	
	@PostMapping
	@Override
	public ResponseEntity<String> sendEmailWithAttachment(@RequestBody EmailRequestDTO emailRequest) throws AddressException, MessagingException, EmailSendException {
		service.sendSimpleEmail(emailRequest);
		return ResponseEntity.ok("e-mail send With success");
	}

	@PostMapping("/withAtachment")
	@Override
	public ResponseEntity<String> sendEmailWithAttachment(@RequestParam("emailRequest") String emailRequest, 
														  @RequestParam("attachment") MultipartFile attachment) throws AddressException, MessagingException {
		service.sendEmailWithAttachment(emailRequest, attachment);
		return ResponseEntity.ok("e-mail with attachment send successfully");
	}

}
