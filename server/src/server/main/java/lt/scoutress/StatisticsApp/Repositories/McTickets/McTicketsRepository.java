package lt.scoutress.StatisticsApp.Repositories.McTickets;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lt.scoutress.StatisticsApp.entity.Employees.Employee;
import lt.scoutress.StatisticsApp.entity.McTickets.McTickets;

@Repository
public interface McTicketsRepository extends JpaRepository<McTickets, Integer> {

    default List<McTickets> getAllMcTickets() {
        return findAll();
    }

    List<McTickets> findByEmployee(Employee employee);
}
