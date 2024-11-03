package com.scoutress.KaimuxAdminStats.Repositories.playtime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.Entity.playtime.NEW_DailyPlaytime;

public interface NEW_DailyPlaytimeRepository extends JpaRepository<NEW_DailyPlaytime, Long> {
}
