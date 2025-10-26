package com.scoutress.KaimuxAdminStats.servicesImpl.minecraftTickets;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.AverageDailyMinecraftTickets;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.AverageMinecraftTicketsPerPlaytime;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.DailyMinecraftTickets;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.MinecraftTicketsAnswers;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.TotalMinecraftTickets;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.TotalOldMinecraftTickets;
import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.AverageDailyMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.AverageMinecraftTicketsPerPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.DailyMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.TotalMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.services.minecraftTickets.MinecraftTicketsService;

@Service
public class MinecraftTicketsServiceImpl implements MinecraftTicketsService {

  private static final Logger log = LoggerFactory.getLogger(MinecraftTicketsServiceImpl.class);

  private final DailyMinecraftTicketsRepository dailyMinecraftTicketsRepository;
  private final AverageDailyMinecraftTicketsRepository averageDailyMinecraftTicketsRepository;
  private final AverageMinecraftTicketsPerPlaytimeRepository averageMinecraftTicketsPerPlaytimeRepository;
  private final TotalMinecraftTicketsRepository totalMinecraftTicketsRepository;

  public MinecraftTicketsServiceImpl(
      DailyMinecraftTicketsRepository dailyMinecraftTicketsRepository,
      AverageDailyMinecraftTicketsRepository averageDailyMinecraftTicketsRepository,
      AverageMinecraftTicketsPerPlaytimeRepository averageMinecraftTicketsPerPlaytimeRepository,
      TotalMinecraftTicketsRepository totalMinecraftTicketsRepository) {
    this.dailyMinecraftTicketsRepository = dailyMinecraftTicketsRepository;
    this.averageDailyMinecraftTicketsRepository = averageDailyMinecraftTicketsRepository;
    this.averageMinecraftTicketsPerPlaytimeRepository = averageMinecraftTicketsPerPlaytimeRepository;
    this.totalMinecraftTicketsRepository = totalMinecraftTicketsRepository;
  }

  // ===========================================================
  // CONVERT RAW TICKETS
  // ===========================================================
  @Override
  public void convertRawMcTicketsData(
      List<MinecraftTicketsAnswers> rawMcTicketsData,
      List<EmployeeCodes> employeeCodes,
      List<Short> allEmployeeIds) {

    log.info("=== [START] Converting raw Minecraft ticket data ===");
    log.debug("Loaded {} raw entries, {} employee codes, {} employees.",
        rawMcTicketsData.size(), employeeCodes.size(), allEmployeeIds.size());

    int processed = 0;

    for (Short employeeId : allEmployeeIds) {
      try {
        Short minecraftUserCode = getMinecraftUserCodeThisEmployee(employeeCodes, employeeId);
        if (minecraftUserCode == null) {
          log.warn("⚠️ Skipping employee {} — no linked KMX Web API code found.", employeeId);
          continue;
        }

        List<MinecraftTicketsAnswers> rawDataThisEmployee = getRawDataForThisEmployee(rawMcTicketsData,
            minecraftUserCode);
        if (rawDataThisEmployee.isEmpty()) {
          log.trace("Employee {} — no raw data found.", employeeId);
          continue;
        }

        List<LocalDate> rawDataDates = getAllDatesFromRawDataThisEmployee(rawDataThisEmployee);
        log.trace("Employee {} — found {} unique dates.", employeeId, rawDataDates.size());

        for (LocalDate date : rawDataDates) {
          int ticketsCount = getTicketsCountForThisEmployee(rawDataThisEmployee, date);
          log.trace("Employee {} | Date {} | {} tickets", employeeId, date, ticketsCount);

          if (!date.equals(LocalDate.now())) {
            saveTicketCountForThisEmployeeThisDate(ticketsCount, date, employeeId);
          }
        }

      } catch (Exception e) {
        log.error("❌ Error converting tickets for employee {}: {}", employeeId, e.getMessage(), e);
      }

      processed++;
      if (processed % 20 == 0 || processed == allEmployeeIds.size()) {
        int percent = (int) ((processed / (double) allEmployeeIds.size()) * 100);
        log.info("Progress: Converted {}/{} employees ({}%)", processed, allEmployeeIds.size(), percent);
      }
    }

    log.info("✅ [END] Raw Minecraft ticket conversion completed for {} employees.", allEmployeeIds.size());
  }

  private Short getMinecraftUserCodeThisEmployee(List<EmployeeCodes> employeeCodes, Short employeeId) {
    Short code = employeeCodes.stream()
        .filter(c -> c.getEmployeeId().equals(employeeId))
        .map(EmployeeCodes::getKmxWebApi)
        .findFirst()
        .orElse(null);
    log.trace("Employee {} — KMX Web API code: {}", employeeId, code);
    return code;
  }

