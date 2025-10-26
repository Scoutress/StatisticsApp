package com.scoutress.KaimuxAdminStats.repositories.playtime;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.scoutress.KaimuxAdminStats.entity.playtime.TimeOfDaySegments;

public interface TimeOfDaySegmentsRepository extends JpaRepository<TimeOfDaySegments, Long> {

  @Query("SELECT t.employeeId, t.server, t.timeSegment, COUNT(t) " +
      "FROM TimeOfDaySegments t " +
      "GROUP BY t.employeeId, t.server, t.timeSegment")
  List<Object[]> findAllSegmentCounts();
}
