package com.scoutress.KaimuxAdminStats.services.playtime;

import java.util.List;

import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;
import com.scoutress.KaimuxAdminStats.entity.playtime.AveragePlaytimeLastYear;
import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;

public interface AveragePlaytimeLastYearService {

  public void handleAveragePlaytime();

  public List<AveragePlaytimeLastYear> calculateAveragePlaytime(
      List<DailyPlaytime> allPlaytimes,
      List<EmployeeCodes> allEmployeeAids,
      List<Employee> allEmployees);

  public void saveAveragePlaytime(
      List<AveragePlaytimeLastYear> averagePlaytimeData);
}
