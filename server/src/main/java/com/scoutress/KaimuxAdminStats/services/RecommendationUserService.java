package com.scoutress.KaimuxAdminStats.services;

import java.util.Map;

public interface RecommendationUserService {

  void handleUserRecommendations();

  Map<String, Object> getEmployeeRecommendation(Short employeeId);
}
