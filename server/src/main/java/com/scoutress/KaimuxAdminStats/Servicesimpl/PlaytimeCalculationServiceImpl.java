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
    // 1. Gaukite visus darbuotojus
    List<Employee> employees = employeeRepository.findAll();

    for (Employee employee : employees) {
      // 2. Gaukite visus prisijungimo/atsijungimo laikus šiam darbuotojui
      List<LoginLogoutTimes> loginLogoutTimes = loginLogoutTimesRepository.findByEmployeeId(employee.getId());

      // 3. Grupavimas pagal serverį
      Map<String, Map<LocalDate, Double>> playtimePerServer = new HashMap<>();

      for (LoginLogoutTimes times : loginLogoutTimes) {
        LocalDateTime loginTime = times.getLoginTime();
        LocalDateTime logoutTime = times.getLogoutTime();
        String serverName = times.getServerName();

        // 4. Patikrinkime, ar sesija kerta vidurnaktį
        if (!loginTime.toLocalDate().equals(logoutTime.toLocalDate())) {
          // Pirmoji dienos dalis
          LocalDate firstDay = loginTime.toLocalDate();
          LocalTime midnight = LocalTime.MIDNIGHT;
          LocalDateTime midnightTime = LocalDateTime.of(firstDay.plusDays(1), midnight);
          Duration firstDuration = Duration.between(loginTime, midnightTime);

          playtimePerServer.computeIfAbsent(serverName, k -> new HashMap<>())
              .merge(firstDay, (double) firstDuration.toMinutes() / 60, Double::sum);

          // Antroji dienos dalis
          LocalDate secondDay = logoutTime.toLocalDate();
          Duration secondDuration = Duration.between(midnightTime, logoutTime);

          playtimePerServer.computeIfAbsent(serverName, k -> new HashMap<>())
              .merge(secondDay, (double) secondDuration.toMinutes() / 60, Double::sum);
        } else {
          // Jei nėra vidurnakčio kirtimo
          Duration duration = Duration.between(loginTime, logoutTime);
          playtimePerServer.computeIfAbsent(serverName, k -> new HashMap<>())
              .merge(loginTime.toLocalDate(), (double) duration.toMinutes() / 60, Double::sum);
        }
      }

      // 5. Sukurkite ir išsaugokite `DailyPlaytime` objektą kiekvienai dienai
      for (Map.Entry<String, Map<LocalDate, Double>> serverEntry : playtimePerServer.entrySet()) {
        String serverName = serverEntry.getKey();
        for (Map.Entry<LocalDate, Double> dayEntry : serverEntry.getValue().entrySet()) {
          LocalDate date = dayEntry.getKey();
          Double playtime = dayEntry.getValue();

          // Patikrinkime, ar `DailyPlaytime` įrašas jau egzistuoja
          DailyPlaytime dailyPlaytime = dailyPlaytimeRepository.findByEmployeeIdAndDate(employee.getId(), date);

          if (dailyPlaytime == null) {
            // Jei įrašas nėra, sukurkite naują
            dailyPlaytime = new DailyPlaytime();
            dailyPlaytime.setEmployeeId(employee.getId());
            dailyPlaytime.setDate(date);
            // Inicializuokite visas vertes į 0.0
            initializeDailyPlaytime(dailyPlaytime);
          }

          // Atnaujinkite arba nustatykite playtime duomenis
          updatePlaytimeForServer(dailyPlaytime, serverName, playtime);

          // Išsaugokite įrašą
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

    // Suminė reikšmė, kuri susumuoja visas atskirų serverių žaidimo trukmes
    dailyPlaytime.setTotalPlaytime(
        dailyPlaytime.getTotalSurvivalPlaytime() +
            dailyPlaytime.getTotalSkyblockPlaytime() +
            dailyPlaytime.getTotalCreativePlaytime() +
            dailyPlaytime.getTotalBoxpvpPlaytime() +
            dailyPlaytime.getTotalPrisonPlaytime() +
            dailyPlaytime.getTotalEventsPlaytime());
  }
}