package com.scoutress.KaimuxAdminStats.Repositories.McTickets;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scoutress.KaimuxAdminStats.entity.Employees.Employee;
import com.scoutress.KaimuxAdminStats.entity.McTickets.McTickets;

@Repository
public interface McTicketsRepository extends JpaRepository<McTickets, Integer> {

    default List<McTickets> getAllMcTickets() {
        return findAll();
    }

    List<McTickets> findByEmployee(Employee employee);
}