  private List<MinecraftTicketsAnswers> getRawDataForThisEmployee(
      List<MinecraftTicketsAnswers> ticketsAnswers, Short minecraftUserCode) {
    List<MinecraftTicketsAnswers> result = ticketsAnswers.stream()
        .filter(t -> t.getKmxWebApiMcTickets().equals(minecraftUserCode))
        .toList();
    log.trace("KMX Web API {} — found {} raw entries.", minecraftUserCode, result.size());
    return result;
  }

  private List<LocalDate> getAllDatesFromRawDataThisEmployee(List<MinecraftTicketsAnswers> rawDataThisEmployee) {
    List<LocalDate> dates = rawDataThisEmployee.stream()
        .map(ans -> ans.getDateTime().toLocalDate())
        .sorted()
        .distinct()
        .toList();
    log.trace("Extracted {} unique dates from raw data for employee.", dates.size());
    return dates;
  }

  private int getTicketsCountForThisEmployee(List<MinecraftTicketsAnswers> rawDataThisEmployee, LocalDate date) {
    int count = (int) rawDataThisEmployee.stream()
        .filter(ans -> ans.getDateTime().toLocalDate().equals(date))
        .count();
    log.trace("Tickets count for {} = {}", date, count);
    return count;
  }

  private void saveTicketCountForThisEmployeeThisDate(int ticketsCount, LocalDate date, Short employeeId) {
    try {
      DailyMinecraftTickets existingRecord = dailyMinecraftTicketsRepository
          .findByEmployeeIdAndDateAndTicketCount(employeeId, date, ticketsCount);

      if (existingRecord == null) {
        DailyMinecraftTickets newRecord = new DailyMinecraftTickets();
        newRecord.setEmployeeId(employeeId);
        newRecord.setDate(date);
        newRecord.setTicketCount(ticketsCount);
        dailyMinecraftTicketsRepository.save(newRecord);
        log.trace("Inserted ticket record for employee {} on {} with {} tickets.", employeeId, date, ticketsCount);
      } else {
        log.trace("Record for employee {} on {} with {} tickets already exists, skipping.", employeeId, date,
            ticketsCount);
      }
    } catch (Exception e) {
      log.error("❌ Error saving ticket record (emp={}, date={}): {}", employeeId, date, e.getMessage(), e);
    }
  }

  // ===========================================================
  // AVERAGE DAILY TICKETS
  // ===========================================================
  @Override
  public void calcAvgDailyMcTicketsPerEmployee(
      List<Short> allEmployeeIds,
      List<Employee> rawEmployeesData,
      LocalDate oldestDateFromData,
      List<DailyMinecraftTickets> rawData) {

    log.info("=== [START] Calculating average daily Minecraft tickets per employee ===");
    log.debug("Dataset: {} employees, {} daily ticket entries.", allEmployeeIds.size(), rawData.size());

    for (Short employeeId : allEmployeeIds) {
      try {
        LocalDate joinDate = getJoinDateForThisEmployee(rawEmployeesData, employeeId);
        LocalDate oldestDate = getOldestDateForThisEmployeeFromTickets(oldestDateFromData, joinDate);

        int days = calculateDaysAfterOldestDate(oldestDate);
        int totalTickets = calculateAllTicketsSinceOldestDate(rawData, employeeId, oldestDate);
        double avg = calculateAverageTicketsValue(totalTickets, days);

        saveAverageValueData(avg, employeeId);
        log.debug("Employee {} — avg daily tickets: {} ({} tickets over {} days)", employeeId, avg, totalTickets, days);

      } catch (Exception e) {
        log.error("❌ Error calculating avg daily tickets for employee {}: {}", employeeId, e.getMessage(), e);
      }
    }

    log.info("✅ [END] Completed average daily ticket calculation.");
  }

  private LocalDate getJoinDateForThisEmployee(List<Employee> rawEmployeesData, Short employee) {
    LocalDate date = rawEmployeesData.stream()
        .filter(e -> e.getId().equals(employee))
        .map(Employee::getJoinDate)
        .findFirst()
        .orElse(LocalDate.of(2100, 1, 1));
    log.trace("Employee {} — join date: {}", employee, date);
    return date;
  }

  private LocalDate getOldestDateForThisEmployeeFromTickets(LocalDate oldestDateFromData, LocalDate joinDate) {
    LocalDate effective = joinDate.isAfter(oldestDateFromData) ? joinDate : oldestDateFromData;
    log.trace("Effective start date = {}", effective);
    return effective;
  }

