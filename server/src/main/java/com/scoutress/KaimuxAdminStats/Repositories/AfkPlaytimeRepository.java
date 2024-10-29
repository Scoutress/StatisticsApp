package com.scoutress.KaimuxAdminStats.Repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.scoutress.KaimuxAdminStats.Entity.Playtime.AfkPlaytime;

public interface AfkPlaytimeRepository extends JpaRepository<AfkPlaytime, Long> {

  List<AfkPlaytime> findByDate(LocalDate date);

  List<AfkPlaytime> findByEmployeeIdAndDateBetween(Integer employeeId, LocalDate startDate, LocalDate endDate);

  @Query("SELECT SUM(p.afkPlaytime) FROM AfkPlaytime p WHERE p.employeeId = :employeeId")
  Double getTotalAfkPlaytimeByEmployeeId(@Param("employeeId") Integer employeeId);

  List<AfkPlaytime> findByEmployeeId(Integer employeeId);

  @Query("SELECT a FROM AfkPlaytime a WHERE a.employeeId = :employeeId AND a.date BETWEEN :startDate AND :endDate")
  List<AfkPlaytime> findByEmployeeIdAndDateRange(@Param("employeeId") Integer employeeId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);
}
