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
@Table(name = "daily_discord_tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyDiscordTickets {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "employee_id", nullable = false)
  private Short employeeId;

  @Column(name = "date", nullable = false)
  private LocalDate date;

  @Column(name = "dc_ticket_count", nullable = false)
  private Integer dcTicketCount;

}
