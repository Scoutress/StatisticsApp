package com.scoutress.KaimuxAdminStats.entity.minecraftTickets;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "minecraft_tickets_answers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MinecraftTicketsAnswers {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "minecraft_ticket_id", nullable = false)
  private Long minecraftTicketId;

  @Column(name = "date_time", nullable = false)
  private LocalDateTime dateTime;

}
