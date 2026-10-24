package com.example.tradetree.user;

import java.util.Locale;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileService {

	private final UserProfileRepository repository;
	private final UserAccountCreationRepository accountCreationRepository;
	private final UserSignInActivityRepository activityRepository;
	private final PasswordEncoder passwordEncoder;

	public UserProfileService(
			UserProfileRepository repository,
			UserAccountCreationRepository accountCreationRepository,
			UserSignInActivityRepository activityRepository,
			PasswordEncoder passwordEncoder) {
		this.repository = repository;
		this.accountCreationRepository = accountCreationRepository;
		this.activityRepository = activityRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public UserProfile create(String username, String name, String email, String password) {
		String normalizedUsername = username.trim().toLowerCase(Locale.ROOT);
		String normalizedName = name.trim();
		String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

		if (repository.existsByUsername(normalizedUsername)) {
			throw new UserProfileAlreadyExistsException("Username is already in use.");
		}
		if (repository.existsByEmail(normalizedEmail)) {
			throw new UserProfileAlreadyExistsException("Email is already in use.");
		}

		UserProfile profile;
		try {
			profile = repository.saveAndFlush(
					new UserProfile(
							normalizedUsername,
							normalizedName,
							normalizedEmail,
							passwordEncoder.encode(password)));
		} catch (DataIntegrityViolationException exception) {
			throw new UserProfileAlreadyExistsException("Username or email is already in use.", exception);
		}
		accountCreationRepository.saveAndFlush(new UserAccountCreation(profile));
		return profile;
	}

	@Transactional
	public UserProfile authenticate(String username, String password) {
		String normalizedUsername = username.trim().toLowerCase(Locale.ROOT);
		Optional<UserProfile> profile = repository.findByUsername(normalizedUsername);

		if (profile.isEmpty() || !passwordEncoder.matches(password, profile.get().getPasswordHash())) {
			throw new UserProfileAuthenticationException();
		}
		activityRepository.save(new UserSignInActivity(profile.get()));
		return profile.get();
	}

	public UserProfile authenticateAdmin(String username, String password) {
		String normalizedUsername = username.trim().toLowerCase(Locale.ROOT);
		Optional<UserProfile> profile = repository.findByUsername(normalizedUsername);

		if (profile.isEmpty()
				|| !profile.get().isAdmin()
				|| !passwordEncoder.matches(password, profile.get().getPasswordHash())) {
			throw new UserProfileAuthenticationException();
		}
		return profile.get();
	}

}
