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
    log.info("=== [START] Minecraft Tickets data handling process ===");
    long startTime = System.currentTimeMillis();

    try {
      // ======================================================
      // 1. Išvalyti senus duomenis
      // ======================================================
      removeRawMcTicketsData();

      // ======================================================
      // 2. Paruošti pradinius duomenis
      // ======================================================
      List<EmployeeCodes> employeeCodes = extractEmployeeCodes();
      List<Short> allEmployeeIds = getAllEmployeeIdsFromEmployeeCodes(employeeCodes);
      LocalDate newestDateFromDailyMcTickets = getNewestDateFromDailyMcTickets();
      List<DailyMinecraftTickets> allDailyMcTickets = getDailyMinecraftTicketsData();

      log.debug("Loaded {} employee codes, {} employee IDs, {} daily tickets (latest date {}).",
          employeeCodes.size(), allEmployeeIds.size(), allDailyMcTickets.size(), newestDateFromDailyMcTickets);

      // ======================================================
      // 3. Patikrinti darbuotojus be duomenų
      // ======================================================
      List<LocalDate> joinDates = new ArrayList<>();
      int processed = 0;
      int total = allEmployeeIds.size();

      for (Short employeeId : allEmployeeIds) {
        boolean hasEmployeeData = hasEmployeeMcTicketsData(employeeId, allDailyMcTickets);
        log.trace("Employee {} → hasData={}", employeeId, hasEmployeeData);

        if (!hasEmployeeData) {
          boolean wasChecked = wasEmployeeCheckedBefore(employeeId);
          log.trace("Employee {} → wasCheckedBefore={}", employeeId, wasChecked);

          if (!wasChecked) {
            LocalDate joinDate = getJoinDateOfEmployeeWithoutData(employeeId);
            if (joinDate != null) {
              joinDates.add(joinDate);
              log.debug("Employee {} → join date {} added to API extraction list.", employeeId, joinDate);
            }
          }
        }

        processed++;
        if (processed % 10 == 0 || processed == total) {
          int percent = (int) ((processed / (double) total) * 100);
          log.info("Progress: {}/{} employees checked ({}%)", processed, total, percent);
        }
      }

      // ======================================================
      // 4. Išgauti duomenis iš API
      // ======================================================
      if (!joinDates.isEmpty()) {
        LocalDate oldestJoinDate = getOldestJoinDate(joinDates);
        log.info("🗓 Oldest join date found: {} — extracting Minecraft tickets from API...", oldestJoinDate);
        apiDataExtractionServiceImpl.extractMinecraftTicketsFromAPI(oldestJoinDate);
      } else {
        log.info("No join dates missing — extracting from latest known date: {}", newestDateFromDailyMcTickets);
        apiDataExtractionServiceImpl.extractMinecraftTicketsFromAPI(newestDateFromDailyMcTickets);
      }

      // ======================================================
      // 5. Atnaujinti paskutinį tikrinimą
      // ======================================================
      updateMcTicketsLastCheck(allEmployeeIds);

      // ======================================================
      // 6. Konvertuoti naujus duomenis
      // ======================================================
      List<MinecraftTicketsAnswers> rawMcTicketsData = extractRawMcTicketsData();
      log.debug("Fetched {} raw Minecraft ticket entries.", rawMcTicketsData.size());
      rawMcTicketsData.forEach(raw -> log.trace("RAW → empCode={}, date={}",
          raw.getKmxWebApiMcTickets(), raw.getDateTime()));

      minecraftTicketsServiceImpl.convertRawMcTicketsData(rawMcTicketsData, employeeCodes, allEmployeeIds);
      log.info("Converted raw Minecraft ticket data into daily records.");

      // ======================================================
      // 7. Išvalyti pasikartojimus
      // ======================================================
      duplicatesRemoverServiceImpl.removeDuplicatesFromDailyMcTickets();

      // ======================================================
      // 8. Skaičiuoti vidurkius, palyginimus ir bendrą statistiką
      // ======================================================
      List<DailyMinecraftTickets> rawDailyMcTicketsData = getDailyMinecraftTicketsData();
      List<Employee> rawEmployeesData = getEmployeesData();
      LocalDate oldestDateFromData = checkForOldestDate(rawDailyMcTicketsData);
      log.debug("Oldest date from daily tickets dataset: {}", oldestDateFromData);

      minecraftTicketsServiceImpl.calcAvgDailyMcTicketsPerEmployee(allEmployeeIds, rawEmployeesData,
          oldestDateFromData, rawDailyMcTicketsData);
      duplicatesRemoverServiceImpl.removeDuplicatesFromAvgDailyMcTickets();

      List<DailyPlaytime> allPlaytimeData = getAllPlaytimeData();
      log.debug("Loaded {} playtime entries.", allPlaytimeData.size());

      minecraftTicketsServiceImpl.calcAvgMcTicketsPerPlaytime(rawDailyMcTicketsData, rawEmployeesData, allEmployeeIds,
          oldestDateFromData, allPlaytimeData);
      duplicatesRemoverServiceImpl.removeDuplicatesFromMcTicketsPerPlaytime();

      List<TotalOldMinecraftTickets> allOldTotalMinecraftTicketsData = getOldTotalDailyMinecraftTicketsData();
      minecraftTicketsServiceImpl.calcTotalMinecraftTickets(allEmployeeIds, rawDailyMcTicketsData,
          allOldTotalMinecraftTicketsData);
      duplicatesRemoverServiceImpl.removeDuplicatesFromTotalMcTickets();

      List<LocalDate> allDatesFromDailyMcTickets = getAllMinecraftTicketsDates(rawDailyMcTicketsData);
      List<Short> allEmployeesFromDailyMcTickets = getAllEmployeesFromDailyMinecraftTickets(rawDailyMcTicketsData);

      minecraftTicketsComparedServiceImpl.compareEachEmployeeDailyMcTicketsValues(
          rawDailyMcTicketsData, allPlaytimeData, allDatesFromDailyMcTickets, allEmployeesFromDailyMcTickets);
      duplicatesRemoverServiceImpl.removeDuplicatesFromComparedMcTickets();

      // ======================================================
      // 9. Baigta
      // ======================================================
      long elapsed = System.currentTimeMillis() - startTime;
      log.info("✅ [END] Minecraft Tickets data processing completed successfully in {} ms ({} s).",
          elapsed, elapsed / 1000.0);

    } catch (Exception e) {
      log.error("❌ Critical error while handling Minecraft tickets: {}", e.getMessage(), e);
    }
  }

  // ======================================================
  // SUPPORTING METHODS (praplėstas logavimas)
  // ======================================================

  private void removeRawMcTicketsData() {
    try {
      minecraftTicketsAnswersRepository.truncateTable();
      log.info("🧹 Cleared raw Minecraft ticket table before import.");
    } catch (Exception e) {
      log.error("❌ Failed to truncate Minecraft tickets table: {}", e.getMessage(), e);
    }
  }

  private LocalDate getNewestDateFromDailyMcTickets() {
    LocalDate date = dailyMinecraftTicketsRepository.findAll()
        .stream()
        .map(DailyMinecraftTickets::getDate)
        .max(LocalDate::compareTo)
        .orElse(LocalDate.parse("1970-01-01"));
    log.trace("Newest date from DailyMinecraftTickets: {}", date);
    return date;
  }

  private boolean hasEmployeeMcTicketsData(Short employeeId, List<DailyMinecraftTickets> allDailyMcTickets) {
    boolean result = allDailyMcTickets
        .stream()
        .filter(e -> e.getEmployeeId().equals(employeeId))
        .anyMatch(e -> e.getDate() != null && e.getTicketCount() > 0);
    log.trace("Employee {} → hasData={}", employeeId, result);
    return result;
  }

  private LocalDate getJoinDateOfEmployeeWithoutData(Short employeeId) {
    return employeeRepository.findById(employeeId)
        .map(Employee::getJoinDate)
        .filter(Objects::nonNull)
        .orElseGet(() -> {
          log.warn("⚠️ Employee {} has no join date — skipping.", employeeId);
          return null;
        });
  }

  private LocalDate getOldestJoinDate(List<LocalDate> joinDates) {
    LocalDate oldest = joinDates.stream()
        .filter(Objects::nonNull)
        .min(LocalDate::compareTo)
        .orElse(null);
    if (oldest == null)
      log.warn("⚠️ No valid join dates found for API extraction.");
    else
      log.trace("Oldest join date from list: {}", oldest);
    return oldest;
  }

  private boolean wasEmployeeCheckedBefore(Short employeeId) {
    boolean result = mcTicketsLastCheckRepository.findAll()
        .stream()
        .filter(e -> employeeId.equals(e.getEmployeeId()))
        .anyMatch(e -> e.getDate() != null);
    log.trace("Employee {} previously checked: {}", employeeId, result);
    return result;
  }

  private List<MinecraftTicketsAnswers> extractRawMcTicketsData() {
    List<MinecraftTicketsAnswers> data = minecraftTicketsAnswersRepository.findAll();
    log.trace("Extracted {} raw MinecraftTicketsAnswers entries.", data.size());
    return data;
  }

  private void updateMcTicketsLastCheck(List<Short> allEmployeeIds) {
    mcTicketsLastCheckRepository.deleteAll();
    allEmployeeIds.forEach(id -> mcTicketsLastCheckRepository.save(new McTicketsLastCheck(id, LocalDate.now())));
    log.info("Updated {} employee entries in McTicketsLastCheck with current date.", allEmployeeIds.size());
  }

  private List<EmployeeCodes> extractEmployeeCodes() {
    List<EmployeeCodes> codes = employeeCodesRepository.findAll();
    log.trace("Fetched {} EmployeeCodes from DB.", codes.size());
    return codes;
  }

  private List<Short> getAllEmployeeIdsFromEmployeeCodes(List<EmployeeCodes> employeeCodes) {
    List<Short> ids = employeeCodes.stream()
        .map(EmployeeCodes::getEmployeeId)
        .distinct()
        .collect(Collectors.toList());
    log.trace("Collected {} distinct employee IDs.", ids.size());
    return ids;
  }

  private List<DailyMinecraftTickets> getDailyMinecraftTicketsData() {
    List<DailyMinecraftTickets> data = dailyMinecraftTicketsRepository.findAll();
    log.trace("Fetched {} DailyMinecraftTickets entries.", data.size());
    return data;
  }

  private List<Employee> getEmployeesData() {
    List<Employee> data = employeeRepository.findAll();
    log.trace("Fetched {} Employee entries.", data.size());
    return data;
  }

  private LocalDate checkForOldestDate(List<DailyMinecraftTickets> rawData) {
    LocalDate oldest = rawData.stream()
        .map(DailyMinecraftTickets::getDate)
        .min(LocalDate::compareTo)
        .orElse(LocalDate.of(2100, 1, 1));
    log.trace("Oldest date detected: {}", oldest);
    return oldest;
  }

  private List<DailyPlaytime> getAllPlaytimeData() {
    List<DailyPlaytime> data = dailyPlaytimeRepository.findAll();
    log.trace("Fetched {} DailyPlaytime entries.", data.size());
    return data;
  }

  private List<TotalOldMinecraftTickets> getOldTotalDailyMinecraftTicketsData() {
    List<TotalOldMinecraftTickets> data = totalOldMinecraftTicketsRepository.findAll();
    log.trace("Fetched {} TotalOldMinecraftTickets entries.", data.size());
    return data;
  }

  private List<LocalDate> getAllMinecraftTicketsDates(List<DailyMinecraftTickets> data) {
    List<LocalDate> dates = data.stream()
        .map(DailyMinecraftTickets::getDate)
        .distinct()
        .collect(Collectors.toList());
    log.trace("Extracted {} distinct dates from ticket data.", dates.size());
    return dates;
  }

  private List<Short> getAllEmployeesFromDailyMinecraftTickets(List<DailyMinecraftTickets> data) {
    List<Short> employees = data.stream()
        .map(DailyMinecraftTickets::getEmployeeId)
        .distinct()
        .collect(Collectors.toList());
    log.trace("Extracted {} unique employees from ticket data.", employees.size());
    return employees;
  }
}
