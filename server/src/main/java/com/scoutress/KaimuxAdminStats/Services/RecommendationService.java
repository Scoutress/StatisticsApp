package com.scoutress.KaimuxAdminStats.Services;

import java.time.LocalDate;

import com.scoutress.KaimuxAdminStats.Entity.Employees.Employee;

public interface RecommendationService {

  void evaluateEmployees();

  boolean checkPlaytime(int employeeId, LocalDate startDate, LocalDate endDate);

  String evaluateHelper(Employee employee, double productivityValue, LocalDate currentDate);

  String evaluateSupport(Employee employee, double productivityValue, LocalDate currentDate);
}
