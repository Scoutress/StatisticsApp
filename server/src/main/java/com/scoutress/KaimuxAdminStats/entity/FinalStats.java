package com.scoutress.KaimuxAdminStats.entity;

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
@Table(name = "final_stats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinalStats {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "level", nullable = false)
  private String level;

  @Column(name = "username", nullable = false)
  private String username;

  @Column(name = "annual_playtime", nullable = false)
  private double annualPlaytime;

  @Column(name = "minecraft_tickets", nullable = false)
  private double minecraftTickets;

  @Column(name = "minecraft_tickets_compared", nullable = false)
  private double minecraftTicketsCompared;

  @Column(name = "playtime", nullable = false)
  private double playtime;

  @Column(name = "productivity", nullable = false)
  private double productivity;

  @Column(name = "recommendation", nullable = false)
  private String recommendation;
}
