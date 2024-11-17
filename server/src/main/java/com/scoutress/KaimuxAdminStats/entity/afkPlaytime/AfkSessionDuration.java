package com.scoutress.KaimuxAdminStats.entity.afkPlaytime;

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
@Table(name = "afk_session_duration")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AfkSessionDuration {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "aid", nullable = false)
  private Short aid;

  @Column(name = "single_afk_session_duration", nullable = false)
  private int singleAfkSessionDuration;

  @Column(name = "date", nullable = false)
  private LocalDate date;

  @Column(name = "server", nullable = false)
  private String server;

  public AfkSessionDuration(short aid, int singleAfkSessionDuration, LocalDate date, String server) {
    this.aid = aid;
    this.singleAfkSessionDuration = singleAfkSessionDuration;
    this.date = date;
    this.server = server;
  }
}
