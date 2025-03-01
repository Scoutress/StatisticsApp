package com.scoutress.KaimuxAdminStats.repositories.minecraftTickets;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.DailyMinecraftTickets;

public interface DailyMinecraftTicketsRepository extends JpaRepository<DailyMinecraftTickets, Long> {

  DailyMinecraftTickets findByEmployeeIdAndDateAndTicketCount(Short employeeId, LocalDate date, int ticketCount);
}
