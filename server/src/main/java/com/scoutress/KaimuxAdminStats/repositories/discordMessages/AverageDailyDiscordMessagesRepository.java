package com.scoutress.KaimuxAdminStats.repositories.discordMessages;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.discordMessages.AverageDailyDiscordMessages;

public interface AverageDailyDiscordMessagesRepository extends JpaRepository<AverageDailyDiscordMessages, Long> {

  AverageDailyDiscordMessages findByEmployeeId(Short employeeId);
}
