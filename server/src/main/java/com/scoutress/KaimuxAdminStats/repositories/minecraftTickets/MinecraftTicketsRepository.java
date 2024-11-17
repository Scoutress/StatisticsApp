package com.scoutress.KaimuxAdminStats.repositories.minecraftTickets;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.DailyMinecraftTickets;

public interface MinecraftTicketsRepository extends JpaRepository<DailyMinecraftTickets, Long> {

}
