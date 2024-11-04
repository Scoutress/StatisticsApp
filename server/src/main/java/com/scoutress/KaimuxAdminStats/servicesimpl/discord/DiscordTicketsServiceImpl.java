package com.scoutress.KaimuxAdminStats.servicesimpl.discord;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.discord.DiscordTickets;
import com.scoutress.KaimuxAdminStats.entity.discord.DiscordTicketsReactions;
import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;
import com.scoutress.KaimuxAdminStats.repositories.discord.DiscordTicketsRepository;
import com.scoutress.KaimuxAdminStats.services.DataExtractingService;
import com.scoutress.KaimuxAdminStats.services.discord.DiscordTicketsService;

@Service
public class DiscordTicketsServiceImpl implements DiscordTicketsService {

  public final DataExtractingService dataExtractingService;
  public final DiscordTicketsRepository discordTicketsRepository;

  public DiscordTicketsServiceImpl(
      DataExtractingService dataExtractingService,
      DiscordTicketsRepository discordTicketsRepository) {
    this.dataExtractingService = dataExtractingService;
    this.discordTicketsRepository = discordTicketsRepository;
  }

  @Override
  public void convertDiscordTicketsResponses() {

    List<EmployeeCodes> employeeCodes = extractEmployeeCodes();
    List<DiscordTicketsReactions> ticketsReactions = extractDataFromResponsesTable();
    List<DiscordTicketsReactions> reactionsWithEmployeeIds = mapDiscordToEmployeeIds(ticketsReactions, employeeCodes);
    List<DiscordTickets> convertedData = convertData(reactionsWithEmployeeIds);

    saveDataToNewTable(convertedData);
  }

  public List<EmployeeCodes> extractEmployeeCodes() {
    List<EmployeeCodes> data = dataExtractingService.getAllEmployeeCodes();
    return data;
  }

  public List<DiscordTicketsReactions> extractDataFromResponsesTable() {
    List<DiscordTicketsReactions> data = dataExtractingService.getAllDcTicketReactions();
    return data;
  }

  public List<DiscordTickets> convertData(List<DiscordTicketsReactions> rawData) {
    Map<Long, Map<LocalDate, Long>> groupedData = rawData
        .stream()
        .collect(Collectors
            .groupingBy(
                DiscordTicketsReactions::getDiscordId,
                Collectors
                    .groupingBy(
                        reaction -> reaction
                            .getDateTime()
                            .toLocalDate(),
                        Collectors
                            .mapping(
                                DiscordTicketsReactions::getTicketId,
                                Collectors
                                    .toSet()))))
        .entrySet()
        .stream()
        .collect(Collectors
            .toMap(
                Map.Entry::getKey,
                entry -> entry
                    .getValue()
                    .entrySet()
                    .stream()
                    .collect(Collectors
                        .toMap(
                            Map.Entry::getKey,
                            e -> (long) e
                                .getValue()
                                .size()))));

    return groupedData
        .entrySet()
        .stream()
        .flatMap(
            adminEntry -> adminEntry
                .getValue()
                .entrySet()
                .stream()
                .map(
                    dateEntry -> new DiscordTickets(
                        null,
                        adminEntry
                            .getKey(),
                        String
                            .valueOf(dateEntry
                                .getValue()),
                        dateEntry
                            .getKey())))
        .collect(Collectors
            .toList());
  }

  public List<DiscordTicketsReactions> mapDiscordToEmployeeIds(
      List<DiscordTicketsReactions> reactions,
      List<EmployeeCodes> employeeCodes) {

    Map<Short, Short> discordToEmployeeMap = employeeCodes
        .stream()
        .filter(code -> code.getDiscordId() != null)
        .filter(code -> code.getEmployeeId() != null)
        .collect(Collectors
            .toMap(
                EmployeeCodes::getDiscordId,
                EmployeeCodes::getEmployeeId));

    return reactions
        .stream()
        .map(reaction -> {
          Short employeeId = discordToEmployeeMap
              .get(reaction
                  .getDiscordId()
                  .shortValue());
          if (employeeId != null) {
            reaction
                .setDiscordId(employeeId
                    .longValue());
          }
          return reaction;
        })
        .collect(Collectors.toList());
  }

  private void saveDataToNewTable(List<DiscordTickets> convertedData) {
    convertedData.sort((a, b) -> a.getDate().compareTo(b.getDate()));
    convertedData.forEach(discordTicketsRepository::save);
  }
}
