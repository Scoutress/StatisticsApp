package com.scoutress.KaimuxAdminStats.servicesImpl.productivity;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.constants.CalculationConstants;
import com.scoutress.KaimuxAdminStats.entity.complaints.ComplaintsSum;
import com.scoutress.KaimuxAdminStats.entity.discordMessages.AverageDailyDiscordMessages;
import com.scoutress.KaimuxAdminStats.entity.discordMessages.AverageDiscordMessagesCompared;
import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.AverageDailyMinecraftTickets;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.AverageMinecraftTicketsCompared;
import com.scoutress.KaimuxAdminStats.entity.playtime.AveragePlaytimeOverall;
import com.scoutress.KaimuxAdminStats.entity.productivity.Productivity;
import com.scoutress.KaimuxAdminStats.repositories.complaints.ComplaintsSumRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordMessages.AverageDailyDiscordMessagesRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordMessages.AverageDiscordMessagesComparedRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.AverageDailyMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.AverageMinecraftTicketsComparedRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.AveragePlaytimeOverallRepository;
import com.scoutress.KaimuxAdminStats.repositories.productivity.ProductivityRepository;
import com.scoutress.KaimuxAdminStats.services.productivity.ProductivityService;

@Service
public class ProductivityServiceImpl implements ProductivityService {

  private final EmployeeRepository employeeRepository;
  private final AverageDailyDiscordMessagesRepository averageDailyDiscordMessagesRepository;
  private final AverageDiscordMessagesComparedRepository averageDiscordMessagesComparedRepository;
  private final AverageDailyMinecraftTicketsRepository averageDailyMinecraftTicketsRepository;
  private final AverageMinecraftTicketsComparedRepository averageMinecraftTicketsComparedRepository;
  private final AveragePlaytimeOverallRepository averagePlaytimeOverallRepository;
  private final ComplaintsSumRepository complaintsSumRepository;
  private final ProductivityRepository productivityRepository;

  public ProductivityServiceImpl(
      EmployeeRepository employeeRepository,
      AverageDailyDiscordMessagesRepository averageDailyDiscordMessagesRepository,
      AverageDiscordMessagesComparedRepository averageDiscordMessagesComparedRepository,
      AverageDailyMinecraftTicketsRepository averageDailyMinecraftTicketsRepository,
      AverageMinecraftTicketsComparedRepository averageMinecraftTicketsComparedRepository,
      AveragePlaytimeOverallRepository averagePlaytimeOverallRepository,
      ComplaintsSumRepository complaintsSumRepository,
      ProductivityRepository productivityRepository) {
    this.employeeRepository = employeeRepository;
    this.averageDailyDiscordMessagesRepository = averageDailyDiscordMessagesRepository;
    this.averageDiscordMessagesComparedRepository = averageDiscordMessagesComparedRepository;
    this.averageDailyMinecraftTicketsRepository = averageDailyMinecraftTicketsRepository;
    this.averageMinecraftTicketsComparedRepository = averageMinecraftTicketsComparedRepository;
    this.averagePlaytimeOverallRepository = averagePlaytimeOverallRepository;
    this.complaintsSumRepository = complaintsSumRepository;
    this.productivityRepository = productivityRepository;
  }

