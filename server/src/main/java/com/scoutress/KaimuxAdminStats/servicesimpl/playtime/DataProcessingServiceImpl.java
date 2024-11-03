package com.scoutress.KaimuxAdminStats.servicesimpl.playtime;

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

import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;
import com.scoutress.KaimuxAdminStats.entity.playtime.SanitizedSessionData;
import com.scoutress.KaimuxAdminStats.entity.playtime.SessionDataItem;
import com.scoutress.KaimuxAdminStats.entity.playtime.SessionDuration;
import com.scoutress.KaimuxAdminStats.repositories.playtime.ProcessedPlaytimeSessionsRepository;
import com.scoutress.KaimuxAdminStats.services.DataExtractingService;
import com.scoutress.KaimuxAdminStats.services.DataFilterService;
import com.scoutress.KaimuxAdminStats.services.playtime.DataProcessingService;

@Service
public class DataProcessingServiceImpl implements DataProcessingService {

  private final DataExtractingService dataExtractingService;
  private final DataFilterService dataFilterService;
  private final DataSanitizationServiceImpl dataSanitizationService;
  private final ProcessedPlaytimeSessionsRepository processedPlaytimeSessionsRepository;

  public DataProcessingServiceImpl(
      DataExtractingService dataExtractingService,
      DataFilterService dataFilterService,
      DataSanitizationServiceImpl dataSanitizationService,
      ProcessedPlaytimeSessionsRepository processedPlaytimeSessionsRepository) {

    this.dataExtractingService = dataExtractingService;
    this.dataFilterService = dataFilterService;
    this.dataSanitizationService = dataSanitizationService;
    this.processedPlaytimeSessionsRepository = processedPlaytimeSessionsRepository;
  }

  @Override
  public void calculateSingleSessionTime() {
    Map<String, Method> serverIdMethods = initializeServerIdMethods();

    List<SessionDataItem> allSessions = dataExtractingService.getLoginLogoutTimes();

    Set<String> serverNames = allSessions
        .stream()
        .map(SessionDataItem::getServer)
        .collect(Collectors.toSet());

    dataSanitizationService.sanitizeData(allSessions);

    List<SanitizedSessionData> sanitizedData = dataExtractingService.getSanitizedLoginLogoutTimes();

    for (String server : serverNames) {
      List<SanitizedSessionData> allSessionsByServer = dataFilterService
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
        List<SanitizedSessionData> allSessionsById = dataFilterService.filterSanitizedSessionsByAid(sanitizedData,
            aid);
        List<SanitizedSessionData> loginSessions = dataFilterService
            .filterSanitizedSessionsByAction(allSessionsById, true);
        List<SanitizedSessionData> logoutSessions = dataFilterService
            .filterSanitizedSessionsByAction(allSessionsByServer, false);

        processSessions(aid, server, loginSessions, logoutSessions);
      }
    }
  }

  private Map<String, Method> initializeServerIdMethods() {
    Map<String, Method> serverIdMethods = new HashMap<>();
    try {
      serverIdMethods.put("survival", EmployeeCodes.class.getMethod("getSurvivalId"));
      serverIdMethods.put("skyblock", EmployeeCodes.class.getMethod("getSkyblockId"));
      serverIdMethods.put("creative", EmployeeCodes.class.getMethod("getCreativeId"));
      serverIdMethods.put("boxpvp", EmployeeCodes.class.getMethod("getBoxpvpId"));
      serverIdMethods.put("prison", EmployeeCodes.class.getMethod("getPrisonId"));
      serverIdMethods.put("events", EmployeeCodes.class.getMethod("getEventsId"));
      serverIdMethods.put("lobby", EmployeeCodes.class.getMethod("getLobbyId"));
    } catch (NoSuchMethodException e) {
      System.err.println("One or more server ID methods could not be found: " + e.getMessage());
    }
    return serverIdMethods;
  }

  @Override
  public void processSessions(
      short aid,
      String server,
      List<SanitizedSessionData> loginSessions,
      List<SanitizedSessionData> logoutSessions) {

    for (int i = 0; i < Math.min(loginSessions.size(), logoutSessions.size()); i++) {
      SanitizedSessionData login = loginSessions.get(i);
      SanitizedSessionData logout = logoutSessions.get(i);

      long loginEpochTime = login.getTime();
      long logoutEpochTime = logout.getTime();

      handleSingleLogout(aid, server, loginEpochTime, logoutEpochTime);
    }
  }

  @Override
  public void handleSingleLogout(short aid, String server, long loginEpochTime, long logoutEpochTime) {
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

  @Override
  public void saveSessionDuration(short aid, int sessionDuration, LocalDate date, String server) {
    SessionDuration session = new SessionDuration(aid, sessionDuration, date, server);
    processedPlaytimeSessionsRepository.save(session);
  }
}
