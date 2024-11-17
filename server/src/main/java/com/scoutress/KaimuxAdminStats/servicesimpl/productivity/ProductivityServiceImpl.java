package com.scoutress.KaimuxAdminStats.servicesImpl.productivity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.constants.CalculationConstants;
import com.scoutress.KaimuxAdminStats.entity.afkPlaytime.DailyAfkPlaytime;
import com.scoutress.KaimuxAdminStats.entity.discordMessages.DailyDiscordMessages;
import com.scoutress.KaimuxAdminStats.entity.discordMessages.DailyDiscordMessagesCompared;
import com.scoutress.KaimuxAdminStats.entity.discordTickets.DailyDiscordTickets;
import com.scoutress.KaimuxAdminStats.entity.discordTickets.DailyDiscordTicketsCompared;
import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeLevel;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.DailyMinecraftTickets;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.DailyMinecraftTicketsCompared;
import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.entity.productivity.DailyObjectiveProductivity;
import com.scoutress.KaimuxAdminStats.entity.productivity.DailyProductivity;
import com.scoutress.KaimuxAdminStats.entity.productivity.DailySubjectiveProductivity;
import com.scoutress.KaimuxAdminStats.repositories.afkPlaytime.DailyAfkPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordMessages.DailyDiscordMessagesComparedRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordMessages.DailyDiscordMessagesRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordTickets.DailyDiscordTicketsComparedRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordTickets.DailyDiscordTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeLevelRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.DailyMinecraftTicketsComparedRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.DailyMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.DailyPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.repositories.productivity.DailyObjectiveProductivityRepository;
import com.scoutress.KaimuxAdminStats.repositories.productivity.DailyProductivityRepository;
import com.scoutress.KaimuxAdminStats.repositories.productivity.DailySubjectiveProductivityRepository;
import com.scoutress.KaimuxAdminStats.services.productivity.ProductivityService;

@Service
public class ProductivityServiceImpl implements ProductivityService {

  private final DailyObjectiveProductivityRepository dailyObjectiveProductivityRepository;
  private final DailySubjectiveProductivityRepository dailySubjectiveProductivityRepository;
  private final DailyProductivityRepository dailyProductivityRepository;
  private final DailyDiscordTicketsRepository dailyDiscordTicketsRepository;
  private final DailyMinecraftTicketsRepository dailyMinecraftTicketsRepository;
  private final DailyPlaytimeRepository dailyPlaytimeRepository;
  private final DailyAfkPlaytimeRepository dailyAfkPlaytimeRepository;
  private final DailyDiscordMessagesRepository dailyDiscordMessagesRepository;
  private final DailyDiscordTicketsComparedRepository dailyDiscordTicketsComparedRepository;
  private final DailyDiscordMessagesComparedRepository dailyDiscordMessagesComparedRepository;
  private final DailyMinecraftTicketsComparedRepository dailyMinecraftTicketsComparedRepository;
  private final EmployeeRepository employeeRepository;
  private final EmployeeLevelRepository employeeLevelRepository;

  public ProductivityServiceImpl(
      DailyObjectiveProductivityRepository objectiveProductivityRepository,
      DailySubjectiveProductivityRepository subjectiveProductivityRepository,
      DailyProductivityRepository productivityRepository,
      DailyDiscordTicketsRepository dailyDiscordTicketsRepository,
      DailyMinecraftTicketsRepository dailyMinecraftTicketsRepository,
      DailyPlaytimeRepository dailyPlaytimeRepository,
      DailyAfkPlaytimeRepository dailyAfkPlaytimeRepository,
      DailyDiscordMessagesRepository dailyDiscordMessagesRepository,
      DailyDiscordTicketsComparedRepository dailyDiscordTicketsComparedRepository,
      DailyDiscordMessagesComparedRepository dailyDiscordMessagesComparedRepository,
      DailyMinecraftTicketsComparedRepository dailyMinecraftTicketsComparedRepository,
      EmployeeRepository employeeRepository,
      EmployeeLevelRepository employeeLevelRepository) {
    this.dailyObjectiveProductivityRepository = objectiveProductivityRepository;
    this.dailySubjectiveProductivityRepository = subjectiveProductivityRepository;
    this.dailyProductivityRepository = productivityRepository;
    this.dailyDiscordTicketsRepository = dailyDiscordTicketsRepository;
    this.dailyMinecraftTicketsRepository = dailyMinecraftTicketsRepository;
    this.dailyPlaytimeRepository = dailyPlaytimeRepository;
    this.dailyAfkPlaytimeRepository = dailyAfkPlaytimeRepository;
    this.dailyDiscordMessagesRepository = dailyDiscordMessagesRepository;
    this.dailyDiscordTicketsComparedRepository = dailyDiscordTicketsComparedRepository;
    this.dailyDiscordMessagesComparedRepository = dailyDiscordMessagesComparedRepository;
    this.dailyMinecraftTicketsComparedRepository = dailyMinecraftTicketsComparedRepository;
    this.employeeRepository = employeeRepository;
    this.employeeLevelRepository = employeeLevelRepository;
  }

