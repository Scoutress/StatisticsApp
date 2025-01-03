package com.scoutress.KaimuxAdminStats.repositories.minecraftTickets;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.OverallMinecraftTicketsCompared;

public interface OverallMinecraftTicketsComparedRepository
    extends JpaRepository<OverallMinecraftTicketsCompared, Long> {

  OverallMinecraftTicketsCompared findByEmployeeId(Short employeeId);
}
