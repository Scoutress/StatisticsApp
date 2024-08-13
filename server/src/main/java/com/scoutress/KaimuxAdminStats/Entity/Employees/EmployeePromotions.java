package com.scoutress.KaimuxAdminStats.Entity.Employees;

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
@Table(name = "employee_promotions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeePromotions {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "employee_id", nullable = false)
  private Integer employeeId;

  @Column(name = "to_support")
  private LocalDate toSupport;

  @Column(name = "to_chatmod")
  private LocalDate toChatmod;

  @Column(name = "to_overseer")
  private LocalDate toOverseer;

  @Column(name = "to_manager")
  private LocalDate toManager;

  public EmployeePromotions(Integer employeeId, LocalDate toSupport, LocalDate toChatmod, LocalDate toOverseer,
      LocalDate toManager) {
    this.employeeId = employeeId;
    this.toSupport = toSupport;
    this.toChatmod = toChatmod;
    this.toOverseer = toOverseer;
    this.toManager = toManager;
  }

  public EmployeePromotions(int id, int employeeId, LocalDate toSupport, LocalDate toChatmod, LocalDate toOverseer,
      LocalDate toManager) {
    this.id = (long) id;
    this.employeeId = employeeId;
    this.toSupport = toSupport;
    this.toChatmod = toChatmod;
    this.toOverseer = toOverseer;
    this.toManager = toManager;
  }
}
