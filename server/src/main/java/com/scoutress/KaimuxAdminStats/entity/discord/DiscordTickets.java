package com.scoutress.KaimuxAdminStats.entity.discord;

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
@Table(name = "discord_tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscordTickets {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "employee_id", nullable = false)
  private Long employeeId;

  @Column(name = "ticket_count", nullable = false)
  private String ticketCount;

  @Column(name = "date", nullable = false)
  private LocalDate date;

}
