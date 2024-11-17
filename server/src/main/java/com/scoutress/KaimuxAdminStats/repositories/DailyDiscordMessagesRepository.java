package com.scoutress.KaimuxAdminStats.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.DailyDiscordMessages;

public interface DailyDiscordMessagesRepository extends JpaRepository<DailyDiscordMessages, Long> {
}
