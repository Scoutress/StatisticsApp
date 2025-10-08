package com.scoutress.KaimuxAdminStats.servicesImpl.minecraftTickets;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.DailyMinecraftTickets;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.McTicketsLastCheck;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.MinecraftTicketsAnswers;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.TotalOldMinecraftTickets;
import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeCodesRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.DailyMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.McTicketsLastCheckRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.MinecraftTicketsAnswersRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.TotalOldMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.DailyPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.services.minecraftTickets.MinecraftTicketsRawService;
import com.scoutress.KaimuxAdminStats.servicesImpl.ApiDataExtractionServiceImpl;
import com.scoutress.KaimuxAdminStats.servicesImpl.DuplicatesRemoverServiceImpl;

@Service
public class MinecraftTicketsRawServiceImpl implements MinecraftTicketsRawService {

  private final MinecraftTicketsAnswersRepository minecraftTicketsAnswersRepository;
  private final ApiDataExtractionServiceImpl apiDataExtractionServiceImpl;
  private final DailyMinecraftTicketsRepository dailyMinecraftTicketsRepository;
  private final MinecraftTicketsServiceImpl minecraftTicketsServiceImpl;
  private final EmployeeCodesRepository employeeCodesRepository;
  private final EmployeeRepository employeeRepository;
  private final DailyPlaytimeRepository dailyPlaytimeRepository;
  private final MinecraftTicketsComparedServiceImpl minecraftTicketsComparedServiceImpl;
  private final TotalOldMinecraftTicketsRepository totalOldMinecraftTicketsRepository;
  private final DuplicatesRemoverServiceImpl duplicatesRemoverServiceImpl;
  private final McTicketsLastCheckRepository mcTicketsLastCheckRepository;

  public MinecraftTicketsRawServiceImpl(
      MinecraftTicketsAnswersRepository minecraftTicketsAnswersRepository,
      ApiDataExtractionServiceImpl apiDataExtractionServiceImpl,
      DailyMinecraftTicketsRepository dailyMinecraftTicketsRepository,
      MinecraftTicketsServiceImpl minecraftTicketsServiceImpl,
      EmployeeCodesRepository employeeCodesRepository,
      EmployeeRepository employeeRepository,
      DailyPlaytimeRepository dailyPlaytimeRepository,
      MinecraftTicketsComparedServiceImpl minecraftTicketsComparedServiceImpl,
      TotalOldMinecraftTicketsRepository totalOldMinecraftTicketsRepository,
      DuplicatesRemoverServiceImpl duplicatesRemoverServiceImpl,
      McTicketsLastCheckRepository mcTicketsLastCheckRepository) {
    this.minecraftTicketsAnswersRepository = minecraftTicketsAnswersRepository;
    this.apiDataExtractionServiceImpl = apiDataExtractionServiceImpl;
    this.dailyMinecraftTicketsRepository = dailyMinecraftTicketsRepository;
    this.minecraftTicketsServiceImpl = minecraftTicketsServiceImpl;
    this.employeeCodesRepository = employeeCodesRepository;
    this.employeeRepository = employeeRepository;
    this.dailyPlaytimeRepository = dailyPlaytimeRepository;
    this.minecraftTicketsComparedServiceImpl = minecraftTicketsComparedServiceImpl;
    this.totalOldMinecraftTicketsRepository = totalOldMinecraftTicketsRepository;
    this.duplicatesRemoverServiceImpl = duplicatesRemoverServiceImpl;
    this.mcTicketsLastCheckRepository = mcTicketsLastCheckRepository;
  }

  @Override
  public void handleMinecraftTickets() {
    removeRawMcTicketsData();

    List<EmployeeCodes> employeeCodes = extractEmployeeCodes();
    List<Short> allEmployeeIds = getAllEmployeeIdsFromEmployeeCodes(employeeCodes);
    LocalDate newestDateFromDailyMcTickets = getNewestDateFromDailyMcTickets();
    List<DailyMinecraftTickets> allDailyMcTickets = getDailyMinecraftTicketsData();
    List<LocalDate> joinDates = new ArrayList<>();

    for (Short employeeId : allEmployeeIds) {
      boolean hasEmployeeData = hasEmployeeMcTicketsData(employeeId, allDailyMcTickets);

      if (!hasEmployeeData) {
        boolean wasEmployeeCheckedBefore = wasEmployeeCheckedBefore(employeeId);

        if (!wasEmployeeCheckedBefore) {
          LocalDate joinDateOfEmployeeWithoutData = getJoinDateOfEmployeeWithoutData(employeeId);
          joinDates.add(joinDateOfEmployeeWithoutData);
        }
      }
    }

    if (!joinDates.isEmpty()) {
      LocalDate oldestJoinDate = getOldestJoinDate(joinDates);
      apiDataExtractionServiceImpl.extractMinecraftTicketsFromAPI(oldestJoinDate);
    } else {
      apiDataExtractionServiceImpl.extractMinecraftTicketsFromAPI(newestDateFromDailyMcTickets);
    }

    updateMcTicketsLastCheck(allEmployeeIds);

    List<MinecraftTicketsAnswers> rawMcTicketsData = extractRawMcTicketsData();
    minecraftTicketsServiceImpl.convertRawMcTicketsData(
        rawMcTicketsData, employeeCodes, allEmployeeIds);

    duplicatesRemoverServiceImpl.removeDuplicatesFromDailyMcTickets();

    List<DailyMinecraftTickets> rawDailyMcTicketsData = getDailyMinecraftTicketsData();
    List<Employee> rawEmployeesData = getEmployeesData();
    LocalDate oldestDateFromData = checkForOldestDate(rawDailyMcTicketsData);
    minecraftTicketsServiceImpl.calcAvgDailyMcTicketsPerEmployee(
        allEmployeeIds, rawEmployeesData, oldestDateFromData, rawDailyMcTicketsData);

    duplicatesRemoverServiceImpl.removeDuplicatesFromAvgDailyMcTickets();

    List<DailyPlaytime> allPlaytimeData = getAllPlaytimeData();
    minecraftTicketsServiceImpl.calcAvgMcTicketsPerPlaytime(
        rawDailyMcTicketsData, rawEmployeesData, allEmployeeIds, oldestDateFromData, allPlaytimeData);

    duplicatesRemoverServiceImpl.removeDuplicatesFromMcTicketsPerPlaytime();

    List<TotalOldMinecraftTickets> allOldTotalMinecraftTicketsData = getOldTotalDailyMinecraftTicketsData();
    minecraftTicketsServiceImpl.calcTotalMinecraftTickets(
        allEmployeeIds, rawDailyMcTicketsData, allOldTotalMinecraftTicketsData);

    duplicatesRemoverServiceImpl.removeDuplicatesFromTotalMcTickets();

    List<LocalDate> allDatesFromDailyMcTickets = getAllMinecraftTicketsDates(rawDailyMcTicketsData);
    List<Short> allEmployeesFromDailyMcTickets = getAllEmployeesFromDailyMinecraftTickets(rawDailyMcTicketsData);
    minecraftTicketsComparedServiceImpl.compareEachEmployeeDailyMcTicketsValues(
        rawDailyMcTicketsData, allPlaytimeData, allDatesFromDailyMcTickets, allEmployeesFromDailyMcTickets);

    duplicatesRemoverServiceImpl.removeDuplicatesFromComparedMcTickets();
  }

