package com.scoutress.KaimuxAdminStats.repositories.discordTickets;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.scoutress.KaimuxAdminStats.entity.discordTickets.DiscordTicketsReactions;

import jakarta.transaction.Transactional;

public interface DiscordTicketsReactionsRepository extends JpaRepository<DiscordTicketsReactions, Long> {

  @Modifying
  @Transactional
  @Query(value = "TRUNCATE TABLE discord_tickets_reactions", nativeQuery = true)
  void truncateTable();
}
