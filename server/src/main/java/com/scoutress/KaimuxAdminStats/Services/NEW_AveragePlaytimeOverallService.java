package com.scoutress.KaimuxAdminStats.Services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.NEW_AveragePlaytimeOverall;
import com.scoutress.KaimuxAdminStats.Entity.NEW_DailyPlaytime;
import com.scoutress.KaimuxAdminStats.Entity.NEW_Employee;
import com.scoutress.KaimuxAdminStats.Entity.NEW_EmployeeCodes;
import com.scoutress.KaimuxAdminStats.Repositories.NEW_AveragePlaytimeOverallRepository;

@Service
public class NEW_AveragePlaytimeOverallService {

  private final NEW_DataExtractingService dataExtractingService;
  private final NEW_AveragePlaytimeOverallRepository averagePlaytimeRepository;

  public NEW_AveragePlaytimeOverallService(
      NEW_DataExtractingService dataExtractingService,
      NEW_AveragePlaytimeOverallRepository averagePlaytimeRepository) {
    this.dataExtractingService = dataExtractingService;
    this.averagePlaytimeRepository = averagePlaytimeRepository;
  }

  public void handleAveragePlaytime() {
    List<NEW_DailyPlaytime> allPlaytime = dataExtractingService.getDailyPlaytimeData();
    List<NEW_EmployeeCodes> allEmployeeAids = dataExtractingService.getAidsFromEmployeeCodes();
    List<NEW_Employee> allEmployees = dataExtractingService.getAllEmployees();

    List<NEW_AveragePlaytimeOverall> averagePlaytime = calculateAveragePlaytime(
        allPlaytime, allEmployeeAids, allEmployees);

    saveAveragePlaytime(averagePlaytime);
  }

  public List<NEW_AveragePlaytimeOverall> calculateAveragePlaytime(
      List<NEW_DailyPlaytime> allPlaytimes,
      List<NEW_EmployeeCodes> allEmployeeAids,
      List<NEW_Employee> allEmployees) {

    List<NEW_AveragePlaytimeOverall> handledAveragePlaytimeData = new ArrayList<>();
    LocalDate today = LocalDate.now();

    Set<Short> uniqueAids = allPlaytimes
        .stream()
        .map(NEW_DailyPlaytime::getAid)
        .collect(Collectors.toSet());

    Set<Short> allAids = allEmployeeAids
        .stream()
        .map(NEW_EmployeeCodes::getEmployeeId)
        .collect(Collectors.toSet());

    for (Short aid : uniqueAids) {
      if (allAids.contains(aid)) {
        NEW_Employee employee = allEmployees
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
              .mapToInt(NEW_DailyPlaytime::getTime)
              .sum();

          int daysAfterJoin = (int) ChronoUnit.DAYS.between(joinDate, today);
          double averagePlaytimeValue = daysAfterJoin > 0 ? (double) playtimesSum / daysAfterJoin : 0;

          NEW_AveragePlaytimeOverall averagePlaytimeData = new NEW_AveragePlaytimeOverall();
          averagePlaytimeData.setAid(aid);
          averagePlaytimeData.setPlaytime(averagePlaytimeValue);
          handledAveragePlaytimeData.add(averagePlaytimeData);
        }
      }
    }

    handledAveragePlaytimeData.sort(Comparator.comparing(NEW_AveragePlaytimeOverall::getAid));
    return handledAveragePlaytimeData;
  }

  public void saveAveragePlaytime(List<NEW_AveragePlaytimeOverall> averagePlaytimeData) {
    averagePlaytimeData.forEach(averagePlaytimeRepository::save);
  }
}
