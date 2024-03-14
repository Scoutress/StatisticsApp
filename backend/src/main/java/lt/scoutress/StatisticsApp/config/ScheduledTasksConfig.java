package lt.scoutress.StatisticsApp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import lt.scoutress.StatisticsApp.servicesimpl.StatisticsServiceImpl;

@Configuration
@EnableScheduling
public class ScheduledTasksConfig {

    private final StatisticsServiceImpl statisticsServiceImpl;

    public ScheduledTasksConfig(StatisticsServiceImpl statisticsServiceImpl) {
        this.statisticsServiceImpl = statisticsServiceImpl;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void runTask1() {
        statisticsServiceImpl.calculateDaysSinceJoinAndSave();
        System.out.println("Scheduled 1 is completed");
    }

    @Scheduled(cron = "0 2 * * * *")
    public void runTask2() {
        statisticsServiceImpl.calculateTotalDailyMcTickets();
        System.out.println("Scheduled 2 is completed");
    }

    @Scheduled(cron = "0 4 * * * *")
    public void runTask3() {
        statisticsServiceImpl.calculateDailyTicketDifference();
        System.out.println("Scheduled 3 is completed");
    }
    
    @Scheduled(cron = "0 6 * * * *")
    public void runTask4() {
        statisticsServiceImpl.calculateDailyTicketRatio();
        System.out.println("Scheduled 4 is completed");
    }

    @Scheduled(cron = "0 8 * * * *")
    public void runTask5() {
        statisticsServiceImpl.calculateTotalDailyDcMessages();
        System.out.println("Scheduled 5 is completed");
    }

    @Scheduled(cron = "0 10 * * * *")
    public void runTask6() {
        statisticsServiceImpl.calculateDailyDcMessagesRatio();
        System.out.println("Scheduled 6 is completed");
    }
    
    @Scheduled(cron = "0 12 * * * *")
    public void runTask7() {
        statisticsServiceImpl.calculateAvgDailyDcMessages();
        System.out.println("Scheduled 7 is completed");
    }
}