  @Override
  public void calculateProductivity() {
    List<Employee> allEmployeesData = getAllEmployeesData();

    if (allEmployeesData == null || allEmployeesData.isEmpty()) {
      System.err.println("No employee data found. Cannot proceed.");
      return;
    }

    List<Short> allEmployeeIds = getAllEmployeeIds(allEmployeesData);

    if (allEmployeeIds == null || allEmployeeIds.isEmpty()) {
      System.err.println("No employee IDs found. Cannot proceed.");
      return;
    }

    for (Short employeeId : allEmployeeIds) {
      String employeeLevel = getEmployeeLevelForThisEmployee(employeeId, allEmployeesData);

      if (employeeLevel == null) {
        System.err.println("Employee level not found for employee ID: " + employeeId);
        continue;
      }

      double afkPlaytimeFinalValue = calculateAfkPlaytimeFinalValueForThisEmployee(
          employeeId, employeeLevel);
      double discordMessagesFinalValue = calculateDiscordMessagesFinalValueForThisEmployee(
          employeeId, employeeLevel);
      double discordMessagesComparedFinalValue = calculateDiscordMessagesComparedFinalValueForThisEmployee(
          employeeId, employeeLevel);
      double minecraftTicketsFinalValue = calculateMinecraftTicketsFinalValueForThisEmployee(
          employeeId, employeeLevel);
      double minecraftTicketsComparedFinalValue = calculateMinecraftTicketsComparedFinalValueForThisEmployee(
          employeeId, employeeLevel);
      double playtimeFinalValue = calculatePlaytimeFinalValueForThisEmployee(
          employeeId, employeeLevel);
      double complaintsFinalValue = getComplaintsFinalValueForThisEmployee(employeeId);

      double averageValueOfAllFinals = calculateAverageValueOfAllFinals(
          afkPlaytimeFinalValue, discordMessagesFinalValue,
          discordMessagesComparedFinalValue, minecraftTicketsFinalValue,
          minecraftTicketsComparedFinalValue, playtimeFinalValue, employeeLevel);

      double productivityAfterComplaints = calculateFinalProductivityValue(averageValueOfAllFinals,
          complaintsFinalValue);

      double finalProductivityValue;

      if (employeeLevel.equals("Organizer")) {
        finalProductivityValue = productivityAfterComplaints + 0.1;
      } else {
        finalProductivityValue = productivityAfterComplaints;
      }

      saveProductivityValueForThisEmployee(finalProductivityValue, employeeId);
    }
  }

  public List<Employee> getAllEmployeesData() {
    return employeeRepository.findAll();
  }

  public List<Short> getAllEmployeeIds(List<Employee> allEmployeesData) {
    return allEmployeesData
        .stream()
        .map(Employee::getId)
        .distinct()
        .sorted()
        .toList();
  }

  public String getEmployeeLevelForThisEmployee(Short employeeId, List<Employee> allEmployeesData) {
    return allEmployeesData
        .stream()
        .filter(employee -> employee.getId().equals(employeeId))
        .map(Employee::getLevel)
        .findFirst()
        .orElse(null);
  }

  //
  // Afk playtime
  public double calculateAfkPlaytimeFinalValueForThisEmployee(Short employeeId, String employeeLevel) {
    // Atm there are no functionality for recording afk playtime data.
    // Temporary fixed to the max value.
    return 1;
  }

  // Discord messages
  public double calculateDiscordMessagesFinalValueForThisEmployee(Short employeeId, String employeeLevel) {
    double averageValueOfDiscordMessages = getAverageValueOfDiscordMessages(employeeId);
    double checkedAverageValueOfDiscordMessages = getMaxOrCurrentValueOfDiscordMessages(
        averageValueOfDiscordMessages, employeeLevel);
    double finalValueOfDiscordMessages = calculateAverageValueOfDiscordMessagesWithCoef(
        checkedAverageValueOfDiscordMessages, employeeLevel);

    return finalValueOfDiscordMessages;
  }

  public double getAverageValueOfDiscordMessages(Short employeeId) {
    List<AverageDailyDiscordMessages> data = averageDailyDiscordMessagesRepository.findAll();

    if (data.isEmpty()) {
      return 0;
    }

    return data
        .stream()
        .filter(discordMessages -> discordMessages.getEmployeeId().equals(employeeId))
        .map(AverageDailyDiscordMessages::getValue)
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(0.0);
  }

  public double getMaxOrCurrentValueOfDiscordMessages(double averageValueOfDiscordMessages, String employeeLevel) {
    double managerMaxValue = CalculationConstants.DISCORD_MESSAGES_MAX_MANAGER;
    double overseerMaxValue = CalculationConstants.DISCORD_MESSAGES_MAX_OVERSEER;
    double chatmodMaxValue = CalculationConstants.DISCORD_MESSAGES_MAX_CHATMOD;
    double supportMaxValue = CalculationConstants.DISCORD_MESSAGES_MAX_SUPPORT;
    double helperMaxValue = CalculationConstants.DISCORD_MESSAGES_MAX_HELPER;

    return switch (employeeLevel) {
      case "Manager" ->
        averageValueOfDiscordMessages > managerMaxValue ? managerMaxValue : averageValueOfDiscordMessages;
      case "Overseer", "Organizer" ->
        averageValueOfDiscordMessages > overseerMaxValue ? overseerMaxValue : averageValueOfDiscordMessages;
      case "ChatMod" ->
        averageValueOfDiscordMessages > chatmodMaxValue ? chatmodMaxValue : averageValueOfDiscordMessages;
      case "Support" ->
        averageValueOfDiscordMessages > supportMaxValue ? supportMaxValue : averageValueOfDiscordMessages;
      case "Helper" ->
        averageValueOfDiscordMessages > helperMaxValue ? helperMaxValue : averageValueOfDiscordMessages;
      default -> 0;
    };
  }

