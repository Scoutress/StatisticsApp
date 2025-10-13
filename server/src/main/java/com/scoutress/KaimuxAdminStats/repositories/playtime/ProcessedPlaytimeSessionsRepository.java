package com.scoutress.KaimuxAdminStats.repositories.playtime;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.playtime.SessionDuration;

public interface ProcessedPlaytimeSessionsRepository extends JpaRepository<SessionDuration, Long> {

  List<SessionDuration> findByEmployeeId(Short employeeId);
}
