package com.example.tradetree.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
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

	@Test
	void requireAdminRejectsRegularAccounts() {
		UUID profileId = UUID.randomUUID();
		UserProfile profile = new UserProfile(
				"alice", "Alice Example", "alice@example.com", passwordEncoder.encode("password123"));
		when(repository.findById(profileId)).thenReturn(Optional.of(profile));

		assertThrows(UserProfileAuthenticationException.class, () -> service.requireAdmin(profileId));
	}

	@Test
	void requireAdminRejectsUnknownAccounts() {
		UUID profileId = UUID.randomUUID();
		when(repository.findById(profileId)).thenReturn(Optional.empty());

		assertThrows(UserProfileAuthenticationException.class, () -> service.requireAdmin(profileId));
	}

	@Test
	void requireAdminAcceptsAdminAccounts() {
		UUID profileId = UUID.randomUUID();
		UserProfile profile = new UserProfile(
				"admin", "TradeTree Demo Admin", "admin@demo.invalid",
				passwordEncoder.encode("demo-password-123"), true);
		when(repository.findById(profileId)).thenReturn(Optional.of(profile));

		assertSame(profile, service.requireAdmin(profileId));
	}

	@Test
	void createBootstrapAdminStoresAnAdminWithHashedPassword() {
		when(repository.findByUsername("demo_admin")).thenReturn(Optional.empty());
		when(repository.existsByEmail("demo_admin@demo.invalid")).thenReturn(false);
		when(repository.saveAndFlush(any(UserProfile.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		assertTrue(service.createBootstrapAdmin(" Demo_Admin ", "secure-demo-password"));
		org.mockito.ArgumentCaptor<UserProfile> profileCaptor =
				org.mockito.ArgumentCaptor.forClass(UserProfile.class);
		verify(repository).saveAndFlush(profileCaptor.capture());
		UserProfile profile = profileCaptor.getValue();
		assertTrue(profile.isAdmin());
		assertEquals("demo_admin", profile.getUsername());
		assertNotEquals("secure-demo-password", profile.getPasswordHash());
		assertTrue(passwordEncoder.matches("secure-demo-password", profile.getPasswordHash()));
		verify(accountCreationRepository).saveAndFlush(any(UserAccountCreation.class));
	}

	@Test
	void createBootstrapAdminDoesNotChangeAnExistingAdmin() {
		UserProfile profile = new UserProfile(
				"demo_admin", "Demo Admin", "demo_admin@demo.invalid",
				passwordEncoder.encode("existing-password"), true);
		when(repository.findByUsername("demo_admin")).thenReturn(Optional.of(profile));

		assertFalse(service.createBootstrapAdmin("demo_admin", "new-demo-password"));
		verify(repository, org.mockito.Mockito.never()).saveAndFlush(any(UserProfile.class));
	}

	@Test
	void createBootstrapAdminDoesNotPromoteAnExistingRegularAccount() {
		UserProfile profile = new UserProfile(
				"demo_admin", "Demo User", "demo@example.com",
				passwordEncoder.encode("existing-password"));
		when(repository.findByUsername("demo_admin")).thenReturn(Optional.of(profile));

		org.junit.jupiter.api.Assertions.assertThrows(
				IllegalStateException.class,
				() -> service.createBootstrapAdmin("demo_admin", "secure-demo-password"));
	}

	@Test
	void createBootstrapAdminRequiresStrongPassword() {
		org.junit.jupiter.api.Assertions.assertThrows(
				IllegalArgumentException.class,
				() -> service.createBootstrapAdmin("demo_admin", "short"));
	}
}
