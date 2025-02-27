package com.scoutress.KaimuxAdminStats.services.minecraftTickets;

import java.time.LocalDate;
import java.util.List;

import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.DailyMinecraftTickets;
import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;

public interface MinecraftTicketsComparedService {

  void compareEachEmployeeDailyMcTicketsValues(
      List<DailyMinecraftTickets> rawDailyMcTicketsData,
      List<DailyPlaytime> allPlaytimeData,
      List<LocalDate> allDatesFromDailyMcTickets,
      List<Short> allEmployeesFromDailyMcTickets);
}
