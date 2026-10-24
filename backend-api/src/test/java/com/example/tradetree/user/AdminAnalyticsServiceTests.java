package com.example.tradetree.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdminAnalyticsServiceTests {

	private UserAccountCreationRepository accountCreationRepository;
	private UserSignInActivityRepository activityRepository;
	private AdminAnalyticsService service;

	@BeforeEach
	void setUp() {
		accountCreationRepository = org.mockito.Mockito.mock(UserAccountCreationRepository.class);
		activityRepository = org.mockito.Mockito.mock(UserSignInActivityRepository.class);
		service = new AdminAnalyticsService(accountCreationRepository, activityRepository);
		when(accountCreationRepository.countBetween(any(Instant.class), any(Instant.class))).thenReturn(4L);
		when(activityRepository.countDistinctUsersBetween(any(Instant.class), any(Instant.class)))
				.thenReturn(7L);
	}

	@Test
	void returnsCurrentUtcCalendarWeekMonthAndYearTotals() {
		LocalDate today = LocalDate.now(ZoneOffset.UTC);
		AdminAnalyticsService.AnalyticsResponse response = service.getAnalytics();

		assertEquals(today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)), response.week().startDate());
		assertEquals(today.withDayOfMonth(1), response.month().startDate());
		assertEquals(today.withDayOfYear(1), response.year().startDate());
		assertEquals(today, response.week().endDate());
		assertEquals(today, response.month().endDate());
		assertEquals(today, response.year().endDate());
		assertEquals(4, response.week().accountsCreated());
		assertEquals(7, response.year().activeUsers());
		verify(accountCreationRepository, times(3)).countBetween(any(Instant.class), any(Instant.class));
		verify(activityRepository, times(3))
				.countDistinctUsersBetween(any(Instant.class), any(Instant.class));
	}
}
