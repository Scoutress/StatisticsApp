package com.scoutress.KaimuxAdminStats.repositories.discordMessages;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.discordMessages.DailyDiscordMessagesCompared;

public interface DailyDiscordMessagesComparedRepository extends JpaRepository<DailyDiscordMessagesCompared, Long> {

  DailyDiscordMessagesCompared findByEmployeeIdAndDate(Short employeeId, LocalDate date);
}
