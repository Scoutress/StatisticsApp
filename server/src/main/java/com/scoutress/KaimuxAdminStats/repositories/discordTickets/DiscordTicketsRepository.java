package com.scoutress.KaimuxAdminStats.repositories.discordTickets;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.discordTickets.DailyDiscordTickets;

public interface DiscordTicketsRepository extends JpaRepository<DailyDiscordTickets, Long> {

}
