package com.scoutress.KaimuxAdminStats.Config;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        @Transactional
        public void run() {
                System.out.println("Scheduled tasks started at: " + getCurrentTimestamp());

                measureExecutionTime(() -> productivityService.updateProductivityData(), "updateProductivityData");
                measureExecutionTime(() -> dcTicketService.updateDiscordTicketsAverage(),
                                "updateDiscordTicketsAverage");
                measureExecutionTime(() -> dcTicketService.calculateDcTicketsPercentage(),
                                "calculateDcTicketsPercentage");
                measureExecutionTime(() -> dcTicketService.updateAverageDcTicketsPercentages(),
                                "updateAverageDcTicketsPercentages");
                measureExecutionTime(() -> mcTicketService.updateMinecraftTicketsAverage(),
                                "updateMinecraftTicketsAverage");
                measureExecutionTime(() -> mcTicketService.calculateMcTicketsPercentage(),
                                "calculateMcTicketsPercentage");
                measureExecutionTime(() -> mcTicketService.updateAverageMcTicketsPercentages(),
                                "updateAverageMcTicketsPercentages");
                measureExecutionTime(() -> productivityService.updateAnnualPlaytimeForAllEmployees(),
                                "updateAnnualPlaytimeForAllEmployees");
                measureExecutionTime(() -> productivityService.updateAveragePlaytimeForAllEmployees(),
                                "updateAveragePlaytimeForAllEmployees");
                measureExecutionTime(() -> productivityService.updateAfkPlaytimeForAllEmployees(),
                                "updateAfkPlaytimeForAllEmployees");
                measureExecutionTime(() -> productivityService.calculateServerTicketsForAllEmployeesWithCoefs(),
                                "calculateServerTicketsForAllEmployeesWithCoefs");
                measureExecutionTime(() -> productivityService.calculateServerTicketsTakenForAllEmployeesWithCoefs(),
                                "calculateServerTicketsTakenForAllEmployeesWithCoefs");
                measureExecutionTime(() -> productivityService.calculatePlaytimeForAllEmployeesWithCoefs(),
                                "calculatePlaytimeForAllEmployeesWithCoefs");
                measureExecutionTime(() -> productivityService.calculateAfkPlaytimeForAllEmployeesWithCoefs(),
                                "calculateAfkPlaytimeForAllEmployeesWithCoefs");
                measureExecutionTime(() -> productivityService.calculateAnsweredDiscordTicketsWithCoefs(),
                                "calculateAnsweredDiscordTicketsWithCoefs");
                measureExecutionTime(() -> productivityService.calculateAndSaveComplainsCalc(),
                                "calculateAndSaveComplainsCalc");
                measureExecutionTime(() -> productivityService.calculateAndSaveProductivity(),
                                "calculateAndSaveProductivity");
                measureExecutionTime(() -> recommendationService.evaluateEmployees(), "evaluateEmployees");
                measureExecutionTime(() -> playtimeCalculationService.calculateDailyPlaytime(),
                                "calculateDailyPlaytime");
                measureExecutionTime(() -> productivityService.calculateAveragePlaytime(), "calculateAveragePlaytime");

                System.out.println("Scheduled tasks completed at: " + getCurrentTimestamp());
        }

        private void measureExecutionTime(Runnable task, String taskName) {
                Instant start = Instant.now();
                System.out.println("Starting " + taskName + " at: " + getCurrentTimestamp());
                task.run();
                Instant end = Instant.now();
                Duration duration = Duration.between(start, end);
                System.out.println("Completed " + taskName + " at: " + getCurrentTimestamp() + ". Duration: "
                                + duration.toMillis() + " ms");
        }

        private String getCurrentTimestamp() {
                return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
}