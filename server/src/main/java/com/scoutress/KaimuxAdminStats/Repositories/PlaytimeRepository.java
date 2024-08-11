package com.scoutress.KaimuxAdminStats.Repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.scoutress.KaimuxAdminStats.Entity.Playtime.Playtime;

public interface PlaytimeRepository extends JpaRepository<Playtime, Long> {

  List<Playtime> findByDate(LocalDate date);

  List<Playtime> findByEmployeeIdAndDateBetween(Integer employeeId, LocalDate startDate, LocalDate endDate);

  @Query("SELECT SUM(p.hoursPlayed) FROM Playtime p WHERE p.employeeId = :employeeId AND p.date BETWEEN :startDate AND :endDate")
  Double sumPlaytimeByEmployeeAndDateRange(Long employeeId, LocalDate startDate, LocalDate endDate);

  @Query("SELECT DISTINCT p.employeeId FROM Playtime p")
  List<Integer> findAllDistinctEmployeeIds();

  @Query("SELECT DISTINCT p.employeeId FROM Playtime p")
  List<Integer> findAllEmployeeIds();

  @Query("SELECT MIN(p.date) FROM Playtime p WHERE p.employeeId = :employeeId")
  LocalDate findEarliestPlaytimeDateByEmployeeId(@Param("employeeId") Integer employeeId);

  @Query("SELECT MAX(p.date) FROM Playtime p WHERE p.employeeId = :employeeId")
  LocalDate findLatestPlaytimeDateByEmployeeId(@Param("employeeId") Integer employeeId);

  @Query("SELECT SUM(p.hoursPlayed) FROM Playtime p WHERE p.employeeId = :employeeId AND p.date BETWEEN :startDate AND :endDate")
  Double sumPlaytimeByEmployeeAndDateRange(@Param("employeeId") Integer employeeId,
      @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

  @Query("SELECT SUM(p.hoursPlayed) FROM Playtime p WHERE p.employeeId = :employeeId")
  Double getTotalPlaytimeByEmployeeId(Integer employeeId);

  @Query("SELECT SUM(p.afkPlaytime) FROM Playtime p WHERE p.employeeId = :employeeId")
  Double getTotalAfkPlaytimeByEmployeeId(Integer employeeId);
}