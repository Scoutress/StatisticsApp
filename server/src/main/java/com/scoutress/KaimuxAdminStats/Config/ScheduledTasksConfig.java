package com.scoutress.KaimuxAdminStats.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.scoutress.KaimuxAdminStats.utils.GetEmployeesDummyData;
import com.scoutress.KaimuxAdminStats.utils.GetProductivityDummyData;

import jakarta.transaction.Transactional;

@Configuration
@EnableScheduling
public class ScheduledTasksConfig {

    // private final EmployeeRepository employeeRepository;
    // private final McTicketsRepository mcTicketsRepository;
    // private final McTicketsAvgDailyRatioRepository mcTicketsAvgDailyRatioRepository;
    // private final McTicketsServiceImpl mcTicketsServiceImpl;
    // private final ProductivityService productivityService;
    private final GetEmployeesDummyData getEmployeesDummyData;
    private final GetProductivityDummyData getProductivityDummyData;

    public ScheduledTasksConfig(GetEmployeesDummyData getEmployeesDummyData , GetProductivityDummyData getProductivityDummyData) {
        this.getEmployeesDummyData = getEmployeesDummyData;
        this.getProductivityDummyData = getProductivityDummyData;
    }

    // For copy-paste (DEBUG)
    // @Scheduled(cron = "0 * * * * *")
    // @Scheduled(cron = "15 * * * * *")
    // @Scheduled(cron = "30 * * * * *")
    // @Scheduled(cron = "45 * * * * *")

    // @Scheduled(cron = "* * * * * *")
    @Transactional
    public void runTask1() {
        System.out.println("Employee dummy data filling is started");
        getEmployeesDummyData.createDummyEmployees();
        System.out.println("Employee dummy data filling is completed");
        System.out.println("Productivity dummy data filling is started");
        getProductivityDummyData.createDummyProductivity();
        System.out.println("Productivity dummy data filling is completed");
    }

    // @Scheduled(cron = "0 0 * * * *")
    // @Transactional
    // public void runTask1() {
    //     System.out.println("Scheduled task 1 is started");
    //     List<Employee> employees = employeeRepository.findAll();
    //     for (Employee employee : employees) {
    //         mcTicketsServiceImpl.calculateMcTicketsAvgDaily(employee);
    //     }
    //     System.out.println("Scheduled task 1 is completed");
    // }

    // @Scheduled(cron = "0 1 * * * *")
    // @Transactional
    // public void runTask2() {
    //     System.out.println("Scheduled task 2 is started");
    //     List<McTickets> mcTicketsList = mcTicketsRepository.findAll();
    //     List<McTicketsAvgDailyRatio> mcTicketsAvgDailyRatioList = mcTicketsServiceImpl
    //             .calculateMcTicketsAvgDailyRatio(mcTicketsList);
    //     mcTicketsAvgDailyRatioRepository.saveAll(mcTicketsAvgDailyRatioList);
    //     System.out.println("Scheduled task 2 is completed");
    // }

    // @Scheduled(cron = "0 2 * * * *")
    // @Transactional
    // public void runTask3() {
    //     System.out.println("Scheduled task 3 is started");
    //     productivityService.createOrUpdateProductivityForAllEmployees();
    //     System.out.println("Scheduled task 3 is completed");
    // }

    // @Scheduled(cron = "0 3 * * * *")
    // @Transactional
    // public void runTask4() {
    //     System.out.println("Scheduled task 4 is started");
    //     productivityService.copyMcTicketsValuesToProductivity();
    //     System.out.println("Scheduled task 4 is completed");
    // }
}
