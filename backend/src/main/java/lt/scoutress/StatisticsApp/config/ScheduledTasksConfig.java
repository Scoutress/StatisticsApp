package lt.scoutress.StatisticsApp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import lt.scoutress.StatisticsApp.servicesimpl.ProductivityServiceImpl;
import lt.scoutress.StatisticsApp.servicesimpl.StatisticsServiceImpl;

@Configuration
@EnableScheduling
public class ScheduledTasksConfig {

    private final StatisticsServiceImpl statisticsServiceImpl;
    private final ProductivityServiceImpl productivityServiceImpl;

    public ScheduledTasksConfig(StatisticsServiceImpl statisticsServiceImpl, ProductivityServiceImpl productivityServiceImpl) {
        this.statisticsServiceImpl = statisticsServiceImpl;
        this.productivityServiceImpl = productivityServiceImpl;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void runTask1() {
        statisticsServiceImpl.calculateDaysSinceJoinAndSave();
        System.out.println("Scheduled task 1 is completed");
    }

    @Scheduled(cron = "0 2 * * * *")
    public void runTask2() {
        statisticsServiceImpl.calculateTotalDailyMcTickets();
        System.out.println("Scheduled task 2 is completed");
    }

    @Scheduled(cron = "0 4 * * * *")
    public void runTask3() {
        statisticsServiceImpl.calculateDailyTicketDifference();
        System.out.println("Scheduled task 3 is completed");
    }
    
    @Scheduled(cron = "0 6 * * * *")
    public void runTask4() {
        statisticsServiceImpl.calculateDailyTicketRatio();
        System.out.println("Scheduled task 4 is completed");
    }

    @Scheduled(cron = "0 8 * * * *")
    public void runTask5() {
        statisticsServiceImpl.calculateTotalDailyDcMessages();
        System.out.println("Scheduled task 5 is completed");
    }

    @Scheduled(cron = "0 10 * * * *")
    public void runTask6() {
        statisticsServiceImpl.calculateDailyDcMessagesRatio();
        System.out.println("Scheduled task 6 is completed");
    }
    
    @Scheduled(cron = "0 12 * * * *")
    public void runTask7() {
        productivityServiceImpl.copyUsernamesAndLevels();
        System.out.println("Scheduled task 7 is completed");
    }
    
    @Scheduled(cron = "0 14 * * * *")
    public void runTask8() {
        statisticsServiceImpl.calculateAvgDailyDcMessages();
        System.out.println("Scheduled task 8 is completed");
    }

    @Scheduled(cron = "0 16 * * * *")
    public void runTask9() {
        statisticsServiceImpl.calculateAvgDailyDcMessagesRatio();
        System.out.println("Scheduled task 9 is completed");
    }
}
