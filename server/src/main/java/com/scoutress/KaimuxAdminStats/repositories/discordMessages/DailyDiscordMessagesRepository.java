package com.scoutress.KaimuxAdminStats.repositories.discordMessages;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.discordMessages.DailyDiscordMessages;

public interface DailyDiscordMessagesRepository extends JpaRepository<DailyDiscordMessages, Long> {
}