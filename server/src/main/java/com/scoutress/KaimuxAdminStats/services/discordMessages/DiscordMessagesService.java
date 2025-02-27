package com.scoutress.KaimuxAdminStats.services.discordMessages;

import java.util.List;

import com.scoutress.KaimuxAdminStats.entity.discordMessages.DailyDiscordMessages;
import com.scoutress.KaimuxAdminStats.entity.discordMessages.DiscordRawMessagesCounts;
import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;

public interface DiscordMessagesService {

  void convertDailyDiscordMessages(
      List<DiscordRawMessagesCounts> rawDcMessagesData,
      List<EmployeeCodes> employeeCodesData);

  void calculateAverageValueOfDailyDiscordMessages(
      List<DailyDiscordMessages> allDailyDcMessages,
      List<Short> allEmployeesFromDailyDcMessages);
}
