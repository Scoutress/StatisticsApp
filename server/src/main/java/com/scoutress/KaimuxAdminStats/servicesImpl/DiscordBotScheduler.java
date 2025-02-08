package com.scoutress.KaimuxAdminStats.servicesImpl;

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

  @Scheduled(fixedRate = 600000) // Run every 600 seconds
  public void scheduledProcessDiscordMessagesCount() {
    discordBotService.processDiscordMessagesCount(new String[] {});
  }
}
