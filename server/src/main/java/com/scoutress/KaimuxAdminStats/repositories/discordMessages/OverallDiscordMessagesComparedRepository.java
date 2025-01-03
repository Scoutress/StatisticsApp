package com.scoutress.KaimuxAdminStats.repositories.discordMessages;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.discordMessages.OverallDiscordMessagesCompared;

public interface OverallDiscordMessagesComparedRepository extends JpaRepository<OverallDiscordMessagesCompared, Long> {

  OverallDiscordMessagesCompared findByEmployeeId(Short employeeId);
}
