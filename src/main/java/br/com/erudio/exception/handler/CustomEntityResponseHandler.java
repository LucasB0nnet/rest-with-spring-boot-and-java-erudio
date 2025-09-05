package br.com.erudio.exception.handler;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import br.com.erudio.exception.BadRequestException;
import br.com.erudio.exception.EmailSendException;
import br.com.erudio.exception.ExceptionResponse;
import br.com.erudio.exception.FileNotFoundException;
import br.com.erudio.exception.FileStorageException;
import br.com.erudio.exception.InvalidEmailAddressException;
import br.com.erudio.exception.InvalidJwtAuthenticationException;
import br.com.erudio.exception.RequiredObjectIsNullException;
import br.com.erudio.exception.ResourceNotFoundException;

@RestController
@ControllerAdvice
public class CustomEntityResponseHandler extends ResponseEntityExceptionHandler{
	
	@ExceptionHandler(exception = Exception.class)
	public final ResponseEntity<ExceptionResponse> handleAllException(Exception ex, WebRequest request){
		ExceptionResponse response = new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(exception = ResourceNotFoundException.class)
	public final ResponseEntity<ExceptionResponse> handleNotFoundException(Exception ex, WebRequest request){
		ExceptionResponse response = new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(exception = BadRequestException.class)
	public final ResponseEntity<ExceptionResponse> handleBadRequestException(Exception ex, WebRequest request){
		ExceptionResponse response = new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(exception = RequiredObjectIsNullException.class)
	public final ResponseEntity<ExceptionResponse> handleRequiredObjectNotNullxception(Exception ex, WebRequest request){
		ExceptionResponse response = new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(exception = FileStorageException.class)
	public final ResponseEntity<ExceptionResponse> handleFileStorageException(Exception ex, WebRequest request){
		ExceptionResponse response = new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(response);
	}
	
	@ExceptionHandler(exception = FileNotFoundException.class)
	public final ResponseEntity<ExceptionResponse> handleFileNotFoundException(Exception ex, WebRequest request){
		ExceptionResponse response = new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(exception = InvalidEmailAddressException.class)
	public final ResponseEntity<ExceptionResponse> handleInvalidEmailAddresException(Exception ex, WebRequest request){
		ExceptionResponse response = new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(exception = EmailSendException.class)
	public final ResponseEntity<ExceptionResponse> handleEmailSendException(Exception ex, WebRequest request){
		ExceptionResponse response = new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(exception = InvalidJwtAuthenticationException.class)
	public final ResponseEntity<ExceptionResponse> handleInvalidJwtAuthenticationException(Exception ex, WebRequest request){
		ExceptionResponse response = new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
		return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
	}

}

	
