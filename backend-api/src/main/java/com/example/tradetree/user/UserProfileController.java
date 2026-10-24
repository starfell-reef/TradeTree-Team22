package com.example.tradetree.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserProfileController {

	private final UserProfileService service;

	public UserProfileController(UserProfileService service) {
		this.service = service;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public UserProfileResponse create(@Valid @RequestBody CreateUserProfileRequest request) {
		UserProfile profile = service.create(
				request.username(), request.name(), request.email(), request.password());
		return new UserProfileResponse(profile.getId(), profile.getUsername(), profile.getEmail());
	}

	@PostMapping("/login")
	public UserProfileResponse login(@Valid @RequestBody LoginRequest request) {
		UserProfile profile = service.authenticate(request.username(), request.password());
		return new UserProfileResponse(profile.getId(), profile.getUsername(), profile.getEmail());
	}

	public record CreateUserProfileRequest(
			@NotBlank
					@Size(min = 3, max = 30)
					@Pattern(regexp = "^[A-Za-z0-9_]+$", message = "Username may use letters, numbers, and underscores.")
					String username,
			@NotBlank @Size(max = 100) String name,
			@NotBlank @Email @Size(max = 254) String email,
			@NotBlank @Size(min = 8, max = 128) String password) {
	}

	public record LoginRequest(
			@NotBlank @Size(max = 30) String username,
			@NotBlank @Size(max = 128) String password) {
	}

	public record UserProfileResponse(UUID id, String username, String email) {
	}
}
