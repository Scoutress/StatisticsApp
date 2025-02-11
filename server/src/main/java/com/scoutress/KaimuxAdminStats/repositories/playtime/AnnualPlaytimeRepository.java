package com.scoutress.KaimuxAdminStats.repositories.playtime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.playtime.AnnualPlaytime;

public interface AnnualPlaytimeRepository extends JpaRepository<AnnualPlaytime, Long> {

  AnnualPlaytime findByEmployeeId(Short employeeId);
}
