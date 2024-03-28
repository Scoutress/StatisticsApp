package lt.scoutress.StatisticsApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsCalculations;

@Repository
public interface McTicketsCalcRepository extends JpaRepository<McTicketsCalculations, Integer> {
    
}
