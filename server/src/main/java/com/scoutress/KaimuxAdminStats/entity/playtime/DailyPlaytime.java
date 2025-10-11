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
@Table(name = "daily_playtime")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyPlaytime implements HasEmployeeId {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "employee_id", nullable = false)
  private Short employeeId;

  @Column(name = "time", nullable = false)
  private Double timeInHours;

  @Column(name = "date", nullable = false)
  private LocalDate date;

  @Column(name = "server", nullable = false)
  private String server;

  @Override
  public Short getEmployeeId() {
    return employeeId;
  }
}
