package lt.scoutress.StatisticsApp.repositories;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lt.scoutress.StatisticsApp.entity.McTickets;

@Repository
public interface McTicketsRepository extends JpaRepository<McTickets, Integer> {

    McTickets findByDate(LocalDate minusDays);

}
