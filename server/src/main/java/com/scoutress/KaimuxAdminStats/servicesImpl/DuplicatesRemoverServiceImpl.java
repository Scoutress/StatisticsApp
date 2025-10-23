package com.scoutress.KaimuxAdminStats.servicesImpl;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.repositories.discordMessages.DailyDiscordMessagesRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.AverageDailyMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.AverageMinecraftTicketsComparedRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.AverageMinecraftTicketsPerPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.DailyMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.TotalMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.services.DuplicatesRemoverService;

import jakarta.transaction.Transactional;

@Service
public class DuplicatesRemoverServiceImpl implements DuplicatesRemoverService {

  private static final Logger log = LoggerFactory.getLogger(DuplicatesRemoverServiceImpl.class);

  private final DailyDiscordMessagesRepository dailyDiscordMessagesRepository;
  private final DailyMinecraftTicketsRepository dailyMinecraftTicketsRepository;
  private final AverageDailyMinecraftTicketsRepository averageDailyMinecraftTicketsRepository;
  private final AverageMinecraftTicketsPerPlaytimeRepository averageMinecraftTicketsPerPlaytimeRepository;
  private final TotalMinecraftTicketsRepository totalMinecraftTicketsRepository;
  private final AverageMinecraftTicketsComparedRepository averageMinecraftTicketsComparedRepository;

  public DuplicatesRemoverServiceImpl(
      DailyDiscordMessagesRepository dailyDiscordMessagesRepository,
      DailyMinecraftTicketsRepository dailyMinecraftTicketsRepository,
      AverageDailyMinecraftTicketsRepository averageDailyMinecraftTicketsRepository,
      AverageMinecraftTicketsPerPlaytimeRepository averageMinecraftTicketsPerPlaytimeRepository,
      TotalMinecraftTicketsRepository totalMinecraftTicketsRepository,
      AverageMinecraftTicketsComparedRepository averageMinecraftTicketsComparedRepository) {
    this.dailyDiscordMessagesRepository = dailyDiscordMessagesRepository;
    this.dailyMinecraftTicketsRepository = dailyMinecraftTicketsRepository;
    this.averageDailyMinecraftTicketsRepository = averageDailyMinecraftTicketsRepository;
    this.averageMinecraftTicketsPerPlaytimeRepository = averageMinecraftTicketsPerPlaytimeRepository;
    this.totalMinecraftTicketsRepository = totalMinecraftTicketsRepository;
    this.averageMinecraftTicketsComparedRepository = averageMinecraftTicketsComparedRepository;
  }

  // ------------------------------------------------------------
  // DAILY DISCORD MESSAGES
  // ------------------------------------------------------------
  @Override
  @Transactional
  public void removeDailyDiscordMessagesDuplicates() {
    processDuplicates("DailyDiscordMessages",
        dailyDiscordMessagesRepository.findAll(),
        msg -> msg.getEmployeeId() + "-" + msg.getDate(),
        dailyDiscordMessagesRepository::deleteAllInBatch);
  }

  // ------------------------------------------------------------
  // DAILY MINECRAFT TICKETS
  // ------------------------------------------------------------
  @Override
  @Transactional
  public void removeDuplicatesFromDailyMcTickets() {
    processDuplicates("DailyMinecraftTickets",
        dailyMinecraftTicketsRepository.findAll(),
        msg -> msg.getEmployeeId() + "-" + msg.getDate(),
        dailyMinecraftTicketsRepository::deleteAllInBatch);
  }

  // ------------------------------------------------------------
  // AVERAGE DAILY MINECRAFT TICKETS
  // ------------------------------------------------------------
  @Override
  @Transactional
  public void removeDuplicatesFromAvgDailyMcTickets() {
    processDuplicates("AverageDailyMinecraftTickets",
        averageDailyMinecraftTicketsRepository.findAll(),
        msg -> msg.getEmployeeId() + "-" + msg.getTickets(),
        averageDailyMinecraftTicketsRepository::deleteAllInBatch);
  }

