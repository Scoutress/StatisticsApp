package lt.scoutress.StatisticsApp.servicesimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import lt.scoutress.StatisticsApp.entity.Employee;
import lt.scoutress.StatisticsApp.entity.McTickets.McTickets;
import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsAvgDaily;
import lt.scoutress.StatisticsApp.services.McTicketsService;

@Service
public class McTicketsServiceImpl implements McTicketsService{

    private final EntityManager entityManager;

    public McTicketsServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void calculateMcTicketsAvgDaily(Employee employee) {
        List<McTickets> mcTickets = employee.getMcTickets();
        if (mcTickets != null && !mcTickets.isEmpty()) {
            double totalMcTicketsCount = mcTickets.stream().mapToInt(McTickets::getMcTicketsCount).sum();
            double averageMcTicketsCount = totalMcTicketsCount / mcTickets.size();
            McTicketsAvgDaily mcTicketsAvgDaily = new McTicketsAvgDaily(employee, averageMcTicketsCount);
            entityManager.persist(mcTicketsAvgDaily);
        }
    }
    
    
}
