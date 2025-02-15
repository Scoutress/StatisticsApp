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
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeCodesRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.AverageDailyMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.AverageMinecraftTicketsPerPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.DailyMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.MinecraftTicketsAnswersRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.TotalMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.TotalOldMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.DailyPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.services.minecraftTickets.MinecraftTicketsService;

@Service
public class MinecraftTicketsServiceImpl implements MinecraftTicketsService {

  private final EmployeeCodesRepository employeeCodesRepository;
  private final MinecraftTicketsAnswersRepository minecraftTicketsAnswersRepository;
  private final EmployeeRepository employeeRepository;
  private final DailyMinecraftTicketsRepository dailyMinecraftTicketsRepository;
  private final AverageDailyMinecraftTicketsRepository averageDailyMinecraftTicketsRepository;
  private final DailyPlaytimeRepository dailyPlaytimeRepository;
  private final AverageMinecraftTicketsPerPlaytimeRepository averageMinecraftTicketsPerPlaytimeRepository;
  private final TotalMinecraftTicketsRepository totalMinecraftTicketsRepository;
  private final TotalOldMinecraftTicketsRepository totalOldMinecraftTicketsRepository;

  public MinecraftTicketsServiceImpl(
      EmployeeCodesRepository employeeCodesRepository,
      MinecraftTicketsAnswersRepository minecraftTicketsAnswersRepository,
      EmployeeRepository employeeRepository,
      DailyMinecraftTicketsRepository discordTicketsRepository,
      AverageDailyMinecraftTicketsRepository averageDailyMinecraftTicketsRepository,
      DailyPlaytimeRepository dailyPlaytimeRepository,
      AverageMinecraftTicketsPerPlaytimeRepository averageMinecraftTicketsPerPlaytimeRepository,
      TotalMinecraftTicketsRepository totalMinecraftTicketsRepository,
      TotalOldMinecraftTicketsRepository totalOldMinecraftTicketsRepository) {
    this.employeeCodesRepository = employeeCodesRepository;
    this.minecraftTicketsAnswersRepository = minecraftTicketsAnswersRepository;
    this.employeeRepository = employeeRepository;
    this.dailyMinecraftTicketsRepository = discordTicketsRepository;
    this.averageDailyMinecraftTicketsRepository = averageDailyMinecraftTicketsRepository;
    this.dailyPlaytimeRepository = dailyPlaytimeRepository;
    this.averageMinecraftTicketsPerPlaytimeRepository = averageMinecraftTicketsPerPlaytimeRepository;
    this.totalMinecraftTicketsRepository = totalMinecraftTicketsRepository;
    this.totalOldMinecraftTicketsRepository = totalOldMinecraftTicketsRepository;
  }

  @Override
  public void convertMinecraftTicketsAnswers() {
    List<MinecraftTicketsAnswers> ticketsAnswers = extractDataFromAnswersTable();
    List<EmployeeCodes> employeeCodes = extractEmployeeCodes();
    List<Short> allEmployeeIds = getAllEmployeeIdsFromEmployeeCodes(employeeCodes);

    for (Short employeeId : allEmployeeIds) {
      Short minecraftUserCode = getMinecraftUserCodeThisEmployee(employeeCodes, employeeId);
      List<MinecraftTicketsAnswers> rawDataThisEmployee = getRawDataForThisEmployee(ticketsAnswers, minecraftUserCode);
      List<LocalDate> rawDataDatesThisEmployee = getAllDatesFromRawDataThisEmployee(rawDataThisEmployee);

      for (LocalDate date : rawDataDatesThisEmployee) {
        int ticketsCount = getTicketsCountForThisEmployee(rawDataThisEmployee, date);

        if (!date.equals(LocalDate.now())) {
          saveTicketCountForThisEmployeeThisDate(ticketsCount, date, employeeId);
        }
      }
    }
  }

  public List<MinecraftTicketsAnswers> extractDataFromAnswersTable() {
    return minecraftTicketsAnswersRepository.findAll();
  }

  public List<EmployeeCodes> extractEmployeeCodes() {
    return employeeCodesRepository.findAll();
  }

  public List<Short> getAllEmployeeIdsFromEmployeeCodes(List<EmployeeCodes> employeeCodes) {
    return employeeCodes
        .stream()
        .map(EmployeeCodes::getEmployeeId)
        .distinct()
        .collect(Collectors.toList());
  }

  public Short getMinecraftUserCodeThisEmployee(List<EmployeeCodes> employeeCodes, Short employeeId) {
    return employeeCodes
        .stream()
        .filter(employeeCode -> employeeCode.getEmployeeId().equals(employeeId))
        .map(EmployeeCodes::getMinecraftId)
        .findFirst()
        .orElse(null);
  }

