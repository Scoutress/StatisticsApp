package com.scoutress.KaimuxAdminStats.Config;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.scoutress.KaimuxAdminStats.Services.NEW_AnnualyPlaytimeService;
import com.scoutress.KaimuxAdminStats.Services.NEW_DailyPlaytimeService;
import com.scoutress.KaimuxAdminStats.Services.NEW_DataProcessingService;
import com.scoutress.KaimuxAdminStats.Services.NEW_DummyDataUploadingService;

import jakarta.transaction.Transactional;

@Configuration
@EnableScheduling
public class ScheduledTasksConfig {

	@SuppressWarnings("unused")
	private final NEW_DataProcessingService dataProcessingService;
	@SuppressWarnings("unused")
	private final NEW_DummyDataUploadingService dummyDataUploadingService;
	@SuppressWarnings("unused")
	private final NEW_DailyPlaytimeService dailyPlaytimeService;
	private final NEW_AnnualyPlaytimeService annualyPlaytimeService;

	public ScheduledTasksConfig(
			NEW_DataProcessingService dataProcessingService,
			NEW_DummyDataUploadingService dummyDataUploadingService,
			NEW_DailyPlaytimeService dailyPlaytimeService,
			NEW_AnnualyPlaytimeService annualyPlaytimeService) {
		this.dataProcessingService = dataProcessingService;
		this.dummyDataUploadingService = dummyDataUploadingService;
		this.dailyPlaytimeService = dailyPlaytimeService;
		this.annualyPlaytimeService = annualyPlaytimeService;
	}

	@Scheduled(cron = "0 * * * * *")
	@Scheduled(cron = "15 * * * * *")
	@Scheduled(cron = "30 * * * * *")
	@Scheduled(cron = "45 * * * * *")
	@Transactional
	public void run() {
		System.out.println("Scheduled tasks started at: " + getCurrentTimestamp());

		measureExecutionTime(() -> annualyPlaytimeService.handleAnnualPlaytime(), "<><><>");

		System.out.println("Scheduled tasks completed at: " + getCurrentTimestamp());
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	}

	private void measureExecutionTime(Runnable task, String taskName) {
		Instant start = Instant.now();
		System.out.println("");
		System.out.println("Starting " + taskName);
		task.run();
		Instant end = Instant.now();
		Duration duration = Duration.between(start, end);
		System.out.println(taskName + " >> " + duration.toMillis() + " ms");
	}

	private String getCurrentTimestamp() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}
}