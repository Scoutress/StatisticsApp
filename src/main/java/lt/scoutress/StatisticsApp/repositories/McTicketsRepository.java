package lt.scoutress.StatisticsApp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lt.scoutress.StatisticsApp.entity.McTickets.McTickets;

@Repository
public interface McTicketsRepository extends JpaRepository<McTickets, Integer> {

    default List<McTickets> getAllMcTickets() {
        return findAll();
    }

    // @Query("SELECT MIN(m.date) FROM McTicketsCounting m")
    // LocalDate findOldestDate();

    // @Query("SELECT MAX(m.date) FROM McTicketsCounting m")
    // LocalDate findNewestDate();
}
