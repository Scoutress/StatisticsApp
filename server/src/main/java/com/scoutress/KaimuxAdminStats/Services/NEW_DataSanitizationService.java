package com.scoutress.KaimuxAdminStats.Services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.NEW_SanitizedSessionData;
import com.scoutress.KaimuxAdminStats.Repositories.NEW_SanitazedDataRepository;

@Service
public class NEW_DataSanitizationService {

  private final NEW_SanitazedDataRepository sanitazedDataRepository;
  private final NEW_DataFilterService dataFilterService;

  public NEW_DataSanitizationService(NEW_SanitazedDataRepository sanitazedDataRepository) {
    this.sanitazedDataRepository = sanitazedDataRepository;
    this.dataFilterService = new NEW_DataFilterService();
  }

  public void filterAndSaveSessions(List<NEW_SanitizedSessionData> data) {
    List<NEW_SanitizedSessionData> allLogins = dataFilterService.filterByAction(data, true);
    List<NEW_SanitizedSessionData> allLogouts = dataFilterService.filterByAction(data, false);

    List<NEW_SanitizedSessionData> filteredLogins = filterLogins(allLogins, allLogouts);
    List<NEW_SanitizedSessionData> filteredLogouts = filterLogouts(allLogouts, allLogins);

    saveFilteredSessions(filteredLogins, filteredLogouts);
  }

  private List<NEW_SanitizedSessionData> filterLogins(
      List<NEW_SanitizedSessionData> logins,
      List<NEW_SanitizedSessionData> logouts) {

    List<NEW_SanitizedSessionData> filteredLogins = new ArrayList<>();

    for (NEW_SanitizedSessionData login : logins) {
      NEW_SanitizedSessionData matchedLogout = findMatchingLogout(login, logouts);
      if (matchedLogout != null) {
        filteredLogins.add(login);
      }
    }
    return filteredLogins;
  }

  private List<NEW_SanitizedSessionData> filterLogouts(
      List<NEW_SanitizedSessionData> logouts,
      List<NEW_SanitizedSessionData> logins) {

    List<NEW_SanitizedSessionData> filteredLogouts = new ArrayList<>();

    for (NEW_SanitizedSessionData logout : logouts) {
      NEW_SanitizedSessionData matchedLogin = findMatchingLogin(logout, logins);
      if (matchedLogin != null) {
        filteredLogouts.add(logout);
      }
    }
    return filteredLogouts;
  }

  private NEW_SanitizedSessionData findMatchingLogout(
      NEW_SanitizedSessionData login,
      List<NEW_SanitizedSessionData> logouts) {

    List<NEW_SanitizedSessionData> logoutsInRange = logouts
        .stream()
        .filter(logout -> logout.getTime() > login.getTime())
        .collect(Collectors.toList());

    if (logoutsInRange.size() == 1) {
      return logoutsInRange.get(0);
    } else if (logoutsInRange.size() > 1) {
      return Collections.max(logoutsInRange, Comparator.comparingLong(NEW_SanitizedSessionData::getTime));
    }
    return null;
  }

  private NEW_SanitizedSessionData findMatchingLogin(
      NEW_SanitizedSessionData logout,
      List<NEW_SanitizedSessionData> logins) {

    List<NEW_SanitizedSessionData> loginsInRange = logins
        .stream()
        .filter(login -> login.getTime() < logout.getTime())
        .collect(Collectors.toList());

    if (loginsInRange.size() == 1) {
      return loginsInRange.get(0);
    } else if (loginsInRange.size() > 1) {
      return Collections.min(loginsInRange, Comparator.comparingLong(NEW_SanitizedSessionData::getTime));
    }
    return null;
  }

  private void saveFilteredSessions(
      List<NEW_SanitizedSessionData> filteredLogins,
      List<NEW_SanitizedSessionData> filteredLogouts) {

    saveSessions(filteredLogins, true);
    saveSessions(filteredLogouts, false);
  }

  private void saveSessions(List<NEW_SanitizedSessionData> sessions, boolean isLogin) {
    sessions.forEach(session -> {
      NEW_SanitizedSessionData sanitizedData = new NEW_SanitizedSessionData();
      sanitizedData.setAid(session.getAid());
      sanitizedData.setTime(session.getTime());
      sanitizedData.setAction(isLogin);
      sanitizedData.setServer(session.getServer());

      sanitazedDataRepository.save(sanitizedData);
    });
  }
}
