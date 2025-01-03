package com.scoutress.KaimuxAdminStats.entity.productivity;

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
@Table(name = "productivity")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Productivity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "employee_id", nullable = false, unique = true)
  private Short employeeId;

  @Column(name = "value", nullable = false)
  private Double value;
}