  private void removeRawMcTicketsData() {
    minecraftTicketsAnswersRepository.truncateTable();
  }

  private LocalDate getNewestDateFromDailyMcTickets() {
    return dailyMinecraftTicketsRepository
        .findAll()
        .stream()
        .map(DailyMinecraftTickets::getDate)
        .max(LocalDate::compareTo)
        .orElse(LocalDate.parse("1970-01-01"));
  }

  private boolean hasEmployeeMcTicketsData(
      Short employeeId, List<DailyMinecraftTickets> allDailyMcTickets) {
    return allDailyMcTickets
        .stream()
        .filter(employee -> employee.getEmployeeId().equals(employeeId))
        .anyMatch(employee -> employee.getDate() != null && employee.getTicketCount() > 0);
  }

  private LocalDate getJoinDateOfEmployeeWithoutData(Short employeeId) {
    return employeeRepository
        .findById(employeeId)
        .map(Employee::getJoinDate)
        .filter(Objects::nonNull)
        .orElseGet(() -> {
          System.err.println("ALERT: Employee " + employeeId + " has no join date — skipping.");
          return null;
        });
  }

  private LocalDate getOldestJoinDate(List<LocalDate> joinDates) {
    LocalDate oldest = joinDates
        .stream()
        .filter(Objects::nonNull)
        .min(LocalDate::compareTo)
        .orElse(null);

    if (oldest == null) {
      System.err.println("ALERT: No valid join dates found — skipping this calculation.");
    }

    return oldest;
  }

  private boolean wasEmployeeCheckedBefore(Short employeeId) {
    return mcTicketsLastCheckRepository
        .findAll()
        .stream()
        .filter(employee -> employeeId.equals(employee.getEmployeeId()))
        .anyMatch(employee -> employee.getDate() != null);
  }

  private List<MinecraftTicketsAnswers> extractRawMcTicketsData() {
    return minecraftTicketsAnswersRepository.findAll();
  }

  private void updateMcTicketsLastCheck(List<Short> allEmployeeIds) {
    mcTicketsLastCheckRepository.deleteAll();

    for (Short employeeId : allEmployeeIds) {
      mcTicketsLastCheckRepository.save(new McTicketsLastCheck(employeeId, LocalDate.now()));
    }
  }

  private List<EmployeeCodes> extractEmployeeCodes() {
    return employeeCodesRepository.findAll();
  }

  private List<Short> getAllEmployeeIdsFromEmployeeCodes(List<EmployeeCodes> employeeCodes) {
    return employeeCodes
        .stream()
        .map(EmployeeCodes::getEmployeeId)
        .distinct()
        .collect(Collectors.toList());
  }

  private List<DailyMinecraftTickets> getDailyMinecraftTicketsData() {
    return dailyMinecraftTicketsRepository.findAll();
  }

  private List<Employee> getEmployeesData() {
    return employeeRepository.findAll();
  }

  private LocalDate checkForOldestDate(List<DailyMinecraftTickets> rawData) {
    return rawData
        .stream()
        .map(DailyMinecraftTickets::getDate)
        .min(LocalDate::compareTo)
        .orElse(LocalDate.of(2100, 1, 1));
  }

  private List<DailyPlaytime> getAllPlaytimeData() {
    return dailyPlaytimeRepository.findAll();
  }

  private List<TotalOldMinecraftTickets> getOldTotalDailyMinecraftTicketsData() {
    return totalOldMinecraftTicketsRepository.findAll();
  }

  private List<LocalDate> getAllMinecraftTicketsDates(List<DailyMinecraftTickets> data) {
    return data
        .stream()
        .map(DailyMinecraftTickets::getDate)
        .distinct()
        .collect(Collectors.toList());
  }

  private List<Short> getAllEmployeesFromDailyMinecraftTickets(List<DailyMinecraftTickets> data) {
    return data
        .stream()
        .map(DailyMinecraftTickets::getEmployeeId)
        .distinct()
        .collect(Collectors.toList());
  }
}
