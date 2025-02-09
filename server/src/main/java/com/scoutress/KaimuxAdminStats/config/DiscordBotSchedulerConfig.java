package com.scoutress.KaimuxAdminStats.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import com.scoutress.KaimuxAdminStats.services.DiscordBotService;

import jakarta.annotation.PostConstruct;

@Configuration
public class DiscordBotSchedulerConfig {

  private final DiscordBotService discordBotService;

  public DiscordBotSchedulerConfig(DiscordBotService discordBotService) {
    this.discordBotService = discordBotService;
  }

  @PostConstruct
  public void init() {
    scheduledProcessDiscordMessagesCount();
  }

  @Scheduled(cron = "0 1 0 * * *")
  public void scheduledProcessDiscordMessagesCount() {
    discordBotService.collectMessagesCountsFromDiscord();
  }
}
