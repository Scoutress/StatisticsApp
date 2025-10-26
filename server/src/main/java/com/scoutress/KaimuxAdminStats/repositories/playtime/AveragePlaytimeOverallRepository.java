package com.scoutress.KaimuxAdminStats.repositories.playtime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.playtime.AveragePlaytimeOverall;

public interface AveragePlaytimeOverallRepository extends JpaRepository<AveragePlaytimeOverall, Long> {

  AveragePlaytimeOverall findByEmployeeId(Short employeeId);
}
