package com.scoutress.KaimuxAdminStats.servicesimpl.discordTickets;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.discordTickets.DiscordTickets;
import com.scoutress.KaimuxAdminStats.entity.discordTickets.DiscordTicketsReactions;
import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;
import com.scoutress.KaimuxAdminStats.repositories.discordTickets.DiscordTicketsReactionsRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordTickets.DiscordTicketsRepository;
import com.scoutress.KaimuxAdminStats.services.DataExtractingService;
import com.scoutress.KaimuxAdminStats.services.discordTickets.DiscordTicketsService;

import jakarta.transaction.Transactional;

@Service
public class DiscordTicketsServiceImpl implements DiscordTicketsService {

  public final DataExtractingService dataExtractingService;
  public final DiscordTicketsRepository discordTicketsRepository;
  public final DiscordTicketsReactionsRepository discordTicketsReactionsRepository;

  public DiscordTicketsServiceImpl(
      DataExtractingService dataExtractingService,
      DiscordTicketsRepository discordTicketsRepository,
      DiscordTicketsReactionsRepository discordTicketsReactionsRepository) {
    this.dataExtractingService = dataExtractingService;
    this.discordTicketsRepository = discordTicketsRepository;
    this.discordTicketsReactionsRepository = discordTicketsReactionsRepository;
  }

  @Override
  public void convertDiscordTicketsResponses() {

    List<EmployeeCodes> employeeCodes = extractEmployeeCodes();
    List<DiscordTicketsReactions> ticketsReactions = extractDataFromResponsesTable();
    List<DiscordTicketsReactions> reactionsWithEmployeeIds = mapDiscordToEmployeeIds(ticketsReactions,
        employeeCodes);
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

  @Override
  @Transactional
  public void removeDuplicateReactions() {
    List<DiscordTicketsReactions> allReactions = discordTicketsReactionsRepository.findAll();

    Map<List<Object>, List<Long>> groupedReactions = allReactions.stream()
        .collect(Collectors.groupingBy(
            reaction -> List.of(
                reaction.getDiscordId(),
                reaction.getTicketId()),
            Collectors.mapping(
                DiscordTicketsReactions::getId,
                Collectors.toList())));

    List<Long> duplicateIds = groupedReactions.values()
        .stream()
        .flatMap(ids -> ids.stream().skip(1))
        .collect(Collectors.toList());

    discordTicketsReactionsRepository.deleteAllById(duplicateIds);
  }

  @Override
  @Transactional
  public void removeDuplicateTicketsData() {
    List<DiscordTickets> allReactions = discordTicketsRepository.findAll();

    Map<List<Object>, List<Long>> groupedReactions = allReactions.stream()
        .collect(Collectors.groupingBy(
            reaction -> List.of(
                reaction.getEmployeeId(),
                reaction.getDate()),
            Collectors.mapping(
                DiscordTickets::getId,
                Collectors.toList())));

    List<Long> duplicateIds = groupedReactions.values()
        .stream()
        .flatMap(ids -> ids.stream().skip(1))
        .collect(Collectors.toList());

    discordTicketsRepository.deleteAllById(duplicateIds);
  }
}
