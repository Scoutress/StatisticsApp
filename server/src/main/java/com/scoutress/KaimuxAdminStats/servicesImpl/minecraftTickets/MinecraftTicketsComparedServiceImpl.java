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

    log.info("=== [START] Minecraft ticket comparison process ===");

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

    log.debug("Loaded {} tickets, {} playtime entries, {} dates, {} employees.",
        rawDailyMcTicketsData.size(),
        allPlaytimeData.size(),
        allDatesFromDailyMcTickets.size(),
        allEmployeesFromDailyMcTickets.size());

    int totalEmployees = allEmployeesFromDailyMcTickets.size();
    int processed = 0;
    long startTime = System.currentTimeMillis();

    for (Short employeeId : allEmployeesFromDailyMcTickets) {
      log.debug("→ Processing employee ID: {}", employeeId);
      try {
        double ticketRatioSumThisEmployee = 0;
        int datesCount = 0;

        for (LocalDate date : allDatesFromDailyMcTickets) {
          double playtime = getPlaytimeForThisEmployeeThisDate(allPlaytimeData, employeeId, date);
          int ticketsEmployee = getTicketCountThisDateThisEmployee(rawDailyMcTicketsData, date, employeeId);
          int ticketsAll = getTicketCountThisDateAllEmployees(rawDailyMcTicketsData, date);

          log.trace("EMP {} | DATE {} | ticketsEmployee={} | ticketsAll={} | playtime(h)={}",
              employeeId, date, ticketsEmployee, ticketsAll, playtime);

          if (ticketsAll == 0) {
            log.trace("Skipping {} — total tickets = 0 on this date.", date);
            continue;
          }

          double ratio = calculateTicketRatioThisDate(ticketsEmployee, ticketsAll);
          double adjustedRatio = checkIfPlaytimeIsMoreThan5minutesAndHasAnyTicketsThatDate(
              ticketsEmployee, ratio, playtime);

          log.trace("EMP {} | DATE {} | raw ratio={} | adjusted ratio={}",
              employeeId, date, ratio, adjustedRatio);

          saveTicketRatioThisDateThisEmployee(adjustedRatio, date, employeeId);

          ticketRatioSumThisEmployee += adjustedRatio;
          datesCount++;
        }

        if (datesCount > 0) {
          double avgRatio = calculateAverageTicketRatioThisEmployee(ticketRatioSumThisEmployee, datesCount);
          saveAverageTicketRatioThisEmployee(avgRatio, employeeId);
          log.debug("✅ Employee {} — avg ratio: {}, processed days: {}", employeeId, avgRatio, datesCount);
        } else {
          log.warn("⚠️ Employee {} has no valid ticket data to process.", employeeId);
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

    long elapsed = System.currentTimeMillis() - startTime;
    log.info("✅ [END] Minecraft ticket comparison completed for {} employees in {} ms ({} s).",
        totalEmployees, elapsed, elapsed / 1000.0);
  }

  // ======================================================
  // HELPER METHODS
  // ======================================================

  private double getPlaytimeForThisEmployeeThisDate(
      List<DailyPlaytime> rawDailyPlaytimeData, Short employeeId, LocalDate date) {
    double playtime = rawDailyPlaytimeData
        .stream()
        .filter(dailyPlaytime -> dailyPlaytime.getEmployeeId().equals(employeeId)
            && dailyPlaytime.getDate().equals(date))
        .map(DailyPlaytime::getTimeInHours)
        .findFirst()
        .orElse(0.0);
    log.trace("Playtime lookup → emp={}, date={}, hours={}", employeeId, date, playtime);
    return playtime;
  }

  private int getTicketCountThisDateThisEmployee(
      List<DailyMinecraftTickets> dailyTickets, LocalDate thisDate, Short thisEmployee) {
    int count = dailyTickets
        .stream()
        .filter(tickets -> tickets.getEmployeeId().equals(thisEmployee)
            && tickets.getDate().equals(thisDate))
        .map(DailyMinecraftTickets::getTicketCount)
        .findFirst()
        .orElse(0);
    log.trace("Tickets lookup → emp={}, date={}, tickets={}", thisEmployee, thisDate, count);
    return count;
  }

  private int getTicketCountThisDateAllEmployees(List<DailyMinecraftTickets> dailyTickets, LocalDate thisDate) {
    int total = dailyTickets
        .stream()
        .filter(tickets -> tickets.getDate().equals(thisDate))
        .mapToInt(DailyMinecraftTickets::getTicketCount)
        .sum();
    log.trace("Total tickets on {} = {}", thisDate, total);
    return total;
  }

  private double calculateTicketRatioThisDate(int ticketsThisDateThisEmployee, int ticketsThisDateAllEmployees) {
    if (ticketsThisDateAllEmployees == 0)
      return 0;
    double ratio = (double) ticketsThisDateThisEmployee / ticketsThisDateAllEmployees;
    log.trace("Ratio calc → empTickets={}, allTickets={}, ratio={}",
        ticketsThisDateThisEmployee, ticketsThisDateAllEmployees, ratio);
    return ratio;
  }

  private double checkIfPlaytimeIsMoreThan5minutesAndHasAnyTicketsThatDate(
      int ticketsThisDateThisEmployee, double ticketRatioThisDateThisEmployee, double playtimeForThisEmployeeThisDate) {

    double playtimeInMinutes = playtimeForThisEmployeeThisDate * 60;

    if (ticketsThisDateThisEmployee > 0 && playtimeInMinutes < 5) {
      log.trace("⚠️ Low playtime (<5m) with tickets detected — boosting ratio from {} to {}",
          ticketRatioThisDateThisEmployee, ticketRatioThisDateThisEmployee + 0.25);
      return ticketRatioThisDateThisEmployee + 0.25;
    }

    log.trace("Playtime OK ({} min), keeping ratio {}", playtimeInMinutes, ticketRatioThisDateThisEmployee);
    return ticketRatioThisDateThisEmployee;
  }

  private void saveTicketRatioThisDateThisEmployee(double ratio, LocalDate date, Short employeeId) {
    try {
      DailyMinecraftTicketsCompared record = dailyMinecraftTicketsComparedRepository.findByEmployeeIdAndDate(employeeId,
          date);

      if (record != null) {
        record.setValue(ratio);
        dailyMinecraftTicketsComparedRepository.save(record);
        log.trace("Updated ratio record → emp={}, date={}, ratio={}", employeeId, date, ratio);
      } else {
        DailyMinecraftTicketsCompared newRecord = new DailyMinecraftTicketsCompared();
        newRecord.setEmployeeId(employeeId);
        newRecord.setValue(ratio);
        newRecord.setDate(date);
        dailyMinecraftTicketsComparedRepository.save(newRecord);
        log.trace("Inserted new ratio record → emp={}, date={}, ratio={}", employeeId, date, ratio);
      }
    } catch (Exception e) {
      log.error("❌ Error saving ratio (emp={}, date={}): {}", employeeId, date, e.getMessage(), e);
    }
  }

  private double calculateAverageTicketRatioThisEmployee(double ticketRatioSumThisEmployee, int datesCount) {
    double avg = datesCount == 0 ? 0 : ticketRatioSumThisEmployee / datesCount;
    log.trace("Average ratio calc → sum={}, days={}, avg={}", ticketRatioSumThisEmployee, datesCount, avg);
    return avg;
  }

  private void saveAverageTicketRatioThisEmployee(double avgRatio, Short employeeId) {
    try {
      AverageMinecraftTicketsCompared record = averageMinecraftTicketsComparedRepository.findByEmployeeId(employeeId);

      if (record != null) {
        record.setValue(avgRatio);
        averageMinecraftTicketsComparedRepository.save(record);
        log.trace("Updated avg ratio record → emp={}, avg={}", employeeId, avgRatio);
      } else {
        AverageMinecraftTicketsCompared newRecord = new AverageMinecraftTicketsCompared();
        newRecord.setEmployeeId(employeeId);
        newRecord.setValue(avgRatio);
        averageMinecraftTicketsComparedRepository.save(newRecord);
        log.trace("Inserted new avg ratio record → emp={}, avg={}", employeeId, avgRatio);
      }
    } catch (Exception e) {
      log.error("❌ Error saving avg ratio for emp {}: {}", employeeId, e.getMessage(), e);
    }
  }
}
