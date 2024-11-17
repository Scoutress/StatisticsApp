package com.scoutress.KaimuxAdminStats.servicesImpl.minecraftTickets;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.DailyMinecraftTickets;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.DailyMinecraftTicketsCompared;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.DailyMinecraftTicketsComparedRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.DailyMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.services.minecraftTickets.MinecraftTicketsComparedService;

@Service
public class MinecraftTicketsComparedServiceImpl implements MinecraftTicketsComparedService {

  private final DailyMinecraftTicketsRepository dailyMinecraftTicketsRepository;
  private final DailyMinecraftTicketsComparedRepository dailyMinecraftTicketsComparedRepository;

  public MinecraftTicketsComparedServiceImpl(
      DailyMinecraftTicketsRepository dailyMinecraftTicketsRepository,
      DailyMinecraftTicketsComparedRepository dailyMinecraftTicketsComparedRepository) {
    this.dailyMinecraftTicketsRepository = dailyMinecraftTicketsRepository;
    this.dailyMinecraftTicketsComparedRepository = dailyMinecraftTicketsComparedRepository;
  }

  @Override
  public void compareEachEmployeeDailyMcTicketsValues() {
    List<DailyMinecraftTickets> dailyTickets = dailyMinecraftTicketsRepository.findAll();

    Map<String, List<DailyMinecraftTickets>> groupedTickets = groupTicketsByAidAndDate(dailyTickets);

    for (Map.Entry<String, List<DailyMinecraftTickets>> entry : groupedTickets.entrySet()) {
      Short aid = extractAid(entry.getKey());
      LocalDate date = extractDate(entry.getKey());
      int totalTickets = calculateTotalTickets(entry.getValue());
      saveComparedData(entry.getValue(), aid, date, totalTickets);
    }
  }

  private Map<String, List<DailyMinecraftTickets>> groupTicketsByAidAndDate(List<DailyMinecraftTickets> dailyTickets) {
    return dailyTickets
        .stream()
        .collect(Collectors.groupingBy(
            ticket -> ticket.getAid() + "_" + ticket.getDate()));
  }

  private Short extractAid(String key) {
    String[] keyParts = key.split("_");
    return Short.valueOf(keyParts[0]);
  }

  private LocalDate extractDate(String key) {
    String[] keyParts = key.split("_");
    return LocalDate.parse(keyParts[1]);
  }

  private int calculateTotalTickets(List<DailyMinecraftTickets> tickets) {
    return tickets
        .stream()
        .mapToInt(DailyMinecraftTickets::getTicketCount)
        .sum();
  }

  private void saveComparedData(List<DailyMinecraftTickets> tickets, Short aid, LocalDate date, int totalTickets) {
    for (DailyMinecraftTickets ticket : tickets) {
      double value = calculateTicketValue(ticket.getTicketCount(), totalTickets);
      DailyMinecraftTicketsCompared comparedData = new DailyMinecraftTicketsCompared(
          null,
          aid,
          value,
          date);
      dailyMinecraftTicketsComparedRepository.save(comparedData);
    }
  }

  private double calculateTicketValue(int ticketCount, int totalTickets) {
    return (double) ticketCount / totalTickets;
  }
}
