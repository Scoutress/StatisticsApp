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
    log.info("=== [START] Discord messages handling process ===");
    long start = System.currentTimeMillis();

    try {
      List<DailyDiscordMessages> dailyMessages = getAllDailyDcMessages();
      log.debug("Loaded {} existing DailyDiscordMessages records.", dailyMessages.size());

      List<EmployeeCodes> employeeCodes = getAllEmployeeCodes();
      log.debug("Loaded {} EmployeeCodes records.", employeeCodes.size());

      List<Short> employeeIds = getAllEmployeeIds(employeeCodes);
      LocalDate todaysDate = LocalDate.now();
      log.info("Total employees: {}, Today's date: {}", employeeIds.size(), todaysDate);

      removeOldRawDcMessagesData();

      List<Short> employeeIdsWithoutData = lookForEmployeesWhereDataIsEmpty(employeeIds, dailyMessages);
      log.debug("{} employees found without Discord data: {}", employeeIdsWithoutData.size(), employeeIdsWithoutData);

      LocalDate latestDate = checkLatestDateFromData(employeeIdsWithoutData);
      log.info("Latest Discord message date found across dataset: {}", latestDate);

      // ======================
      // DISCORD BOT OPERATIONS
      // ======================
      log.info("🤖 Starting Discord bot message collection...");
      discordBotServiceImpl.checkOrStartDiscordBot();
      discordBotServiceImpl.sleepForHalfMin();

      log.info("🕐 Discord bot now collecting messages between {} and {}", latestDate, todaysDate);
      discordBotServiceImpl.handleDcBotRequests(employeeCodes, latestDate, todaysDate, employeeIdsWithoutData);
      discordBotServiceImpl.sleepForHalfMin();
      discordBotServiceImpl.stopBot();
      log.info("✅ Discord bot data collection completed successfully.");

      // ======================
      // DATA HANDLING SECTION
      // ======================
      List<DiscordRawMessagesCounts> rawMessages = getAllRawDcMessagesData();
      log.debug("Fetched {} raw Discord messages from temporary table.", rawMessages.size());
      rawMessages.forEach(msg -> log.trace("RAW_MSG -> userId={}, date={}, count={}", msg.getDcUserId(),
          msg.getMessageDate(), msg.getMessageCount()));

      discordMessagesService.convertDailyDiscordMessages(rawMessages, employeeCodes);
      log.info("Converted raw Discord message data into daily records.");

      duplicatesRemoverService.removeDailyDiscordMessagesDuplicates();
      log.info("Removed any duplicate entries from DailyDiscordMessages table.");

      discordMessagesService.calculateAverageValueOfDailyDiscordMessages(dailyMessages, employeeIds);
      log.info("Calculated average Discord message counts per employee per day.");

      discordMessagesComparedService.compareEachEmployeeDailyDiscordMessagesValues(
          dailyMessages, employeeIds, employeeIdsWithoutData);
      log.info("Completed Discord message ratio comparison per employee.");

    } catch (Exception e) {
      log.error("❌ Error during Discord messages handling: {}", e.getMessage(), e);
    }

    log.info("=== [END] Discord messages handling process completed in {} ms ===",
        System.currentTimeMillis() - start);
  }

  private void removeOldRawDcMessagesData() {
    log.debug("🧹 Removing old raw Discord message data...");
    try {
      discordRawMessagesCountsRepository.truncateTable();
      log.info("Old raw Discord message data truncated successfully.");
    } catch (Exception e) {
      log.error("Failed to truncate raw Discord messages table: {}", e.getMessage(), e);
    }
  }

  private List<DailyDiscordMessages> getAllDailyDcMessages() {
    List<DailyDiscordMessages> data = dailyDiscordMessagesRepository.findAll();
    data.forEach(msg -> log.trace("DAILY_MSG -> employeeId={}, date={}, msgCount={}", msg.getEmployeeId(),
        msg.getDate(), msg.getMsgCount()));
    return data;
  }

  private List<Short> getAllEmployeeIds(List<EmployeeCodes> employeeCodes) {
    List<Short> ids = employeeCodes.stream()
        .map(EmployeeCodes::getEmployeeId)
        .distinct()
        .collect(Collectors.toList());
    log.trace("Collected {} distinct employee IDs: {}", ids.size(), ids);
    return ids;
  }

  private List<EmployeeCodes> getAllEmployeeCodes() {
    List<EmployeeCodes> codes = employeeCodesRepository.findAll();
    codes.forEach(
        code -> log.trace("EMP_CODE -> employeeId={}, code={}", code.getEmployeeId(), code.getDiscordUserId()));
    return codes;
  }

  private List<Short> lookForEmployeesWhereDataIsEmpty(List<Short> employeeIds,
      List<DailyDiscordMessages> dailyMessages) {
    log.debug("🔍 Searching for employees without any daily Discord messages...");
    List<Short> employeeIdsWithoutDailyDcMsgsData = new ArrayList<>();

    for (Short employeeId : employeeIds) {
      boolean hasData = hasDailyDcMsgsData(employeeId, dailyMessages);

      if (!hasData) {
        employeeIdsWithoutDailyDcMsgsData.add(employeeId);
        log.warn("⚠ Employee {} has no daily Discord message data.", employeeId);
      } else {
        log.trace("Employee {} has valid daily Discord message data.", employeeId);
      }
    }

    return employeeIdsWithoutDailyDcMsgsData;
  }

  private boolean hasDailyDcMsgsData(Short employeeId, List<DailyDiscordMessages> allDailyDiscordMessagesData) {
    boolean result = allDailyDiscordMessagesData.stream()
        .filter(msg -> msg.getEmployeeId().equals(employeeId))
        .anyMatch(msg -> msg.getDate() != null && msg.getMsgCount() >= 0);
    log.trace("Employee {} daily data check -> {}", employeeId, result ? "HAS DATA" : "NO DATA");
    return result;
  }

  private LocalDate checkLatestDateFromData(List<Short> employeeIdsWithoutData) {
    log.debug("🧭 Checking latest date from Discord messages (excluding employees with no data)...");
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
          log.trace("Employee {} latest date: {}", employeeId, latestDate);
        }
      }
    }

    LocalDate oldest = findOldestDateFromList(latestDcMsgDates);
    log.info("Earliest common Discord message date across employees: {}", oldest);
    return oldest;
  }

  private List<DailyDiscordMessages> getAllDcMsgDataForThisEmployee(Short employeeId,
      List<DailyDiscordMessages> allData) {
    List<DailyDiscordMessages> filtered = allData.stream()
        .filter(msg -> msg.getEmployeeId().equals(employeeId))
        .toList();
    log.trace("Employee {} -> {} records found.", employeeId, filtered.size());
    return filtered;
  }

  private LocalDate getLatestDateFromData(List<DailyDiscordMessages> employeeData) {
    LocalDate date = employeeData.stream()
        .map(DailyDiscordMessages::getDate)
        .max(Comparator.naturalOrder())
        .orElse(LocalDate.MIN);
    log.trace("Latest date from {} records: {}", employeeData.size(), date);
    return date;
  }

  private LocalDate findOldestDateFromList(List<LocalDate> dates) {
    LocalDate date = dates.stream()
        .min(LocalDate::compareTo)
        .orElse(LocalDate.parse("1970-01-01"));
    log.trace("Oldest date found across {} entries: {}", dates.size(), date);
    return date;
  }

  public List<EmployeeCodes> getAddEmployeeCodesData() {
    List<EmployeeCodes> data = employeeCodesRepository.findAll();
    log.debug("Fetched {} EmployeeCodes from DB.", data.size());
    return data;
  }

  public LocalDate getLatestDateFromDiscordMessagesData() {
    LocalDate date = dailyDiscordMessagesRepository.findMaxDate()
        .orElse(LocalDate.parse("1970-01-01"));
    log.info("Latest date from DailyDiscordMessages: {}", date);
    return date;
  }

  public List<DiscordRawMessagesCounts> getAllRawDcMessagesData() {
    List<DiscordRawMessagesCounts> data = discordRawMessagesCountsRepository.findAll();
    log.trace("Fetched {} DiscordRawMessagesCounts entries.", data.size());
    return data;
  }

  public List<DailyDiscordMessages> getAllDailyDiscordMessages() {
    List<DailyDiscordMessages> data = dailyDiscordMessagesRepository.findAll();
    log.trace("Fetched {} DailyDiscordMessages entries.", data.size());
    return data;
  }

  public List<Short> getAllEmployeesFromDailyDcMessages(List<DailyDiscordMessages> dailyMessages) {
    List<Short> ids = dailyMessages.stream()
        .map(DailyDiscordMessages::getEmployeeId)
        .distinct()
        .sorted()
        .toList();
    log.trace("Found {} employees from daily Discord messages: {}", ids.size(), ids);
    return ids;
  }
}
