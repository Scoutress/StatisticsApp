package com.scoutress.KaimuxAdminStats.repositories.minecraftTickets;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.TotalOldMinecraftTickets;

public interface TotalOldMinecraftTicketsRepository extends JpaRepository<TotalOldMinecraftTickets, Long> {

  TotalOldMinecraftTickets findByEmployeeId(Short employeeId);
}
