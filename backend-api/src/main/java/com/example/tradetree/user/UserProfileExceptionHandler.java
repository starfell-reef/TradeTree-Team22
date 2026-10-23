package com.example.tradetree.user;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserProfileExceptionHandler {

	@ExceptionHandler(UserProfileAlreadyExistsException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public Map<String, String> handleAlreadyExists(UserProfileAlreadyExistsException exception) {
		return Map.of("error", exception.getMessage());
	}

	@ExceptionHandler(UserProfileAuthenticationException.class)
	public ResponseEntity<Map<String, String>> handleAuthenticationFailure(
			UserProfileAuthenticationException exception) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(Map.of("error", exception.getMessage()));
	}
}
