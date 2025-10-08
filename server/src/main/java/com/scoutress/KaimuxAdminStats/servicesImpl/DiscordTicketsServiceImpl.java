package com.scoutress.KaimuxAdminStats.servicesImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.discordTickets.DailyDiscordTickets;
import com.scoutress.KaimuxAdminStats.entity.discordTickets.DiscordTicketsRawData;
import com.scoutress.KaimuxAdminStats.entity.discordTickets.DiscordTicketsReactions;
import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;
import com.scoutress.KaimuxAdminStats.repositories.discordTickets.DailyDiscordTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordTickets.DiscordTicketsRawDataRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordTickets.DiscordTicketsReactionsRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeCodesRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.services.DiscordTicketsService;

import jakarta.transaction.Transactional;

@Service
public class DiscordTicketsServiceImpl implements DiscordTicketsService {

  private final DiscordTicketsReactionsRepository discordTicketsReactionsRepository;
  private final EmployeeCodesRepository employeeCodesRepository;
  private final DiscordTicketsRawDataRepository discordTicketsRawDataRepository;
  private final EmployeeRepository employeeRepository;
  private final ApiDataExtractionServiceImpl apiDataExtractionServiceImpl;
  private final DailyDiscordTicketsRepository dailyDiscordTicketsRepository;

  public DiscordTicketsServiceImpl(
      DiscordTicketsReactionsRepository discordTicketsReactionsRepository,
      EmployeeCodesRepository employeeCodesRepository,
      DiscordTicketsRawDataRepository discordTicketsRawDataRepository,
      EmployeeRepository employeeRepository,
      ApiDataExtractionServiceImpl apiDataExtractionServiceImpl,
      DailyDiscordTicketsRepository dailyDiscordTicketsRepository) {
    this.discordTicketsReactionsRepository = discordTicketsReactionsRepository;
    this.employeeCodesRepository = employeeCodesRepository;
    this.discordTicketsRawDataRepository = discordTicketsRawDataRepository;
    this.employeeRepository = employeeRepository;
    this.apiDataExtractionServiceImpl = apiDataExtractionServiceImpl;
    this.dailyDiscordTicketsRepository = dailyDiscordTicketsRepository;
  }

  @Override
  public void processDiscordTickets() {
    removeOldProcessedDcTicketsData();

    List<EmployeeCodes> employeeCodes = extractEmployeeCodes();
    List<Short> allEmployeeIds = getAllEmployeeIdsFromEmployeeCodes(employeeCodes);
    List<DiscordTicketsRawData> allDcTicketsRawData = getDiscordTicketsRawData();

    List<LocalDate> allLatestDates = new ArrayList<>();

    for (Short employeeId : allEmployeeIds) {
      Short employeeCode = getEmployeeCodeByEmployeeId(employeeCodes, employeeId);
      boolean hasEmployeeData = hasEmployeeDcTicketsData(employeeCode, allDcTicketsRawData);

      LocalDate latestDateForEmployee = null;

      if (hasEmployeeData) {
        LocalDateTime latestDateTime = getLatestDateFromThisEmployeeData(employeeCode, allDcTicketsRawData);
        latestDateForEmployee = latestDateTime != null ? latestDateTime.toLocalDate() : null;
      } else {
        LocalDate latestDateRaw = getJoinDateOfEmployeeWithoutData(employeeId);

        if (latestDateRaw != null) {
          latestDateForEmployee = latestDateRaw.minusDays(1);
        } else {
          System.out.println("ALERT: Skipping employee " + employeeId + " because no tickets and no join date");
        }
      }

      if (latestDateForEmployee != null) {
        allLatestDates.add(latestDateForEmployee);
      }
    }

    LocalDate oldestDate = allLatestDates
        .stream()
        .min(LocalDate::compareTo)
        .orElse(LocalDate.now());

    System.out.println("Processing Discord tickets from oldest date: " + oldestDate);
    apiDataExtractionServiceImpl.extractDiscordTicketsFromAPI(oldestDate);
    moveDcTicketsReactionsToRawData();

    removeDcTicketsRawDataDuplicates();

    List<DiscordTicketsRawData> allDcTicketsRawDataAfterAddedNewData = getDiscordTicketsRawData();
    processDailyDiscordTickets(allDcTicketsRawDataAfterAddedNewData);

    removeDailyDiscordTicketsDuplicates();
  }

  private void removeOldProcessedDcTicketsData() {
    discordTicketsReactionsRepository.truncateTable();
  }

  private List<EmployeeCodes> extractEmployeeCodes() {
    return employeeCodesRepository.findAll();
  }

  private List<Short> getAllEmployeeIdsFromEmployeeCodes(List<EmployeeCodes> employeeCodes) {
    return employeeCodes
        .stream()
        .map(EmployeeCodes::getEmployeeId)
        .distinct()
        .toList();
  }

  private List<DiscordTicketsRawData> getDiscordTicketsRawData() {
    return discordTicketsRawDataRepository.findAll();
  }

  private Short getEmployeeCodeByEmployeeId(List<EmployeeCodes> employeeCodes, Short employeeId) {
    return employeeCodes
        .stream()
        .filter(employeeCode -> employeeCode.getEmployeeId().equals(employeeId))
        .map(EmployeeCodes::getKmxWebApi)
        .findFirst()
        .orElse(null);
  }

