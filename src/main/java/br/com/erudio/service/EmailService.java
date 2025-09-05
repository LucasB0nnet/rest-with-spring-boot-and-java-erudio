package br.com.erudio.service;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.erudio.config.EmailConfig;
import br.com.erudio.data.dto.V1.request.EmailRequestDTO;
import br.com.erudio.exception.AttachmentPRocessingException;
import br.com.erudio.exception.InvalidJsonFormatException;
import br.com.erudio.mail.EmailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;

@Service
public class EmailService {

	@Autowired
	private EmailSender emailSender;

	@Autowired
	private EmailConfig config;

	public void sendSimpleEmail( EmailRequestDTO emailRequest) throws AddressException, MessagingException{
		emailSender.
		to(emailRequest.getTo()).
		withSubject(emailRequest.getSubject()).
		withMessage(emailRequest.getBody()).
		send(config);

	}

	public void sendEmailWithAttachment(String emailRequestJSon, MultipartFile attachment) throws AddressException, MessagingException{
		File tempFile = null;
		try {
			EmailRequestDTO emailRequest = new ObjectMapper().readValue(emailRequestJSon, EmailRequestDTO.class);
			tempFile = File.createTempFile("attachment", attachment.getOriginalFilename());
			attachment.transferTo(tempFile);
			
			emailSender.
			to(emailRequest.getTo()).
			withSubject(emailRequest.getSubject()).
			withMessage(emailRequest.getBody()).
			attach(tempFile.getAbsolutePath()).
			send(config);
			
		} catch (JsonMappingException e) {
			throw new InvalidJsonFormatException("Error parsing email request JSON",e);
		
		} catch (IOException e) {
			throw new AttachmentPRocessingException("Error processing the attachment",e);
		}
		finally {
			if(tempFile!=null && tempFile.exists()) {
				tempFile.delete(); 
			}
		}
	}
}
