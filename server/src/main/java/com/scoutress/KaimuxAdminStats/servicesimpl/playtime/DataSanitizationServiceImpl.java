package com.scoutress.KaimuxAdminStats.servicesImpl.playtime;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.playtime.SanitizedSessionData;
import com.scoutress.KaimuxAdminStats.entity.playtime.SessionDataItem;
import com.scoutress.KaimuxAdminStats.repositories.playtime.SanitazedDataRepository;
import com.scoutress.KaimuxAdminStats.services.DataFilterService;
import com.scoutress.KaimuxAdminStats.services.playtime.DataSanitizationService;

@Service
public class DataSanitizationServiceImpl implements DataSanitizationService {

  private final SanitazedDataRepository sanitazedDataRepository;
  private final DataFilterService dataFilterService;

  public DataSanitizationServiceImpl(
      SanitazedDataRepository sanitazedDataRepository,
      DataFilterService dataFilterService) {

    this.sanitazedDataRepository = sanitazedDataRepository;
    this.dataFilterService = dataFilterService;
  }

  @Override
  public void sanitizeData(List<SessionDataItem> sessionDataItems) {

    Set<Short> uniqueAids = sessionDataItems
        .stream()
        .map(SessionDataItem::getAid)
        .collect(Collectors.toSet());

    for (Short aid : uniqueAids) {

      List<SessionDataItem> sessionsForAid = dataFilterService.filterSessionsByAid(sessionDataItems, aid);

      Set<String> uniqueServers = sessionsForAid
          .stream()
          .map(SessionDataItem::getServer)
          .collect(Collectors.toSet());

      for (String server : uniqueServers) {

        List<SessionDataItem> sessionsForServer = dataFilterService.filterSessionsByServer(sessionsForAid, server);

        List<SessionDataItem> loginSessions = dataFilterService.filterSessionsByAction(sessionsForServer, true);

        List<SessionDataItem> logoutSessions = dataFilterService.filterSessionsByAction(sessionsForServer, false);

        List<SanitizedSessionData> sanitizedData = sanitizeSessions(loginSessions, logoutSessions);

        saveSanitizedData(sanitizedData);
      }
    }
  }

  @Override
  public List<SanitizedSessionData> sanitizeSessions(
      List<SessionDataItem> loginSessions,
      List<SessionDataItem> logoutSessions) {

    List<SanitizedSessionData> sanitizedSessions = new ArrayList<>();

    List<SessionDataItem> logoutsDataWithoutEarlyLogouts = removeEarlyLogouts(loginSessions, logoutSessions);

    List<SessionDataItem> sessionsWithFilteredLogouts = removeDuplicateLogouts(loginSessions,
        logoutsDataWithoutEarlyLogouts);

    List<SessionDataItem> sessionsWithFilteredLoginsAndLogouts = removeDuplicateLogins(sessionsWithFilteredLogouts);

    List<SessionDataItem> sessionsWithRemovedLateLogins = removeLateLogins(sessionsWithFilteredLoginsAndLogouts);

    for (SessionDataItem session : sessionsWithRemovedLateLogins) {
      if (session.getActionAsBoolean()) {
        loginSessions.add(session);
      } else {
        logoutSessions.add(session);
      }
    }

    for (SessionDataItem login : loginSessions) {
      SanitizedSessionData sanitizedData = new SanitizedSessionData();
      sanitizedData.setAid(login.getAid());
      sanitizedData.setTime(login.getTime());
      sanitizedData.setAction(true);
      sanitizedData.setServer(login.getServer());
      sanitizedSessions.add(sanitizedData);
    }

    for (SessionDataItem logout : logoutSessions) {
      SanitizedSessionData sanitizedData = new SanitizedSessionData();
      sanitizedData.setAid(logout.getAid());
      sanitizedData.setTime(logout.getTime());
      sanitizedData.setAction(false);
      sanitizedData.setServer(logout.getServer());
      sanitizedSessions.add(sanitizedData);
    }

    sanitizedSessions.sort(Comparator.comparingLong(SanitizedSessionData::getTime));

    return removeDuplicates(sanitizedSessions);
  }

  @Override
  public List<SessionDataItem> removeEarlyLogouts(
      List<SessionDataItem> loginSessions,
      List<SessionDataItem> logoutSessions) {

    if (!loginSessions.isEmpty() && !logoutSessions.isEmpty()) {
      long firstLoginTime = loginSessions.get(0).getTime();

      List<SessionDataItem> filteredLogouts = logoutSessions
          .stream()
          .filter(logout -> logout.getTime() > firstLoginTime)
          .collect(Collectors.toList());

      return filteredLogouts;
    }

    return logoutSessions;
  }

