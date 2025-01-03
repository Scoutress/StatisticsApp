package com.scoutress.KaimuxAdminStats.services.playtime;

public interface SessionDurationService {

  void processSessions();

  void removeDuplicateSessionData();
}
