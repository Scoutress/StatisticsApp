package com.scoutress.KaimuxAdminStats.servicesImpl.minecraftTickets;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.AverageMinecraftTicketsCompared;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.DailyMinecraftTickets;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.DailyMinecraftTicketsCompared;
import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.AverageMinecraftTicketsComparedRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.DailyMinecraftTicketsComparedRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.DailyMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.DailyPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.services.minecraftTickets.MinecraftTicketsComparedService;

@Service
public class MinecraftTicketsComparedServiceImpl implements MinecraftTicketsComparedService {

  private final DailyMinecraftTicketsRepository dailyMinecraftTicketsRepository;
  private final DailyPlaytimeRepository dailyPlaytimeRepository;
  private final DailyMinecraftTicketsComparedRepository dailyMinecraftTicketsComparedRepository;
  private final AverageMinecraftTicketsComparedRepository averageMinecraftTicketsComparedRepository;

  public MinecraftTicketsComparedServiceImpl(
      DailyMinecraftTicketsRepository dailyMinecraftTicketsRepository,
      DailyPlaytimeRepository dailyPlaytimeRepository,
      DailyMinecraftTicketsComparedRepository dailyMinecraftTicketsComparedRepository,
      AverageMinecraftTicketsComparedRepository averageMinecraftTicketsComparedRepository) {
    this.dailyMinecraftTicketsRepository = dailyMinecraftTicketsRepository;
    this.dailyPlaytimeRepository = dailyPlaytimeRepository;
    this.dailyMinecraftTicketsComparedRepository = dailyMinecraftTicketsComparedRepository;
    this.averageMinecraftTicketsComparedRepository = averageMinecraftTicketsComparedRepository;
  }

  @Override
  public void compareEachEmployeeDailyMcTicketsValues() {
    List<DailyMinecraftTickets> rawData = getAllMinecraftTickets();

    if (rawData == null || rawData.isEmpty()) {
      throw new RuntimeException("No Minecraft tickets data found. Cannot proceed.");
    }

    List<DailyPlaytime> rawDailyPlaytimeData = getRawDailyPlaytimeData();

    if (rawDailyPlaytimeData == null || rawDailyPlaytimeData.isEmpty()) {
      throw new RuntimeException("No Minecraft tickets data found. Cannot proceed.");
    }

    List<LocalDate> allDates = getAllMinecraftTicketsDates(rawData);

    if (allDates == null || allDates.isEmpty()) {
      throw new RuntimeException("No dates found in Minecraft tickets data. Cannot proceed.");
    }

    List<Short> allEmployees = getAllEmployeesFromDailyMinecraftTickets(rawData);

    if (allEmployees == null || allEmployees.isEmpty()) {
      throw new RuntimeException("No employees found in Minecraft tickets data. Cannot proceed.");
    }

    for (Short employeeId : allEmployees) {
      double ticketRatioSumThisEmployee = 0;
      int datesCount = 0;

      for (LocalDate date : allDates) {
        try {
          double playtimeForThisEmployeeThisDate = getPlaytimeForThisEmployeeThisDate(
              rawDailyPlaytimeData, employeeId, date);
          int ticketsThisDateThisEmployee = getTicketCountThisDateThisEmployee(rawData, date, employeeId);
          int ticketsThisDateAllEmployees = getTicketCountThisDateAllEmployees(rawData, date);

          if (ticketsThisDateAllEmployees == 0) {
            System.err.println("Total tickets for all employees on " + date + " is zero. Skipping.");
            continue;
          }

          double ticketRatioThisDateThisEmployee = calculateTicketRatioThisDate(
              ticketsThisDateThisEmployee, ticketsThisDateAllEmployees);

          double finalTicketRatio = checkIfPlaytimeIsMoreThan5minutesAndHasAnyTicketsThatDate(
              ticketsThisDateThisEmployee, ticketRatioThisDateThisEmployee, playtimeForThisEmployeeThisDate);

          saveTicketRatioThisDateThisEmployee(finalTicketRatio, date, employeeId);

          ticketRatioSumThisEmployee += finalTicketRatio;
          datesCount++;
        } catch (Exception e) {
          System.err.println("Error processing employee " + employeeId + " on date " + date + ": " + e.getMessage());
        }
      }

      if (datesCount > 0) {
        double averageValueOfTicketRatiosThisEmployee = calculateAverageTicketRatioThisEmployee(
            ticketRatioSumThisEmployee, datesCount);

        saveAverageTicketRatioThisEmployee(averageValueOfTicketRatiosThisEmployee, employeeId);
      } else {
        System.err.println("No valid data for employee " + employeeId + ". Skipping average calculation.");
      }
    }
  }

