package com.scoutress.KaimuxAdminStats.services.playtime;

import java.time.LocalDate;
import java.util.List;

import com.scoutress.KaimuxAdminStats.entity.playtime.SessionDataPlugin;

public interface SessionsDataFromPluginService {

  LocalDate getLatestDate();

  void saveSessionData(List<SessionDataPlugin> sessionData);
}