  @Override
  public void calculateDailyProductivity() {
    List<DailyObjectiveProductivity> objProd = dailyObjectiveProductivityRepository.findAll();
    List<DailySubjectiveProductivity> subjProd = dailySubjectiveProductivityRepository.findAll();

    Map<Short, List<Double>> objectiveValues = objProd.stream()
        .collect(Collectors.groupingBy(
            DailyObjectiveProductivity::getAid,
            Collectors.mapping(DailyObjectiveProductivity::getValue, Collectors.toList())));

    Map<Short, List<Double>> subjectiveValues = subjProd.stream()
        .collect(Collectors.groupingBy(
            DailySubjectiveProductivity::getAid,
            Collectors.mapping(DailySubjectiveProductivity::getValue, Collectors.toList())));

    Set<Short> allAids = new HashSet<>();
    allAids.addAll(objectiveValues.keySet());
    allAids.addAll(subjectiveValues.keySet());

    List<DailyProductivity> productivityResults = new ArrayList<>();

    for (Short aid : allAids) {
      List<Double> objValues = objectiveValues.getOrDefault(aid, Collections.emptyList());
      List<Double> subjValues = subjectiveValues.getOrDefault(aid, Collections.emptyList());

      double objAvg = objValues.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
      double subjAvg = subjValues.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

      double finalValue = (objAvg + subjAvg) / 2;

      DailyProductivity productivity = new DailyProductivity();
      productivity.setAid(aid);
      productivity.setValue(finalValue);
      productivityResults.add(productivity);
    }

    dailyProductivityRepository.saveAll(productivityResults);
  }

  @Override
  public void calculateDailyObjectiveProductivity() {
    List<Employee> employees = employeeRepository.findAll();
    List<DailyPlaytime> dailyPlaytime = dailyPlaytimeRepository.findAll();
    List<DailyAfkPlaytime> dailyAfkPlaytime = dailyAfkPlaytimeRepository.findAll();
    List<DailyDiscordTickets> dailyDiscordTickets = dailyDiscordTicketsRepository.findAll();
    List<DailyDiscordTicketsCompared> dailyDiscordTicketsComp = dailyDiscordTicketsComparedRepository.findAll();
    List<DailyDiscordMessages> dailyDiscordMessages = dailyDiscordMessagesRepository.findAll();
    List<DailyDiscordMessagesCompared> dailyDiscordMessagesComp = dailyDiscordMessagesComparedRepository.findAll();
    List<DailyMinecraftTickets> dailyMinecraftTickets = dailyMinecraftTicketsRepository.findAll();
    List<DailyMinecraftTicketsCompared> dailyMinecraftTicketsComp = dailyMinecraftTicketsComparedRepository.findAll();

    LocalDate oldestDate = findOldestDate(
        dailyPlaytime, dailyAfkPlaytime, dailyDiscordTickets, dailyDiscordTicketsComp,
        dailyDiscordMessages, dailyDiscordMessagesComp, dailyMinecraftTickets, dailyMinecraftTicketsComp);

    LocalDate today = LocalDate.now();

    List<DailyObjectiveProductivity> results = new ArrayList<>();

    for (Employee employee : employees) {
      List<EmployeeLevel> levelHistory = employeeLevelRepository.findByAid(employee.getId());

      for (LocalDate date = oldestDate; date.isBefore(today); date = date.plusDays(1)) {
        String level = getEmployeeLevelForDate(levelHistory, date);
        Map<String, Double> constants = getConstantsForLevel(level);

        double productivity = calculateObjectiveProductivityForThatDay(
            employee, date, constants,
            dailyPlaytime, dailyAfkPlaytime, dailyDiscordTickets, dailyDiscordTicketsComp,
            dailyDiscordMessages, dailyDiscordMessagesComp, dailyMinecraftTickets, dailyMinecraftTicketsComp);

        results.add(new DailyObjectiveProductivity(null, employee.getId(), productivity, date));
      }
    }
    dailyObjectiveProductivityRepository.saveAll(results);
  }

