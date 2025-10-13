package com.scoutress.KaimuxAdminStats.servicesImpl.playtime;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger log = LoggerFactory.getLogger(AveragePlaytimeOverallServiceImpl.class);

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
    long startTime = System.currentTimeMillis();
    log.info("=== Starting average playtime calculation ===");

    try {
      List<DailyPlaytime> allPlaytime = dailyPlaytimeRepository.findAll();
      List<EmployeeCodes> allEmployeeCodes = employeeCodesRepository.findAll();
      List<Employee> allEmployees = employeeRepository.findAll();

      if (allPlaytime.isEmpty()) {
        log.warn("⚠️ No playtime data found. Exiting early.");
        return;
      }

      Map<Short, List<DailyPlaytime>> playtimeByEmployee = allPlaytime
          .stream()
          .collect(Collectors.groupingBy(DailyPlaytime::getEmployeeId));

      Set<Short> validEmployeeIds = allEmployeeCodes
          .stream()
          .map(EmployeeCodes::getEmployeeId)
          .collect(Collectors.toSet());

      Map<Short, Employee> employeeMap = allEmployees
          .stream()
          .collect(Collectors.toMap(Employee::getId, emp -> emp));

      log.debug("Loaded {} employees, {} employee codes, and {} playtime entries.",
          allEmployees.size(), allEmployeeCodes.size(), allPlaytime.size());

      List<AveragePlaytimeOverall> results = new ArrayList<>();
      LocalDate today = LocalDate.now();

      int processed = 0;
      for (Map.Entry<Short, List<DailyPlaytime>> entry : playtimeByEmployee.entrySet()) {
        Short employeeId = entry.getKey();

        if (!validEmployeeIds.contains(employeeId)) {
          continue;
        }

        Employee employee = employeeMap.get(employeeId);
        if (employee == null || employee.getJoinDate() == null) {
          log.warn("⚠️ Missing employee or join date for ID {} — skipping.", employeeId);
          continue;
        }

        List<DailyPlaytime> empPlaytime = entry.getValue();
        LocalDate joinDate = employee.getJoinDate();

        double totalPlaytime = empPlaytime
            .stream()
            .filter(pt -> !pt.getDate().isBefore(joinDate))
            .mapToDouble(DailyPlaytime::getTimeInHours)
            .sum();

        LocalDate oldestDateFromData = empPlaytime
            .stream()
            .map(DailyPlaytime::getDate)
            .min(LocalDate::compareTo)
            .orElse(joinDate);

        LocalDate effectiveStart = oldestDateFromData.isAfter(joinDate) ? oldestDateFromData : joinDate;
        long daysBetween = Math.max(ChronoUnit.DAYS.between(effectiveStart, today), 1);
        double average = totalPlaytime / daysBetween;

        AveragePlaytimeOverall avgEntity = new AveragePlaytimeOverall();
        avgEntity.setEmployeeId(employeeId);
        avgEntity.setPlaytime(average);
        results.add(avgEntity);

        processed++;
        if (processed % 20 == 0 || processed == playtimeByEmployee.size()) {
          int percent = (int) ((processed / (double) playtimeByEmployee.size()) * 100);
          log.info("Progress: {}/{} employees processed ({}%)", processed, playtimeByEmployee.size(), percent);
        }
      }

      results.sort(Comparator.comparing(AveragePlaytimeOverall::getEmployeeId));
      saveAveragePlaytime(results);

      long elapsed = System.currentTimeMillis() - startTime;
      log.info("✅ Average playtime calculation completed. Processed {} employees in {} ms ({} s).",
          results.size(), elapsed, elapsed / 1000.0);

    } catch (Exception e) {
      log.error("❌ Critical error during average playtime calculation: {}", e.getMessage(), e);
    }
  }

  private void saveAveragePlaytime(List<AveragePlaytimeOverall> averagePlaytimeData) {
    log.info("Saving {} average playtime entries...", averagePlaytimeData.size());

    for (AveragePlaytimeOverall avg : averagePlaytimeData) {
      try {
        AveragePlaytimeOverall existing = averagePlaytimeOverallRepository
            .findByEmployeeId(avg.getEmployeeId());

        if (existing != null) {
          existing.setPlaytime(avg.getPlaytime());
          averagePlaytimeOverallRepository.save(existing);
        } else {
          averagePlaytimeOverallRepository.save(avg);
        }

      } catch (Exception e) {
        log.error("❌ Failed to save average playtime for employee {}: {}", avg.getEmployeeId(), e.getMessage(), e);
      }
    }

    log.info("✅ Finished saving all average playtime entries.");
  }
}
