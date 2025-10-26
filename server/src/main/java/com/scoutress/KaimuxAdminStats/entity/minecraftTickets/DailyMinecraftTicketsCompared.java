package com.scoutress.KaimuxAdminStats.entity.minecraftTickets;

import java.time.LocalDate;

import com.scoutress.KaimuxAdminStats.services.HasEmployeeId;

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
@Table(name = "daily_minecraft_tickets_comp")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyMinecraftTicketsCompared implements HasEmployeeId {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "employee_id", nullable = false)
  private Short employeeId;

  @Column(name = "value", nullable = false)
  private Double value;

  @Column(name = "date", nullable = false)
  private LocalDate date;

  @Override
  public Short getEmployeeId() {
    return employeeId;
  }
}