  private LocalDate findOldestDate(List<?>... dataLists) {
    return Arrays.stream(dataLists)
        .flatMap(list -> list.stream().map(this::getDate))
        .min(LocalDate::compareTo)
        .orElseThrow(() -> new RuntimeException("No dates found"));
  }

  private String getEmployeeLevelForDate(List<EmployeeLevel> levelHistory, LocalDate date) {
    EmployeeLevel levelForDate = levelHistory
        .stream()
        .filter(level -> level.getDate().equals(date))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No level found for date: " + date));

    return switch (levelForDate.getLevel()) {
      case 0 -> "organizer";
      case 1 -> "helper";
      case 2 -> "support";
      case 3 -> "chatmod";
      case 4 -> "overseer";
      case 6 -> "manager";
      default -> throw new IllegalStateException("Unknown level: " + levelForDate.getLevel());
    };
  }

  private Map<String, Double> getConstantsForLevel(String level) {
    return switch (level) {
      case "helper" -> Map.ofEntries(
          Map.entry("playtime", CalculationConstants.PLAYTIME_HELPER),
          Map.entry("afkPlaytime", CalculationConstants.AFK_PLAYTIME_HELPER),
          Map.entry("discordMessages", CalculationConstants.DISCORD_MESSAGES_HELPER),
          Map.entry("discordMessagesComp", CalculationConstants.DISCORD_MESSAGES_COMPARED_HELPER),
          Map.entry("discordTickets", null),
          Map.entry("discordTicketsComp", null),
          Map.entry("minecraftTickets", null),
          Map.entry("minecraftTicketsComp", null));
      case "support" -> Map.ofEntries(
          Map.entry("playtime", CalculationConstants.PLAYTIME_SUPPORT),
          Map.entry("afkPlaytime", CalculationConstants.AFK_PLAYTIME_SUPPORT),
          Map.entry("discordMessages", CalculationConstants.DISCORD_MESSAGES_SUPPORT),
          Map.entry("discordMessagesComp", CalculationConstants.DISCORD_MESSAGES_COMPARED_SUPPORT),
          Map.entry("discordTickets", CalculationConstants.DISCORD_TICKETS_SUPPORT),
          Map.entry("discordTicketsComp", CalculationConstants.DISCORD_TICKETS_COMPARED_SUPPORT),
          Map.entry("minecraftTickets", CalculationConstants.MINECRAFT_TICKETS_SUPPORT),
          Map.entry("minecraftTicketsComp", CalculationConstants.MINECRAFT_TICKETS_COMPARED_SUPPORT));
      case "chatmod" -> Map.ofEntries(
          Map.entry("playtime", CalculationConstants.PLAYTIME_CHATMOD),
          Map.entry("afkPlaytime", CalculationConstants.AFK_PLAYTIME_CHATMOD),
          Map.entry("discordMessages", CalculationConstants.DISCORD_MESSAGES_CHATMOD),
          Map.entry("discordMessagesComp", CalculationConstants.DISCORD_MESSAGES_COMPARED_CHATMOD),
          Map.entry("discordTickets", CalculationConstants.DISCORD_TICKETS_CHATMOD),
          Map.entry("discordTicketsComp", CalculationConstants.DISCORD_TICKETS_COMPARED_CHATMOD),
          Map.entry("minecraftTickets", CalculationConstants.MINECRAFT_TICKETS_CHATMOD),
          Map.entry("minecraftTicketsComp", CalculationConstants.MINECRAFT_TICKETS_COMPARED_CHATMOD));
      case "overseer", "organizer" -> Map.ofEntries(
          Map.entry("playtime", CalculationConstants.PLAYTIME_OVERSEER),
          Map.entry("afkPlaytime", CalculationConstants.AFK_PLAYTIME_OVERSEER),
          Map.entry("discordMessages", CalculationConstants.DISCORD_MESSAGES_OVERSEER),
          Map.entry("discordMessagesComp", CalculationConstants.DISCORD_MESSAGES_COMPARED_OVERSEER),
          Map.entry("discordTickets", CalculationConstants.DISCORD_TICKETS_OVERSEER),
          Map.entry("discordTicketsComp", CalculationConstants.DISCORD_TICKETS_COMPARED_OVERSEER),
          Map.entry("minecraftTickets", CalculationConstants.MINECRAFT_TICKETS_OVERSEER),
          Map.entry("minecraftTicketsComp", CalculationConstants.MINECRAFT_TICKETS_COMPARED_OVERSEER));
      case "manager" -> Map.ofEntries(
          Map.entry("playtime", CalculationConstants.PLAYTIME_MANAGER),
          Map.entry("afkPlaytime", CalculationConstants.AFK_PLAYTIME_MANAGER),
          Map.entry("discordMessages", CalculationConstants.DISCORD_MESSAGES_MANAGER),
          Map.entry("discordMessagesComp", CalculationConstants.DISCORD_MESSAGES_COMPARED_MANAGER),
          Map.entry("discordTickets", CalculationConstants.DISCORD_TICKETS_MANAGER),
          Map.entry("discordTicketsComp", CalculationConstants.DISCORD_TICKETS_COMPARED_MANAGER),
          Map.entry("minecraftTickets", CalculationConstants.MINECRAFT_TICKETS_MANAGER),
          Map.entry("minecraftTicketsComp", CalculationConstants.MINECRAFT_TICKETS_COMPARED_MANAGER));
      default -> throw new IllegalArgumentException("Unknown level: " + level);
    };
  }

