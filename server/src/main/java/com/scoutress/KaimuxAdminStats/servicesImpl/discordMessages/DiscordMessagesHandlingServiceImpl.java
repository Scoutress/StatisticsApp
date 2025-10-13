package com.scoutress.KaimuxAdminStats.servicesImpl.discordMessages;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.discordMessages.DailyDiscordMessages;
import com.scoutress.KaimuxAdminStats.entity.discordMessages.DiscordRawMessagesCounts;
import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;
import com.scoutress.KaimuxAdminStats.repositories.discordMessages.DailyDiscordMessagesRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordMessages.DiscordRawMessagesCountsRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeCodesRepository;
import com.scoutress.KaimuxAdminStats.services.DuplicatesRemoverService;
import com.scoutress.KaimuxAdminStats.services.discordMessages.DiscordMessagesComparedService;
import com.scoutress.KaimuxAdminStats.services.discordMessages.DiscordMessagesHandlingService;
import com.scoutress.KaimuxAdminStats.services.discordMessages.DiscordMessagesService;

@Service
public class DiscordMessagesHandlingServiceImpl implements DiscordMessagesHandlingService {

  private static final Logger log = LoggerFactory.getLogger(DiscordMessagesHandlingServiceImpl.class);

  private final EmployeeCodesRepository employeeCodesRepository;
  private final DailyDiscordMessagesRepository dailyDiscordMessagesRepository;
  private final DiscordBotServiceImpl discordBotServiceImpl;
  private final DiscordRawMessagesCountsRepository discordRawMessagesCountsRepository;
  private final DiscordMessagesService discordMessagesService;
  private final DiscordMessagesComparedService discordMessagesComparedService;
  private final DuplicatesRemoverService duplicatesRemoverService;

  public DiscordMessagesHandlingServiceImpl(
      EmployeeCodesRepository employeeCodesRepository,
      DailyDiscordMessagesRepository dailyDiscordMessagesRepository,
      DiscordBotServiceImpl discordBotServiceImpl,
      DiscordRawMessagesCountsRepository discordRawMessagesCountsRepository,
      DiscordMessagesService discordMessagesService,
      DiscordMessagesComparedService discordMessagesComparedService,
      DuplicatesRemoverService duplicatesRemoverService) {
    this.employeeCodesRepository = employeeCodesRepository;
    this.dailyDiscordMessagesRepository = dailyDiscordMessagesRepository;
    this.discordBotServiceImpl = discordBotServiceImpl;
    this.discordRawMessagesCountsRepository = discordRawMessagesCountsRepository;
    this.discordMessagesService = discordMessagesService;
    this.discordMessagesComparedService = discordMessagesComparedService;
    this.duplicatesRemoverService = duplicatesRemoverService;
  }

  @Override
  public void handleDiscordMessages() {
    log.info("=== Starting Discord messages handling process ===");

    try {
      List<DailyDiscordMessages> dailyMessages = getAllDailyDcMessages();
      List<EmployeeCodes> employeeCodes = getAllEmployeeCodes();
      List<Short> employeeIds = getAllEmployeeIds(employeeCodes);
      LocalDate todaysDate = LocalDate.now();

      log.debug("Loaded {} employee codes and {} daily Discord message records.",
          employeeCodes.size(),
          dailyMessages.size());

      removeOldRawDcMessagesData();

      List<Short> employeeIdsWithoutData = lookForEmployeesWhereDataIsEmpty(employeeIds, dailyMessages);
      log.debug("{} employees have no daily Discord data.", employeeIdsWithoutData.size());

      LocalDate latestDate = checkLatestDateFromData(employeeIdsWithoutData);
      log.info("Latest date found in Discord message data: {}", latestDate);

      log.info("🤖 Starting Discord bot message collection...");
      discordBotServiceImpl.checkOrStartDiscordBot();
      discordBotServiceImpl.sleepForHalfMin();
      discordBotServiceImpl.handleDcBotRequests(employeeCodes, latestDate, todaysDate, employeeIdsWithoutData);
      discordBotServiceImpl.sleepForHalfMin();
      discordBotServiceImpl.stopBot();
      log.info("✅ Discord bot data collection completed.");

      List<DiscordRawMessagesCounts> rawMessages = getAllRawDcMessagesData();
      log.debug("Fetched {} raw Discord message count records.", rawMessages.size());

      discordMessagesService.convertDailyDiscordMessages(rawMessages, employeeCodes);
      log.info("Converted raw Discord data into daily message records.");

      duplicatesRemoverService.removeDailyDiscordMessagesDuplicates();
      log.info("Removed duplicate daily Discord message entries.");

      discordMessagesService.calculateAverageValueOfDailyDiscordMessages(dailyMessages, employeeIds);
      log.info("Calculated average Discord message values per employee.");

      discordMessagesComparedService.compareEachEmployeeDailyDiscordMessagesValues(dailyMessages, employeeIds,
          employeeIdsWithoutData);
      log.info("Compared employee Discord activity ratios successfully.");

    } catch (Exception e) {
      log.error("❌ Error during Discord messages handling: {}", e.getMessage(), e);
    }

    log.info("=== Discord messages handling process completed ===");
  }

