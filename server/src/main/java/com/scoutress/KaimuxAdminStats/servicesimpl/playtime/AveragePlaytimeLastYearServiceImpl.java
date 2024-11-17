package com.scoutress.KaimuxAdminStats.servicesImpl.playtime;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
    List<EmployeeCodes> allEmployeeAids = dataExtractingService.getAllEmployeeCodes();
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

    Map<Short, Employee> employeeMap = allEmployees.stream()
        .collect(Collectors.toMap(Employee::getId, employee -> employee));

    Set<Short> uniqueAids = allPlaytimes.stream()
        .map(DailyPlaytime::getAid)
        .collect(Collectors.toSet());

    Set<Short> validAids = allEmployeeAids.stream()
        .map(EmployeeCodes::getEmployeeId)
        .collect(Collectors.toSet());

    List<AveragePlaytimeLastYear> handledAveragePlaytimeData = new ArrayList<>();

    LocalDate dateOneYearAgo = LocalDate.now().minusYears(1).minusDays(1);

    for (Short aid : uniqueAids) {
      if (validAids.contains(aid)) {
        Employee employee = employeeMap.get(aid);

        if (employee != null) {
          double totalPlaytime = allPlaytimes.stream()
              .filter(pt -> pt.getAid().equals(aid))
              .filter(pt -> pt.getDate().isAfter(dateOneYearAgo))
              .mapToDouble(DailyPlaytime::getTime)
              .sum();

          long daysPlayed = allPlaytimes.stream()
              .filter(pt -> pt.getAid().equals(aid))
              .filter(pt -> pt.getDate().isAfter(dateOneYearAgo))
              .map(DailyPlaytime::getDate)
              .distinct()
              .count();

          double averagePlaytime = daysPlayed > 0 ? totalPlaytime / daysPlayed : 0;

          AveragePlaytimeLastYear averagePlaytimeData = new AveragePlaytimeLastYear();
          averagePlaytimeData.setAid(aid);
          averagePlaytimeData.setPlaytime(averagePlaytime);
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