  private double calculateObjectiveProductivityForThatDay(
      Employee employee,
      LocalDate date,
      Map<String, Double> constants,
      List<DailyPlaytime> dailyPlaytime,
      List<DailyAfkPlaytime> dailyAfkPlaytime,
      List<DailyDiscordTickets> dailyDiscordTickets,
      List<DailyDiscordTicketsCompared> dailyDiscordTicketsComp,
      List<DailyDiscordMessages> dailyDiscordMessages,
      List<DailyDiscordMessagesCompared> dailyDiscordMessagesComp,
      List<DailyMinecraftTickets> dailyMinecraftTickets,
      List<DailyMinecraftTicketsCompared> dailyMinecraftTicketsComp) {

    double totalProductivity = 0.0;
    int categoriesCount = 8;

    totalProductivity += calculateProductivityFromList(
        dailyPlaytime, employee, date, constants.get("playtime"));
    totalProductivity += calculateProductivityFromList(
        dailyAfkPlaytime, employee, date, constants.get("afkPlaytime"));
    totalProductivity += calculateProductivityFromList(
        dailyDiscordTickets, employee, date, constants.get("discordTickets"));
    totalProductivity += calculateProductivityFromList(
        dailyDiscordTicketsComp, employee, date, constants.get("discordTicketsComp"));
    totalProductivity += calculateProductivityFromList(
        dailyDiscordMessages, employee, date, constants.get("discordMessages"));
    totalProductivity += calculateProductivityFromList(
        dailyDiscordMessagesComp, employee, date, constants.get("discordMessagesComp"));
    totalProductivity += calculateProductivityFromList(
        dailyMinecraftTickets, employee, date, constants.get("minecraftTickets"));
    totalProductivity += calculateProductivityFromList(
        dailyMinecraftTicketsComp, employee, date, constants.get("minecraftTicketsComp"));

    return totalProductivity / categoriesCount;
  }

  private double calculateProductivityFromList(List<?> dataList, Employee employee, LocalDate date, Double constant) {
    return dataList.stream()
        .filter(data -> matchesEmployeeAndDate(data, employee, date))
        .mapToDouble(this::extractValue)
        .sum() * constant;
  }

