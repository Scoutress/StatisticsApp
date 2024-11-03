package com.scoutress.KaimuxAdminStats.repositories.discord;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.discord.DiscordTicketsReactions;

public interface DiscordTicketsReactionsRepository extends JpaRepository<DiscordTicketsReactions, Long> {
}
