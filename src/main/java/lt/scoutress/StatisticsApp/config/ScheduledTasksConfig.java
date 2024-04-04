package lt.scoutress.StatisticsApp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import lt.scoutress.StatisticsApp.servicesimpl.PlaytimeDBCodesServiceImpl;
import lt.scoutress.StatisticsApp.servicesimpl.PlaytimeServiceImpl;
import lt.scoutress.StatisticsApp.servicesimpl.ProductivityServiceImpl;
import lt.scoutress.StatisticsApp.servicesimpl.StatisticsServiceImpl;

@Configuration
@EnableScheduling
public class ScheduledTasksConfig {

    private final StatisticsServiceImpl statisticsServiceImpl;
    private final ProductivityServiceImpl productivityServiceImpl;
    private final PlaytimeServiceImpl playtimeServiceImpl;

    public ScheduledTasksConfig(StatisticsServiceImpl statisticsServiceImpl,
            ProductivityServiceImpl productivityServiceImpl, PlaytimeDBCodesServiceImpl playtimeDBCodesServiceImpl, PlaytimeServiceImpl playtimeServiceImpl) {
        this.statisticsServiceImpl = statisticsServiceImpl;
        this.productivityServiceImpl = productivityServiceImpl;
        this.playtimeServiceImpl = playtimeServiceImpl;
    }

    //1. Calculate days since join
    //2. Copy usernames and levels
    //3. Calculate daily MC tickets sum
    //4. Calculate daily MC tickets ratio per employee
    //5. Calculate daily DC messages sum
    //6. Calculate daily DC messages ratio per employee
    //7. Calculate avg. DC messages per emloyee per day
    //8. Calculate avg. DC messages ratio per employee per day
    //9. Calculate avg. MC tickets per employee per day
    //10. Calculate avg. MC tickets ratio per employee per day (not created yet)

    // For copy-paste (testing)
    // @Scheduled(cron = "0 * * * * *")
    // @Scheduled(cron = "15 * * * * *")
    // @Scheduled(cron = "30 * * * * *")
    // @Scheduled(cron = "45 * * * * *")

    @Scheduled(cron = "0 0 * * * *")
    @Scheduled(cron = "0 30 * * * *")
    public void runTask1() {
        System.out.println("Scheduled task 1 is started");
        statisticsServiceImpl.calculateDaysSinceJoinAndSave();
        System.out.println("Scheduled task 1 is completed");
    }

    @Scheduled(cron = "0 2 * * * *")
    @Scheduled(cron = "0 32 * * * *")
    public void runTask2() {
        System.out.println("Scheduled task 2 is started");
        productivityServiceImpl.copyUsernamesAndLevels();
        System.out.println("Scheduled task 2 is completed");
    }

    @Scheduled(cron = "0 4 * * * *")
    @Scheduled(cron = "0 34 * * * *")
    public void runTask3() {
        System.out.println("Scheduled task 3 is started");
        statisticsServiceImpl.calculateTotalDailyMcTickets();
        System.out.println("Scheduled task 3 is completed");
    }

    @Scheduled(cron = "0 6 * * * *")
    @Scheduled(cron = "0 36 * * * *")
    public void runTask4() {
        System.out.println("Scheduled task 4 is started");
        statisticsServiceImpl.calculateDailyTicketRatio();
        System.out.println("Scheduled task 4 is completed");
    }

    @Scheduled(cron = "0 8 * * * *")
    @Scheduled(cron = "0 38 * * * *")
    public void runTask5() {
        System.out.println("Scheduled task 5 is started");
        statisticsServiceImpl.calculateTotalDailyDcMessages();
        System.out.println("Scheduled task 5 is completed");
    }

    @Scheduled(cron = "0 10 * * * *")
    @Scheduled(cron = "0 40 * * * *")
    public void runTask6() {
        System.out.println("Scheduled task 6 is started");
        statisticsServiceImpl.calculateDailyDcMessagesRatio();
        System.out.println("Scheduled task 6 is completed");
    }

