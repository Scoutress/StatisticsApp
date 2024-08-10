package com.scoutress.KaimuxAdminStats.Repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.Entity.Playtime;

public interface PlaytimeRepository extends JpaRepository<Playtime, Long> {
  List<Playtime> findByDate(LocalDate date);
  List<Playtime> findByEmployeeIdAndDateBetween(Integer employeeId, LocalDate startDate, LocalDate endDate);
}