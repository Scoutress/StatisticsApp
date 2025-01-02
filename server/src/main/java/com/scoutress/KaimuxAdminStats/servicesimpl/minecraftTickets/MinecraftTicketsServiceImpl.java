package com.scoutress.KaimuxAdminStats.servicesImpl.minecraftTickets;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.AverageDailyMinecraftTickets;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.DailyMinecraftTickets;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.MinecraftTicketsAnswers;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.AverageDailyMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.DailyMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.services.DataExtractingService;
import com.scoutress.KaimuxAdminStats.services.minecraftTickets.MinecraftTicketsService;

@Service
public class MinecraftTicketsServiceImpl implements MinecraftTicketsService {

  public final DataExtractingService dataExtractingService;
  public final DailyMinecraftTicketsRepository minecraftTicketsRepository;
  public final AverageDailyMinecraftTicketsRepository averageDailyMinecraftTicketsRepository;

  public MinecraftTicketsServiceImpl(
      DataExtractingService dataExtractingService,
      DailyMinecraftTicketsRepository discordTicketsRepository,
      AverageDailyMinecraftTicketsRepository averageDailyMinecraftTicketsRepository) {
    this.dataExtractingService = dataExtractingService;
    this.minecraftTicketsRepository = discordTicketsRepository;
    this.averageDailyMinecraftTicketsRepository = averageDailyMinecraftTicketsRepository;
  }

  @Override
  public void convertMinecraftTicketsAnswers() {
    List<EmployeeCodes> employeeCodes = extractEmployeeCodes();
    List<MinecraftTicketsAnswers> ticketsAnswers = extractDataFromAnswersTable();
    List<MinecraftTicketsAnswers> reactionsWithEmployeeIds = mapMinecraftToEmployeeIds(ticketsAnswers, employeeCodes);
    List<DailyMinecraftTickets> convertedData = convertData(reactionsWithEmployeeIds);

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
    List<DailyMinecraftTickets> rawData = extractDataFromDailyMinecraftTicketsTable();
    List<Short> allEmployees = getAllEmployeesFromTheTable();
    LocalDate oldestDate = checkForOldestDate(rawData);
    int daysCount = calculateDaysAfterOldestDate(oldestDate);

    for (Short employee : allEmployees) {
      int ticketsCountSinceOldestDate = calculateAllTicketsSinceOldestDate(rawData, employee);
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

  public LocalDate checkForOldestDate(List<DailyMinecraftTickets> rawData) {
    LocalDate oldestDate = minecraftTicketsRepository
        .findAll()
        .stream()
        .map(DailyMinecraftTickets::getDate)
        .min(LocalDate::compareTo)
        .orElseThrow(() -> new RuntimeException("No dates found in the database"));
    return oldestDate;
  }

  public int calculateDaysAfterOldestDate(LocalDate oldestDate) {
    LocalDate today = LocalDate.now();
    long daysBetweenLong = ChronoUnit.DAYS.between(oldestDate, today);
    int daysBetween = (int) daysBetweenLong;
    return daysBetween;
  }

  public int calculateAllTicketsSinceOldestDate(List<DailyMinecraftTickets> rawData, int targetEmployeeId) {
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
}
