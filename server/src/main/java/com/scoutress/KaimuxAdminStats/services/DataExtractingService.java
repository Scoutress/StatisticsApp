package com.scoutress.KaimuxAdminStats.services;

import java.util.List;

import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;
import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.entity.playtime.SanitizedSessionData;
import com.scoutress.KaimuxAdminStats.entity.playtime.SessionDataItem;
import com.scoutress.KaimuxAdminStats.entity.playtime.SessionDuration;

public interface DataExtractingService {

  public List<SessionDataItem> getLoginLogoutTimes();

  public List<SanitizedSessionData> getSanitizedLoginLogoutTimes();

  public List<SessionDuration> getSessionDurations();

  public List<DailyPlaytime> getDailyPlaytimeData();

  public List<EmployeeCodes> getAidsFromEmployeeCodes();

  public List<Employee> getAllEmployees();
}