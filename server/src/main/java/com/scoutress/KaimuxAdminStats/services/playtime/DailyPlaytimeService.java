package com.scoutress.KaimuxAdminStats.services.playtime;

public interface DailyPlaytimeService {

  void handleDailyPlaytime();

  void removeDuplicateDailyPlaytimes();

  Double getSumOfPlaytimeByEmployeeIdAndDuration(Short employeeId, Short days);
}