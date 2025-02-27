package com.scoutress.KaimuxAdminStats.services.minecraftTickets;

import java.time.LocalDate;
import java.util.List;

import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.DailyMinecraftTickets;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.MinecraftTicketsAnswers;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.TotalOldMinecraftTickets;
import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;

public interface MinecraftTicketsService {

  void convertRawMcTicketsData(
      List<MinecraftTicketsAnswers> rawMcTicketsData,
      List<EmployeeCodes> employeeCodes,
      List<Short> allEmployeeIds);

  void calcAvgDailyMcTicketsPerEmployee(
      List<Short> allEmployeeIds,
      List<Employee> rawEmployeesData,
      LocalDate oldestDateFromData,
      List<DailyMinecraftTickets> rawData);

  void calcAvgMcTicketsPerPlaytime(
      List<DailyMinecraftTickets> rawData,
      List<Employee> rawEmployeesData,
      List<Short> allEmployeeIds,
      LocalDate oldestDateFromData,
      List<DailyPlaytime> playtimeData);

  void calcTotalMinecraftTickets(
      List<Short> allEmployeeIds,
      List<DailyMinecraftTickets> rawDailyMcTicketsData,
      List<TotalOldMinecraftTickets> allOldTotalMinecraftTicketsData);

  double getSumOfMcTicketsByEmployeeIdAndDuration(Short employeeId, Short days);
}
