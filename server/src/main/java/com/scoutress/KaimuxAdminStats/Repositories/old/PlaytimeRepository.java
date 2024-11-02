package com.scoutress.KaimuxAdminStats.Repositories.old;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.scoutress.KaimuxAdminStats.Entity.old.Playtime.AfkPlaytime;

public interface PlaytimeRepository extends JpaRepository<AfkPlaytime, Long> {

  List<AfkPlaytime> findByDate(LocalDate date);

  List<AfkPlaytime> findByEmployeeIdAndDateBetween(Integer employeeId, LocalDate startDate, LocalDate endDate);

  @Query("SELECT SUM(p.totalPlaytime) FROM DailyPlaytime p WHERE p.employeeId = :employeeId AND p.date BETWEEN :startDate AND :endDate")
  Double sumPlaytimeByEmployeeAndDateRange(Long employeeId, LocalDate startDate, LocalDate endDate);

  @Query("SELECT DISTINCT p.employeeId FROM DailyPlaytime p")
  List<Integer> findAllDistinctEmployeeIds();

  @Query("SELECT DISTINCT p.employeeId FROM DailyPlaytime p")
  List<Integer> findAllEmployeeIds();

  @Query("SELECT MIN(p.date) FROM DailyPlaytime p WHERE p.employeeId = :employeeId")
  LocalDate findEarliestPlaytimeDateByEmployeeId(@Param("employeeId") Integer employeeId);

  @Query("SELECT MAX(p.date) FROM DailyPlaytime p WHERE p.employeeId = :employeeId")
  LocalDate findLatestPlaytimeDateByEmployeeId(@Param("employeeId") Integer employeeId);

  @Query("SELECT SUM(p.totalPlaytime) FROM DailyPlaytime p WHERE p.employeeId = :employeeId AND p.date BETWEEN :startDate AND :endDate")
  Double sumPlaytimeByEmployeeAndDateRange(@Param("employeeId") Integer employeeId,
      @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

  @Query("SELECT SUM(p.totalPlaytime) FROM DailyPlaytime p WHERE p.employeeId = :employeeId")
  Double getTotalPlaytimeByEmployeeId(Integer employeeId);

  @Query("SELECT SUM(p.afkPlaytime) FROM AfkPlaytime p WHERE p.employeeId = :employeeId")
  Double getTotalAfkPlaytimeByEmployeeId(Integer employeeId);
}