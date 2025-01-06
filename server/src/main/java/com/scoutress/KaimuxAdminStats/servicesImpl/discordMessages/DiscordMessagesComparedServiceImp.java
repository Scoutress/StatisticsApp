package com.scoutress.KaimuxAdminStats.servicesImpl.discordMessages;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.discordMessages.AverageDiscordMessagesCompared;
import com.scoutress.KaimuxAdminStats.entity.discordMessages.DailyDiscordMessages;
import com.scoutress.KaimuxAdminStats.entity.discordMessages.DailyDiscordMessagesCompared;
import com.scoutress.KaimuxAdminStats.repositories.discordMessages.AverageDiscordMessagesComparedRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordMessages.DailyDiscordMessagesComparedRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordMessages.DailyDiscordMessagesRepository;
import com.scoutress.KaimuxAdminStats.services.discordMessages.DiscordMessagesComparedService;

@Service
public class DiscordMessagesComparedServiceImp implements DiscordMessagesComparedService {

  private final DailyDiscordMessagesRepository dailyDiscordMessagesRepository;
  private final DailyDiscordMessagesComparedRepository dailyDiscordMessagesComparedRepository;
  private final AverageDiscordMessagesComparedRepository averageDiscordMessagesComparedRepository;

  public DiscordMessagesComparedServiceImp(
      DailyDiscordMessagesRepository dailyDiscordMessagesRepository,
      DailyDiscordMessagesComparedRepository dailyDiscordMessagesComparedRepository,
      AverageDiscordMessagesComparedRepository averageDiscordMessagesComparedRepository) {
    this.dailyDiscordMessagesRepository = dailyDiscordMessagesRepository;
    this.dailyDiscordMessagesComparedRepository = dailyDiscordMessagesComparedRepository;
    this.averageDiscordMessagesComparedRepository = averageDiscordMessagesComparedRepository;
  }

  @Override
  public void compareEachEmployeeDailyDiscordMessagesValues() {
    List<DailyDiscordMessages> rawData = getAllDiscordMessages();

    if (rawData == null || rawData.isEmpty()) {
      throw new RuntimeException("No Discord messages data found. Cannot proceed.");
    }

    List<LocalDate> allDates = getAllDiscordMessagesDates(rawData);

    if (allDates == null || allDates.isEmpty()) {
      throw new RuntimeException("No dates found in Discord messages data. Cannot proceed.");
    }

    List<Short> allEmployees = getAllEmployeesFromDailyDiscordMessages(rawData);

    if (allEmployees == null || allEmployees.isEmpty()) {
      throw new RuntimeException("No employees found in Discord messages data. Cannot proceed.");
    }

    for (Short employee : allEmployees) {
      double messagesRatioSumThisEmployee = 0;
      int datesCount = 0;

      for (LocalDate date : allDates) {
        try {
          int messagesThisDateThisEmployee = getMessagesCountThisDateThisEmployee(rawData, date, employee);
          int messagesThisDateAllEmployees = getMessagesCountThisDateAllEmployees(rawData, date);

          if (messagesThisDateAllEmployees == 0) {
            System.err.println("Total messages for all employees on " + date + " is zero. Skipping.");
            continue;
          }

          double messagesRatioThisDateThisEmployee = calculateMessagesRatioThisDate(
              messagesThisDateThisEmployee, messagesThisDateAllEmployees);

          saveMessagesRatioThisDateThisEmployee(messagesRatioThisDateThisEmployee, date, employee);

          messagesRatioSumThisEmployee += messagesRatioThisDateThisEmployee;
          datesCount++;
        } catch (Exception e) {
          System.err.println("Error processing employee " + employee + " on date " + date + ": " + e.getMessage());
        }
      }

      if (datesCount > 0) {
        double averageValueOfMessagesRatiosThisEmployee = calculateAverageMessagesRatioThisEmployee(
            messagesRatioSumThisEmployee, datesCount);

        saveAverageMessagesRatioThisEmployee(averageValueOfMessagesRatiosThisEmployee, employee);
      } else {
        System.err.println("No valid data for employee " + employee + ". Skipping average calculation.");
      }
    }
  }

  public List<DailyDiscordMessages> getAllDiscordMessages() {
    return dailyDiscordMessagesRepository.findAll();
  }

  public List<LocalDate> getAllDiscordMessagesDates(List<DailyDiscordMessages> data) {
    return data
        .stream()
        .map(DailyDiscordMessages::getDate)
        .distinct()
        .collect(Collectors.toList());
  }

  public List<Short> getAllEmployeesFromDailyDiscordMessages(List<DailyDiscordMessages> data) {
    return data
        .stream()
        .map(DailyDiscordMessages::getEmployeeId)
        .distinct()
        .collect(Collectors.toList());
  }

  public int getMessagesCountThisDateThisEmployee(
      List<DailyDiscordMessages> data, LocalDate thisDate, Short thisEmployee) {
    return data
        .stream()
        .filter(messages -> messages.getEmployeeId().equals(thisEmployee) && messages.getDate().equals(thisDate))
        .map(DailyDiscordMessages::getMsgCount)
        .findFirst()
        .orElse(0);
  }

  public int getMessagesCountThisDateAllEmployees(List<DailyDiscordMessages> data, LocalDate thisDate) {
    return data
        .stream()
        .filter(messages -> messages.getDate().equals(thisDate))
        .mapToInt(DailyDiscordMessages::getMsgCount)
        .sum();
  }

  public double calculateMessagesRatioThisDate(int messagesThisDateThisEmployee, int messagesThisDateAllEmployees) {
    if (messagesThisDateAllEmployees == 0) {
      return 0;
    }
    return (double) messagesThisDateThisEmployee / messagesThisDateAllEmployees;
  }

  public double calculateAverageMessagesRatioThisEmployee(double messagesRatioSumThisEmployee, int datesCount) {
    if (datesCount == 0) {
      return 0;
    }
    return (double) messagesRatioSumThisEmployee / datesCount;
  }

  public void saveMessagesRatioThisDateThisEmployee(
      double messagesRatioThisDateThisEmployee, LocalDate date, Short employee) {
    DailyDiscordMessagesCompared existingRecord = dailyDiscordMessagesComparedRepository
        .findByEmployeeIdAndDate(employee, date);

    if (existingRecord != null) {
      existingRecord.setValue(messagesRatioThisDateThisEmployee);
      dailyDiscordMessagesComparedRepository.save(existingRecord);
    } else {
      DailyDiscordMessagesCompared newRecord = new DailyDiscordMessagesCompared();
      newRecord.setEmployeeId(employee);
      newRecord.setValue(messagesRatioThisDateThisEmployee);
      newRecord.setDate(date);
      dailyDiscordMessagesComparedRepository.save(newRecord);
    }
  }

  public void saveAverageMessagesRatioThisEmployee(double averageValueOfTicketRatiosThisEmployee, Short employee) {
    AverageDiscordMessagesCompared existingRecord = averageDiscordMessagesComparedRepository
        .findByEmployeeId(employee);

    if (existingRecord != null) {
      existingRecord.setValue(averageValueOfTicketRatiosThisEmployee);
      averageDiscordMessagesComparedRepository.save(existingRecord);
    } else {
      AverageDiscordMessagesCompared newRecord = new AverageDiscordMessagesCompared();
      newRecord.setEmployeeId(employee);
      newRecord.setValue(averageValueOfTicketRatiosThisEmployee);
      averageDiscordMessagesComparedRepository.save(newRecord);
    }
  }
}