  // ------------------------------------------------------------
  // AVERAGE TICKETS PER PLAYTIME
  // ------------------------------------------------------------
  @Override
  @Transactional
  public void removeDuplicatesFromMcTicketsPerPlaytime() {
    processDuplicates("AverageMinecraftTicketsPerPlaytime",
        averageMinecraftTicketsPerPlaytimeRepository.findAll(),
        msg -> msg.getEmployeeId() + "-" + msg.getValue(),
        averageMinecraftTicketsPerPlaytimeRepository::deleteAllInBatch);
  }

  // ------------------------------------------------------------
  // TOTAL MINECRAFT TICKETS
  // ------------------------------------------------------------
  @Override
  @Transactional
  public void removeDuplicatesFromTotalMcTickets() {
    processDuplicates("TotalMinecraftTickets",
        totalMinecraftTicketsRepository.findAll(),
        msg -> msg.getEmployeeId() + "-" + msg.getTicketCount(),
        totalMinecraftTicketsRepository::deleteAllInBatch);
  }

  // ------------------------------------------------------------
  // COMPARED MINECRAFT TICKETS
  // ------------------------------------------------------------
  @Override
  @Transactional
  public void removeDuplicatesFromComparedMcTickets() {
    processDuplicates("AverageMinecraftTicketsCompared",
        averageMinecraftTicketsComparedRepository.findAll(),
        msg -> msg.getEmployeeId() + "-" + msg.getValue(),
        averageMinecraftTicketsComparedRepository::deleteAllInBatch);
  }

  // ------------------------------------------------------------
  // GENERIC WRAPPER
  // ------------------------------------------------------------
  private <T> void processDuplicates(
      String entityName,
      List<T> records,
      java.util.function.Function<T, String> groupingKey,
      java.util.function.Consumer<List<T>> batchDeleteFn) {

    long start = System.currentTimeMillis();
    log.info("🧹 [{}] Starting duplicate removal...", entityName);

    if (records.isEmpty()) {
      log.info("[{}] No records found — skipping.", entityName);
      return;
    }

    log.debug("[{}] Loaded {} records from database.", entityName, records.size());

    int duplicates = removeDuplicatesGeneric(records, groupingKey, batchDeleteFn, entityName);

    log.info("✅ [{}] Removed {} duplicates. Took {} ms.", entityName, duplicates, System.currentTimeMillis() - start);
  }

  // ------------------------------------------------------------
  // GENERIC DUPLICATE REMOVAL METHOD
  // ------------------------------------------------------------
  private <T> int removeDuplicatesGeneric(
      List<T> records,
      java.util.function.Function<T, String> groupingKey,
      java.util.function.Consumer<List<T>> batchDeleteFn,
      String entityName) {

    Map<String, List<T>> grouped = records.stream()
        .collect(Collectors.groupingBy(groupingKey));

    log.debug("[{}] Grouped into {} unique keys.", entityName, grouped.size());

    if (log.isTraceEnabled()) {
      grouped.forEach((key, list) -> log.trace("[{}] Key '{}' → {} record(s)", entityName, key, list.size()));
    }

    List<T> duplicates = grouped.values().stream()
        .flatMap(list -> list.stream()
            .sorted(Comparator.comparingInt(this::getEntityId))
            .skip(1))
        .toList();

    if (!duplicates.isEmpty()) {
      if (log.isDebugEnabled()) {
        log.debug("[{}] Found {} duplicates. Preparing to delete...", entityName, duplicates.size());
      }

      if (log.isTraceEnabled()) {
        for (T dup : duplicates) {
          log.trace("[{}] Duplicate entity: class={} id={}", entityName,
              dup.getClass().getSimpleName(), getEntityId(dup));
        }
      }

      batchDeleteFn.accept(duplicates);
      log.debug("[{}] Deleted {} duplicate record(s) successfully.", entityName, duplicates.size());
    } else {
      log.info("[{}] No duplicates detected.", entityName);
    }

    return duplicates.size();
  }

  private int getEntityId(Object entity) {
    try {
      Object value = entity.getClass().getMethod("getId").invoke(entity);
      return value instanceof Number ? ((Number) value).intValue() : Integer.MAX_VALUE;
    } catch (IllegalAccessException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
      log.warn("⚠️ Unable to extract ID from entity of type {}: {}", entity.getClass().getSimpleName(), e.getMessage());
      return Integer.MAX_VALUE;
    }
  }
}
