package com.scoutress.KaimuxAdminStats.Entity.playtime;

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
@Table(name = "session_duration")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NEW_SessionDuration {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "aid", nullable = false)
  private short aid;

  @Column(name = "single_session_duration", nullable = false)
  private int singleSessionDuration;

  @Column(name = "date", nullable = false)
  private LocalDate date;

  @Column(name = "server", nullable = false)
  private String server;

  public NEW_SessionDuration(short aid, int singleSessionDuration, LocalDate date, String server) {
    this.aid = aid;
    this.singleSessionDuration = singleSessionDuration;
    this.date = date;
    this.server = server;
  }
}
