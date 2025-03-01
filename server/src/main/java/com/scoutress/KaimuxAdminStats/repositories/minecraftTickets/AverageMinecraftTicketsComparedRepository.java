package com.scoutress.KaimuxAdminStats.repositories.minecraftTickets;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.AverageMinecraftTicketsCompared;

public interface AverageMinecraftTicketsComparedRepository
    extends JpaRepository<AverageMinecraftTicketsCompared, Long> {

  AverageMinecraftTicketsCompared findByEmployeeId(Short employeeId);
}
