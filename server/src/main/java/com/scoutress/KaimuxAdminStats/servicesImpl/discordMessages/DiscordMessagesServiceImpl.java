package com.scoutress.KaimuxAdminStats.servicesImpl.discordMessages;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.discordMessages.AverageDailyDiscordMessages;
import com.scoutress.KaimuxAdminStats.entity.discordMessages.DailyDiscordMessages;
import com.scoutress.KaimuxAdminStats.entity.discordMessages.DiscordRawMessagesCounts;
import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;
import com.scoutress.KaimuxAdminStats.repositories.discordMessages.AverageDailyDiscordMessagesRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordMessages.DailyDiscordMessagesRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.services.discordMessages.DiscordMessagesService;

import jakarta.transaction.Transactional;

@Service
public class DiscordMessagesServiceImpl implements DiscordMessagesService {

  private final DailyDiscordMessagesRepository dailyDiscordMessagesRepository;
  private final EmployeeRepository employeeRepository;
  private final AverageDailyDiscordMessagesRepository averageDailyDiscordMessagesRepository;

  public DiscordMessagesServiceImpl(
      DailyDiscordMessagesRepository dailyDiscordMessagesRepository,
      EmployeeRepository employeeRepository,
      AverageDailyDiscordMessagesRepository averageDailyDiscordMessagesRepository) {
    this.dailyDiscordMessagesRepository = dailyDiscordMessagesRepository;
    this.employeeRepository = employeeRepository;
    this.averageDailyDiscordMessagesRepository = averageDailyDiscordMessagesRepository;
  }

  @Override
  public void convertDailyDiscordMessages(
      List<DiscordRawMessagesCounts> rawDcMessagesData,
      List<EmployeeCodes> employeeCodesData) {
    List<String> allUserIds = getAllUserIdsFromRawData(rawDcMessagesData);

    for (String userID : allUserIds) {
      Long userIdAsLong = Long.valueOf(userID);
      Short employeeId = getEmployeeIdByUserId(employeeCodesData, userIdAsLong);
      List<LocalDate> listOfDatesThisUser = getAllDatesForThisUserFromRawData(rawDcMessagesData, userID);

      for (LocalDate date : listOfDatesThisUser) {
        int messagesValue = getMessagesValueForThisUserThisDate(rawDcMessagesData, userID, date);
        saveConvertedValue(messagesValue, employeeId, date);
      }
    }
  }

  public List<String> getAllUserIdsFromRawData(List<DiscordRawMessagesCounts> rawDcMessagesData) {
    return rawDcMessagesData
        .stream()
        .map(DiscordRawMessagesCounts::getDcUserId)
        .distinct()
        .sorted()
        .toList();
  }

  public Short getEmployeeIdByUserId(List<EmployeeCodes> allEmployeeCodes, Long userIdAsLong) {
    return allEmployeeCodes
        .stream()
        .filter(employee -> employee.getDiscordUserId().equals(userIdAsLong))
        .map(EmployeeCodes::getEmployeeId)
        .findFirst()
        .orElseThrow(() -> new RuntimeException("Employee not found"));
  }

  public List<LocalDate> getAllDatesForThisUserFromRawData(
      List<DiscordRawMessagesCounts> rawDcMessagesData, String userID) {
    return rawDcMessagesData
        .stream()
        .filter(dcMessage -> dcMessage.getDcUserId().equals(userID))
        .map(DiscordRawMessagesCounts::getMessageDate)
        .distinct()
        .sorted()
        .toList();
  }

  public int getMessagesValueForThisUserThisDate(
      List<DiscordRawMessagesCounts> rawDcMessagesData, String userID, LocalDate date) {
    return rawDcMessagesData
        .stream()
        .filter(dcMessage -> dcMessage.getDcUserId().equals(userID))
        .filter(dcMessage -> dcMessage.getMessageDate().equals(date))
        .mapToInt(DiscordRawMessagesCounts::getMessageCount)
        .sum();
  }

