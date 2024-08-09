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

    public ScheduledTasksConfig(DcTicketService dcTicketService, ProductivityService productivityService, McTicketService mcTicketService) {
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
    //     System.out.println("Employee dummy data filling is started");
    //     getEmployeesDummyData.createDummyEmployees();
    //     System.out.println("Employee dummy data filling is completed");
    //     System.out.println("Productivity dummy data filling is started");
    //     getProductivityDummyData.createDummyProductivity();
    //     System.out.println("Productivity dummy data filling is completed");
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
}