  private int calculateDaysAfterOldestDate(LocalDate oldestDate) {
    int days = (int) ChronoUnit.DAYS.between(oldestDate, LocalDate.now());
    log.trace("Days since {} = {}", oldestDate, days);
    return days;
  }

  private int calculateAllTicketsSinceOldestDate(List<DailyMinecraftTickets> rawData, int employeeId,
      LocalDate oldestDate) {
    int sum = rawData.stream()
        .filter(ticket -> ticket.getEmployeeId() == employeeId)
        .filter(ticket -> ticket.getDate().isAfter(oldestDate))
        .mapToInt(DailyMinecraftTickets::getTicketCount)
        .sum();
    log.trace("Employee {} — total tickets since {} = {}", employeeId, oldestDate, sum);
    return sum;
  }

  private double calculateAverageTicketsValue(int total, int days) {
    double avg = days == 0 ? 0.0 : (double) total / days;
    log.trace("Calculated average: total={} days={} avg={}", total, days, avg);
    return avg;
  }

  private void saveAverageValueData(double avg, Short employeeId) {
    try {
      AverageDailyMinecraftTickets record = averageDailyMinecraftTicketsRepository.findByEmployeeId(employeeId);
      if (record != null) {
        record.setTickets(avg);
        averageDailyMinecraftTicketsRepository.save(record);
        log.trace("Updated avg daily tickets record for emp {}: {}", employeeId, avg);
      } else {
        AverageDailyMinecraftTickets newRecord = new AverageDailyMinecraftTickets();
        newRecord.setEmployeeId(employeeId);
        newRecord.setTickets(avg);
        averageDailyMinecraftTicketsRepository.save(newRecord);
        log.trace("Inserted avg daily tickets record for emp {}: {}", employeeId, avg);
      }
    } catch (Exception e) {
      log.error("❌ Error saving average tickets for employee {}: {}", employeeId, e.getMessage(), e);
    }
  }

  // ===========================================================
  // TICKETS PER PLAYTIME
  // ===========================================================
  @Override
  public void calcAvgMcTicketsPerPlaytime(
      List<DailyMinecraftTickets> rawData,
      List<Employee> rawEmployeesData,
      List<Short> allEmployeeIds,
      LocalDate oldestDateFromData,
      List<DailyPlaytime> playtimeData) {

    log.info("=== [START] Calculating tickets per playtime ratio ===");
    log.debug("Dataset: {} employees, {} playtime entries.", allEmployeeIds.size(), playtimeData.size());

    for (Short employeeId : allEmployeeIds) {
      try {
        LocalDate joinDate = getJoinDateForThisEmployee(rawEmployeesData, employeeId);
        LocalDate oldestDate = getOldestDateForThisEmployeeFromTickets(oldestDateFromData, joinDate);

        int tickets = calculateAllTicketsSinceOldestDate(rawData, employeeId, oldestDate);
        double playtime = getAllPlaytimeSinceOldestDate(playtimeData, employeeId, oldestDate);

        log.trace("Employee {} | totalTickets={} | totalPlaytime={}h", employeeId, tickets, playtime);

        if (playtime <= 0) {
          log.warn("⚠️ Skipping employee {} — invalid playtime: {}h", employeeId, playtime);
          continue;
        }

        double ratio = calculateTicketsPerPlaytime(tickets, playtime);
        saveTicketsPerPlaytime(ratio, employeeId);
        log.debug("Employee {} — tickets/playtime ratio: {}", employeeId, ratio);

      } catch (Exception e) {
        log.error("❌ Error calculating tickets/playtime for employee {}: {}", employeeId, e.getMessage(), e);
      }
    }

    log.info("✅ [END] Completed tickets per playtime calculation.");
  }

  private double getAllPlaytimeSinceOldestDate(
      List<DailyPlaytime> playtimeData, Short targetEmployeeId, LocalDate oldestDate) {
    double total = playtimeData.stream()
        .filter(p -> p.getEmployeeId().equals(targetEmployeeId))
        .filter(p -> !p.getDate().isBefore(oldestDate))
        .mapToDouble(DailyPlaytime::getTimeInHours)
        .sum();
    log.trace("Employee {} — total playtime since {} = {}h", targetEmployeeId, oldestDate, total);
    return total;
  }

  private double calculateTicketsPerPlaytime(int tickets, double playtime) {
    double result = playtime == 0 ? 0.0 : tickets / playtime;
    log.trace("tickets={} / playtime={}h = {}", tickets, playtime, result);
    return result;
  }

