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
        .sorted()
        .collect(Collectors.toList());

    log.info("Found {} employees to process.", employeeIds.size());
    int processed = 0;

    for (Short employeeId : employeeIds) {
      long empStart = System.currentTimeMillis();

      try {
        Employee employee = employees.stream()
            .filter(e -> e.getId().equals(employeeId))
            .findFirst()
            .orElse(null);

        if (employee == null || employee.getLevel() == null) {
          log.warn("⚠ Employee ID {} missing data or level — skipping.", employeeId);
          continue;
        }

        String level = employee.getLevel();

        if (log.isDebugEnabled()) {
          log.debug("➡️ Processing Employee {} [{}]", employeeId, level);
        }

        // === Step-by-step calculation
        double discordMessages = calculateDiscordMessagesFinalValueForThisEmployee(employeeId, level);
        double discordMessagesCompared = calculateDiscordMessagesComparedFinalValueForThisEmployee(employeeId, level);
        double minecraftTickets = calculateMinecraftTicketsFinalValueForThisEmployee(employeeId, level);
        double minecraftTicketsCompared = calculateMinecraftTicketsComparedFinalValueForThisEmployee(employeeId, level);
        double playtime = calculatePlaytimeFinalValueForThisEmployee(employeeId, level);
        double complaints = getComplaintsFinalValueForThisEmployee(employeeId);

        if (log.isTraceEnabled()) {
          log.trace(
              "[Employee {}] Raw metrics → Discord={}, DiscordComp={}, MC={}, MCComp={}, Playtime={}, Complaints={}",
              employeeId, discordMessages, discordMessagesCompared, minecraftTickets,
              minecraftTicketsCompared, playtime, complaints);
        }

        double average = calculateAverageValueOfAllFinals(
            discordMessages, discordMessagesCompared, minecraftTickets,
            minecraftTicketsCompared, playtime, level);

        double finalProductivity = calculateFinalProductivityValue(average, complaints);

        saveProductivityValueForThisEmployee(finalProductivity, employeeId);

        long empElapsed = System.currentTimeMillis() - empStart;

        log.info("✅ Employee {} [{}] -> Final productivity = {} ({} ms)",
            employeeId, level, String.format("%.4f", finalProductivity), empElapsed);

        if (log.isDebugEnabled()) {
          log.debug(
              "Details for Employee {} [{}]: Discord={}, DiscordComp={}, Tickets={}, TicketsComp={}, Playtime={}, Complaints={}, Average={}, Final={}",
              employeeId, level, discordMessages, discordMessagesCompared,
              minecraftTickets, minecraftTicketsCompared, playtime, complaints,
              String.format("%.4f", average), String.format("%.4f", finalProductivity));
        }

      } catch (Exception e) {
        log.error("❌ Error calculating productivity for employee ID {}: {}", employeeId, e.getMessage(), e);
      }

      processed++;
      if (processed % 10 == 0 || processed == employeeIds.size()) {
        log.info("Progress: processed {}/{} employees.", processed, employeeIds.size());
      }
    }

    log.info("✅ Productivity calculation completed for {} employees in {} ms",
        employeeIds.size(), System.currentTimeMillis() - startTime);
  }

  // === Complaints ===
  private double getComplaintsFinalValueForThisEmployee(Short employeeId) {
    double val = complaintsSumRepository.findAll().stream()
        .filter(c -> c.getEmployeeId().equals(employeeId))
        .mapToDouble(ComplaintsSum::getValue)
        .findFirst()
        .orElse(0);
    if (log.isTraceEnabled())
      log.trace("[Complaints] emp={} val={}", employeeId, val);
    return val;
  }

  // === Average Calculation ===
  private double calculateAverageValueOfAllFinals(
      double discordMessages, double discordMessagesCompared,
      double minecraftTickets, double minecraftTicketsCompared,
      double playtime, String level) {

    double sum;
    int count;

    if ("Helper".equals(level)) {
      sum = discordMessages + discordMessagesCompared + playtime;
      count = 3;
    } else {
      sum = discordMessages + discordMessagesCompared + minecraftTickets + minecraftTicketsCompared + playtime;
      count = 5;
    }

    double result = sum / count;
    if (log.isTraceEnabled()) {
      log.trace("[Average] lvl={} sum={} count={} → {}", level, sum, count, result);
    }
    return result;
  }

  private double calculateFinalProductivityValue(double avg, double complaints) {
    double result = avg - (complaints * 0.01);
    if (log.isTraceEnabled()) {
      log.trace("[FinalProductivity] avg={} complaints={} → {}", avg, complaints, result);
    }
    return result;
  }

  private void saveProductivityValueForThisEmployee(double value, Short employeeId) {
    long start = System.currentTimeMillis();

    Productivity existing = productivityRepository.findByEmployeeId(employeeId);
    if (existing != null) {
      if (existing.getValue() != value) {
        existing.setValue(value);
        productivityRepository.save(existing);
        log.debug("[DB] Updated existing productivity emp={} val={}", employeeId, value);
      } else {
        log.trace("[DB] No change for emp={} (existing={})", employeeId, existing.getValue());
      }
    } else {
      Productivity record = new Productivity();
      record.setEmployeeId(employeeId);
      record.setValue(value);
      productivityRepository.save(record);
      log.debug("[DB] Inserted new productivity emp={} val={}", employeeId, value);
    }

    if (log.isTraceEnabled()) {
      log.trace("[DB timing] emp={} saved in {} ms", employeeId, System.currentTimeMillis() - start);
    }
  }

  // === Discord Messages ===
  private double calculateDiscordMessagesFinalValueForThisEmployee(Short id, String level) {
    double avg = getAverageValueOfDiscordMessages(id);
    double capped = getMaxOrCurrentValueOfDiscordMessages(avg, level);
    double result = calculateAverageValueOfDiscordMessagesWithCoef(capped, level);
    if (log.isTraceEnabled()) {
      log.trace("[Discord] emp={} lvl={} avg={} capped={} result={}", id, level, avg, capped, result);
    }
    return result;
  }

  private double getAverageValueOfDiscordMessages(Short id) {
    double val = averageDailyDiscordMessagesRepository.findAll().stream()
        .filter(v -> v.getEmployeeId().equals(id))
        .map(AverageDailyDiscordMessages::getValue)
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(0.0);
    if (log.isTraceEnabled())
      log.trace("[DiscordAvg] emp={} val={}", id, val);
    return val;
  }

  private double getMaxOrCurrentValueOfDiscordMessages(double val, String lvl) {
    double capped = switch (lvl) {
      case "Manager" -> Math.min(val, CalculationConstants.DISCORD_MESSAGES_MAX_MANAGER);
      case "Overseer", "Organizer" -> Math.min(val, CalculationConstants.DISCORD_MESSAGES_MAX_OVERSEER);
      case "ChatMod" -> Math.min(val, CalculationConstants.DISCORD_MESSAGES_MAX_CHATMOD);
      case "Support" -> Math.min(val, CalculationConstants.DISCORD_MESSAGES_MAX_SUPPORT);
      case "Helper" -> Math.min(val, CalculationConstants.DISCORD_MESSAGES_MAX_HELPER);
      default -> 0;
    };
    if (log.isTraceEnabled())
      log.trace("[DiscordCap] lvl={} val={} → {}", lvl, val, capped);
    return capped;
  }

  private double calculateAverageValueOfDiscordMessagesWithCoef(double val, String lvl) {
    double result = switch (lvl) {
      case "Manager" -> val * CalculationConstants.DISCORD_MESSAGES_MANAGER;
      case "Overseer", "Organizer" -> val * CalculationConstants.DISCORD_MESSAGES_OVERSEER;
      case "ChatMod" -> val * CalculationConstants.DISCORD_MESSAGES_CHATMOD;
      case "Support" -> val * CalculationConstants.DISCORD_MESSAGES_SUPPORT;
      case "Helper" -> val * CalculationConstants.DISCORD_MESSAGES_HELPER;
      default -> 0;
    };
    if (log.isTraceEnabled())
      log.trace("[DiscordCoef] lvl={} val={} → {}", lvl, val, result);
    return result;
  }

  // === Discord Compared ===
  private double calculateDiscordMessagesComparedFinalValueForThisEmployee(Short id, String lvl) {
    double avg = getAverageValueOfComparedDiscordMessages(id);
    double capped = getMaxOrCurrentValueOfComparedDiscordMessages(avg, lvl);
    double result = calculateAverageValueOfComparedDiscordMessagesWithCoef(capped, lvl);
    if (log.isTraceEnabled())
      log.trace("[DiscordComp] emp={} lvl={} avg={} capped={} result={}", id, lvl, avg, capped, result);
    return result;
  }

  private double getAverageValueOfComparedDiscordMessages(Short id) {
    double val = averageDiscordMessagesComparedRepository.findAll().stream()
        .filter(v -> v.getEmployeeId().equals(id))
        .map(AverageDiscordMessagesCompared::getValue)
        .findFirst()
        .orElse(0.0);
    if (log.isTraceEnabled())
      log.trace("[DiscordCompAvg] emp={} val={}", id, val);
    return val;
  }

  private double getMaxOrCurrentValueOfComparedDiscordMessages(double val, String lvl) {
    double capped = switch (lvl) {
      case "Manager" -> Math.min(val, CalculationConstants.DISCORD_MESSAGES_COMPARED_MAX_MANAGER);
      case "Overseer", "Organizer" -> Math.min(val, CalculationConstants.DISCORD_MESSAGES_COMPARED_MAX_OVERSEER);
      case "ChatMod" -> Math.min(val, CalculationConstants.DISCORD_MESSAGES_COMPARED_MAX_CHATMOD);
      case "Support" -> Math.min(val, CalculationConstants.DISCORD_MESSAGES_COMPARED_MAX_SUPPORT);
      case "Helper" -> Math.min(val, CalculationConstants.DISCORD_MESSAGES_COMPARED_MAX_HELPER);
      default -> 0;
    };
    if (log.isTraceEnabled())
      log.trace("[DiscordCompCap] lvl={} val={} → {}", lvl, val, capped);
    return capped;
  }

  private double calculateAverageValueOfComparedDiscordMessagesWithCoef(double val, String lvl) {
    double result = switch (lvl) {
      case "Manager" -> val * CalculationConstants.DISCORD_MESSAGES_COMPARED_MANAGER;
      case "Overseer", "Organizer" -> val * CalculationConstants.DISCORD_MESSAGES_COMPARED_OVERSEER;
      case "ChatMod" -> val * CalculationConstants.DISCORD_MESSAGES_COMPARED_CHATMOD;
      case "Support" -> val * CalculationConstants.DISCORD_MESSAGES_COMPARED_SUPPORT;
      case "Helper" -> val * CalculationConstants.DISCORD_MESSAGES_COMPARED_HELPER;
      default -> 0;
    };
    if (log.isTraceEnabled())
      log.trace("[DiscordCompCoef] lvl={} val={} → {}", lvl, val, result);
    return result;
  }

  // === Minecraft Tickets ===
  private double calculateMinecraftTicketsFinalValueForThisEmployee(Short id, String lvl) {
    double avg = getAverageValueOfMinecraftTickets(id);
    double capped = getMaxOrCurrentValueOfMinecraftTickets(avg, lvl);
    double result = calculateAverageValueOfMinecraftTicketsWithCoef(capped, lvl);
    if (log.isTraceEnabled())
      log.trace("[Tickets] emp={} lvl={} avg={} capped={} result={}", id, lvl, avg, capped, result);
    return result;
  }

  private double getAverageValueOfMinecraftTickets(Short id) {
    double val = averageDailyMinecraftTicketsRepository.findAll().stream()
        .filter(v -> v.getEmployeeId().equals(id))
        .map(AverageDailyMinecraftTickets::getTickets)
        .findFirst()
        .orElse(0.0);
    if (log.isTraceEnabled())
      log.trace("[TicketsAvg] emp={} val={}", id, val);
    return val;
  }

  private double getMaxOrCurrentValueOfMinecraftTickets(double val, String lvl) {
    double capped = switch (lvl) {
      case "Manager" -> Math.min(val, CalculationConstants.MINECRAFT_TICKETS_MAX_MANAGER);
      case "Overseer", "Organizer" -> Math.min(val, CalculationConstants.MINECRAFT_TICKETS_MAX_OVERSEER);
      case "ChatMod" -> Math.min(val, CalculationConstants.MINECRAFT_TICKETS_MAX_CHATMOD);
      case "Support" -> Math.min(val, CalculationConstants.MINECRAFT_TICKETS_MAX_SUPPORT);
      default -> 0;
    };
    if (log.isTraceEnabled())
      log.trace("[TicketsCap] lvl={} val={} → {}", lvl, val, capped);
    return capped;
  }

  private double calculateAverageValueOfMinecraftTicketsWithCoef(double val, String lvl) {
    double result = switch (lvl) {
      case "Manager" -> val * CalculationConstants.MINECRAFT_TICKETS_MANAGER;
      case "Overseer", "Organizer" -> val * CalculationConstants.MINECRAFT_TICKETS_OVERSEER;
      case "ChatMod" -> val * CalculationConstants.MINECRAFT_TICKETS_CHATMOD;
      case "Support" -> val * CalculationConstants.MINECRAFT_TICKETS_SUPPORT;
      default -> 0;
    };
    if (log.isTraceEnabled())
      log.trace("[TicketsCoef] lvl={} val={} → {}", lvl, val, result);
    return result;
  }

  // === Minecraft Tickets Compared ===
  private double calculateMinecraftTicketsComparedFinalValueForThisEmployee(Short id, String lvl) {
    double avg = getAverageValueOfMinecraftTicketsCompared(id);
    double capped = getMaxOrCurrentValueOfMinecraftTicketsCompared(avg, lvl);
    double result = calculateAverageValueOfMinecraftTicketsComparedWithCoef(capped, lvl);
    if (log.isTraceEnabled())
      log.trace("[TicketsComp] emp={} lvl={} avg={} capped={} result={}", id, lvl, avg, capped, result);
    return result;
  }

  private double getAverageValueOfMinecraftTicketsCompared(Short id) {
    double val = averageMinecraftTicketsComparedRepository.findAll().stream()
        .filter(v -> v.getEmployeeId().equals(id))
        .map(AverageMinecraftTicketsCompared::getValue)
        .findFirst()
        .orElse(0.0);
    if (log.isTraceEnabled())
      log.trace("[TicketsCompAvg] emp={} val={}", id, val);
    return val;
  }

  private double getMaxOrCurrentValueOfMinecraftTicketsCompared(double val, String lvl) {
    double capped = switch (lvl) {
      case "Manager" -> Math.min(val, CalculationConstants.MINECRAFT_TICKETS_COMPARED_MAX_MANAGER);
      case "Overseer", "Organizer" -> Math.min(val, CalculationConstants.MINECRAFT_TICKETS_COMPARED_MAX_OVERSEER);
      case "ChatMod" -> Math.min(val, CalculationConstants.MINECRAFT_TICKETS_COMPARED_MAX_CHATMOD);
      case "Support" -> Math.min(val, CalculationConstants.MINECRAFT_TICKETS_COMPARED_MAX_SUPPORT);
      default -> 0;
    };
    if (log.isTraceEnabled())
      log.trace("[TicketsCompCap] lvl={} val={} → {}", lvl, val, capped);
    return capped;
  }

  private double calculateAverageValueOfMinecraftTicketsComparedWithCoef(double val, String lvl) {
    double result = switch (lvl) {
      case "Manager" -> val * CalculationConstants.MINECRAFT_TICKETS_COMPARED_MANAGER;
      case "Overseer", "Organizer" -> val * CalculationConstants.MINECRAFT_TICKETS_COMPARED_OVERSEER;
      case "ChatMod" -> val * CalculationConstants.MINECRAFT_TICKETS_COMPARED_CHATMOD;
      case "Support" -> val * CalculationConstants.MINECRAFT_TICKETS_COMPARED_SUPPORT;
      default -> 0;
    };
    if (log.isTraceEnabled())
      log.trace("[TicketsCompCoef] lvl={} val={} → {}", lvl, val, result);
    return result;
  }

  // === Playtime ===
  private double calculatePlaytimeFinalValueForThisEmployee(Short id, String lvl) {
    double avg = getAverageValueOfPlaytime(id);
    double capped = getMaxOrCurrentValueOfPlaytime(avg, lvl);
    double result = calculateAverageValueOfPlaytimeWithCoef(capped, lvl);
    if (log.isTraceEnabled())
      log.trace("[Playtime] emp={} lvl={} avg={} capped={} result={}", id, lvl, avg, capped, result);
    return result;
  }

  private double getAverageValueOfPlaytime(Short id) {
    double val = averagePlaytimeOverallRepository.findAll().stream()
        .filter(v -> v.getEmployeeId().equals(id))
        .map(AveragePlaytimeOverall::getPlaytime)
        .findFirst()
        .orElse(0.0);
    if (log.isTraceEnabled())
      log.trace("[PlaytimeAvg] emp={} val={}", id, val);
    return val;
  }

  private double getMaxOrCurrentValueOfPlaytime(double val, String lvl) {
    double capped = switch (lvl) {
      case "Manager" -> Math.min(val, CalculationConstants.PLAYTIME_MAX_MANAGER);
      case "Overseer", "Organizer" -> Math.min(val, CalculationConstants.PLAYTIME_MAX_OVERSEER);
      case "ChatMod" -> Math.min(val, CalculationConstants.PLAYTIME_MAX_CHATMOD);
      case "Support" -> Math.min(val, CalculationConstants.PLAYTIME_MAX_SUPPORT);
      case "Helper" -> Math.min(val, CalculationConstants.PLAYTIME_MAX_HELPER);
      default -> 0;
    };
    if (log.isTraceEnabled())
      log.trace("[PlaytimeCap] lvl={} val={} → {}", lvl, val, capped);
    return capped;
  }

  private double calculateAverageValueOfPlaytimeWithCoef(double val, String lvl) {
    double result = switch (lvl) {
      case "Manager" -> val * CalculationConstants.PLAYTIME_MANAGER;
      case "Overseer", "Organizer" -> val * CalculationConstants.PLAYTIME_OVERSEER;
      case "ChatMod" -> val * CalculationConstants.PLAYTIME_CHATMOD;
      case "Support" -> val * CalculationConstants.PLAYTIME_SUPPORT;
      case "Helper" -> val * CalculationConstants.PLAYTIME_HELPER;
      default -> 0;
    };
    if (log.isTraceEnabled())
      log.trace("[PlaytimeCoef] lvl={} val={} → {}", lvl, val, result);
    return result;
  }
}
