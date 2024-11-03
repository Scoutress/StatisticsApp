package com.scoutress.KaimuxAdminStats.servicesimpl.playtime;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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
    List<EmployeeCodes> allEmployeeAids = dataExtractingService.getAidsFromEmployeeCodes();
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

    Set<Short> uniqueAids = allPlaytimes
        .stream()
        .map(DailyPlaytime::getAid)
        .collect(Collectors.toSet());

    Set<Short> allAids = allEmployeeAids
        .stream()
        .map(EmployeeCodes::getEmployeeId)
        .collect(Collectors.toSet());

    for (Short aid : uniqueAids) {
      if (allAids.contains(aid)) {
        Employee employee = allEmployees
            .stream()
            .filter(emp -> Objects.equals(emp.getId(), aid))
            .findFirst()
            .orElse(null);

        if (employee != null) {
          LocalDate joinDate = employee.getJoinDate().minusDays(1);

          int playtimesSum = allPlaytimes
              .stream()
              .filter(pt -> pt.getDate().isAfter(joinDate))
              .filter(pt -> pt.getAid() == aid)
              .mapToInt(DailyPlaytime::getTime)
              .sum();

          int daysAfterJoin = (int) ChronoUnit.DAYS.between(joinDate, today);
          double averagePlaytimeValue = daysAfterJoin > 0 ? (double) playtimesSum / daysAfterJoin : 0;

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