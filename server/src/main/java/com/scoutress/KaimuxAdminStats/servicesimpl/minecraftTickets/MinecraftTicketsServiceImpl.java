package com.scoutress.KaimuxAdminStats.servicesImpl.minecraftTickets;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.AverageDailyMinecraftTickets;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.AverageMinecraftTicketsPerPlaytime;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.DailyMinecraftTickets;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.MinecraftTicketsAnswers;
import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.AverageDailyMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.AverageMinecraftTicketsPerPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.DailyMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.DailyPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.services.DataExtractingService;
import com.scoutress.KaimuxAdminStats.services.minecraftTickets.MinecraftTicketsService;

@Service
public class MinecraftTicketsServiceImpl implements MinecraftTicketsService {

  public final DataExtractingService dataExtractingService;
  public final DailyMinecraftTicketsRepository minecraftTicketsRepository;
  public final AverageDailyMinecraftTicketsRepository averageDailyMinecraftTicketsRepository;
  public final DailyPlaytimeRepository dailyPlaytimeRepository;
  public final AverageMinecraftTicketsPerPlaytimeRepository averageMinecraftTicketsPerPlaytimeRepository;

  public MinecraftTicketsServiceImpl(
      DataExtractingService dataExtractingService,
      DailyMinecraftTicketsRepository discordTicketsRepository,
      AverageDailyMinecraftTicketsRepository averageDailyMinecraftTicketsRepository,
      DailyPlaytimeRepository dailyPlaytimeRepository,
      AverageMinecraftTicketsPerPlaytimeRepository averageMinecraftTicketsPerPlaytimeRepository) {
    this.dataExtractingService = dataExtractingService;
    this.minecraftTicketsRepository = discordTicketsRepository;
    this.averageDailyMinecraftTicketsRepository = averageDailyMinecraftTicketsRepository;
    this.dailyPlaytimeRepository = dailyPlaytimeRepository;
    this.averageMinecraftTicketsPerPlaytimeRepository = averageMinecraftTicketsPerPlaytimeRepository;
  }

  @Override
  public void convertMinecraftTicketsAnswers() {
    List<EmployeeCodes> employeeCodes = extractEmployeeCodes();

    if (employeeCodes == null || employeeCodes.isEmpty()) {
      throw new RuntimeException("No employee codes found. Cannot proceed.");
    }

    List<MinecraftTicketsAnswers> ticketsAnswers = extractDataFromAnswersTable();

    if (ticketsAnswers == null || ticketsAnswers.isEmpty()) {
      throw new RuntimeException("No tickets answers found. Cannot proceed.");
    }

    List<MinecraftTicketsAnswers> reactionsWithEmployeeIds = mapMinecraftToEmployeeIds(ticketsAnswers, employeeCodes);

    if (reactionsWithEmployeeIds == null || reactionsWithEmployeeIds.isEmpty()) {
      throw new RuntimeException("Failed to map tickets answers to employee IDs. Cannot proceed.");
    }

    List<DailyMinecraftTickets> convertedData = convertData(reactionsWithEmployeeIds);
    if (convertedData == null || convertedData.isEmpty()) {
      throw new RuntimeException("Data conversion failed. Cannot proceed.");
    }

    saveDataToNewTable(convertedData);
  }

  public List<EmployeeCodes> extractEmployeeCodes() {
    List<EmployeeCodes> data = dataExtractingService.getAllEmployeeCodes();
    return data;
  }

  public List<MinecraftTicketsAnswers> extractDataFromAnswersTable() {
    List<MinecraftTicketsAnswers> data = dataExtractingService.getAllMcTicketsAnswers();
    return data;
  }

  public List<MinecraftTicketsAnswers> mapMinecraftToEmployeeIds(
      List<MinecraftTicketsAnswers> answers,
      List<EmployeeCodes> employeeCodes) {

    Map<Short, Short> minecraftToEmployeeMap = employeeCodes
        .stream()
        .filter(code -> code.getMinecraftId() != null)
        .filter(code -> code.getEmployeeId() != null)
        .collect(Collectors
            .toMap(
                EmployeeCodes::getMinecraftId,
                EmployeeCodes::getEmployeeId));

    return answers
        .stream()
        .map(reaction -> {
          Short employeeId = minecraftToEmployeeMap
              .get(reaction
                  .getMinecraftTicketId()
                  .shortValue());
          if (employeeId != null) {
            reaction
                .setMinecraftTicketId(employeeId
                    .longValue());
          }
          return reaction;
        })
        .collect(Collectors.toList());
  }

