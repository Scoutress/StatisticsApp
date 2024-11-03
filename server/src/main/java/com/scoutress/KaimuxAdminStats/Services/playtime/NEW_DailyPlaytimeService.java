package com.scoutress.KaimuxAdminStats.Services.playtime;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.playtime.NEW_DailyPlaytime;
import com.scoutress.KaimuxAdminStats.Entity.playtime.NEW_SessionDuration;
import com.scoutress.KaimuxAdminStats.Repositories.playtime.NEW_DailyPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.Services.NEW_DataExtractingService;

@Service
public class NEW_DailyPlaytimeService {

  private final NEW_DailyPlaytimeRepository dailyPlaytimeRepository;
  private final NEW_DataExtractingService dataExtractingService;

  public NEW_DailyPlaytimeService(
      NEW_DailyPlaytimeRepository dailyPlaytimeRepository,
      NEW_DataExtractingService dataExtractingService) {
    this.dailyPlaytimeRepository = dailyPlaytimeRepository;
    this.dataExtractingService = dataExtractingService;
  }

  public void handleDailyPlaytime() {

    List<NEW_SessionDuration> allSessions = dataExtractingService.getSessionDurations();

    List<NEW_DailyPlaytime> dailyPlaytimeData = calculateDailyPlaytime(allSessions);

    saveCalculatedPlaytime(dailyPlaytimeData);
  }

  public List<NEW_DailyPlaytime> calculateDailyPlaytime(
      List<NEW_SessionDuration> sessions) {

    List<NEW_DailyPlaytime> handledDailyPlaytimeData = new ArrayList<>();

    Set<Short> uniqueAids = sessions
        .stream()
        .map(NEW_SessionDuration::getAid)
        .collect(Collectors.toSet());

    Set<String> uniqueServers = sessions
        .stream()
        .map(NEW_SessionDuration::getServer)
        .collect(Collectors.toSet());

    Set<LocalDate> uniqueDates = sessions
        .stream()
        .map(NEW_SessionDuration::getDate)
        .collect(Collectors.toSet());

    for (Short aid : uniqueAids) {
      for (String server : uniqueServers) {
        for (LocalDate date : uniqueDates) {
          int sessionPlaytimeInSec = sessions
              .stream()
              .filter(session -> session.getAid() == aid)
              .filter(session -> session.getServer().equals(server))
              .filter(session -> session.getDate().equals(date))
              .mapToInt(NEW_SessionDuration::getSingleSessionDuration)
              .sum();

          NEW_DailyPlaytime dailyPlaytimeData = new NEW_DailyPlaytime();
          dailyPlaytimeData.setAid(aid);
          dailyPlaytimeData.setServer(server);
          dailyPlaytimeData.setDate(date);
          dailyPlaytimeData.setTime(sessionPlaytimeInSec);
          handledDailyPlaytimeData.add(dailyPlaytimeData);
        }
      }
    }
    handledDailyPlaytimeData.sort(Comparator.comparing(NEW_DailyPlaytime::getDate));

    return handledDailyPlaytimeData;
  }

  public void saveCalculatedPlaytime(List<NEW_DailyPlaytime> dailyPlaytimeData) {
    dailyPlaytimeData.forEach(dailyPlaytimeRepository::save);
  }
}
