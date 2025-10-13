package com.scoutress.KaimuxAdminStats.servicesImpl.discordMessages;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
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

@Service
public class DiscordMessagesServiceImpl implements DiscordMessagesService {

  private static final Logger log = LoggerFactory.getLogger(DiscordMessagesServiceImpl.class);

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

  // ======================================================
  // CONVERT RAW DISCORD MESSAGES
  // ======================================================
  @Override
  public void convertDailyDiscordMessages(
      List<DiscordRawMessagesCounts> rawDcMessagesData,
      List<EmployeeCodes> employeeCodesData) {

    log.info("=== Starting Discord message data conversion ===");

    List<String> allUserIds = getAllUserIdsFromRawData(rawDcMessagesData);
    log.debug("Found {} unique Discord user IDs.", allUserIds.size());

    for (String userID : allUserIds) {
      try {
        Long userIdAsLong = Long.valueOf(userID);
        Short employeeId = getEmployeeIdByUserId(employeeCodesData, userIdAsLong);
        List<LocalDate> listOfDatesThisUser = getAllDatesForThisUserFromRawData(rawDcMessagesData, userID);

        for (LocalDate date : listOfDatesThisUser) {
          int messagesValue = getMessagesValueForThisUserThisDate(rawDcMessagesData, userID, date);
          saveConvertedValue(messagesValue, employeeId, date);
        }

      } catch (RuntimeException e) {
        log.warn("⚠️ Skipping user ID {}: {}", userID, e.getMessage());
      }
    }

    log.info("✅ Discord message data conversion completed.");
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
        .orElseThrow(() -> new RuntimeException("Employee not found for Discord user ID: " + userIdAsLong));
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
        log.debug("Updated daily message record for employee {} on {}: {}", employeeId, date, messagesValue);
      } else {
        DailyDiscordMessages newRecord = new DailyDiscordMessages();
        newRecord.setEmployeeId(employeeId);
        newRecord.setMsgCount(messagesValue);
        newRecord.setDate(date);
        dailyDiscordMessagesRepository.save(newRecord);
        log.trace("Inserted new daily message record for employee {} on {}: {}", employeeId, date, messagesValue);
      }
    } catch (IncorrectResultSizeDataAccessException e) {
      log.error("❌ Duplicate record issue for employee {} on {}: {}", employeeId, date, e.getMessage());
    }
  }

  // ======================================================
  // CALCULATE AVERAGES
  // ======================================================
  @Override
  public void calculateAverageValueOfDailyDiscordMessages(
      List<DailyDiscordMessages> allDailyDcMessages,
      List<Short> allEmployeesFromDailyDcMessages) {

    log.info("=== Starting Discord average message value calculation ===");

    if (allDailyDcMessages == null || allDailyDcMessages.isEmpty()) {
      log.warn("⚠️ No Discord message data found — skipping calculation.");
      return;
    }

    if (allEmployeesFromDailyDcMessages == null || allEmployeesFromDailyDcMessages.isEmpty()) {
      log.warn("⚠️ No employees found in Discord message data — skipping calculation.");
      return;
    }

    LocalDate oldestDate = getOldestDateFromMessagesData(allDailyDcMessages);
    log.debug("Oldest message date in dataset: {}", oldestDate);

    for (Short employeeId : allEmployeesFromDailyDcMessages) {
      try {
        LocalDate joinDate = getJoinDateThisEmployee(employeeId);

        if (joinDate == null) {
          log.warn("[{}] Skipping employee {} — join date missing.", LocalDateTime.now(), employeeId);
          continue;
        }

        LocalDate effectiveStartDate = checkIfJoinDateIsAfterOldestDateFromMsgData(oldestDate, joinDate);

        double avgValue = calculateAverageValueOfDiscordMessagesThisEmployee(allDailyDcMessages, effectiveStartDate,
            employeeId);

        saveAverageValueForThisEmployee(avgValue, employeeId);

        log.debug("Employee {} — average daily messages: {}", employeeId, avgValue);

      } catch (Exception e) {
        log.error("❌ Error processing employee {}: {}", employeeId, e.getMessage(), e);
      }
    }

    log.info("✅ Completed Discord average message value calculation.");
  }

  public LocalDate getOldestDateFromMessagesData(List<DailyDiscordMessages> rawData) {
    return rawData
        .stream()
        .map(DailyDiscordMessages::getDate)
        .min(LocalDate::compareTo)
        .orElse(LocalDate.now());
  }

  public LocalDate getJoinDateThisEmployee(Short employeeId) {
    return employeeRepository
        .findAll()
        .stream()
        .filter(e -> e.getId().equals(employeeId))
        .map(Employee::getJoinDate)
        .findFirst()
        .orElse(null);
  }

  public LocalDate checkIfJoinDateIsAfterOldestDateFromMsgData(LocalDate oldestDate, LocalDate joinDate) {
    return joinDate.isAfter(oldestDate) ? joinDate : oldestDate;
  }

  public double calculateAverageValueOfDiscordMessagesThisEmployee(List<DailyDiscordMessages> rawData,
      LocalDate startDate, Short employee) {
    int totalMessages = calculateDiscordMessagesSumForThisEmployee(rawData, employee);
    int daysCount = calculateDaysAfterOldestDate(startDate);
    return calculateAverageValue(totalMessages, daysCount);
  }

  public int calculateDiscordMessagesSumForThisEmployee(List<DailyDiscordMessages> rawData, Short employee) {
    return rawData
        .stream()
        .filter(messages -> messages.getEmployeeId().equals(employee))
        .mapToInt(DailyDiscordMessages::getMsgCount)
        .sum();
  }

  public int calculateDaysAfterOldestDate(LocalDate oldestDate) {
    return (int) ChronoUnit.DAYS.between(oldestDate, LocalDate.now());
  }

  public double calculateAverageValue(int totalMessages, int daysCount) {
    if (daysCount == 0)
      return 0;
    return (double) totalMessages / daysCount;
  }

  public void saveAverageValueForThisEmployee(double avgValue, Short employeeId) {
    AverageDailyDiscordMessages record = averageDailyDiscordMessagesRepository.findByEmployeeId(employeeId);

    if (record != null) {
      record.setValue(avgValue);
      averageDailyDiscordMessagesRepository.save(record);
      log.trace("Updated average message record for employee {}: {}", employeeId, avgValue);
    } else {
      AverageDailyDiscordMessages newRecord = new AverageDailyDiscordMessages();
      newRecord.setEmployeeId(employeeId);
      newRecord.setValue(avgValue);
      averageDailyDiscordMessagesRepository.save(newRecord);
      log.trace("Inserted new average message record for employee {}: {}", employeeId, avgValue);
    }
  }
}
