package com.scoutress.KaimuxAdminStats.services.discordTickets;

public interface DiscordTicketsService {

  void convertDiscordTicketsResponses();

  void removeDuplicateReactions();

  void removeDuplicateTicketsData();
}
