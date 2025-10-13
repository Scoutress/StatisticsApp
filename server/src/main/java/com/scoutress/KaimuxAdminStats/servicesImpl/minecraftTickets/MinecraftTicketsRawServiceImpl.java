package com.scoutress.KaimuxAdminStats.servicesImpl.minecraftTickets;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.DailyMinecraftTickets;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.McTicketsLastCheck;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.MinecraftTicketsAnswers;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.TotalOldMinecraftTickets;
import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeCodesRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.DailyMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.McTicketsLastCheckRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.MinecraftTicketsAnswersRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.TotalOldMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.DailyPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.services.minecraftTickets.MinecraftTicketsRawService;
import com.scoutress.KaimuxAdminStats.servicesImpl.ApiDataExtractionServiceImpl;
import com.scoutress.KaimuxAdminStats.servicesImpl.DuplicatesRemoverServiceImpl;

@Service
public class MinecraftTicketsRawServiceImpl implements MinecraftTicketsRawService {

  private static final Logger log = LoggerFactory.getLogger(MinecraftTicketsRawServiceImpl.class);

  private final MinecraftTicketsAnswersRepository minecraftTicketsAnswersRepository;
  private final ApiDataExtractionServiceImpl apiDataExtractionServiceImpl;
  private final DailyMinecraftTicketsRepository dailyMinecraftTicketsRepository;
  private final MinecraftTicketsServiceImpl minecraftTicketsServiceImpl;
  private final EmployeeCodesRepository employeeCodesRepository;
  private final EmployeeRepository employeeRepository;
  private final DailyPlaytimeRepository dailyPlaytimeRepository;
  private final MinecraftTicketsComparedServiceImpl minecraftTicketsComparedServiceImpl;
  private final TotalOldMinecraftTicketsRepository totalOldMinecraftTicketsRepository;
  private final DuplicatesRemoverServiceImpl duplicatesRemoverServiceImpl;
  private final McTicketsLastCheckRepository mcTicketsLastCheckRepository;

  public MinecraftTicketsRawServiceImpl(
      MinecraftTicketsAnswersRepository minecraftTicketsAnswersRepository,
      ApiDataExtractionServiceImpl apiDataExtractionServiceImpl,
      DailyMinecraftTicketsRepository dailyMinecraftTicketsRepository,
      MinecraftTicketsServiceImpl minecraftTicketsServiceImpl,
      EmployeeCodesRepository employeeCodesRepository,
      EmployeeRepository employeeRepository,
      DailyPlaytimeRepository dailyPlaytimeRepository,
      MinecraftTicketsComparedServiceImpl minecraftTicketsComparedServiceImpl,
      TotalOldMinecraftTicketsRepository totalOldMinecraftTicketsRepository,
      DuplicatesRemoverServiceImpl duplicatesRemoverServiceImpl,
      McTicketsLastCheckRepository mcTicketsLastCheckRepository) {
    this.minecraftTicketsAnswersRepository = minecraftTicketsAnswersRepository;
    this.apiDataExtractionServiceImpl = apiDataExtractionServiceImpl;
    this.dailyMinecraftTicketsRepository = dailyMinecraftTicketsRepository;
    this.minecraftTicketsServiceImpl = minecraftTicketsServiceImpl;
    this.employeeCodesRepository = employeeCodesRepository;
    this.employeeRepository = employeeRepository;
    this.dailyPlaytimeRepository = dailyPlaytimeRepository;
    this.minecraftTicketsComparedServiceImpl = minecraftTicketsComparedServiceImpl;
    this.totalOldMinecraftTicketsRepository = totalOldMinecraftTicketsRepository;
    this.duplicatesRemoverServiceImpl = duplicatesRemoverServiceImpl;
    this.mcTicketsLastCheckRepository = mcTicketsLastCheckRepository;
  }

