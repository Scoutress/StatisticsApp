package com.scoutress.KaimuxAdminStats.Repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.scoutress.KaimuxAdminStats.Entity.Playtime;

public interface PlaytimeRepository extends JpaRepository<Playtime, Long> {

  List<Playtime> findByDate(LocalDate date);

  List<Playtime> findByEmployeeIdAndDateBetween(Integer employeeId, LocalDate startDate, LocalDate endDate);

  @Query("SELECT SUM(p.hoursPlayed) FROM Playtime p WHERE p.employeeId = :employeeId AND p.date BETWEEN :startDate AND :endDate")
  Double sumPlaytimeByEmployeeAndDateRange(Long employeeId, LocalDate startDate, LocalDate endDate);

  @Query("SELECT DISTINCT p.employeeId FROM Playtime p")
  List<Long> findAllDistinctEmployeeIds();
}