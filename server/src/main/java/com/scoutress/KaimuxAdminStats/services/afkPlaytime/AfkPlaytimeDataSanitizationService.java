package com.scoutress.KaimuxAdminStats.services.afkPlaytime;

import java.util.List;

import com.scoutress.KaimuxAdminStats.entity.afkPlaytime.AfkPlaytimeRawData;
import com.scoutress.KaimuxAdminStats.entity.afkPlaytime.SanitizedAfkSessionData;

public interface AfkPlaytimeDataSanitizationService {

  void sanitizeData(List<AfkPlaytimeRawData> dataItems);

  List<SanitizedAfkSessionData> sanitizeSessions(
      List<AfkPlaytimeRawData> startSessions, List<AfkPlaytimeRawData> finishSessions);

  List<AfkPlaytimeRawData> removeEarlyAfkStarts(
      List<AfkPlaytimeRawData> startSessions, List<AfkPlaytimeRawData> finishSessions);

  List<AfkPlaytimeRawData> removeDuplicateAfkFinishes(
      List<AfkPlaytimeRawData> startSessions, List<AfkPlaytimeRawData> finishSessions);

  List<AfkPlaytimeRawData> removeDuplicateAfkStarts(List<AfkPlaytimeRawData> sessions);

  List<AfkPlaytimeRawData> removeLateAfkStarts(List<AfkPlaytimeRawData> sessions);

  List<SanitizedAfkSessionData> removeDuplicates(List<SanitizedAfkSessionData> sessions);

  void saveSanitizedData(List<SanitizedAfkSessionData> sanitizedData);
}
