package com.scoutress.KaimuxAdminStats.servicesImpl;

import java.lang.reflect.InvocationTargetException;
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
    log.info("🕒 [START] Calculating latest employee activity...");

    List<Short> employeeIds = employeeRepository.findAll()
        .stream()
        .map(e -> e.getId())
        .toList();

    if (employeeIds.isEmpty()) {
      log.warn("⚠ No employees found. Skipping LatestActivity calculation.");
      return;
    }

    log.info("Found {} employees for LatestActivity update.", employeeIds.size());

    // === PLAYTIME ===
    List<?> allPlaytimes = dailyPlaytimeRepository.findAll();
    Map<Short, LocalDate> lastPlaytime = allPlaytimes.stream()
        .filter(p -> {
          try {
            var time = (Double) p.getClass().getMethod("getTimeInHours").invoke(p);
            return time != null && time > 0;
          } catch (IllegalAccessException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            return false;
          }
        })
        .collect(Collectors.toMap(
            p -> {
              try {
                return (Short) p.getClass().getMethod("getEmployeeId").invoke(p);
              } catch (IllegalAccessException | NoSuchMethodException | SecurityException
                  | InvocationTargetException e) {
                return (short) -1;
              }
            },
            p -> {
              try {
                return (LocalDate) p.getClass().getMethod("getDate").invoke(p);
              } catch (IllegalAccessException | NoSuchMethodException | SecurityException
                  | InvocationTargetException e) {
                return null;
              }
            },
            (a, b) -> a.isAfter(b) ? a : b));

    log.debug("📊 Loaded {} playtime entries, {} valid for processing.", allPlaytimes.size(), lastPlaytime.size());

    // === MINECRAFT TICKETS ===
    List<?> allMcTickets = dailyMinecraftTicketsRepository.findAll();
    Map<Short, LocalDate> lastMcTickets = allMcTickets.stream()
        .collect(Collectors.toMap(
            t -> {
              try {
                return (Short) t.getClass().getMethod("getEmployeeId").invoke(t);
              } catch (IllegalAccessException | NoSuchMethodException | SecurityException
                  | InvocationTargetException e) {
                return (short) -1;
              }
            },
            t -> {
              try {
                return (LocalDate) t.getClass().getMethod("getDate").invoke(t);
              } catch (IllegalAccessException | NoSuchMethodException | SecurityException
                  | InvocationTargetException e) {
                return null;
              }
            },
            (a, b) -> a.isAfter(b) ? a : b));

    log.debug("📊 Loaded {} Minecraft ticket entries, {} valid for processing.", allMcTickets.size(),
        lastMcTickets.size());

    // === DISCORD MESSAGES ===
    List<?> allDcMessages = dailyDiscordMessagesRepository.findAll();
    Map<Short, LocalDate> lastDcChats = allDcMessages.stream()
        .filter(m -> {
          try {
            var msgCount = (Integer) m.getClass().getMethod("getMsgCount").invoke(m);
            return msgCount != null && msgCount > 0;
          } catch (IllegalAccessException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            return false;
          }
        })
        .collect(Collectors.toMap(
            m -> {
              try {
                return (Short) m.getClass().getMethod("getEmployeeId").invoke(m);
              } catch (IllegalAccessException | NoSuchMethodException | SecurityException
                  | InvocationTargetException e) {
                return (short) -1;
              }
            },
            m -> {
              try {
                return (LocalDate) m.getClass().getMethod("getDate").invoke(m);
              } catch (IllegalAccessException | NoSuchMethodException | SecurityException
                  | InvocationTargetException e) {
                return null;
              }
            },
            (a, b) -> a.isAfter(b) ? a : b));

    log.debug("📊 Loaded {} Discord message entries, {} valid for processing.", allDcMessages.size(),
        lastDcChats.size());

    // === DISCORD TICKETS ===
    List<?> allDcTickets = dailyDiscordTicketsRepository.findAll();
    Map<Short, LocalDate> lastDcTickets = allDcTickets.stream()
        .filter(t -> {
          try {
            return t.getClass().getMethod("getEmployeeId").invoke(t) != null;
          } catch (IllegalAccessException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            return false;
          }
        })
        .collect(Collectors.toMap(
            t -> {
              try {
                return (Short) t.getClass().getMethod("getEmployeeId").invoke(t);
              } catch (IllegalAccessException | NoSuchMethodException | SecurityException
                  | InvocationTargetException e) {
                return (short) -1;
              }
            },
            t -> {
              try {
                return (LocalDate) t.getClass().getMethod("getDate").invoke(t);
              } catch (IllegalAccessException | NoSuchMethodException | SecurityException
                  | InvocationTargetException e) {
                return null;
              }
            },
            (a, b) -> a.isAfter(b) ? a : b));

    log.debug("📊 Loaded {} Discord ticket entries, {} valid for processing.", allDcTickets.size(),
        lastDcTickets.size());

    // === BUILD ACTIVITY RESULTS ===
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

      if (log.isTraceEnabled()) {
        log.trace("🧾 Employee {} → playtime={}d, mcTickets={}d, dcChat={}d, dcTickets={}d",
            id,
            a.getDaysSinceLastPlaytime(),
            a.getDaysSinceLastMinecraftTicket(),
            a.getDaysSinceLastDiscordChat(),
            a.getDaysSinceLastDiscordTicket());
      }
    }

    latestActivityRepository.saveAll(activities);

    long duration = System.currentTimeMillis() - startTime;
    log.info("✅ [DONE] LatestActivity updated for {} employees in {} ms.", activities.size(), duration);
  }

  private short daysSince(LocalDate date) {
    if (date == null)
      return (short) -1;
    long diff = LocalDate.now().toEpochDay() - date.toEpochDay();
    return (short) Math.max(0, diff);
  }
}
