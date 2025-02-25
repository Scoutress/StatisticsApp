package com.scoutress.KaimuxAdminStats.servicesImpl.discordMessages;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.discordMessages.DailyDiscordMessages;
import com.scoutress.KaimuxAdminStats.entity.discordMessages.DiscordRawMessagesCounts;
import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;
import com.scoutress.KaimuxAdminStats.repositories.discordMessages.DailyDiscordMessagesRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordMessages.DiscordRawMessagesCountsRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeCodesRepository;
import com.scoutress.KaimuxAdminStats.services.discordMessages.DiscordBotService;
import com.scoutress.KaimuxAdminStats.services.discordMessages.DiscordMessagesComparedService;
import com.scoutress.KaimuxAdminStats.services.discordMessages.DiscordMessagesHandlingService;
import com.scoutress.KaimuxAdminStats.services.discordMessages.DiscordMessagesService;

@Service
public class DiscordMessagesHandlingServiceImpl implements DiscordMessagesHandlingService {

  private final EmployeeCodesRepository employeeCodesRepository;
  private final DailyDiscordMessagesRepository dailyDiscordMessagesRepository;
  private final DiscordBotService discordBotService;
  private final DiscordRawMessagesCountsRepository discordRawMessagesCountsRepository;
  private final DiscordMessagesService discordMessagesService;
  private final DiscordMessagesComparedService discordMessagesComparedService;

  public DiscordMessagesHandlingServiceImpl(
      EmployeeCodesRepository employeeCodesRepository,
      DailyDiscordMessagesRepository dailyDiscordMessagesRepository,
      DiscordBotService discordBotService,
      DiscordRawMessagesCountsRepository discordRawMessagesCountsRepository,
      DiscordMessagesService discordMessagesService,
      DiscordMessagesComparedService discordMessagesComparedService) {
    this.employeeCodesRepository = employeeCodesRepository;
    this.dailyDiscordMessagesRepository = dailyDiscordMessagesRepository;
    this.discordBotService = discordBotService;
    this.discordRawMessagesCountsRepository = discordRawMessagesCountsRepository;
    this.discordMessagesService = discordMessagesService;
    this.discordMessagesComparedService = discordMessagesComparedService;
  }

  @Override
  public void handleDiscordMessages() {
    removeOldRawDcMessagesData();

    List<EmployeeCodes> employeeCodes = getAddEmployeeCodesData();
    LocalDate latestDate = getLatestDateFromDiscordMessagesData();
    LocalDate todaysDate = LocalDate.now();

    discordBotService.startBot();
    discordBotService.sleepForOneMin();
    discordBotService.handleDcBotRequests(employeeCodes, latestDate, todaysDate);
    discordBotService.sleepForOneMin();
    discordBotService.stopBot();

    List<DiscordRawMessagesCounts> rawMessages = getAllRawDcMessagesData();
    List<DailyDiscordMessages> dailyMessages = getAllDailyDiscordMessages();
    List<Short> employeeIds = getAllEmployeesFromDailyDcMessages(dailyMessages);

    discordMessagesService.convertDailyDiscordMessages(rawMessages, employeeCodes);
    discordMessagesService.removeDailyDiscordMessagesDuplicates(dailyMessages);
    discordMessagesService.calculateAverageValueOfDailyDiscordMessages(dailyMessages, employeeIds);

    discordMessagesComparedService.compareEachEmployeeDailyDiscordMessagesValues(dailyMessages, employeeIds);
  }

  public void removeOldRawDcMessagesData() {
    discordRawMessagesCountsRepository.truncateTable();
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
