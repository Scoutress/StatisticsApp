package com.scoutress.KaimuxAdminStats.Services.playtime;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.employees.NEW_Employee;
import com.scoutress.KaimuxAdminStats.Entity.employees.NEW_EmployeeCodes;
import com.scoutress.KaimuxAdminStats.Entity.playtime.NEW_AveragePlaytimeLastYear;
import com.scoutress.KaimuxAdminStats.Entity.playtime.NEW_DailyPlaytime;
import com.scoutress.KaimuxAdminStats.Repositories.playtime.NEW_AveragePlaytimeLastYearRepository;
import com.scoutress.KaimuxAdminStats.Services.NEW_DataExtractingService;

@Service
public class NEW_AveragePlaytimeLastYearService {

  private final NEW_DataExtractingService dataExtractingService;
  private final NEW_AveragePlaytimeLastYearRepository averagePlaytimeLastYearRepository;

  public NEW_AveragePlaytimeLastYearService(
      NEW_DataExtractingService dataExtractingService,
      NEW_AveragePlaytimeLastYearRepository averagePlaytimeLastYearRepository) {
    this.dataExtractingService = dataExtractingService;
    this.averagePlaytimeLastYearRepository = averagePlaytimeLastYearRepository;
  }

  public void handleAveragePlaytime() {
    List<NEW_DailyPlaytime> allPlaytime = dataExtractingService.getDailyPlaytimeData();
    List<NEW_EmployeeCodes> allEmployeeAids = dataExtractingService.getAidsFromEmployeeCodes();
    List<NEW_Employee> allEmployees = dataExtractingService.getAllEmployees();

    List<NEW_AveragePlaytimeLastYear> averagePlaytime = calculateAveragePlaytime(
        allPlaytime, allEmployeeAids, allEmployees);

    saveAveragePlaytime(averagePlaytime);
  }

  public List<NEW_AveragePlaytimeLastYear> calculateAveragePlaytime(
      List<NEW_DailyPlaytime> allPlaytimes,
      List<NEW_EmployeeCodes> allEmployeeAids,
      List<NEW_Employee> allEmployees) {

    List<NEW_AveragePlaytimeLastYear> handledAveragePlaytimeData = new ArrayList<>();

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
          LocalDate dateOneYearAgo = LocalDate.now().minusYears(1).minusDays(1);

          int playtimesSum = allPlaytimes
              .stream()
              .filter(pt -> pt.getDate().isAfter(dateOneYearAgo))
              .filter(pt -> pt.getAid() == aid)
              .mapToInt(NEW_DailyPlaytime::getTime)
              .sum();

          double averagePlaytimeValue = (double) playtimesSum / 365;

          NEW_AveragePlaytimeLastYear averagePlaytimeData = new NEW_AveragePlaytimeLastYear();
          averagePlaytimeData.setAid(aid);
          averagePlaytimeData.setPlaytime(averagePlaytimeValue);
          handledAveragePlaytimeData.add(averagePlaytimeData);
        }
      }
    }

    handledAveragePlaytimeData.sort(Comparator.comparing(NEW_AveragePlaytimeLastYear::getAid));
    return handledAveragePlaytimeData;
  }

  public void saveAveragePlaytime(List<NEW_AveragePlaytimeLastYear> averagePlaytimeData) {
    averagePlaytimeData.forEach(averagePlaytimeLastYearRepository::save);
  }
}
