package com.scoutress.KaimuxAdminStats.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.NEW_DailyPlaytime;
import com.scoutress.KaimuxAdminStats.Entity.NEW_SanitizedSessionData;
import com.scoutress.KaimuxAdminStats.Entity.NEW_SessionDataItem;
import com.scoutress.KaimuxAdminStats.Entity.NEW_SessionDuration;
import com.scoutress.KaimuxAdminStats.Repositories.NEW_DailyPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.Repositories.NEW_PlaytimeSessionsRepository;
import com.scoutress.KaimuxAdminStats.Repositories.NEW_ProcessedPlaytimeSessionsRepository;
import com.scoutress.KaimuxAdminStats.Repositories.NEW_SanitazedDataRepository;

@Service
public class NEW_DataExtractingService {

  public final NEW_PlaytimeSessionsRepository playtimeSessionsRepository;
  public final NEW_SanitazedDataRepository sanitazedDataRepository;
  public final NEW_ProcessedPlaytimeSessionsRepository processedPlaytimeSessionsRepository;
  public final NEW_DailyPlaytimeRepository dailyPlaytimeRepository;

  public NEW_DataExtractingService(
      NEW_PlaytimeSessionsRepository playtimeSessionsRepository,
      NEW_SanitazedDataRepository sanitazedDataRepository,
      NEW_ProcessedPlaytimeSessionsRepository processedPlaytimeSessionsRepository,
      NEW_DailyPlaytimeRepository dailyPlaytimeRepository) {
    this.playtimeSessionsRepository = playtimeSessionsRepository;
    this.sanitazedDataRepository = sanitazedDataRepository;
    this.processedPlaytimeSessionsRepository = processedPlaytimeSessionsRepository;
    this.dailyPlaytimeRepository = dailyPlaytimeRepository;
  }

  public List<NEW_SessionDataItem> getLoginLogoutTimes() {
    return playtimeSessionsRepository.findAll();
  }

  public List<NEW_SanitizedSessionData> getSanitizedLoginLogoutTimes() {
    return sanitazedDataRepository.findAll();
  }

  public List<NEW_SessionDuration> getSessionDurations() {
    return processedPlaytimeSessionsRepository.findAll();
  }

  public List<NEW_DailyPlaytime> getDailyPlaytimeData() {
    return dailyPlaytimeRepository.findAll();
  }
}
