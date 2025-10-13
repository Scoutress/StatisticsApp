package com.scoutress.KaimuxAdminStats.servicesImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.discordTickets.DailyDiscordTickets;
import com.scoutress.KaimuxAdminStats.entity.discordTickets.DiscordTicketsRawData;
import com.scoutress.KaimuxAdminStats.entity.discordTickets.DiscordTicketsReactions;
import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;
import com.scoutress.KaimuxAdminStats.repositories.discordTickets.DailyDiscordTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordTickets.DiscordTicketsRawDataRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordTickets.DiscordTicketsReactionsRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeCodesRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.services.DiscordTicketsService;

import jakarta.transaction.Transactional;

@Service
public class DiscordTicketsServiceImpl implements DiscordTicketsService {

  private static final Logger log = LoggerFactory.getLogger(DiscordTicketsServiceImpl.class);
  private static final int BATCH_SIZE = 1000;

  private final DiscordTicketsReactionsRepository discordTicketsReactionsRepository;
  private final EmployeeCodesRepository employeeCodesRepository;
  private final DiscordTicketsRawDataRepository discordTicketsRawDataRepository;
  private final EmployeeRepository employeeRepository;
  private final ApiDataExtractionServiceImpl apiDataExtractionServiceImpl;
  private final DailyDiscordTicketsRepository dailyDiscordTicketsRepository;

  public DiscordTicketsServiceImpl(
      DiscordTicketsReactionsRepository discordTicketsReactionsRepository,
      EmployeeCodesRepository employeeCodesRepository,
      DiscordTicketsRawDataRepository discordTicketsRawDataRepository,
      EmployeeRepository employeeRepository,
      ApiDataExtractionServiceImpl apiDataExtractionServiceImpl,
      DailyDiscordTicketsRepository dailyDiscordTicketsRepository) {
    this.discordTicketsReactionsRepository = discordTicketsReactionsRepository;
    this.employeeCodesRepository = employeeCodesRepository;
    this.discordTicketsRawDataRepository = discordTicketsRawDataRepository;
    this.employeeRepository = employeeRepository;
    this.apiDataExtractionServiceImpl = apiDataExtractionServiceImpl;
    this.dailyDiscordTicketsRepository = dailyDiscordTicketsRepository;
  }

  @Override
  public void processDiscordTickets() {
    long startTime = System.currentTimeMillis();
    log.info("=== [START] Discord tickets data processing ===");

    removeOldProcessedDcTicketsData();

    List<EmployeeCodes> employeeCodes = employeeCodesRepository.findAll();

    List<Short> employeeIds = employeeCodes
        .stream()
        .map(EmployeeCodes::getEmployeeId)
        .distinct()
        .toList();

    List<DiscordTicketsRawData> allRawData = discordTicketsRawDataRepository.findAll();

    if (employeeIds.isEmpty()) {
      log.warn("⚠ No employees found — skipping Discord tickets processing.");
      return;
    }

    List<LocalDate> allLatestDates = new ArrayList<>();
    for (Short employeeId : employeeIds) {
      try {
        Short employeeCode = getEmployeeCodeByEmployeeId(employeeCodes, employeeId);
        LocalDate latestDateForEmployee = determineLatestDateForEmployee(employeeId, employeeCode, allRawData);

        if (latestDateForEmployee != null) {
          allLatestDates.add(latestDateForEmployee);
        }
      } catch (Exception e) {
        log.error("❌ Error processing employee ID {}: {}", employeeId, e.getMessage(), e);
      }
    }

    LocalDate oldestDate = allLatestDates.stream().min(LocalDate::compareTo).orElse(LocalDate.now());
    log.info("📅 Starting extraction from oldest date: {}", oldestDate);

    apiDataExtractionServiceImpl.extractDiscordTicketsFromAPI(oldestDate);
    moveDcTicketsReactionsToRawData();

    removeDcTicketsRawDataDuplicates();

    List<DiscordTicketsRawData> updatedRawData = discordTicketsRawDataRepository.findAll();
    processDailyDiscordTickets(updatedRawData);

    removeDailyDiscordTicketsDuplicates();

    long totalMs = System.currentTimeMillis() - startTime;
    log.info("✅ Discord tickets processing completed in {} ms", totalMs);
  }

  private void removeOldProcessedDcTicketsData() {
    log.info("🧹 Clearing temporary reactions table...");
    discordTicketsReactionsRepository.truncateTable();
  }

  private Short getEmployeeCodeByEmployeeId(List<EmployeeCodes> employeeCodes, Short employeeId) {
    return employeeCodes
        .stream()
        .filter(c -> c.getEmployeeId().equals(employeeId))
        .map(EmployeeCodes::getKmxWebApi)
        .findFirst()
        .orElse(null);
  }

  private LocalDate determineLatestDateForEmployee(Short employeeId, Short employeeCode,
      List<DiscordTicketsRawData> allRawData) {
    if (employeeCode == null)
      return null;

    boolean hasData = allRawData
        .stream()
        .anyMatch(d -> d.getDiscordId() != null && d.getDiscordId().equals(employeeCode.longValue()));

    if (hasData) {
      LocalDateTime latestDateTime = allRawData
          .stream()
          .filter(d -> d.getDiscordId() != null && d.getDiscordId().equals(employeeCode.longValue()))
          .map(DiscordTicketsRawData::getDateTime)
          .filter(Objects::nonNull)
          .max(LocalDateTime::compareTo)
          .orElse(null);
      return latestDateTime != null ? latestDateTime.toLocalDate() : null;
    }

    LocalDate joinDate = employeeRepository
        .findAll()
        .stream()
        .filter(e -> e.getId().equals(employeeId))
        .map(Employee::getJoinDate)
        .findFirst()
        .orElse(null);

    if (joinDate == null) {
      log.warn("⚠ Employee {} has no join date — skipping.", employeeId);
      return null;
    }

    return joinDate.minusDays(1);
  }

