package com.scoutress.KaimuxAdminStats.Repositories.playtime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.Entity.playtime.NEW_SessionDataItem;

public interface NEW_PlaytimeSessionsRepository extends JpaRepository<NEW_SessionDataItem, Long> {
}
