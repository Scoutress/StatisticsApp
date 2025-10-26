package com.scoutress.KaimuxAdminStats.repositories.discordTickets;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.discordTickets.DiscordTicketsRawData;

public interface DiscordTicketsRawDataRepository extends JpaRepository<DiscordTicketsRawData, Long> {

}
