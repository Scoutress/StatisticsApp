package com.scoutress.KaimuxAdminStats.repositories.discord;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.discord.DiscordTickets;

public interface DiscordTicketsRepository extends JpaRepository<DiscordTickets, Long> {

}
