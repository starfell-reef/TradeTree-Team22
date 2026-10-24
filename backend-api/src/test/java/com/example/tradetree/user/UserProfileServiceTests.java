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
	private UserAccountCreationRepository accountCreationRepository;
	private UserSignInActivityRepository activityRepository;
	private PasswordEncoder passwordEncoder;
	private UserProfileService service;

	@BeforeEach
	void setUp() {
		repository = org.mockito.Mockito.mock(UserProfileRepository.class);
		accountCreationRepository = org.mockito.Mockito.mock(UserAccountCreationRepository.class);
		activityRepository = org.mockito.Mockito.mock(UserSignInActivityRepository.class);
		passwordEncoder = Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
		service = new UserProfileService(repository, accountCreationRepository, activityRepository, passwordEncoder);
	}

	@Test
	void createNormalizesAccountFieldsAndStoresHashedPassword() {
		when(repository.existsByUsername("alice")).thenReturn(false);
		when(repository.existsByEmail("alice@example.com")).thenReturn(false);
		when(repository.saveAndFlush(any(UserProfile.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		UserProfile profile = service.create(" Alice ", "Alice Example", " ALICE@example.com ", "password123");

		assertEquals("alice", profile.getUsername());
		assertEquals("Alice Example", profile.getName());
		assertEquals("alice@example.com", profile.getEmail());
		assertNotEquals("password123", profile.getPasswordHash());
		assertTrue(passwordEncoder.matches("password123", profile.getPasswordHash()));
		verify(repository).saveAndFlush(any(UserProfile.class));
		verify(accountCreationRepository).saveAndFlush(any(UserAccountCreation.class));
	}

	@Test
	void authenticateMatchesUsernameWithoutCaseSensitivity() {
		UserProfile profile = new UserProfile(
				"alice", "Alice Example", "alice@example.com", passwordEncoder.encode("password123"));
		when(repository.findByUsername("alice")).thenReturn(Optional.of(profile));

		assertSame(profile, service.authenticate("Alice", "password123"));
		verify(activityRepository).save(any(UserSignInActivity.class));
	}

	@Test
	void authenticateRejectsIncorrectPassword() {
		UserProfile profile = new UserProfile(
				"alice", "Alice Example", "alice@example.com", passwordEncoder.encode("password123"));
		when(repository.findByUsername("alice")).thenReturn(Optional.of(profile));

		assertThrows(UserProfileAuthenticationException.class, () -> service.authenticate("alice", "wrong"));
	}

	@Test
	void authenticateRejectsUnknownUser() {
		when(repository.findByUsername("missing")).thenReturn(Optional.empty());

		assertThrows(UserProfileAuthenticationException.class, () -> service.authenticate("missing", "password123"));
	}

	@Test
	void authenticateAdminRejectsRegularAccounts() {
		UserProfile profile = new UserProfile(
				"alice", "Alice Example", "alice@example.com", passwordEncoder.encode("password123"));
		when(repository.findByUsername("alice")).thenReturn(Optional.of(profile));

		assertThrows(
				UserProfileAuthenticationException.class,
				() -> service.authenticateAdmin("alice", "password123"));
	}
}
