package com.scoutress.KaimuxAdminStats.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scoutress.KaimuxAdminStats.Entity.Employees.Employee;
import com.scoutress.KaimuxAdminStats.Entity.Productivity;

@Repository
public interface ProductivityRepository extends JpaRepository<Productivity, Integer> {

    @Query("SELECT p FROM Productivity p JOIN FETCH p.employee e ORDER BY " +
            "CASE e.level WHEN 'Owner' THEN 1 WHEN 'Coder' THEN 2 WHEN 'Operator' THEN 3 WHEN 'Manager' THEN 4 WHEN 'Organizer' THEN 5 WHEN 'Overseer' THEN 6 WHEN 'ChatMod' THEN 7 WHEN 'Support' THEN 8 WHEN 'Helper' THEN 9 ELSE 10 END")
    List<Productivity> findAllWithEmployeeDetails();

    Productivity findByEmployeeId(Integer employeeId);

    Optional<Productivity> findByEmployee(Employee employee);

    @Query("SELECT p.serverTickets FROM Productivity p WHERE p.employee.id = :employeeId")
    Double findServerTicketsByEmployeeId(@Param("employeeId") Integer employeeId);

    @Query("SELECT p.serverTicketsTaking FROM Productivity p WHERE p.employee.id = :employeeId")
    Double findServerTicketsTakenByEmployeeId(@Param("employeeId") Integer employeeId);

    @Query("SELECT p.playtime FROM Productivity p WHERE p.employee.id = :employeeId")
    Double findPlaytimeByEmployeeId(@Param("employeeId") Integer employeeId);

    @Query("SELECT p.afkPlaytime FROM Productivity p WHERE p.employee.id = :employeeId")
    Double findAfkPlaytimeByEmployeeId(@Param("employeeId") Integer employeeId);
}
