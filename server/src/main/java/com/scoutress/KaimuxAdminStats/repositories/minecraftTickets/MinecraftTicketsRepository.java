package com.scoutress.KaimuxAdminStats.repositories.minecraftTickets;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.MinecraftTickets;

public interface MinecraftTicketsRepository extends JpaRepository<MinecraftTickets, Long> {

}
