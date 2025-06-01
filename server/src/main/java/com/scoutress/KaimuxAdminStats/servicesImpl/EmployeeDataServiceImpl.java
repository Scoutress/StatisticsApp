package com.scoutress.KaimuxAdminStats.servicesImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeCodesRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.services.EmployeeDataService;

@Service
public class EmployeeDataServiceImpl implements EmployeeDataService {

  private EmployeeRepository employeeRepository;
  private EmployeeCodesRepository employeeCodesRepository;
  private final JdbcTemplate jdbcTemplate;

  public EmployeeDataServiceImpl(
      EmployeeRepository employeeRepository,
      EmployeeCodesRepository employeeCodesRepository,
      JdbcTemplate jdbcTemplate) {
    this.employeeRepository = employeeRepository;
    this.employeeCodesRepository = employeeCodesRepository;
    this.jdbcTemplate = jdbcTemplate;
  }

  private static final Map<String, Integer> realEmployeeData = new HashMap<>();

  static {
    realEmployeeData.put("ItsVaidas", 1);
    realEmployeeData.put("Scoutress", 2);
    realEmployeeData.put("Mboti212", 3);
    realEmployeeData.put("Furija", 4);
    realEmployeeData.put("Ernestasltu12", 5);
    realEmployeeData.put("D0fka", 6);
    realEmployeeData.put("MelitaLove", 7);
    realEmployeeData.put("Libete", 8);
    realEmployeeData.put("Ariena", 9);
    realEmployeeData.put("Beche_", 11);
    realEmployeeData.put("everly", 12);
    realEmployeeData.put("RichPica", 13);
    realEmployeeData.put("Shizo", 14);
    realEmployeeData.put("BobsBuilder", 15);
    realEmployeeData.put("plrxq", 16);
    realEmployeeData.put("3MAHH", 17);
    realEmployeeData.put("Honske", 18);
  }

  private static final Map<String, String> serverColumnMap = Map.of(
      "Survival", "survival_id",
      "Skyblock", "skyblock_id",
      "Creative", "creative_id",
      "Boxpvp", "boxpvp_id",
      "Prison", "prison_id",
      "Events", "events_id",
      "Lobby", "lobby_id");

  @Override
  public void updateEmployeeCodes() {
    for (String server : serverColumnMap.keySet()) {
      for (Map.Entry<String, Integer> entry : realEmployeeData.entrySet()) {
        String username = entry.getKey();
        Integer employeeId = entry.getValue();

        try {
          String selectQuery = "SELECT user_id FROM raw_user_data_" + server.toLowerCase() + " WHERE username = ?";
          Integer userId = jdbcTemplate.queryForObject(selectQuery, Integer.class, username);

          if (userId != null) {
            insertIntoEmployeeCodes(employeeId, userId, server);
          }
        } catch (DataAccessException e) {
        }
      }
    }
  }

  private void insertIntoEmployeeCodes(Integer employeeId, Integer userId, String server) {
    String column = serverColumnMap.get(server);

    if (column != null) {
      try {
        String updateQuery = "UPDATE employee_codes SET " + column + " = ? WHERE employee_id = ?";
        jdbcTemplate.update(updateQuery, userId, employeeId);
      } catch (DataAccessException e) {
      }
    }
  }

  @Override
  public List<Short> checkNessesaryEmployeeData() {
    List<Short> employeeIds = getAllEmployeeIds();
    List<Short> employeeIdsWithoutData = new ArrayList<>();
    List<EmployeeCodes> employeeCodesData = getAllEmployeeCodesData();

    for (Short employeeId : employeeIds) {
      boolean employeeHasData = hasEmployeeData(employeeId, employeeCodesData);

      if (!employeeHasData) {
        employeeIdsWithoutData.add(employeeId);
      }
    }

    return employeeIdsWithoutData;
  }

  private List<Short> getAllEmployeeIds() {
    List<Employee> employeesData = getAllEmployeesData();
    return employeesData
        .stream()
        .map(Employee::getId)
        .distinct()
        .sorted()
        .toList();
  }

  private List<Employee> getAllEmployeesData() {
    return employeeRepository
        .findAll();
  }

  private List<EmployeeCodes> getAllEmployeeCodesData() {
    return employeeCodesRepository
        .findAll();
  }

  private boolean hasEmployeeData(Short employeeId, List<EmployeeCodes> employeeCodesData) {
    return employeeCodesData
        .stream()
        .filter(employee -> employee.getEmployeeId().equals(employeeId))
        .anyMatch(employee -> employee.getKmxWebApi() != null && employee.getDiscordUserId() != null);
  }
}
