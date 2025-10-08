package com.scoutress.KaimuxAdminStats.servicesImpl.minecraftTickets;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

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

  @Override
  public void convertRawMcTicketsData(
      List<MinecraftTicketsAnswers> rawMcTicketsData,
      List<EmployeeCodes> employeeCodes,
      List<Short> allEmployeeIds) {

    for (Short employeeId : allEmployeeIds) {
      Short minecraftUserCode = getMinecraftUserCodeThisEmployee(
          employeeCodes, employeeId);
      List<MinecraftTicketsAnswers> rawDataThisEmployee = getRawDataForThisEmployee(
          rawMcTicketsData, minecraftUserCode);
      List<LocalDate> rawDataDatesThisEmployee = getAllDatesFromRawDataThisEmployee(
          rawDataThisEmployee);

      for (LocalDate date : rawDataDatesThisEmployee) {
        int ticketsCount = getTicketsCountForThisEmployee(
            rawDataThisEmployee, date);

        if (!date.equals(LocalDate.now())) {
          saveTicketCountForThisEmployeeThisDate(
              ticketsCount, date, employeeId);
        }
      }
    }
  }

  private Short getMinecraftUserCodeThisEmployee(List<EmployeeCodes> employeeCodes, Short employeeId) {
    return employeeCodes
        .stream()
        .filter(employeeCode -> employeeCode.getEmployeeId().equals(employeeId))
        .map(EmployeeCodes::getKmxWebApi)
        .findFirst()
        .orElse(null);
  }

  private List<MinecraftTicketsAnswers> getRawDataForThisEmployee(
      List<MinecraftTicketsAnswers> ticketsAnswers, Short minecraftUserCode) {
    return ticketsAnswers
        .stream()
        .filter(ticketAnswer -> ticketAnswer.getKmxWebApiMcTickets().equals(minecraftUserCode))
        .collect(Collectors.toList());
  }

  private List<LocalDate> getAllDatesFromRawDataThisEmployee(List<MinecraftTicketsAnswers> rawDataThisEmployee) {
    return rawDataThisEmployee
        .stream()
        .map(MinecraftTicketsAnswers::getDateTime)
        .map(dateTime -> dateTime.toLocalDate())
        .sorted()
        .collect(Collectors.toList());
  }

  private int getTicketsCountForThisEmployee(List<MinecraftTicketsAnswers> rawDataThisEmployee, LocalDate date) {
    return rawDataThisEmployee
        .stream()
        .filter(ticketAnswer -> ticketAnswer.getDateTime().toLocalDate().equals(date))
        .collect(Collectors.toList())
        .size();
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
    }
  }

  @Override
  public void calcAvgDailyMcTicketsPerEmployee(
      List<Short> allEmployeeIds,
      List<Employee> rawEmployeesData,
      LocalDate oldestDateFromData,
      List<DailyMinecraftTickets> rawData) {

    for (Short employeeId : allEmployeeIds) {
      LocalDate joinDateForThisEmployee = getJoinDateForThisEmployee(rawEmployeesData, employeeId);
      LocalDate oldestDate = getOldestDateForThisEmployeeFromTickets(oldestDateFromData, joinDateForThisEmployee);
      int daysCount = calculateDaysAfterOldestDate(oldestDate);
      int ticketsCountSinceOldestDate = calculateAllTicketsSinceOldestDate(rawData, employeeId, oldestDate);
      double averageValue = calculateAverageTicketsValue(ticketsCountSinceOldestDate, daysCount);
      saveAverageValueData(averageValue, employeeId);
    }
  }

  private LocalDate getJoinDateForThisEmployee(List<Employee> rawEmployeesData, Short employee) {
    return rawEmployeesData
        .stream()
        .filter(employeeData -> employeeData.getId().equals(employee))
        .map(Employee::getJoinDate)
        .findFirst()
        .orElse(LocalDate.of(2100, 1, 1));
  }

  private LocalDate getOldestDateForThisEmployeeFromTickets(
      LocalDate oldestDateFromData, LocalDate joinDateForThisEmployee) {
    return joinDateForThisEmployee.isAfter(oldestDateFromData)
        ? joinDateForThisEmployee
        : oldestDateFromData;
  }

  private int calculateDaysAfterOldestDate(LocalDate oldestDate) {
    LocalDate today = LocalDate.now();
    long daysBetweenLong = ChronoUnit.DAYS.between(oldestDate, today);
    return (int) daysBetweenLong;
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

  private double calculateAverageTicketsValue(int ticketsCountSinceOldestDate, int daysCount) {
    if (daysCount == 0) {
      return 0.0;
    }
    return (double) ticketsCountSinceOldestDate / daysCount;
  }

  private void saveAverageValueData(double averageValue, Short employeeId) {
    AverageDailyMinecraftTickets existingRecord = averageDailyMinecraftTicketsRepository.findByEmployeeId(employeeId);

    if (existingRecord != null) {
      existingRecord.setTickets(averageValue);
      averageDailyMinecraftTicketsRepository.save(existingRecord);
    } else {
      AverageDailyMinecraftTickets newRecord = new AverageDailyMinecraftTickets();
      newRecord.setEmployeeId(employeeId);
      newRecord.setTickets(averageValue);
      averageDailyMinecraftTicketsRepository.save(newRecord);
    }
  }

  @Override
  public void calcAvgMcTicketsPerPlaytime(
      List<DailyMinecraftTickets> rawData,
      List<Employee> rawEmployeesData,
      List<Short> allEmployeeIds,
      LocalDate oldestDateFromData,
      List<DailyPlaytime> playtimeData) {

    for (Short employeeId : allEmployeeIds) {
      try {
        LocalDate joinDateForThisEmployee = getJoinDateForThisEmployee(rawEmployeesData, employeeId);
        LocalDate oldestDate = getOldestDateForThisEmployeeFromTickets(oldestDateFromData, joinDateForThisEmployee);
        int ticketsCountSinceOldestDate = calculateAllTicketsSinceOldestDate(rawData, employeeId, oldestDate);
        double playtimeSinceOldestDate = getAllPlaytimeSinceOldestDate(playtimeData, employeeId, oldestDateFromData);

        if (playtimeSinceOldestDate <= 0) {
          System.err.println("ALERT: Invalid playtime for employee " + employeeId + ". Skipping.");
          continue;
        }

        double ticketsPerPlaytime = calculateTicketsPerPlaytime(ticketsCountSinceOldestDate, playtimeSinceOldestDate);
        saveTicketsPerPlaytime(ticketsPerPlaytime, employeeId);
      } catch (Exception e) {
        System.err.println("ALERT: Error processing employee " + employeeId + ": " + e.getMessage());
      }
    }
  }

  private double getAllPlaytimeSinceOldestDate(
      List<DailyPlaytime> playtimeData, Short targetEmployeeId, LocalDate oldestDate) {
    return playtimeData
        .stream()
        .filter(ticket -> ticket.getEmployeeId().equals(targetEmployeeId))
        .filter(ticket -> !ticket.getDate().isBefore(oldestDate))
        .mapToDouble(DailyPlaytime::getTimeInHours)
        .sum();
  }

  private double calculateTicketsPerPlaytime(int ticketsCount, double playtimeSinceOldestDate) {
    if (playtimeSinceOldestDate == 0) {
      return 0.0;
    }

    double playtimeInHours = playtimeSinceOldestDate;
    return ticketsCount / playtimeInHours;
  }

  private void saveTicketsPerPlaytime(double ticketsPerPlaytime, Short employeeId) {
    AverageMinecraftTicketsPerPlaytime existingRecord = averageMinecraftTicketsPerPlaytimeRepository
        .findByEmployeeId(employeeId);

    if (existingRecord != null) {
      existingRecord.setValue(ticketsPerPlaytime);
      averageMinecraftTicketsPerPlaytimeRepository.save(existingRecord);
    } else {
      AverageMinecraftTicketsPerPlaytime newRecord = new AverageMinecraftTicketsPerPlaytime();
      newRecord.setEmployeeId(employeeId);
      newRecord.setValue(ticketsPerPlaytime);
      averageMinecraftTicketsPerPlaytimeRepository.save(newRecord);
    }
  }

  @Override
  public void calcTotalMinecraftTickets(
      List<Short> allEmployeeIds,
      List<DailyMinecraftTickets> rawDailyMcTicketsData,
      List<TotalOldMinecraftTickets> allOldTotalMinecraftTicketsData) {

    for (Short employeeId : allEmployeeIds) {
      int totalMinecraftTickets = getTotalMinecraftTicketsThisEmployee(
          rawDailyMcTicketsData, employeeId);
      int totalOldMinecraftTickets = getTotalOldMinecraftTicketsThisEmployee(
          allOldTotalMinecraftTicketsData, employeeId);
      int sumWithOldTickets = calculateTotalMinecraftTicketsThisEmployeeWithOldOnes(
          totalMinecraftTickets, totalOldMinecraftTickets);

      saveTotalMinecraftTicketsThisEmployee(sumWithOldTickets, employeeId);
    }
  }

  private int getTotalMinecraftTicketsThisEmployee(
      List<DailyMinecraftTickets> allDailyMcTicketsData, Short employeeId) {
    return allDailyMcTicketsData
        .stream()
        .filter(dailyMcTickets -> dailyMcTickets.getEmployeeId().equals(employeeId))
        .mapToInt(DailyMinecraftTickets::getTicketCount)
        .sum();
  }

  private int getTotalOldMinecraftTicketsThisEmployee(
      List<TotalOldMinecraftTickets> allOldTotalMinecraftTicketsData, Short employeeId) {
    return allOldTotalMinecraftTicketsData
        .stream()
        .filter(oldTotal -> oldTotal.getEmployeeId().equals(employeeId))
        .mapToInt(TotalOldMinecraftTickets::getTicketCount)
        .sum();
  }

  private int calculateTotalMinecraftTicketsThisEmployeeWithOldOnes(
      int totalMinecraftTickets, int totalOldMinecraftTickets) {
    return totalMinecraftTickets + totalOldMinecraftTickets;
  }

  private void saveTotalMinecraftTicketsThisEmployee(int sumWithOldTickets, Short employeeId) {
    TotalMinecraftTickets existingRecord = totalMinecraftTicketsRepository.findByEmployeeId(employeeId);

    if (existingRecord != null) {
      existingRecord.setTicketCount(sumWithOldTickets);
      totalMinecraftTicketsRepository.save(existingRecord);
    } else {
      TotalMinecraftTickets newRecord = new TotalMinecraftTickets();
      newRecord.setEmployeeId(employeeId);
      newRecord.setTicketCount(sumWithOldTickets);
      totalMinecraftTicketsRepository.save(newRecord);
    }
  }

  @Override
  public double getSumOfMcTicketsByEmployeeIdAndDuration(Short employeeId, Short days) {
    List<DailyMinecraftTickets> rawMcTicketsData = getRawMcTicketsData();
    List<DailyMinecraftTickets> mcTicketsThisEmployee = getMcTicketsForThisEmployee(rawMcTicketsData, employeeId);

    return calculateMcTickets(mcTicketsThisEmployee, days);
  }

  private List<DailyMinecraftTickets> getRawMcTicketsData() {
    return dailyMinecraftTicketsRepository.findAll();
  }

  private List<DailyMinecraftTickets> getMcTicketsForThisEmployee(
      List<DailyMinecraftTickets> rawMcTicketsData, Short employeeId) {
    return rawMcTicketsData
        .stream()
        .filter(mcTicketsData -> mcTicketsData.getEmployeeId().equals(employeeId))
        .collect(Collectors.toList());
  }

  private Double calculateMcTickets(List<DailyMinecraftTickets> mcTicketsData, Short days) {
    return mcTicketsData
        .stream()
        .filter(data -> data.getDate().isAfter(LocalDate.now().minusDays(days)))
        .mapToDouble(DailyMinecraftTickets::getTicketCount)
        .sum();
  }
}
