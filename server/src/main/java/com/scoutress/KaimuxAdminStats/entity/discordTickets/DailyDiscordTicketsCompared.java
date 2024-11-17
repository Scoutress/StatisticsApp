package com.scoutress.KaimuxAdminStats.entity.discordTickets;

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
@Table(name = "daily_discord_tickets_compared")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyDiscordTicketsCompared {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "aid", nullable = false)
  private Short aid;

  @Column(name = "value", nullable = false)
  private Double value;

  @Column(name = "date", nullable = false)
  private LocalDate date;

}
