package com.example.tradetree.user;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import org.springframework.stereotype.Service;

@Service
public class AdminAnalyticsService {

	private final UserAccountCreationRepository accountCreationRepository;
	private final UserSignInActivityRepository activityRepository;

	public AdminAnalyticsService(
			UserAccountCreationRepository accountCreationRepository,
			UserSignInActivityRepository activityRepository) {
		this.accountCreationRepository = accountCreationRepository;
		this.activityRepository = activityRepository;
	}

	public AnalyticsResponse getAnalytics() {
		LocalDate today = LocalDate.now(ZoneOffset.UTC);
		Instant end = Instant.now();
		LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

		return new AnalyticsResponse(
				getPeriod(weekStart, today, end),
				getPeriod(today.withDayOfMonth(1), today, end),
				getPeriod(today.withDayOfYear(1), today, end));
	}

	private PeriodAnalytics getPeriod(LocalDate startDate, LocalDate today, Instant end) {
		Instant start = startDate.atStartOfDay(ZoneOffset.UTC).toInstant();
		return new PeriodAnalytics(
				accountCreationRepository.countBetween(start, end),
				activityRepository.countDistinctUsersBetween(start, end),
				startDate,
				today);
	}

	public record AnalyticsResponse(
			PeriodAnalytics week, PeriodAnalytics month, PeriodAnalytics year) {
	}

	public record PeriodAnalytics(long accountsCreated, long activeUsers, LocalDate startDate, LocalDate endDate) {
	}
}