    @Scheduled(cron = "0 12 * * * *")
    @Scheduled(cron = "0 42 * * * *")
    public void runTask7() {
        System.out.println("Scheduled task 7 is started");
        statisticsServiceImpl.calculateAvgDailyDcMessages();
        System.out.println("Scheduled task 7 is completed");
    }

    @Scheduled(cron = "0 14 * * * *")
    @Scheduled(cron = "0 44 * * * *")
    public void runTask8() {
        System.out.println("Scheduled task 8 is started");
        statisticsServiceImpl.calculateAvgDailyDcMessagesRatio();
        System.out.println("Scheduled task 8 is completed");
    }

    @Scheduled(cron = "0 16 * * * *")
    @Scheduled(cron = "0 46 * * * *")
    public void runTask9() {
        System.out.println("Scheduled task 9 is started");
        statisticsServiceImpl.calculateAvgDailyMcTickets();
        System.out.println("Scheduled task 9 is completed");
    }

    //Temporary method
    // @Scheduled(cron = "0 20 * * * *")
    // @Scheduled(cron = "0 50 * * * *")
    // public void runTask11() {
    //     playtimeDBCodesServiceImpl.copyDBUsernames();
    //     System.out.println("Scheduled task 11 is completed");
    // }

    @Scheduled(cron = "0 18 * * * *")
    @Scheduled(cron = "0 48 * * * *")
    public void runTask10() {
        System.out.println("Scheduled task 10 is started");
        System.out.println("Survival:");
        playtimeServiceImpl.migrateSurvivalPlaytimeData();
        System.out.println("");
        System.out.println("Skyblock:");
        playtimeServiceImpl.migrateSkyblockPlaytimeData();
        System.out.println("");
        System.out.println("Creative:");
        playtimeServiceImpl.migrateCreativePlaytimeData();
        System.out.println("");
        System.out.println("Boxpvp:");
        playtimeServiceImpl.migrateBoxpvpPlaytimeData();
        System.out.println("");
        System.out.println("Prison");
        playtimeServiceImpl.migratePrisonPlaytimeData();
        System.out.println("");
        System.out.println("Events:");
        playtimeServiceImpl.migrateEventsPlaytimeData();
        System.out.println("Scheduled task 10 is completed");
    }

    @Scheduled(cron = "0 20 * * * *")
    @Scheduled(cron = "0 50 * * * *")
    public void runTask11() {
        System.out.println("Scheduled task 11 is started");
        playtimeServiceImpl.convertTimestampToDateSurvival();
        playtimeServiceImpl.convertTimestampToDateSkyblock();
        playtimeServiceImpl.convertTimestampToDateCreative();
        playtimeServiceImpl.convertTimestampToDateBoxpvp();
        playtimeServiceImpl.convertTimestampToDatePrison();
        playtimeServiceImpl.convertTimestampToDateEvents();
        System.out.println("Scheduled task 11 is completed");
    }

    @Scheduled(cron = "0 20 * * * *")
    @Scheduled(cron = "0 50 * * * *")
    public void runTask12() {
        System.out.println("Scheduled task 12 is started");
        productivityServiceImpl.calculateActivityPerHalfYear();
        System.out.println("Scheduled task 12 is completed");
    }

    @Scheduled(cron = "0 22 * * * *")
    @Scheduled(cron = "0 52 * * * *")
    public void runTask13() {
        System.out.println("Scheduled task 13 is started");
        productivityServiceImpl.checkIfEmployeeHasEnoughDaysForPromotion();
        System.out.println("Scheduled task 13 is completed");
    }

    @Scheduled(cron = "0 24 * * * *")
    @Scheduled(cron = "0 54 * * * *")
    public void runTask14() {
        System.out.println("Scheduled task 14 is started");
        productivityServiceImpl.checkIfEmployeeLastHalfYearPlaytimeIsOK();
        System.out.println("Scheduled task 14 is completed");
    }
}
