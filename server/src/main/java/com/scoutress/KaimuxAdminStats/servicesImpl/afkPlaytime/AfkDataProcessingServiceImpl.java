package com.scoutress.KaimuxAdminStats.servicesImpl.afkPlaytime;

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

import com.scoutress.KaimuxAdminStats.entity.afkPlaytime.AfkPlaytimeRawData;
import com.scoutress.KaimuxAdminStats.entity.afkPlaytime.SanitizedAfkSessionData;
import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;
import com.scoutress.KaimuxAdminStats.entity.playtime.SessionDuration;
import com.scoutress.KaimuxAdminStats.repositories.playtime.ProcessedPlaytimeSessionsRepository;
import com.scoutress.KaimuxAdminStats.services.DataExtractingService;
import com.scoutress.KaimuxAdminStats.services.DataFilterService;
import com.scoutress.KaimuxAdminStats.services.afkPlaytime.AfkDataProcessingService;
import com.scoutress.KaimuxAdminStats.services.afkPlaytime.AfkPlaytimeDataSanitizationService;

@Service
public class AfkDataProcessingServiceImpl implements AfkDataProcessingService {

  private final DataExtractingService dataExtractingService;
  private final DataFilterService dataFilterService;
  private final AfkPlaytimeDataSanitizationService afkPlaytimeDataSanitizationService;
  private final ProcessedPlaytimeSessionsRepository processedPlaytimeSessionsRepository;

  public AfkDataProcessingServiceImpl(
      DataExtractingService dataExtractingService,
      DataFilterService dataFilterService,
      AfkPlaytimeDataSanitizationService afkPlaytimeDataSanitizationService,
      ProcessedPlaytimeSessionsRepository processedPlaytimeSessionsRepository) {

    this.dataExtractingService = dataExtractingService;
    this.dataFilterService = dataFilterService;
    this.afkPlaytimeDataSanitizationService = afkPlaytimeDataSanitizationService;
    this.processedPlaytimeSessionsRepository = processedPlaytimeSessionsRepository;
  }

  @Override
  public void calculateSingleAfkSessionTime() {
    Map<String, Method> serverIdMethods = initializeServerIdMethods();

    List<AfkPlaytimeRawData> allAfkSessions = dataExtractingService.getStartFinishTimes();

    Set<String> serverNames = allAfkSessions
        .stream()
        .map(AfkPlaytimeRawData::getServer)
        .collect(Collectors.toSet());

    afkPlaytimeDataSanitizationService.sanitizeData(allAfkSessions);

    List<SanitizedAfkSessionData> sanitizedData = dataExtractingService.getSanitizedAfkStartFinishTimes();

    for (String server : serverNames) {
      List<SanitizedAfkSessionData> allSessionsByServer = dataFilterService
          .filterSanitizedAfkSessionsByServer(sanitizedData, server);

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
        List<SanitizedAfkSessionData> allSessionsById = dataFilterService
            .filterSanitizedAfkSessionsByAid(sanitizedData, aid);
        List<SanitizedAfkSessionData> loginSessions = dataFilterService
            .filterSanitizedAfkSessionsByAction(allSessionsById, true);
        List<SanitizedAfkSessionData> logoutSessions = dataFilterService
            .filterSanitizedAfkSessionsByAction(allSessionsByServer, false);

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

  public void processSessions(
      short aid,
      String server,
      List<SanitizedAfkSessionData> startSessions,
      List<SanitizedAfkSessionData> finishSessions) {

    for (int i = 0; i < Math.min(startSessions.size(), finishSessions.size()); i++) {
      SanitizedAfkSessionData start = startSessions.get(i);
      SanitizedAfkSessionData finish = finishSessions.get(i);

      long startEpochTime = start.getTime();
      long finishEpochTime = finish.getTime();

      handleSingleFinish(aid, server, startEpochTime, finishEpochTime);
    }
  }

  public void handleSingleFinish(short aid, String server, long startEpochTime, long finishEpochTime) {
    LocalDate startDate = LocalDateTime.ofEpochSecond(startEpochTime, 0, ZoneOffset.UTC).toLocalDate();
    LocalDate finishDate = LocalDateTime.ofEpochSecond(finishEpochTime, 0, ZoneOffset.UTC).toLocalDate();

    if (startDate.isEqual(finishDate)) {
      int sessionDuration = (int) (finishEpochTime - startEpochTime);
      saveSessionDuration(aid, sessionDuration, startDate, server);
    } else {
      long midnightEpochTime = finishDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC);
      int sessionDurationTillMidnight = (int) (midnightEpochTime - startEpochTime);
      int sessionDurationAfterMidnight = (int) (finishEpochTime - midnightEpochTime);

      saveSessionDuration(aid, sessionDurationTillMidnight, startDate, server);
      saveSessionDuration(aid, sessionDurationAfterMidnight, finishDate, server);
    }
  }

  public void saveSessionDuration(short aid, int sessionDuration, LocalDate date, String server) {
    SessionDuration session = new SessionDuration(aid, sessionDuration, date, server);
    processedPlaytimeSessionsRepository.save(session);
  }
}
