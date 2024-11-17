package com.scoutress.KaimuxAdminStats.servicesImpl.afkPlaytime;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.afkPlaytime.AfkSessionDuration;
import com.scoutress.KaimuxAdminStats.entity.afkPlaytime.DailyAfkPlaytime;
import com.scoutress.KaimuxAdminStats.repositories.afkPlaytime.DailyAfkPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.services.DataExtractingService;
import com.scoutress.KaimuxAdminStats.services.afkPlaytime.DailyAfkPlaytimeService;

@Service
public class DailyAfkPlaytimeServiceImpl implements DailyAfkPlaytimeService {

  private final DailyAfkPlaytimeRepository dailyAfkPlaytimeRepository;
  private final DataExtractingService dataExtractingService;

  public DailyAfkPlaytimeServiceImpl(
      DailyAfkPlaytimeRepository dailyAfkPlaytimeRepository,
      DataExtractingService dataExtractingService) {

    this.dailyAfkPlaytimeRepository = dailyAfkPlaytimeRepository;
    this.dataExtractingService = dataExtractingService;
  }

  @Override
  public void handleDailyAfkPlaytime() {

    List<AfkSessionDuration> allSessions = dataExtractingService.getAfkSessionDurations();

    List<DailyAfkPlaytime> dailyPlaytimeData = calculateDailyPlaytime(allSessions);

    saveCalculatedPlaytime(dailyPlaytimeData);
  }

  public List<DailyAfkPlaytime> calculateDailyPlaytime(List<AfkSessionDuration> sessions) {

    List<DailyAfkPlaytime> handledDailyAfkPlaytimeData = new ArrayList<>();

    Set<Short> uniqueAids = sessions
        .stream()
        .map(AfkSessionDuration::getAid)
        .collect(Collectors.toSet());

    Set<String> uniqueServers = sessions
        .stream()
        .map(AfkSessionDuration::getServer)
        .collect(Collectors.toSet());

    Set<LocalDate> uniqueDates = sessions
        .stream()
        .map(AfkSessionDuration::getDate)
        .collect(Collectors.toSet());

    for (Short aid : uniqueAids) {
      for (String server : uniqueServers) {
        for (LocalDate date : uniqueDates) {
          double sessionPlaytimeInSec = sessions
              .stream()
              .filter(session -> session.getAid().equals(aid))
              .filter(session -> session.getServer().equals(server))
              .filter(session -> session.getDate().equals(date))
              .mapToDouble(AfkSessionDuration::getSingleAfkSessionDuration)
              .sum();

          DailyAfkPlaytime dailyPlaytimeData = new DailyAfkPlaytime();
          dailyPlaytimeData.setAid(aid);
          dailyPlaytimeData.setServer(server);
          dailyPlaytimeData.setDate(date);
          dailyPlaytimeData.setTime(sessionPlaytimeInSec);
          handledDailyAfkPlaytimeData.add(dailyPlaytimeData);
        }
      }
    }

    handledDailyAfkPlaytimeData.sort(Comparator.comparing(DailyAfkPlaytime::getDate));

    return handledDailyAfkPlaytimeData;
  }

  public void saveCalculatedPlaytime(List<DailyAfkPlaytime> dailyAfkPlaytimeData) {
    dailyAfkPlaytimeData.forEach(dailyAfkPlaytimeRepository::save);
  }
}
