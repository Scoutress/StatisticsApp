package com.scoutress.KaimuxAdminStats.repositories.minecraftTickets;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.TotalMinecraftTickets;

public interface TotalMinecraftTicketsRepository extends JpaRepository<TotalMinecraftTickets, Long> {

  TotalMinecraftTickets findByEmployeeId(Short employeeId);
}
