package com.example.tradetree.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/session")
public class AdminSessionController {

	static final String ADMIN_ID_ATTRIBUTE = AdminSessionController.class.getName() + ".adminId";

	private final UserProfileService userProfileService;

	public AdminSessionController(UserProfileService userProfileService) {
		this.userProfileService = userProfileService;
	}

	@PostMapping
	public AdminSessionResponse createSession(
			@Valid @RequestBody AdminLoginRequest request,
			HttpServletRequest httpRequest) {
		UserProfile profile = userProfileService.authenticateAdmin(request.username(), request.password());
		HttpSession existingSession = httpRequest.getSession(false);
		if (existingSession != null) {
			existingSession.invalidate();
		}
		HttpSession session = httpRequest.getSession(true);
		session.setAttribute(ADMIN_ID_ATTRIBUTE, profile.getId());
		return new AdminSessionResponse(profile.getUsername());
	}

	@GetMapping
	public AdminSessionResponse getSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			throw new UserProfileAuthenticationException();
		}

		Object adminId = session.getAttribute(ADMIN_ID_ATTRIBUTE);
		if (!(adminId instanceof UUID id)) {
			session.invalidate();
			throw new UserProfileAuthenticationException();
		}

		UserProfile profile = userProfileService.requireAdmin(id);
		return new AdminSessionResponse(profile.getUsername());
	}

	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
	}

	public record AdminLoginRequest(
			@NotBlank @Size(max = 30) String username,
			@NotBlank @Size(max = 128) String password) {
	}

	public record AdminSessionResponse(String username) {
	}
}
