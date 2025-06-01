package com.scoutress.KaimuxAdminStats.servicesImpl.discordMessages;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    List<DailyDiscordMessages> dailyMessages = getAllDailyDcMessages();
    List<EmployeeCodes> employeeCodes = getAllEmployeeCodes();
    List<Short> employeeIds = getAllEmployeeIds(employeeCodes);
    LocalDate todaysDate = LocalDate.now();

    removeOldRawDcMessagesData();

    List<Short> employeeIdsWithoutData = lookForEmployeesWhereDataIsEmpty(employeeIds, dailyMessages);
    LocalDate latestDate = checkLatestDateFromData(employeeIdsWithoutData);

    discordBotServiceImpl.checkOrStartDiscordBot();
    discordBotServiceImpl.sleepForHalfMin();
    discordBotServiceImpl.handleDcBotRequests(
        employeeCodes, latestDate, todaysDate, employeeIdsWithoutData);
    discordBotServiceImpl.sleepForHalfMin();
    discordBotServiceImpl.stopBot();

    List<DiscordRawMessagesCounts> rawMessages = getAllRawDcMessagesData();

    discordMessagesService.convertDailyDiscordMessages(rawMessages, employeeCodes);

    duplicatesRemoverService.removeDailyDiscordMessagesDuplicates();

    discordMessagesService.calculateAverageValueOfDailyDiscordMessages(dailyMessages, employeeIds);

    discordMessagesComparedService.compareEachEmployeeDailyDiscordMessagesValues(
        dailyMessages, employeeIds, employeeIdsWithoutData);
  }

  private void removeOldRawDcMessagesData() {
    discordRawMessagesCountsRepository.truncateTable();
  }

  private List<DailyDiscordMessages> getAllDailyDcMessages() {
    return dailyDiscordMessagesRepository.findAll();
  }

  private List<Short> getAllEmployeeIds(List<EmployeeCodes> employeeCodes) {
    return employeeCodes
        .stream()
        .map(EmployeeCodes::getEmployeeId)
        .distinct()
        .collect(Collectors.toList());
  }

  private List<EmployeeCodes> getAllEmployeeCodes() {
    return employeeCodesRepository.findAll();
  }

  private List<Short> lookForEmployeesWhereDataIsEmpty(
      List<Short> employeeIds, List<DailyDiscordMessages> dailyMessages) {

    List<Short> employeeIdsWithoutDailyDcMsgsData = new ArrayList<>();

    for (Short employeeId : employeeIds) {
      boolean hasDailyDcMsgsData = hasDailyDcMsgsData(employeeId, dailyMessages);

      if (!hasDailyDcMsgsData) {
        employeeIdsWithoutDailyDcMsgsData.add(employeeId);
        System.out.println("Employee " + employeeId + " does not have daily dc msgs data");
      }
    }

    return employeeIdsWithoutDailyDcMsgsData;
  }

  private boolean hasDailyDcMsgsData(
      Short employeeId, List<DailyDiscordMessages> allDailyDiscordMessagesData) {
    return allDailyDiscordMessagesData
        .stream()
        .filter(employee -> employee.getEmployeeId().equals(employeeId))
        .anyMatch(employee -> employee.getDate() != null && employee.getMsgCount() >= 0);
  }

  private LocalDate checkLatestDateFromData(List<Short> employeeIdsWithoutData) {
    List<DailyDiscordMessages> dailyMessages = getAllDailyDcMessages();
    List<EmployeeCodes> employeeCodes = getAllEmployeeCodes();
    List<Short> employeeIds = getAllEmployeeIds(employeeCodes);
    List<LocalDate> latestDcMsgDates = new ArrayList<>();

    for (Short employeeId : employeeIds) {

      if (!employeeIdsWithoutData.contains(employeeId)) {
        List<DailyDiscordMessages> allDcMsgDataForThisEmployee = getAllDcMsgDataForThisEmployee(
            employeeId, dailyMessages);

        LocalDate latestDate = getLatestDateFromData(allDcMsgDataForThisEmployee);

        latestDcMsgDates.add(latestDate);
      }
    }

    return findOldestDateFromList(latestDcMsgDates);
  }

  private List<DailyDiscordMessages> getAllDcMsgDataForThisEmployee(
      Short employeeId, List<DailyDiscordMessages> allDailyDiscordMessagesData) {
    return allDailyDiscordMessagesData
        .stream()
        .filter(employee -> employee.getEmployeeId().equals(employeeId))
        .toList();
  }

  private LocalDate getLatestDateFromData(
      List<DailyDiscordMessages> allDcMsgDataForThisEmployee) {
    return allDcMsgDataForThisEmployee
        .stream()
        .map(DailyDiscordMessages::getDate)
        .max(Comparator.naturalOrder())
        .orElseThrow(() -> new RuntimeException("There is no daily tickets data"));
  }

  private LocalDate findOldestDateFromList(List<LocalDate> latestDcMsgDates) {
    return latestDcMsgDates
        .stream()
        .min(LocalDate::compareTo)
        .orElseThrow();
  }

  public List<EmployeeCodes> getAddEmployeeCodesData() {
    return employeeCodesRepository.findAll();
  }

  public LocalDate getLatestDateFromDiscordMessagesData() {
    return dailyDiscordMessagesRepository.findMaxDate().orElse(LocalDate.parse("1970-01-01"));
  }

  public List<DiscordRawMessagesCounts> getAllRawDcMessagesData() {
    return discordRawMessagesCountsRepository.findAll();
  }

  public List<DailyDiscordMessages> getAllDailyDiscordMessages() {
    return dailyDiscordMessagesRepository.findAll();
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
