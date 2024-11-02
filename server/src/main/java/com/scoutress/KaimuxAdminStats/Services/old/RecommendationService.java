package com.scoutress.KaimuxAdminStats.Services.old;

import java.time.LocalDate;

import com.scoutress.KaimuxAdminStats.Entity.old.Employees.Employee;

public interface RecommendationService {

  void evaluateEmployees();

  boolean checkPlaytime(int employeeId, LocalDate startDate, LocalDate endDate);

  String evaluateHelper(Employee employee, double productivityValue, LocalDate currentDate);

  String evaluateSupport(Employee employee, double productivityValue, LocalDate currentDate);
}
