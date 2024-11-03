package com.scoutress.KaimuxAdminStats.Services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.NEW_SanitizedSessionData;
import com.scoutress.KaimuxAdminStats.Entity.NEW_SessionDataItem;
import com.scoutress.KaimuxAdminStats.Repositories.NEW_SanitazedDataRepository;

@Service
public class NEW_DataSanitizationService {

  private final NEW_SanitazedDataRepository sanitazedDataRepository;
  private final NEW_DataFilterService dataFilterService;

  public NEW_DataSanitizationService(NEW_SanitazedDataRepository sanitazedDataRepository) {
    this.sanitazedDataRepository = sanitazedDataRepository;
    this.dataFilterService = new NEW_DataFilterService();
  }

  public void sanitizeData(List<NEW_SessionDataItem> sessionDataItems) {

    Set<Short> uniqueAids = sessionDataItems
        .stream()
        .map(NEW_SessionDataItem::getAid)
        .collect(Collectors.toSet());

    for (Short aid : uniqueAids) {

      List<NEW_SessionDataItem> sessionsForAid = dataFilterService.filterSessionsByAid(sessionDataItems, aid);

      Set<String> uniqueServers = sessionsForAid
          .stream()
          .map(NEW_SessionDataItem::getServer)
          .collect(Collectors.toSet());

      for (String server : uniqueServers) {

        List<NEW_SessionDataItem> sessionsForServer = dataFilterService.filterSessionsByServer(sessionsForAid, server);

        List<NEW_SessionDataItem> loginSessions = dataFilterService.filterSessionsByAction(sessionsForServer, true);

        List<NEW_SessionDataItem> logoutSessions = dataFilterService.filterSessionsByAction(sessionsForServer, false);

        List<NEW_SanitizedSessionData> sanitizedData = sanitizeSessions(loginSessions, logoutSessions);

        saveSanitizedData(sanitizedData);
      }
    }
  }

  public List<NEW_SanitizedSessionData> sanitizeSessions(
      List<NEW_SessionDataItem> loginSessions,
      List<NEW_SessionDataItem> logoutSessions) {

    List<NEW_SanitizedSessionData> sanitizedSessions = new ArrayList<>();

    List<NEW_SessionDataItem> logoutsDataWithoutEarlyLogouts = removeEarlyLogouts(loginSessions, logoutSessions);

    List<NEW_SessionDataItem> sessionsWithFilteredLogouts = removeDuplicateLogouts(loginSessions,
        logoutsDataWithoutEarlyLogouts);

    List<NEW_SessionDataItem> sessionsWithFilteredLoginsAndLogouts = removeDuplicateLogins(sessionsWithFilteredLogouts);

    List<NEW_SessionDataItem> sessionsWithRemovedLateLogins = removeLateLogins(sessionsWithFilteredLoginsAndLogouts);

    for (NEW_SessionDataItem session : sessionsWithRemovedLateLogins) {
      if (session.getActionAsBoolean()) {
        loginSessions.add(session);
      } else {
        logoutSessions.add(session);
      }
    }

    for (NEW_SessionDataItem login : loginSessions) {
      NEW_SanitizedSessionData sanitizedData = new NEW_SanitizedSessionData();
      sanitizedData.setAid(login.getAid());
      sanitizedData.setTime(login.getTime());
      sanitizedData.setAction(true);
      sanitizedData.setServer(login.getServer());
      sanitizedSessions.add(sanitizedData);
    }

    for (NEW_SessionDataItem logout : logoutSessions) {
      NEW_SanitizedSessionData sanitizedData = new NEW_SanitizedSessionData();
      sanitizedData.setAid(logout.getAid());
      sanitizedData.setTime(logout.getTime());
      sanitizedData.setAction(false);
      sanitizedData.setServer(logout.getServer());
      sanitizedSessions.add(sanitizedData);
    }

    sanitizedSessions.sort(Comparator.comparingLong(NEW_SanitizedSessionData::getTime));

    return removeDuplicates(sanitizedSessions);
  }

  public List<NEW_SessionDataItem> removeEarlyLogouts(
      List<NEW_SessionDataItem> loginSessions,
      List<NEW_SessionDataItem> logoutSessions) {

    if (!loginSessions.isEmpty() && !logoutSessions.isEmpty()) {
      long firstLoginTime = loginSessions.get(0).getTime();

      List<NEW_SessionDataItem> filteredLogouts = logoutSessions
          .stream()
          .filter(logout -> logout.getTime() > firstLoginTime)
          .collect(Collectors.toList());

      return filteredLogouts;
    }

    return logoutSessions;
  }

