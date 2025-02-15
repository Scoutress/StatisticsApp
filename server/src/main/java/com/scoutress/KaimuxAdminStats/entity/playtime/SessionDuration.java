package com.scoutress.KaimuxAdminStats.entity.playtime;

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
public class SessionDuration {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "employee_id", nullable = false)
  private Short employeeId;

  @Column(name = "single_session_duration", nullable = false)
  private int singleSessionDurationInSec;

  @Column(name = "date", nullable = false)
  private LocalDate date;

  @Column(name = "server", nullable = false)
  private String server;

  public SessionDuration(short employeeId, int singleSessionDuration, LocalDate date, String server) {
    this.employeeId = employeeId;
    this.singleSessionDurationInSec = singleSessionDuration;
    this.date = date;
    this.server = server;
  }
}
