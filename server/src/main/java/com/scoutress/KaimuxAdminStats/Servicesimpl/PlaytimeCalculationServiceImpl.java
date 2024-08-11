package com.scoutress.KaimuxAdminStats.Servicesimpl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.Employee;
import com.scoutress.KaimuxAdminStats.Entity.Playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.Entity.Playtime.LoginLogoutTimes;
import com.scoutress.KaimuxAdminStats.Repositories.DailyPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.Repositories.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.Repositories.LoginLogoutTimesRepository;
import com.scoutress.KaimuxAdminStats.Services.PlaytimeCalculationService;

import jakarta.transaction.Transactional;

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
  @Transactional
  public void calculateDailyPlaytime() {
    List<Employee> employees = employeeRepository.findAll();

    for (Employee employee : employees) {
      List<LoginLogoutTimes> loginLogoutTimes = loginLogoutTimesRepository.findByEmployeeId(employee.getId());

      Map<String, Map<LocalDate, Double>> playtimePerServer = new HashMap<>();

      for (LoginLogoutTimes times : loginLogoutTimes) {
        LocalDateTime loginTime = times.getLoginTime();
        LocalDateTime logoutTime = times.getLogoutTime();
        String serverName = times.getServerName();

        if (!loginTime.toLocalDate().equals(logoutTime.toLocalDate())) {
          LocalDate firstDay = loginTime.toLocalDate();
          LocalTime midnight = LocalTime.MIDNIGHT;
          LocalDateTime midnightTime = LocalDateTime.of(firstDay.plusDays(1), midnight);
          Duration firstDuration = Duration.between(loginTime, midnightTime);

          playtimePerServer.computeIfAbsent(serverName, k -> new HashMap<>())
              .merge(firstDay, (double) firstDuration.toMinutes() / 60, Double::sum);

          LocalDate secondDay = logoutTime.toLocalDate();
          Duration secondDuration = Duration.between(midnightTime, logoutTime);

          playtimePerServer.computeIfAbsent(serverName, k -> new HashMap<>())
              .merge(secondDay, (double) secondDuration.toMinutes() / 60, Double::sum);
        } else {
          Duration duration = Duration.between(loginTime, logoutTime);
          playtimePerServer.computeIfAbsent(serverName, k -> new HashMap<>())
              .merge(loginTime.toLocalDate(), (double) duration.toMinutes() / 60, Double::sum);
        }
      }

      for (Map.Entry<String, Map<LocalDate, Double>> serverEntry : playtimePerServer.entrySet()) {
        String serverName = serverEntry.getKey();
        for (Map.Entry<LocalDate, Double> dayEntry : serverEntry.getValue().entrySet()) {
          LocalDate date = dayEntry.getKey();
          Double playtime = dayEntry.getValue();

          DailyPlaytime dailyPlaytime = dailyPlaytimeRepository.findByEmployeeIdAndDate(employee.getId(), date);

          if (dailyPlaytime == null) {
            dailyPlaytime = new DailyPlaytime();
            dailyPlaytime.setEmployeeId(employee.getId());
            dailyPlaytime.setDate(date);
            initializeDailyPlaytime(dailyPlaytime);
          }

          updatePlaytimeForServer(dailyPlaytime, serverName, playtime);

          dailyPlaytimeRepository.save(dailyPlaytime);
        }
      }
    }
  }

  private void initializeDailyPlaytime(DailyPlaytime dailyPlaytime) {
    dailyPlaytime.setTotalSurvivalPlaytime(0.0);
    dailyPlaytime.setTotalSkyblockPlaytime(0.0);
    dailyPlaytime.setTotalCreativePlaytime(0.0);
    dailyPlaytime.setTotalBoxpvpPlaytime(0.0);
    dailyPlaytime.setTotalPrisonPlaytime(0.0);
    dailyPlaytime.setTotalEventsPlaytime(0.0);
    dailyPlaytime.setTotalPlaytime(0.0);
  }

  @Override
  public void updatePlaytimeForServer(DailyPlaytime dailyPlaytime, String serverName, Double playtime) {
    switch (serverName.toLowerCase()) {
      case "survival" -> {
        double currentSurvivalPlaytime = dailyPlaytime.getTotalSurvivalPlaytime();
        dailyPlaytime.setTotalSurvivalPlaytime(playtime + currentSurvivalPlaytime);
      }
      case "skyblock" -> {
        double currentSkyblockPlaytime = dailyPlaytime.getTotalSkyblockPlaytime();
        dailyPlaytime.setTotalSkyblockPlaytime(playtime + currentSkyblockPlaytime);
      }
      case "creative" -> {
        double currentCreativePlaytime = dailyPlaytime.getTotalCreativePlaytime();
        dailyPlaytime.setTotalCreativePlaytime(playtime + currentCreativePlaytime);
      }
      case "boxpvp" -> {
        double currentBoxpvpPlaytime = dailyPlaytime.getTotalBoxpvpPlaytime();
        dailyPlaytime.setTotalBoxpvpPlaytime(playtime + currentBoxpvpPlaytime);
      }
      case "prison" -> {
        double currentPrisonPlaytime = dailyPlaytime.getTotalPrisonPlaytime();
        dailyPlaytime.setTotalPrisonPlaytime(playtime + currentPrisonPlaytime);
      }
      case "events" -> {
        double currentEventsPlaytime = dailyPlaytime.getTotalEventsPlaytime();
        dailyPlaytime.setTotalEventsPlaytime(playtime + currentEventsPlaytime);
      }
    }

    dailyPlaytime.setTotalPlaytime(
        dailyPlaytime.getTotalSurvivalPlaytime() +
            dailyPlaytime.getTotalSkyblockPlaytime() +
            dailyPlaytime.getTotalCreativePlaytime() +
            dailyPlaytime.getTotalBoxpvpPlaytime() +
            dailyPlaytime.getTotalPrisonPlaytime() +
            dailyPlaytime.getTotalEventsPlaytime());
  }
}