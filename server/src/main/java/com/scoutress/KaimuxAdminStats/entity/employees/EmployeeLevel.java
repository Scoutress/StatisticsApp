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
  private String aid;

  @Column(name = "became_helper", nullable = true)
  private LocalDate becameHelper;

  @Column(name = "promoted_to_support", nullable = true)
  private LocalDate promotedToSupport;

  @Column(name = "promoted_to_chatmod", nullable = true)
  private LocalDate promotedToChatMod;

  @Column(name = "promoted_to_overseer", nullable = true)
  private LocalDate promotedToOverseer;

  @Column(name = "promoted_to_manager", nullable = true)
  private LocalDate promotedToManager;

  @Column(name = "demoted_to_overseer", nullable = true)
  private LocalDate demotedToOverseer;

  @Column(name = "demoted_to_chatmod", nullable = true)
  private LocalDate demotedToChatMod;

  @Column(name = "demoted_to_support", nullable = true)
  private LocalDate demotedToSupport;

  @Column(name = "demoted_to_helper", nullable = true)
  private LocalDate demotedToHelper;
}
