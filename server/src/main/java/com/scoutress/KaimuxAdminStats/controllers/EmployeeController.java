package com.scoutress.KaimuxAdminStats.controllers;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    List<String> levelOrder = Arrays.asList(
        "Owner", "Operator", "Manager", "Organizer",
        "Overseer", "ChatMod", "Support", "Helper");

    return employeeRepository
        .findAll()
        .stream()
        .sorted(Comparator.comparingInt(employee -> levelOrder.indexOf(employee.getLevel())))
        .collect(Collectors.toList());
  }

  @PostMapping("/add")
  public ResponseEntity<Employee> addEmployee(@RequestBody Employee employee) {
    try {
      Employee savedEmployee = employeeRepository.save(employee);
      return ResponseEntity.ok(savedEmployee);
    } catch (Exception e) {
      return ResponseEntity.status(500).body(null);
    }
  }
}
