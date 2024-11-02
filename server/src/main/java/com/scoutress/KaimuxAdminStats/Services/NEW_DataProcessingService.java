package com.scoutress.KaimuxAdminStats.Services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.NEW_SanitizedSessionData;
import com.scoutress.KaimuxAdminStats.Entity.NEW_SessionDataItem;
import com.scoutress.KaimuxAdminStats.Entity.NEW_SessionDuration;
import com.scoutress.KaimuxAdminStats.Repositories.NEW_ProcessedPlaytimeSessionsRepository;

@Service
public class NEW_DataProcessingService {

  private final NEW_DataExtractingService dataExtractingService;
  private final NEW_DataFilterService dataFilterService;
  private final NEW_DataSanitizationService dataSanitizationService;
  private final NEW_ProcessedPlaytimeSessionsRepository processedPlaytimeSessionsRepository;

  // TODO: temp. admin id's
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

  public NEW_DataProcessingService(
      NEW_DataExtractingService dataExtractingService,
      NEW_DataFilterService dataFilterService,
      NEW_DataSanitizationService dataSanitizationService,
      NEW_ProcessedPlaytimeSessionsRepository processedPlaytimeSessionsRepository) {
    this.dataExtractingService = dataExtractingService;
    this.dataFilterService = dataFilterService;
    this.dataSanitizationService = dataSanitizationService;
    this.processedPlaytimeSessionsRepository = processedPlaytimeSessionsRepository;
  }

  public void calculateSingleSessionTime() {

    List<NEW_SessionDataItem> allSessions = dataExtractingService
        .getLoginLogoutTimes();

    dataSanitizationService.sanitizeData(allSessions);

    List<NEW_SanitizedSessionData> sanitizedData = dataExtractingService
        .getSanitizedLoginLogoutTimes();

    for (Short aid : aids) {

      List<NEW_SanitizedSessionData> allSessionsById = dataFilterService
          .sessionsSanitizedFilterByAid(sanitizedData, aid);

      for (String server : serverNames) {

        List<NEW_SanitizedSessionData> allSessionsByServer = dataFilterService
            .sessionsSanitizedFilterByServer(allSessionsById, server);

        List<NEW_SanitizedSessionData> loginSessions = dataFilterService
            .sessionsSanitizedFilterByAction(allSessionsByServer, true);

        List<NEW_SanitizedSessionData> logoutSessions = dataFilterService
            .sessionsSanitizedFilterByAction(allSessionsByServer, false);

        processSessions(aid, server, loginSessions, logoutSessions);

      }
    }
  }

  private void processSessions(
      short aid,
      String server,
      List<NEW_SanitizedSessionData> loginSessions,
      List<NEW_SanitizedSessionData> logoutSessions) {

    for (int i = 0; i < Math.min(loginSessions.size(), logoutSessions.size()); i++) {

      NEW_SanitizedSessionData login = loginSessions.get(i);
      NEW_SanitizedSessionData logout = logoutSessions.get(i);

      long loginEpochTime = login.getTime();
      long logoutEpochTime = logout.getTime();

      handleSingleLogout(aid, server, loginEpochTime, logoutEpochTime);
    }
  }

  private void handleSingleLogout(short aid, String server, long loginEpochTime, long logoutEpochTime) {
    LocalDate loginDate = LocalDateTime.ofEpochSecond(loginEpochTime, 0, ZoneOffset.UTC).toLocalDate();
    LocalDate logoutDate = LocalDateTime.ofEpochSecond(logoutEpochTime, 0, ZoneOffset.UTC).toLocalDate();

    if (loginDate.isEqual(logoutDate)) {

      int sessionDuration = (int) (logoutEpochTime - loginEpochTime);
      saveSessionDuration(aid, sessionDuration, loginDate, server);

    } else {

      long midnightEpochTime = logoutDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC);
      int sessionDurationTillMidnight = (int) (midnightEpochTime - loginEpochTime);
      int sessionDurationAfterMidnight = (int) (logoutEpochTime - midnightEpochTime);

      saveSessionDuration(aid, sessionDurationTillMidnight, loginDate, server);
      saveSessionDuration(aid, sessionDurationAfterMidnight, logoutDate, server);

    }
  }

  private void saveSessionDuration(short aid, int sessionDuration, LocalDate date, String server) {
    NEW_SessionDuration session = new NEW_SessionDuration(aid, sessionDuration, date, server);
    processedPlaytimeSessionsRepository.save(session);
  }
}