  public List<DailyMinecraftTickets> getAllMinecraftTickets() {
    return dailyMinecraftTicketsRepository.findAll();
  }

  public List<DailyPlaytime> getRawDailyPlaytimeData() {
    return dailyPlaytimeRepository.findAll();
  }

  public List<LocalDate> getAllMinecraftTicketsDates(List<DailyMinecraftTickets> data) {
    return data
        .stream()
        .map(DailyMinecraftTickets::getDate)
        .distinct()
        .collect(Collectors.toList());
  }

  public List<Short> getAllEmployeesFromDailyMinecraftTickets(List<DailyMinecraftTickets> data) {
    return data
        .stream()
        .map(DailyMinecraftTickets::getEmployeeId)
        .distinct()
        .collect(Collectors.toList());
  }

  public double getPlaytimeForThisEmployeeThisDate(List<DailyPlaytime> rawDailyPlaytimeData, Short employeeId,
      LocalDate date) {
    return rawDailyPlaytimeData
        .stream()
        .filter(dailyPlaytime -> dailyPlaytime.getEmployeeId().equals(employeeId)
            && dailyPlaytime.getDate().equals(date))
        .map(DailyPlaytime::getTimeInHours)
        .findFirst()
        .orElse(0.0);
  }

  public int getTicketCountThisDateThisEmployee(
      List<DailyMinecraftTickets> dailyTickets, LocalDate thisDate, Short thisEmployee) {
    return dailyTickets
        .stream()
        .filter(tickets -> tickets.getEmployeeId().equals(thisEmployee) && tickets.getDate().equals(thisDate))
        .map(DailyMinecraftTickets::getTicketCount)
        .findFirst()
        .orElse(0);
  }

  public int getTicketCountThisDateAllEmployees(List<DailyMinecraftTickets> dailyTickets, LocalDate thisDate) {
    return dailyTickets
        .stream()
        .filter(tickets -> tickets.getDate().equals(thisDate))
        .mapToInt(DailyMinecraftTickets::getTicketCount)
        .sum();
  }

  public double calculateTicketRatioThisDate(int ticketsThisDateThisEmployee, int ticketsThisDateAllEmployees) {
    if (ticketsThisDateAllEmployees == 0) {
      return 0;
    }
    return (double) ticketsThisDateThisEmployee / ticketsThisDateAllEmployees;
  }

  public double checkIfPlaytimeIsMoreThan5minutesAndHasAnyTicketsThatDate(int ticketsThisDateThisEmployee,
      double ticketRatioThisDateThisEmployee, double playtimeForThisEmployeeThisDate) {
    double playtimeInMinutes = playtimeForThisEmployeeThisDate * 60;

    if (ticketsThisDateThisEmployee > 0 && playtimeInMinutes < 5) {
      return ticketRatioThisDateThisEmployee + 0.25;
    } else {
      return ticketRatioThisDateThisEmployee;
    }
  }

  public void saveTicketRatioThisDateThisEmployee(double ticketRatioThisDateThisEmployee, LocalDate date,
      Short employee) {
    DailyMinecraftTicketsCompared existingRecord = dailyMinecraftTicketsComparedRepository
        .findByEmployeeIdAndDate(employee, date);

    if (existingRecord != null) {
      existingRecord.setValue(ticketRatioThisDateThisEmployee);
      dailyMinecraftTicketsComparedRepository.save(existingRecord);
    } else {
      DailyMinecraftTicketsCompared newRecord = new DailyMinecraftTicketsCompared();
      newRecord.setEmployeeId(employee);
      newRecord.setValue(ticketRatioThisDateThisEmployee);
      newRecord.setDate(date);
      dailyMinecraftTicketsComparedRepository.save(newRecord);
    }
  }

  public double calculateAverageTicketRatioThisEmployee(double ticketRatioSumThisEmployee, int datesCount) {
    if (datesCount == 0) {
      return 0;
    }
    return (double) ticketRatioSumThisEmployee / datesCount;
  }

  public void saveAverageTicketRatioThisEmployee(double averageValueOfTicketRatiosThisEmployee, Short employee) {
    AverageMinecraftTicketsCompared existingRecord = averageMinecraftTicketsComparedRepository
        .findByEmployeeId(employee);

    if (existingRecord != null) {
      existingRecord.setValue(averageValueOfTicketRatiosThisEmployee);
      averageMinecraftTicketsComparedRepository.save(existingRecord);
    } else {
      AverageMinecraftTicketsCompared newRecord = new AverageMinecraftTicketsCompared();
      newRecord.setEmployeeId(employee);
      newRecord.setValue(averageValueOfTicketRatiosThisEmployee);
      averageMinecraftTicketsComparedRepository.save(newRecord);
    }
  }
}
