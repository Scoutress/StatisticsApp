package com.scoutress.KaimuxAdminStats.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.Entity.NEW_SessionDataItem;

public interface NEW_PlaytimeSessionsRepository extends JpaRepository<NEW_SessionDataItem, Long> {
}
