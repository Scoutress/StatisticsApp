package com.scoutress.KaimuxAdminStats.servicesimpl.productivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.discordTickets.DailyDiscordTickets;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.DailyMinecraftTickets;
import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.entity.productivity.DailyObjectiveProductivity;
import com.scoutress.KaimuxAdminStats.entity.productivity.DailyProductivity;
import com.scoutress.KaimuxAdminStats.entity.productivity.DailySubjectiveProductivity;
import com.scoutress.KaimuxAdminStats.repositories.discordTickets.DailyDiscordTicketsRepository;
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

  public ProductivityServiceImpl(
      DailyObjectiveProductivityRepository objectiveProductivityRepository,
      DailySubjectiveProductivityRepository subjectiveProductivityRepository,
      DailyProductivityRepository productivityRepository,
      DailyDiscordTicketsRepository dailyDiscordTicketsRepository,
      DailyMinecraftTicketsRepository dailyMinecraftTicketsRepository,
      DailyPlaytimeRepository dailyPlaytimeRepository) {
    this.dailyObjectiveProductivityRepository = objectiveProductivityRepository;
    this.dailySubjectiveProductivityRepository = subjectiveProductivityRepository;
    this.dailyProductivityRepository = productivityRepository;
    this.dailyDiscordTicketsRepository = dailyDiscordTicketsRepository;
    this.dailyMinecraftTicketsRepository = dailyMinecraftTicketsRepository;
    this.dailyPlaytimeRepository = dailyPlaytimeRepository;
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
    List<DailyPlaytime> dailyPlaytime = dailyPlaytimeRepository.findAll();
    // List<DailyAfkPlaytime> dailyAfkPlaytime =
    // dailyAfkPlaytimeRepository.findAll();
    List<DailyDiscordTickets> dailyDiscordTickets = dailyDiscordTicketsRepository.findAll();
    // List<DailyDiscordTicketsComp> dailyDiscordTicketsComp =
    // dailyDiscordTicketsCompRepository.findAll();
    // List<DailyDiscordMessages> dailyDiscordMessages =
    // dailyDiscordMessagesRepository.findAll();
    List<DailyMinecraftTickets> dailyMinecraftTickets = dailyMinecraftTicketsRepository.findAll();
    // List<DailyMinecraftTicketsComp> dailyMinecraftTicketsComp =
    // dailyMinecraftTicketsCompRepository.findAll();

    Map<Short, List<Double>> groupedValues = new HashMap<>();

    mergeValues(groupedValues, dailyPlaytime
        .stream()
        .collect(Collectors.groupingBy(
            playtime -> playtime.getAid(),
            Collectors.mapping(
                playtime -> playtime.getTime(),
                Collectors.toList()))));

    // mergeValues(groupedValues, dailyAfkPlaytime
    // .stream()
    // .collect(Collectors.groupingBy(DailyAfkPlaytime::getAid,
    // Collectors.mapping(
    // DailyAfkPlaytime::getValue,
    // Collectors.toList()))));

    mergeValues(groupedValues, dailyDiscordTickets
        .stream()
        .collect(Collectors.groupingBy(
            DailyDiscordTickets::getAid,
            Collectors.mapping(
                ticket -> (double) ticket.getTicketCount(),
                Collectors.toList()))));

    // mergeValues(groupedValues, dailyDiscordTicketsComp
    // .stream()
    // .collect(Collectors.groupingBy(DailyDiscordTicketsComp::getAid,
    // Collectors.mapping(
    // DailyDiscordTicketsComp::getValue,
    // Collectors.toList()))));

    // mergeValues(groupedValues, dailyDiscordMessages
    // .stream()
    // .collect(Collectors.groupingBy(DailyDiscordMessages::getAid,
    // Collectors.mapping(
    // DailyDiscordMessages::getValue,
    // Collectors.toList()))));

    mergeValues(groupedValues, dailyMinecraftTickets
        .stream()
        .collect(Collectors.groupingBy(
            ticket -> ticket.getAid(),
            Collectors.mapping(
                ticket -> (double) ticket.getTicketCount(),
                Collectors.toList()))));

    // mergeValues(groupedValues, dailyMinecraftTicketsComp
    // .stream()
    // .collect(Collectors.groupingBy(DailyMinecraftTicketsComp::getAid,
    // Collectors.mapping(
    // DailyMinecraftTicketsComp::getValue,
    // Collectors.toList()))));

    List<DailyObjectiveProductivity> dailyObjectiveProductivityResults = groupedValues.entrySet().stream()
        .map(entry -> {
          Short aid = entry.getKey();
          double averageValue = entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
          return new DailyObjectiveProductivity(null, aid, averageValue);
        })
        .collect(Collectors.toList());

    dailyObjectiveProductivityRepository.saveAll(dailyObjectiveProductivityResults);
  }

  private void mergeValues(Map<Short, List<Double>> mainMap, Map<Short, List<Double>> newMap) {
    newMap.forEach((key, valueList) -> mainMap.merge(key, valueList, (existing, newValues) -> {
      existing.addAll(newValues);
      return existing;
    }));
  }
}
