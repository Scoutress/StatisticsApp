package com.scoutress.KaimuxAdminStats.Utils;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.scoutress.KaimuxAdminStats.Entity.Employee;
import com.scoutress.KaimuxAdminStats.Entity.Productivity;
import com.scoutress.KaimuxAdminStats.Repositories.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.Repositories.ProductivityRepository;

public class GetProductivityDummyData {

  @Autowired
  private EmployeeRepository employeeRepository;

  @Autowired
  private ProductivityRepository productivityRepository;

  public void createDummyProductivity() {
      List<Employee> employees = employeeRepository.findAll();

      if (employees.size() < 5) {
          return;
      }

      Productivity prod1 = new Productivity(employees.get(0), 1200.0, 100.0, 95.0, 80.0, 0.8, 0.5, 60.0, 0.75, "Excellent work");
      Productivity prod2 = new Productivity(employees.get(1), 1100.0, 90.0, 85.0, 70.0, 0.77, 0.55, 50.0, 0.65, "Great job");
      Productivity prod3 = new Productivity(employees.get(2), 1000.0, 80.0, 75.0, 60.0, 0.75, 0.6, 40.0, 0.55, "Good job");
      Productivity prod4 = new Productivity(employees.get(3), 900.0, 70.0, 65.0, 50.0, 0.72, 0.65, 30.0, 0.45, "Keep it up");
      Productivity prod5 = new Productivity(employees.get(4), 800.0, 60.0, 55.0, 40.0, 0.7, 0.7, 20.0, 0.35, "Needs improvement");

      List<Productivity> productivities = List.of(prod1, prod2, prod3, prod4, prod5);
      productivityRepository.saveAll(productivities);
  }
}
