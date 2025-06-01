package com.scoutress.KaimuxAdminStats.servicesImpl.playtime;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.services.playtime.PlaytimeHandlingService;
import com.scoutress.KaimuxAdminStats.servicesImpl.SQLiteToMySQLServiceImpl;

@Service
public class PlaytimeHandlingServiceImpl implements PlaytimeHandlingService {

  private final SQLiteToMySQLServiceImpl sqLiteToMySQLServiceImpl;
  private final SessionDurationServiceImpl sessionDurationServiceImpl;
  private final DailyPlaytimeServiceImpl dailyPlaytimeServiceImpl;
  private final AnnualPlaytimeServiceImpl annualPlaytimeServiceImpl;
  private final AveragePlaytimeOverallServiceImpl averagePlaytimeOverallServiceImpl;
  private final TimeOfDayPlaytimeServiceImpl timeOfDayPlaytimeServiceImpl;

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

  private static final List<String> servers = List.of(
      "Survival", "Skyblock", "Creative", "Boxpvp", "Prison", "Events", "Lobby");

  @Override
  public void handlePlaytime() {
    long startTime = System.currentTimeMillis();

    System.out.println("Starting playtime handling process...");

    // Initialize databases
    long initStartTime = System.currentTimeMillis();
    sqLiteToMySQLServiceImpl.initializeUsersDatabase(servers);
    sqLiteToMySQLServiceImpl.initializePlaytimeSessionsDatabase(servers);
    long initEndTime = System.currentTimeMillis();
    System.out.println("Database initialization completed in " + (initEndTime - initStartTime) + " ms");

    // Process login logouts
    long loginLogoutsStartTime = System.currentTimeMillis();
    sessionDurationServiceImpl.processLoginLogouts(servers);
    sessionDurationServiceImpl.removeLoginLogoutsDupe();
    long loginLogoutsEndTime = System.currentTimeMillis();
    System.out.println("Login/Logout processing completed in " + (loginLogoutsEndTime - loginLogoutsStartTime) + " ms");

    // Process sessions
    long sessionStartTime = System.currentTimeMillis();
    sessionDurationServiceImpl.processSessions(servers);
    sessionDurationServiceImpl.removeDuplicateSessionData();
    long sessionEndTime = System.currentTimeMillis();
    System.out.println("Session processing completed in " + (sessionEndTime - sessionStartTime) + " ms");

    // Handle daily playtime
    long dailyPlaytimeStartTime = System.currentTimeMillis();
    dailyPlaytimeServiceImpl.handleDailyPlaytime();
    dailyPlaytimeServiceImpl.removeDuplicateDailyPlaytimes();
    long dailyPlaytimeEndTime = System.currentTimeMillis();
    System.out
        .println("Daily playtime handling completed in " + (dailyPlaytimeEndTime - dailyPlaytimeStartTime) + " ms");

    // Handle annual playtime
    long annualPlaytimeStartTime = System.currentTimeMillis();
    annualPlaytimeServiceImpl.handleAnnualPlaytime();
    long annualPlaytimeEndTime = System.currentTimeMillis();
    System.out
        .println("Annual playtime handling completed in " + (annualPlaytimeEndTime - annualPlaytimeStartTime) + " ms");

    // Handle average playtime overall
    long avgPlaytimeStartTime = System.currentTimeMillis();
    averagePlaytimeOverallServiceImpl.handleAveragePlaytime();
    long avgPlaytimeEndTime = System.currentTimeMillis();
    System.out.println(
        "Average playtime overall handling completed in " + (avgPlaytimeEndTime - avgPlaytimeStartTime) + " ms");

    // Handle time of day playtime
    long timeOfDayPlaytimeStartTime = System.currentTimeMillis();
    timeOfDayPlaytimeServiceImpl.handleTimeOfDayPlaytime();
    timeOfDayPlaytimeServiceImpl.handleProcessedTimeOfDayPlaytime(servers);
    long timeOfDayPlaytimeEndTime = System.currentTimeMillis();
    System.out.println("Time of day playtime handling completed in "
        + (timeOfDayPlaytimeEndTime - timeOfDayPlaytimeStartTime) + " ms");

    long endTime = System.currentTimeMillis();
    System.out.println("Total playtime handling process completed in " + (endTime - startTime) + " ms");
  }
}
