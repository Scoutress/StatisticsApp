package com.scoutress.KaimuxAdminStats.services.playtime;

import java.util.List;

import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;
import com.scoutress.KaimuxAdminStats.entity.playtime.AveragePlaytimeOverall;
import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;

public interface AveragePlaytimeOverallService {

  public void handleAveragePlaytime();

  public List<AveragePlaytimeOverall> calculateAveragePlaytime(
      List<DailyPlaytime> allPlaytimes,
      List<EmployeeCodes> allEmployeeAids,
      List<Employee> allEmployees);

  public void saveAveragePlaytime(
      List<AveragePlaytimeOverall> averagePlaytimeData);
}
