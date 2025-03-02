package com.scoutress.KaimuxAdminStats.servicesImpl;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

  @Override
  @Transactional
  public void removeDailyDiscordMessagesDuplicates() {
    List<DailyDiscordMessages> dailyMessages = dailyDiscordMessagesRepository.findAll();

    Map<String, List<DailyDiscordMessages>> groupedByEmployeeIdAndDate = dailyMessages
        .stream()
        .collect(Collectors.groupingBy(message -> message.getEmployeeId() + "-" + message.getDate()));

    groupedByEmployeeIdAndDate.forEach((key, messages) -> {
      messages.stream()
          .sorted(Comparator.comparing(DailyDiscordMessages::getId))
          .skip(1)
          .forEach(msg -> {
            dailyDiscordMessagesRepository.delete(msg);
          });
    });
  }

  @Override
  @Transactional
  public void removeDuplicatesFromDailyMcTickets() {
    List<DailyMinecraftTickets> dailyTickets = dailyMinecraftTicketsRepository.findAll();

    Map<String, List<DailyMinecraftTickets>> groupedByEmployeeIdAndDate = dailyTickets
        .stream()
        .collect(Collectors.groupingBy(message -> message.getEmployeeId() + "-" + message.getDate()));

    groupedByEmployeeIdAndDate.forEach((key, messages) -> {
      messages.stream()
          .sorted(Comparator.comparing(DailyMinecraftTickets::getId))
          .skip(1)
          .forEach(msg -> {
            dailyMinecraftTicketsRepository.delete(msg);
          });
    });
  }

  @Override
  @Transactional
  public void removeDuplicatesFromAvgDailyMcTickets() {
    List<AverageDailyMinecraftTickets> avgDailyTickets = averageDailyMinecraftTicketsRepository.findAll();

    Map<String, List<AverageDailyMinecraftTickets>> groupedByEmployeeIdAndTickets = avgDailyTickets
        .stream()
        .collect(Collectors.groupingBy(message -> message.getEmployeeId() + "-" + message.getTickets()));

    groupedByEmployeeIdAndTickets.forEach((key, messages) -> {
      messages.stream()
          .sorted(Comparator.comparing(AverageDailyMinecraftTickets::getId))
          .skip(1)
          .forEach(msg -> {
            averageDailyMinecraftTicketsRepository.delete(msg);
          });
    });
  }

  @Override
  @Transactional
  public void removeDuplicatesFromMcTicketsPerPlaytime() {
    List<AverageMinecraftTicketsPerPlaytime> ticketsPerPlaytime = averageMinecraftTicketsPerPlaytimeRepository
        .findAll();

    Map<String, List<AverageMinecraftTicketsPerPlaytime>> groupedByEmployeeIdAndValue = ticketsPerPlaytime
        .stream()
        .collect(Collectors.groupingBy(message -> message.getEmployeeId() + "-" + message.getValue()));

    groupedByEmployeeIdAndValue.forEach((key, messages) -> {
      messages.stream()
          .sorted(Comparator.comparing(AverageMinecraftTicketsPerPlaytime::getId))
          .skip(1)
          .forEach(msg -> {
            averageMinecraftTicketsPerPlaytimeRepository.delete(msg);
          });
    });
  }

  @Override
  @Transactional
  public void removeDuplicatesFromTotalMcTickets() {
    List<TotalMinecraftTickets> ticketsPerPlaytime = totalMinecraftTicketsRepository.findAll();

    Map<String, List<TotalMinecraftTickets>> groupedByEmployeeIdAndValue = ticketsPerPlaytime
        .stream()
        .collect(Collectors.groupingBy(message -> message.getEmployeeId() + "-" + message.getTicketCount()));

    groupedByEmployeeIdAndValue.forEach((key, messages) -> {
      messages.stream()
          .sorted(Comparator.comparing(TotalMinecraftTickets::getId))
          .skip(1)
          .forEach(msg -> {
            totalMinecraftTicketsRepository.delete(msg);
          });
    });
  }

  @Override
  @Transactional
  public void removeDuplicatesFromComparedMcTickets() {
    List<AverageMinecraftTicketsCompared> ticketsPerPlaytime = averageMinecraftTicketsComparedRepository.findAll();

    Map<String, List<AverageMinecraftTicketsCompared>> groupedByEmployeeIdAndValue = ticketsPerPlaytime
        .stream()
        .collect(Collectors.groupingBy(message -> message.getEmployeeId() + "-" + message.getValue()));

    groupedByEmployeeIdAndValue.forEach((key, messages) -> {
      messages.stream()
          .sorted(Comparator.comparing(AverageMinecraftTicketsCompared::getId))
          .skip(1)
          .forEach(msg -> {
            averageMinecraftTicketsComparedRepository.delete(msg);
          });
    });
  }
}
