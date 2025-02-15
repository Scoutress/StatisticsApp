package com.scoutress.KaimuxAdminStats.repositories.minecraftTickets;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.DailyMinecraftTicketsCompared;

public interface DailyMinecraftTicketsComparedRepository extends JpaRepository<DailyMinecraftTicketsCompared, Long> {

  DailyMinecraftTicketsCompared findByEmployeeIdAndDate(Short employeeId, LocalDate date);
}