  @Override
  public void handleMinecraftTickets() {
    log.info("=== Starting Minecraft Tickets data handling process ===");

    try {
      removeRawMcTicketsData();

      List<EmployeeCodes> employeeCodes = extractEmployeeCodes();
      List<Short> allEmployeeIds = getAllEmployeeIdsFromEmployeeCodes(employeeCodes);
      LocalDate newestDateFromDailyMcTickets = getNewestDateFromDailyMcTickets();
      List<DailyMinecraftTickets> allDailyMcTickets = getDailyMinecraftTicketsData();

      log.debug("Loaded {} employee codes and {} daily ticket entries.", employeeCodes.size(),
          allDailyMcTickets.size());

      List<LocalDate> joinDates = new ArrayList<>();
      int processed = 0;

      for (Short employeeId : allEmployeeIds) {
        boolean hasEmployeeData = hasEmployeeMcTicketsData(employeeId, allDailyMcTickets);

        if (!hasEmployeeData) {
          boolean wasChecked = wasEmployeeCheckedBefore(employeeId);

          if (!wasChecked) {
            LocalDate joinDate = getJoinDateOfEmployeeWithoutData(employeeId);
            if (joinDate != null)
              joinDates.add(joinDate);
          }
        }

        processed++;
        if (processed % 10 == 0 || processed == allEmployeeIds.size()) {
          int percent = (int) ((processed / (double) allEmployeeIds.size()) * 100);
          log.info("Progress: Processed {}/{} employees ({}%)", processed, allEmployeeIds.size(), percent);
        }
      }

      if (!joinDates.isEmpty()) {
        LocalDate oldestJoinDate = getOldestJoinDate(joinDates);
        log.info("Oldest join date found: {} — extracting data from API...", oldestJoinDate);
        apiDataExtractionServiceImpl.extractMinecraftTicketsFromAPI(oldestJoinDate);
      } else {
        log.info("No missing join dates — extracting data from latest known date: {}", newestDateFromDailyMcTickets);
        apiDataExtractionServiceImpl.extractMinecraftTicketsFromAPI(newestDateFromDailyMcTickets);
      }

      updateMcTicketsLastCheck(allEmployeeIds);

      List<MinecraftTicketsAnswers> rawMcTicketsData = extractRawMcTicketsData();
      log.debug("Fetched {} raw Minecraft ticket entries from API.", rawMcTicketsData.size());
      minecraftTicketsServiceImpl.convertRawMcTicketsData(rawMcTicketsData, employeeCodes, allEmployeeIds);

      duplicatesRemoverServiceImpl.removeDuplicatesFromDailyMcTickets();

      List<DailyMinecraftTickets> rawDailyMcTicketsData = getDailyMinecraftTicketsData();
      List<Employee> rawEmployeesData = getEmployeesData();
      LocalDate oldestDateFromData = checkForOldestDate(rawDailyMcTicketsData);

      minecraftTicketsServiceImpl.calcAvgDailyMcTicketsPerEmployee(allEmployeeIds, rawEmployeesData, oldestDateFromData,
          rawDailyMcTicketsData);
      duplicatesRemoverServiceImpl.removeDuplicatesFromAvgDailyMcTickets();

      List<DailyPlaytime> allPlaytimeData = getAllPlaytimeData();
      minecraftTicketsServiceImpl.calcAvgMcTicketsPerPlaytime(rawDailyMcTicketsData, rawEmployeesData, allEmployeeIds,
          oldestDateFromData, allPlaytimeData);
      duplicatesRemoverServiceImpl.removeDuplicatesFromMcTicketsPerPlaytime();

      List<TotalOldMinecraftTickets> allOldTotalMinecraftTicketsData = getOldTotalDailyMinecraftTicketsData();
      minecraftTicketsServiceImpl.calcTotalMinecraftTickets(allEmployeeIds, rawDailyMcTicketsData,
          allOldTotalMinecraftTicketsData);
      duplicatesRemoverServiceImpl.removeDuplicatesFromTotalMcTickets();

      List<LocalDate> allDatesFromDailyMcTickets = getAllMinecraftTicketsDates(rawDailyMcTicketsData);
      List<Short> allEmployeesFromDailyMcTickets = getAllEmployeesFromDailyMinecraftTickets(rawDailyMcTicketsData);
      minecraftTicketsComparedServiceImpl.compareEachEmployeeDailyMcTicketsValues(rawDailyMcTicketsData,
          allPlaytimeData, allDatesFromDailyMcTickets, allEmployeesFromDailyMcTickets);
      duplicatesRemoverServiceImpl.removeDuplicatesFromComparedMcTickets();

      log.info("✅ Minecraft Tickets data processing completed successfully.");

    } catch (Exception e) {
      log.error("❌ Critical error while handling Minecraft tickets: {}", e.getMessage(), e);
    }
  }

