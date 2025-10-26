package com.scoutress.KaimuxAdminStats.services.discordMessages;

import java.util.List;

import com.scoutress.KaimuxAdminStats.entity.discordMessages.DailyDiscordMessages;

public interface DiscordMessagesComparedService {

  void compareEachEmployeeDailyDiscordMessagesValues(
      List<DailyDiscordMessages> allDailyDcMessages,
      List<Short> allEmployeesFromDailyDcMessages,
      List<Short> employeeIdsWithoutData);
}
