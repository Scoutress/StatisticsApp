package com.scoutress.KaimuxAdminStats.Repositories.playtime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.Entity.playtime.NEW_SessionDuration;

public interface NEW_ProcessedPlaytimeSessionsRepository extends JpaRepository<NEW_SessionDuration, Long> {
}
