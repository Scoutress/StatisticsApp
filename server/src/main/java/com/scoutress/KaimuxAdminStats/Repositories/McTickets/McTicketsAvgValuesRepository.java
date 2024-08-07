package com.scoutress.KaimuxAdminStats.Repositories.McTickets;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scoutress.KaimuxAdminStats.Entity.McTickets.McTicketsAvgDaily;

@Repository
public interface McTicketsAvgValuesRepository extends JpaRepository<McTicketsAvgDaily, Integer> {

    default List<McTicketsAvgDaily> getAllMcTickets() {
        return findAll();
    }

    McTicketsAvgDaily findByEmployeeId(Integer id);
}
