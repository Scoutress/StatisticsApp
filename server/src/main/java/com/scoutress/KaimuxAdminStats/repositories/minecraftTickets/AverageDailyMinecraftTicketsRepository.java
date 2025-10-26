package com.scoutress.KaimuxAdminStats.repositories.minecraftTickets;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.AverageDailyMinecraftTickets;

public interface AverageDailyMinecraftTicketsRepository extends JpaRepository<AverageDailyMinecraftTickets, Long> {

  AverageDailyMinecraftTickets findByEmployeeId(Short employeeId);
}
