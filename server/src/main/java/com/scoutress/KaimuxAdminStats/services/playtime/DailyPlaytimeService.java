package com.scoutress.KaimuxAdminStats.services.playtime;

import java.util.List;

import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.entity.playtime.SessionDuration;

public interface DailyPlaytimeService {

  public void handleDailyPlaytime();

  public List<DailyPlaytime> calculateDailyPlaytime(
      List<SessionDuration> sessions);

  public void saveCalculatedPlaytime(
      List<DailyPlaytime> dailyPlaytimeData);
}