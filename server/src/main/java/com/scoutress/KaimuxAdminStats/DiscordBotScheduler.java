package com.scoutress.KaimuxAdminStats;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.services.DiscordBotService;

// Temp. class
@Service
public class DiscordBotScheduler {

  private final DiscordBotService discordBotService;

  public DiscordBotScheduler(DiscordBotService discordBotService) {
    this.discordBotService = discordBotService;
  }

  @Scheduled(cron = "0 02 11 * * *")
  public void scheduledProcessDiscordMessagesCount() {
    discordBotService.collectMessagesCountsFromDiscord();
  }
}
