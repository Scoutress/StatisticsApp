package com.scoutress.KaimuxAdminStats.repositories.discordMessages;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.discordMessages.AverageDiscordMessagesCompared;

public interface AverageDiscordMessagesComparedRepository extends JpaRepository<AverageDiscordMessagesCompared, Long> {

  AverageDiscordMessagesCompared findByEmployeeId(Short employeeId);
}
