package com.scoutress.KaimuxAdminStats.repositories.discordMessages;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.discordMessages.DailyDiscordMessagesCompared;

public interface DailyDiscordMessagesComparedRepository extends JpaRepository<DailyDiscordMessagesCompared, Long> {
}
