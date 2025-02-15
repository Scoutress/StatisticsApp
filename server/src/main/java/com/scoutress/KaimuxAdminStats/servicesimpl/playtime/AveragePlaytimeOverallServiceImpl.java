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
import com.scoutress.KaimuxAdminStats.repositories.playtime.AveragePlaytimeOverallRepository;
import com.scoutress.KaimuxAdminStats.services.DataExtractingService;
import com.scoutress.KaimuxAdminStats.services.playtime.AveragePlaytimeOverallService;

@Service
public class AveragePlaytimeOverallServiceImpl implements AveragePlaytimeOverallService {

  private final DataExtractingService dataExtractingService;
  private final AveragePlaytimeOverallRepository averagePlaytimeOverallRepository;

  public AveragePlaytimeOverallServiceImpl(
      DataExtractingService dataExtractingService,
      AveragePlaytimeOverallRepository averagePlaytimeOverallRepository) {

    this.dataExtractingService = dataExtractingService;
    this.averagePlaytimeOverallRepository = averagePlaytimeOverallRepository;
  }

  @Override
  public void handleAveragePlaytime() {
    List<DailyPlaytime> allPlaytime = dataExtractingService.getDailyPlaytimeData();
    List<EmployeeCodes> allEmployeeCodes = dataExtractingService.getAllEmployeeCodes();
    List<Employee> allEmployees = dataExtractingService.getAllEmployees();
    List<AveragePlaytimeOverall> averagePlaytime = calculateAveragePlaytime(
        allPlaytime, allEmployeeCodes, allEmployees);

    saveAveragePlaytime(averagePlaytime);
  }

  public List<AveragePlaytimeOverall> calculateAveragePlaytime(
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

  public void saveAveragePlaytime(List<AveragePlaytimeOverall> averagePlaytimeData) {
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
