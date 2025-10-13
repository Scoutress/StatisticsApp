package com.scoutress.KaimuxAdminStats.servicesImpl.minecraftTickets;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.AverageMinecraftTicketsCompared;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.DailyMinecraftTickets;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.DailyMinecraftTicketsCompared;
import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.AverageMinecraftTicketsComparedRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.DailyMinecraftTicketsComparedRepository;
import com.scoutress.KaimuxAdminStats.services.minecraftTickets.MinecraftTicketsComparedService;

@Service
public class MinecraftTicketsComparedServiceImpl implements MinecraftTicketsComparedService {

  private static final Logger log = LoggerFactory.getLogger(MinecraftTicketsComparedServiceImpl.class);

  private final DailyMinecraftTicketsComparedRepository dailyMinecraftTicketsComparedRepository;
  private final AverageMinecraftTicketsComparedRepository averageMinecraftTicketsComparedRepository;

  public MinecraftTicketsComparedServiceImpl(
      DailyMinecraftTicketsComparedRepository dailyMinecraftTicketsComparedRepository,
      AverageMinecraftTicketsComparedRepository averageMinecraftTicketsComparedRepository) {
    this.dailyMinecraftTicketsComparedRepository = dailyMinecraftTicketsComparedRepository;
    this.averageMinecraftTicketsComparedRepository = averageMinecraftTicketsComparedRepository;
  }

  @Override
  public void compareEachEmployeeDailyMcTicketsValues(
      List<DailyMinecraftTickets> rawDailyMcTicketsData,
      List<DailyPlaytime> allPlaytimeData,
      List<LocalDate> allDatesFromDailyMcTickets,
      List<Short> allEmployeesFromDailyMcTickets) {

    log.info("=== Starting Minecraft ticket comparison process ===");

    if (rawDailyMcTicketsData == null || rawDailyMcTicketsData.isEmpty()) {
      log.warn("⚠️ No Minecraft ticket data found. Skipping process.");
      return;
    }
    if (allPlaytimeData == null || allPlaytimeData.isEmpty()) {
      log.warn("⚠️ No playtime data found. Skipping process.");
      return;
    }
    if (allDatesFromDailyMcTickets == null || allDatesFromDailyMcTickets.isEmpty()) {
      log.warn("⚠️ No date range found for ticket data. Skipping process.");
      return;
    }
    if (allEmployeesFromDailyMcTickets == null || allEmployeesFromDailyMcTickets.isEmpty()) {
      log.warn("⚠️ No employees found in ticket data. Skipping process.");
      return;
    }

    int totalEmployees = allEmployeesFromDailyMcTickets.size();
    int processed = 0;

    for (Short employeeId : allEmployeesFromDailyMcTickets) {
      try {
        double ticketRatioSumThisEmployee = 0;
        int datesCount = 0;

        for (LocalDate date : allDatesFromDailyMcTickets) {
          double playtime = getPlaytimeForThisEmployeeThisDate(allPlaytimeData, employeeId, date);
          int ticketsEmployee = getTicketCountThisDateThisEmployee(rawDailyMcTicketsData, date, employeeId);
          int ticketsAll = getTicketCountThisDateAllEmployees(rawDailyMcTicketsData, date);

          if (ticketsAll == 0)
            continue;

          double ratio = calculateTicketRatioThisDate(ticketsEmployee, ticketsAll);
          double adjustedRatio = checkIfPlaytimeIsMoreThan5minutesAndHasAnyTicketsThatDate(
              ticketsEmployee, ratio, playtime);

          saveTicketRatioThisDateThisEmployee(adjustedRatio, date, employeeId);

          ticketRatioSumThisEmployee += adjustedRatio;
          datesCount++;
        }

        if (datesCount > 0) {
          double avgRatio = calculateAverageTicketRatioThisEmployee(ticketRatioSumThisEmployee, datesCount);
          saveAverageTicketRatioThisEmployee(avgRatio, employeeId);
          log.debug("Processed employee {} — avg ratio: {}, days: {}", employeeId, avgRatio, datesCount);
        } else {
          log.warn("Employee {} has no valid ticket data to process.", employeeId);
        }

      } catch (Exception e) {
        log.error("❌ Error processing employee {}: {}", employeeId, e.getMessage(), e);
      }

      processed++;
      if (processed % 10 == 0 || processed == totalEmployees) {
        int percent = (int) ((processed / (double) totalEmployees) * 100);
        log.info("Progress: {}/{} employees processed ({}%)", processed, totalEmployees, percent);
      }
    }

    log.info("✅ Minecraft ticket comparison process completed for {} employees.", totalEmployees);
  }

