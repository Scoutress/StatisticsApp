package com.scoutress.KaimuxAdminStats.Repositories.DcTickets;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scoutress.KaimuxAdminStats.Entity.DcTickets.DcTicket;

@Repository
public interface DcTicketRepository extends JpaRepository<DcTicket, Long> {

  @Query("SELECT DISTINCT p.date FROM DcTicket p")
  List<LocalDate> findAllDates();

  @Query("SELECT SUM(p.ticketCount) FROM DcTicket p WHERE p.date = :date")
  double sumByDate(@Param("date") LocalDate date);

  @Query("SELECT COALESCE(SUM(p.ticketCount), 0) FROM DcTicket p WHERE p.employeeId = :employeeId AND p.date = :date")
  double findAnsweredDiscordTicketsByEmployeeIdAndDate(@Param("employeeId") Integer employeeId, @Param("date") LocalDate date);
}