  public List<NEW_SessionDataItem> removeDuplicateLogouts(
      List<NEW_SessionDataItem> loginSessions,
      List<NEW_SessionDataItem> logoutSessions) {

    List<NEW_SessionDataItem> processedLogins = new ArrayList<>();
    List<NEW_SessionDataItem> processedLogouts = new ArrayList<>();

    if (loginSessions.isEmpty()) {
      return processedLogins;
    }

    for (int i = 0; i < loginSessions.size(); i++) {
      NEW_SessionDataItem currentLogin = loginSessions.get(i);
      processedLogins.add(currentLogin);

      if (i < loginSessions.size() - 1) {
        NEW_SessionDataItem nextLogin = loginSessions.get(i + 1);
        NEW_SessionDataItem logoutBetweenTwoLogins = logoutSessions
            .stream()
            .filter(logouts -> logouts.getTime() > currentLogin.getTime() && logouts.getTime() < nextLogin.getTime())
            .max(Comparator.comparingLong(NEW_SessionDataItem::getTime))
            .orElse(null);

        if (logoutBetweenTwoLogins != null) {
          processedLogouts.add(logoutBetweenTwoLogins);
        }
      } else {
        NEW_SessionDataItem logoutAfterLastLogin = logoutSessions
            .stream()
            .filter(logouts -> logouts.getTime() > currentLogin.getTime())
            .findFirst()
            .orElse(null);

        if (logoutAfterLastLogin != null) {
          processedLogouts.add(logoutAfterLastLogin);
        }
      }
    }

    List<NEW_SessionDataItem> combinedResult = new ArrayList<>(processedLogins);
    combinedResult.addAll(processedLogouts);
    combinedResult.sort(Comparator.comparingLong(NEW_SessionDataItem::getTime));

    return combinedResult;
  }

  public List<NEW_SessionDataItem> removeDuplicateLogins(
      List<NEW_SessionDataItem> sessions) {
    List<NEW_SessionDataItem> processedLogins = new ArrayList<>();
    List<NEW_SessionDataItem> processedLogouts = new ArrayList<>();
    List<NEW_SessionDataItem> loginSessions = new ArrayList<>();
    List<NEW_SessionDataItem> logoutSessions = new ArrayList<>();

    for (NEW_SessionDataItem session : sessions) {
      if (session.getActionAsBoolean()) {
        loginSessions.add(session);
      } else {
        logoutSessions.add(session);
      }
    }

    if (logoutSessions.isEmpty()) {
      return processedLogouts;
    }

    loginSessions.sort(Comparator.comparingLong(NEW_SessionDataItem::getTime));
    logoutSessions.sort(Comparator.comparingLong(NEW_SessionDataItem::getTime));

    for (int i = 0; i < logoutSessions.size(); i++) {
      NEW_SessionDataItem currentLogout = logoutSessions.get(i);
      processedLogouts.add(currentLogout);

      if (i < logoutSessions.size() - 1) {
        NEW_SessionDataItem nextLogout = logoutSessions.get(i + 1);

        NEW_SessionDataItem loginBetweenTwoLogouts = loginSessions
            .stream()
            .filter(logins -> logins.getTime() > currentLogout.getTime())
            .filter(logins -> logins.getTime() < nextLogout.getTime())
            .min(Comparator.comparingLong(NEW_SessionDataItem::getTime))
            .orElse(null);

        if (loginBetweenTwoLogouts != null) {
          processedLogins.add(loginBetweenTwoLogouts);
        }
      } else {
        NEW_SessionDataItem loginAfterLastLogout = loginSessions
            .stream()
            .filter(logins -> logins.getTime() > currentLogout.getTime())
            .min(Comparator.comparingLong(NEW_SessionDataItem::getTime))
            .orElse(null);

        if (loginAfterLastLogout != null) {
          processedLogins.add(loginAfterLastLogout);
        }
      }
    }

    List<NEW_SessionDataItem> combinedResult = new ArrayList<>(processedLogouts);
    combinedResult.addAll(processedLogins);
    combinedResult.sort(Comparator.comparingLong(NEW_SessionDataItem::getTime));

    return combinedResult;
  }

  public List<NEW_SessionDataItem> removeLateLogins(
      List<NEW_SessionDataItem> sessions) {

    List<NEW_SessionDataItem> loginSessions = new ArrayList<>();
    List<NEW_SessionDataItem> logoutSessions = new ArrayList<>();

    for (NEW_SessionDataItem session : sessions) {
      if (session.getActionAsBoolean()) {
        loginSessions.add(session);
      } else {
        logoutSessions.add(session);
      }
    }

    if (!loginSessions.isEmpty() && !logoutSessions.isEmpty()) {
      NEW_SessionDataItem lastLogout = logoutSessions.get(logoutSessions.size() - 1);
      return loginSessions
          .stream()
          .filter(login -> login.getTime() < lastLogout.getTime())
          .collect(Collectors.toList());
    }
    return loginSessions;
  }

  public List<NEW_SanitizedSessionData> removeDuplicates(
      List<NEW_SanitizedSessionData> sessions) {
    return sessions
        .stream()
        .distinct()
        .collect(Collectors.toList());
  }

  public void saveSanitizedData(List<NEW_SanitizedSessionData> sanitizedData) {
    sanitizedData.forEach(data -> sanitazedDataRepository.save(data));
  }
}
