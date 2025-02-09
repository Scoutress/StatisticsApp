package com.scoutress.KaimuxAdminStats.repositories.discordMessages;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.discordMessages.DailyDiscordMessages;

public interface DailyDiscordMessagesRepository extends JpaRepository<DailyDiscordMessages, Long> {

  DailyDiscordMessages findByEmployeeIdAndDate(Short employeeId, LocalDate date);
}
