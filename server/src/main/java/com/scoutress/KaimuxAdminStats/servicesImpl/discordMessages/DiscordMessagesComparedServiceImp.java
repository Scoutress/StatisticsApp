package com.scoutress.KaimuxAdminStats.servicesImpl.discordMessages;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scoutress.KaimuxAdminStats.entity.discordMessages.AverageDiscordMessagesCompared;
import com.scoutress.KaimuxAdminStats.entity.discordMessages.DailyDiscordMessages;
import com.scoutress.KaimuxAdminStats.entity.discordMessages.DailyDiscordMessagesCompared;
import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.repositories.discordMessages.AverageDiscordMessagesComparedRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordMessages.DailyDiscordMessagesComparedRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.services.discordMessages.DiscordMessagesComparedService;

@Service
public class DiscordMessagesComparedServiceImp implements DiscordMessagesComparedService {

  private static final Logger log = LoggerFactory.getLogger(DiscordMessagesComparedServiceImp.class);

  private final DailyDiscordMessagesComparedRepository dailyDiscordMessagesComparedRepository;
  private final AverageDiscordMessagesComparedRepository averageDiscordMessagesComparedRepository;
  private final EmployeeRepository employeeRepository;

  public DiscordMessagesComparedServiceImp(
      DailyDiscordMessagesComparedRepository dailyDiscordMessagesComparedRepository,
      AverageDiscordMessagesComparedRepository averageDiscordMessagesComparedRepository,
      EmployeeRepository employeeRepository) {
    this.dailyDiscordMessagesComparedRepository = dailyDiscordMessagesComparedRepository;
    this.averageDiscordMessagesComparedRepository = averageDiscordMessagesComparedRepository;
    this.employeeRepository = employeeRepository;
  }

  @Override
  @Transactional
  public void compareEachEmployeeDailyDiscordMessagesValues(
      List<DailyDiscordMessages> allDailyDcMessages,
      List<Short> allEmployeesFromDailyDcMessages,
      List<Short> employeeIdsWithoutData) {

    log.info("=== [START] Discord messages comparison (multithreaded) ===");
    long globalStart = System.currentTimeMillis();

    if (allDailyDcMessages == null || allDailyDcMessages.isEmpty()) {
      log.warn("⚠ No Discord messages found — skipping comparison.");
      return;
    }

    if (allEmployeesFromDailyDcMessages == null || allEmployeesFromDailyDcMessages.isEmpty()) {
      log.warn("⚠ No employees found in Discord messages dataset.");
      return;
    }

    log.debug("Pre-grouping data for {} messages...", allDailyDcMessages.size());

    // ================================
    // Pre-group data in memory
    // ================================
    Map<Short, List<DailyDiscordMessages>> messagesByEmployee = allDailyDcMessages
        .stream()
        .collect(Collectors.groupingBy(DailyDiscordMessages::getEmployeeId));

    Map<LocalDate, Integer> totalMessagesByDate = allDailyDcMessages
        .stream()
        .collect(Collectors.groupingBy(DailyDiscordMessages::getDate,
            Collectors.summingInt(DailyDiscordMessages::getMsgCount)));

    log.debug("Data grouped: {} employees, {} unique dates.",
        messagesByEmployee.size(), totalMessagesByDate.size());

    Queue<DailyDiscordMessagesCompared> dailyBatch = new ConcurrentLinkedQueue<>();
    Queue<AverageDiscordMessagesCompared> avgBatch = new ConcurrentLinkedQueue<>();

    long start = System.currentTimeMillis();

    allEmployeesFromDailyDcMessages
        .parallelStream()
        .filter(id -> !employeeIdsWithoutData.contains(id))
        .forEach(employeeId -> {
          log.debug("➡ Processing employee ID {} ...", employeeId);
          try {
            processEmployee(employeeId, messagesByEmployee, totalMessagesByDate, dailyBatch, avgBatch);
          } catch (Exception e) {
            log.error("❌ Error processing employee {}: {}", employeeId, e.getMessage(), e);
          }
        });

    // ================================
    // Save results in batches
    // ================================
    log.info("💾 Saving {} daily ratios and {} averages...", dailyBatch.size(), avgBatch.size());

    if (!dailyBatch.isEmpty()) {
      log.trace("Persisting DailyDiscordMessagesCompared records...");
      dailyDiscordMessagesComparedRepository.saveAll(dailyBatch);
      log.trace("✔ {} DailyDiscordMessagesCompared saved.", dailyBatch.size());
    }

    if (!avgBatch.isEmpty()) {
      log.trace("Persisting AverageDiscordMessagesCompared records...");
      averageDiscordMessagesComparedRepository.saveAll(avgBatch);
      log.trace("✔ {} AverageDiscordMessagesCompared saved.", avgBatch.size());
    }

    long elapsed = System.currentTimeMillis() - start;
    log.info("✅ Discord messages comparison completed in {} ms ({} s)",
        elapsed, elapsed / 1000.0);
    log.info("=== [END] Total duration: {} ms ===", (System.currentTimeMillis() - globalStart));
  }

