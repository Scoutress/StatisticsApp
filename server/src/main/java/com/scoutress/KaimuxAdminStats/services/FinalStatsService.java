package com.scoutress.KaimuxAdminStats.services;

import java.util.List;
import java.util.Map;

import com.scoutress.KaimuxAdminStats.entity.FinalStats;

public interface FinalStatsService {

  void handleFinalStats();

  List<FinalStats> getAllFinalStats();

  double getProductivity(Short employeeId);

  Map<String, Object> getEmployeeRanking(Short employeeId);
}
