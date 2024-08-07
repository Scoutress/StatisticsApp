package lt.scoutress.StatisticsApp.Repositories.McTickets;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsAvgDaily;

@Repository
public interface McTicketsAvgValuesRepository extends JpaRepository<McTicketsAvgDaily, Integer> {

    default List<McTicketsAvgDaily> getAllMcTickets() {
        return findAll();
    }

    McTicketsAvgDaily findByEmployeeId(Integer id);
}
