package com.scoutress.KaimuxAdminStats.Repositories.McTickets;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scoutress.KaimuxAdminStats.Entity.McTickets.McTicket;

@Repository
public interface McTicketRepository extends JpaRepository<McTicket, Long> {

  @Query("SELECT MIN(m.date) FROM McTicket m WHERE m.employeeId = :employeeId")
  Optional<LocalDate> findEarliestTicketDateByEmployeeId(@Param("employeeId") Integer employeeId);

  @Query("SELECT SUM(m.ticketCount) FROM McTicket m WHERE m.employeeId = :employeeId AND m.date BETWEEN :startDate AND :endDate")
  Optional<Double> sumTicketsByEmployeeIdAndDateRange(@Param("employeeId") Integer employeeId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);

}
