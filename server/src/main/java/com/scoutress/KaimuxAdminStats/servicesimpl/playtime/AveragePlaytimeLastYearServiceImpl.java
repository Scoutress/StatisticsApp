package com.scoutress.KaimuxAdminStats.servicesimpl.playtime;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;
import com.scoutress.KaimuxAdminStats.entity.playtime.AveragePlaytimeLastYear;
import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.repositories.playtime.AveragePlaytimeLastYearRepository;
import com.scoutress.KaimuxAdminStats.services.DataExtractingService;
import com.scoutress.KaimuxAdminStats.services.playtime.AveragePlaytimeLastYearService;

@Service
public class AveragePlaytimeLastYearServiceImpl implements AveragePlaytimeLastYearService {

  private final DataExtractingService dataExtractingService;
  private final AveragePlaytimeLastYearRepository averagePlaytimeLastYearRepository;

  public AveragePlaytimeLastYearServiceImpl(
      DataExtractingService dataExtractingService,
      AveragePlaytimeLastYearRepository averagePlaytimeLastYearRepository) {

    this.dataExtractingService = dataExtractingService;
    this.averagePlaytimeLastYearRepository = averagePlaytimeLastYearRepository;
  }

  @Override
  public void handleAveragePlaytime() {
    List<DailyPlaytime> allPlaytime = dataExtractingService.getDailyPlaytimeData();
    List<EmployeeCodes> allEmployeeAids = dataExtractingService.getAidsFromEmployeeCodes();
    List<Employee> allEmployees = dataExtractingService.getAllEmployees();

    List<AveragePlaytimeLastYear> averagePlaytime = calculateAveragePlaytime(
        allPlaytime, allEmployeeAids, allEmployees);

    saveAveragePlaytime(averagePlaytime);
  }

  @Override
  public List<AveragePlaytimeLastYear> calculateAveragePlaytime(
      List<DailyPlaytime> allPlaytimes,
      List<EmployeeCodes> allEmployeeAids,
      List<Employee> allEmployees) {

    List<AveragePlaytimeLastYear> handledAveragePlaytimeData = new ArrayList<>();

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
          LocalDate dateOneYearAgo = LocalDate.now().minusYears(1).minusDays(1);

          int playtimesSum = allPlaytimes
              .stream()
              .filter(pt -> pt.getDate().isAfter(dateOneYearAgo))
              .filter(pt -> pt.getAid() == aid)
              .mapToInt(DailyPlaytime::getTime)
              .sum();

          double averagePlaytimeValue = (double) playtimesSum / 365;

          AveragePlaytimeLastYear averagePlaytimeData = new AveragePlaytimeLastYear();
          averagePlaytimeData.setAid(aid);
          averagePlaytimeData.setPlaytime(averagePlaytimeValue);
          handledAveragePlaytimeData.add(averagePlaytimeData);
        }
      }
    }

    handledAveragePlaytimeData.sort(Comparator.comparing(AveragePlaytimeLastYear::getAid));
    return handledAveragePlaytimeData;
  }

  @Override
  public void saveAveragePlaytime(List<AveragePlaytimeLastYear> averagePlaytimeData) {
    averagePlaytimeData.forEach(averagePlaytimeLastYearRepository::save);
  }
}
