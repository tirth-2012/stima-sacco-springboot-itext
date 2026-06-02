package com.rutusoft.flowable.exception;

import java.util.Date;

import com.rutusoft.flowable.dto.ErrorMessageDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = { ItemNotFoundException.class })
	public ResponseEntity<ErrorMessageDto> resourceNotFoundExceptionHandler(ItemNotFoundException ex, WebRequest request) {
		ErrorMessageDto message = new ErrorMessageDto(HttpStatus.NOT_FOUND.value(), new Date(), ex.getMessage(),
				request.getDescription(false));

		return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(value = { ValidationException.class })
	public ResponseEntity<ErrorMessageDto> resourceAlreadyExistExceptionHandler(ValidationException ex,
			WebRequest request) {
		ErrorMessageDto message = new ErrorMessageDto(HttpStatus.UNPROCESSABLE_ENTITY.value(), new Date(), ex.getMessage(),
				request.getDescription(false));

		return new ResponseEntity<>(message, HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(UserAuthenticationException.class)
	public ResponseEntity<ErrorMessageDto> unAuthenticatedUserExceptionHandler(UserAuthenticationException ex,
			WebRequest request) {
		ErrorMessageDto message = new ErrorMessageDto(HttpStatus.UNAUTHORIZED.value(), new Date(), ex.getMessage(),
				request.getDescription(false));

		return new ResponseEntity<>(message, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(AuthorizationServiceException.class)
	public ResponseEntity<ErrorMessageDto> unAuthorizedUserExceptionHandler(AuthorizationServiceException ex,
			WebRequest request) {
		ErrorMessageDto message = new ErrorMessageDto(HttpStatus.UNAUTHORIZED.value(), new Date(), ex.getMessage(),
				request.getDescription(false));

		return new ResponseEntity<>(message, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(ServerException.class)
	public ResponseEntity<ErrorMessageDto> serverExceptionHandler(ServerException ex, WebRequest request) {
		ErrorMessageDto message = new ErrorMessageDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), new Date(), ex.getMessage(),
				request.getDescription(false));

		return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(AwsS3Exception.class)
	public ResponseEntity<ErrorMessageDto> globalExceptionHandler(AwsS3Exception ex, WebRequest request) {
		ErrorMessageDto message = new ErrorMessageDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), new Date(), ex.getMessage(),
				request.getDescription(false));

		return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorMessageDto> globalExceptionHandler(Exception ex, WebRequest request) {
		ErrorMessageDto message = new ErrorMessageDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), new Date(), ex.getMessage(),
				request.getDescription(false));

		return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
	}



}