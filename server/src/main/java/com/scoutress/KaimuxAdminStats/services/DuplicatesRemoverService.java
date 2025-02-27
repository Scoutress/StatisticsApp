package com.scoutress.KaimuxAdminStats.services;

public interface DuplicatesRemoverService {

  void removeDailyDiscordMessagesDuplicates();

  void removeDuplicatesFromDailyMcTickets();

  void removeDuplicatesFromAvgDailyMcTickets();

  void removeDuplicatesFromMcTicketsPerPlaytime();

  void removeDuplicatesFromTotalMcTickets();

  void removeDuplicatesFromComparedMcTickets();

}
