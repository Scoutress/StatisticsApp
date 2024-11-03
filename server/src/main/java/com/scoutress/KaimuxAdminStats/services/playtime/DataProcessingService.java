package com.scoutress.KaimuxAdminStats.services.playtime;

import java.time.LocalDate;
import java.util.List;

import com.scoutress.KaimuxAdminStats.entity.playtime.SanitizedSessionData;

public interface DataProcessingService {

  public void calculateSingleSessionTime();

  public void processSessions(
      short aid,
      String server,
      List<SanitizedSessionData> loginSessions,
      List<SanitizedSessionData> logoutSessions);

  public void handleSingleLogout(
      short aid,
      String server,
      long loginEpochTime,
      long logoutEpochTime);

  public void saveSessionDuration(
      short aid,
      int sessionDuration,
      LocalDate date,
      String server);
}
