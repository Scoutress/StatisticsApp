package lt.scoutress.StatisticsApp.repositories;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsCounting;

@Repository
public interface McTicketsRepository extends JpaRepository<McTicketsCounting, Integer> {

    @Query("SELECT MIN(m.date) FROM McTicketsCounting m")
    LocalDate findOldestDate();

    @Query("SELECT MAX(m.date) FROM McTicketsCounting m")
    LocalDate findNewestDate();
}
