package com.scoutress.KaimuxAdminStats.repositories.playtime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.playtime.SessionDataItem;

public interface PlaytimeSessionsRepository extends JpaRepository<SessionDataItem, Long> {
}
