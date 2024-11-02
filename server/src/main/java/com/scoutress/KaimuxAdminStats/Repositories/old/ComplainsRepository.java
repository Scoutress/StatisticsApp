package com.scoutress.KaimuxAdminStats.Repositories.old;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.scoutress.KaimuxAdminStats.Entity.old.Complains;

public interface ComplainsRepository extends JpaRepository<Complains, Long> {
  List<Complains> findByEmployeeId(Integer employeeId);

  List<Complains> findByDate(LocalDate date);

  @Query("SELECT SUM(c.complainsCount) FROM Complains c WHERE c.employeeId = :employeeId")
  Double sumComplaintsByEmployeeId(@Param("employeeId") Integer employeeId);
}
