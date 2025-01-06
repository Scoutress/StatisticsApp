package com.scoutress.KaimuxAdminStats.servicesImpl.discordMessages;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.discordMessages.AverageDailyDiscordMessages;
import com.scoutress.KaimuxAdminStats.entity.discordMessages.DailyDiscordMessages;
import com.scoutress.KaimuxAdminStats.repositories.discordMessages.AverageDailyDiscordMessagesRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordMessages.DailyDiscordMessagesRepository;
import com.scoutress.KaimuxAdminStats.services.discordMessages.DiscordMessagesService;

@Service
public class DiscordMessagesServiceImpl implements DiscordMessagesService {

  public final DailyDiscordMessagesRepository dailyDiscordMessagesRepository;
  public final AverageDailyDiscordMessagesRepository averageDailyDiscordMessagesRepository;

  public DiscordMessagesServiceImpl(
      DailyDiscordMessagesRepository dailyDiscordMessagesRepository,
      AverageDailyDiscordMessagesRepository averageDailyDiscordMessagesRepository) {
    this.dailyDiscordMessagesRepository = dailyDiscordMessagesRepository;
    this.averageDailyDiscordMessagesRepository = averageDailyDiscordMessagesRepository;
  }

  @Override
  public void calculateAverageValueOfDailyDiscordMessages() {
    try {
      List<DailyDiscordMessages> rawData = getAllDiscordMessages();

      if (rawData == null || rawData.isEmpty()) {
        throw new RuntimeException("No Discord messages found in the database.");
      }

      List<Short> allEmployees = getAllEmployeesFromMessagesData(rawData);
      LocalDate oldestDate = getOldestDateFromMessagesData(rawData);

      if (allEmployees == null || oldestDate == null) {
        throw new RuntimeException("Missing required data (employees or oldest date).");
      }

      for (Short employee : allEmployees) {
        double averageValue = calculateAverageValueOfDiscordMessagesThisEmployee(rawData, oldestDate, employee);
        saveAverageValueForThisEmployee(averageValue, employee);
      }
    } catch (RuntimeException e) {
      System.err.println("Error in calculateAverageValueOfDailyDiscordMessages: " + e.getMessage());
    }
  }

  public List<DailyDiscordMessages> getAllDiscordMessages() {
    return dailyDiscordMessagesRepository.findAll();
  }

  public List<Short> getAllEmployeesFromMessagesData(List<DailyDiscordMessages> rawData) {
    return rawData
        .stream()
        .map(DailyDiscordMessages::getEmployeeId)
        .distinct()
        .sorted()
        .toList();
  }

  public LocalDate getOldestDateFromMessagesData(List<DailyDiscordMessages> rawData) {
    return rawData
        .stream()
        .map(DailyDiscordMessages::getDate)
        .min(LocalDate::compareTo)
        .orElseThrow(() -> new RuntimeException("No dates found in the database"));
  }

  public double calculateAverageValueOfDiscordMessagesThisEmployee(
      List<DailyDiscordMessages> rawData, LocalDate oldestDate, Short employee) {
    int discordMessagesSumThisEmployee = calculateDiscordMessagesSumForThisEmployee(rawData, employee);
    int daysCountAfterOldestDate = calculateDaysAfterOldestDate(oldestDate);
    double averageValue = calculateAverageValue(discordMessagesSumThisEmployee, daysCountAfterOldestDate);

    return averageValue;
  }

  public int calculateDiscordMessagesSumForThisEmployee(List<DailyDiscordMessages> rawData, Short employee) {
    return rawData
        .stream()
        .filter(messages -> messages.getEmployeeId().equals(employee))
        .mapToInt(DailyDiscordMessages::getMsgCount)
        .sum();
  }

  public int calculateDaysAfterOldestDate(LocalDate oldestDate) {
    LocalDate today = LocalDate.now();
    long daysBetweenLong = ChronoUnit.DAYS.between(oldestDate, today);
    int daysBetween = (int) daysBetweenLong;
    return daysBetween;
  }

  public double calculateAverageValue(int discordMessagesSumThisEmployee, int daysCountAfterOldestDate) {
    if (daysCountAfterOldestDate == 0) {
      return 0;
    }
    return discordMessagesSumThisEmployee / daysCountAfterOldestDate;
  }

  public void saveAverageValueForThisEmployee(double averageValueOfDiscordMessagesThisEmployee, Short employeeId) {
    AverageDailyDiscordMessages existingRecord = averageDailyDiscordMessagesRepository.findByEmployeeId(employeeId);

    if (existingRecord != null) {
      existingRecord.setValue(averageValueOfDiscordMessagesThisEmployee);
      averageDailyDiscordMessagesRepository.save(existingRecord);
    } else {
      AverageDailyDiscordMessages newRecord = new AverageDailyDiscordMessages();
      newRecord.setEmployeeId(employeeId);
      newRecord.setValue(averageValueOfDiscordMessagesThisEmployee);
      averageDailyDiscordMessagesRepository.save(newRecord);
    }
  }
}
