package com.scoutress.KaimuxAdminStats.Entity.old.Employees;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeePromotionsPlus {

  private Integer employeeId;
  private String username;
  private String level;
  private LocalDate toSupport;
  private LocalDate toChatmod;
  private LocalDate toOverseer;
  private LocalDate toManager;

}