  public List<DailyMinecraftTickets> convertData(List<MinecraftTicketsAnswers> rawData) {
    Map<Long, Map<LocalDate, Long>> groupedData = rawData.stream()
        .collect(Collectors.groupingBy(
            MinecraftTicketsAnswers::getMinecraftTicketId,
            Collectors.groupingBy(
                answer -> answer.getDateTime().toLocalDate(),
                Collectors.counting())));

    return groupedData.entrySet().stream()
        .flatMap(ticketEntry -> ticketEntry.getValue().entrySet().stream()
            .map(dateEntry -> new DailyMinecraftTickets(
                null,
                ticketEntry.getKey().shortValue(),
                dateEntry.getValue().intValue(),
                dateEntry.getKey())))
        .collect(Collectors.toList());
  }

  private void saveDataToNewTable(List<DailyMinecraftTickets> convertedData) {
    convertedData.sort((a, b) -> a.getDate().compareTo(b.getDate()));
    convertedData.forEach(minecraftTicketsRepository::save);
  }

  @Override
  public void calculateAverageDailyMinecraftTicketsValues() {
    List<Short> allEmployees = getAllEmployeesFromTheTable();

    if (allEmployees == null || allEmployees.isEmpty()) {
      throw new RuntimeException("No employees found in the table. Cannot proceed.");
    }

    LocalDate oldestDate = checkForOldestDate();

    if (oldestDate == null) {
      throw new RuntimeException("No oldest date found. Cannot proceed.");
    }

    int daysCount = calculateDaysAfterOldestDate(oldestDate);

    if (daysCount <= 0) {
      throw new RuntimeException("Invalid days count (" + daysCount + "). Cannot proceed.");
    }

    for (Short employee : allEmployees) {
      int ticketsCountSinceOldestDate = calculateAllTicketsSinceOldestDate(employee);
      double averageValue = calculateAverageTicketsValue(ticketsCountSinceOldestDate, daysCount);
      saveAverageValueData(averageValue, employee);
    }
  }

  public List<DailyMinecraftTickets> extractDataFromDailyMinecraftTicketsTable() {
    return minecraftTicketsRepository.findAll();
  }

  public List<Short> getAllEmployeesFromTheTable() {
    List<Short> employees = minecraftTicketsRepository
        .findAll()
        .stream()
        .map(DailyMinecraftTickets::getEmployeeId)
        .distinct()
        .sorted()
        .toList();
    return employees;
  }

  public LocalDate checkForOldestDate() {
    Optional<LocalDate> oldestDate = minecraftTicketsRepository
        .findAll()
        .stream()
        .map(DailyMinecraftTickets::getDate)
        .min(LocalDate::compareTo);

    if (oldestDate.isEmpty()) {
      System.err.println("No dates found in the database");
      return null;
    }
    return oldestDate.get();
  }

  public int calculateDaysAfterOldestDate(LocalDate oldestDate) {
    LocalDate today = LocalDate.now();
    long daysBetweenLong = ChronoUnit.DAYS.between(oldestDate, today);
    int daysBetween = (int) daysBetweenLong;
    return daysBetween;
  }

  public int calculateAllTicketsSinceOldestDate(int targetEmployeeId) {
    int ticketsSum = minecraftTicketsRepository
        .findAll()
        .stream()
        .filter(ticket -> ticket.getEmployeeId() == targetEmployeeId)
        .mapToInt(DailyMinecraftTickets::getTicketCount)
        .sum();
    return ticketsSum;
  }

  public double calculateAverageTicketsValue(int ticketsCountSinceOldestDate, int daysCount) {
    if (daysCount == 0) {
      return 0.0;
    }
    double average = ticketsCountSinceOldestDate / daysCount;
    return average;
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
    List<Short> allEmployees = getAllEmployeesFromTheTable();

    if (allEmployees == null || allEmployees.isEmpty()) {
      throw new RuntimeException("No employees found in the table. Cannot proceed.");
    }

    LocalDate oldestDate = checkForOldestDate();

    if (oldestDate == null) {
      throw new RuntimeException("No oldest date found. Cannot proceed.");
    }

    for (Short employee : allEmployees) {
      try {
        int ticketsCount = calculateAllTicketsSinceOldestDate(employee);
        double playtimeSinceOldestDate = getAllPlaytimeSinceOldestDate(employee, oldestDate);

        if (playtimeSinceOldestDate <= 0) {
          System.err.println("Invalid playtime for employee " + employee + ". Skipping.");
          continue;
        }

        double ticketsPerPlaytime = calculateTicketsPerPlaytime(ticketsCount, playtimeSinceOldestDate);
        saveTicketsPerPlaytime(ticketsPerPlaytime, employee);
      } catch (Exception e) {
        System.err.println("Error processing employee " + employee + ": " + e.getMessage());
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

    double playtimeInHours = playtimeSinceOldestDate / 3600;
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
}
