package com.scoutress.KaimuxAdminStats.servicesImpl.minecraftTickets;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.DailyMinecraftTickets;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.DailyMinecraftTicketsCompared;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.OverallMinecraftTicketsCompared;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.DailyMinecraftTicketsComparedRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.DailyMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.OverallMinecraftTicketsComparedRepository;
import com.scoutress.KaimuxAdminStats.services.minecraftTickets.MinecraftTicketsComparedService;

@Service
public class MinecraftTicketsComparedServiceImpl implements MinecraftTicketsComparedService {

  private final DailyMinecraftTicketsRepository dailyMinecraftTicketsRepository;
  private final DailyMinecraftTicketsComparedRepository dailyMinecraftTicketsComparedRepository;
  private final OverallMinecraftTicketsComparedRepository overallMinecraftTicketsComparedRepository;

  public MinecraftTicketsComparedServiceImpl(
      DailyMinecraftTicketsRepository dailyMinecraftTicketsRepository,
      DailyMinecraftTicketsComparedRepository dailyMinecraftTicketsComparedRepository,
      OverallMinecraftTicketsComparedRepository overallMinecraftTicketsComparedRepository) {
    this.dailyMinecraftTicketsRepository = dailyMinecraftTicketsRepository;
    this.dailyMinecraftTicketsComparedRepository = dailyMinecraftTicketsComparedRepository;
    this.overallMinecraftTicketsComparedRepository = overallMinecraftTicketsComparedRepository;
  }

  @Override
  public void compareEachEmployeeDailyMcTicketsValues() {
    List<DailyMinecraftTickets> dailyTickets = getAllMinecraftTickets();
    List<LocalDate> allDates = getAllMinecraftTicketsDates(dailyTickets);
    List<Short> allEmployees = getAllEmployeesFromDailyMinecraftTickets(dailyTickets);

    for (Short employee : allEmployees) {

      double ticketRatioSumThisEmployee = 0;
      int datesCount = 0;

      for (LocalDate date : allDates) {
        int ticketsThisDayThisEmployee = getTicketCountThisDayThisEmployee(dailyTickets, date, employee);
        int ticketsThisDayAllEmployees = getTicketCountThisDayAllEmployees(dailyTickets, date);
        double ticketRatioThisDayThisEmployee = calculateTicketRatioThisDay(
            ticketsThisDayThisEmployee, ticketsThisDayAllEmployees);

        saveTicketRatioThisDayThisEmployee(ticketRatioThisDayThisEmployee, date, employee);

        ticketRatioSumThisEmployee += ticketRatioThisDayThisEmployee;
        datesCount++;
      }

      if (datesCount > 0) {
        double averageValueOfTicketRatiosThisEmployee = calculateAverageTicketRatioThisEmployee(
            ticketRatioSumThisEmployee, datesCount);
        saveAverageTicketRatioThisEmployee(averageValueOfTicketRatiosThisEmployee, employee);
      }
    }
  }

  public List<DailyMinecraftTickets> getAllMinecraftTickets() {
    return dailyMinecraftTicketsRepository.findAll();
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

  public int getTicketCountThisDayThisEmployee(
      List<DailyMinecraftTickets> dailyTickets, LocalDate thisDate, Short thisEmployee) {
    return dailyTickets
        .stream()
        .filter(tickets -> tickets.getEmployeeId().equals(thisEmployee) && tickets.getDate().equals(thisDate))
        .map(DailyMinecraftTickets::getTicketCount)
        .findFirst()
        .orElse(0);
  }

  public int getTicketCountThisDayAllEmployees(List<DailyMinecraftTickets> dailyTickets, LocalDate thisDate) {
    return dailyTickets
        .stream()
        .filter(tickets -> tickets.getDate().equals(thisDate))
        .mapToInt(DailyMinecraftTickets::getTicketCount)
        .sum();
  }

  public double calculateTicketRatioThisDay(int ticketsThisDayThisEmployee, int ticketsThisDayAllEmployees) {
    if (ticketsThisDayAllEmployees == 0) {
      return 0;
    }
    return (double) ticketsThisDayThisEmployee / ticketsThisDayAllEmployees;
  }

  public double calculateAverageTicketRatioThisEmployee(double ticketRatioSumThisEmployee, int datesCount) {
    if (datesCount == 0) {
      return 0;
    }
    return (double) ticketRatioSumThisEmployee / datesCount;
  }

  public void saveTicketRatioThisDayThisEmployee(double ticketRatioThisDayThisEmployee, LocalDate date,
      Short employee) {
    DailyMinecraftTicketsCompared existingRecord = dailyMinecraftTicketsComparedRepository
        .findByEmployeeIdAndDate(employee, date);

    if (existingRecord != null) {
      existingRecord.setValue(ticketRatioThisDayThisEmployee);
      dailyMinecraftTicketsComparedRepository.save(existingRecord);
    } else {
      DailyMinecraftTicketsCompared newRecord = new DailyMinecraftTicketsCompared();
      newRecord.setEmployeeId(employee);
      newRecord.setValue(ticketRatioThisDayThisEmployee);
      newRecord.setDate(date);
      dailyMinecraftTicketsComparedRepository.save(newRecord);
    }
  }

  public void saveAverageTicketRatioThisEmployee(double averageValueOfTicketRatiosThisEmployee, Short employee) {
    OverallMinecraftTicketsCompared existingRecord = overallMinecraftTicketsComparedRepository
        .findByEmployeeId(employee);

    if (existingRecord != null) {
      existingRecord.setValue(averageValueOfTicketRatiosThisEmployee);
      overallMinecraftTicketsComparedRepository.save(existingRecord);
    } else {
      OverallMinecraftTicketsCompared newRecord = new OverallMinecraftTicketsCompared();
      newRecord.setEmployeeId(employee);
      newRecord.setValue(averageValueOfTicketRatiosThisEmployee);
      overallMinecraftTicketsComparedRepository.save(newRecord);
    }
  }
}
