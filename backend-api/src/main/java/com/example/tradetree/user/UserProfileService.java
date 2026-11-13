package com.example.tradetree.user;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileService {

	private static final Pattern ADMIN_USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]{3,30}$");

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

	public UserProfile requireAdmin(UUID id) {
		return repository.findById(id)
				.filter(UserProfile::isAdmin)
				.orElseThrow(UserProfileAuthenticationException::new);
	}

	@Transactional
	public boolean createBootstrapAdmin(String username, String password) {
		String normalizedUsername = username.trim().toLowerCase(Locale.ROOT);
		if (!ADMIN_USERNAME_PATTERN.matcher(normalizedUsername).matches()) {
			throw new IllegalArgumentException("Bootstrap admin username must be 3-30 letters, numbers, or underscores.");
		}
		if (password.length() < 12 || password.length() > 128) {
			throw new IllegalArgumentException("Bootstrap admin password must be between 12 and 128 characters.");
		}

		Optional<UserProfile> existingProfile = repository.findByUsername(normalizedUsername);
		if (existingProfile.isPresent()) {
			if (!existingProfile.get().isAdmin()) {
				throw new IllegalStateException(
						"Bootstrap admin username belongs to a non-admin account; choose a different username.");
			}
			return false;
		}

		String email = normalizedUsername + "@demo.invalid";
		if (repository.existsByEmail(email)) {
			throw new IllegalStateException(
					"Bootstrap admin email is already in use; choose a different admin username.");
		}

		UserProfile profile = repository.saveAndFlush(new UserProfile(
				normalizedUsername,
				"TradeTree Demo Admin",
				email,
				passwordEncoder.encode(password),
				true));
		accountCreationRepository.saveAndFlush(new UserAccountCreation(profile));
		return true;
	}

}
