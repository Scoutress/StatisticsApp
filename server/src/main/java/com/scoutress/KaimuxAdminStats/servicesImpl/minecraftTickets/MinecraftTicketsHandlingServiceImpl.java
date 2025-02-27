package com.scoutress.KaimuxAdminStats.servicesImpl.minecraftTickets;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.DailyMinecraftTickets;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.MinecraftTicketsAnswers;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.TotalOldMinecraftTickets;
import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeCodesRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.DailyMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.MinecraftTicketsAnswersRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.TotalOldMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.DailyPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.services.ApiDataExtractionService;
import com.scoutress.KaimuxAdminStats.services.DuplicatesRemoverService;
import com.scoutress.KaimuxAdminStats.services.minecraftTickets.MinecraftTicketsComparedService;
import com.scoutress.KaimuxAdminStats.services.minecraftTickets.MinecraftTicketsHandlingService;
import com.scoutress.KaimuxAdminStats.services.minecraftTickets.MinecraftTicketsService;

@Service
public class MinecraftTicketsHandlingServiceImpl implements MinecraftTicketsHandlingService {

  private final MinecraftTicketsAnswersRepository minecraftTicketsAnswersRepository;
  private final ApiDataExtractionService apiDataExtractionService;
  private final DailyMinecraftTicketsRepository dailyMinecraftTicketsRepository;
  private final MinecraftTicketsService minecraftTicketsService;
  private final EmployeeCodesRepository employeeCodesRepository;
  private final EmployeeRepository employeeRepository;
  private final DailyPlaytimeRepository dailyPlaytimeRepository;
  private final MinecraftTicketsComparedService minecraftTicketsComparedService;
  private final TotalOldMinecraftTicketsRepository totalOldMinecraftTicketsRepository;
  private final DuplicatesRemoverService duplicatesRemoverService;

  public MinecraftTicketsHandlingServiceImpl(
      MinecraftTicketsAnswersRepository minecraftTicketsAnswersRepository,
      ApiDataExtractionService apiDataExtractionService,
      DailyMinecraftTicketsRepository dailyMinecraftTicketsRepository,
      MinecraftTicketsService minecraftTicketsService,
      EmployeeCodesRepository employeeCodesRepository,
      EmployeeRepository employeeRepository,
      DailyPlaytimeRepository dailyPlaytimeRepository,
      MinecraftTicketsComparedService minecraftTicketsComparedService,
      TotalOldMinecraftTicketsRepository totalOldMinecraftTicketsRepository,
      DuplicatesRemoverService duplicatesRemoverService) {
    this.minecraftTicketsAnswersRepository = minecraftTicketsAnswersRepository;
    this.apiDataExtractionService = apiDataExtractionService;
    this.dailyMinecraftTicketsRepository = dailyMinecraftTicketsRepository;
    this.minecraftTicketsService = minecraftTicketsService;
    this.employeeCodesRepository = employeeCodesRepository;
    this.employeeRepository = employeeRepository;
    this.dailyPlaytimeRepository = dailyPlaytimeRepository;
    this.minecraftTicketsComparedService = minecraftTicketsComparedService;
    this.totalOldMinecraftTicketsRepository = totalOldMinecraftTicketsRepository;
    this.duplicatesRemoverService = duplicatesRemoverService;
  }

  @Override
  public void handleMinecraftTickets() {
    removeRawMcTicketsData();

    LocalDate newestDateFromDailyMcTickets = getNewestDateFromDailyMcTickets();
    apiDataExtractionService.extractMinecraftTicketsFromAPI(newestDateFromDailyMcTickets);

    List<MinecraftTicketsAnswers> rawMcTicketsData = extractRawMcTicketsData();
    List<EmployeeCodes> employeeCodes = extractEmployeeCodes();
    List<Short> allEmployeeIds = getAllEmployeeIdsFromEmployeeCodes(employeeCodes);
    minecraftTicketsService.convertRawMcTicketsData(
        rawMcTicketsData, employeeCodes, allEmployeeIds);

    duplicatesRemoverService.removeDuplicatesFromDailyMcTickets();

    List<DailyMinecraftTickets> rawDailyMcTicketsData = getDailyMinecraftTicketsData();
    List<Employee> rawEmployeesData = getEmployeesData();
    LocalDate oldestDateFromData = checkForOldestDate(rawDailyMcTicketsData);
    minecraftTicketsService.calcAvgDailyMcTicketsPerEmployee(
        allEmployeeIds, rawEmployeesData, oldestDateFromData, rawDailyMcTicketsData);

    duplicatesRemoverService.removeDuplicatesFromAvgDailyMcTickets();

    List<DailyPlaytime> allPlaytimeData = getAllPlaytimeData();
    minecraftTicketsService.calcAvgMcTicketsPerPlaytime(
        rawDailyMcTicketsData, rawEmployeesData, allEmployeeIds, oldestDateFromData, allPlaytimeData);

    duplicatesRemoverService.removeDuplicatesFromMcTicketsPerPlaytime();

    List<TotalOldMinecraftTickets> allOldTotalMinecraftTicketsData = getOldTotalDailyMinecraftTicketsData();
    minecraftTicketsService.calcTotalMinecraftTickets(
        allEmployeeIds, rawDailyMcTicketsData, allOldTotalMinecraftTicketsData);

    duplicatesRemoverService.removeDuplicatesFromTotalMcTickets();

    List<LocalDate> allDatesFromDailyMcTickets = getAllMinecraftTicketsDates(rawDailyMcTicketsData);
    List<Short> allEmployeesFromDailyMcTickets = getAllEmployeesFromDailyMinecraftTickets(rawDailyMcTicketsData);
    minecraftTicketsComparedService.compareEachEmployeeDailyMcTicketsValues(
        rawDailyMcTicketsData, allPlaytimeData, allDatesFromDailyMcTickets, allEmployeesFromDailyMcTickets);

    duplicatesRemoverService.removeDuplicatesFromComparedMcTickets();
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

  private List<MinecraftTicketsAnswers> extractRawMcTicketsData() {
    return minecraftTicketsAnswersRepository.findAll();
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
