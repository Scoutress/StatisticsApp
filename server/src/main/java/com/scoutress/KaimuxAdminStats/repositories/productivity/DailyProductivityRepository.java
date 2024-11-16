package com.scoutress.KaimuxAdminStats.repositories.productivity;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.productivity.DailyProductivity;

public interface DailyProductivityRepository extends JpaRepository<DailyProductivity, Long> {
}
