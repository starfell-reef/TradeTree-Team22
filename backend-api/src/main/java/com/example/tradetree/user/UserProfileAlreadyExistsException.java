package com.example.tradetree.user;

public class UserProfileAlreadyExistsException extends RuntimeException {

	public UserProfileAlreadyExistsException(String message) {
		super(message);
	}

	public UserProfileAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}
}
