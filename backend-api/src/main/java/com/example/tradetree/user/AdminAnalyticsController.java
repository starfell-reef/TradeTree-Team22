package com.example.tradetree.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/analytics")
public class AdminAnalyticsController {

	private final UserProfileService userProfileService;
	private final AdminAnalyticsService analyticsService;

	public AdminAnalyticsController(
			UserProfileService userProfileService, AdminAnalyticsService analyticsService) {
		this.userProfileService = userProfileService;
		this.analyticsService = analyticsService;
	}

	@PostMapping
	public AdminAnalyticsService.AnalyticsResponse getAnalytics(
			@Valid @RequestBody AdminAnalyticsRequest request) {
		userProfileService.authenticateAdmin(request.username(), request.password());
		return analyticsService.getAnalytics();
	}

	public record AdminAnalyticsRequest(
			@NotBlank @Size(max = 30) String username,
			@NotBlank @Size(max = 128) String password) {
	}
}
