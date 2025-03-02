package com.scoutress.KaimuxAdminStats.services.discordMessages;

import java.time.LocalDate;
import java.util.List;

import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;

public interface DiscordBotService {

  void handleDcBotRequests(
      List<EmployeeCodes> employeeCodesData,
      LocalDate latestDateFromDcMsgsData,
      LocalDate todaysDate);

  void startBot();

  void sleepForHalfMin();

  void stopBot();
}
