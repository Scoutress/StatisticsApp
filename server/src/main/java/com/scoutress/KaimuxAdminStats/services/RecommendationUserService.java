package com.scoutress.KaimuxAdminStats.services;

import java.util.Map;

public interface RecommendationUserService {

  void checkAndSaveRecommendations();

  Map<String, Object> getEmployeeRecommendation(Short employeeId);
}
