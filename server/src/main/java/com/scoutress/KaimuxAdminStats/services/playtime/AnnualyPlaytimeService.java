package com.scoutress.KaimuxAdminStats.services.playtime;

import java.util.List;

import com.scoutress.KaimuxAdminStats.entity.playtime.AnnualPlaytime;
import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;

public interface AnnualyPlaytimeService {

  public void handleAnnualPlaytime();

  public List<AnnualPlaytime> calculateAnnualPlaytime(List<DailyPlaytime> allPlaytime);

  public void saveAnnualPlaytime(List<AnnualPlaytime> annualPlaytimeData);
}
