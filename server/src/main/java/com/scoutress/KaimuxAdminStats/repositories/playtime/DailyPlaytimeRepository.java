package com.scoutress.KaimuxAdminStats.repositories.playtime;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.scoutress.KaimuxAdminStats.entity.playtime.DailyPlaytime;

public interface DailyPlaytimeRepository extends JpaRepository<DailyPlaytime, Long> {

  DailyPlaytime findByEmployeeIdAndDateAndServer(Short employeeId, LocalDate date, String server);

  @Query("SELECT MAX(d.date) FROM DailyPlaytime d")
  LocalDate findLatestDate();

}
