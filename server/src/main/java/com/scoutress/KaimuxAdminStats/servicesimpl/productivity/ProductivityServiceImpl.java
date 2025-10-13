package com.scoutress.KaimuxAdminStats.servicesImpl.productivity;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger log = LoggerFactory.getLogger(ProductivityServiceImpl.class);

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
  public void handleProductivity() {
    long startTime = System.currentTimeMillis();
    log.info("=== [START] Productivity calculation ===");

    List<Employee> employees = employeeRepository.findAll();

    if (employees.isEmpty()) {
      log.warn("⚠ No employee data found — skipping productivity calculation.");
      return;
    }

    List<Short> employeeIds = employees.stream()
        .map(Employee::getId)
        .distinct()
        .sorted()
        .collect(Collectors.toList());

    log.info("Found {} employees to process.", employeeIds.size());

    int processed = 0;
    long avgPerEmployeeMs = 0;

    for (Short employeeId : employeeIds) {
      long empStart = System.currentTimeMillis();

      try {
        String level = employees.stream()
            .filter(e -> e.getId().equals(employeeId))
            .map(Employee::getLevel)
            .findFirst()
            .orElse(null);

        if (level == null) {
          log.warn("Employee ID {} has no level defined — skipping.", employeeId);
          continue;
        }

        double discordMessages = calculateDiscordMessagesFinalValueForThisEmployee(employeeId, level);
        double discordMessagesCompared = calculateDiscordMessagesComparedFinalValueForThisEmployee(employeeId, level);
        double minecraftTickets = calculateMinecraftTicketsFinalValueForThisEmployee(employeeId, level);
        double minecraftTicketsCompared = calculateMinecraftTicketsComparedFinalValueForThisEmployee(employeeId, level);
        double playtime = calculatePlaytimeFinalValueForThisEmployee(employeeId, level);
        double complaints = getComplaintsFinalValueForThisEmployee(employeeId);

        double average = calculateAverageValueOfAllFinals(
            discordMessages, discordMessagesCompared, minecraftTickets,
            minecraftTicketsCompared, playtime, level);

        double afterComplaints = calculateFinalProductivityValue(average, complaints);

        double finalProductivity = level.equals("Organizer")
            ? afterComplaints + 0.1
            : afterComplaints;

        saveProductivityValueForThisEmployee(finalProductivity, employeeId);

        if (log.isDebugEnabled()) {
          log.debug(
              "Employee {} [{}]: Discord={}, DiscordComp={}, Tickets={}, TicketsComp={}, Playtime={}, Complaints={}, Final={}",
              employeeId, level, discordMessages, discordMessagesCompared,
              minecraftTickets, minecraftTicketsCompared, playtime, complaints, finalProductivity);
        }

      } catch (Exception e) {
        log.error("❌ Error while calculating productivity for employee ID {}: {}", employeeId, e.getMessage(), e);
      }

      processed++;
      long empTime = System.currentTimeMillis() - empStart;
      avgPerEmployeeMs = (avgPerEmployeeMs * (processed - 1) + empTime) / processed;

      int progress = (int) ((processed / (double) employeeIds.size()) * 100);
      long remaining = avgPerEmployeeMs * (employeeIds.size() - processed);
      String eta = formatMillis(remaining);

      if (processed % 5 == 0 || processed == employeeIds.size()) {
        log.info("Progress: {}% ({}/{}) employees processed | ETA: {}", progress, processed, employeeIds.size(), eta);
      }
    }

    log.info("✅ Productivity calculation completed for {} employees in {} ms",
        employeeIds.size(), System.currentTimeMillis() - startTime);
  }

  // === Utility method for ETA ===
  private String formatMillis(long ms) {
    long sec = ms / 1000;
    long min = sec / 60;
    sec = sec % 60;
    return String.format("%dm %ds", min, sec);
  }

  // === Calculations ===

  private double getComplaintsFinalValueForThisEmployee(Short employeeId) {
    return complaintsSumRepository.findAll().stream()
        .filter(c -> c.getEmployeeId().equals(employeeId))
        .mapToDouble(ComplaintsSum::getValue)
        .findFirst()
        .orElse(0);
  }

  private double calculateAverageValueOfAllFinals(
      double discordMessages, double discordMessagesCompared,
      double minecraftTickets, double minecraftTicketsCompared,
      double playtime, String level) {

    double sum;
    int count;

    if (level.equals("Helper")) {
      sum = discordMessages + discordMessagesCompared + playtime;
      count = 3;
    } else {
      sum = discordMessages + discordMessagesCompared + minecraftTickets + minecraftTicketsCompared + playtime;
      count = 5;
    }

    return sum / count;
  }

  private double calculateFinalProductivityValue(double avg, double complaints) {
    return avg - (complaints * 0.01);
  }

  private void saveProductivityValueForThisEmployee(double value, Short employeeId) {
    Productivity existing = productivityRepository.findByEmployeeId(employeeId);

    if (existing != null) {
      if (existing.getValue() != value) {
        existing.setValue(value);
        productivityRepository.save(existing);
      }
    } else {
      Productivity record = new Productivity();
      record.setEmployeeId(employeeId);
      record.setValue(value);
      productivityRepository.save(record);
    }
  }

  // === Discord Messages ===
  private double calculateDiscordMessagesFinalValueForThisEmployee(Short id, String level) {
    double avg = getAverageValueOfDiscordMessages(id);
    double capped = getMaxOrCurrentValueOfDiscordMessages(avg, level);
    return calculateAverageValueOfDiscordMessagesWithCoef(capped, level);
  }

  private double getAverageValueOfDiscordMessages(Short id) {
    return averageDailyDiscordMessagesRepository.findAll().stream()
        .filter(v -> v.getEmployeeId().equals(id))
        .map(AverageDailyDiscordMessages::getValue)
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(0.0);
  }

  private double getMaxOrCurrentValueOfDiscordMessages(double val, String lvl) {
    return switch (lvl) {
      case "Manager" -> Math.min(val, CalculationConstants.DISCORD_MESSAGES_MAX_MANAGER);
      case "Overseer", "Organizer" -> Math.min(val, CalculationConstants.DISCORD_MESSAGES_MAX_OVERSEER);
      case "ChatMod" -> Math.min(val, CalculationConstants.DISCORD_MESSAGES_MAX_CHATMOD);
      case "Support" -> Math.min(val, CalculationConstants.DISCORD_MESSAGES_MAX_SUPPORT);
      case "Helper" -> Math.min(val, CalculationConstants.DISCORD_MESSAGES_MAX_HELPER);
      default -> 0;
    };
  }

  private double calculateAverageValueOfDiscordMessagesWithCoef(double val, String lvl) {
    return switch (lvl) {
      case "Manager" -> val * CalculationConstants.DISCORD_MESSAGES_MANAGER;
      case "Overseer", "Organizer" -> val * CalculationConstants.DISCORD_MESSAGES_OVERSEER;
      case "ChatMod" -> val * CalculationConstants.DISCORD_MESSAGES_CHATMOD;
      case "Support" -> val * CalculationConstants.DISCORD_MESSAGES_SUPPORT;
      case "Helper" -> val * CalculationConstants.DISCORD_MESSAGES_HELPER;
      default -> 0;
    };
  }

  // === Discord Compared ===
  private double calculateDiscordMessagesComparedFinalValueForThisEmployee(Short id, String lvl) {
    double avg = getAverageValueOfComparedDiscordMessages(id);
    double capped = getMaxOrCurrentValueOfComparedDiscordMessages(avg, lvl);
    return calculateAverageValueOfComparedDiscordMessagesWithCoef(capped, lvl);
  }

  private double getAverageValueOfComparedDiscordMessages(Short id) {
    return averageDiscordMessagesComparedRepository.findAll().stream()
        .filter(v -> v.getEmployeeId().equals(id))
        .map(AverageDiscordMessagesCompared::getValue)
        .findFirst()
        .orElse(0.0);
  }

  private double getMaxOrCurrentValueOfComparedDiscordMessages(double val, String lvl) {
    return switch (lvl) {
      case "Manager" -> Math.min(val, CalculationConstants.DISCORD_MESSAGES_COMPARED_MAX_MANAGER);
      case "Overseer", "Organizer" -> Math.min(val, CalculationConstants.DISCORD_MESSAGES_COMPARED_MAX_OVERSEER);
      case "ChatMod" -> Math.min(val, CalculationConstants.DISCORD_MESSAGES_COMPARED_MAX_CHATMOD);
      case "Support" -> Math.min(val, CalculationConstants.DISCORD_MESSAGES_COMPARED_MAX_SUPPORT);
      case "Helper" -> Math.min(val, CalculationConstants.DISCORD_MESSAGES_COMPARED_MAX_HELPER);
      default -> 0;
    };
  }

  private double calculateAverageValueOfComparedDiscordMessagesWithCoef(double val, String lvl) {
    return switch (lvl) {
      case "Manager" -> val * CalculationConstants.DISCORD_MESSAGES_COMPARED_MANAGER;
      case "Overseer", "Organizer" -> val * CalculationConstants.DISCORD_MESSAGES_COMPARED_OVERSEER;
      case "ChatMod" -> val * CalculationConstants.DISCORD_MESSAGES_COMPARED_CHATMOD;
      case "Support" -> val * CalculationConstants.DISCORD_MESSAGES_COMPARED_SUPPORT;
      case "Helper" -> val * CalculationConstants.DISCORD_MESSAGES_COMPARED_HELPER;
      default -> 0;
    };
  }

  // === Minecraft Tickets ===
  private double calculateMinecraftTicketsFinalValueForThisEmployee(Short id, String lvl) {
    double avg = getAverageValueOfMinecraftTickets(id);
    double capped = getMaxOrCurrentValueOfMinecraftTickets(avg, lvl);
    return calculateAverageValueOfMinecraftTicketsWithCoef(capped, lvl);
  }

  private double getAverageValueOfMinecraftTickets(Short id) {
    return averageDailyMinecraftTicketsRepository.findAll().stream()
        .filter(v -> v.getEmployeeId().equals(id))
        .map(AverageDailyMinecraftTickets::getTickets)
        .findFirst()
        .orElse(0.0);
  }

  private double getMaxOrCurrentValueOfMinecraftTickets(double val, String lvl) {
    return switch (lvl) {
      case "Manager" -> Math.min(val, CalculationConstants.MINECRAFT_TICKETS_MAX_MANAGER);
      case "Overseer", "Organizer" -> Math.min(val, CalculationConstants.MINECRAFT_TICKETS_MAX_OVERSEER);
      case "ChatMod" -> Math.min(val, CalculationConstants.MINECRAFT_TICKETS_MAX_CHATMOD);
      case "Support" -> Math.min(val, CalculationConstants.MINECRAFT_TICKETS_MAX_SUPPORT);
      default -> 0;
    };
  }

  private double calculateAverageValueOfMinecraftTicketsWithCoef(double val, String lvl) {
    return switch (lvl) {
      case "Manager" -> val * CalculationConstants.MINECRAFT_TICKETS_MANAGER;
      case "Overseer", "Organizer" -> val * CalculationConstants.MINECRAFT_TICKETS_OVERSEER;
      case "ChatMod" -> val * CalculationConstants.MINECRAFT_TICKETS_CHATMOD;
      case "Support" -> val * CalculationConstants.MINECRAFT_TICKETS_SUPPORT;
      default -> 0;
    };
  }

  // === Minecraft Tickets Compared ===
  private double calculateMinecraftTicketsComparedFinalValueForThisEmployee(Short id, String lvl) {
    double avg = getAverageValueOfMinecraftTicketsCompared(id);
    double capped = getMaxOrCurrentValueOfMinecraftTicketsCompared(avg, lvl);
    return calculateAverageValueOfMinecraftTicketsComparedWithCoef(capped, lvl);
  }

  private double getAverageValueOfMinecraftTicketsCompared(Short id) {
    return averageMinecraftTicketsComparedRepository.findAll().stream()
        .filter(v -> v.getEmployeeId().equals(id))
        .map(AverageMinecraftTicketsCompared::getValue)
        .findFirst()
        .orElse(0.0);
  }

  private double getMaxOrCurrentValueOfMinecraftTicketsCompared(double val, String lvl) {
    return switch (lvl) {
      case "Manager" -> Math.min(val, CalculationConstants.MINECRAFT_TICKETS_COMPARED_MAX_MANAGER);
      case "Overseer", "Organizer" -> Math.min(val, CalculationConstants.MINECRAFT_TICKETS_COMPARED_MAX_OVERSEER);
      case "ChatMod" -> Math.min(val, CalculationConstants.MINECRAFT_TICKETS_COMPARED_MAX_CHATMOD);
      case "Support" -> Math.min(val, CalculationConstants.MINECRAFT_TICKETS_COMPARED_MAX_SUPPORT);
      default -> 0;
    };
  }

  private double calculateAverageValueOfMinecraftTicketsComparedWithCoef(double val, String lvl) {
    return switch (lvl) {
      case "Manager" -> val * CalculationConstants.MINECRAFT_TICKETS_COMPARED_MANAGER;
      case "Overseer", "Organizer" -> val * CalculationConstants.MINECRAFT_TICKETS_COMPARED_OVERSEER;
      case "ChatMod" -> val * CalculationConstants.MINECRAFT_TICKETS_COMPARED_CHATMOD;
      case "Support" -> val * CalculationConstants.MINECRAFT_TICKETS_COMPARED_SUPPORT;
      default -> 0;
    };
  }

  // === Playtime ===
  private double calculatePlaytimeFinalValueForThisEmployee(Short id, String lvl) {
    double avg = getAverageValueOfPlaytime(id);
    double capped = getMaxOrCurrentValueOfPlaytime(avg, lvl);
    return calculateAverageValueOfPlaytimeWithCoef(capped, lvl);
  }

  private double getAverageValueOfPlaytime(Short id) {
    return averagePlaytimeOverallRepository.findAll().stream()
        .filter(v -> v.getEmployeeId().equals(id))
        .map(AveragePlaytimeOverall::getPlaytime)
        .findFirst()
        .orElse(0.0);
  }

  private double getMaxOrCurrentValueOfPlaytime(double val, String lvl) {
    return switch (lvl) {
      case "Manager" -> Math.min(val, CalculationConstants.PLAYTIME_MAX_MANAGER);
      case "Overseer", "Organizer" -> Math.min(val, CalculationConstants.PLAYTIME_MAX_OVERSEER);
      case "ChatMod" -> Math.min(val, CalculationConstants.PLAYTIME_MAX_CHATMOD);
      case "Support" -> Math.min(val, CalculationConstants.PLAYTIME_MAX_SUPPORT);
      case "Helper" -> Math.min(val, CalculationConstants.PLAYTIME_MAX_HELPER);
      default -> 0;
    };
  }

  private double calculateAverageValueOfPlaytimeWithCoef(double val, String lvl) {
    return switch (lvl) {
      case "Manager" -> val * CalculationConstants.PLAYTIME_MANAGER;
      case "Overseer", "Organizer" -> val * CalculationConstants.PLAYTIME_OVERSEER;
      case "ChatMod" -> val * CalculationConstants.PLAYTIME_CHATMOD;
      case "Support" -> val * CalculationConstants.PLAYTIME_SUPPORT;
      case "Helper" -> val * CalculationConstants.PLAYTIME_HELPER;
      default -> 0;
    };
  }
}
