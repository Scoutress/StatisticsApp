package lt.scoutress.StatisticsApp.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lt.scoutress.StatisticsApp.entity.Employee;
import lt.scoutress.StatisticsApp.entity.McTickets.McTickets;
import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsAvgDaily;
import lt.scoutress.StatisticsApp.repositories.EmployeeRepository;

@Configuration
@EnableScheduling
public class ScheduledTasksConfig {

    private final EntityManager entityManager;
    private final EmployeeRepository employeeRepository;

    public ScheduledTasksConfig(EmployeeRepository employeeRepository, EntityManager entityManager) {
        this.entityManager = entityManager;
        this.employeeRepository = employeeRepository;
    }

    // For copy-paste (DEBUG)
    // @Scheduled(cron = "0 * * * * *")
    // @Scheduled(cron = "15 * * * * *")
    // @Scheduled(cron = "30 * * * * *")
    // @Scheduled(cron = "45 * * * * *")

    @Scheduled(cron = "0 0 * * * *")
    @Scheduled(cron = "0 30 * * * *")
    @Transactional
    public void runTask1() {
        System.out.println("Scheduled task 1 is started");
        List<Employee> employees = employeeRepository.findAll();
        for (Employee employee : employees) {
            calculateMcTicketsAvgDaily(employee);
        }
        System.out.println("Scheduled task 1 is completed");
    }

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
