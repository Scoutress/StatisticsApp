package com.scoutress.KaimuxAdminStats.Servicesimpl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.old.Employees.Employee;
import com.scoutress.KaimuxAdminStats.Entity.old.Playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.Entity.old.Playtime.LoginLogoutTimes;
import com.scoutress.KaimuxAdminStats.Repositories.old.DailyPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.Repositories.old.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.Repositories.old.LoginLogoutTimesRepository;
import com.scoutress.KaimuxAdminStats.Services.old.PlaytimeCalculationService;

@Service
public class PlaytimeCalculationServiceImpl implements PlaytimeCalculationService {

  public final EmployeeRepository employeeRepository;
  public final LoginLogoutTimesRepository loginLogoutTimesRepository;
  public final DailyPlaytimeRepository dailyPlaytimeRepository;

  public PlaytimeCalculationServiceImpl(EmployeeRepository employeeRepository,
      LoginLogoutTimesRepository loginLogoutTimesRepository, DailyPlaytimeRepository dailyPlaytimeRepository) {
    this.employeeRepository = employeeRepository;
    this.loginLogoutTimesRepository = loginLogoutTimesRepository;
    this.dailyPlaytimeRepository = dailyPlaytimeRepository;
  }

  @Override
  public void calculateDailyPlaytime() {
    List<Employee> employees = employeeRepository.findAll();
    for (Employee employee : employees) {
      List<LoginLogoutTimes> loginLogoutTimes = getEmployeeLoginLogoutTimes(employee);
      Map<String, Map<LocalDate, Double>> playtimePerServer = calculatePlaytimeForEmployee(loginLogoutTimes);
      updatePlaytimeForEmployee(employee, playtimePerServer);
    }
  }

  private List<LoginLogoutTimes> getEmployeeLoginLogoutTimes(Employee employee) {
    return loginLogoutTimesRepository.findByEmployeeId(employee.getId());
  }

  private Map<String, Map<LocalDate, Double>> calculatePlaytimeForEmployee(List<LoginLogoutTimes> loginLogoutTimes) {
    Map<String, Map<LocalDate, Double>> playtimePerServer = new HashMap<>();

    for (LoginLogoutTimes times : loginLogoutTimes) {
      String serverName = times.getServerName();
      LocalDateTime loginTime = times.getLoginTime();
      LocalDateTime logoutTime = times.getLogoutTime();
      long minutesPlayed = Duration.between(loginTime, logoutTime).toMinutes();
      double hoursPlayed = minutesPlayed / 60.0;

      playtimePerServer.computeIfAbsent(serverName, k -> new HashMap<>())
          .merge(loginTime.toLocalDate(), hoursPlayed, Double::sum);
    }
    return playtimePerServer;
  }

  private void updatePlaytimeForEmployee(Employee employee, Map<String, Map<LocalDate, Double>> playtimePerServer) {
    for (Map.Entry<String, Map<LocalDate, Double>> entry : playtimePerServer.entrySet()) {
      String serverName = entry.getKey();
      Map<LocalDate, Double> dailyPlaytimes = entry.getValue();

      for (Map.Entry<LocalDate, Double> dayEntry : dailyPlaytimes.entrySet()) {
        LocalDate date = dayEntry.getKey();
        Double playtime = dayEntry.getValue();

        DailyPlaytime dailyPlaytime = dailyPlaytimeRepository.findByEmployeeIdAndDate(employee.getId(), date);
        if (dailyPlaytime == null) {
          dailyPlaytime = new DailyPlaytime();
          dailyPlaytime.setEmployeeId(employee.getId());
          dailyPlaytime.setDate(date);
        }

        resetPlaytimeForServer(dailyPlaytime, serverName);
        updatePlaytimeForServer(dailyPlaytime, serverName, playtime);
        dailyPlaytimeRepository.save(dailyPlaytime);
      }
    }
  }

  private void resetPlaytimeForServer(DailyPlaytime dailyPlaytime, String serverName) {
    switch (serverName.toLowerCase()) {
      case "survival" -> dailyPlaytime.setTotalSurvivalPlaytime(0.0);
      case "skyblock" -> dailyPlaytime.setTotalSkyblockPlaytime(0.0);
      case "creative" -> dailyPlaytime.setTotalCreativePlaytime(0.0);
      case "boxpvp" -> dailyPlaytime.setTotalBoxpvpPlaytime(0.0);
      case "prison" -> dailyPlaytime.setTotalPrisonPlaytime(0.0);
      case "events" -> dailyPlaytime.setTotalEventsPlaytime(0.0);
    }
  }

  private void updatePlaytimeForServer(DailyPlaytime dailyPlaytime, String serverName, Double playtime) {
    if (dailyPlaytime == null || playtime == null) {
      return;
    }

    switch (serverName.toLowerCase()) {
      case "survival" -> dailyPlaytime.setTotalSurvivalPlaytime(playtime);
      case "skyblock" -> dailyPlaytime.setTotalSkyblockPlaytime(playtime);
      case "creative" -> dailyPlaytime.setTotalCreativePlaytime(playtime);
      case "boxpvp" -> dailyPlaytime.setTotalBoxpvpPlaytime(playtime);
      case "prison" -> dailyPlaytime.setTotalPrisonPlaytime(playtime);
      case "events" -> dailyPlaytime.setTotalEventsPlaytime(playtime);
    }

    double totalPlaytime = dailyPlaytime.getTotalSurvivalPlaytime() +
        dailyPlaytime.getTotalSkyblockPlaytime() +
        dailyPlaytime.getTotalCreativePlaytime() +
        dailyPlaytime.getTotalBoxpvpPlaytime() +
        dailyPlaytime.getTotalPrisonPlaytime() +
        dailyPlaytime.getTotalEventsPlaytime();

    dailyPlaytime.setTotalPlaytime(totalPlaytime);
  }
}
