package com.scoutress.KaimuxAdminStats.servicesImpl.minecraftTickets;

import java.time.LocalDate;
import java.util.List;

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

    if (rawDailyMcTicketsData != null && !rawDailyMcTicketsData.isEmpty()) {
      if (allPlaytimeData != null && !allPlaytimeData.isEmpty()) {
        if (allDatesFromDailyMcTickets != null && !allDatesFromDailyMcTickets.isEmpty()) {
          if (allEmployeesFromDailyMcTickets != null && !allEmployeesFromDailyMcTickets.isEmpty()) {
            for (Short employeeId : allEmployeesFromDailyMcTickets) {
              double ticketRatioSumThisEmployee = 0;
              int datesCount = 0;

              for (LocalDate date : allDatesFromDailyMcTickets) {
                double playtimeForThisEmployeeThisDate = getPlaytimeForThisEmployeeThisDate(
                    allPlaytimeData, employeeId, date);
                int ticketsThisDateThisEmployee = getTicketCountThisDateThisEmployee(rawDailyMcTicketsData, date,
                    employeeId);
                int ticketsThisDateAllEmployees = getTicketCountThisDateAllEmployees(rawDailyMcTicketsData, date);

                if (ticketsThisDateAllEmployees != 0) {
                  double ticketRatioThisDateThisEmployee = calculateTicketRatioThisDate(
                      ticketsThisDateThisEmployee, ticketsThisDateAllEmployees);

                  double finalTicketRatio = checkIfPlaytimeIsMoreThan5minutesAndHasAnyTicketsThatDate(
                      ticketsThisDateThisEmployee, ticketRatioThisDateThisEmployee, playtimeForThisEmployeeThisDate);

                  saveTicketRatioThisDateThisEmployee(finalTicketRatio, date, employeeId);

                  ticketRatioSumThisEmployee += finalTicketRatio;
                  datesCount++;
                }
              }

              if (datesCount > 0) {
                double averageValueOfTicketRatiosThisEmployee = calculateAverageTicketRatioThisEmployee(
                    ticketRatioSumThisEmployee, datesCount);

                saveAverageTicketRatioThisEmployee(averageValueOfTicketRatiosThisEmployee, employeeId);
              }
            }
          }
        }
      }
    }
  }

  private double getPlaytimeForThisEmployeeThisDate(List<DailyPlaytime> rawDailyPlaytimeData, Short employeeId,
      LocalDate date) {
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
    if (ticketsThisDateAllEmployees == 0) {
      return 0;
    }
    return (double) ticketsThisDateThisEmployee / ticketsThisDateAllEmployees;
  }

  private double checkIfPlaytimeIsMoreThan5minutesAndHasAnyTicketsThatDate(int ticketsThisDateThisEmployee,
      double ticketRatioThisDateThisEmployee, double playtimeForThisEmployeeThisDate) {
    double playtimeInMinutes = playtimeForThisEmployeeThisDate * 60;

    if (ticketsThisDateThisEmployee > 0 && playtimeInMinutes < 5) {
      return ticketRatioThisDateThisEmployee + 0.25;
    } else {
      return ticketRatioThisDateThisEmployee;
    }
  }

  private void saveTicketRatioThisDateThisEmployee(double ticketRatioThisDateThisEmployee, LocalDate date,
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

  private double calculateAverageTicketRatioThisEmployee(double ticketRatioSumThisEmployee, int datesCount) {
    if (datesCount == 0) {
      return 0;
    }
    return (double) ticketRatioSumThisEmployee / datesCount;
  }

  private void saveAverageTicketRatioThisEmployee(double averageValueOfTicketRatiosThisEmployee, Short employee) {
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
