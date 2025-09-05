package br.com.erudio.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InvalidJsonFormatException extends RuntimeException {

	public InvalidJsonFormatException(String message) {
		super(message);
	}
	
	public InvalidJsonFormatException(String message, Throwable cause) {
		super(message, cause);
	}
}