  public double calculateAverageValueOfDiscordMessagesWithCoef(
      double checkedAverageValueOfDiscordMessages, String employeeLevel) {
    double managerCoef = CalculationConstants.DISCORD_MESSAGES_MANAGER;
    double overseerCoef = CalculationConstants.DISCORD_MESSAGES_OVERSEER;
    double chatmodCoef = CalculationConstants.DISCORD_MESSAGES_CHATMOD;
    double supportCoef = CalculationConstants.DISCORD_MESSAGES_SUPPORT;
    double helperCoef = CalculationConstants.DISCORD_MESSAGES_HELPER;

    return switch (employeeLevel) {
      case "Manager" -> checkedAverageValueOfDiscordMessages * managerCoef;
      case "Overseer", "Organizer" -> checkedAverageValueOfDiscordMessages * overseerCoef;
      case "ChatMod" -> checkedAverageValueOfDiscordMessages * chatmodCoef;
      case "Support" -> checkedAverageValueOfDiscordMessages * supportCoef;
      case "Helper" -> checkedAverageValueOfDiscordMessages * helperCoef;
      default -> 0;
    };
  }

  // Discord messages compared
  public double calculateDiscordMessagesComparedFinalValueForThisEmployee(Short employeeId, String employeeLevel) {
    double averageValueOfComparedDiscordMessages = getAverageValueOfComparedDiscordMessages(employeeId);
    // TODO Add check for max values
    double finalValueOfComparedDiscordMessages = calculateAverageValueOfComparedDiscordMessagesWithCoef(
        averageValueOfComparedDiscordMessages, employeeLevel);

    return finalValueOfComparedDiscordMessages;
  }

  public double getAverageValueOfComparedDiscordMessages(Short employeeId) {
    List<AverageDiscordMessagesCompared> data = averageDiscordMessagesComparedRepository.findAll();

    if (data.isEmpty()) {
      return 0;
    }

    return data
        .stream()
        .filter(complaints -> complaints.getEmployeeId().equals(employeeId))
        .map(AverageDiscordMessagesCompared::getValue)
        .findFirst()
        .orElse(0.0);
  }

  public double calculateAverageValueOfComparedDiscordMessagesWithCoef(
      double averageValueOfComparedDiscordMessages, String employeeLevel) {
    double managerCoef = CalculationConstants.DISCORD_MESSAGES_COMPARED_MANAGER;
    double overseerCoef = CalculationConstants.DISCORD_MESSAGES_COMPARED_OVERSEER;
    double chatmodCoef = CalculationConstants.DISCORD_MESSAGES_COMPARED_CHATMOD;
    double supportCoef = CalculationConstants.DISCORD_MESSAGES_COMPARED_SUPPORT;
    double helperCoef = CalculationConstants.DISCORD_MESSAGES_COMPARED_HELPER;

    return switch (employeeLevel) {
      case "Manager" -> averageValueOfComparedDiscordMessages * managerCoef;
      case "Overseer", "Organizer" -> averageValueOfComparedDiscordMessages * overseerCoef;
      case "ChatMod" -> averageValueOfComparedDiscordMessages * chatmodCoef;
      case "Support" -> averageValueOfComparedDiscordMessages * supportCoef;
      case "Helper" -> averageValueOfComparedDiscordMessages * helperCoef;
      default -> 0;
    };
  }

