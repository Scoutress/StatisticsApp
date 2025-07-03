package com.scoutress.KaimuxAdminStats.servicesImpl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

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
  public void calculateLatestActivity() {
    List<Short> employeeIds = getAllEmployeeIds();

    for (Short employeeId : employeeIds) {
      LocalDate lastPlaytime = getLastPlaytime(employeeId);
      LocalDate lastMinecraftTicket = getLastMinecraftTicket(employeeId);
      LocalDate lastDiscordChat = getLastDiscordChat(employeeId);

      LocalDate lastDiscordTicketDate = getLastDiscordTicket(employeeId);

      saveLatestActivity(
          employeeId,
          lastPlaytime,
          lastDiscordTicketDate,
          lastMinecraftTicket,
          lastDiscordChat);
    }
  }

  private List<Short> getAllEmployeeIds() {
    return employeeRepository
        .findAll()
        .stream()
        .map(employee -> employee.getId())
        .toList();
  }

  private LocalDate getLastPlaytime(Short employeeId) {
    return dailyPlaytimeRepository
        .findAll()
        .stream()
        .filter(playtime -> playtime.getEmployeeId().equals(employeeId))
        .filter(playtime -> playtime.getTimeInHours() > 0)
        .map(playtime -> playtime.getDate())
        .max(LocalDate::compareTo)
        .orElse(null);
  }

  private LocalDate getLastMinecraftTicket(Short employeeId) {
    return dailyMinecraftTicketsRepository
        .findAll()
        .stream()
        .filter(ticket -> ticket.getEmployeeId().equals(employeeId))
        .map(ticket -> ticket.getDate())
        .max(LocalDate::compareTo)
        .orElse(null);
  }

  private LocalDate getLastDiscordChat(Short employeeId) {
    return dailyDiscordMessagesRepository
        .findAll()
        .stream()
        .filter(chat -> chat.getEmployeeId().equals(employeeId))
        .filter(chat -> chat.getMsgCount() > 0)
        .map(chat -> chat.getDate())
        .max(LocalDate::compareTo)
        .orElse(null);
  }

  private LocalDate getLastDiscordTicket(Short employeeId) {
    return dailyDiscordTicketsRepository
        .findAll()
        .stream()
        .filter(ticket -> ticket.getEmployeeId() != null && ticket.getEmployeeId().equals(employeeId))
        .map(ticket -> ticket.getDate())
        .max(LocalDate::compareTo)
        .orElse(null);
  }

  private void saveLatestActivity(
      Short employeeId,
      LocalDate lastPlaytime,
      LocalDate lastDiscordTicket,
      LocalDate lastMinecraftTicket,
      LocalDate lastDiscordChat) {

    Short daysSinceLastPlaytime = (lastPlaytime != null)
        ? (short) (LocalDate.now().toEpochDay() - lastPlaytime.toEpochDay())
        : (short) -1;

    Short daysSinceLastDiscordTicket = (lastDiscordTicket != null)
        ? (short) (LocalDate.now().toEpochDay() - lastDiscordTicket.toEpochDay())
        : (short) -1;

    Short daysSinceLastMinecraftTicket = (lastMinecraftTicket != null)
        ? (short) (LocalDate.now().toEpochDay() - lastMinecraftTicket.toEpochDay())
        : (short) -1;

    Short daysSinceLastDiscordChat = (lastDiscordChat != null)
        ? (short) (LocalDate.now().toEpochDay() - lastDiscordChat.toEpochDay())
        : (short) -1;

    LatestActivity existingActivity = latestActivityRepository.findByEmployeeId(employeeId);

    if (existingActivity != null) {
      existingActivity.setDaysSinceLastPlaytime(daysSinceLastPlaytime);
      existingActivity.setDaysSinceLastDiscordTicket(daysSinceLastDiscordTicket);
      existingActivity.setDaysSinceLastMinecraftTicket(daysSinceLastMinecraftTicket);
      existingActivity.setDaysSinceLastDiscordChat(daysSinceLastDiscordChat);
      latestActivityRepository.save(existingActivity);
    } else {
      LatestActivity newActivity = new LatestActivity();
      newActivity.setEmployeeId(employeeId);
      newActivity.setDaysSinceLastPlaytime(daysSinceLastPlaytime);
      newActivity.setDaysSinceLastDiscordTicket(daysSinceLastDiscordTicket);
      newActivity.setDaysSinceLastMinecraftTicket(daysSinceLastMinecraftTicket);
      newActivity.setDaysSinceLastDiscordChat(daysSinceLastDiscordChat);
      latestActivityRepository.save(newActivity);
    }
  }
}
