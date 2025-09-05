package br.com.erudio.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InvalidEmailAddressException extends RuntimeException {

	public InvalidEmailAddressException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public InvalidEmailAddressException(String message) {
		super(message);
	}
}
