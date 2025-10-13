package com.scoutress.KaimuxAdminStats.servicesImpl.playtime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.entity.playtime.SessionDuration;
import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.repositories.playtime.DailyPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.ProcessedPlaytimeSessionsRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.services.playtime.DailyPlaytimeService;

@Service
public class DailyPlaytimeServiceImpl implements DailyPlaytimeService {

  private static final Logger log = LoggerFactory.getLogger(DailyPlaytimeServiceImpl.class);

  private final DailyPlaytimeRepository dailyPlaytimeRepository;
  private final ProcessedPlaytimeSessionsRepository processedPlaytimeSessionsRepository;
  private final EmployeeRepository employeeRepository;

  public DailyPlaytimeServiceImpl(
      DailyPlaytimeRepository dailyPlaytimeRepository,
      ProcessedPlaytimeSessionsRepository processedPlaytimeSessionsRepository,
      EmployeeRepository employeeRepository) {
    this.dailyPlaytimeRepository = dailyPlaytimeRepository;
    this.processedPlaytimeSessionsRepository = processedPlaytimeSessionsRepository;
    this.employeeRepository = employeeRepository;
  }

  // ===============================================================
  // MAIN PROCESS
  // ===============================================================
  @Override
  public void handleDailyPlaytime() {
    long start = System.currentTimeMillis();

    log.info("=== Starting daily playtime calculation ===");

    try {
      List<Employee> employees = employeeRepository.findAll();

      if (employees.isEmpty()) {
        log.warn("⚠️ No employees found. Exiting early.");
        return;
      }

      List<DailyPlaytime> results = new ArrayList<>();
      int totalProcessed = 0;

      for (Employee employee : employees) {
        Short employeeId = employee.getId();
        List<SessionDuration> sessions = processedPlaytimeSessionsRepository.findByEmployeeId(employeeId);

        if (sessions.isEmpty())
          continue;

        Map<String, Double> groupedPlaytime = sessions
            .stream()
            .collect(Collectors.groupingBy(
                s -> s.getServer().trim().toLowerCase() + "|" + s.getDate(),
                Collectors.summingDouble(SessionDuration::getSingleSessionDurationInSec)));

        for (Map.Entry<String, Double> entry : groupedPlaytime.entrySet()) {
          try {
            String[] parts = entry.getKey().split("\\|");

            if (parts.length < 2) {
              log.warn("⚠️ Invalid grouped key format for employee {}: {}", employeeId, entry.getKey());
              continue;
            }

            String server = parts[0];
            LocalDate date = safeParseDate(parts[1]);

            if (date == null) {
              log.warn("⚠️ Skipping invalid date '{}' for employee {}", parts[1], employeeId);
              continue;
            }

            double hours = entry.getValue() / 3600.0;

            if (hours <= 0)
              continue;

            DailyPlaytime dp = new DailyPlaytime();
            dp.setEmployeeId(employeeId);
            dp.setServer(server);
            dp.setDate(date);
            dp.setTimeInHours(hours);
            results.add(dp);

          } catch (Exception e) {
            log.error("❌ Error processing playtime for employee {}: {}", employeeId, e.getMessage());
          }
        }

        totalProcessed++;
        if (totalProcessed % 5 == 0 || totalProcessed == employees.size()) {

          int percent = (int) ((totalProcessed / (double) employees.size()) * 100);

          log.info("Progress: processed {}/{} employees ({}%)", totalProcessed, employees.size(), percent);
        }
      }

      if (results.isEmpty()) {
        log.warn("⚠️ No valid playtime data calculated.");
        return;
      }

      results.sort(Comparator.comparing(DailyPlaytime::getDate));
      saveCalculatedPlaytime(results);

      long elapsed = System.currentTimeMillis() - start;

      log.info("✅ Daily playtime calculation completed in {} ms ({} s). Total saved records: {}",
          elapsed, elapsed / 1000.0, results.size());

    } catch (Exception e) {
      log.error("❌ Critical error in handleDailyPlaytime: {}", e.getMessage(), e);
    }
  }