  private void removeOldRawDcMessagesData() {
    try {
      discordRawMessagesCountsRepository
          .truncateTable();
      log.info("Old raw Discord messages data removed.");
    } catch (Exception e) {
      log.error("Failed to truncate raw Discord messages table: {}", e.getMessage(), e);
    }
  }

  private List<DailyDiscordMessages> getAllDailyDcMessages() {
    return dailyDiscordMessagesRepository
        .findAll();
  }

  private List<Short> getAllEmployeeIds(List<EmployeeCodes> employeeCodes) {
    return employeeCodes
        .stream()
        .map(EmployeeCodes::getEmployeeId)
        .distinct()
        .collect(Collectors.toList());
  }

  private List<EmployeeCodes> getAllEmployeeCodes() {
    return employeeCodesRepository
        .findAll();
  }

  private List<Short> lookForEmployeesWhereDataIsEmpty(List<Short> employeeIds,
      List<DailyDiscordMessages> dailyMessages) {
    List<Short> employeeIdsWithoutDailyDcMsgsData = new ArrayList<>();

    for (Short employeeId : employeeIds) {
      boolean hasData = hasDailyDcMsgsData(employeeId, dailyMessages);

      if (!hasData) {
        employeeIdsWithoutDailyDcMsgsData.add(employeeId);
        log.warn("Employee {} has no daily Discord message data.", employeeId);
      }
    }

    return employeeIdsWithoutDailyDcMsgsData;
  }

  private boolean hasDailyDcMsgsData(Short employeeId, List<DailyDiscordMessages> allDailyDiscordMessagesData) {
    return allDailyDiscordMessagesData
        .stream()
        .filter(msg -> msg.getEmployeeId().equals(employeeId))
        .anyMatch(msg -> msg.getDate() != null && msg.getMsgCount() >= 0);
  }

  private LocalDate checkLatestDateFromData(List<Short> employeeIdsWithoutData) {
    List<DailyDiscordMessages> dailyMessages = getAllDailyDcMessages();
    List<EmployeeCodes> employeeCodes = getAllEmployeeCodes();
    List<Short> employeeIds = getAllEmployeeIds(employeeCodes);
    List<LocalDate> latestDcMsgDates = new ArrayList<>();

    for (Short employeeId : employeeIds) {
      if (!employeeIdsWithoutData.contains(employeeId)) {
        List<DailyDiscordMessages> employeeData = getAllDcMsgDataForThisEmployee(employeeId, dailyMessages);
        if (!employeeData.isEmpty()) {
          LocalDate latestDate = getLatestDateFromData(employeeData);
          latestDcMsgDates.add(latestDate);
        }
      }
    }

    return findOldestDateFromList(latestDcMsgDates);
  }

  private List<DailyDiscordMessages> getAllDcMsgDataForThisEmployee(Short employeeId,
      List<DailyDiscordMessages> allData) {
    return allData
        .stream()
        .filter(msg -> msg.getEmployeeId().equals(employeeId))
        .toList();
  }

  private LocalDate getLatestDateFromData(List<DailyDiscordMessages> employeeData) {
    return employeeData
        .stream()
        .map(DailyDiscordMessages::getDate)
        .max(Comparator.naturalOrder())
        .orElse(LocalDate.MIN);
  }

  private LocalDate findOldestDateFromList(List<LocalDate> dates) {
    return dates
        .stream()
        .min(LocalDate::compareTo)
        .orElse(LocalDate.parse("1970-01-01"));
  }

  public List<EmployeeCodes> getAddEmployeeCodesData() {
    return employeeCodesRepository
        .findAll();
  }

  public LocalDate getLatestDateFromDiscordMessagesData() {
    return dailyDiscordMessagesRepository
        .findMaxDate()
        .orElse(LocalDate.parse("1970-01-01"));
  }

  public List<DiscordRawMessagesCounts> getAllRawDcMessagesData() {
    return discordRawMessagesCountsRepository
        .findAll();
  }

  public List<DailyDiscordMessages> getAllDailyDiscordMessages() {
    return dailyDiscordMessagesRepository
        .findAll();
  }

  public List<Short> getAllEmployeesFromDailyDcMessages(List<DailyDiscordMessages> dailyMessages) {
    return dailyMessages
        .stream()
        .map(DailyDiscordMessages::getEmployeeId)
        .distinct()
        .sorted()
        .toList();
  }
}
