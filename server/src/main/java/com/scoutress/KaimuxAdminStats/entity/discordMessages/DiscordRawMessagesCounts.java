package com.scoutress.KaimuxAdminStats.entity.discordMessages;

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
@Table(name = "discord_raw_message_counts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscordRawMessagesCounts {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "dc_user_id", nullable = false)
  private String dcUserId;

  @Column(name = "message_date", nullable = false)
  private LocalDate messageDate;

  @Column(name = "message_count", nullable = false)
  private int messageCount;

}
