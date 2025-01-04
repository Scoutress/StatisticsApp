package com.scoutress.KaimuxAdminStats.services;

import java.util.List;

import com.scoutress.KaimuxAdminStats.entity.FinalStats;

public interface FinalStatsService {

  void updateNewStatsData();

  List<FinalStats> getAllFinalStats();
}