  // ===============================================================
  // SAFE DATE PARSER
  // ===============================================================
  private LocalDate safeParseDate(String input) {
    if (input == null || input.isBlank())
      return null;

    try {
      if (input.matches("\\d{4}-\\d{2}-\\d{2}")) {
        return LocalDate.parse(input);
      }
      if (input.matches("\\d{4}-\\d{2}-\\d{2}[ T]\\d{2}:\\d{2}:\\d{2}")) {
        String normalized = input.replace(' ', 'T');
        return LocalDateTime.parse(normalized, DateTimeFormatter.ISO_LOCAL_DATE_TIME).toLocalDate();
      }
      if (input.matches("\\d{4}")) {
        return LocalDate.of(Integer.parseInt(input), 1, 1);
      }
      if (input.matches("\\d{4}-\\d{2}")) {
        String fullDate = input + "-01";
        return LocalDate.parse(fullDate);
      }

      log.warn("⚠️ Unrecognized date format: {}", input);
      return null;

    } catch (Exception e) {
      log.error("❌ Failed to parse date '{}': {}", input, e.getMessage());
      return null;
    }
  }

  // ===============================================================
  // SAVE RESULTS
  // ===============================================================
  private void saveCalculatedPlaytime(List<DailyPlaytime> dailyPlaytimeData) {
    log.info("Saving {} daily playtime records...", dailyPlaytimeData.size());
    int updated = 0, inserted = 0;

    for (DailyPlaytime playtime : dailyPlaytimeData) {
      try {
        DailyPlaytime existing = dailyPlaytimeRepository.findByEmployeeIdAndDateAndServer(
            playtime.getEmployeeId(), playtime.getDate(), playtime.getServer());

        if (existing != null) {
          if (!existing.getTimeInHours().equals(playtime.getTimeInHours())) {
            existing.setTimeInHours(playtime.getTimeInHours());
            dailyPlaytimeRepository.save(existing);
            updated++;
          }
        } else {
          dailyPlaytimeRepository.save(playtime);
          inserted++;
        }
      } catch (Exception e) {
        log.error("Error saving playtime for employee {} on {}: {}",
            playtime.getEmployeeId(), playtime.getDate(), e.getMessage());
      }
    }

    log.info("✅ Saved playtime records — inserted: {}, updated: {}", inserted, updated);
  }

  // ===============================================================
  // DUPLICATES REMOVAL
  // ===============================================================
  @Override
  public void removeDuplicateDailyPlaytimes() {
    long start = System.currentTimeMillis();
    log.info("=== Starting duplicate removal for DailyPlaytime ===");

    try {
      List<DailyPlaytime> all = dailyPlaytimeRepository.findAll();
      if (all.isEmpty()) {
        log.info("No playtime data found, skipping duplicate removal.");
        return;
      }

      Map<String, List<DailyPlaytime>> grouped = all
          .stream()
          .collect(Collectors.groupingBy(
              p -> p.getEmployeeId()
                  + "|"
                  + p.getServer().trim().toLowerCase()
                  + "|"
                  + p.getDate()
                  + "|"
                  + p.getTimeInHours()));

      List<DailyPlaytime> toRemove = new ArrayList<>();
      int duplicates = 0;

      for (List<DailyPlaytime> group : grouped.values()) {
        if (group.size() > 1) {
          duplicates += group.size() - 1;
          toRemove.addAll(group.subList(1, group.size()));
        }
      }

      if (!toRemove.isEmpty()) {
        dailyPlaytimeRepository.deleteAll(toRemove);

        log.info("Removed {} duplicate playtime records.", duplicates);
      } else {
        log.info("No duplicates found.");
      }

      long elapsed = System.currentTimeMillis() - start;
      log.info("✅ Duplicate cleanup finished in {} ms ({} s).", elapsed, elapsed / 1000.0);

    } catch (Exception e) {
      log.error("❌ Error removing duplicates: {}", e.getMessage(), e);
    }
  }

  // ===============================================================
  // METRICS
  // ===============================================================
  @Override
  public Double getSumOfPlaytimeByEmployeeIdAndDuration(Short employeeId, Short days) {
    return dailyPlaytimeRepository
        .findAll()
        .stream()
        .filter(p -> p.getEmployeeId().equals(employeeId))
        .filter(p -> p.getDate().isAfter(LocalDate.now().minusDays(days)))
        .mapToDouble(DailyPlaytime::getTimeInHours)
        .sum();
  }
}
