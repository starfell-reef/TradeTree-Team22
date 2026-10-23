package com.example.tradetree.user;

public class UserProfileAuthenticationException extends RuntimeException {

	public UserProfileAuthenticationException() {
		super("Invalid username or password.");
	}
}
