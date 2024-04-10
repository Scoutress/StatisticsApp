package lt.scoutress.StatisticsApp.Repositories.McTickets;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsAvgDailyRatio;

@Repository
public interface McTicketsAvgDailyRatioRepository extends JpaRepository<McTicketsAvgDailyRatio, Integer> {

}