  // ======================================================
  // HELPER METHODS
  // ======================================================

  private double getPlaytimeForThisEmployeeThisDate(
      List<DailyPlaytime> rawDailyPlaytimeData, Short employeeId, LocalDate date) {
    return rawDailyPlaytimeData
        .stream()
        .filter(dailyPlaytime -> dailyPlaytime.getEmployeeId().equals(employeeId)
            && dailyPlaytime.getDate().equals(date))
        .map(DailyPlaytime::getTimeInHours)
        .findFirst()
        .orElse(0.0);
  }

  private int getTicketCountThisDateThisEmployee(
      List<DailyMinecraftTickets> dailyTickets, LocalDate thisDate, Short thisEmployee) {
    return dailyTickets
        .stream()
        .filter(tickets -> tickets.getEmployeeId().equals(thisEmployee) && tickets.getDate().equals(thisDate))
        .map(DailyMinecraftTickets::getTicketCount)
        .findFirst()
        .orElse(0);
  }

  private int getTicketCountThisDateAllEmployees(List<DailyMinecraftTickets> dailyTickets, LocalDate thisDate) {
    return dailyTickets
        .stream()
        .filter(tickets -> tickets.getDate().equals(thisDate))
        .mapToInt(DailyMinecraftTickets::getTicketCount)
        .sum();
  }

  private double calculateTicketRatioThisDate(int ticketsThisDateThisEmployee, int ticketsThisDateAllEmployees) {
    if (ticketsThisDateAllEmployees == 0)
      return 0;
    return (double) ticketsThisDateThisEmployee / ticketsThisDateAllEmployees;
  }

  private double checkIfPlaytimeIsMoreThan5minutesAndHasAnyTicketsThatDate(
      int ticketsThisDateThisEmployee, double ticketRatioThisDateThisEmployee, double playtimeForThisEmployeeThisDate) {

    double playtimeInMinutes = playtimeForThisEmployeeThisDate * 60;

    if (ticketsThisDateThisEmployee > 0 && playtimeInMinutes < 5) {
      log.trace("⚠️ Employee with very low playtime (<5m) but tickets present — boosting ratio.");
      return ticketRatioThisDateThisEmployee + 0.25;
    }
    return ticketRatioThisDateThisEmployee;
  }

  private void saveTicketRatioThisDateThisEmployee(double ratio, LocalDate date, Short employeeId) {
    DailyMinecraftTicketsCompared record = dailyMinecraftTicketsComparedRepository
        .findByEmployeeIdAndDate(employeeId, date);

    if (record != null) {
      record.setValue(ratio);
      dailyMinecraftTicketsComparedRepository.save(record);
      log.trace("Updated ratio for employee {} on {}: {}", employeeId, date, ratio);
    } else {
      DailyMinecraftTicketsCompared newRecord = new DailyMinecraftTicketsCompared();
      newRecord.setEmployeeId(employeeId);
      newRecord.setValue(ratio);
      newRecord.setDate(date);
      dailyMinecraftTicketsComparedRepository.save(newRecord);
      log.trace("Inserted new ratio for employee {} on {}: {}", employeeId, date, ratio);
    }
  }

  private double calculateAverageTicketRatioThisEmployee(double ticketRatioSumThisEmployee, int datesCount) {
    if (datesCount == 0)
      return 0;
    return ticketRatioSumThisEmployee / datesCount;
  }

  private void saveAverageTicketRatioThisEmployee(double avgRatio, Short employeeId) {
    AverageMinecraftTicketsCompared record = averageMinecraftTicketsComparedRepository.findByEmployeeId(employeeId);

    if (record != null) {
      record.setValue(avgRatio);
      averageMinecraftTicketsComparedRepository.save(record);
      log.trace("Updated avg ratio for employee {}: {}", employeeId, avgRatio);
    } else {
      AverageMinecraftTicketsCompared newRecord = new AverageMinecraftTicketsCompared();
      newRecord.setEmployeeId(employeeId);
      newRecord.setValue(avgRatio);
      averageMinecraftTicketsComparedRepository.save(newRecord);
      log.trace("Inserted avg ratio for employee {}: {}", employeeId, avgRatio);
    }
  }
}
