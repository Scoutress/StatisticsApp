package com.scoutress.KaimuxAdminStats.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

  private final EmployeeRepository employeeRepository;

  public EmployeeController(
      EmployeeRepository employeeRepository) {
    this.employeeRepository = employeeRepository;
  }

  @GetMapping("/all")
  public List<Employee> getAllEmployees() {
    return employeeRepository.findAll();
  }
}
