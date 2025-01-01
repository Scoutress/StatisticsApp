package com.scoutress.KaimuxAdminStats.repositories.playtime;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;

public interface DailyPlaytimeRepository extends JpaRepository<DailyPlaytime, Long> {

  DailyPlaytime findByAidAndDateAndServer(Short aid, LocalDate date, String server);
}
