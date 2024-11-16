package com.scoutress.KaimuxAdminStats.repositories.productivity;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.productivity.SubjectiveProductivity;

public interface SubjectiveProductivityRepository extends JpaRepository<SubjectiveProductivity, Long> {
}
