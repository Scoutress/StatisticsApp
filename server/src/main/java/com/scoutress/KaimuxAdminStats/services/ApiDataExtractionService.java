package com.scoutress.KaimuxAdminStats.services;

import java.time.LocalDate;

public interface ApiDataExtractionService {

  void extractMinecraftTicketsFromAPI(LocalDate newestDateFromDailyMcTickets);

  void extractDiscordTicketsFromAPI(LocalDate newestDateFromDcTicketsRawData);
}
