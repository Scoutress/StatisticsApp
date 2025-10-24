package com.scoutress.KaimuxAdminStats.servicesImpl.playtime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.entity.playtime.SessionDuration;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.DailyPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.ProcessedPlaytimeSessionsRepository;
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

  @Override
  public void handleDailyPlaytime() {
    long start = System.currentTimeMillis();
    log.info("=== [START] Daily playtime calculation ===");

    try {
      List<Employee> employees = employeeRepository.findAll();

      if (employees.isEmpty()) {
        log.warn("⚠️ No employees found. Exiting early.");
        return;
      }

      log.debug("Loaded {} employees from repository.", employees.size());

      List<DailyPlaytime> results = new ArrayList<>();
      int processed = 0;

      for (Employee employee : employees) {
        Short employeeId = employee.getId();
        List<SessionDuration> sessions = processedPlaytimeSessionsRepository.findByEmployeeId(employeeId);

        if (sessions.isEmpty()) {
          log.trace("Employee {} — no session data found, skipping.", employeeId);
          continue;
        }

        Map<String, Double> groupedPlaytime = sessions
            .stream()
            .collect(Collectors.groupingBy(
                s -> s.getServer().trim().toLowerCase() + "|" + s.getDate(),
                Collectors.summingDouble(SessionDuration::getSingleSessionDurationInSec)));

        log.trace("Employee {} — grouped into {} server/date combinations.", employeeId, groupedPlaytime.size());

        for (Map.Entry<String, Double> entry : groupedPlaytime.entrySet()) {
          try {
            String[] parts = entry.getKey().split("\\|");

            if (parts.length < 2) {
              log.warn("⚠️ Invalid key format '{}' for employee {}.", entry.getKey(), employeeId);
              continue;
            }

            String server = parts[0];
            LocalDate date = safeParseDate(parts[1]);
            if (date == null) {
              log.warn("⚠️ Skipping invalid date '{}' for employee {}.", parts[1], employeeId);
              continue;
            }

            double hours = entry.getValue() / 3600.0;
            if (hours <= 0) {
              log.trace("Employee {} — server {} — zero-hour entry ignored.", employeeId, server);
              continue;
            }

            DailyPlaytime dp = new DailyPlaytime();
            dp.setEmployeeId(employeeId);
            dp.setServer(server);
            dp.setDate(date);
            dp.setTimeInHours(hours);
            results.add(dp);

            log.trace("Employee {} | {} | {} — {}h", employeeId, server, date, hours);

          } catch (Exception e) {
            log.error("❌ Error processing playtime for employee {}: {}", employeeId, e.getMessage(), e);
          }
        }

        processed++;
        if (processed % 10 == 0 || processed == employees.size()) {
          int percent = (int) ((processed / (double) employees.size()) * 100);
          log.info("Progress: processed {}/{} employees ({}%)", processed, employees.size(), percent);
        }
      }

      if (results.isEmpty()) {
        log.warn("⚠️ No valid playtime data calculated.");
        return;
      }

      results.sort(Comparator.comparing(DailyPlaytime::getDate));
      saveCalculatedPlaytime(results);

      long elapsed = System.currentTimeMillis() - start;
      log.info("✅ [END] Daily playtime calculation completed in {} ms ({} s). Saved {} records.",
          elapsed, elapsed / 1000.0, results.size());

    } catch (Exception e) {
      log.error("❌ Critical error in handleDailyPlaytime: {}", e.getMessage(), e);
    }
  }

  private LocalDate safeParseDate(String input) {
    if (input == null || input.isBlank())
      return null;
    try {
      if (input.matches("\\d{4}-\\d{2}-\\d{2}")) {
        return LocalDate.parse(input);
      }
      if (input.matches("\\d{4}-\\d{2}-\\d{2}[ T]\\d{2}:\\d{2}:\\d{2}")) {
        return LocalDateTime.parse(input.replace(' ', 'T'), DateTimeFormatter.ISO_LOCAL_DATE_TIME).toLocalDate();
      }
      if (input.matches("\\d{4}")) {
        return LocalDate.of(Integer.parseInt(input), 1, 1);
      }
      if (input.matches("\\d{4}-\\d{2}")) {
        return LocalDate.parse(input + "-01");
      }

      log.warn("⚠️ Unrecognized date format: {}", input);
      return null;

    } catch (NumberFormatException e) {
      log.error("❌ Failed to parse date '{}': {}", input, e.getMessage());
      return null;
    }
  }

  private void saveCalculatedPlaytime(List<DailyPlaytime> dailyPlaytimeData) {
    log.info("Saving {} daily playtime records to database...", dailyPlaytimeData.size());
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
            log.trace("Updated record for employee {} — {} on {} ({}h).",
                playtime.getEmployeeId(), playtime.getServer(), playtime.getDate(), playtime.getTimeInHours());
          } else {
            log.trace("Unchanged record for employee {} — {} on {} ({}h).",
                playtime.getEmployeeId(), playtime.getServer(), playtime.getDate(), playtime.getTimeInHours());
          }
        } else {
          dailyPlaytimeRepository.save(playtime);
          inserted++;
          log.trace("Inserted new record for employee {} — {} on {} ({}h).",
              playtime.getEmployeeId(), playtime.getServer(), playtime.getDate(), playtime.getTimeInHours());
        }

      } catch (Exception e) {
        log.error("❌ Error saving playtime for employee {} on {}: {}",
            playtime.getEmployeeId(), playtime.getDate(), e.getMessage(), e);
      }
    }

    log.info("✅ Playtime save completed — inserted: {}, updated: {}", inserted, updated);
  }

  @Override
  public void removeDuplicateDailyPlaytimes() {
    long start = System.currentTimeMillis();
    log.info("=== [START] Duplicate removal for DailyPlaytime ===");

    try {
      List<DailyPlaytime> all = dailyPlaytimeRepository.findAll();
      if (all.isEmpty()) {
        log.info("No playtime data found — skipping duplicate removal.");
        return;
      }

      Map<String, List<DailyPlaytime>> grouped = all
          .stream()
          .collect(Collectors.groupingBy(p -> p.getEmployeeId() + "|" + p.getServer().trim().toLowerCase() + "|"
              + p.getDate() + "|" + p.getTimeInHours()));

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
        log.info("Removed {} duplicate records from DailyPlaytime.", duplicates);
      } else {
        log.info("No duplicate playtime records found.");
      }

      long elapsed = System.currentTimeMillis() - start;
      log.info("✅ [END] Duplicate cleanup finished in {} ms ({} s).", elapsed, elapsed / 1000.0);

    } catch (Exception e) {
      log.error("❌ Error removing duplicates: {}", e.getMessage(), e);
    }
  }

  @Override
  public Double getSumOfPlaytimeByEmployeeIdAndDuration(Short employeeId, Short days) {
    double total = dailyPlaytimeRepository
        .findAll()
        .stream()
        .filter(p -> p.getEmployeeId().equals(employeeId))
        .filter(p -> p.getDate().isAfter(LocalDate.now().minusDays(days)))
        .mapToDouble(DailyPlaytime::getTimeInHours)
        .sum();

    log.debug("Queried total playtime for emp {} — {} days = {}h", employeeId, days, total);
    return total;
  }
}
