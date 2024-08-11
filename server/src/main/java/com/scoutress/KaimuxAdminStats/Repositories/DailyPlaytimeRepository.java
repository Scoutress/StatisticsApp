package com.scoutress.KaimuxAdminStats.Repositories;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scoutress.KaimuxAdminStats.Entity.Playtime.DailyPlaytime;

@Repository
public interface DailyPlaytimeRepository extends JpaRepository<DailyPlaytime, Long> {

  DailyPlaytime findByEmployeeIdAndDate(Integer id, LocalDate date);

}
