package com.scoutress.KaimuxAdminStats.services.playtime;

import java.util.List;

import com.scoutress.KaimuxAdminStats.entity.playtime.SanitizedSessionData;
import com.scoutress.KaimuxAdminStats.entity.playtime.SessionDataItem;

public interface DataSanitizationService {

  public void sanitizeData(
      List<SessionDataItem> sessionDataItems);

  public List<SanitizedSessionData> sanitizeSessions(
      List<SessionDataItem> loginSessions,
      List<SessionDataItem> logoutSessions);

  public List<SessionDataItem> removeEarlyLogouts(
      List<SessionDataItem> loginSessions,
      List<SessionDataItem> logoutSessions);

  public List<SessionDataItem> removeDuplicateLogouts(
      List<SessionDataItem> loginSessions,
      List<SessionDataItem> logoutSessions);

  public List<SessionDataItem> removeDuplicateLogins(
      List<SessionDataItem> sessions);

  public List<SessionDataItem> removeLateLogins(
      List<SessionDataItem> sessions);

  public List<SanitizedSessionData> removeDuplicates(
      List<SanitizedSessionData> sessions);

  public void saveSanitizedData(
      List<SanitizedSessionData> sanitizedData);
}
