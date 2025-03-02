package com.scoutress.KaimuxAdminStats.repositories.minecraftTickets;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.MinecraftTicketsAnswers;

import jakarta.transaction.Transactional;

public interface MinecraftTicketsAnswersRepository extends JpaRepository<MinecraftTicketsAnswers, Long> {

  @Modifying
  @Transactional
  @Query(value = "TRUNCATE TABLE minecraft_tickets_answers", nativeQuery = true)
  void truncateTable();
}
