package com.scoutress.KaimuxAdminStats.Config;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.scoutress.KaimuxAdminStats.Services.ProductivityService;

import jakarta.transaction.Transactional;

@Configuration
@EnableScheduling
public class ScheduledTasksConfig {

        private final ProductivityService productivityService;

        public ScheduledTasksConfig(ProductivityService productivityService) {
                this.productivityService = productivityService;
        }

        // @Scheduled(cron = "0 0 * * * *")
        // @Scheduled(cron = "0 * * * * *")
        // @Scheduled(cron = "15 * * * * *")
        // @Scheduled(cron = "30 * * * * *")
        // @Scheduled(cron = "45 * * * * *")
        @Transactional
        public void run() {
                System.out.println("Scheduled tasks started at: " + getCurrentTimestamp());

                measureExecutionTime(() -> productivityService.updateProductivity(),
                                "updateProductivity");

                System.out.println("Scheduled tasks completed at: " + getCurrentTimestamp());
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        }

        private void measureExecutionTime(Runnable task, String taskName) {
                Instant start = Instant.now();
                System.out.println("");
                System.out.println("Starting " + taskName);
                task.run();
                Instant end = Instant.now();
                Duration duration = Duration.between(start, end);
                System.out.println(taskName + " >> " + duration.toMillis() + " ms");
        }

        private String getCurrentTimestamp() {
                return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        // @Scheduled(cron = "0 * * * * *")
        // @Scheduled(cron = "15 * * * * *")
        // @Scheduled(cron = "30 * * * * *")
        // @Scheduled(cron = "45 * * * * *")
        // @Transactional
        // public void runTesting() {
        // List<LoginLogoutTimes> list = dataExtractingService.getLoginLogoutTimes();
        // for (LoginLogoutTimes list1 : list) {
        // System.out.println(list1);
        // }
        // System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        // }
}