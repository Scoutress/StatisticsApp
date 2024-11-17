package com.scoutress.KaimuxAdminStats.servicesImpl.afkPlaytime;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.afkPlaytime.AfkPlaytimeRawData;
import com.scoutress.KaimuxAdminStats.entity.afkPlaytime.SanitizedAfkSessionData;
import com.scoutress.KaimuxAdminStats.repositories.afkPlaytime.SanitizedAfkSessionDataRepository;
import com.scoutress.KaimuxAdminStats.services.DataFilterService;
import com.scoutress.KaimuxAdminStats.services.afkPlaytime.AfkPlaytimeDataSanitizationService;

@Service
public class AfkPlaytimeDataSanitizationServiceImpl implements AfkPlaytimeDataSanitizationService {

  private final SanitizedAfkSessionDataRepository sanitizedAfkSessionDataRepository;
  private final DataFilterService dataFilterService;

  public AfkPlaytimeDataSanitizationServiceImpl(
      SanitizedAfkSessionDataRepository sanitizedAfkSessionDataRepository,
      DataFilterService dataFilterService) {

    this.sanitizedAfkSessionDataRepository = sanitizedAfkSessionDataRepository;
    this.dataFilterService = dataFilterService;
  }

  @Override
  public void sanitizeData(List<AfkPlaytimeRawData> dataItems) {

    Set<Short> uniqueAids = dataItems
        .stream()
        .map(AfkPlaytimeRawData::getAid)
        .collect(Collectors.toSet());

    for (Short aid : uniqueAids) {

      List<AfkPlaytimeRawData> sessionsForAid = dataFilterService.filterAfkSessionsByAid(dataItems, aid);

      Set<String> uniqueServers = sessionsForAid
          .stream()
          .map(AfkPlaytimeRawData::getServer)
          .collect(Collectors.toSet());

      for (String server : uniqueServers) {

        List<AfkPlaytimeRawData> sessionsForServer = dataFilterService.filterAfkSessionsByServer(
            sessionsForAid, server);

        List<AfkPlaytimeRawData> startSessions = dataFilterService.filterAfkSessionsByAction(
            sessionsForServer, true);

        List<AfkPlaytimeRawData> finishSessions = dataFilterService.filterAfkSessionsByAction(
            sessionsForServer, false);

        List<SanitizedAfkSessionData> sanitizedData = sanitizeSessions(startSessions, finishSessions);

        saveSanitizedData(sanitizedData);
      }
    }
  }

  @Override
  public List<SanitizedAfkSessionData> sanitizeSessions(
      List<AfkPlaytimeRawData> startSessions,
      List<AfkPlaytimeRawData> finishSessions) {

    List<SanitizedAfkSessionData> sanitizedSessions = new ArrayList<>();

    List<AfkPlaytimeRawData> logoutsDataWithoutEarlyLogouts = removeEarlyAfkStarts(startSessions, finishSessions);

    List<AfkPlaytimeRawData> sessionsWithFilteredLogouts = removeDuplicateAfkFinishes(startSessions,
        logoutsDataWithoutEarlyLogouts);

    List<AfkPlaytimeRawData> sessionsWithFilteredLoginsAndLogouts = removeDuplicateAfkStarts(
        sessionsWithFilteredLogouts);

    List<AfkPlaytimeRawData> sessionsWithRemovedLateLogins = removeLateAfkStarts(sessionsWithFilteredLoginsAndLogouts);

    for (AfkPlaytimeRawData session : sessionsWithRemovedLateLogins) {
      if (session.getActionAsBoolean()) {
        startSessions.add(session);
      } else {
        finishSessions.add(session);
      }
    }

    for (AfkPlaytimeRawData login : startSessions) {
      SanitizedAfkSessionData sanitizedData = new SanitizedAfkSessionData();
      sanitizedData.setAid(login.getAid());
      sanitizedData.setTime(login.getTime());
      sanitizedData.setAction(true);
      sanitizedData.setServer(login.getServer());
      sanitizedSessions.add(sanitizedData);
    }

    for (AfkPlaytimeRawData logout : finishSessions) {
      SanitizedAfkSessionData sanitizedData = new SanitizedAfkSessionData();
      sanitizedData.setAid(logout.getAid());
      sanitizedData.setTime(logout.getTime());
      sanitizedData.setAction(false);
      sanitizedData.setServer(logout.getServer());
      sanitizedSessions.add(sanitizedData);
    }

    sanitizedSessions.sort(Comparator.comparingLong(SanitizedAfkSessionData::getTime));

    return removeDuplicates(sanitizedSessions);
  }

  @Override
  public List<AfkPlaytimeRawData> removeEarlyAfkStarts(
      List<AfkPlaytimeRawData> startSessions,
      List<AfkPlaytimeRawData> finishSessions) {

    if (!startSessions.isEmpty() && !finishSessions.isEmpty()) {
      long firstLoginTime = startSessions.get(0).getTime();

      List<AfkPlaytimeRawData> filteredLogouts = finishSessions
          .stream()
          .filter(logout -> logout.getTime() > firstLoginTime)
          .collect(Collectors.toList());

      return filteredLogouts;
    }

    return finishSessions;
  }

