package com.scoutress.KaimuxAdminStats.Services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.NEW_SessionDataItem;
import com.scoutress.KaimuxAdminStats.Entity.NEW_SessionDuration;
import com.scoutress.KaimuxAdminStats.Repositories.NEW_ProcessedPlaytimeSessionsRepository;

@Service
public class NEW_DataProcessingService {

  private final NEW_DataExtractingService dataExtractingService;
  private final NEW_DataFilterService dataFilterService;
  private final NEW_ProcessedPlaytimeSessionsRepository processedPlaytimeSessionsRepository;

  // temp. admin id's
  private final List<Short> aids = Arrays
      .asList(
          (short) 1,
          (short) 2,
          (short) 3,
          (short) 4,
          (short) 5,
          (short) 6,
          (short) 7,
          (short) 8,
          (short) 9,
          (short) 10);

  private final List<String> serverNames = Arrays
      .asList("survival", "skyblock", "creative", "boxpvp", "prison", "events", "lobby");

  public NEW_DataProcessingService(NEW_DataExtractingService dataExtractingService,
      NEW_DataFilterService dataFilterService,
      NEW_ProcessedPlaytimeSessionsRepository processedPlaytimeSessionsRepository) {
    this.dataExtractingService = dataExtractingService;
    this.dataFilterService = dataFilterService;
    this.processedPlaytimeSessionsRepository = processedPlaytimeSessionsRepository;
  }

  public void calculateSingleSessionTime() {
    List<NEW_SessionDataItem> allSessions = dataExtractingService
        .getLoginLogoutTimes();

    for (Short aid : aids) {
      List<NEW_SessionDataItem> allSessionsById = dataFilterService
          .sessionsFilterByAid(allSessions, aid);

      for (String server : serverNames) {
        List<NEW_SessionDataItem> allSessionsByServer = dataFilterService
            .sessionsFilterByServer(allSessionsById, server);
        List<NEW_SessionDataItem> loginSessions = dataFilterService
            .filterByAction(allSessionsByServer, true);
        List<NEW_SessionDataItem> logoutSessions = dataFilterService
            .filterByAction(allSessionsByServer, false);

        processSessions(aid, server, loginSessions, logoutSessions, allSessionsByServer);
      }
    }
  }

  private void processSessions(
      short aid,
      String server,
      List<NEW_SessionDataItem> loginSessions,
      List<NEW_SessionDataItem> logoutSessions,
      List<NEW_SessionDataItem> allSessionsByServer) {

    for (int i = 0; i < Math.min(loginSessions.size(), logoutSessions.size()); i++) {
      NEW_SessionDataItem login = loginSessions.get(i);
      long loginEpochTime = login.getTime();
      long nextLoginEpochTime = (i + 1 < loginSessions.size())
          ? loginSessions.get(i + 1).getTime()
          : Long.MAX_VALUE;

      List<NEW_SessionDataItem> logoutsInRange = dataFilterService.filterForMultipleLoginsOrLogouts(
          allSessionsByServer,
          false,
          LocalDateTime.ofEpochSecond(loginEpochTime, 0, ZoneOffset.UTC),
          LocalDateTime.ofEpochSecond(nextLoginEpochTime, 0, ZoneOffset.UTC));

      if (logoutsInRange.size() == 1) {
        handleSingleLogout(aid, server, loginEpochTime, logoutSessions.get(i).getTime());
      } else if (!logoutsInRange.isEmpty()) {
        handleMultipleLogouts(aid, server, loginEpochTime, logoutsInRange);
      }
    }
  }

  private void handleSingleLogout(short aid, String server, long loginEpochTime, long logoutEpochTime) {
    LocalDate loginDate = LocalDateTime.ofEpochSecond(loginEpochTime, 0, ZoneOffset.UTC).toLocalDate();
    LocalDate logoutDate = LocalDateTime.ofEpochSecond(logoutEpochTime, 0, ZoneOffset.UTC).toLocalDate();

    if (loginDate.isEqual(logoutDate)) {
      long sessionDuration = logoutEpochTime - loginEpochTime;
      saveSessionDuration(aid, sessionDuration, loginDate, server);
    } else {
      long midnightEpochTime = logoutDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC);
      long sessionDurationTillMidnight = midnightEpochTime - loginEpochTime;
      long sessionDurationAfterMidnight = logoutEpochTime - midnightEpochTime;

      saveSessionDuration(aid, sessionDurationTillMidnight, loginDate, server);
      saveSessionDuration(aid, sessionDurationAfterMidnight, logoutDate, server);
    }
  }

  private void handleMultipleLogouts(short aid, String server, long loginEpochTime,
      List<NEW_SessionDataItem> logoutsInRange) {
    for (NEW_SessionDataItem logout : logoutsInRange) {
      // Logic to handle multiple logouts
      // You might need to determine how to process this scenario
    }
  }

  private void saveSessionDuration(short aid, long sessionDuration, LocalDate date, String server) {
    NEW_SessionDuration session = new NEW_SessionDuration(aid, sessionDuration, date, server);
    processedPlaytimeSessionsRepository.save(session);
  }
}