  public void saveConvertedValue(int messagesValue, Short employeeId, LocalDate date) {
    try {
      DailyDiscordMessages existingRecord = dailyDiscordMessagesRepository.findByEmployeeIdAndDate(employeeId, date);

      if (existingRecord != null) {
        existingRecord.setMsgCount(messagesValue);
        dailyDiscordMessagesRepository.save(existingRecord);
      } else {
        DailyDiscordMessages newRecord = new DailyDiscordMessages();
        newRecord.setEmployeeId(employeeId);
        newRecord.setMsgCount(messagesValue);
        newRecord.setDate(date);
        dailyDiscordMessagesRepository.save(newRecord);
      }
    } catch (org.springframework.dao.IncorrectResultSizeDataAccessException e) {
      System.err.println("Error: " + e.getMessage());
    }
  }

  @Override
  @Transactional
  public void removeDailyDiscordMessagesDuplicates(List<DailyDiscordMessages> allDailyDcMessages) {
    Map<String, List<DailyDiscordMessages>> groupedByEmployeeIdAndDate = allDailyDcMessages
        .stream()
        .collect(Collectors.groupingBy(message -> message.getEmployeeId() + "-" + message.getDate()));

    groupedByEmployeeIdAndDate.forEach((key, messages) -> {
      messages.stream()
          .sorted(Comparator.comparing(DailyDiscordMessages::getId))
          .skip(1)
          .forEach(msg -> {
            dailyDiscordMessagesRepository.delete(msg);
          });
    });
  }

  @Override
  public void calculateAverageValueOfDailyDiscordMessages(
      List<DailyDiscordMessages> allDailyDcMessages,
      List<Short> allEmployeesFromDailyDcMessages) {

    if (allDailyDcMessages != null && !allDailyDcMessages.isEmpty()) {
      LocalDate oldestDateFromDcMessagesData = getOldestDateFromMessagesData(allDailyDcMessages);

      for (Short employeeId : allEmployeesFromDailyDcMessages) {
        LocalDate joinDateThisEmployee = getJoinDateThisEmployee(employeeId);
        LocalDate oldestDate = checkIfJoinDateIsAfterOldestDateFromMsgData(
            oldestDateFromDcMessagesData, joinDateThisEmployee);
        double averageValue = calculateAverageValueOfDiscordMessagesThisEmployee(
            allDailyDcMessages, oldestDate, employeeId);

        saveAverageValueForThisEmployee(averageValue, employeeId);
      }
    }
  }

  public LocalDate getOldestDateFromMessagesData(List<DailyDiscordMessages> rawData) {
    return rawData
        .stream()
        .map(DailyDiscordMessages::getDate)
        .min(LocalDate::compareTo)
        .orElseThrow(() -> new RuntimeException("No dates found in the database"));
  }

  public LocalDate getJoinDateThisEmployee(Short employeeId) {
    return employeeRepository
        .findAll()
        .stream()
        .filter(employee -> employee.getId().equals(employeeId))
        .map(Employee::getJoinDate)
        .findFirst()
        .orElseThrow(() -> new RuntimeException("No join date for this employee"));
  }

  public LocalDate checkIfJoinDateIsAfterOldestDateFromMsgData(
      LocalDate oldestDateFromDcMessagesData, LocalDate joinDateThisEmployee) {
    return joinDateThisEmployee.isAfter(oldestDateFromDcMessagesData)
        ? joinDateThisEmployee
        : oldestDateFromDcMessagesData;
  }

  public double calculateAverageValueOfDiscordMessagesThisEmployee(
      List<DailyDiscordMessages> rawData, LocalDate oldestDate, Short employee) {
    int discordMessagesSumThisEmployee = calculateDiscordMessagesSumForThisEmployee(rawData, employee);
    int daysCountAfterOldestDate = calculateDaysAfterOldestDate(oldestDate);
    return calculateAverageValue(discordMessagesSumThisEmployee, daysCountAfterOldestDate);
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
    return (double) discordMessagesSumThisEmployee / daysCountAfterOldestDate;
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
