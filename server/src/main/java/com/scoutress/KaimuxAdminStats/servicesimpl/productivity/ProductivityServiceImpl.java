package com.scoutress.KaimuxAdminStats.servicesimpl.productivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.productivity.ObjectiveProductivity;
import com.scoutress.KaimuxAdminStats.entity.productivity.Productivity;
import com.scoutress.KaimuxAdminStats.entity.productivity.SubjectiveProductivity;
import com.scoutress.KaimuxAdminStats.repositories.productivity.ObjectiveProductivityRepository;
import com.scoutress.KaimuxAdminStats.repositories.productivity.ProductivityRepository;
import com.scoutress.KaimuxAdminStats.repositories.productivity.SubjectiveProductivityRepository;
import com.scoutress.KaimuxAdminStats.services.productivity.ProductivityService;

@Service
public class ProductivityServiceImpl implements ProductivityService {

  private final ObjectiveProductivityRepository objectiveProductivityRepository;
  private final SubjectiveProductivityRepository subjectiveProductivityRepository;
  private final ProductivityRepository productivityRepository;

  public ProductivityServiceImpl(
      ObjectiveProductivityRepository objectiveProductivityRepository,
      SubjectiveProductivityRepository subjectiveProductivityRepository,
      ProductivityRepository productivityRepository) {
    this.objectiveProductivityRepository = objectiveProductivityRepository;
    this.subjectiveProductivityRepository = subjectiveProductivityRepository;
    this.productivityRepository = productivityRepository;
  }

  @Override
  public void calculateDailyProductivity() {
    List<ObjectiveProductivity> objProd = objectiveProductivityRepository.findAll();
    List<SubjectiveProductivity> subjProd = subjectiveProductivityRepository.findAll();

    Map<Short, List<Double>> objectiveValues = objProd.stream()
        .collect(Collectors.groupingBy(
            ObjectiveProductivity::getAid,
            Collectors.mapping(ObjectiveProductivity::getValue, Collectors.toList())));

    Map<Short, List<Double>> subjectiveValues = subjProd.stream()
        .collect(Collectors.groupingBy(
            SubjectiveProductivity::getAid,
            Collectors.mapping(SubjectiveProductivity::getValue, Collectors.toList())));

    Set<Short> allAids = new HashSet<>();
    allAids.addAll(objectiveValues.keySet());
    allAids.addAll(subjectiveValues.keySet());

    List<Productivity> productivityResults = new ArrayList<>();

    for (Short aid : allAids) {
      List<Double> objValues = objectiveValues.getOrDefault(aid, Collections.emptyList());
      List<Double> subjValues = subjectiveValues.getOrDefault(aid, Collections.emptyList());

      double objAvg = objValues.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
      double subjAvg = subjValues.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

      double finalValue = (objAvg + subjAvg) / 2;

      Productivity productivity = new Productivity();
      productivity.setAid(aid);
      productivity.setValue(finalValue);
      productivityResults.add(productivity);
    }

    productivityRepository.saveAll(productivityResults);
  }

  @Override
  public void calculateDailyObjectiveProductivity() {
    // List<DailyPlaytime> dailyPlaytime = dailyPlaytimeRepository.findAll();
    // List<DailyAfkPlaytime> dailyAfkPlaytime =
    // dailyAfkPlaytimeRepository.findAll();
    // List<DailyDiscordTickets> dailyDiscordTickets =
    // dailyDiscordTicketsRepository.findAll();
    // List<DailyDiscordTicketsComp> dailyDiscordTicketsComp =
    // dailyDiscordTicketsCompRepository.findAll();
    // List<DailyDiscordMessages> dailyDiscordMessages =
    // dailyDiscordMessagesRepository.findAll();
    // List<DailyMinecraftTickets> dailyMinecraftTickets =
    // dailyMinecraftTicketsRepository.findAll();
    // List<DailyMinecraftTicketsComp> dailyMinecraftTicketsComp =
    // dailyMinecraftTicketsCompRepository.findAll();

    // Map<Short, List<Double>> groupedValues = new HashMap<>();

    // mergeValues(groupedValues, dailyPlaytime
    // .stream()
    // .collect(Collectors.groupingBy(DailyPlaytime::getAid,
    // Collectors.mapping(
    // DailyPlaytime::getValue,
    // Collectors.toList()))));

    // mergeValues(groupedValues, dailyAfkPlaytime
    // .stream()
    // .collect(Collectors.groupingBy(DailyAfkPlaytime::getAid,
    // Collectors.mapping(
    // DailyAfkPlaytime::getValue,
    // Collectors.toList()))));

    // mergeValues(groupedValues, dailyDiscordTickets
    // .stream()
    // .collect(Collectors.groupingBy(DailyDiscordTickets::getAid,
    // Collectors.mapping(
    // DailyDiscordTickets::getValue,
    // Collectors.toList()))));

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

    // mergeValues(groupedValues, dailyMinecraftTickets
    // .stream()
    // .collect(Collectors.groupingBy(DailyMinecraftTickets::getAid,
    // Collectors.mapping(
    // DailyMinecraftTickets::getValue,
    // Collectors.toList()))));

    // mergeValues(groupedValues, dailyMinecraftTicketsComp
    // .stream()
    // .collect(Collectors.groupingBy(DailyMinecraftTicketsComp::getAid,
    // Collectors.mapping(
    // DailyMinecraftTicketsComp::getValue,
    // Collectors.toList()))));

    // List<ObjectiveProductivity> dailyObjectiveProductivityResults =
    // groupedValues.entrySet().stream()
    // .map(entry -> {
    // Short aid = entry.getKey();
    // double averageValue =
    // entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    // return new ObjectiveProductivity(null, aid, averageValue);
    // })
    // .collect(Collectors.toList());

    // objectiveProductivityRepository.saveAll(dailyObjectiveProductivityResults);
  }

  private void mergeValues(Map<Short, List<Double>> mainMap, Map<Short, List<Double>> newMap) {
    newMap.forEach((key, valueList) -> mainMap.merge(key, valueList, (existing, newValues) -> {
      existing.addAll(newValues);
      return existing;
    }));
  }
}
