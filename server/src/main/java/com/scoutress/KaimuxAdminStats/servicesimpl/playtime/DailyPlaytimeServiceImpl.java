package com.scoutress.KaimuxAdminStats.servicesImpl.playtime;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.entity.playtime.SessionDuration;
import com.scoutress.KaimuxAdminStats.repositories.playtime.DailyPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.ProcessedPlaytimeSessionsRepository;
import com.scoutress.KaimuxAdminStats.services.playtime.DailyPlaytimeService;

@Service
public class DailyPlaytimeServiceImpl implements DailyPlaytimeService {

  private static final Logger logger = LoggerFactory.getLogger(SessionDurationServiceImpl.class);

  private final DailyPlaytimeRepository dailyPlaytimeRepository;
  private final ProcessedPlaytimeSessionsRepository processedPlaytimeSessionsRepository;

  public DailyPlaytimeServiceImpl(
      DailyPlaytimeRepository dailyPlaytimeRepository,
      ProcessedPlaytimeSessionsRepository processedPlaytimeSessionsRepository) {
    this.dailyPlaytimeRepository = dailyPlaytimeRepository;
    this.processedPlaytimeSessionsRepository = processedPlaytimeSessionsRepository;
  }

  @Override
  public void handleDailyPlaytime() {
    List<SessionDuration> allSessions = getSessionDurations();
    List<DailyPlaytime> dailyPlaytimeData = calculateDailyPlaytime(allSessions);
    saveCalculatedPlaytime(dailyPlaytimeData);
  }

  private List<SessionDuration> getSessionDurations() {
    return processedPlaytimeSessionsRepository.findAll();
  }

  private List<DailyPlaytime> calculateDailyPlaytime(List<SessionDuration> sessions) {
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

  private void saveCalculatedPlaytime(List<DailyPlaytime> dailyPlaytimeData) {
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
  public void removeDuplicateDailyPlaytimes() {
    List<DailyPlaytime> allDailyPlaytimes = getDailyPlaytimes();
    Map<String, List<DailyPlaytime>> groupedDailyPlaytimes = groupDailyPlaytimesByUniqueFields(allDailyPlaytimes);
    removeDuplicateDailyPlaytimes(groupedDailyPlaytimes);
  }

  private List<DailyPlaytime> getDailyPlaytimes() {
    return dailyPlaytimeRepository.findAll();
  }

  private Map<String, List<DailyPlaytime>> groupDailyPlaytimesByUniqueFields(List<DailyPlaytime> allDailyPlaytimes) {
    return allDailyPlaytimes
        .stream()
        .collect(Collectors.groupingBy(playtime -> playtime.getEmployeeId() + "-" +
            playtime.getTimeInHours() + "-" +
            playtime.getDate() + "-" +
            playtime.getServer()));
  }

  private void removeDuplicateDailyPlaytimes(Map<String, List<DailyPlaytime>> groupedDailyPlaytimes) {
    int totalDuplicatesRemoved = 0;

    for (List<DailyPlaytime> group : groupedDailyPlaytimes.values()) {
      if (group.size() > 1) {
        totalDuplicatesRemoved += group.size() - 1;

        logger.info("Removing {} duplicates from group: {}", group.size() - 1, group.get(0));

        group.subList(1, group.size()).forEach(session -> {
          logger.debug("Removing session: {}", session);
          dailyPlaytimeRepository.delete(session);
        });
      }
    }

    logger.info("Total duplicates removed: {}", totalDuplicatesRemoved);
  }

  @Override
  public Double getSumOfPlaytimeByEmployeeIdAndDuration(Short employeeId, Short days) {
    List<DailyPlaytime> rawPlaytimeData = getRawPlaytimeData();
    List<DailyPlaytime> playtimeDataThisEmployee = getPlaytimeDataForThisEmployee(rawPlaytimeData, employeeId);

    return calculatePlaytime(playtimeDataThisEmployee, days);
  }

  private List<DailyPlaytime> getRawPlaytimeData() {
    return dailyPlaytimeRepository.findAll();
  }

  private List<DailyPlaytime> getPlaytimeDataForThisEmployee(
      List<DailyPlaytime> rawPlaytimeData, Short employeeId) {
    return rawPlaytimeData
        .stream()
        .filter(playtimeData -> playtimeData.getEmployeeId().equals(employeeId))
        .collect(Collectors.toList());
  }

  private Double calculatePlaytime(List<DailyPlaytime> playtimeData, Short days) {
    return playtimeData
        .stream()
        .filter(data -> data.getDate().isAfter(LocalDate.now().minusDays(days)))
        .mapToDouble(DailyPlaytime::getTimeInHours)
        .sum();
  }
}