  @Override
  public List<AfkPlaytimeRawData> removeDuplicateAfkFinishes(
      List<AfkPlaytimeRawData> startSessions,
      List<AfkPlaytimeRawData> finishSessions) {

    List<AfkPlaytimeRawData> processedLogins = new ArrayList<>();
    List<AfkPlaytimeRawData> processedLogouts = new ArrayList<>();

    if (startSessions.isEmpty()) {
      return processedLogins;
    }

    for (int i = 0; i < startSessions.size(); i++) {
      AfkPlaytimeRawData currentLogin = startSessions.get(i);
      processedLogins.add(currentLogin);

      if (i < startSessions.size() - 1) {
        AfkPlaytimeRawData nextLogin = startSessions.get(i + 1);
        AfkPlaytimeRawData logoutBetweenTwoLogins = finishSessions
            .stream()
            .filter(logouts -> logouts.getTime() > currentLogin.getTime() && logouts.getTime() < nextLogin.getTime())
            .max(Comparator.comparingLong(AfkPlaytimeRawData::getTime))
            .orElse(null);

        if (logoutBetweenTwoLogins != null) {
          processedLogouts.add(logoutBetweenTwoLogins);
        }
      } else {
        AfkPlaytimeRawData logoutAfterLastLogin = finishSessions
            .stream()
            .filter(logouts -> logouts.getTime() > currentLogin.getTime())
            .findFirst()
            .orElse(null);

        if (logoutAfterLastLogin != null) {
          processedLogouts.add(logoutAfterLastLogin);
        }
      }
    }

    List<AfkPlaytimeRawData> combinedResult = new ArrayList<>(processedLogins);
    combinedResult.addAll(processedLogouts);
    combinedResult.sort(Comparator.comparingLong(AfkPlaytimeRawData::getTime));

    return combinedResult;
  }

  @Override
  public List<AfkPlaytimeRawData> removeDuplicateAfkStarts(
      List<AfkPlaytimeRawData> sessions) {
    List<AfkPlaytimeRawData> processedLogins = new ArrayList<>();
    List<AfkPlaytimeRawData> processedLogouts = new ArrayList<>();
    List<AfkPlaytimeRawData> startSessions = new ArrayList<>();
    List<AfkPlaytimeRawData> finishSessions = new ArrayList<>();

    for (AfkPlaytimeRawData session : sessions) {
      if (session.getActionAsBoolean()) {
        startSessions.add(session);
      } else {
        finishSessions.add(session);
      }
    }

    if (finishSessions.isEmpty()) {
      return processedLogouts;
    }

    startSessions.sort(Comparator.comparingLong(AfkPlaytimeRawData::getTime));
    finishSessions.sort(Comparator.comparingLong(AfkPlaytimeRawData::getTime));

    for (int i = 0; i < finishSessions.size(); i++) {
      AfkPlaytimeRawData currentLogout = finishSessions.get(i);
      processedLogouts.add(currentLogout);

      if (i < finishSessions.size() - 1) {
        AfkPlaytimeRawData nextLogout = finishSessions.get(i + 1);

        AfkPlaytimeRawData loginBetweenTwoLogouts = startSessions
            .stream()
            .filter(logins -> logins.getTime() > currentLogout.getTime())
            .filter(logins -> logins.getTime() < nextLogout.getTime())
            .min(Comparator.comparingLong(AfkPlaytimeRawData::getTime))
            .orElse(null);

        if (loginBetweenTwoLogouts != null) {
          processedLogins.add(loginBetweenTwoLogouts);
        }
      } else {
        AfkPlaytimeRawData loginAfterLastLogout = startSessions
            .stream()
            .filter(logins -> logins.getTime() > currentLogout.getTime())
            .min(Comparator.comparingLong(AfkPlaytimeRawData::getTime))
            .orElse(null);

        if (loginAfterLastLogout != null) {
          processedLogins.add(loginAfterLastLogout);
        }
      }
    }

    List<AfkPlaytimeRawData> combinedResult = new ArrayList<>(processedLogouts);
    combinedResult.addAll(processedLogins);
    combinedResult.sort(Comparator.comparingLong(AfkPlaytimeRawData::getTime));

    return combinedResult;
  }

  @Override
  public List<AfkPlaytimeRawData> removeLateAfkStarts(
      List<AfkPlaytimeRawData> sessions) {

    List<AfkPlaytimeRawData> startSessions = new ArrayList<>();
    List<AfkPlaytimeRawData> finishSessions = new ArrayList<>();

    for (AfkPlaytimeRawData session : sessions) {
      if (session.getActionAsBoolean()) {
        startSessions.add(session);
      } else {
        finishSessions.add(session);
      }
    }

    if (!startSessions.isEmpty() && !finishSessions.isEmpty()) {
      AfkPlaytimeRawData lastLogout = finishSessions.get(finishSessions.size() - 1);
      return startSessions
          .stream()
          .filter(login -> login.getTime() < lastLogout.getTime())
          .collect(Collectors.toList());
    }
    return startSessions;
  }

  @Override
  public List<SanitizedAfkSessionData> removeDuplicates(
      List<SanitizedAfkSessionData> sessions) {
    return sessions
        .stream()
        .distinct()
        .collect(Collectors.toList());
  }

  @Override
  public void saveSanitizedData(List<SanitizedAfkSessionData> sanitizedData) {
    sanitizedData.forEach(data -> sanitizedAfkSessionDataRepository.save(data));
  }
}
