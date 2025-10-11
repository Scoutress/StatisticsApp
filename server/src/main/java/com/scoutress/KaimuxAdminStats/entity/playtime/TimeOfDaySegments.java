package com.scoutress.KaimuxAdminStats.entity.playtime;

import java.time.LocalDate;

import com.scoutress.KaimuxAdminStats.servicesImpl.EmployeeDataServiceImpl.HasEmployeeId;

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
public class TimeOfDaySegments implements HasEmployeeId {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "employee_id", nullable = false)
  private short employeeId;

  @Column(name = "server", nullable = false)
  private String server;

  @Column(name = "date", nullable = false)
  private LocalDate date;

  @Column(name = "time_segment", nullable = false)
  private int timeSegment;

  public TimeOfDaySegments(short employeeId, String server, LocalDate date, int timeSegment) {
    this.employeeId = employeeId;
    this.server = server;
    this.date = date;
    this.timeSegment = timeSegment;
  }

  @Override
  public Short getEmployeeId() {
    return employeeId;
  }
}
