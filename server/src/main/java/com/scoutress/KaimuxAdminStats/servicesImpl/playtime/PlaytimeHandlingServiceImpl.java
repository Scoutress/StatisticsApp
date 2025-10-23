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

  // ============================================================
  // MAIN PROCESS
  // ============================================================
  @Override
  public void handlePlaytime() {
    long totalStart = System.currentTimeMillis();
    log.info("\n==============================");
    log.info("🎮 [PLAYTIME PIPELINE STARTED]");
    log.info("==============================");

    try {
      int stageIndex = 0;
      int totalStages = 7;

      runStage(++stageIndex, totalStages, "SQLite database initialization", () -> {
        sqLiteToMySQLServiceImpl.initializeUsersDatabase(SERVERS);
        sqLiteToMySQLServiceImpl.initializePlaytimeSessionsDatabase(SERVERS);
      });

      runStage(++stageIndex, totalStages, "Login/Logout session processing", () -> {
        sessionDurationServiceImpl.processLoginLogouts(SERVERS);
        sessionDurationServiceImpl.removeLoginLogoutsDupe();
      });

      runStage(++stageIndex, totalStages, "Session duration processing", () -> {
        sessionDurationServiceImpl.processSessions(SERVERS);
        sessionDurationServiceImpl.removeDuplicateSessionData();
      });

      runStage(++stageIndex, totalStages, "Daily playtime calculation", () -> {
        dailyPlaytimeServiceImpl.handleDailyPlaytime();
        dailyPlaytimeServiceImpl.removeDuplicateDailyPlaytimes();
      });

      runStage(++stageIndex, totalStages, "Annual playtime calculation", () -> {
        annualPlaytimeServiceImpl.handleAnnualPlaytime();
      });

      runStage(++stageIndex, totalStages, "Average overall playtime calculation", () -> {
        averagePlaytimeOverallServiceImpl.handleAveragePlaytime();
      });

      runStage(++stageIndex, totalStages, "Time-of-day playtime analysis", () -> {
        timeOfDayPlaytimeServiceImpl.handleTimeOfDayPlaytime();
        timeOfDayPlaytimeServiceImpl.handleProcessedTimeOfDayPlaytime(SERVERS);
      });

      long totalEnd = System.currentTimeMillis();
      long totalElapsed = totalEnd - totalStart;

      log.info("\n==============================");
      log.info("✅ [PLAYTIME PIPELINE COMPLETED]");
      log.info("==============================");
      log.info("Total time: {} ms ({} s)", totalElapsed, totalElapsed / 1000.0);
      log.info("Total stages executed: {}", totalStages);

    } catch (Exception e) {
      log.error("❌ Critical error in full playtime pipeline: {}", e.getMessage(), e);
    }
  }

  // ============================================================
  // STAGE RUNNER
  // ============================================================
  private void runStage(int index, int totalStages, String stageName, Runnable action) {
    long start = System.currentTimeMillis();
    log.info("\n[Stage {}/{}] ▶️ Starting: {}", index, totalStages, stageName);

    try {
      action.run();
      long end = System.currentTimeMillis();
      long duration = end - start;

      log.info("[Stage {}/{}] ✅ Completed '{}' in {} ms ({} s).",
          index, totalStages, stageName, duration, duration / 1000.0);

    } catch (Exception e) {
      log.error("[Stage {}/{}] ❌ Error during '{}': {}", index, totalStages, stageName, e.getMessage(), e);
    }
  }
}
