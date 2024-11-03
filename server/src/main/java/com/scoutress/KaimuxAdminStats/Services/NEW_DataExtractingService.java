package com.scoutress.KaimuxAdminStats.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.employees.NEW_Employee;
import com.scoutress.KaimuxAdminStats.Entity.employees.NEW_EmployeeCodes;
import com.scoutress.KaimuxAdminStats.Entity.playtime.NEW_DailyPlaytime;
import com.scoutress.KaimuxAdminStats.Entity.playtime.NEW_SanitizedSessionData;
import com.scoutress.KaimuxAdminStats.Entity.playtime.NEW_SessionDataItem;
import com.scoutress.KaimuxAdminStats.Entity.playtime.NEW_SessionDuration;
import com.scoutress.KaimuxAdminStats.Repositories.employees.NEW_EmployeeCodesRepository;
import com.scoutress.KaimuxAdminStats.Repositories.employees.NEW_EmployeeRepository;
import com.scoutress.KaimuxAdminStats.Repositories.playtime.NEW_DailyPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.Repositories.playtime.NEW_PlaytimeSessionsRepository;
import com.scoutress.KaimuxAdminStats.Repositories.playtime.NEW_ProcessedPlaytimeSessionsRepository;
import com.scoutress.KaimuxAdminStats.Repositories.playtime.NEW_SanitazedDataRepository;

@Service
public class NEW_DataExtractingService {

  public final NEW_PlaytimeSessionsRepository playtimeSessionsRepository;
  public final NEW_SanitazedDataRepository sanitazedDataRepository;
  public final NEW_ProcessedPlaytimeSessionsRepository processedPlaytimeSessionsRepository;
  public final NEW_DailyPlaytimeRepository dailyPlaytimeRepository;
  public final NEW_EmployeeCodesRepository employeeCodesRepository;
  public final NEW_EmployeeRepository employeeRepository;

  public NEW_DataExtractingService(
      NEW_PlaytimeSessionsRepository playtimeSessionsRepository,
      NEW_SanitazedDataRepository sanitazedDataRepository,
      NEW_ProcessedPlaytimeSessionsRepository processedPlaytimeSessionsRepository,
      NEW_DailyPlaytimeRepository dailyPlaytimeRepository,
      NEW_EmployeeCodesRepository employeeCodesRepository,
      NEW_EmployeeRepository employeeRepository) {

    this.playtimeSessionsRepository = playtimeSessionsRepository;
    this.sanitazedDataRepository = sanitazedDataRepository;
    this.processedPlaytimeSessionsRepository = processedPlaytimeSessionsRepository;
    this.dailyPlaytimeRepository = dailyPlaytimeRepository;
    this.employeeCodesRepository = employeeCodesRepository;
    this.employeeRepository = employeeRepository;
  }

  public List<NEW_SessionDataItem> getLoginLogoutTimes() {
    return playtimeSessionsRepository.findAll();
  }

  public List<NEW_SanitizedSessionData> getSanitizedLoginLogoutTimes() {
    return sanitazedDataRepository.findAll();
  }

  public List<NEW_SessionDuration> getSessionDurations() {
    return processedPlaytimeSessionsRepository.findAll();
  }

  public List<NEW_DailyPlaytime> getDailyPlaytimeData() {
    return dailyPlaytimeRepository.findAll();
  }

  public List<NEW_EmployeeCodes> getAidsFromEmployeeCodes() {
    return employeeCodesRepository.findAll();
  }

  public List<NEW_Employee> getAllEmployees() {
    return employeeRepository.findAll();
  }
}
