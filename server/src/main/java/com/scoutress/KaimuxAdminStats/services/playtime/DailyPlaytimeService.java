package com.scoutress.KaimuxAdminStats.services.playtime;

public interface DailyPlaytimeService {

  void handleDailyPlaytime();

  Double getSumOfPlaytimeByEmployeeIdAndDuration(Short employeeId, Short days);
}