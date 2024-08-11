package com.scoutress.KaimuxAdminStats.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.scoutress.KaimuxAdminStats.Services.DcTicketService;
import com.scoutress.KaimuxAdminStats.Services.McTicketService;
import com.scoutress.KaimuxAdminStats.Services.PlaytimeCalculationService;
import com.scoutress.KaimuxAdminStats.Services.ProductivityService;
import com.scoutress.KaimuxAdminStats.Services.RecommendationService;

import jakarta.transaction.Transactional;

@Configuration
@EnableScheduling
public class ScheduledTasksConfig {

    private final DcTicketService dcTicketService;
    private final ProductivityService productivityService;
    private final McTicketService mcTicketService;
    private final RecommendationService recommendationService;
    private final PlaytimeCalculationService playtimeCalculationService;

    public ScheduledTasksConfig(DcTicketService dcTicketService, ProductivityService productivityService,
            McTicketService mcTicketService, RecommendationService recommendationService,
            PlaytimeCalculationService playtimeCalculationService) {
        this.dcTicketService = dcTicketService;
        this.productivityService = productivityService;
        this.mcTicketService = mcTicketService;
        this.recommendationService = recommendationService;
        this.playtimeCalculationService = playtimeCalculationService;
    }

    @Scheduled(cron = "0 0 * * * *")
    @Scheduled(cron = "0 10 * * * *")
    @Scheduled(cron = "0 20 * * * *")
    @Scheduled(cron = "0 30 * * * *")
    @Scheduled(cron = "0 40 * * * *")
    @Scheduled(cron = "0 50 * * * *")
    @Transactional
    public void run() {
        productivityService.updateProductivityData();
        dcTicketService.updateDiscordTicketsAverage();
        dcTicketService.calculateDcTicketsPercentage();
        dcTicketService.updateAverageDcTicketsPercentages();
        mcTicketService.updateMinecraftTicketsAverage();
        mcTicketService.calculateMcTicketsPercentage();
        mcTicketService.updateAverageMcTicketsPercentages();
        productivityService.updateAnnualPlaytimeForAllEmployees();
        productivityService.updateAveragePlaytimeForAllEmployees();
        productivityService.updateAfkPlaytimeForAllEmployees();
        productivityService.calculateServerTicketsForAllEmployeesWithCoefs();
        productivityService.calculateServerTicketsTakenForAllEmployeesWithCoefs();
        productivityService.calculatePlaytimeForAllEmployeesWithCoefs();
        productivityService.calculateAfkPlaytimeForAllEmployeesWithCoefs();
        productivityService.calculateAnsweredDiscordTicketsWithCoefs();
        productivityService.calculateAndSaveComplainsCalc();
        productivityService.calculateAndSaveProductivity();
        recommendationService.evaluateEmployees();
        playtimeCalculationService.calculateDailyPlaytime();
        productivityService.calculateAveragePlaytime();
    }
}