  public List<MinecraftTicketsAnswers> getRawDataForThisEmployee(
      List<MinecraftTicketsAnswers> ticketsAnswers, Short minecraftUserCode) {
    return ticketsAnswers
        .stream()
        .filter(ticketAnswer -> ticketAnswer.getMinecraftTicketId().equals(minecraftUserCode))
        .collect(Collectors.toList());
  }

  public List<LocalDate> getAllDatesFromRawDataThisEmployee(List<MinecraftTicketsAnswers> rawDataThisEmployee) {
    return rawDataThisEmployee
        .stream()
        .map(MinecraftTicketsAnswers::getDateTime)
        .map(dateTime -> dateTime.toLocalDate())
        .sorted()
        .collect(Collectors.toList());
  }

  public int getTicketsCountForThisEmployee(List<MinecraftTicketsAnswers> rawDataThisEmployee, LocalDate date) {
    return rawDataThisEmployee
        .stream()
        .filter(ticketAnswer -> ticketAnswer.getDateTime().toLocalDate().equals(date))
        .collect(Collectors.toList())
        .size();
  }

  public void saveTicketCountForThisEmployeeThisDate(int ticketsCount, LocalDate date, Short employeeId) {
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
  public void calculateAverageDailyMinecraftTicketsValues() {
    List<DailyMinecraftTickets> rawData = getDailyMinecraftTicketsData();
    List<Employee> rawEmployeesData = getEmployeesData();
    List<Short> allEmployeeIds = getAllEmployeesFromTheTable(rawData);
    LocalDate oldestDateFromData = checkForOldestDate(rawData);

    for (Short employeeId : allEmployeeIds) {
      LocalDate joinDateForThisEmployee = getJoinDateForThisEmployee(rawEmployeesData, employeeId);
      LocalDate oldestDate = getOldestDateForThisEmployeeFromTickets(oldestDateFromData, joinDateForThisEmployee);
      int daysCount = calculateDaysAfterOldestDate(oldestDate);
      int ticketsCountSinceOldestDate = calculateAllTicketsSinceOldestDate(rawData, employeeId, oldestDate);
      double averageValue = calculateAverageTicketsValue(ticketsCountSinceOldestDate, daysCount);
      saveAverageValueData(averageValue, employeeId);
    }
  }

  public List<DailyMinecraftTickets> getDailyMinecraftTicketsData() {
    return dailyMinecraftTicketsRepository.findAll();
  }

  public List<Employee> getEmployeesData() {
    return employeeRepository.findAll();
  }

  public List<Short> getAllEmployeesFromTheTable(List<DailyMinecraftTickets> rawData) {
    return rawData
        .stream()
        .map(DailyMinecraftTickets::getEmployeeId)
        .distinct()
        .sorted()
        .toList();
  }

  public LocalDate checkForOldestDate(List<DailyMinecraftTickets> rawData) {
    return rawData
        .stream()
        .map(DailyMinecraftTickets::getDate)
        .min(LocalDate::compareTo)
        .orElse(LocalDate.of(2100, 1, 1));
  }

  public LocalDate getOldestDateForThisEmployeeFromTickets(
      LocalDate oldestDateFromData, LocalDate joinDateForThisEmployee) {
    return joinDateForThisEmployee.isAfter(oldestDateFromData)
        ? joinDateForThisEmployee
        : oldestDateFromData;
  }

  public int calculateDaysAfterOldestDate(LocalDate oldestDate) {
    LocalDate today = LocalDate.now();
    long daysBetweenLong = ChronoUnit.DAYS.between(oldestDate, today);
    return (int) daysBetweenLong;
  }

  public LocalDate getJoinDateForThisEmployee(List<Employee> rawEmployeesData, Short employee) {
    return rawEmployeesData
        .stream()
        .filter(employeeData -> employeeData.getId().equals(employee))
        .map(Employee::getJoinDate)
        .findFirst()
        .orElse(LocalDate.of(2100, 1, 1));
  }

  public int calculateAllTicketsSinceOldestDate(
      List<DailyMinecraftTickets> rawData, int employeeId, LocalDate oldestDate) {
    return rawData
        .stream()
        .filter(ticket -> ticket.getEmployeeId() == employeeId)
        .filter(ticket -> ticket.getDate().isAfter(oldestDate))
        .mapToInt(DailyMinecraftTickets::getTicketCount)
        .sum();
  }

  public double calculateAverageTicketsValue(int ticketsCountSinceOldestDate, int daysCount) {
    if (daysCount == 0) {
      return 0.0;
    }
    return (double) ticketsCountSinceOldestDate / daysCount;
  }

  public void saveAverageValueData(double averageValue, Short employeeId) {
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
  public void calculateAverageMinecraftTicketsPerPlaytime() {
    List<DailyMinecraftTickets> rawData = getDailyMinecraftTicketsData();
    List<Employee> rawEmployeesData = getEmployeesData();
    List<Short> allEmployeeIds = getAllEmployeesFromTheTable(rawData);

    LocalDate oldestDateFromData = checkForOldestDate(rawData);

    for (Short employeeId : allEmployeeIds) {

      try {
        LocalDate joinDateForThisEmployee = getJoinDateForThisEmployee(rawEmployeesData, employeeId);
        LocalDate oldestDate = getOldestDateForThisEmployeeFromTickets(oldestDateFromData, joinDateForThisEmployee);
        int ticketsCountSinceOldestDate = calculateAllTicketsSinceOldestDate(rawData, employeeId, oldestDate);
        double playtimeSinceOldestDate = getAllPlaytimeSinceOldestDate(employeeId, oldestDateFromData);

        if (playtimeSinceOldestDate <= 0) {
          System.err.println("Invalid playtime for employee " + employeeId + ". Skipping.");
          continue;
        }

        double ticketsPerPlaytime = calculateTicketsPerPlaytime(ticketsCountSinceOldestDate, playtimeSinceOldestDate);
        saveTicketsPerPlaytime(ticketsPerPlaytime, employeeId);
      } catch (Exception e) {
        System.err.println("Error processing employee " + employeeId + ": " + e.getMessage());
      }
    }
  }

  public List<DailyPlaytime> extractDataFromDailyPlaytimeTable() {
    return dailyPlaytimeRepository.findAll();
  }

  public double getAllPlaytimeSinceOldestDate(Short targetEmployeeId, LocalDate oldestDate) {
    return dailyPlaytimeRepository
        .findAll()
        .stream()
        .filter(ticket -> ticket.getEmployeeId().equals(targetEmployeeId))
        .filter(ticket -> !ticket.getDate().isBefore(oldestDate))
        .mapToDouble(DailyPlaytime::getTimeInHours)
        .sum();
  }

  public double calculateTicketsPerPlaytime(int ticketsCount, double playtimeSinceOldestDate) {
    if (playtimeSinceOldestDate == 0) {
      return 0.0;
    }

    double playtimeInHours = playtimeSinceOldestDate;
    return ticketsCount / playtimeInHours;
  }

  public void saveTicketsPerPlaytime(double ticketsPerPlaytime, Short employeeId) {
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
  public void calculateTotalMinecraftTickets() {
    List<Short> allEmployeeIds = getAllEmployeeIds();
    List<DailyMinecraftTickets> allDailyMcTicketsData = getAllDailyMcTicketsData();
    List<TotalOldMinecraftTickets> allOldTotalMinecraftTicketsData = getOldTotalDailyMinecraftTicketsData();

    for (Short employeeId : allEmployeeIds) {
      int totalMinecraftTickets = getTotalMinecraftTicketsThisEmployee(
          allDailyMcTicketsData, employeeId);
      int totalOldMinecraftTickets = getTotalOldMinecraftTicketsThisEmployee(
          allOldTotalMinecraftTicketsData, employeeId);
      int sumWithOldTickets = calculateTotalMinecraftTicketsThisEmployeeWithOldOnes(
          totalMinecraftTickets, totalOldMinecraftTickets);

      saveTotalMinecraftTicketsThisEmployee(sumWithOldTickets, employeeId);
    }
  }

  public List<Short> getAllEmployeeIds() {
    return employeeRepository
        .findAll()
        .stream()
        .map(Employee::getId)
        .distinct()
        .collect(Collectors.toList());
  }

  public List<DailyMinecraftTickets> getAllDailyMcTicketsData() {
    return dailyMinecraftTicketsRepository.findAll();
  }

  public List<TotalOldMinecraftTickets> getOldTotalDailyMinecraftTicketsData() {
    return totalOldMinecraftTicketsRepository.findAll();
  }

  public int getTotalMinecraftTicketsThisEmployee(
      List<DailyMinecraftTickets> allDailyMcTicketsData, Short employeeId) {
    return allDailyMcTicketsData
        .stream()
        .filter(dailyMcTickets -> dailyMcTickets.getEmployeeId().equals(employeeId))
        .mapToInt(DailyMinecraftTickets::getTicketCount)
        .sum();
  }

  public int getTotalOldMinecraftTicketsThisEmployee(
      List<TotalOldMinecraftTickets> allOldTotalMinecraftTicketsData, Short employeeId) {
    return allOldTotalMinecraftTicketsData
        .stream()
        .filter(oldTotal -> oldTotal.getEmployeeId().equals(employeeId))
        .mapToInt(TotalOldMinecraftTickets::getTicketCount)
        .sum();
  }

  public int calculateTotalMinecraftTicketsThisEmployeeWithOldOnes(
      int totalMinecraftTickets, int totalOldMinecraftTickets) {
    return totalMinecraftTickets + totalOldMinecraftTickets;
  }

  public void saveTotalMinecraftTicketsThisEmployee(int sumWithOldTickets, Short employeeId) {
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
}
