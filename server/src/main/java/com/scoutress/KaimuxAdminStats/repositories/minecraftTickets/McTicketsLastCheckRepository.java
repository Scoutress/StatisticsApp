package com.scoutress.KaimuxAdminStats.repositories.minecraftTickets;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.McTicketsLastCheck;

public interface McTicketsLastCheckRepository extends JpaRepository<McTicketsLastCheck, Long> {

}