  private void saveTicketsPerPlaytime(double ratio, Short employeeId) {
    try {
      AverageMinecraftTicketsPerPlaytime record = averageMinecraftTicketsPerPlaytimeRepository
          .findByEmployeeId(employeeId);
      if (record != null) {
        record.setValue(ratio);
        averageMinecraftTicketsPerPlaytimeRepository.save(record);
        log.trace("Updated tickets/playtime ratio for emp {}: {}", employeeId, ratio);
      } else {
        AverageMinecraftTicketsPerPlaytime newRecord = new AverageMinecraftTicketsPerPlaytime();
        newRecord.setEmployeeId(employeeId);
        newRecord.setValue(ratio);
        averageMinecraftTicketsPerPlaytimeRepository.save(newRecord);
        log.trace("Inserted tickets/playtime ratio for emp {}: {}", employeeId, ratio);
      }
    } catch (Exception e) {
      log.error("❌ Error saving tickets/playtime for employee {}: {}", employeeId, e.getMessage(), e);
    }
  }

  // ===========================================================
  // TOTAL TICKETS
  // ===========================================================
  @Override
  public void calcTotalMinecraftTickets(
      List<Short> allEmployeeIds,
      List<DailyMinecraftTickets> rawDailyMcTicketsData,
      List<TotalOldMinecraftTickets> allOldTotalMinecraftTicketsData) {

    log.info("=== [START] Calculating total Minecraft tickets ===");
    log.debug("Dataset: {} employees, {} daily, {} old total records.",
        allEmployeeIds.size(), rawDailyMcTicketsData.size(), allOldTotalMinecraftTicketsData.size());

    for (Short employeeId : allEmployeeIds) {
      try {
        int totalNew = getTotalMinecraftTicketsThisEmployee(rawDailyMcTicketsData, employeeId);
        int totalOld = getTotalOldMinecraftTicketsThisEmployee(allOldTotalMinecraftTicketsData, employeeId);
        int totalSum = totalNew + totalOld;

        saveTotalMinecraftTicketsThisEmployee(totalSum, employeeId);
        log.debug("Employee {} — total tickets: {} (new {} + old {})", employeeId, totalSum, totalNew, totalOld);

      } catch (Exception e) {
        log.error("❌ Error calculating total tickets for employee {}: {}", employeeId, e.getMessage(), e);
      }
    }

    log.info("✅ [END] Total ticket calculation completed.");
  }

  private int getTotalMinecraftTicketsThisEmployee(List<DailyMinecraftTickets> data, Short employeeId) {
    int total = data.stream()
        .filter(t -> t.getEmployeeId().equals(employeeId))
        .mapToInt(DailyMinecraftTickets::getTicketCount)
        .sum();
    log.trace("Employee {} — total new tickets: {}", employeeId, total);
    return total;
  }

  private int getTotalOldMinecraftTicketsThisEmployee(List<TotalOldMinecraftTickets> oldData, Short employeeId) {
    int total = oldData.stream()
        .filter(t -> t.getEmployeeId().equals(employeeId))
        .mapToInt(TotalOldMinecraftTickets::getTicketCount)
        .sum();
    log.trace("Employee {} — total old tickets: {}", employeeId, total);
    return total;
  }

  private void saveTotalMinecraftTicketsThisEmployee(int total, Short employeeId) {
    try {
      TotalMinecraftTickets record = totalMinecraftTicketsRepository.findByEmployeeId(employeeId);
      if (record != null) {
        record.setTicketCount(total);
        totalMinecraftTicketsRepository.save(record);
        log.trace("Updated total tickets record for emp {}: {}", employeeId, total);
      } else {
        TotalMinecraftTickets newRecord = new TotalMinecraftTickets();
        newRecord.setEmployeeId(employeeId);
        newRecord.setTicketCount(total);
        totalMinecraftTicketsRepository.save(newRecord);
        log.trace("Inserted total tickets record for emp {}: {}", employeeId, total);
      }
    } catch (Exception e) {
      log.error("❌ Error saving total tickets for employee {}: {}", employeeId, e.getMessage(), e);
    }
  }

  // ===========================================================
  // QUERY METHODS
  // ===========================================================
  @Override
  public double getSumOfMcTicketsByEmployeeIdAndDuration(Short employeeId, Short days) {
    List<DailyMinecraftTickets> mcTicketsData = dailyMinecraftTicketsRepository.findAll().stream()
        .filter(d -> d.getEmployeeId().equals(employeeId))
        .filter(d -> d.getDate().isAfter(LocalDate.now().minusDays(days)))
        .toList();

    double total = mcTicketsData.stream().mapToDouble(DailyMinecraftTickets::getTicketCount).sum();
    log.debug("Queried {} days of tickets for employee {} — total: {}", days, employeeId, total);
    return total;
  }
}
