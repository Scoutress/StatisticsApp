package com.scoutress.KaimuxAdminStats.Services.playtime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.employees.NEW_EmployeeCodes;
import com.scoutress.KaimuxAdminStats.Entity.playtime.NEW_SanitizedSessionData;
import com.scoutress.KaimuxAdminStats.Entity.playtime.NEW_SessionDataItem;
import com.scoutress.KaimuxAdminStats.Entity.playtime.NEW_SessionDuration;
import com.scoutress.KaimuxAdminStats.Repositories.playtime.NEW_ProcessedPlaytimeSessionsRepository;
import com.scoutress.KaimuxAdminStats.Services.NEW_DataExtractingService;
import com.scoutress.KaimuxAdminStats.Services.NEW_DataFilterService;

@Service
public class NEW_DataProcessingService {

  private final NEW_DataExtractingService dataExtractingService;
  private final NEW_DataFilterService dataFilterService;
  private final NEW_DataSanitizationService dataSanitizationService;
  private final NEW_ProcessedPlaytimeSessionsRepository processedPlaytimeSessionsRepository;

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
    Map<String, Method> serverIdMethods = initializeServerIdMethods();

    List<NEW_SessionDataItem> allSessions = dataExtractingService.getLoginLogoutTimes();

    Set<String> serverNames = allSessions
        .stream()
        .map(NEW_SessionDataItem::getServer)
        .collect(Collectors.toSet());

    dataSanitizationService.sanitizeData(allSessions);

    List<NEW_SanitizedSessionData> sanitizedData = dataExtractingService.getSanitizedLoginLogoutTimes();

    for (String server : serverNames) {
      List<NEW_SanitizedSessionData> allSessionsByServer = dataFilterService
          .filterSanitizedSessionsByServer(sanitizedData, server);

      Method serverIdMethod = serverIdMethods.get(server);
      if (serverIdMethod == null) {
        System.err.println("Server ID method not found for server: " + server);
        continue;
      }

      Set<Short> allAidsByServer = allSessionsByServer
          .stream()
          .map(employee -> {
            try {
              return (Short) serverIdMethod.invoke(employee);
            } catch (IllegalAccessException | InvocationTargetException e) {
              return null;
            }
          })
          .filter(Objects::nonNull)
          .collect(Collectors.toSet());

      for (Short aid : allAidsByServer) {
        List<NEW_SanitizedSessionData> allSessionsById = dataFilterService.filterSanitizedSessionsByAid(sanitizedData,
            aid);
        List<NEW_SanitizedSessionData> loginSessions = dataFilterService
            .filterSanitizedSessionsByAction(allSessionsById, true);
        List<NEW_SanitizedSessionData> logoutSessions = dataFilterService
            .filterSanitizedSessionsByAction(allSessionsByServer, false);

        processSessions(aid, server, loginSessions, logoutSessions);
      }
    }
  }

  private Map<String, Method> initializeServerIdMethods() {
    Map<String, Method> serverIdMethods = new HashMap<>();
    try {
      serverIdMethods.put("survival", NEW_EmployeeCodes.class.getMethod("getSurvivalId"));
      serverIdMethods.put("skyblock", NEW_EmployeeCodes.class.getMethod("getSkyblockId"));
      serverIdMethods.put("creative", NEW_EmployeeCodes.class.getMethod("getCreativeId"));
      serverIdMethods.put("boxpvp", NEW_EmployeeCodes.class.getMethod("getBoxpvpId"));
      serverIdMethods.put("prison", NEW_EmployeeCodes.class.getMethod("getPrisonId"));
      serverIdMethods.put("events", NEW_EmployeeCodes.class.getMethod("getEventsId"));
      serverIdMethods.put("lobby", NEW_EmployeeCodes.class.getMethod("getLobbyId"));
    } catch (NoSuchMethodException e) {
      System.err.println("One or more server ID methods could not be found: " + e.getMessage());
    }
    return serverIdMethods;
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
