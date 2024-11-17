package com.scoutress.KaimuxAdminStats.repositories.minecraftTickets;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.DailyMinecraftTicketsCompared;

public interface DailyMinecraftTicketsComparedRepository extends JpaRepository<DailyMinecraftTicketsCompared, Long> {

}
