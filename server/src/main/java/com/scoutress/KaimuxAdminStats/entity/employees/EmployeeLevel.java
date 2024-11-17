package com.scoutress.KaimuxAdminStats.entity.employees;

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
@Table(name = "employee_level")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeLevel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Short id;

  @Column(name = "aid", nullable = false, unique = true)
  private Short aid;

  @Column(name = "level", nullable = true)
  private Short level;

  @Column(name = "date", nullable = true)
  private LocalDate date;
}
