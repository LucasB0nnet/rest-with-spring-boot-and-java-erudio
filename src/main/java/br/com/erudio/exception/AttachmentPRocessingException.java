package br.com.erudio.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class AttachmentPRocessingException extends RuntimeException {

	public AttachmentPRocessingException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public AttachmentPRocessingException(String message) {
		super(message);
	}
}
