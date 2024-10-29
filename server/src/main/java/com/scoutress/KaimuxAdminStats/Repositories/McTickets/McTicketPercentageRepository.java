package com.scoutress.KaimuxAdminStats.Repositories.McTickets;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.scoutress.KaimuxAdminStats.Entity.McTickets.McTicketPercentage;

public interface McTicketPercentageRepository extends JpaRepository<McTicketPercentage, Long> {

  @Query("SELECT m FROM McTicketPercentage m WHERE m.employeeId = :employeeId AND m.date = :date")
  Optional<McTicketPercentage> findFirstByEmployeeIdAndDate(@Param("employeeId") Integer employeeId,
      @Param("date") LocalDate date);

  @Query("SELECT SUM(m.percentage) FROM McTicketPercentage m WHERE m.employeeId = :employeeId AND m.date BETWEEN :startDate AND :endDate")
  Optional<Double> sumTicketsByEmployeeIdAndDateRange(@Param("employeeId") Integer employeeId,
      @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}