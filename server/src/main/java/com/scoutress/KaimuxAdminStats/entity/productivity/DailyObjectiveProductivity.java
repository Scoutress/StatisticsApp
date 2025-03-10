package com.scoutress.KaimuxAdminStats.entity.productivity;

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
@Table(name = "daily_objective_productivity")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyObjectiveProductivity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "aid", nullable = false, unique = true)
  private Short aid;

  @Column(name = "value", nullable = false, unique = true)
  private Double value;

  @Column(name = "date", nullable = false)
  private LocalDate date;
}
