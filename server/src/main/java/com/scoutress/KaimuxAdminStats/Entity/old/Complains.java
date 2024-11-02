package com.scoutress.KaimuxAdminStats.Entity.old;

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
@Table(name = "complains")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Complains {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "employee_id", nullable = false)
  private Integer employeeId;

  @Column(name = "date", nullable = false)
  private LocalDate date;

  @Column(name = "complains_count", nullable = false)
  private Double complainsCount;
}