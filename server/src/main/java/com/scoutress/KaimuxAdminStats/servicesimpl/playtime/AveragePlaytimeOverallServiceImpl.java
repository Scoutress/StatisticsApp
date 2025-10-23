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
    log.info("=== [START] Average playtime calculation process ===");

    try {
      // 1️⃣ — DUOMENŲ NUSKAITYMAS
      List<DailyPlaytime> allPlaytime = dailyPlaytimeRepository.findAll();
      List<EmployeeCodes> allEmployeeCodes = employeeCodesRepository.findAll();
      List<Employee> allEmployees = employeeRepository.findAll();

      log.debug("Loaded {} playtime records, {} employee codes, and {} employees.",
          allPlaytime.size(), allEmployeeCodes.size(), allEmployees.size());

      if (allPlaytime.isEmpty()) {
        log.warn("⚠️ No playtime data found. Exiting early.");
        return;
      }

      // 2️⃣ — DUOMENŲ PARUOŠIMAS
      Map<Short, List<DailyPlaytime>> playtimeByEmployee = allPlaytime.stream()
          .collect(Collectors.groupingBy(DailyPlaytime::getEmployeeId));

      Set<Short> validEmployeeIds = allEmployeeCodes.stream()
          .map(EmployeeCodes::getEmployeeId)
          .collect(Collectors.toSet());

      Map<Short, Employee> employeeMap = allEmployees.stream()
          .collect(Collectors.toMap(Employee::getId, emp -> emp));

      log.trace("Data grouped: {} employees have playtime data.", playtimeByEmployee.size());

      // 3️⃣ — SKAIČIAVIMAS
      List<AveragePlaytimeOverall> results = new ArrayList<>();
      LocalDate today = LocalDate.now();
      int processed = 0;

      for (Map.Entry<Short, List<DailyPlaytime>> entry : playtimeByEmployee.entrySet()) {
        Short employeeId = entry.getKey();

        // Skip non-active / invalid employees
        if (!validEmployeeIds.contains(employeeId)) {
          log.trace("Skipping employee {} — not found in valid employee codes.", employeeId);
          continue;
        }

        Employee employee = employeeMap.get(employeeId);
        if (employee == null || employee.getJoinDate() == null) {
          log.warn("⚠️ Missing employee record or join date for ID {} — skipping.", employeeId);
          continue;
        }

        List<DailyPlaytime> empPlaytime = entry.getValue();
        LocalDate joinDate = employee.getJoinDate();

        // Bendras playtime nuo prisijungimo
        double totalPlaytime = empPlaytime.stream()
            .filter(pt -> !pt.getDate().isBefore(joinDate))
            .mapToDouble(DailyPlaytime::getTimeInHours)
            .sum();

        // Seniausia data su duomenimis
        LocalDate oldestDateFromData = empPlaytime.stream()
            .map(DailyPlaytime::getDate)
            .min(LocalDate::compareTo)
            .orElse(joinDate);

        LocalDate effectiveStart = oldestDateFromData.isAfter(joinDate) ? oldestDateFromData : joinDate;
        long daysBetween = Math.max(ChronoUnit.DAYS.between(effectiveStart, today), 1);
        double average = totalPlaytime / daysBetween;

        log.trace("Employee {} — total={}h, days={}, avg={}",
            employeeId, totalPlaytime, daysBetween, average);

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

      // 4️⃣ — DUOMENŲ SAUGOJIMAS
      saveAveragePlaytime(results);

      long elapsed = System.currentTimeMillis() - startTime;
      log.info("✅ [END] Average playtime calculation completed. {} employees processed in {} ms ({} s).",
          results.size(), elapsed, elapsed / 1000.0);

    } catch (Exception e) {
      log.error("❌ Critical error during average playtime calculation: {}", e.getMessage(), e);
    }
  }

  // ===========================================================
  // DUOMENŲ SAUGOJIMAS
  // ===========================================================
  private void saveAveragePlaytime(List<AveragePlaytimeOverall> averagePlaytimeData) {
    log.info("Saving {} average playtime entries to database...", averagePlaytimeData.size());
    int processed = 0;

    for (AveragePlaytimeOverall avg : averagePlaytimeData) {
      try {
        Short empId = avg.getEmployeeId();
        double avgPlaytime = avg.getPlaytime();

        AveragePlaytimeOverall existing = averagePlaytimeOverallRepository.findByEmployeeId(empId);
        if (existing != null) {
          double oldVal = existing.getPlaytime();
          existing.setPlaytime(avgPlaytime);
          averagePlaytimeOverallRepository.save(existing);
          log.trace("Updated employee {} — old={}h → new={}h", empId, oldVal, avgPlaytime);
        } else {
          averagePlaytimeOverallRepository.save(avg);
          log.trace("Inserted new avg playtime record for employee {} — {}h", empId, avgPlaytime);
        }

      } catch (Exception e) {
        log.error("❌ Failed to save average playtime for employee {}: {}", avg.getEmployeeId(), e.getMessage(), e);
      }

      processed++;
      if (processed % 25 == 0 || processed == averagePlaytimeData.size()) {
        int percent = (int) ((processed / (double) averagePlaytimeData.size()) * 100);
        log.info("Progress: saved {}/{} ({}%)", processed, averagePlaytimeData.size(), percent);
      }
    }

    log.info("✅ All average playtime entries saved successfully.");
  }
}
