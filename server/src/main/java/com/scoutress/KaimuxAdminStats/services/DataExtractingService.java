package com.scoutress.KaimuxAdminStats.services;

import java.util.List;

import com.scoutress.KaimuxAdminStats.entity.afkPlaytime.AfkPlaytimeRawData;
import com.scoutress.KaimuxAdminStats.entity.afkPlaytime.SanitizedAfkSessionData;
import com.scoutress.KaimuxAdminStats.entity.discordTickets.DiscordTicketsReactions;
import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.MinecraftTicketsAnswers;
import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.entity.playtime.SanitizedSessionData;
import com.scoutress.KaimuxAdminStats.entity.playtime.SessionDataItem;
import com.scoutress.KaimuxAdminStats.entity.playtime.SessionDuration;

public interface DataExtractingService {

  public List<SessionDataItem> getLoginLogoutTimes();

  public List<SanitizedSessionData> getSanitizedLoginLogoutTimes();

  public List<SessionDuration> getSessionDurations();

  public List<DailyPlaytime> getDailyPlaytimeData();

  public List<EmployeeCodes> getAllEmployeeCodes();

  public List<Employee> getAllEmployees();

  public List<DiscordTicketsReactions> getAllDcTicketReactions();

  public List<MinecraftTicketsAnswers> getAllMcTicketsAnswers();

  public List<AfkPlaytimeRawData> getStartFinishTimes();

  public List<SanitizedAfkSessionData> getSanitizedAfkStartFinishTimes();

}
