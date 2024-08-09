package com.scoutress.KaimuxAdminStats.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.scoutress.KaimuxAdminStats.Services.DcTicketService;

import jakarta.transaction.Transactional;

@Configuration
@EnableScheduling
public class ScheduledTasksConfig {

    private final DcTicketService dcTicketService;
    public ScheduledTasksConfig(com.scoutress.KaimuxAdminStats.Services.DcTicketService dcTicketService) {
        this.dcTicketService = dcTicketService;
    }
    
    // For copy-paste (DEBUG)
    // @Scheduled(cron = "0 * * * * *")
    // @Scheduled(cron = "15 * * * * *")
    // @Scheduled(cron = "30 * * * * *")
    // @Scheduled(cron = "45 * * * * *")

    // @Scheduled(cron = "* * * * * *")
    // @Transactional
    // public void runTask1() {
    //     System.out.println("Employee dummy data filling is started");
    //     getEmployeesDummyData.createDummyEmployees();
    //     System.out.println("Employee dummy data filling is completed");
    //     System.out.println("Productivity dummy data filling is started");
    //     getProductivityDummyData.createDummyProductivity();
    //     System.out.println("Productivity dummy data filling is completed");
    // }

    // @Scheduled(cron = "0 1 * * * *")
    @Transactional
    public void runTask1() {
        System.out.println("DC tickets avg. update is started");
        dcTicketService.updateDiscordTicketsAverage();
        System.out.println("DC tickets avg. update is completed");
    }

    // @Scheduled(cron = "0 2 * * * *")
    @Transactional
    public void runTask2() {
        System.out.println("DC tickets compare update is started");
        dcTicketService.calculateDcTicketsPercentage();
        System.out.println("DC tickets compare update is completed");
    }
}
