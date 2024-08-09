package com.scoutress.KaimuxAdminStats.Repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scoutress.KaimuxAdminStats.Entity.Playtime.DailyPlaytime;

@Repository
public interface DailyPlaytimeRepository extends JpaRepository<DailyPlaytime, Integer> {

    DailyPlaytime findByDate(LocalDate date);

    List<DailyPlaytime> findAllByDate(LocalDate date);    
}
