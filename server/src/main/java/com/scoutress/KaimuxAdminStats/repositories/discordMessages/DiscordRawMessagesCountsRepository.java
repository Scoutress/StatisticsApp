package com.scoutress.KaimuxAdminStats.repositories.discordMessages;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.discordMessages.DiscordRawMessagesCounts;

public interface DiscordRawMessagesCountsRepository extends JpaRepository<DiscordRawMessagesCounts, Long> {
}
