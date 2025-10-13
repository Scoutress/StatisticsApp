package com.scoutress.KaimuxAdminStats.servicesImpl;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.discordMessages.DailyDiscordMessages;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.AverageDailyMinecraftTickets;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.AverageMinecraftTicketsCompared;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.AverageMinecraftTicketsPerPlaytime;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.DailyMinecraftTickets;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.TotalMinecraftTickets;
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
    log.info("🧹 Removing duplicates from DailyDiscordMessages...");
    List<DailyDiscordMessages> records = dailyDiscordMessagesRepository
        .findAll();
    if (records.isEmpty()) {
      log.info("No DailyDiscordMessages found, skipping.");
      return;
    }

    int duplicates = removeDuplicatesGeneric(
        records,
        msg -> msg.getEmployeeId() + "-" + msg.getDate(),
        dailyDiscordMessagesRepository::deleteAllInBatch);

    log.info("✅ Removed {} duplicates from DailyDiscordMessages.", duplicates);
  }

  // ------------------------------------------------------------
  // DAILY MINECRAFT TICKETS
  // ------------------------------------------------------------
  @Override
  @Transactional
  public void removeDuplicatesFromDailyMcTickets() {
    log.info("🧹 Removing duplicates from DailyMinecraftTickets...");
    List<DailyMinecraftTickets> records = dailyMinecraftTicketsRepository
        .findAll();
    if (records.isEmpty()) {
      log.info("No DailyMinecraftTickets found, skipping.");
      return;
    }

    int duplicates = removeDuplicatesGeneric(
        records,
        msg -> msg.getEmployeeId() + "-" + msg.getDate(),
        dailyMinecraftTicketsRepository::deleteAllInBatch);

    log.info("✅ Removed {} duplicates from DailyMinecraftTickets.", duplicates);
  }

  // ------------------------------------------------------------
  // AVERAGE DAILY MINECRAFT TICKETS
  // ------------------------------------------------------------
  @Override
  @Transactional
  public void removeDuplicatesFromAvgDailyMcTickets() {
    log.info("🧹 Removing duplicates from AverageDailyMinecraftTickets...");
    List<AverageDailyMinecraftTickets> records = averageDailyMinecraftTicketsRepository
        .findAll();
    if (records.isEmpty()) {
      log.info("No AverageDailyMinecraftTickets found, skipping.");
      return;
    }

    int duplicates = removeDuplicatesGeneric(
        records,
        msg -> msg.getEmployeeId() + "-" + msg.getTickets(),
        averageDailyMinecraftTicketsRepository::deleteAllInBatch);

    log.info("✅ Removed {} duplicates from AverageDailyMinecraftTickets.", duplicates);
  }

  // ------------------------------------------------------------
  // AVERAGE TICKETS PER PLAYTIME
  // ------------------------------------------------------------
  @Override
  @Transactional
  public void removeDuplicatesFromMcTicketsPerPlaytime() {
    log.info("🧹 Removing duplicates from AverageMinecraftTicketsPerPlaytime...");
    List<AverageMinecraftTicketsPerPlaytime> records = averageMinecraftTicketsPerPlaytimeRepository
        .findAll();
    if (records.isEmpty()) {
      log.info("No AverageMinecraftTicketsPerPlaytime found, skipping.");
      return;
    }

    int duplicates = removeDuplicatesGeneric(
        records,
        msg -> msg.getEmployeeId() + "-" + msg.getValue(),
        averageMinecraftTicketsPerPlaytimeRepository::deleteAllInBatch);

    log.info("✅ Removed {} duplicates from AverageMinecraftTicketsPerPlaytime.", duplicates);
  }

  // ------------------------------------------------------------
  // TOTAL MINECRAFT TICKETS
  // ------------------------------------------------------------
  @Override
  @Transactional
  public void removeDuplicatesFromTotalMcTickets() {
    log.info("🧹 Removing duplicates from TotalMinecraftTickets...");
    List<TotalMinecraftTickets> records = totalMinecraftTicketsRepository
        .findAll();
    if (records.isEmpty()) {
      log.info("No TotalMinecraftTickets found, skipping.");
      return;
    }

    int duplicates = removeDuplicatesGeneric(
        records,
        msg -> msg.getEmployeeId() + "-" + msg.getTicketCount(),
        totalMinecraftTicketsRepository::deleteAllInBatch);

    log.info("✅ Removed {} duplicates from TotalMinecraftTickets.", duplicates);
  }

  // ------------------------------------------------------------
  // COMPARED MINECRAFT TICKETS
  // ------------------------------------------------------------
  @Override
  @Transactional
  public void removeDuplicatesFromComparedMcTickets() {
    log.info("🧹 Removing duplicates from AverageMinecraftTicketsCompared...");
    List<AverageMinecraftTicketsCompared> records = averageMinecraftTicketsComparedRepository
        .findAll();
    if (records.isEmpty()) {
      log.info("No AverageMinecraftTicketsCompared found, skipping.");
      return;
    }

    int duplicates = removeDuplicatesGeneric(
        records,
        msg -> msg.getEmployeeId() + "-" + msg.getValue(),
        averageMinecraftTicketsComparedRepository::deleteAllInBatch);

    log.info("✅ Removed {} duplicates from AverageMinecraftTicketsCompared.", duplicates);
  }

  // ------------------------------------------------------------
  // GENERIC DUPLICATE REMOVAL METHOD
  // ------------------------------------------------------------
  private <T> int removeDuplicatesGeneric(
      List<T> records,
      java.util.function.Function<T, String> groupingKey,
      java.util.function.Consumer<List<T>> batchDeleteFn) {

    Map<String, List<T>> grouped = records.stream()
        .collect(Collectors.groupingBy(groupingKey));

    List<T> duplicates = grouped.values().stream()
        .flatMap(list -> list.stream()
            .sorted(Comparator.comparingInt(obj -> getEntityId(obj)))
            .skip(1))
        .toList();

    if (!duplicates.isEmpty()) {
      batchDeleteFn.accept(duplicates);
    }

    return duplicates.size();
  }

  private int getEntityId(Object entity) {
    try {
      return (int) entity.getClass().getMethod("getId").invoke(entity);
    } catch (IllegalAccessException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
      return Integer.MAX_VALUE;
    }
  }
}