  @Override
  public List<SessionDataItem> removeDuplicateLogouts(
      List<SessionDataItem> loginSessions,
      List<SessionDataItem> logoutSessions) {

    List<SessionDataItem> processedLogins = new ArrayList<>();
    List<SessionDataItem> processedLogouts = new ArrayList<>();

    if (loginSessions.isEmpty()) {
      return processedLogins;
    }

    for (int i = 0; i < loginSessions.size(); i++) {
      SessionDataItem currentLogin = loginSessions.get(i);
      processedLogins.add(currentLogin);

      if (i < loginSessions.size() - 1) {
        SessionDataItem nextLogin = loginSessions.get(i + 1);
        SessionDataItem logoutBetweenTwoLogins = logoutSessions
            .stream()
            .filter(logouts -> logouts.getTime() > currentLogin.getTime() && logouts.getTime() < nextLogin.getTime())
            .max(Comparator.comparingLong(SessionDataItem::getTime))
            .orElse(null);

        if (logoutBetweenTwoLogins != null) {
          processedLogouts.add(logoutBetweenTwoLogins);
        }
      } else {
        SessionDataItem logoutAfterLastLogin = logoutSessions
            .stream()
            .filter(logouts -> logouts.getTime() > currentLogin.getTime())
            .findFirst()
            .orElse(null);

        if (logoutAfterLastLogin != null) {
          processedLogouts.add(logoutAfterLastLogin);
        }
      }
    }

    List<SessionDataItem> combinedResult = new ArrayList<>(processedLogins);
    combinedResult.addAll(processedLogouts);
    combinedResult.sort(Comparator.comparingLong(SessionDataItem::getTime));

    return combinedResult;
  }

  @Override
  public List<SessionDataItem> removeDuplicateLogins(
      List<SessionDataItem> sessions) {
    List<SessionDataItem> processedLogins = new ArrayList<>();
    List<SessionDataItem> processedLogouts = new ArrayList<>();
    List<SessionDataItem> loginSessions = new ArrayList<>();
    List<SessionDataItem> logoutSessions = new ArrayList<>();

    for (SessionDataItem session : sessions) {
      if (session.getActionAsBoolean()) {
        loginSessions.add(session);
      } else {
        logoutSessions.add(session);
      }
    }

    if (logoutSessions.isEmpty()) {
      return processedLogouts;
    }

    loginSessions.sort(Comparator.comparingLong(SessionDataItem::getTime));
    logoutSessions.sort(Comparator.comparingLong(SessionDataItem::getTime));

    for (int i = 0; i < logoutSessions.size(); i++) {
      SessionDataItem currentLogout = logoutSessions.get(i);
      processedLogouts.add(currentLogout);

      if (i < logoutSessions.size() - 1) {
        SessionDataItem nextLogout = logoutSessions.get(i + 1);

        SessionDataItem loginBetweenTwoLogouts = loginSessions
            .stream()
            .filter(logins -> logins.getTime() > currentLogout.getTime())
            .filter(logins -> logins.getTime() < nextLogout.getTime())
            .min(Comparator.comparingLong(SessionDataItem::getTime))
            .orElse(null);

        if (loginBetweenTwoLogouts != null) {
          processedLogins.add(loginBetweenTwoLogouts);
        }
      } else {
        SessionDataItem loginAfterLastLogout = loginSessions
            .stream()
            .filter(logins -> logins.getTime() > currentLogout.getTime())
            .min(Comparator.comparingLong(SessionDataItem::getTime))
            .orElse(null);

        if (loginAfterLastLogout != null) {
          processedLogins.add(loginAfterLastLogout);
        }
      }
    }

    List<SessionDataItem> combinedResult = new ArrayList<>(processedLogouts);
    combinedResult.addAll(processedLogins);
    combinedResult.sort(Comparator.comparingLong(SessionDataItem::getTime));

    return combinedResult;
  }

  @Override
  public List<SessionDataItem> removeLateLogins(
      List<SessionDataItem> sessions) {

    List<SessionDataItem> loginSessions = new ArrayList<>();
    List<SessionDataItem> logoutSessions = new ArrayList<>();

    for (SessionDataItem session : sessions) {
      if (session.getActionAsBoolean()) {
        loginSessions.add(session);
      } else {
        logoutSessions.add(session);
      }
    }

    if (!loginSessions.isEmpty() && !logoutSessions.isEmpty()) {
      SessionDataItem lastLogout = logoutSessions.get(logoutSessions.size() - 1);
      return loginSessions
          .stream()
          .filter(login -> login.getTime() < lastLogout.getTime())
          .collect(Collectors.toList());
    }
    return loginSessions;
  }

  @Override
  public List<SanitizedSessionData> removeDuplicates(
      List<SanitizedSessionData> sessions) {
    return sessions
        .stream()
        .distinct()
        .collect(Collectors.toList());
  }

  @Override
  public void saveSanitizedData(List<SanitizedSessionData> sanitizedData) {
    sanitizedData.forEach(data -> sanitazedDataRepository.save(data));
  }
}
