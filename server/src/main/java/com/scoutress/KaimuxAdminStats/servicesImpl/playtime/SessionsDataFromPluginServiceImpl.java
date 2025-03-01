package com.scoutress.KaimuxAdminStats.servicesImpl.playtime;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.playtime.SessionDataPlugin;
import com.scoutress.KaimuxAdminStats.repositories.playtime.DailyPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.SessionDataPluginRepository;
import com.scoutress.KaimuxAdminStats.services.playtime.SessionsDataFromPluginService;

@Service
public class SessionsDataFromPluginServiceImpl implements SessionsDataFromPluginService {

  private final DailyPlaytimeRepository dailyPlaytimeRepository;
  private final SessionDataPluginRepository sessionDataPluginRepository;

  public SessionsDataFromPluginServiceImpl(
      DailyPlaytimeRepository dailyPlaytimeRepository,
      SessionDataPluginRepository sessionDataPluginRepository) {
    this.dailyPlaytimeRepository = dailyPlaytimeRepository;
    this.sessionDataPluginRepository = sessionDataPluginRepository;
  }

  @Override
  public LocalDate getLatestDate() {
    return dailyPlaytimeRepository.findLatestDate();
  }

  @Override
  public void saveSessionData(List<SessionDataPlugin> sessionData) {
    sessionDataPluginRepository.saveAll(sessionData);
  }
}
