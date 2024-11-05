package com.scoutress.KaimuxAdminStats.repositories.discordTickets;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.discordTickets.DiscordTickets;

public interface DiscordTicketsRepository extends JpaRepository<DiscordTickets, Long> {

}
