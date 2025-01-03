package com.scoutress.KaimuxAdminStats.entity.discordMessages;

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
@Table(name = "average_daily_discord_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AverageDailyDiscordMessages {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "employee_id", nullable = false)
  private Short employeeId;

  @Column(name = "value", nullable = false)
  private double value;
}
