package com.scoutress.KaimuxAdminStats.entity.discordTickets;

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
@Table(name = "discord_tickets_raw_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscordTicketsRawData {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "discord_id", nullable = false)
  private Long discordId;

  @Column(name = "ticket_id", nullable = false)
  private String ticketId;

  @Column(name = "date_time", nullable = false)
  private LocalDateTime dateTime;

}
