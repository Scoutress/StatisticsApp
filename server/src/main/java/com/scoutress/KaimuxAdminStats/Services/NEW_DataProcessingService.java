package com.scoutress.KaimuxAdminStats.Services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

  private List<NEW_SanitizedSessionData> convertToSanitizedSessionData(List<NEW_SessionDataItem> items) {
    return items.stream().map(item -> {
      NEW_SanitizedSessionData sanitizedData = new NEW_SanitizedSessionData();
      sanitizedData.setAid(item.getAid());
      sanitizedData.setTime(item.getTime());
      sanitizedData.setAction(item.isAction());
      sanitizedData.setServer(item.getServer());
      return sanitizedData;
    }).collect(Collectors.toList());
  }

  public void calculateSingleSessionTime() {

    List<NEW_SessionDataItem> allSessions = dataExtractingService
        .getLoginLogoutTimes();

    List<NEW_SanitizedSessionData> sanitizedSessionData = convertToSanitizedSessionData(allSessions);

    dataSanitizationService.filterAndSaveSessions(sanitizedSessionData);

    List<NEW_SanitizedSessionData> sanitizedData = dataExtractingService
        .getSanitizedLoginLogoutTimes();

    for (Short aid : aids) {

      List<NEW_SanitizedSessionData> allSessionsById = dataFilterService
          .sessionsFilterByAid(sanitizedData, aid);

      for (String server : serverNames) {

        List<NEW_SanitizedSessionData> allSessionsByServer = dataFilterService
            .sessionsFilterByServer(allSessionsById, server);

        List<NEW_SanitizedSessionData> loginSessions = dataFilterService
            .filterByAction(allSessionsByServer, true);

        List<NEW_SanitizedSessionData> logoutSessions = dataFilterService
            .filterByAction(allSessionsByServer, false);

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

  private void saveSessionDuration(short aid, long sessionDuration, LocalDate date, String server) {
    NEW_SessionDuration session = new NEW_SessionDuration(aid, sessionDuration, date, server);
    processedPlaytimeSessionsRepository.save(session);
  }
}
