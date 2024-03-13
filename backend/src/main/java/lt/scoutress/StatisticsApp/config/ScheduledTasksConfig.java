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
    }

    @Scheduled(cron = "0 2 * * * *")
    public void runTask2() {
        statisticsServiceImpl.calculateTotalDailyMcTickets();
    }

    @Scheduled(cron = "0 4 * * * *")
    public void runTask3() {
        statisticsServiceImpl.calculateDailyTicketDifference();
    }
    
    @Scheduled(cron = "0 6 * * * *")
    public void runTask4() {
        statisticsServiceImpl.calculateDailyTicketRatio();
    }
}
