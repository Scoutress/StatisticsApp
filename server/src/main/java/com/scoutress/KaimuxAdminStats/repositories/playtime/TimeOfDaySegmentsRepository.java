package com.scoutress.KaimuxAdminStats.repositories.playtime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.playtime.TimeOfDaySegments;

public interface TimeOfDaySegmentsRepository extends JpaRepository<TimeOfDaySegments, Long> {
  long countByEmployeeIdAndServerAndTimeSegment(Short employeeId, String server, int timeSegment);
}
