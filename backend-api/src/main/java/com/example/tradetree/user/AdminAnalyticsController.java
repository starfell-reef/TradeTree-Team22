package com.example.tradetree.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
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

	@GetMapping
	public AdminAnalyticsService.AnalyticsResponse getAnalytics(
			HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		Object adminId = session == null ? null : session.getAttribute(AdminSessionController.ADMIN_ID_ATTRIBUTE);
		if (!(adminId instanceof UUID id)) {
			throw new UserProfileAuthenticationException();
		}
		userProfileService.requireAdmin(id);
		return analyticsService.getAnalytics();
	}
}
