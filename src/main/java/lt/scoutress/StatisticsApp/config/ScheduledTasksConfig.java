package lt.scoutress.StatisticsApp.Config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import jakarta.transaction.Transactional;
import lt.scoutress.StatisticsApp.Repositories.EmployeeRepository;
import lt.scoutress.StatisticsApp.Repositories.McTickets.McTicketsAvgDailyRatioRepository;
import lt.scoutress.StatisticsApp.Repositories.McTickets.McTicketsRepository;
import lt.scoutress.StatisticsApp.Services.ProductivityService;
import lt.scoutress.StatisticsApp.Servicesimpl.McTickets.McTicketsServiceImpl;
import lt.scoutress.StatisticsApp.entity.Employees.Employee;
import lt.scoutress.StatisticsApp.entity.McTickets.McTickets;
import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsAvgDailyRatio;

@Configuration
@EnableScheduling
public class ScheduledTasksConfig {

    private final EmployeeRepository employeeRepository;
    private final McTicketsRepository mcTicketsRepository;
    private final McTicketsAvgDailyRatioRepository mcTicketsAvgDailyRatioRepository;
    private final McTicketsServiceImpl mcTicketsServiceImpl;
    private final ProductivityService productivityService;

    public ScheduledTasksConfig(EmployeeRepository employeeRepository, McTicketsServiceImpl mcTicketsServiceImpl,
            McTicketsRepository mcTicketsRepository, McTicketsAvgDailyRatioRepository mcTicketsAvgDailyRatioRepository,
            ProductivityService productivityService) {
        this.employeeRepository = employeeRepository;
        this.mcTicketsRepository = mcTicketsRepository;
        this.mcTicketsAvgDailyRatioRepository = mcTicketsAvgDailyRatioRepository;
        this.mcTicketsServiceImpl = mcTicketsServiceImpl;
        this.productivityService = productivityService;
    }

    // For copy-paste (DEBUG)
    // @Scheduled(cron = "0 * * * * *")
    // @Scheduled(cron = "15 * * * * *")
    // @Scheduled(cron = "30 * * * * *")
    // @Scheduled(cron = "45 * * * * *")

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void runTask1() {
        System.out.println("Scheduled task 1 is started");
        List<Employee> employees = employeeRepository.findAll();
        for (Employee employee : employees) {
            mcTicketsServiceImpl.calculateMcTicketsAvgDaily(employee);
        }
        System.out.println("Scheduled task 1 is completed");
    }

    @Scheduled(cron = "0 1 * * * *")
    @Transactional
    public void runTask2() {
        System.out.println("Scheduled task 2 is started");
        List<McTickets> mcTicketsList = mcTicketsRepository.findAll();
        List<McTicketsAvgDailyRatio> mcTicketsAvgDailyRatioList = mcTicketsServiceImpl
                .calculateMcTicketsAvgDailyRatio(mcTicketsList);
        mcTicketsAvgDailyRatioRepository.saveAll(mcTicketsAvgDailyRatioList);
        System.out.println("Scheduled task 2 is completed");
    }

    @Scheduled(cron = "0 2 * * * *")
    @Transactional
    public void runTask3() {
        System.out.println("Scheduled task 3 is started");
        productivityService.createOrUpdateProductivityForAllEmployees();
        System.out.println("Scheduled task 3 is completed");
    }
}
