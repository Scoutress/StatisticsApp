package com.scoutress.KaimuxAdminStats.repositories.productivity;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.productivity.DailyObjectiveProductivity;

public interface DailyObjectiveProductivityRepository extends JpaRepository<DailyObjectiveProductivity, Long> {

  Optional<DailyObjectiveProductivity> findTopByOrderByDateDesc();
}
