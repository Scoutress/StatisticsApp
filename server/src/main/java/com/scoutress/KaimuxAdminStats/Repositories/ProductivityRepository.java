package com.scoutress.KaimuxAdminStats.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.scoutress.KaimuxAdminStats.Entity.Productivity;

@Repository
public interface ProductivityRepository extends JpaRepository<Productivity, Integer> {

    @Query("SELECT p FROM Productivity p JOIN FETCH p.employee e ORDER BY " +
           "CASE e.level WHEN 'Owner' THEN 1 WHEN 'Coder' THEN 2 WHEN 'Operator' THEN 3 WHEN 'Manager' THEN 4 WHEN 'Organizer' THEN 5 WHEN 'Overseer' THEN 6 WHEN 'ChatMod' THEN 7 WHEN 'Support' THEN 8 WHEN 'Helper' THEN 9 ELSE 10 END")
    List<Productivity> findAllWithEmployeeDetails();

    Productivity findByEmployeeId(Integer employeeId);
    
    // Optional<Productivity> findByEmployeeId(Long employeeId);

    // Optional<Productivity> findByEmployeeId(Integer employeeId);
}
