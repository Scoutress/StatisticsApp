package com.scoutress.KaimuxAdminStats.repositories.playtime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.playtime.SessionDuration;

public interface ProcessedPlaytimeSessionsRepository extends JpaRepository<SessionDuration, Long> {
}
