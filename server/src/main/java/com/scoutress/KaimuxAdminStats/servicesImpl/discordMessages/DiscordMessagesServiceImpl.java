package com.scoutress.KaimuxAdminStats.servicesImpl.discordMessages;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.discordMessages.AverageDailyDiscordMessages;
import com.scoutress.KaimuxAdminStats.entity.discordMessages.DailyDiscordMessages;
import com.scoutress.KaimuxAdminStats.entity.discordMessages.DiscordRawMessagesCounts;
import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;
import com.scoutress.KaimuxAdminStats.repositories.discordMessages.AverageDailyDiscordMessagesRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordMessages.DailyDiscordMessagesRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordMessages.DiscordRawMessagesCountsRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeCodesRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.services.discordMessages.DiscordMessagesService;

@Service
public class DiscordMessagesServiceImpl implements DiscordMessagesService {

  private final DailyDiscordMessagesRepository dailyDiscordMessagesRepository;
  private final EmployeeRepository employeeRepository;
  private final AverageDailyDiscordMessagesRepository averageDailyDiscordMessagesRepository;
  private final DiscordRawMessagesCountsRepository discordRawMessagesCountsRepository;
  private final EmployeeCodesRepository employeeCodesRepository;

  public DiscordMessagesServiceImpl(
      DailyDiscordMessagesRepository dailyDiscordMessagesRepository,
      EmployeeRepository employeeRepository,
      AverageDailyDiscordMessagesRepository averageDailyDiscordMessagesRepository,
      DiscordRawMessagesCountsRepository discordRawMessagesCountsRepository,
      EmployeeCodesRepository employeeCodesRepository) {
    this.dailyDiscordMessagesRepository = dailyDiscordMessagesRepository;
    this.employeeRepository = employeeRepository;
    this.averageDailyDiscordMessagesRepository = averageDailyDiscordMessagesRepository;
    this.discordRawMessagesCountsRepository = discordRawMessagesCountsRepository;
    this.employeeCodesRepository = employeeCodesRepository;
  }

  @Override
  public void convertDailyDiscordMessagesValue() {
    List<DiscordRawMessagesCounts> rawDcMessagesData = getAllRawDcMessagesData();
    List<EmployeeCodes> allEmployeeCodes = getAllEmployeeCodes();
    List<String> allUserIds = getAllUserIdsFromRawData(rawDcMessagesData);

    for (String userID : allUserIds) {
      Long userIdAsLong = convertUserIdToLong(userID);
      Short employeeId = getEmployeeIdByUserId(allEmployeeCodes, userIdAsLong);
      List<LocalDate> listOfDatesThisUser = getAllDatesForThisUserFromRawData(rawDcMessagesData, userIdAsLong);

      for (LocalDate date : listOfDatesThisUser) {
        int messagesValue = getMessagesValueForThisUserThisDate(rawDcMessagesData, userIdAsLong, date);
        saveConvertedValue(messagesValue, employeeId, date);
      }
    }
    removeRawData();
  }

  public List<DiscordRawMessagesCounts> getAllRawDcMessagesData() {
    return discordRawMessagesCountsRepository.findAll();
  }

  public List<EmployeeCodes> getAllEmployeeCodes() {
    return employeeCodesRepository.findAll();
  }

  public List<String> getAllUserIdsFromRawData(List<DiscordRawMessagesCounts> rawDcMessagesData) {
    return rawDcMessagesData
        .stream()
        .map(DiscordRawMessagesCounts::getDcUserId)
        .distinct()
        .sorted()
        .toList();
  }

  public Long convertUserIdToLong(String userId) {
    return Long.valueOf(userId);
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
      List<DiscordRawMessagesCounts> rawDcMessagesData, Long userIdAsLong) {
    return rawDcMessagesData
        .stream()
        .filter(dcMessage -> dcMessage.getDcUserId().equals(userIdAsLong.toString()))
        .map(DiscordRawMessagesCounts::getMessageDate)
        .distinct()
        .sorted()
        .toList();
  }

  public int getMessagesValueForThisUserThisDate(
      List<DiscordRawMessagesCounts> rawDcMessagesData, Long userIdAsLong, LocalDate date) {
    return rawDcMessagesData
        .stream()
        .filter(dcMessage -> dcMessage.getDcUserId().equals(userIdAsLong.toString()))
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
      System.err.println(e);
    }
  }

  public void removeRawData() {
    discordRawMessagesCountsRepository.deleteAll();
  }

  @Override
  public void calculateAverageValueOfDailyDiscordMessages() {
    List<DailyDiscordMessages> rawData = getAllDiscordMessages();

    if (rawData != null && !rawData.isEmpty()) {
      List<Short> allEmployees = getAllEmployeesFromMessagesData(rawData);
      LocalDate oldestDateFromDcMessagesData = getOldestDateFromMessagesData(rawData);

      if (allEmployees != null && oldestDateFromDcMessagesData != null) {

        for (Short employeeId : allEmployees) {
          LocalDate joinDateThisEmployee = getJoinDateThisEmployee(employeeId);
          LocalDate oldestDate = checkIfJoinDateIsAfterOldestDateFromMsgData(
              oldestDateFromDcMessagesData, joinDateThisEmployee);
          double averageValue = calculateAverageValueOfDiscordMessagesThisEmployee(
              rawData, oldestDate, employeeId);

          saveAverageValueForThisEmployee(averageValue, employeeId);
        }
      }
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
