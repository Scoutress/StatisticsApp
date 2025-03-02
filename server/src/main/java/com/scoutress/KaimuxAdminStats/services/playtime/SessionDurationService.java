package com.scoutress.KaimuxAdminStats.services.playtime;

import java.util.List;

public interface SessionDurationService {

  void processLoginLogouts(List<String> servers);

  void removeLoginLogoutsDupe();

  void processSessions(List<String> servers);

  void removeDuplicateSessionData();

  // temp. method
  void processSessionsFromBackup();
}
