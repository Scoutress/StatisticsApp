package com.scoutress.KaimuxAdminStats.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.ProjectVisitorsRawData;

public interface ProjectVisitorsRawDataRepository extends JpaRepository<ProjectVisitorsRawData, Long> {
}