  private boolean matchesEmployeeAndDate(Object entity, Employee employee, LocalDate date) {
    if (entity instanceof DailyPlaytime
        || entity instanceof DailyAfkPlaytime
        || entity instanceof DailyDiscordMessages
        || entity instanceof DailyDiscordTickets
        || entity instanceof DailyDiscordTicketsCompared
        || entity instanceof DailyDiscordMessagesCompared
        || entity instanceof DailyMinecraftTickets
        || entity instanceof DailyMinecraftTicketsCompared) {
      return getAid(entity).equals(employee.getId()) && getDate(entity).equals(date);
    }
    throw new IllegalArgumentException("Unsupported entity type: " + entity);
  }

  private double extractValue(Object entity) {
    if (entity instanceof DailyPlaytime dailyPlaytime)
      return dailyPlaytime.getTime();
    if (entity instanceof DailyAfkPlaytime dailyAfkPlaytime)
      return dailyAfkPlaytime.getTime();
    if (entity instanceof DailyDiscordMessages dailyDiscordMessages)
      return dailyDiscordMessages.getMsgCount();
    if (entity instanceof DailyDiscordTickets dailyDiscordTickets)
      return dailyDiscordTickets.getTicketCount();
    if (entity instanceof DailyDiscordTicketsCompared dailyDiscordTicketsCompared)
      return dailyDiscordTicketsCompared.getValue();
    if (entity instanceof DailyDiscordMessagesCompared dailyDiscordMessagesCompared)
      return dailyDiscordMessagesCompared.getValue();
    if (entity instanceof DailyMinecraftTickets dailyMinecraftTickets)
      return dailyMinecraftTickets.getTicketCount();
    if (entity instanceof DailyMinecraftTicketsCompared dailyMinecraftTicketsCompared)
      return dailyMinecraftTicketsCompared.getValue();

    throw new IllegalArgumentException("Unsupported entity type: " + entity);
  }

  private Object getAid(Object entity) {
    if (entity instanceof DailyPlaytime dailyPlaytime)
      return dailyPlaytime.getAid();
    if (entity instanceof DailyAfkPlaytime dailyAfkPlaytime)
      return dailyAfkPlaytime.getAid();
    if (entity instanceof DailyDiscordMessages dailyDiscordMessages)
      return dailyDiscordMessages.getAid();
    if (entity instanceof DailyDiscordTickets dailyDiscordTickets)
      return dailyDiscordTickets.getAid();
    if (entity instanceof DailyDiscordTicketsCompared dailyDiscordTicketsCompared)
      return dailyDiscordTicketsCompared.getAid();
    if (entity instanceof DailyDiscordMessagesCompared dailyDiscordMessagesCompared)
      return dailyDiscordMessagesCompared.getAid();
    if (entity instanceof DailyMinecraftTickets dailyMinecraftTickets)
      return dailyMinecraftTickets.getAid();
    if (entity instanceof DailyMinecraftTicketsCompared dailyMinecraftTicketsCompared)
      return dailyMinecraftTicketsCompared.getAid();

    throw new IllegalArgumentException("Unsupported entity type: " + entity);
  }

  private LocalDate getDate(Object entity) {
    if (entity instanceof DailyPlaytime dailyPlaytime)
      return dailyPlaytime.getDate();
    if (entity instanceof DailyAfkPlaytime dailyAfkPlaytime)
      return dailyAfkPlaytime.getDate();
    if (entity instanceof DailyDiscordMessages dailyDiscordMessages)
      return dailyDiscordMessages.getDate();
    if (entity instanceof DailyDiscordTickets dailyDiscordTickets)
      return dailyDiscordTickets.getDate();
    if (entity instanceof DailyDiscordTicketsCompared dailyDiscordTicketsCompared)
      return dailyDiscordTicketsCompared.getDate();
    if (entity instanceof DailyDiscordMessagesCompared dailyDiscordMessagesCompared)
      return dailyDiscordMessagesCompared.getDate();
    if (entity instanceof DailyMinecraftTickets dailyMinecraftTickets)
      return dailyMinecraftTickets.getDate();
    if (entity instanceof DailyMinecraftTicketsCompared dailyMinecraftTicketsCompared)
      return dailyMinecraftTicketsCompared.getDate();

    throw new IllegalArgumentException("Unsupported entity type: " + entity);
  }
}
