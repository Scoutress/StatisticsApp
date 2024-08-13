package com.scoutress.KaimuxAdminStats.Servicesimpl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.Employees.Employee;
import com.scoutress.KaimuxAdminStats.Entity.Playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.Entity.Playtime.LoginLogoutTimes;
import com.scoutress.KaimuxAdminStats.Repositories.DailyPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.Repositories.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.Repositories.LoginLogoutTimesRepository;
import com.scoutress.KaimuxAdminStats.Services.PlaytimeCalculationService;

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
      List<LoginLogoutTimes> loginLogoutTimes = loginLogoutTimesRepository.findByEmployeeId(employee.getId());
      Map<String, Map<LocalDate, Double>> playtimePerServer = new HashMap<>();

      for (LoginLogoutTimes times : loginLogoutTimes) {
        String serverName = times.getServerName();
        LocalDateTime loginTime = times.getLoginTime();
        LocalDateTime logoutTime = times.getLogoutTime();
        long minutesPlayed = Duration.between(loginTime, logoutTime).toMinutes();
        double hoursPlayed = minutesPlayed / 60.0;

        System.out.println("Processing server: " + serverName + ", playtime: " + hoursPlayed + " hours");

        playtimePerServer.computeIfAbsent(serverName, k -> new HashMap<>())
            .merge(loginTime.toLocalDate(), hoursPlayed, Double::sum);
      }

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

          System.out.println("Before update: Survival playtime: " + dailyPlaytime.getTotalSurvivalPlaytime());
          System.out
              .println("Updating playtime for server: " + serverName + ", date: " + date + ", playtime: " + playtime);
          updatePlaytimeForServer(dailyPlaytime, serverName, playtime);

          System.out.println("After update: Survival playtime: " + dailyPlaytime.getTotalSurvivalPlaytime());
          dailyPlaytimeRepository.save(dailyPlaytime);
          System.out
              .println("Saved playtime for server: " + serverName + ", date: " + date + ", playtime: " + playtime);
        }
      }
    }
  }

  @Override
  public void updatePlaytimeForServer(DailyPlaytime dailyPlaytime, String serverName, Double playtime) {
    if (dailyPlaytime == null || playtime == null) {
      return;
    }

    if (dailyPlaytime.getTotalSurvivalPlaytime() == null) {
      dailyPlaytime.setTotalSurvivalPlaytime(0.0);
    }
    if (dailyPlaytime.getTotalSkyblockPlaytime() == null) {
      dailyPlaytime.setTotalSkyblockPlaytime(0.0);
    }
    if (dailyPlaytime.getTotalCreativePlaytime() == null) {
      dailyPlaytime.setTotalCreativePlaytime(0.0);
    }
    if (dailyPlaytime.getTotalBoxpvpPlaytime() == null) {
      dailyPlaytime.setTotalBoxpvpPlaytime(0.0);
    }
    if (dailyPlaytime.getTotalPrisonPlaytime() == null) {
      dailyPlaytime.setTotalPrisonPlaytime(0.0);
    }
    if (dailyPlaytime.getTotalEventsPlaytime() == null) {
      dailyPlaytime.setTotalEventsPlaytime(0.0);
    }

    switch (serverName.toLowerCase()) {
      case "survival" -> {
        System.out.println("Updating Survival playtime...");
        double currentSurvivalPlaytime = dailyPlaytime.getTotalSurvivalPlaytime();
        dailyPlaytime.setTotalSurvivalPlaytime(playtime + currentSurvivalPlaytime);
        System.out.println("New Survival playtime: " + dailyPlaytime.getTotalSurvivalPlaytime());
      }
      case "skyblock" -> {
        System.out.println("Updating Skyblock playtime...");
        double currentSkyblockPlaytime = dailyPlaytime.getTotalSkyblockPlaytime();
        dailyPlaytime.setTotalSkyblockPlaytime(playtime + currentSkyblockPlaytime);
        System.out.println("New Skyblock playtime: " + dailyPlaytime.getTotalSkyblockPlaytime());
      }
      case "creative" -> {
        System.out.println("Updating Creative playtime...");
        double currentCreativePlaytime = dailyPlaytime.getTotalCreativePlaytime();
        dailyPlaytime.setTotalCreativePlaytime(playtime + currentCreativePlaytime);
        System.out.println("New Creative playtime: " + dailyPlaytime.getTotalCreativePlaytime());
      }
      case "boxpvp" -> {
        System.out.println("Updating BoxPVP playtime...");
        double currentBoxpvpPlaytime = dailyPlaytime.getTotalBoxpvpPlaytime();
        dailyPlaytime.setTotalBoxpvpPlaytime(playtime + currentBoxpvpPlaytime);
        System.out.println("New BoxPVP playtime: " + dailyPlaytime.getTotalBoxpvpPlaytime());
      }
      case "prison" -> {
        System.out.println("Updating Prison playtime...");
        double currentPrisonPlaytime = dailyPlaytime.getTotalPrisonPlaytime();
        dailyPlaytime.setTotalPrisonPlaytime(playtime + currentPrisonPlaytime);
        System.out.println("New Prison playtime: " + dailyPlaytime.getTotalPrisonPlaytime());
      }
      case "events" -> {
        System.out.println("Updating Events playtime...");
        double currentEventsPlaytime = dailyPlaytime.getTotalEventsPlaytime();
        dailyPlaytime.setTotalEventsPlaytime(playtime + currentEventsPlaytime);
        System.out.println("New Events playtime: " + dailyPlaytime.getTotalEventsPlaytime());
      }
      default -> {
        System.out.println("Server name not recognized: " + serverName);
        return;
      }
    }

    double totalPlaytime = dailyPlaytime.getTotalSurvivalPlaytime() +
        dailyPlaytime.getTotalSkyblockPlaytime() +
        dailyPlaytime.getTotalCreativePlaytime() +
        dailyPlaytime.getTotalBoxpvpPlaytime() +
        dailyPlaytime.getTotalPrisonPlaytime() +
        dailyPlaytime.getTotalEventsPlaytime();

    dailyPlaytime.setTotalPlaytime(totalPlaytime);
    System.out.println("Total playtime updated: " + dailyPlaytime.getTotalPlaytime());
  }

}