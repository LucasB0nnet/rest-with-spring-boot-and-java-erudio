package br.com.erudio.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class EmailSendException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public EmailSendException(String message, Throwable cause) {
		super(message, cause);
	}

}
