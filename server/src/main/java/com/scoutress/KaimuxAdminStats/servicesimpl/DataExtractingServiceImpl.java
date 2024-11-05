package com.scoutress.KaimuxAdminStats.servicesimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.discordTickets.DiscordTicketsReactions;
import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.MinecraftTicketsAnswers;
import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.entity.playtime.SanitizedSessionData;
import com.scoutress.KaimuxAdminStats.entity.playtime.SessionDataItem;
import com.scoutress.KaimuxAdminStats.entity.playtime.SessionDuration;
import com.scoutress.KaimuxAdminStats.repositories.discordTickets.DiscordTicketsReactionsRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeCodesRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.MinecraftTicketsAnswersRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.DailyPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.PlaytimeSessionsRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.ProcessedPlaytimeSessionsRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.SanitazedDataRepository;
import com.scoutress.KaimuxAdminStats.services.DataExtractingService;

@Service
public class DataExtractingServiceImpl implements DataExtractingService {

  public final PlaytimeSessionsRepository playtimeSessionsRepository;
  public final SanitazedDataRepository sanitazedDataRepository;
  public final ProcessedPlaytimeSessionsRepository processedPlaytimeSessionsRepository;
  public final DailyPlaytimeRepository dailyPlaytimeRepository;
  public final EmployeeCodesRepository employeeCodesRepository;
  public final EmployeeRepository employeeRepository;
  public final DiscordTicketsReactionsRepository discordTicketsReactionsRepository;
  public final MinecraftTicketsAnswersRepository minecraftTicketsAnswersRepository;

  public DataExtractingServiceImpl(
      PlaytimeSessionsRepository playtimeSessionsRepository,
      SanitazedDataRepository sanitazedDataRepository,
      ProcessedPlaytimeSessionsRepository processedPlaytimeSessionsRepository,
      DailyPlaytimeRepository dailyPlaytimeRepository,
      EmployeeCodesRepository employeeCodesRepository,
      EmployeeRepository employeeRepository,
      DiscordTicketsReactionsRepository discordTicketsReactionsRepository,
      MinecraftTicketsAnswersRepository minecraftTicketsAnswersRepository) {

    this.playtimeSessionsRepository = playtimeSessionsRepository;
    this.sanitazedDataRepository = sanitazedDataRepository;
    this.processedPlaytimeSessionsRepository = processedPlaytimeSessionsRepository;
    this.dailyPlaytimeRepository = dailyPlaytimeRepository;
    this.employeeCodesRepository = employeeCodesRepository;
    this.employeeRepository = employeeRepository;
    this.discordTicketsReactionsRepository = discordTicketsReactionsRepository;
    this.minecraftTicketsAnswersRepository = minecraftTicketsAnswersRepository;
  }

  @Override
  public List<SessionDataItem> getLoginLogoutTimes() {
    return playtimeSessionsRepository.findAll();
  }

  @Override
  public List<SanitizedSessionData> getSanitizedLoginLogoutTimes() {
    return sanitazedDataRepository.findAll();
  }

  @Override
  public List<SessionDuration> getSessionDurations() {
    return processedPlaytimeSessionsRepository.findAll();
  }

  @Override
  public List<DailyPlaytime> getDailyPlaytimeData() {
    return dailyPlaytimeRepository.findAll();
  }

  @Override
  public List<EmployeeCodes> getAllEmployeeCodes() {
    return employeeCodesRepository.findAll();
  }

  @Override
  public List<Employee> getAllEmployees() {
    return employeeRepository.findAll();
  }

  @Override
  public List<DiscordTicketsReactions> getAllDcTicketReactions() {
    return discordTicketsReactionsRepository.findAll();
  }

  @Override
  public List<MinecraftTicketsAnswers> getAllMcTicketsAnswers() {
    return minecraftTicketsAnswersRepository.findAll();
  }
}
