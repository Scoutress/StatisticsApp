package com.scoutress.KaimuxAdminStats.repositories.discordMessages;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.scoutress.KaimuxAdminStats.entity.discordMessages.DailyDiscordMessages;

public interface DailyDiscordMessagesRepository extends JpaRepository<DailyDiscordMessages, Long> {

  DailyDiscordMessages findByEmployeeIdAndDate(Short employeeId, LocalDate date);

  @Query("SELECT MAX(d.date) FROM DailyDiscordMessages d")
  Optional<LocalDate> findMaxDate();

}
