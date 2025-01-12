package com.scoutress.KaimuxAdminStats.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scoutress.KaimuxAdminStats.entity.RecommendationUser;
import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.TotalMinecraftTickets;
import com.scoutress.KaimuxAdminStats.repositories.RecommendationUserRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.TotalMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.services.FinalStatsService;

@RestController
@RequestMapping("/user")
public class UserStatsController {

  private final FinalStatsService finalStatsService;
  private final RecommendationUserRepository recommendationUserRepository;
  private final TotalMinecraftTicketsRepository totalMinecraftTicketsRepository;
  private final EmployeeRepository employeeRepository;

  public UserStatsController(
      FinalStatsService finalStatsService,
      RecommendationUserRepository recommendationUserRepository,
      TotalMinecraftTicketsRepository totalMinecraftTicketsRepository,
      EmployeeRepository employeeRepository) {
    this.finalStatsService = finalStatsService;
    this.recommendationUserRepository = recommendationUserRepository;
    this.totalMinecraftTicketsRepository = totalMinecraftTicketsRepository;
    this.employeeRepository = employeeRepository;
  }

  @GetMapping("/stats/{employeeId}")
  public ResponseEntity<Double> getProductivity(@PathVariable Short employeeId) {
    Double productivity = finalStatsService.getProductivity(employeeId);
    return ResponseEntity.ok(productivity);
  }

  @GetMapping("/ranking/{employeeId}")
  public ResponseEntity<Map<String, Object>> getEmployeeRanking(@PathVariable Short employeeId) {
    Map<String, Object> rankingData = finalStatsService.getEmployeeRanking(employeeId);
    return ResponseEntity.ok(rankingData);
  }

  @GetMapping("/recommendation/{employeeId}")
  public ResponseEntity<Map<String, Object>> getEmployeeRecommendation(@PathVariable Short employeeId) {
    RecommendationUser recommendationData = recommendationUserRepository.findByEmployeeId(employeeId);
    Map<String, Object> response = new HashMap<>();

    response.put("text", recommendationData.getText());
    return ResponseEntity.ok(response);
  }

  @GetMapping("/total-tickets")
  public List<Map<String, Object>> getAllEmployeesWithTickets() {
    List<Employee> employees = employeeRepository.findAll();
    List<TotalMinecraftTickets> allTickets = totalMinecraftTicketsRepository.findAll();

    return employees.stream()
        .filter(employee -> !employee.getUsername().equals("ItsVaidas") &&
            !employee.getUsername().equals("Scoutress"))
        .map(employee -> {
          Map<String, Object> employeeData = new HashMap<>();
          employeeData.put("id", employee.getId());
          employeeData.put("username", employee.getUsername());
          int totalTickets = allTickets.stream()
              .filter(ticket -> ticket.getEmployeeId().equals(employee.getId()))
              .mapToInt(TotalMinecraftTickets::getTicketCount)
              .sum();
          employeeData.put("totalTickets", totalTickets);
          return employeeData;
        })
        .sorted((e1, e2) -> Integer.compare((int) e2.get("totalTickets"), (int) e1.get("totalTickets")))
        .collect(Collectors.toList());
  }
}
