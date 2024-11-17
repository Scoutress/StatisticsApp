package com.scoutress.KaimuxAdminStats.repositories.afkPlaytime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.afkPlaytime.DailyAfkPlaytime;

public interface DailyAfkPlaytimeRepository extends JpaRepository<DailyAfkPlaytime, Long> {
}
