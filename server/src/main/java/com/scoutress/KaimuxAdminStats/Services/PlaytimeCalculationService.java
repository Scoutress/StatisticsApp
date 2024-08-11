package com.scoutress.KaimuxAdminStats.Services;

import com.scoutress.KaimuxAdminStats.Entity.Playtime.DailyPlaytime;

public interface PlaytimeCalculationService {

  void calculateDailyPlaytime();

  void updatePlaytimeForServer(DailyPlaytime dailyPlaytime, String serverName, Double playtime);
}
