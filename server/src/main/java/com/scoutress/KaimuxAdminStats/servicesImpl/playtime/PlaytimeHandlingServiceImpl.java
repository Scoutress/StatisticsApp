package com.scoutress.KaimuxAdminStats.servicesImpl.playtime;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.services.playtime.PlaytimeHandlingService;
import com.scoutress.KaimuxAdminStats.servicesImpl.SQLiteToMySQLServiceImpl;

@Service
public class PlaytimeHandlingServiceImpl implements PlaytimeHandlingService {

  private static final Logger log = LoggerFactory.getLogger(PlaytimeHandlingServiceImpl.class);

  private final SQLiteToMySQLServiceImpl sqLiteToMySQLServiceImpl;
  private final SessionDurationServiceImpl sessionDurationServiceImpl;
  private final DailyPlaytimeServiceImpl dailyPlaytimeServiceImpl;
  private final AnnualPlaytimeServiceImpl annualPlaytimeServiceImpl;
  private final AveragePlaytimeOverallServiceImpl averagePlaytimeOverallServiceImpl;
  private final TimeOfDayPlaytimeServiceImpl timeOfDayPlaytimeServiceImpl;

  private static final List<String> SERVERS = List.of(
      "Survival", "Skyblock", "Creative", "Boxpvp", "Prison", "Events", "Lobby");

  public PlaytimeHandlingServiceImpl(
      SQLiteToMySQLServiceImpl sqLiteToMySQLServiceImpl,
      SessionDurationServiceImpl sessionDurationServiceImpl,
      DailyPlaytimeServiceImpl dailyPlaytimeServiceImpl,
      AnnualPlaytimeServiceImpl annualPlaytimeServiceImpl,
      AveragePlaytimeOverallServiceImpl averagePlaytimeOverallServiceImpl,
      TimeOfDayPlaytimeServiceImpl timeOfDayPlaytimeServiceImpl) {
    this.sqLiteToMySQLServiceImpl = sqLiteToMySQLServiceImpl;
    this.sessionDurationServiceImpl = sessionDurationServiceImpl;
    this.dailyPlaytimeServiceImpl = dailyPlaytimeServiceImpl;
    this.annualPlaytimeServiceImpl = annualPlaytimeServiceImpl;
    this.averagePlaytimeOverallServiceImpl = averagePlaytimeOverallServiceImpl;
    this.timeOfDayPlaytimeServiceImpl = timeOfDayPlaytimeServiceImpl;
  }

  @Override
  public void handlePlaytime() {
    long totalStart = System.currentTimeMillis();
    log.info("=== Starting full playtime handling process ===");

    runStage("SQLite database initialization", () -> {
      sqLiteToMySQLServiceImpl.initializeUsersDatabase(SERVERS);
      sqLiteToMySQLServiceImpl.initializePlaytimeSessionsDatabase(SERVERS);
    });

    runStage("Login/Logout session processing", () -> {
      sessionDurationServiceImpl.processLoginLogouts(SERVERS);
      sessionDurationServiceImpl.removeLoginLogoutsDupe();
    });

    runStage("Session duration processing", () -> {
      sessionDurationServiceImpl.processSessions(SERVERS);
      sessionDurationServiceImpl.removeDuplicateSessionData();
    });

    runStage("Daily playtime calculation", () -> {
      dailyPlaytimeServiceImpl.handleDailyPlaytime();
      dailyPlaytimeServiceImpl.removeDuplicateDailyPlaytimes();
    });

    runStage("Annual playtime calculation", () -> {
      annualPlaytimeServiceImpl.handleAnnualPlaytime();
    });

    runStage("Average overall playtime calculation", () -> {
      averagePlaytimeOverallServiceImpl.handleAveragePlaytime();
    });

    runStage("Time-of-day playtime analysis", () -> {
      timeOfDayPlaytimeServiceImpl.handleTimeOfDayPlaytime();
      timeOfDayPlaytimeServiceImpl.handleProcessedTimeOfDayPlaytime(SERVERS);
    });

    long totalEnd = System.currentTimeMillis();
    log.info("✅ Total playtime handling completed in {} ms ({} s).",
        (totalEnd - totalStart), (totalEnd - totalStart) / 1000.0);
  }

  private void runStage(String stageName, Runnable stageAction) {
    long start = System.currentTimeMillis();
    log.info("▶️ Starting stage: {}", stageName);

    try {
      stageAction.run();
      long end = System.currentTimeMillis();
      log.info("✅ Completed '{}' in {} ms ({} s).", stageName, (end - start), (end - start) / 1000.0);
    } catch (Exception e) {
      log.error("❌ Error during stage '{}': {}", stageName, e.getMessage(), e);
    }
  }
}
