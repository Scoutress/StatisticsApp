package com.scoutress.KaimuxAdminStats.servicesImpl.playtime;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.services.SQLiteToMySQLService;
import com.scoutress.KaimuxAdminStats.services.playtime.AnnualPlaytimeService;
import com.scoutress.KaimuxAdminStats.services.playtime.AveragePlaytimeOverallService;
import com.scoutress.KaimuxAdminStats.services.playtime.DailyPlaytimeService;
import com.scoutress.KaimuxAdminStats.services.playtime.PlaytimeHandlingService;
import com.scoutress.KaimuxAdminStats.services.playtime.SessionDurationService;
import com.scoutress.KaimuxAdminStats.services.playtime.TimeOfDayPlaytimeService;

@Service
public class PlaytimeHandlingServiceImpl implements PlaytimeHandlingService {

  private final SQLiteToMySQLService sqLiteToMySQLService;
  private final SessionDurationService sessionDurationService;
  private final DailyPlaytimeService dailyPlaytimeService;
  private final AnnualPlaytimeService annualPlaytimeService;
  private final AveragePlaytimeOverallService averagePlaytimeOverallService;
  private final TimeOfDayPlaytimeService timeOfDayPlaytimeService;

  public PlaytimeHandlingServiceImpl(
      SQLiteToMySQLService sqLiteToMySQLService,
      SessionDurationService sessionDurationService,
      DailyPlaytimeService dailyPlaytimeService,
      AnnualPlaytimeService annualPlaytimeService,
      AveragePlaytimeOverallService averagePlaytimeOverallService,
      TimeOfDayPlaytimeService timeOfDayPlaytimeService) {
    this.sqLiteToMySQLService = sqLiteToMySQLService;
    this.sessionDurationService = sessionDurationService;
    this.dailyPlaytimeService = dailyPlaytimeService;
    this.annualPlaytimeService = annualPlaytimeService;
    this.averagePlaytimeOverallService = averagePlaytimeOverallService;
    this.timeOfDayPlaytimeService = timeOfDayPlaytimeService;
  }

  private static final List<String> servers = List.of(
      "Survival", "Skyblock", "Creative", "Boxpvp", "Prison", "Events", "Lobby");

  @Override
  public void handlePlaytime() {
    long startTime = System.currentTimeMillis();

    System.out.println("Starting playtime handling process...");

    // Initialize databases
    long initStartTime = System.currentTimeMillis();
    sqLiteToMySQLService.initializeUsersDatabase(servers);
    sqLiteToMySQLService.initializePlaytimeSessionsDatabase(servers);
    long initEndTime = System.currentTimeMillis();
    System.out.println("Database initialization completed in " + (initEndTime - initStartTime) + " ms");

    // Process login logouts
    long loginLogoutsStartTime = System.currentTimeMillis();
    sessionDurationService.processLoginLogouts(servers);
    sessionDurationService.removeLoginLogoutsDupe();
    long loginLogoutsEndTime = System.currentTimeMillis();
    System.out.println("Login/Logout processing completed in " + (loginLogoutsEndTime - loginLogoutsStartTime) + " ms");

    // Process sessions
    long sessionStartTime = System.currentTimeMillis();
    sessionDurationService.processSessions(servers);
    sessionDurationService.removeDuplicateSessionData();
    long sessionEndTime = System.currentTimeMillis();
    System.out.println("Session processing completed in " + (sessionEndTime - sessionStartTime) + " ms");

    // Handle daily playtime
    long dailyPlaytimeStartTime = System.currentTimeMillis();
    dailyPlaytimeService.handleDailyPlaytime();
    dailyPlaytimeService.removeDuplicateDailyPlaytimes();
    long dailyPlaytimeEndTime = System.currentTimeMillis();
    System.out
        .println("Daily playtime handling completed in " + (dailyPlaytimeEndTime - dailyPlaytimeStartTime) + " ms");

    // Handle annual playtime
    long annualPlaytimeStartTime = System.currentTimeMillis();
    annualPlaytimeService.handleAnnualPlaytime();
    long annualPlaytimeEndTime = System.currentTimeMillis();
    System.out
        .println("Annual playtime handling completed in " + (annualPlaytimeEndTime - annualPlaytimeStartTime) + " ms");

    // Handle average playtime overall
    long avgPlaytimeStartTime = System.currentTimeMillis();
    averagePlaytimeOverallService.handleAveragePlaytime();
    long avgPlaytimeEndTime = System.currentTimeMillis();
    System.out.println(
        "Average playtime overall handling completed in " + (avgPlaytimeEndTime - avgPlaytimeStartTime) + " ms");

    // Handle time of day playtime
    long timeOfDayPlaytimeStartTime = System.currentTimeMillis();
    timeOfDayPlaytimeService.handleTimeOfDayPlaytime();
    timeOfDayPlaytimeService.handleProcessedTimeOfDayPlaytime(servers);
    long timeOfDayPlaytimeEndTime = System.currentTimeMillis();
    System.out.println("Time of day playtime handling completed in "
        + (timeOfDayPlaytimeEndTime - timeOfDayPlaytimeStartTime) + " ms");

    long endTime = System.currentTimeMillis();
    System.out.println("Total playtime handling process completed in " + (endTime - startTime) + " ms");
  }
}
