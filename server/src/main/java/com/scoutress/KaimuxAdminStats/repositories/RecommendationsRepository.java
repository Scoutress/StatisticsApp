package com.scoutress.KaimuxAdminStats.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.Recommendations;

public interface RecommendationsRepository extends JpaRepository<Recommendations, Long> {

  Recommendations findByEmployeeId(Short employeeId);
}
