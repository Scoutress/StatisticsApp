package com.scoutress.KaimuxAdminStats.entity.minecraftTickets;

import java.time.LocalDate;

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
@Table(name = "daily_minecraft_tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyMinecraftTickets {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "aid", nullable = false)
  private Short aid;

  @Column(name = "ticket_count", nullable = false)
  private int ticketCount;

  @Column(name = "date", nullable = false)
  private LocalDate date;

}
