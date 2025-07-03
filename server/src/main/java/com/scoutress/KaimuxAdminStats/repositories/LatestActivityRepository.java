package com.scoutress.KaimuxAdminStats.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.LatestActivity;

public interface LatestActivityRepository extends JpaRepository<LatestActivity, Long> {

  LatestActivity findByEmployeeId(Short employeeId);
}
