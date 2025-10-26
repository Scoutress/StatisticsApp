package com.scoutress.KaimuxAdminStats.servicesImpl.playtime;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scoutress.KaimuxAdminStats.entity.playtime.SessionDataPlugin;
import com.scoutress.KaimuxAdminStats.repositories.playtime.DailyPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.repositories.playtime.SessionDataPluginRepository;
import com.scoutress.KaimuxAdminStats.services.playtime.SessionsDataFromPluginService;

@Service
public class SessionsDataFromPluginServiceImpl implements SessionsDataFromPluginService {

  private static final Logger log = LoggerFactory.getLogger(SessionsDataFromPluginServiceImpl.class);

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
    try {
      LocalDate date = dailyPlaytimeRepository.findLatestDate();
      log.debug("Latest playtime date fetched: {}", date);
      return date;
    } catch (DataAccessException e) {
      log.error("❌ Failed to fetch latest playtime date: {}", e.getMessage(), e);
      return LocalDate.of(1970, 1, 1);
    }
  }

  @Override
  @Transactional
  public void saveSessionData(List<SessionDataPlugin> sessionData) {
    if (sessionData == null || sessionData.isEmpty()) {
      log.warn("⚠️ No session data provided for saving — skipping.");
      return;
    }

    log.info("Saving {} session entries from plugin...", sessionData.size());

    try {
      sessionDataPluginRepository.saveAll(sessionData);
      log.info("✅ Successfully saved {} session entries.", sessionData.size());
    } catch (DataAccessException e) {
      log.error("❌ Error while saving session data: {}", e.getMessage(), e);
    }
  }
}
