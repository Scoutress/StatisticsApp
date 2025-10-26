package com.scoutress.KaimuxAdminStats.repositories.minecraftTickets;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.AverageMinecraftTicketsPerPlaytime;

public interface AverageMinecraftTicketsPerPlaytimeRepository
    extends JpaRepository<AverageMinecraftTicketsPerPlaytime, Long> {

  AverageMinecraftTicketsPerPlaytime findByEmployeeId(Short employeeId);
}
