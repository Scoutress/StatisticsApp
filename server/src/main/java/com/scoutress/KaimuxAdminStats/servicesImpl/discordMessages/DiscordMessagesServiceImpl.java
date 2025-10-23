package com.scoutress.KaimuxAdminStats.servicesImpl.discordMessages;

import java.time.LocalDate;
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

    log.info("=== [START] Discord raw → daily message conversion ===");

    List<String> allUserIds = getAllUserIdsFromRawData(rawDcMessagesData);
    log.debug("Detected {} unique Discord user IDs for conversion.", allUserIds.size());
    allUserIds.forEach(uid -> log.trace("User ID detected: {}", uid));

    for (String userID : allUserIds) {
      log.debug("Processing user ID: {}", userID);
      try {
        Long userIdAsLong = Long.valueOf(userID);
        Short employeeId = getEmployeeIdByUserId(employeeCodesData, userIdAsLong);
        log.trace("User {} matched with employee ID {}", userID, employeeId);

        List<LocalDate> listOfDatesThisUser = getAllDatesForThisUserFromRawData(rawDcMessagesData, userID);
        log.trace("User {} has {} message dates: {}", userID, listOfDatesThisUser.size(), listOfDatesThisUser);

        for (LocalDate date : listOfDatesThisUser) {
          int messagesValue = getMessagesValueForThisUserThisDate(rawDcMessagesData, userID, date);
          log.trace("User {} → Date {} → Message count: {}", userID, date, messagesValue);
          saveConvertedValue(messagesValue, employeeId, date);
        }

      } catch (RuntimeException e) {
        log.warn("⚠️ Skipping user ID {}: {}", userID, e.getMessage());
      }
    }

    log.info("✅ [END] Discord message conversion completed.");
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
    Short id = allEmployeeCodes
        .stream()
        .filter(employee -> employee.getDiscordUserId().equals(userIdAsLong))
        .map(EmployeeCodes::getEmployeeId)
        .findFirst()
        .orElseThrow(() -> new RuntimeException("Employee not found for Discord user ID: " + userIdAsLong));
    log.trace("Mapped Discord user {} to employee ID {}", userIdAsLong, id);
    return id;
  }

  public List<LocalDate> getAllDatesForThisUserFromRawData(
      List<DiscordRawMessagesCounts> rawDcMessagesData, String userID) {
    List<LocalDate> dates = rawDcMessagesData
        .stream()
        .filter(dcMessage -> dcMessage.getDcUserId().equals(userID))
        .map(DiscordRawMessagesCounts::getMessageDate)
        .distinct()
        .sorted()
        .toList();
    log.trace("User {} → {} distinct message dates.", userID, dates.size());
    return dates;
  }

  public int getMessagesValueForThisUserThisDate(
      List<DiscordRawMessagesCounts> rawDcMessagesData, String userID, LocalDate date) {
    int total = rawDcMessagesData
        .stream()
        .filter(dcMessage -> dcMessage.getDcUserId().equals(userID))
        .filter(dcMessage -> dcMessage.getMessageDate().equals(date))
        .mapToInt(DiscordRawMessagesCounts::getMessageCount)
        .sum();
    log.trace("User {} on {} → total messages: {}", userID, date, total);
    return total;
  }

  public void saveConvertedValue(int messagesValue, Short employeeId, LocalDate date) {
    try {
      DailyDiscordMessages existingRecord = dailyDiscordMessagesRepository.findByEmployeeIdAndDate(employeeId, date);

      if (existingRecord != null) {
        existingRecord.setMsgCount(messagesValue);
        dailyDiscordMessagesRepository.save(existingRecord);
        log.debug("Updated record → employee={}, date={}, messages={}", employeeId, date, messagesValue);
      } else {
        DailyDiscordMessages newRecord = new DailyDiscordMessages();
        newRecord.setEmployeeId(employeeId);
        newRecord.setMsgCount(messagesValue);
        newRecord.setDate(date);
        dailyDiscordMessagesRepository.save(newRecord);
        log.trace("Inserted new record → employee={}, date={}, messages={}", employeeId, date, messagesValue);
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

    log.info("=== [START] Calculating average Discord message values per employee ===");

    if (allDailyDcMessages == null || allDailyDcMessages.isEmpty()) {
      log.warn("⚠️ No Discord message data found — skipping calculation.");
      return;
    }

    if (allEmployeesFromDailyDcMessages == null || allEmployeesFromDailyDcMessages.isEmpty()) {
      log.warn("⚠️ No employees found in Discord message data — skipping calculation.");
      return;
    }

    LocalDate oldestDate = getOldestDateFromMessagesData(allDailyDcMessages);
    log.debug("Oldest date in Discord dataset: {}", oldestDate);

    for (Short employeeId : allEmployeesFromDailyDcMessages) {
      log.debug("Calculating average for employee ID {}", employeeId);
      try {
        LocalDate joinDate = getJoinDateThisEmployee(employeeId);
        log.trace("Employee {} → join date: {}", employeeId, joinDate);

        if (joinDate == null) {
          log.warn("Skipping employee {} — join date missing.", employeeId);
          continue;
        }

        LocalDate effectiveStartDate = checkIfJoinDateIsAfterOldestDateFromMsgData(oldestDate, joinDate);
        log.trace("Employee {} → effective start date for avg calc: {}", employeeId, effectiveStartDate);

        double avgValue = calculateAverageValueOfDiscordMessagesThisEmployee(allDailyDcMessages, effectiveStartDate,
            employeeId);
        log.trace("Employee {} → calculated avg: {}", employeeId, avgValue);

        saveAverageValueForThisEmployee(avgValue, employeeId);
        log.debug("Employee {} → average value saved: {}", employeeId, avgValue);

      } catch (Exception e) {
        log.error("❌ Error processing employee {}: {}", employeeId, e.getMessage(), e);
      }
    }

    log.info("✅ [END] Discord average message calculation completed.");
  }

  public LocalDate getOldestDateFromMessagesData(List<DailyDiscordMessages> rawData) {
    LocalDate oldest = rawData
        .stream()
        .map(DailyDiscordMessages::getDate)
        .min(LocalDate::compareTo)
        .orElse(LocalDate.now());
    log.trace("Oldest date detected in message data: {}", oldest);
    return oldest;
  }

  public LocalDate getJoinDateThisEmployee(Short employeeId) {
    LocalDate joinDate = employeeRepository.findAll()
        .stream()
        .filter(e -> e.getId().equals(employeeId))
        .map(Employee::getJoinDate)
        .findFirst()
        .orElse(null);
    log.trace("Employee {} → join date fetched: {}", employeeId, joinDate);
    return joinDate;
  }

  public LocalDate checkIfJoinDateIsAfterOldestDateFromMsgData(LocalDate oldestDate, LocalDate joinDate) {
    LocalDate result = joinDate.isAfter(oldestDate) ? joinDate : oldestDate;
    log.trace("Comparing join vs oldest date: join={}, oldest={} → using {}", joinDate, oldestDate, result);
    return result;
  }

  public double calculateAverageValueOfDiscordMessagesThisEmployee(List<DailyDiscordMessages> rawData,
      LocalDate startDate, Short employee) {
    int totalMessages = calculateDiscordMessagesSumForThisEmployee(rawData, employee);
    int daysCount = calculateDaysAfterOldestDate(startDate);
    double avg = calculateAverageValue(totalMessages, daysCount);
    log.trace("Employee {} → total={}, days={}, avg={}", employee, totalMessages, daysCount, avg);
    return avg;
  }

  public int calculateDiscordMessagesSumForThisEmployee(List<DailyDiscordMessages> rawData, Short employee) {
    int total = rawData
        .stream()
        .filter(messages -> messages.getEmployeeId().equals(employee))
        .mapToInt(DailyDiscordMessages::getMsgCount)
        .sum();
    log.trace("Employee {} → total messages sum: {}", employee, total);
    return total;
  }

  public int calculateDaysAfterOldestDate(LocalDate oldestDate) {
    int days = (int) ChronoUnit.DAYS.between(oldestDate, LocalDate.now());
    log.trace("Days between {} and today: {}", oldestDate, days);
    return days;
  }

  public double calculateAverageValue(int totalMessages, int daysCount) {
    double avg = (daysCount == 0) ? 0 : (double) totalMessages / daysCount;
    log.trace("Calculating average → total={}, days={}, avg={}", totalMessages, daysCount, avg);
    return avg;
  }

  public void saveAverageValueForThisEmployee(double avgValue, Short employeeId) {
    AverageDailyDiscordMessages record = averageDailyDiscordMessagesRepository.findByEmployeeId(employeeId);

    if (record != null) {
      record.setValue(avgValue);
      averageDailyDiscordMessagesRepository.save(record);
      log.debug("Updated average record → employee={}, avg={}", employeeId, avgValue);
    } else {
      AverageDailyDiscordMessages newRecord = new AverageDailyDiscordMessages();
      newRecord.setEmployeeId(employeeId);
      newRecord.setValue(avgValue);
      averageDailyDiscordMessagesRepository.save(newRecord);
      log.trace("Inserted new average record → employee={}, avg={}", employeeId, avgValue);
    }
  }
}
