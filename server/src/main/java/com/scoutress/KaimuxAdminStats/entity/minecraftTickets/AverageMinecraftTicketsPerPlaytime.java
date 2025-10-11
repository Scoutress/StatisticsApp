package com.scoutress.KaimuxAdminStats.entity.minecraftTickets;

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
@Table(name = "avg_minecraft_tickets_per_playtime")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AverageMinecraftTicketsPerPlaytime implements HasEmployeeId {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "employee_id", nullable = false)
  private Short employeeId;

  @Column(name = "value", nullable = false)
  private double value;

  @Override
  public Short getEmployeeId() {
    return employeeId;
  }
}
