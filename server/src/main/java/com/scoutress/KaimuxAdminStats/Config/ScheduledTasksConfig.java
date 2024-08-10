package com.scoutress.KaimuxAdminStats.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.scoutress.KaimuxAdminStats.Services.DcTicketService;
import com.scoutress.KaimuxAdminStats.Services.McTicketService;
import com.scoutress.KaimuxAdminStats.Services.ProductivityService;

import jakarta.transaction.Transactional;

@Configuration
@EnableScheduling
public class ScheduledTasksConfig {

    private final DcTicketService dcTicketService;
    private final ProductivityService productivityService;
    private final McTicketService mcTicketService;

    public ScheduledTasksConfig(DcTicketService dcTicketService, ProductivityService productivityService,
            McTicketService mcTicketService) {
        this.dcTicketService = dcTicketService;
        this.productivityService = productivityService;
        this.mcTicketService = mcTicketService;
    }

    // For copy-paste (DEBUG)
    // @Scheduled(cron = "0 * * * * *")
    // @Scheduled(cron = "15 * * * * *")
    // @Scheduled(cron = "30 * * * * *")
    // @Scheduled(cron = "45 * * * * *")

    // @Scheduled(cron = "0 * * * * *")
    // @Transactional
    // public void runTask1() {
    // System.out.println("Employee dummy data filling is started");
    // getEmployeesDummyData.createDummyEmployees();
    // System.out.println("Employee dummy data filling is completed");
    // System.out.println("Productivity dummy data filling is started");
    // getProductivityDummyData.createDummyProductivity();
    // System.out.println("Productivity dummy data filling is completed");
    // }

    @Scheduled(cron = "0 1 * * * *")
    @Transactional
    public void runTask1() {
        System.out.println("Productivity data update is started");
        productivityService.updateProductivityData();
        System.out.println("Productivity data update is completed");
    }

    @Scheduled(cron = "0 2 * * * *")
    @Transactional
    public void runTask2() {
        System.out.println("DC tickets avg. update is started");
        dcTicketService.updateDiscordTicketsAverage();
        System.out.println("DC tickets avg. update is completed");
    }

    @Scheduled(cron = "0 3 * * * *")
    @Transactional
    public void runTask3() {
        System.out.println("DC tickets compare update is started");
        dcTicketService.calculateDcTicketsPercentage();
        System.out.println("DC tickets compare update is completed");
    }

    @Scheduled(cron = "0 4 * * * *")
    @Transactional
    public void runTask4() {
        System.out.println("DC tickets avg. percentages update is started");
        dcTicketService.updateAverageDcTicketsPercentages();
        System.out.println("DC tickets avg. percentages update is completed");
    }

    @Scheduled(cron = "0 5 * * * *")
    @Transactional
    public void runTask5() {
        System.out.println("MC tickets avg. update is started");
        mcTicketService.updateMinecraftTicketsAverage();
        System.out.println("MC tickets avg. update is completed");
    }

    @Scheduled(cron = "0 6 * * * *")
    @Transactional
    public void runTask6() {
        System.out.println("MC tickets compare update is started");
        mcTicketService.calculateMcTicketsPercentage();
        System.out.println("MC tickets compare update is completed");
    }

    @Scheduled(cron = "0 7 * * * *")
    @Transactional
    public void runTask7() {
        System.out.println("MC tickets avg. percentages update is started");
        mcTicketService.updateAverageMcTicketsPercentages();
        System.out.println("MC tickets avg. percentages update is completed");
    }

    @Scheduled(cron = "0 8 * * * *")
    @Transactional
    public void runTask8() {
        System.out.println("Annual playtime calc. is started");
        productivityService.updateAnnualPlaytimeForAllEmployees();
        System.out.println("Annual playtime calc. is completed");
    }

    @Scheduled(cron = "0 9 * * * *")
    @Transactional
    public void runTask9() {
        System.out.println("Average playtime calc. is started");
        productivityService.updateAveragePlaytimeForAllEmployees();
        System.out.println("Average playtime calc. is completed");
    }

    @Scheduled(cron = "0 10 * * * *")
    @Transactional
    public void runTask10() {
        System.out.println("Average AFK playtime calc. is started");
        productivityService.updateAfkPlaytimeForAllEmployees();
        System.out.println("Average AFK playtime calc. is completed");
    }

    @Scheduled(cron = "0 11 * * * *")
    @Transactional
    public void runTask11() {
        System.out.println("Server tickets with coef. calc. is started");
        productivityService.calculateServerTicketsForAllEmployeesWithCoefs();
        System.out.println("Server tickets with coef. calc. is completed");
    }

    @Scheduled(cron = "0 12 * * * *")
    @Transactional
    public void runTask12() {
        System.out.println("Server tickets taken with coef. calc. is started");
        productivityService.calculateServerTicketsTakenForAllEmployeesWithCoefs();
        System.out.println("Server tickets taken with coef. calc. is completed");
    }

    @Scheduled(cron = "0 13 * * * *")
    @Transactional
    public void runTask13() {
        System.out.println("Playtime with coef. calc. is started");
        productivityService.calculatePlaytimeForAllEmployeesWithCoefs();
        System.out.println("Playtime with coef. calc. is completed");
    }

    @Scheduled(cron = "0 14 * * * *")
    @Transactional
    public void runTask14() {
        System.out.println("AFK Playtime with coef. calc. is started");
        productivityService.calculateAfkPlaytimeForAllEmployeesWithCoefs();
        System.out.println("AFK Playtime with coef. calc. is completed");
    }

    @Scheduled(cron = "0 15 * * * *")
    @Transactional
    public void runTask15() {
        System.out.println("Answered DC tickets with coef. calc. is started");
        productivityService.calculateAnsweredDiscordTicketsWithCoefs();
        System.out.println("Answered DC tickets with coef. calc. is completed");
    }

    @Scheduled(cron = "0 16 * * * *")
    @Transactional
    public void runTask16() {
        System.out.println("Complains with coef. calc. is started");
        productivityService.calculateAndSaveComplainsCalc();
        System.out.println("Complains with coef. calc. is completed");
    }

    @Scheduled(cron = "0 17 * * * *")
    @Transactional
    public void runTask17() {
        System.out.println("Productivity calculation is started");
        productivityService.calculateAndSaveProductivity();
        System.out.println("Productivity calculation is completed");
    }
}
