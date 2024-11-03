package com.scoutress.KaimuxAdminStats.config;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.scoutress.KaimuxAdminStats.servicesimpl.discord.DiscordTicketsReactionsServiceImpl;

import jakarta.transaction.Transactional;

@Configuration
@EnableScheduling
public class ScheduledTasksConfig {

	private final DiscordTicketsReactionsServiceImpl service;

	public ScheduledTasksConfig(DiscordTicketsReactionsServiceImpl service) {
		this.service = service;
	}

	@Scheduled(cron = "0 * * * * *")
	@Transactional
	public void run() {
		System.out.println("Scheduled tasks started at: " + getCurrentTimestamp());
		System.out.println("");

		measureExecutionTime(() -> {
			String result;
			try {
				result = service.fetchDataFromApi();
				System.out.println("Discord API response: " + result);
			} catch (JSONException e) {
			}
		}, "Discord API Call");

		System.out.println("");
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