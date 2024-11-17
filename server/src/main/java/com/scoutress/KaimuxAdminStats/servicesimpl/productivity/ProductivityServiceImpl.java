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
    LocalDate yesterday = LocalDate.now().minusDays(1);

    List<DailyObjectiveProductivity> results = new ArrayList<>();
    for (Employee employee : employees) {
      EmployeeLevel levelInfo = getEmployeeLevelInfo(employee.getId());
      for (LocalDate date = oldestDate; date.isBefore(yesterday); date = date.plusDays(1)) {
        double productivity = calculateObjectiveProductivityForDay(
            employee, levelInfo, date,
            dailyPlaytime, dailyAfkPlaytime, dailyDiscordTickets, dailyDiscordTicketsComp,
            dailyDiscordMessages, dailyDiscordMessagesComp, dailyMinecraftTickets, dailyMinecraftTicketsComp);
        results.add(new DailyObjectiveProductivity(null, employee.getId(), productivity, date));
      }
    }
    dailyObjectiveProductivityRepository.saveAll(results);
  }

  private LocalDate findOldestDate(List<?>... dataLists) {
    return Arrays.stream(dataLists)
        .flatMap(list -> list.stream().map(this::extractDate))
        .min(LocalDate::compareTo)
        .orElseThrow(() -> new RuntimeException("No dates found"));
  }

  private LocalDate extractDate(Object entity) {
    if (entity instanceof DailyPlaytime playtime)
      return playtime.getDate();
    if (entity instanceof DailyAfkPlaytime afk)
      return afk.getDate();
    if (entity instanceof DailyDiscordTickets ticket)
      return ticket.getDate();
    if (entity instanceof DailyDiscordTicketsCompared ticketComp)
      return ticketComp.getDate();
    if (entity instanceof DailyDiscordMessages msg)
      return msg.getDate();
    if (entity instanceof DailyDiscordMessagesCompared msgComp)
      return msgComp.getDate();
    if (entity instanceof DailyMinecraftTickets mcTicket)
      return mcTicket.getDate();
    if (entity instanceof DailyMinecraftTicketsCompared mcTicketComp)
      return mcTicketComp.getDate();
    throw new IllegalArgumentException("Unsupported entity type");
  }

  private EmployeeLevel getEmployeeLevelInfo(Short employeeId) {
    return employeeLevelRepository.findByAid(employeeId);
  }

  private String getEmployeeLevelForDate(EmployeeLevel levelInfo, LocalDate date) {
    if (levelInfo.getBecameHelper() != null && !date.isBefore(levelInfo.getBecameHelper())) {
      if (levelInfo.getPromotedToSupport() != null && !date.isBefore(levelInfo.getPromotedToSupport())) {
        if (levelInfo.getPromotedToChatMod() != null && !date.isBefore(levelInfo.getPromotedToChatMod())) {
          if (levelInfo.getPromotedToOverseer() != null && !date.isBefore(levelInfo.getPromotedToOverseer())) {
            if (levelInfo.getPromotedToManager() != null && !date.isBefore(levelInfo.getPromotedToManager())) {
              return "manager";
            }
            return "overseer";
          }
          return "chatmod";
        }
        return "support";
      }
      return "helper";
    }

    if (levelInfo.getDemotedToOverseer() != null && !date.isBefore(levelInfo.getDemotedToOverseer())) {
      if (levelInfo.getDemotedToChatMod() != null && !date.isBefore(levelInfo.getDemotedToChatMod())) {
        if (levelInfo.getDemotedToSupport() != null && !date.isBefore(levelInfo.getDemotedToSupport())) {
          if (levelInfo.getDemotedToHelper() != null && !date.isBefore(levelInfo.getDemotedToHelper())) {
            return "helper";
          }
          return "support";
        }
        return "chatmod";
      }
      return "overseer";
    }
    throw new IllegalStateException("Unable to determine level for date: " + date);
  }

  private double calculateObjectiveProductivityForDay(
      Employee employee, EmployeeLevel levelInfo, LocalDate date,
      List<DailyPlaytime> dailyPlaytime, List<DailyAfkPlaytime> dailyAfkPlaytime,
      List<DailyDiscordTickets> dailyDiscordTickets, List<DailyDiscordTicketsCompared> dailyDiscordTicketsComp,
      List<DailyDiscordMessages> dailyDiscordMessages, List<DailyDiscordMessagesCompared> dailyDiscordMessagesComp,
      List<DailyMinecraftTickets> dailyMinecraftTickets,
      List<DailyMinecraftTicketsCompared> dailyMinecraftTicketsComp) {
    String level = getEmployeeLevelForDate(levelInfo, date);
    Map<String, Double> constants = getConstantsForLevel(level);
    double productivity = 0.0;

    productivity += calculateProductivityFromList(
        dailyPlaytime, employee, date, constants.get("playtime"));
    productivity += calculateProductivityFromList(
        dailyAfkPlaytime, employee, date, constants.get("afkPlaytime"));
    productivity += calculateProductivityFromList(
        dailyDiscordTickets, employee, date, constants.get("discordTickets"));
    productivity += calculateProductivityFromList(
        dailyDiscordTicketsComp, employee, date, constants.get("discordTicketsComp"));
    productivity += calculateProductivityFromList(
        dailyDiscordMessages, employee, date, constants.get("discordMessages"));
    productivity += calculateProductivityFromList(
        dailyDiscordMessagesComp, employee, date, constants.get("discordMessagesComp"));
    productivity += calculateProductivityFromList(
        dailyMinecraftTickets, employee, date, constants.get("minecraftTickets"));
    productivity += calculateProductivityFromList(
        dailyMinecraftTicketsComp, employee, date, constants.get("minecraftTicketsComp"));

    return productivity;
  }

  private double calculateProductivityFromList(List<?> dataList, Employee employee, LocalDate date, Double constant) {
    if (constant == null)
      return 0.0;
    return dataList.stream()
        .filter(data -> matchesEmployeeAndDate(data, employee, date))
        .mapToDouble(this::extractValue)
        .sum() * constant;
  }

  private boolean matchesEmployeeAndDate(Object entity, Employee employee, LocalDate date) {
    if (entity instanceof DailyPlaytime playtime)
      return playtime.getAid().equals(employee.getId())
          && playtime.getDate().equals(date);
    if (entity instanceof DailyAfkPlaytime afkPlaytime)
      return afkPlaytime.getAid().equals(employee.getId())
          && afkPlaytime.getDate().equals(date);
    if (entity instanceof DailyDiscordTickets discordTickets)
      return discordTickets.getAid().equals(employee.getId())
          && discordTickets.getDate().equals(date);
    if (entity instanceof DailyDiscordTicketsCompared discordTicketsCompared)
      return discordTicketsCompared.getAid().equals(employee.getId())
          && discordTicketsCompared.getDate().equals(date);
    if (entity instanceof DailyDiscordMessages discordMessages)
      return discordMessages.getAid().equals(employee.getId())
          && discordMessages.getDate().equals(date);
    if (entity instanceof DailyDiscordMessagesCompared discordMessagesCompared)
      return discordMessagesCompared.getAid().equals(employee.getId())
          && discordMessagesCompared.getDate().equals(date);
    if (entity instanceof DailyMinecraftTickets minecraftTickets)
      return minecraftTickets.getAid().equals(employee.getId())
          && minecraftTickets.getDate().equals(date);
    if (entity instanceof DailyMinecraftTicketsCompared minecraftTicketsCompared)
      return minecraftTicketsCompared.getAid().equals(employee.getId())
          && minecraftTicketsCompared.getDate().equals(date);

    throw new IllegalArgumentException("Unsupported entity type: " + entity);
  }

  private double extractValue(Object entity) {
    if (entity instanceof DailyPlaytime playtime)
      return playtime.getTime();
    if (entity instanceof DailyAfkPlaytime afkPlaytime)
      return afkPlaytime.getTime();
    if (entity instanceof DailyDiscordMessages discordMessages)
      return discordMessages.getMsgCount();
    if (entity instanceof DailyDiscordTickets discordTickets)
      return discordTickets.getTicketCount();
    if (entity instanceof DailyDiscordTicketsCompared discordTicketsCompared)
      return discordTicketsCompared.getValue();
    if (entity instanceof DailyDiscordMessagesCompared discordMessagesCompared)
      return discordMessagesCompared.getValue();
    if (entity instanceof DailyMinecraftTickets minecraftTickets)
      return minecraftTickets.getTicketCount();
    if (entity instanceof DailyMinecraftTicketsCompared minecraftTicketsCompared)
      return minecraftTicketsCompared.getValue();

    throw new IllegalArgumentException("Unsupported entity type: " + entity);
  }

  private Map<String, Double> getConstantsForLevel(String level) {
    return switch (level) {
      case "helper" -> Map.ofEntries(
          Map.entry("playtime", CalculationConstants.PLAYTIME_HELPER),
          Map.entry("playtimeMax", CalculationConstants.PLAYTIME_MAX_HELPER),
          Map.entry("afkPlaytime", CalculationConstants.AFK_PLAYTIME_HELPER),
          Map.entry("discordMessages", CalculationConstants.DISCORD_MESSAGES_HELPER),
          Map.entry("discordMessagesComp", CalculationConstants.DISCORD_MESSAGES_COMPARED_HELPER),
          Map.entry("discordTickets", null),
          Map.entry("discordTicketsComp", null),
          Map.entry("minecraftTickets", null),
          Map.entry("minecraftTicketsMax", null),
          Map.entry("minecraftTicketsComp", null),
          Map.entry("minecraftTicketsCompMax", null));
      case "support" -> Map.ofEntries(
          Map.entry("playtime", CalculationConstants.PLAYTIME_HELPER),
          Map.entry("playtimeMax", CalculationConstants.PLAYTIME_MAX_HELPER),
          Map.entry("afkPlaytime", CalculationConstants.AFK_PLAYTIME_HELPER),
          Map.entry("discordMessages", CalculationConstants.DISCORD_MESSAGES_HELPER),
          Map.entry("discordMessagesComp", CalculationConstants.DISCORD_MESSAGES_COMPARED_HELPER),
          Map.entry("discordTickets", CalculationConstants.DISCORD_TICKETS_SUPPORT),
          Map.entry("discordTicketsComp", CalculationConstants.DISCORD_TICKETS_COMPARED_SUPPORT),
          Map.entry("minecraftTickets", CalculationConstants.MINECRAFT_TICKETS_SUPPORT),
          Map.entry("minecraftTicketsMax", CalculationConstants.MINECRAFT_TICKETS_MAX_SUPPORT),
          Map.entry("minecraftTicketsComp", CalculationConstants.MINECRAFT_TICKETS_COMPARED_SUPPORT),
          Map.entry("minecraftTicketsCompMax", CalculationConstants.MINECRAFT_TICKETS_COMPARED_MAX_SUPPORT));
      case "chatmod" -> Map.ofEntries(
          Map.entry("playtime", CalculationConstants.PLAYTIME_HELPER),
          Map.entry("playtimeMax", CalculationConstants.PLAYTIME_MAX_HELPER),
          Map.entry("afkPlaytime", CalculationConstants.AFK_PLAYTIME_HELPER),
          Map.entry("discordMessages", CalculationConstants.DISCORD_MESSAGES_HELPER),
          Map.entry("discordMessagesComp", CalculationConstants.DISCORD_MESSAGES_COMPARED_HELPER),
          Map.entry("discordTickets", CalculationConstants.DISCORD_TICKETS_CHATMOD),
          Map.entry("discordTicketsComp", CalculationConstants.DISCORD_TICKETS_COMPARED_CHATMOD),
          Map.entry("minecraftTickets", CalculationConstants.MINECRAFT_TICKETS_CHATMOD),
          Map.entry("minecraftTicketsMax", CalculationConstants.MINECRAFT_TICKETS_MAX_CHATMOD),
          Map.entry("minecraftTicketsComp", CalculationConstants.MINECRAFT_TICKETS_COMPARED_CHATMOD),
          Map.entry("minecraftTicketsCompMax", CalculationConstants.MINECRAFT_TICKETS_COMPARED_MAX_CHATMOD));
      case "overseer" -> Map.ofEntries(
          Map.entry("playtime", CalculationConstants.PLAYTIME_HELPER),
          Map.entry("playtimeMax", CalculationConstants.PLAYTIME_MAX_HELPER),
          Map.entry("afkPlaytime", CalculationConstants.AFK_PLAYTIME_HELPER),
          Map.entry("discordMessages", CalculationConstants.DISCORD_MESSAGES_HELPER),
          Map.entry("discordMessagesComp", CalculationConstants.DISCORD_MESSAGES_COMPARED_HELPER),
          Map.entry("discordTickets", CalculationConstants.DISCORD_TICKETS_OVERSEER),
          Map.entry("discordTicketsComp", CalculationConstants.DISCORD_TICKETS_COMPARED_OVERSEER),
          Map.entry("minecraftTickets", CalculationConstants.MINECRAFT_TICKETS_OVERSEER),
          Map.entry("minecraftTicketsMax", CalculationConstants.MINECRAFT_TICKETS_MAX_OVERSEER),
          Map.entry("minecraftTicketsComp", CalculationConstants.MINECRAFT_TICKETS_COMPARED_OVERSEER),
          Map.entry("minecraftTicketsCompMax", CalculationConstants.MINECRAFT_TICKETS_COMPARED_MAX_OVERSEER));
      case "manager" -> Map.ofEntries(
          Map.entry("playtime", CalculationConstants.PLAYTIME_HELPER),
          Map.entry("playtimeMax", CalculationConstants.PLAYTIME_MAX_HELPER),
          Map.entry("afkPlaytime", CalculationConstants.AFK_PLAYTIME_HELPER),
          Map.entry("discordMessages", CalculationConstants.DISCORD_MESSAGES_HELPER),
          Map.entry("discordMessagesComp", CalculationConstants.DISCORD_MESSAGES_COMPARED_HELPER),
          Map.entry("discordTickets", CalculationConstants.DISCORD_TICKETS_MANAGER),
          Map.entry("discordTicketsComp", CalculationConstants.DISCORD_TICKETS_COMPARED_MANAGER),
          Map.entry("minecraftTickets", CalculationConstants.MINECRAFT_TICKETS_MANAGER),
          Map.entry("minecraftTicketsMax", CalculationConstants.MINECRAFT_TICKETS_MAX_MANAGER),
          Map.entry("minecraftTicketsComp", CalculationConstants.MINECRAFT_TICKETS_COMPARED_MANAGER),
          Map.entry("minecraftTicketsCompMax", CalculationConstants.MINECRAFT_TICKETS_COMPARED_MAX_MANAGER));
      default -> throw new IllegalArgumentException("Unknown level: " + level);
    };
  }
}
