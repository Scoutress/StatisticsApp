package com.scoutress.KaimuxAdminStats.repositories.discordTickets;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.discordTickets.DailyDiscordTicketsCompared;

public interface DailyDiscordTicketsComparedRepository extends JpaRepository<DailyDiscordTicketsCompared, Long> {

}
