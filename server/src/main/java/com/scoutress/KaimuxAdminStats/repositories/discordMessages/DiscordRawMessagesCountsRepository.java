package com.scoutress.KaimuxAdminStats.repositories.discordMessages;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.scoutress.KaimuxAdminStats.entity.discordMessages.DiscordRawMessagesCounts;

import jakarta.transaction.Transactional;

public interface DiscordRawMessagesCountsRepository extends JpaRepository<DiscordRawMessagesCounts, Long> {

  @Modifying
  @Transactional
  @Query(value = "TRUNCATE TABLE discord_raw_message_counts", nativeQuery = true)
  void truncateTable();
}
