package lt.scoutress.StatisticsApp.servicesimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import lt.scoutress.StatisticsApp.entity.Employee;
import lt.scoutress.StatisticsApp.entity.McTickets.McTickets;
import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsAvgDaily;
import lt.scoutress.StatisticsApp.repositories.McTickets.McTicketsAvgValuesRepository;
import lt.scoutress.StatisticsApp.services.McTickets.McTicketsService;

@Service
public class McTicketsServiceImpl implements McTicketsService{

    private final EntityManager entityManager;
    private final McTicketsAvgValuesRepository mcTicketsAvgValuesRepository;

    public McTicketsServiceImpl(EntityManager entityManager, McTicketsAvgValuesRepository mcTicketsAvgValuesRepository) {
        this.entityManager = entityManager;
        this.mcTicketsAvgValuesRepository = mcTicketsAvgValuesRepository;
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

    @Override
    public List<McTicketsAvgDaily> findAll() {
        return mcTicketsAvgValuesRepository.findAll();
    }
}