  // Minecraft tickets
  public double calculateMinecraftTicketsFinalValueForThisEmployee(Short employeeId, String employeeLevel) {
    double averageValueOfMinecraftTickets = getAverageValueOfMinecraftTickets(employeeId);
    double checkedAverageValueOfMinecraftTickets = getMaxOrCurrentValueOfMinecraftTickets(
        averageValueOfMinecraftTickets, employeeLevel);
    double finalValueOfMinecraftTickets = calculateAverageValueOfMinecraftTicketsWithCoef(
        checkedAverageValueOfMinecraftTickets, employeeLevel);

    return finalValueOfMinecraftTickets;
  }

  public double getAverageValueOfMinecraftTickets(Short employeeId) {
    List<AverageDailyMinecraftTickets> data = averageDailyMinecraftTicketsRepository.findAll();

    if (data.isEmpty()) {
      return 0;
    }

    return data
        .stream()
        .filter(complaints -> complaints.getEmployeeId().equals(employeeId))
        .map(AverageDailyMinecraftTickets::getTickets)
        .findFirst()
        .orElse(0.0);
  }

  public double getMaxOrCurrentValueOfMinecraftTickets(double averageValueOfMinecraftTickets, String employeeLevel) {
    double managerMaxValue = CalculationConstants.MINECRAFT_TICKETS_MAX_MANAGER;
    double overseerMaxValue = CalculationConstants.MINECRAFT_TICKETS_MAX_OVERSEER;
    double chatmodMaxValue = CalculationConstants.MINECRAFT_TICKETS_MAX_CHATMOD;
    double supportMaxValue = CalculationConstants.MINECRAFT_TICKETS_MAX_SUPPORT;

    return switch (employeeLevel) {
      case "Manager" ->
        averageValueOfMinecraftTickets > managerMaxValue ? managerMaxValue : averageValueOfMinecraftTickets;
      case "Overseer", "Organizer" ->
        averageValueOfMinecraftTickets > overseerMaxValue ? overseerMaxValue : averageValueOfMinecraftTickets;
      case "ChatMod" ->
        averageValueOfMinecraftTickets > chatmodMaxValue ? chatmodMaxValue : averageValueOfMinecraftTickets;
      case "Support" ->
        averageValueOfMinecraftTickets > supportMaxValue ? supportMaxValue : averageValueOfMinecraftTickets;
      default -> 0;
    };
  }

  public double calculateAverageValueOfMinecraftTicketsWithCoef(
      double checkedAverageValueOfMinecraftTickets, String employeeLevel) {
    double managerCoef = CalculationConstants.MINECRAFT_TICKETS_MANAGER;
    double overseerCoef = CalculationConstants.MINECRAFT_TICKETS_OVERSEER;
    double chatmodCoef = CalculationConstants.MINECRAFT_TICKETS_CHATMOD;
    double supportCoef = CalculationConstants.MINECRAFT_TICKETS_SUPPORT;

    return switch (employeeLevel) {
      case "Manager" -> checkedAverageValueOfMinecraftTickets * managerCoef;
      case "Overseer", "Organizer" -> checkedAverageValueOfMinecraftTickets * overseerCoef;
      case "ChatMod" -> checkedAverageValueOfMinecraftTickets * chatmodCoef;
      case "Support" -> checkedAverageValueOfMinecraftTickets * supportCoef;
      default -> 0;
    };
  }

  // Minecraft tickets compared
  public double calculateMinecraftTicketsComparedFinalValueForThisEmployee(Short employeeId, String employeeLevel) {
    double averageValueOfMinecraftTicketsCompared = getAverageValueOfMinecraftTicketsCompared(employeeId);
    double checkedAverageValueOfMinecraftTicketsCompared = getMaxOrCurrentValueOfMinecraftTicketsCompared(
        averageValueOfMinecraftTicketsCompared, employeeLevel);
    double finalValueOfMinecraftTicketsCompared = calculateAverageValueOfMinecraftTicketsComparedWithCoef(
        checkedAverageValueOfMinecraftTicketsCompared, employeeLevel);

    return finalValueOfMinecraftTicketsCompared;
  }