  private boolean hasEmployeeDcTicketsData(
      Short employeeCode, List<DiscordTicketsRawData> allDcTicketsRawData) {
    return allDcTicketsRawData
        .stream()
        .filter(employee -> employee.getDiscordId() != null && employee.getDiscordId().equals(employeeCode.longValue()))
        .anyMatch(employee -> employee.getDateTime() != null);
  }

  private LocalDateTime getLatestDateFromThisEmployeeData(
      Short employeeCode, List<DiscordTicketsRawData> allDcTicketsRawData) {
    return allDcTicketsRawData
        .stream()
        .filter(employee -> employee.getDiscordId() != null && employee.getDiscordId().equals(employeeCode.longValue()))
        .map(DiscordTicketsRawData::getDateTime)
        .max(LocalDateTime::compareTo)
        .orElse(null);
  }

  private LocalDate getJoinDateOfEmployeeWithoutData(Short employeeId) {
    return employeeRepository
        .findAll()
        .stream()
        .filter(employee -> employee.getId().equals(employeeId))
        .map(Employee::getJoinDate)
        .findFirst()
        .orElse(null);
  }

  private void moveDcTicketsReactionsToRawData() {
    List<DiscordTicketsReactions> allDcTicketsReactions = discordTicketsReactionsRepository.findAll();

    for (DiscordTicketsReactions reaction : allDcTicketsReactions) {
      DiscordTicketsRawData rawData = new DiscordTicketsRawData();
      rawData.setDiscordId(reaction.getDiscordId());
      rawData.setTicketId(reaction.getTicketId());
      rawData.setDateTime(reaction.getDateTime());
      discordTicketsRawDataRepository.save(rawData);
    }
    discordTicketsReactionsRepository.deleteAll();
  }

  @Transactional
  private void removeDcTicketsRawDataDuplicates() {
    List<DiscordTicketsRawData> allDcTicketsRawData = getDiscordTicketsRawData();

    Map<String, List<DiscordTicketsRawData>> grouped = allDcTicketsRawData
        .stream()
        .collect(Collectors.groupingBy(msg -> msg.getDiscordId() + "-" +
            (msg.getTicketId() != null ? msg.getTicketId() : "null") + "-" +
            (msg.getDateTime() != null ? msg.getDateTime().toString() : "null")));

    grouped.forEach((key, messages) -> {
      messages.stream()
          .sorted(Comparator.comparing(DiscordTicketsRawData::getId))
          .skip(1)
          .forEach(discordTicketsRawDataRepository::delete);
    });
  }

  private void processDailyDiscordTickets(List<DiscordTicketsRawData> rawData) {
    List<Long> allDiscordIds = rawData
        .stream()
        .map(DiscordTicketsRawData::getDiscordId)
        .filter(java.util.Objects::nonNull)
        .distinct()
        .toList();

    for (Long discordId : allDiscordIds) {
      List<DiscordTicketsRawData> employeeTickets = rawData
          .stream()
          .filter(ticket -> ticket.getDiscordId() != null && ticket.getDiscordId().equals(discordId))
          .toList();

      Map<LocalDate, Long> ticketsCountByDate = employeeTickets
          .stream()
          .collect(Collectors.groupingBy(
              ticket -> ticket.getDateTime().toLocalDate(),
              Collectors.counting()));

      Short employeeId = getEmployeeIdByDiscordId(discordId);
      if (employeeId == null)
        continue;

      for (Map.Entry<LocalDate, Long> entry : ticketsCountByDate.entrySet()) {
        DailyDiscordTickets dailyTicket = new DailyDiscordTickets();
        dailyTicket.setEmployeeId(employeeId);
        dailyTicket.setDate(entry.getKey());
        dailyTicket.setDcTicketCount(entry.getValue().intValue());
        dailyDiscordTicketsRepository.save(dailyTicket);
      }
    }
  }

  private Short getEmployeeIdByDiscordId(Long discordId) {
    return employeeCodesRepository
        .findAll()
        .stream()
        .filter(code -> code.getDiscordUserId() != null && code.getKmxWebApi().longValue() == discordId.longValue())
        .map(EmployeeCodes::getEmployeeId)
        .findFirst()
        .orElse(null);
  }

  @Transactional
  private void removeDailyDiscordTicketsDuplicates() {
    List<DailyDiscordTickets> allDailyDiscordTickets = dailyDiscordTicketsRepository.findAll();

    Map<String, List<DailyDiscordTickets>> grouped = allDailyDiscordTickets
        .stream()
        .collect(Collectors.groupingBy(msg -> msg.getEmployeeId() + "-" +
            (msg.getDate() != null ? msg.getDate().toString() : "null") + "-" +
            (msg.getDcTicketCount() != null ? msg.getDcTicketCount().toString() : "null")));

    grouped.forEach((key, messages) -> {
      messages.stream()
          .sorted(Comparator.comparing(DailyDiscordTickets::getId))
          .skip(1)
          .forEach(dailyDiscordTicketsRepository::delete);
    });
  }
}
