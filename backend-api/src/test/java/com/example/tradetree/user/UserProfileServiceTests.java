package com.example.tradetree.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

class UserProfileServiceTests {

	private UserProfileRepository repository;
	private PasswordEncoder passwordEncoder;
	private UserProfileService service;

	@BeforeEach
	void setUp() {
		repository = org.mockito.Mockito.mock(UserProfileRepository.class);
		passwordEncoder = Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
		service = new UserProfileService(repository, passwordEncoder);
	}

	@Test
	void createNormalizesAccountFieldsAndStoresHashedPassword() {
		when(repository.existsByUsername("alice")).thenReturn(false);
		when(repository.existsByEmail("alice@example.com")).thenReturn(false);
		when(repository.saveAndFlush(any(UserProfile.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		UserProfile profile = service.create(" Alice ", " ALICE@example.com ", "password123");

		assertEquals("alice", profile.getUsername());
		assertEquals("alice@example.com", profile.getEmail());
		assertNotEquals("password123", profile.getPasswordHash());
		assertTrue(passwordEncoder.matches("password123", profile.getPasswordHash()));
		verify(repository).saveAndFlush(any(UserProfile.class));
	}

	@Test
	void authenticateMatchesUsernameWithoutCaseSensitivity() {
		UserProfile profile = new UserProfile("alice", "alice@example.com", passwordEncoder.encode("password123"));
		when(repository.findByUsername("alice")).thenReturn(Optional.of(profile));

		assertSame(profile, service.authenticate("Alice", "password123"));
	}

	@Test
	void authenticateRejectsIncorrectPassword() {
		UserProfile profile = new UserProfile("alice", "alice@example.com", passwordEncoder.encode("password123"));
		when(repository.findByUsername("alice")).thenReturn(Optional.of(profile));

		assertThrows(UserProfileAuthenticationException.class, () -> service.authenticate("alice", "wrong"));
	}
}
