package com.scoutress.KaimuxAdminStats.repositories.productivity;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.productivity.DailySubjectiveProductivity;

public interface DailySubjectiveProductivityRepository extends JpaRepository<DailySubjectiveProductivity, Long> {
}
