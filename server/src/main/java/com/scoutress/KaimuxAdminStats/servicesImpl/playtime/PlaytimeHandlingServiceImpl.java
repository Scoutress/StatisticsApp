package com.scoutress.KaimuxAdminStats.servicesImpl.playtime;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.services.SQLiteToMySQLService;
import com.scoutress.KaimuxAdminStats.services.playtime.AnnualPlaytimeService;
import com.scoutress.KaimuxAdminStats.services.playtime.AveragePlaytimeOverallService;
import com.scoutress.KaimuxAdminStats.services.playtime.DailyPlaytimeService;
import com.scoutress.KaimuxAdminStats.services.playtime.PlaytimeHandlingService;
import com.scoutress.KaimuxAdminStats.services.playtime.SessionDurationService;

@Service
public class PlaytimeHandlingServiceImpl implements PlaytimeHandlingService {

  private final SQLiteToMySQLService sqLiteToMySQLService;
  private final SessionDurationService sessionDurationService;
  private final DailyPlaytimeService dailyPlaytimeService;
  private final AnnualPlaytimeService annualPlaytimeService;
  private final AveragePlaytimeOverallService averagePlaytimeOverallService;

  public PlaytimeHandlingServiceImpl(
      SQLiteToMySQLService sqLiteToMySQLService,
      SessionDurationService sessionDurationService,
      DailyPlaytimeService dailyPlaytimeService,
      AnnualPlaytimeService annualPlaytimeService,
      AveragePlaytimeOverallService averagePlaytimeOverallService) {
    this.sqLiteToMySQLService = sqLiteToMySQLService;
    this.sessionDurationService = sessionDurationService;
    this.dailyPlaytimeService = dailyPlaytimeService;
    this.annualPlaytimeService = annualPlaytimeService;
    this.averagePlaytimeOverallService = averagePlaytimeOverallService;
  }

  private static final List<String> servers = List.of(
      "Survival", "Skyblock", "Creative", "Boxpvp", "Prison", "Events", "Lobby");

  @Override
  public void handlePlaytime() {
    sqLiteToMySQLService.initializeUsersDatabase(servers);
    sqLiteToMySQLService.initializePlaytimeSessionsDatabase(servers);

    sessionDurationService.processLoginLogouts(servers);
    sessionDurationService.removeLoginLogoutsDupe();

    sessionDurationService.processSessions(servers);
    sessionDurationService.removeDuplicateSessionData();

    dailyPlaytimeService.handleDailyPlaytime();
    dailyPlaytimeService.removeDuplicateDailyPlaytimes();

    annualPlaytimeService.handleAnnualPlaytime();

    averagePlaytimeOverallService.handleAveragePlaytime();
  }
}
