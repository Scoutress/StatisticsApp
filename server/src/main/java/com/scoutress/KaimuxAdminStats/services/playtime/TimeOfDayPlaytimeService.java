package com.scoutress.KaimuxAdminStats.services.playtime;

import java.util.List;

public interface TimeOfDayPlaytimeService {

  void handleTimeOfDayPlaytime();

  void handleProcessedTimeOfDayPlaytime(List<String> servers);

}