package com.scoutress.KaimuxAdminStats.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.FinalStats;

public interface FinalStatsRepository extends JpaRepository<FinalStats, Long> {

  FinalStats findByEmployeeId(Short employeeId);
}
