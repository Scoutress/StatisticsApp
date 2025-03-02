package com.scoutress.KaimuxAdminStats.entity.minecraftTickets;

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
@Table(name = "avg_daily_minecraft_tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AverageDailyMinecraftTickets {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "employee_id", nullable = false)
  private Short employeeId;

  @Column(name = "tickets", nullable = false)
  private double tickets;
}
