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
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "time_of_day_segments")
public class TimeOfDaySegments {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private short employeeId;

  @Column(nullable = false)
  private String server;

  @Column(nullable = false)
  private LocalDate date;

  @Column(nullable = false)
  private int timeSegment;

  public TimeOfDaySegments(short employeeId, String server, LocalDate date, int timeSegment) {
    this.employeeId = employeeId;
    this.server = server;
    this.date = date;
    this.timeSegment = timeSegment;
  }
}
