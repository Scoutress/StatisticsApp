package lt.scoutress.StatisticsApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsCounting;

@Repository
public interface McTicketsRepository extends JpaRepository<McTicketsCounting, Integer> {
    
}
