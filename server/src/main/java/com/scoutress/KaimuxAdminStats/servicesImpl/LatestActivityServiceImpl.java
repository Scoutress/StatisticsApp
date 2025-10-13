package com.scoutress.KaimuxAdminStats.servicesImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scoutress.KaimuxAdminStats.entity.LatestActivity;
import com.scoutress.KaimuxAdminStats.repositories.LatestActivityRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordMessages.DailyDiscordMessagesRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordTickets.DailyDiscordTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.DailyMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.DailyPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.services.LatestActivityService;

@Service
public class LatestActivityServiceImpl implements LatestActivityService {

  private static final Logger log = LoggerFactory.getLogger(LatestActivityServiceImpl.class);

  private final EmployeeRepository employeeRepository;
  private final DailyPlaytimeRepository dailyPlaytimeRepository;
  private final DailyDiscordMessagesRepository dailyDiscordMessagesRepository;
  private final DailyMinecraftTicketsRepository dailyMinecraftTicketsRepository;
  private final DailyDiscordTicketsRepository dailyDiscordTicketsRepository;
  private final LatestActivityRepository latestActivityRepository;

  public LatestActivityServiceImpl(
      EmployeeRepository employeeRepository,
      DailyPlaytimeRepository dailyPlaytimeRepository,
      DailyDiscordMessagesRepository dailyDiscordMessagesRepository,
      DailyMinecraftTicketsRepository dailyMinecraftTicketsRepository,
      DailyDiscordTicketsRepository dailyDiscordTicketsRepository,
      LatestActivityRepository latestActivityRepository) {
    this.employeeRepository = employeeRepository;
    this.dailyPlaytimeRepository = dailyPlaytimeRepository;
    this.dailyDiscordMessagesRepository = dailyDiscordMessagesRepository;
    this.dailyMinecraftTicketsRepository = dailyMinecraftTicketsRepository;
    this.dailyDiscordTicketsRepository = dailyDiscordTicketsRepository;
    this.latestActivityRepository = latestActivityRepository;
  }

  @Override
  @Transactional
  public void calculateLatestActivity() {
    long startTime = System.currentTimeMillis();
    log.info("🕒 Starting LatestActivity calculation...");

    List<Short> employeeIds = employeeRepository
        .findAll()
        .stream()
        .map(e -> e.getId())
        .toList();

    if (employeeIds.isEmpty()) {
      log.warn("No employees found. Skipping LatestActivity calculation.");
      return;
    }

    Map<Short, LocalDate> lastPlaytime = dailyPlaytimeRepository
        .findAll()
        .stream()
        .filter(p -> p.getTimeInHours() > 0)
        .collect(Collectors.toMap(
            p -> p.getEmployeeId(),
            p -> p.getDate(),
            (a, b) -> a.isAfter(b) ? a : b));

    Map<Short, LocalDate> lastMcTickets = dailyMinecraftTicketsRepository
        .findAll()
        .stream()
        .collect(Collectors.toMap(
            t -> t.getEmployeeId(),
            t -> t.getDate(),
            (a, b) -> a.isAfter(b) ? a : b));

    Map<Short, LocalDate> lastDcChats = dailyDiscordMessagesRepository
        .findAll()
        .stream()
        .filter(m -> m.getMsgCount() > 0)
        .collect(Collectors.toMap(
            m -> m.getEmployeeId(),
            m -> m.getDate(),
            (a, b) -> a.isAfter(b) ? a : b));

    Map<Short, LocalDate> lastDcTickets = dailyDiscordTicketsRepository
        .findAll()
        .stream()
        .filter(t -> t.getEmployeeId() != null)
        .collect(Collectors.toMap(
            t -> t.getEmployeeId(),
            t -> t.getDate(),
            (a, b) -> a.isAfter(b) ? a : b));

    List<LatestActivity> activities = new ArrayList<>();

    for (Short id : employeeIds) {
      LatestActivity a = latestActivityRepository.findByEmployeeId(id);
      if (a == null)
        a = new LatestActivity();

      a.setEmployeeId(id);
      a.setDaysSinceLastPlaytime(daysSince(lastPlaytime.get(id)));
      a.setDaysSinceLastMinecraftTicket(daysSince(lastMcTickets.get(id)));
      a.setDaysSinceLastDiscordChat(daysSince(lastDcChats.get(id)));
      a.setDaysSinceLastDiscordTicket(daysSince(lastDcTickets.get(id)));

      activities.add(a);
    }

    latestActivityRepository.saveAll(activities);

    long duration = System.currentTimeMillis() - startTime;
    log.info("✅ LatestActivity updated for {} employees in {} ms.", activities.size(), duration);
  }

  private Short daysSince(LocalDate date) {
    return (date != null)
        ? (short) (LocalDate.now().toEpochDay() - date.toEpochDay())
        : (short) -1;
  }
}
