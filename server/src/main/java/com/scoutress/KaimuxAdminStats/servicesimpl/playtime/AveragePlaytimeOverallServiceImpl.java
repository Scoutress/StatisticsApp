package com.scoutress.KaimuxAdminStats.servicesimpl.playtime;

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
  private final AveragePlaytimeOverallRepository averagePlaytimeRepository;

  public AveragePlaytimeOverallServiceImpl(
      DataExtractingService dataExtractingService,
      AveragePlaytimeOverallRepository averagePlaytimeRepository) {

    this.dataExtractingService = dataExtractingService;
    this.averagePlaytimeRepository = averagePlaytimeRepository;
  }

  @Override
  public void handleAveragePlaytime() {
    List<DailyPlaytime> allPlaytime = dataExtractingService.getDailyPlaytimeData();
    List<EmployeeCodes> allEmployeeAids = dataExtractingService.getAllEmployeeCodes();
    List<Employee> allEmployees = dataExtractingService.getAllEmployees();

    List<AveragePlaytimeOverall> averagePlaytime = calculateAveragePlaytime(
        allPlaytime, allEmployeeAids, allEmployees);

    saveAveragePlaytime(averagePlaytime);
  }

  @Override
  public List<AveragePlaytimeOverall> calculateAveragePlaytime(
      List<DailyPlaytime> allPlaytimes,
      List<EmployeeCodes> allEmployeeAids,
      List<Employee> allEmployees) {

    List<AveragePlaytimeOverall> handledAveragePlaytimeData = new ArrayList<>();
    LocalDate today = LocalDate.now();

    Map<Short, Employee> employeeMap = allEmployees.stream()
        .collect(Collectors.toMap(Employee::getId, emp -> emp));

    Set<Short> allAids = allEmployeeAids.stream()
        .map(EmployeeCodes::getEmployeeId)
        .collect(Collectors.toSet());

    Set<Short> uniqueAids = allPlaytimes.stream()
        .map(DailyPlaytime::getAid)
        .collect(Collectors.toSet());

    for (Short aid : uniqueAids) {
      if (allAids.contains(aid)) {
        Employee employee = employeeMap.get(aid);

        if (employee != null) {
          LocalDate joinDate = employee.getJoinDate();

          double playtimesSum = allPlaytimes.stream()
              .filter(pt -> pt.getAid().equals(aid))
              .filter(pt -> !pt.getDate().isBefore(joinDate))
              .mapToDouble(DailyPlaytime::getTime)
              .sum();

          long daysAfterJoin = ChronoUnit.DAYS.between(joinDate, today);

          double averagePlaytimeValue = daysAfterJoin > 0 ? playtimesSum / daysAfterJoin : 0;

          AveragePlaytimeOverall averagePlaytimeData = new AveragePlaytimeOverall();
          averagePlaytimeData.setAid(aid);
          averagePlaytimeData.setPlaytime(averagePlaytimeValue);
          handledAveragePlaytimeData.add(averagePlaytimeData);
        }
      }
    }

    handledAveragePlaytimeData.sort(Comparator.comparing(AveragePlaytimeOverall::getAid));

    return handledAveragePlaytimeData;
  }

  @Override
  public void saveAveragePlaytime(List<AveragePlaytimeOverall> averagePlaytimeData) {
    averagePlaytimeData.forEach(averagePlaytimeRepository::save);
  }
}
