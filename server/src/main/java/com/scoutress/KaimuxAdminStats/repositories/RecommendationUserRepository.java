package com.scoutress.KaimuxAdminStats.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.RecommendationUser;

public interface RecommendationUserRepository extends JpaRepository<RecommendationUser, Short> {

  RecommendationUser findByEmployeeId(Short employeeId);
}
