package com.scoutress.KaimuxAdminStats.config;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.scoutress.KaimuxAdminStats.services.discordTickets.DiscordTicketsReactionsService;
import com.scoutress.KaimuxAdminStats.services.discordTickets.DiscordTicketsService;
import com.scoutress.KaimuxAdminStats.services.minecraftTickets.MinecraftTicketsAnswersService;
import com.scoutress.KaimuxAdminStats.servicesimpl.discordTickets.DiscordTicketsReactionsServiceImpl;

import jakarta.transaction.Transactional;

@Configuration
@EnableScheduling
public class ScheduledTasksConfig {

	@SuppressWarnings("unused")
	private final DiscordTicketsReactionsService discordTicketsReactionsService;
	private final DiscordTicketsService discordTicketsService;
	private final MinecraftTicketsAnswersService minecraftTicketsAnswersService;

	public ScheduledTasksConfig(
			DiscordTicketsReactionsServiceImpl discordTicketsReactionsService,
			DiscordTicketsService discordTicketsService,
			MinecraftTicketsAnswersService minecraftTicketsAnswersService) {
		this.discordTicketsReactionsService = discordTicketsReactionsService;
		this.discordTicketsService = discordTicketsService;
		this.minecraftTicketsAnswersService = minecraftTicketsAnswersService;
	}

	// @Scheduled(cron = "0 0 * * * *")
	@Scheduled(cron = "0 6 * * * *")
	@Transactional
	public void run() {
		System.out.println("Scheduled tasks started at: " + getCurrentTimestamp());
		System.out.println("");

		// measureExecutionTime(() -> {
		// try {
		// discordTicketsReactionsService.fetchAndSaveData();
		// } catch (JSONException e) {
		// }
		// }, "Discord API Call");

		measureExecutionTime(() -> {
			try {
				minecraftTicketsAnswersService.fetchAndSaveData();
			} catch (JSONException e) {
			}
		}, "Discord API Call");

		discordTicketsService.convertDiscordTicketsResponses();

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