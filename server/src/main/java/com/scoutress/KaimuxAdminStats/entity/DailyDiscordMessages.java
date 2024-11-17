package com.scoutress.KaimuxAdminStats.entity;

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
@Table(name = "daily_discord_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyDiscordMessages {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "aid", nullable = false)
  private Short aid;

  @Column(name = "msg_count", nullable = false)
  private int msgCount;

  @Column(name = "date", nullable = false)
  private LocalDate date;

}
