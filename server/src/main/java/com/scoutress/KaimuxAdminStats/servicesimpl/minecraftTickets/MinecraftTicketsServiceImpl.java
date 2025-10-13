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

    log.info("=== Starting raw Minecraft ticket data conversion ===");

    int processed = 0;
    for (Short employeeId : allEmployeeIds) {
      try {
        Short minecraftUserCode = getMinecraftUserCodeThisEmployee(employeeCodes, employeeId);
        if (minecraftUserCode == null) {
          log.warn("⚠️ Skipping employee {}: no linked KMX Web API code found.", employeeId);
          continue;
        }

        List<MinecraftTicketsAnswers> rawDataThisEmployee = getRawDataForThisEmployee(rawMcTicketsData,
            minecraftUserCode);

        List<LocalDate> rawDataDates = getAllDatesFromRawDataThisEmployee(rawDataThisEmployee);

        for (LocalDate date : rawDataDates) {
          int ticketsCount = getTicketsCountForThisEmployee(rawDataThisEmployee, date);

          if (!date.equals(LocalDate.now())) {
            saveTicketCountForThisEmployeeThisDate(ticketsCount, date, employeeId);
          }
        }

      } catch (Exception e) {
        log.error("❌ Error while converting tickets for employee {}: {}", employeeId, e.getMessage(), e);
      }

      processed++;
      if (processed % 20 == 0 || processed == allEmployeeIds.size()) {
        int percent = (int) ((processed / (double) allEmployeeIds.size()) * 100);
        log.info("Progress: Converted {}/{} employees ({}%)", processed, allEmployeeIds.size(), percent);
      }
    }

    log.info("✅ Raw Minecraft ticket conversion completed for {} employees.", allEmployeeIds.size());
  }

  private Short getMinecraftUserCodeThisEmployee(List<EmployeeCodes> employeeCodes, Short employeeId) {
    return employeeCodes
        .stream()
        .filter(code -> code.getEmployeeId().equals(employeeId))
        .map(EmployeeCodes::getKmxWebApi)
        .findFirst()
        .orElse(null);
  }

  private List<MinecraftTicketsAnswers> getRawDataForThisEmployee(
      List<MinecraftTicketsAnswers> ticketsAnswers, Short minecraftUserCode) {
    return ticketsAnswers
        .stream()
        .filter(t -> t.getKmxWebApiMcTickets().equals(minecraftUserCode))
        .toList();
  }

  private List<LocalDate> getAllDatesFromRawDataThisEmployee(List<MinecraftTicketsAnswers> rawDataThisEmployee) {
    return rawDataThisEmployee
        .stream()
        .map(ans -> ans.getDateTime().toLocalDate())
        .sorted()
        .toList();
  }

  private int getTicketsCountForThisEmployee(List<MinecraftTicketsAnswers> rawDataThisEmployee, LocalDate date) {
    return (int) rawDataThisEmployee
        .stream()
        .filter(ans -> ans.getDateTime().toLocalDate().equals(date))
        .count();
  }

  private void saveTicketCountForThisEmployeeThisDate(int ticketsCount, LocalDate date, Short employeeId) {
    DailyMinecraftTickets existingRecord = dailyMinecraftTicketsRepository
        .findByEmployeeIdAndDateAndTicketCount(employeeId, date, ticketsCount);

    if (existingRecord == null) {
      DailyMinecraftTickets newRecord = new DailyMinecraftTickets();
      newRecord.setEmployeeId(employeeId);
      newRecord.setDate(date);
      newRecord.setTicketCount(ticketsCount);
      dailyMinecraftTicketsRepository.save(newRecord);
      log.trace("Inserted ticket record for employee {} on {} with {} tickets.", employeeId, date, ticketsCount);
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

    log.info("=== Calculating average daily Minecraft tickets per employee ===");

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

    log.info("✅ Completed average daily ticket calculation.");
  }

  private LocalDate getJoinDateForThisEmployee(List<Employee> rawEmployeesData, Short employee) {
    return rawEmployeesData
        .stream()
        .filter(e -> e.getId().equals(employee))
        .map(Employee::getJoinDate)
        .findFirst()
        .orElse(LocalDate.of(2100, 1, 1));
  }

  private LocalDate getOldestDateForThisEmployeeFromTickets(LocalDate oldestDateFromData,
      LocalDate joinDateForThisEmployee) {
    return joinDateForThisEmployee.isAfter(oldestDateFromData)
        ? joinDateForThisEmployee
        : oldestDateFromData;
  }

  private int calculateDaysAfterOldestDate(LocalDate oldestDate) {
    return (int) ChronoUnit.DAYS.between(oldestDate, LocalDate.now());
  }

  private int calculateAllTicketsSinceOldestDate(
      List<DailyMinecraftTickets> rawData, int employeeId, LocalDate oldestDate) {
    return rawData
        .stream()
        .filter(ticket -> ticket.getEmployeeId() == employeeId)
        .filter(ticket -> ticket.getDate().isAfter(oldestDate))
        .mapToInt(DailyMinecraftTickets::getTicketCount)
        .sum();
  }

  private double calculateAverageTicketsValue(int total, int days) {
    return days == 0 ? 0.0 : (double) total / days;
  }

  private void saveAverageValueData(double avg, Short employeeId) {
    AverageDailyMinecraftTickets record = averageDailyMinecraftTicketsRepository.findByEmployeeId(employeeId);

    if (record != null) {
      record.setTickets(avg);
      averageDailyMinecraftTicketsRepository.save(record);
    } else {
      AverageDailyMinecraftTickets newRecord = new AverageDailyMinecraftTickets();
      newRecord.setEmployeeId(employeeId);
      newRecord.setTickets(avg);
      averageDailyMinecraftTicketsRepository.save(newRecord);
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

    log.info("=== Calculating average tickets per playtime ===");

    for (Short employeeId : allEmployeeIds) {
      try {
        LocalDate joinDate = getJoinDateForThisEmployee(rawEmployeesData, employeeId);
        LocalDate oldestDate = getOldestDateForThisEmployeeFromTickets(oldestDateFromData, joinDate);

        int tickets = calculateAllTicketsSinceOldestDate(rawData, employeeId, oldestDate);
        double playtime = getAllPlaytimeSinceOldestDate(playtimeData, employeeId, oldestDate);

        if (playtime <= 0) {
          log.warn("⚠️ Invalid playtime for employee {} ({}h). Skipping.", employeeId, playtime);
          continue;
        }

        double ratio = calculateTicketsPerPlaytime(tickets, playtime);
        saveTicketsPerPlaytime(ratio, employeeId);
        log.debug("Employee {} — tickets per playtime: {}", employeeId, ratio);

      } catch (Exception e) {
        log.error("❌ Error calculating tickets/playtime for employee {}: {}", employeeId, e.getMessage(), e);
      }
    }

    log.info("✅ Completed tickets per playtime calculation.");
  }

  private double getAllPlaytimeSinceOldestDate(
      List<DailyPlaytime> playtimeData, Short targetEmployeeId, LocalDate oldestDate) {
    return playtimeData
        .stream()
        .filter(p -> p.getEmployeeId().equals(targetEmployeeId))
        .filter(p -> !p.getDate().isBefore(oldestDate))
        .mapToDouble(DailyPlaytime::getTimeInHours)
        .sum();
  }

  private double calculateTicketsPerPlaytime(int tickets, double playtime) {
    return playtime == 0 ? 0.0 : tickets / playtime;
  }

  private void saveTicketsPerPlaytime(double ratio, Short employeeId) {
    AverageMinecraftTicketsPerPlaytime record = averageMinecraftTicketsPerPlaytimeRepository
        .findByEmployeeId(employeeId);

    if (record != null) {
      record.setValue(ratio);
      averageMinecraftTicketsPerPlaytimeRepository.save(record);
    } else {
      AverageMinecraftTicketsPerPlaytime newRecord = new AverageMinecraftTicketsPerPlaytime();
      newRecord.setEmployeeId(employeeId);
      newRecord.setValue(ratio);
      averageMinecraftTicketsPerPlaytimeRepository.save(newRecord);
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

    log.info("=== Calculating total Minecraft tickets per employee ===");

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

    log.info("✅ Total ticket calculation completed.");
  }

  private int getTotalMinecraftTicketsThisEmployee(List<DailyMinecraftTickets> data, Short employeeId) {
    return data
        .stream()
        .filter(t -> t.getEmployeeId().equals(employeeId))
        .mapToInt(DailyMinecraftTickets::getTicketCount)
        .sum();
  }

  private int getTotalOldMinecraftTicketsThisEmployee(List<TotalOldMinecraftTickets> oldData, Short employeeId) {
    return oldData
        .stream()
        .filter(t -> t.getEmployeeId().equals(employeeId))
        .mapToInt(TotalOldMinecraftTickets::getTicketCount)
        .sum();
  }

  private void saveTotalMinecraftTicketsThisEmployee(int total, Short employeeId) {
    TotalMinecraftTickets record = totalMinecraftTicketsRepository.findByEmployeeId(employeeId);

    if (record != null) {
      record.setTicketCount(total);
      totalMinecraftTicketsRepository.save(record);
    } else {
      TotalMinecraftTickets newRecord = new TotalMinecraftTickets();
      newRecord.setEmployeeId(employeeId);
      newRecord.setTicketCount(total);
      totalMinecraftTicketsRepository.save(newRecord);
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

    double total = mcTicketsData
        .stream()
        .mapToDouble(DailyMinecraftTickets::getTicketCount).sum();
    log.debug("Queried {} days of tickets for employee {} — total: {}", days, employeeId, total);
    return total;
  }
}