  public double getAverageValueOfMinecraftTicketsCompared(Short employeeId) {
    List<AverageMinecraftTicketsCompared> data = averageMinecraftTicketsComparedRepository.findAll();

    if (data.isEmpty()) {
      return 0;
    }

    return data
        .stream()
        .filter(complaints -> complaints.getEmployeeId().equals(employeeId))
        .map(AverageMinecraftTicketsCompared::getValue)
        .findFirst()
        .orElse(0.0);
  }

  public double getMaxOrCurrentValueOfMinecraftTicketsCompared(double averageValueOfMinecraftTicketsCompared,
      String employeeLevel) {
    double managerMaxValue = CalculationConstants.MINECRAFT_TICKETS_COMPARED_MAX_MANAGER;
    double overseerMaxValue = CalculationConstants.MINECRAFT_TICKETS_COMPARED_MAX_OVERSEER;
    double chatmodMaxValue = CalculationConstants.MINECRAFT_TICKETS_COMPARED_MAX_CHATMOD;
    double supportMaxValue = CalculationConstants.MINECRAFT_TICKETS_COMPARED_MAX_SUPPORT;

    return switch (employeeLevel) {
      case "Manager" ->
        averageValueOfMinecraftTicketsCompared > managerMaxValue ? managerMaxValue
            : averageValueOfMinecraftTicketsCompared;
      case "Overseer", "Organizer" ->
        averageValueOfMinecraftTicketsCompared > overseerMaxValue ? overseerMaxValue
            : averageValueOfMinecraftTicketsCompared;
      case "ChatMod" ->
        averageValueOfMinecraftTicketsCompared > chatmodMaxValue ? chatmodMaxValue
            : averageValueOfMinecraftTicketsCompared;
      case "Support" ->
        averageValueOfMinecraftTicketsCompared > supportMaxValue ? supportMaxValue
            : averageValueOfMinecraftTicketsCompared;
      default -> 0;
    };
  }

  public double calculateAverageValueOfMinecraftTicketsComparedWithCoef(
      double checkedAverageValueOfMinecraftTicketsCompared, String employeeLevel) {
    double managerCoef = CalculationConstants.MINECRAFT_TICKETS_COMPARED_MANAGER;
    double overseerCoef = CalculationConstants.MINECRAFT_TICKETS_COMPARED_OVERSEER;
    double chatmodCoef = CalculationConstants.MINECRAFT_TICKETS_COMPARED_CHATMOD;
    double supportCoef = CalculationConstants.MINECRAFT_TICKETS_COMPARED_SUPPORT;

    return switch (employeeLevel) {
      case "Manager" -> checkedAverageValueOfMinecraftTicketsCompared * managerCoef;
      case "Overseer", "Organizer" -> checkedAverageValueOfMinecraftTicketsCompared * overseerCoef;
      case "ChatMod" -> checkedAverageValueOfMinecraftTicketsCompared * chatmodCoef;
      case "Support" -> checkedAverageValueOfMinecraftTicketsCompared * supportCoef;
      default -> 0;
    };
  }

  // Playtime
  public double calculatePlaytimeFinalValueForThisEmployee(Short employeeId, String employeeLevel) {
    double averageValueOfPlaytime = getAverageValueOfPlaytime(employeeId);
    double checkedAverageValueOfPlaytime = getMaxOrCurrentValueOfPlaytime(averageValueOfPlaytime, employeeLevel);
    double finalValueOfPlaytime = calculateAverageValueOfPlaytimeWithCoef(checkedAverageValueOfPlaytime, employeeLevel);

    return finalValueOfPlaytime;
  }

  public double getAverageValueOfPlaytime(Short employeeId) {
    List<AveragePlaytimeOverall> data = averagePlaytimeOverallRepository.findAll();

    if (data.isEmpty()) {
      return 0;
    }

    return data
        .stream()
        .filter(complaints -> complaints.getEmployeeId().equals(employeeId))
        .map(AveragePlaytimeOverall::getPlaytime)
        .findFirst()
        .orElse(0.0);
  }

