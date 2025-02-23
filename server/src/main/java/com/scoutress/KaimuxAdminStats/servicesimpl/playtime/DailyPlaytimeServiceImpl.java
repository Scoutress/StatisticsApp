package com.scoutress.KaimuxAdminStats.servicesImpl.playtime;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.entity.playtime.SessionDuration;
import com.scoutress.KaimuxAdminStats.repositories.playtime.DailyPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.services.DataExtractingService;
import com.scoutress.KaimuxAdminStats.services.playtime.DailyPlaytimeService;

@Service
public class DailyPlaytimeServiceImpl implements DailyPlaytimeService {

  private final DailyPlaytimeRepository dailyPlaytimeRepository;
  private final DataExtractingService dataExtractingService;

  public DailyPlaytimeServiceImpl(
      DailyPlaytimeRepository dailyPlaytimeRepository,
      DataExtractingService dataExtractingService) {
    this.dailyPlaytimeRepository = dailyPlaytimeRepository;
    this.dataExtractingService = dataExtractingService;
  }

  @Override
  public void handleDailyPlaytime() {
    List<SessionDuration> allSessions = dataExtractingService.getSessionDurations();
    List<DailyPlaytime> dailyPlaytimeData = calculateDailyPlaytime(allSessions);
    saveCalculatedPlaytime(dailyPlaytimeData);
  }

  public List<DailyPlaytime> calculateDailyPlaytime(List<SessionDuration> sessions) {
    List<DailyPlaytime> handledDailyPlaytimeData = new ArrayList<>();

    Set<Short> allEmployeeIds = sessions
        .stream()
        .map(SessionDuration::getEmployeeId)
        .collect(Collectors.toSet());

    Set<String> uniqueServers = sessions
        .stream()
        .map(SessionDuration::getServer)
        .collect(Collectors.toSet());

    Set<LocalDate> uniqueDates = sessions
        .stream()
        .map(SessionDuration::getDate)
        .collect(Collectors.toSet());

    for (Short employeeId : allEmployeeIds) {
      for (String server : uniqueServers) {
        for (LocalDate date : uniqueDates) {
          int sessionPlaytimeInSec = sessions
              .stream()
              .filter(session -> session.getEmployeeId().equals(employeeId))
              .filter(session -> session.getServer().equals(server))
              .filter(session -> session.getDate().equals(date))
              .mapToInt(SessionDuration::getSingleSessionDurationInSec)
              .sum();

          if (sessionPlaytimeInSec < 0) {
            throw new IllegalArgumentException("Playtime can not be less than 0!");
          }

          double sessionPlaytimeInHours = sessionPlaytimeInSec / 3600.0;

          if (sessionPlaytimeInSec > 0) {
            DailyPlaytime dailyPlaytimeData = new DailyPlaytime();
            dailyPlaytimeData.setEmployeeId(employeeId);
            dailyPlaytimeData.setServer(server);
            dailyPlaytimeData.setDate(date);
            dailyPlaytimeData.setTimeInHours(sessionPlaytimeInHours);
            handledDailyPlaytimeData.add(dailyPlaytimeData);
          }
        }
      }
    }

    handledDailyPlaytimeData.sort(Comparator.comparing(DailyPlaytime::getDate));

    return handledDailyPlaytimeData;
  }

  public void saveCalculatedPlaytime(List<DailyPlaytime> dailyPlaytimeData) {
    dailyPlaytimeData.forEach(dailyPlaytime -> {
      DailyPlaytime existingPlaytime = dailyPlaytimeRepository.findByEmployeeIdAndDateAndServer(
          dailyPlaytime.getEmployeeId(),
          dailyPlaytime.getDate(),
          dailyPlaytime.getServer());

      if (existingPlaytime != null) {
        if (!existingPlaytime.getTimeInHours().equals(dailyPlaytime.getTimeInHours())) {
          existingPlaytime.setTimeInHours(dailyPlaytime.getTimeInHours());
          dailyPlaytimeRepository.save(existingPlaytime);
        }
      } else {
        dailyPlaytimeRepository.save(dailyPlaytime);
      }
    });
  }

  @Override
  public Double getSumOfPlaytimeByEmployeeIdAndDuration(Short employeeId, Short days) {
    List<DailyPlaytime> rawPlaytimeData = getRawPlaytimeData();
    List<DailyPlaytime> playtimeDataThisEmployee = getPlaytimeDataForThisEmployee(rawPlaytimeData, employeeId);

    return calculatePlaytime(playtimeDataThisEmployee, days);
  }

  public List<DailyPlaytime> getRawPlaytimeData() {
    return dailyPlaytimeRepository.findAll();
  }

  public List<DailyPlaytime> getPlaytimeDataForThisEmployee(
      List<DailyPlaytime> rawPlaytimeData, Short employeeId) {
    return rawPlaytimeData
        .stream()
        .filter(playtimeData -> playtimeData.getEmployeeId().equals(employeeId))
        .collect(Collectors.toList());
  }

  public Double calculatePlaytime(List<DailyPlaytime> playtimeData, Short days) {
    double totalPlaytime = playtimeData
        .stream()
        .filter(data -> data.getDate().isAfter(LocalDate.now().minusDays(days)))
        .mapToDouble(DailyPlaytime::getTimeInHours)
        .sum();

    System.out.println("Playtime: " + totalPlaytime);

    return totalPlaytime;
  }
}