  private void processEmployee(
      Short employeeId,
      Map<Short, List<DailyDiscordMessages>> messagesByEmployee,
      Map<LocalDate, Integer> totalMessagesByDate,
      Queue<DailyDiscordMessagesCompared> dailyBatch,
      Queue<AverageDiscordMessagesCompared> avgBatch) {

    LocalDate joinDate = getJoinDateThisEmployee(employeeId);
    List<DailyDiscordMessages> empMessages = messagesByEmployee.get(employeeId);

    log.trace("Employee {} -> Join date: {}, Messages count: {}", employeeId, joinDate,
        empMessages == null ? 0 : empMessages.size());

    if (empMessages == null || empMessages.isEmpty() || joinDate == null) {
      log.debug("Skipping employee {} (no data or no join date).", employeeId);
      return;
    }

    LocalDate oldestDate = empMessages
        .stream()
        .map(DailyDiscordMessages::getDate)
        .min(Comparator.naturalOrder())
        .orElse(joinDate);

    LocalDate startDate = joinDate.isAfter(oldestDate) ? joinDate : oldestDate;
    LocalDate today = LocalDate.now();

    log.trace("Employee {} -> Oldest date: {}, Start date: {}, Today: {}",
        employeeId, oldestDate, startDate, today);

    double sumRatio = 0;
    int count = 0;

    for (LocalDate date = startDate; date.isBefore(today); date = date.plusDays(1)) {
      final LocalDate currentDate = date;

      int empCount = empMessages
          .stream()
          .filter(m -> m.getDate().equals(currentDate))
          .mapToInt(DailyDiscordMessages::getMsgCount)
          .sum();

      int totalCount = totalMessagesByDate.getOrDefault(currentDate, 0);

      log.trace("Employee {} - Date {}: empCount={}, totalCount={}",
          employeeId, currentDate, empCount, totalCount);

      if (totalCount == 0) {
        log.trace("Skipping date {} for employee {} (totalCount=0)", currentDate, employeeId);
        continue;
      }

      double ratio = (double) empCount / totalCount;
      sumRatio += ratio;
      count++;

      DailyDiscordMessagesCompared dailyRecord = new DailyDiscordMessagesCompared();
      dailyRecord.setEmployeeId(employeeId);
      dailyRecord.setDate(currentDate);
      dailyRecord.setValue(ratio);

      dailyBatch.add(dailyRecord);
      log.trace("Added DailyRecord -> empId={}, date={}, ratio={}", employeeId, currentDate, ratio);
    }

    if (count > 0) {
      double avg = sumRatio / count;

      AverageDiscordMessagesCompared avgRecord = new AverageDiscordMessagesCompared();
      avgRecord.setEmployeeId(employeeId);
      avgRecord.setValue(avg);
      avgBatch.add(avgRecord);

      log.debug("Employee {}: Processed {} days, avgRatio={}", employeeId, count, avg);
    } else {
      log.debug("Employee {}: No valid days processed (count=0)", employeeId);
    }
  }

  private LocalDate getJoinDateThisEmployee(Short employeeId) {
    LocalDate joinDate = employeeRepository
        .findById(employeeId)
        .map(Employee::getJoinDate)
        .orElse(null);
    log.trace("Employee {} join date fetched: {}", employeeId, joinDate);
    return joinDate;
  }
}
