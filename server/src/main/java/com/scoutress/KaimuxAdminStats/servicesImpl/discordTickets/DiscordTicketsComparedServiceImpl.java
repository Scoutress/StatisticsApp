package com.scoutress.KaimuxAdminStats.servicesImpl.discordTickets;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.discordTickets.DailyDiscordTickets;
import com.scoutress.KaimuxAdminStats.entity.discordTickets.DailyDiscordTicketsCompared;
import com.scoutress.KaimuxAdminStats.repositories.discordTickets.DailyDiscordTicketsComparedRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordTickets.DailyDiscordTicketsRepository;
import com.scoutress.KaimuxAdminStats.services.discordTickets.DiscordTicketsComparedService;

@Service
public class DiscordTicketsComparedServiceImpl implements DiscordTicketsComparedService {

  private final DailyDiscordTicketsRepository dailyDiscordTicketsRepository;
  private final DailyDiscordTicketsComparedRepository dailyDiscordTicketsComparedRepository;

  public DiscordTicketsComparedServiceImpl(
      DailyDiscordTicketsRepository dailyDiscordTicketsRepository,
      DailyDiscordTicketsComparedRepository dailyDiscordTicketsComparedRepository) {
    this.dailyDiscordTicketsRepository = dailyDiscordTicketsRepository;
    this.dailyDiscordTicketsComparedRepository = dailyDiscordTicketsComparedRepository;
  }

  @Override
  public void compareEachEmployeeDailyDcTicketsValues() {
    List<DailyDiscordTickets> dailyTickets = dailyDiscordTicketsRepository.findAll();

    Map<String, List<DailyDiscordTickets>> groupedTickets = groupTicketsByAidAndDate(dailyTickets);

    for (Map.Entry<String, List<DailyDiscordTickets>> entry : groupedTickets.entrySet()) {
      Short aid = extractAid(entry.getKey());
      LocalDate date = extractDate(entry.getKey());
      int totalTickets = calculateTotalTickets(entry.getValue());
      saveComparedData(entry.getValue(), aid, date, totalTickets);
    }
  }

  private Map<String, List<DailyDiscordTickets>> groupTicketsByAidAndDate(List<DailyDiscordTickets> dailyTickets) {
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

  private int calculateTotalTickets(List<DailyDiscordTickets> tickets) {
    return tickets
        .stream()
        .mapToInt(DailyDiscordTickets::getTicketCount)
        .sum();
  }

  private void saveComparedData(List<DailyDiscordTickets> tickets, Short aid, LocalDate date, int totalTickets) {
    for (DailyDiscordTickets ticket : tickets) {
      double value = calculateTicketValue(ticket.getTicketCount(), totalTickets);
      DailyDiscordTicketsCompared comparedData = new DailyDiscordTicketsCompared(
          null,
          aid,
          value,
          date);
      dailyDiscordTicketsComparedRepository.save(comparedData);
    }
  }

  private double calculateTicketValue(int ticketCount, int totalTickets) {
    return (double) ticketCount / totalTickets;
  }
}