  private void moveDcTicketsReactionsToRawData() {
    List<DiscordTicketsReactions> reactions = discordTicketsReactionsRepository.findAll();

    if (reactions.isEmpty()) {
      log.info("No new Discord reactions to move.");
      return;
    }

    List<DiscordTicketsRawData> batch = new ArrayList<>();
    for (DiscordTicketsReactions reaction : reactions) {
      DiscordTicketsRawData raw = new DiscordTicketsRawData();
      raw.setDiscordId(reaction.getDiscordId());
      raw.setTicketId(reaction.getTicketId());
      raw.setDateTime(reaction.getDateTime());
      batch.add(raw);

      if (batch.size() >= BATCH_SIZE) {
        discordTicketsRawDataRepository.saveAll(batch);
        batch.clear();
      }
    }
    if (!batch.isEmpty()) {
      discordTicketsRawDataRepository.saveAll(batch);
    }

    discordTicketsReactionsRepository.deleteAll();
    log.info("✅ Moved {} Discord reactions to raw data.", reactions.size());
  }

  @Transactional
  private void removeDcTicketsRawDataDuplicates() {
    log.info("🧩 Removing duplicate raw Discord ticket entries...");
    List<DiscordTicketsRawData> all = discordTicketsRawDataRepository.findAll();

    Map<String, List<DiscordTicketsRawData>> grouped = all
        .stream()
        .collect(Collectors.groupingBy(d -> d.getDiscordId() + "-" +
            (d.getTicketId() != null ? d.getTicketId() : "null") + "-" +
            (d.getDateTime() != null ? d.getDateTime().toString() : "null")));

    int duplicates = 0;
    for (List<DiscordTicketsRawData> group : grouped.values()) {
      if (group.size() > 1) {
        group.stream()
            .sorted(Comparator.comparing(DiscordTicketsRawData::getId))
            .skip(1)
            .forEach(entry -> {
              discordTicketsRawDataRepository.delete(entry);
            });
        duplicates += group.size() - 1;
      }
    }

    log.info("✅ Removed {} duplicate raw Discord entries.", duplicates);
  }

  private void processDailyDiscordTickets(List<DiscordTicketsRawData> rawData) {
    log.info("📊 Aggregating Discord tickets per day...");

    Map<Long, List<DiscordTicketsRawData>> byDiscordId = rawData
        .stream()
        .filter(r -> r.getDiscordId() != null && r.getDateTime() != null)
        .collect(Collectors.groupingBy(DiscordTicketsRawData::getDiscordId));

    int totalEntries = 0;
    List<DailyDiscordTickets> batch = new ArrayList<>();

    for (Map.Entry<Long, List<DiscordTicketsRawData>> entry : byDiscordId.entrySet()) {
      Long discordId = entry.getKey();
      Short employeeId = getEmployeeIdByDiscordId(discordId);
      if (employeeId == null)
        continue;

      Map<LocalDate, Long> dailyCounts = entry
          .getValue()
          .stream()
          .collect(Collectors.groupingBy(r -> r.getDateTime().toLocalDate(), Collectors.counting()));

      for (Map.Entry<LocalDate, Long> dateEntry : dailyCounts.entrySet()) {
        DailyDiscordTickets daily = new DailyDiscordTickets();
        daily.setEmployeeId(employeeId);
        daily.setDate(dateEntry.getKey());
        daily.setDcTicketCount(dateEntry.getValue().intValue());
        batch.add(daily);
        totalEntries++;

        if (batch.size() >= BATCH_SIZE) {
          dailyDiscordTicketsRepository.saveAll(batch);
          batch.clear();
        }
      }
    }

    if (!batch.isEmpty())
      dailyDiscordTicketsRepository.saveAll(batch);
    log.info("✅ Created {} daily Discord ticket records.", totalEntries);
  }

  private Short getEmployeeIdByDiscordId(Long discordId) {
    return employeeCodesRepository
        .findAll()
        .stream()
        .filter(code -> code.getDiscordUserId() != null &&
            code.getKmxWebApi().longValue() == discordId.longValue())
        .map(EmployeeCodes::getEmployeeId)
        .findFirst()
        .orElse(null);
  }

  @Transactional
  private void removeDailyDiscordTicketsDuplicates() {
    log.info("🧩 Removing duplicate daily Discord ticket entries...");
    List<DailyDiscordTickets> all = dailyDiscordTicketsRepository.findAll();

    Map<String, List<DailyDiscordTickets>> grouped = all
        .stream()
        .collect(Collectors.groupingBy(d -> d.getEmployeeId() + "-" +
            (d.getDate() != null ? d.getDate() : "null") + "-" +
            (d.getDcTicketCount() != null ? d.getDcTicketCount() : "null")));

    int duplicates = 0;
    for (List<DailyDiscordTickets> group : grouped.values()) {
      if (group.size() > 1) {
        group
            .stream()
            .sorted(Comparator.comparing(DailyDiscordTickets::getId))
            .skip(1)
            .forEach(entry -> {
              dailyDiscordTicketsRepository.delete(entry);
            });
        duplicates += group.size() - 1;
      }
    }

    log.info("✅ Removed {} duplicate daily ticket entries.", duplicates);
  }
}
