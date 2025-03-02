package com.scoutress.KaimuxAdminStats.servicesImpl.playtime;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;
import com.scoutress.KaimuxAdminStats.entity.playtime.AveragePlaytimeOverall;
import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeCodesRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.AveragePlaytimeOverallRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.DailyPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.services.playtime.AveragePlaytimeOverallService;

@Service
public class AveragePlaytimeOverallServiceImpl implements AveragePlaytimeOverallService {

  private final AveragePlaytimeOverallRepository averagePlaytimeOverallRepository;
  private final DailyPlaytimeRepository dailyPlaytimeRepository;
  private final EmployeeCodesRepository employeeCodesRepository;
  private final EmployeeRepository employeeRepository;

  public AveragePlaytimeOverallServiceImpl(
      AveragePlaytimeOverallRepository averagePlaytimeOverallRepository,
      DailyPlaytimeRepository dailyPlaytimeRepository,
      EmployeeCodesRepository employeeCodesRepository,
      EmployeeRepository employeeRepository) {
    this.averagePlaytimeOverallRepository = averagePlaytimeOverallRepository;
    this.dailyPlaytimeRepository = dailyPlaytimeRepository;
    this.employeeCodesRepository = employeeCodesRepository;
    this.employeeRepository = employeeRepository;
  }

  @Override
  public void handleAveragePlaytime() {
    List<DailyPlaytime> allPlaytime = getDailyPlaytimeData();
    List<EmployeeCodes> allEmployeeCodes = getAllEmployeeCodes();
    List<Employee> allEmployees = getAllEmployees();
    List<AveragePlaytimeOverall> averagePlaytime = calculateAveragePlaytime(
        allPlaytime, allEmployeeCodes, allEmployees);

    saveAveragePlaytime(averagePlaytime);
  }

  private List<DailyPlaytime> getDailyPlaytimeData() {
    return dailyPlaytimeRepository.findAll();
  }

  private List<EmployeeCodes> getAllEmployeeCodes() {
    return employeeCodesRepository.findAll();
  }

  private List<Employee> getAllEmployees() {
    return employeeRepository.findAll();
  }

  private List<AveragePlaytimeOverall> calculateAveragePlaytime(
      List<DailyPlaytime> allPlaytimes,
      List<EmployeeCodes> allEmployeeCodes,
      List<Employee> allEmployees) {

    List<AveragePlaytimeOverall> handledAveragePlaytimeData = new ArrayList<>();
    LocalDate today = LocalDate.now();

    Map<Short, Employee> employeeMap = allEmployees
        .stream()
        .collect(Collectors.toMap(Employee::getId, emp -> emp));

    Set<Short> allEmployeeIds = allEmployeeCodes
        .stream()
        .map(EmployeeCodes::getEmployeeId)
        .collect(Collectors.toSet());

    Set<Short> uniqueEmployeeIdsInPlaytime = allPlaytimes
        .stream()
        .map(DailyPlaytime::getEmployeeId)
        .collect(Collectors.toSet());

    for (Short employeeId : uniqueEmployeeIdsInPlaytime) {
      if (allEmployeeIds.contains(employeeId)) {
        Employee employee = employeeMap.get(employeeId);

        if (employee != null) {
          LocalDate joinDate = employee.getJoinDate();

          double playtimesSum = allPlaytimes
              .stream()
              .filter(pt -> pt.getEmployeeId().equals(employeeId))
              .filter(pt -> !pt.getDate().isBefore(joinDate))
              .mapToDouble(DailyPlaytime::getTimeInHours)
              .sum();

          LocalDate oldestDateFromData = allPlaytimes
              .stream()
              .filter(date -> date.getEmployeeId().equals(employeeId))
              .map(DailyPlaytime::getDate)
              .min(LocalDate::compareTo)
              .orElse(LocalDate.of(1970, 1, 1));

          LocalDate processingDate = oldestDateFromData.isAfter(joinDate) ? oldestDateFromData : joinDate;

          long daysAfterJoin = ChronoUnit.DAYS.between(processingDate, today);

          double averagePlaytimeValue = daysAfterJoin > 0 ? playtimesSum / daysAfterJoin : 0;

          AveragePlaytimeOverall averagePlaytimeData = new AveragePlaytimeOverall();
          averagePlaytimeData.setEmployeeId(employeeId);
          averagePlaytimeData.setPlaytime(averagePlaytimeValue);
          handledAveragePlaytimeData.add(averagePlaytimeData);
        }
      }
    }

    handledAveragePlaytimeData.sort(Comparator.comparing(AveragePlaytimeOverall::getEmployeeId));

    return handledAveragePlaytimeData;
  }

  private void saveAveragePlaytime(List<AveragePlaytimeOverall> averagePlaytimeData) {
    averagePlaytimeData.forEach(averagePlaytimeOverall -> {
      Short employeeId = averagePlaytimeOverall.getEmployeeId();

      AveragePlaytimeOverall existingPlaytime = averagePlaytimeOverallRepository
          .findByEmployeeId(employeeId);

      if (existingPlaytime != null) {
        existingPlaytime.setPlaytime(averagePlaytimeOverall.getPlaytime());
        averagePlaytimeOverallRepository.save(existingPlaytime);
      } else {
        averagePlaytimeOverallRepository.save(averagePlaytimeOverall);
      }
    });
  }
}
