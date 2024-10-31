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
          .sessionsFilterByAid(allSessions, (short) 1);

      for (String server : serverNames) {

        List<NEW_SessionDataItem> allSessionsByIdByServer = dataFilterService
            .sessionsFilterByServer(allSessionsById, server);

        List<NEW_SessionDataItem> loginSessions = dataFilterService
            .filterByAction(allSessionsByIdByServer, true);

        List<NEW_SessionDataItem> logoutSessions = dataFilterService
            .filterByAction(allSessionsByIdByServer, false);

        for (int i = 0; i < Math.min(loginSessions.size(), logoutSessions.size()); i++) {
          NEW_SessionDataItem login = loginSessions.get(i);
          NEW_SessionDataItem logout = logoutSessions.get(i);

          long loginEpochTime = login.getTime();
          long logoutEpochTime = logout.getTime();

          LocalDate loginDate = LocalDateTime.ofEpochSecond(loginEpochTime, 0, ZoneOffset.UTC).toLocalDate();
          LocalDate logoutDate = LocalDateTime.ofEpochSecond(logoutEpochTime, 0, ZoneOffset.UTC).toLocalDate();

          long midnightEpochTime = logoutDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC);

          if (loginDate.isEqual(logoutDate)) {
            long sessionDuration = logoutEpochTime - loginEpochTime;

            NEW_SessionDuration session = new NEW_SessionDuration(
                aid, sessionDuration, loginDate, server);

            processedPlaytimeSessionsRepository.save(session);
          } else {
            long sessionDurationTillMidnight = midnightEpochTime - loginEpochTime;
            long sessionDurationAfterMidnight = logoutEpochTime - midnightEpochTime;

            NEW_SessionDuration sessionTillMidnight = new NEW_SessionDuration(
                aid, sessionDurationTillMidnight, loginDate, server);
            NEW_SessionDuration sessionAfterMidnight = new NEW_SessionDuration(
                aid, sessionDurationAfterMidnight, loginDate, server);

            processedPlaytimeSessionsRepository.save(sessionTillMidnight);
            processedPlaytimeSessionsRepository.save(sessionAfterMidnight);
          }

          // TODO:
          // I need also to add check for errors in DB with double logins or logouts.
          // Also check for only login time.
        }
      }
    }
  }
}