  public double getMaxOrCurrentValueOfPlaytime(double averageValueOfPlaytime, String employeeLevel) {
    double managerMaxValue = CalculationConstants.PLAYTIME_MAX_MANAGER;
    double overseerMaxValue = CalculationConstants.PLAYTIME_MAX_OVERSEER;
    double chatmodMaxValue = CalculationConstants.PLAYTIME_MAX_CHATMOD;
    double supportMaxValue = CalculationConstants.PLAYTIME_MAX_SUPPORT;
    double helperMaxValue = CalculationConstants.PLAYTIME_MAX_HELPER;

    return switch (employeeLevel) {
      case "Manager" ->
        averageValueOfPlaytime > managerMaxValue ? managerMaxValue : averageValueOfPlaytime;
      case "Overseer", "Organizer" ->
        averageValueOfPlaytime > overseerMaxValue ? overseerMaxValue : averageValueOfPlaytime;
      case "ChatMod" ->
        averageValueOfPlaytime > chatmodMaxValue ? chatmodMaxValue : averageValueOfPlaytime;
      case "Support" ->
        averageValueOfPlaytime > supportMaxValue ? supportMaxValue : averageValueOfPlaytime;
      case "Helper" ->
        averageValueOfPlaytime > helperMaxValue ? helperMaxValue : averageValueOfPlaytime;
      default -> 0;
    };
  }

  public double calculateAverageValueOfPlaytimeWithCoef(
      double checkedAverageValueOfPlaytime, String employeeLevel) {
    double managerCoef = CalculationConstants.PLAYTIME_MANAGER;
    double overseerCoef = CalculationConstants.PLAYTIME_OVERSEER;
    double chatmodCoef = CalculationConstants.PLAYTIME_CHATMOD;
    double supportCoef = CalculationConstants.PLAYTIME_SUPPORT;
    double helperCoef = CalculationConstants.PLAYTIME_HELPER;

    return switch (employeeLevel) {
      case "Manager" -> checkedAverageValueOfPlaytime * managerCoef;
      case "Overseer", "Organizer" -> checkedAverageValueOfPlaytime * overseerCoef;
      case "ChatMod" -> checkedAverageValueOfPlaytime * chatmodCoef;
      case "Support" -> checkedAverageValueOfPlaytime * supportCoef;
      case "Helper" -> checkedAverageValueOfPlaytime * helperCoef;
      default -> 0;
    };
  }

  // Complaints
  public double getComplaintsFinalValueForThisEmployee(Short employeeId) {
    List<ComplaintsSum> data = complaintsSumRepository.findAll();

    if (data.isEmpty()) {
      return 0;
    }

    return data
        .stream()
        .filter(complaints -> complaints.getEmployeeId().equals(employeeId))
        .map(ComplaintsSum::getValue)
        .findFirst()
        .orElse(0);
  }
  //

  public double calculateAverageValueOfAllFinals(
      double afkPlaytimeFinalValue, double discordMessagesFinalValue,
      double discordMessagesComparedFinalValue, double minecraftTicketsFinalValue,
      double minecraftTicketsComparedFinalValue, double playtimeFinalValue, String employeeLevel) {

    double finalValuesSum;
    int parametersCount;

    if (employeeLevel.equals("Helper")) {
      finalValuesSum = afkPlaytimeFinalValue + discordMessagesFinalValue
          + discordMessagesComparedFinalValue + playtimeFinalValue;
      parametersCount = 4;
    } else {
      finalValuesSum = afkPlaytimeFinalValue + discordMessagesFinalValue
          + discordMessagesComparedFinalValue + minecraftTicketsFinalValue
          + minecraftTicketsComparedFinalValue + playtimeFinalValue;
      parametersCount = 6;
    }

    return finalValuesSum / parametersCount;
  }

  public double calculateFinalProductivityValue(double averageValueOfAllFinals, double complaintsFinalValue) {
    return averageValueOfAllFinals - (complaintsFinalValue * 0.01);
  }

  public void saveProductivityValueForThisEmployee(double finalProductivityValue, Short employeeId) {
    Productivity existingRecord = productivityRepository.findByEmployeeId(employeeId);

    if (existingRecord != null) {
      if (existingRecord.getValue() != finalProductivityValue) {
        existingRecord.setValue(finalProductivityValue);
        productivityRepository.save(existingRecord);
      }
    } else {
      Productivity newRecord = new Productivity();
      newRecord.setEmployeeId(employeeId);
      newRecord.setValue(finalProductivityValue);
      productivityRepository.save(newRecord);
    }
  }
}