  // ======================================================
  // SUPPORTING METHODS WITH LIGHT LOGGING
  // ======================================================

  private void removeRawMcTicketsData() {
    try {
      minecraftTicketsAnswersRepository
          .truncateTable();
      log.info("Cleared raw Minecraft ticket table.");
    } catch (Exception e) {
      log.error("Failed to truncate Minecraft tickets table: {}", e.getMessage(), e);
    }
  }

  private LocalDate getNewestDateFromDailyMcTickets() {
    return dailyMinecraftTicketsRepository
        .findAll()
        .stream()
        .map(DailyMinecraftTickets::getDate)
        .max(LocalDate::compareTo)
        .orElse(LocalDate.parse("1970-01-01"));
  }

  private boolean hasEmployeeMcTicketsData(Short employeeId, List<DailyMinecraftTickets> allDailyMcTickets) {
    return allDailyMcTickets
        .stream()
        .filter(e -> e.getEmployeeId().equals(employeeId))
        .anyMatch(e -> e.getDate() != null && e.getTicketCount() > 0);
  }

  private LocalDate getJoinDateOfEmployeeWithoutData(Short employeeId) {
    return employeeRepository
        .findById(employeeId)
        .map(Employee::getJoinDate)
        .filter(Objects::nonNull)
        .orElseGet(() -> {
          log.warn("⚠️ Employee {} has no join date — skipping.", employeeId);
          return null;
        });
  }

  private LocalDate getOldestJoinDate(List<LocalDate> joinDates) {
    LocalDate oldest = joinDates
        .stream()
        .filter(Objects::nonNull)
        .min(LocalDate::compareTo)
        .orElse(null);
    if (oldest == null)
      log.warn("⚠️ No valid join dates found — skipping API extraction.");
    return oldest;
  }

  private boolean wasEmployeeCheckedBefore(Short employeeId) {
    return mcTicketsLastCheckRepository
        .findAll()
        .stream()
        .filter(e -> employeeId.equals(e.getEmployeeId()))
        .anyMatch(e -> e.getDate() != null);
  }

  private List<MinecraftTicketsAnswers> extractRawMcTicketsData() {
    return minecraftTicketsAnswersRepository
        .findAll();
  }

  private void updateMcTicketsLastCheck(List<Short> allEmployeeIds) {
    mcTicketsLastCheckRepository
        .deleteAll();
    allEmployeeIds.forEach(id -> mcTicketsLastCheckRepository.save(new McTicketsLastCheck(id, LocalDate.now())));
    log.info("Updated {} employee records in McTicketsLastCheck.", allEmployeeIds.size());
  }

  private List<EmployeeCodes> extractEmployeeCodes() {
    return employeeCodesRepository
        .findAll();
  }

  private List<Short> getAllEmployeeIdsFromEmployeeCodes(List<EmployeeCodes> employeeCodes) {
    return employeeCodes
        .stream()
        .map(EmployeeCodes::getEmployeeId)
        .distinct()
        .collect(Collectors.toList());
  }

  private List<DailyMinecraftTickets> getDailyMinecraftTicketsData() {
    return dailyMinecraftTicketsRepository
        .findAll();
  }

  private List<Employee> getEmployeesData() {
    return employeeRepository
        .findAll();
  }

  private LocalDate checkForOldestDate(List<DailyMinecraftTickets> rawData) {
    return rawData
        .stream()
        .map(DailyMinecraftTickets::getDate)
        .min(LocalDate::compareTo)
        .orElse(LocalDate.of(2100, 1, 1));
  }

  private List<DailyPlaytime> getAllPlaytimeData() {
    return dailyPlaytimeRepository
        .findAll();
  }

  private List<TotalOldMinecraftTickets> getOldTotalDailyMinecraftTicketsData() {
    return totalOldMinecraftTicketsRepository
        .findAll();
  }

  private List<LocalDate> getAllMinecraftTicketsDates(List<DailyMinecraftTickets> data) {
    return data
        .stream()
        .map(DailyMinecraftTickets::getDate)
        .distinct()
        .collect(Collectors.toList());
  }

  private List<Short> getAllEmployeesFromDailyMinecraftTickets(List<DailyMinecraftTickets> data) {
    return data
        .stream()
        .map(DailyMinecraftTickets::getEmployeeId)
        .distinct()
        .collect(Collectors.toList());
  }
}
