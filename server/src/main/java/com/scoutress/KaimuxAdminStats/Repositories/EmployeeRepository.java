package com.scoutress.KaimuxAdminStats.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.scoutress.KaimuxAdminStats.Entity.Employees.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    default List<Employee> getAllEmployees() {
        return findAll();
    }

    @Query("SELECT e FROM Employee e ORDER BY CASE e.level " +
            "WHEN 'Owner' THEN 1 " +
            "WHEN 'Coder' THEN 2 " +
            "WHEN 'Operator' THEN 3 " +
            "WHEN 'Manager' THEN 4 " +
            "WHEN 'Organizer' THEN 5 " +
            "WHEN 'Overseer' THEN 6 " +
            "WHEN 'ChatMod' THEN 7 " +
            "WHEN 'Support' THEN 8 " +
            "WHEN 'Helper' THEN 9 " +
            "ELSE 10 END")
    List<Employee> findAllByOrderByLevel();

    @Query("SELECT e.id FROM Employee e")
    List<Integer> findAllEmployeeIds();
}
