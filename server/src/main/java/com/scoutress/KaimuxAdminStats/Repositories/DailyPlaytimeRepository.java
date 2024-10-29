package com.scoutress.KaimuxAdminStats.Repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scoutress.KaimuxAdminStats.Entity.Playtime.DailyPlaytime;

@Repository
public interface DailyPlaytimeRepository extends JpaRepository<DailyPlaytime, Long> {

  DailyPlaytime findByEmployeeIdAndDate(Integer id, LocalDate date);

  List<DailyPlaytime> findByEmployeeId(Integer id);

  @Query("SELECT SUM(p.totalPlaytime) FROM DailyPlaytime p WHERE p.employeeId = :employeeId AND p.date BETWEEN :startDate AND :endDate")
  Double sumPlaytimeByEmployeeAndDateRange(@Param("employeeId") Integer employeeId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);

  @Query("SELECT DISTINCT p.employeeId FROM DailyPlaytime p")
  List<Integer> findAllDistinctEmployeeIds();

  @Query("SELECT MIN(p.date) FROM DailyPlaytime p WHERE p.employeeId = :employeeId")
  LocalDate findEarliestPlaytimeDateByEmployeeId(@Param("employeeId") Integer employeeId);

  @Query("SELECT MAX(p.date) FROM DailyPlaytime p WHERE p.employeeId = :employeeId")
  LocalDate findLatestPlaytimeDateByEmployeeId(@Param("employeeId") Integer employeeId);

  @Query("SELECT SUM(p.totalPlaytime) FROM DailyPlaytime p WHERE p.employeeId = :employeeId")
  Double getTotalPlaytimeByEmployeeId(@Param("employeeId") Integer employeeId);

  @Query("SELECT d FROM DailyPlaytime d WHERE d.employeeId = :employeeId AND d.date BETWEEN :startDate AND :endDate")
  List<DailyPlaytime> findByEmployeeIdAndDateRange(@Param("employeeId") Integer employeeId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);
}
