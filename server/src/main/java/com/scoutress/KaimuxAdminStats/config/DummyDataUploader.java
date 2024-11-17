package com.scoutress.KaimuxAdminStats.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.scoutress.KaimuxAdminStats.services.DummyDataUploadingService;

import jakarta.transaction.Transactional;

@Configuration
@EnableScheduling
public class DummyDataUploader {

  private final DummyDataUploadingService dummyDataUploadingService;

  public DummyDataUploader(
      DummyDataUploadingService dummyDataUploadingService) {
    this.dummyDataUploadingService = dummyDataUploadingService;
  }

  @Scheduled(initialDelay = 1000, fixedRate = 86400000)
  @Transactional
  public void runScheduledTasks() {
    System.out.println("-----------------------------------------------");
    System.out.println("Started DDU at: " + getCurrentTimestamp());
    System.out.println("");

    // uploadDummyData();

    System.out.println("Scheduled DDU at: " + getCurrentTimestamp());
    System.out.println("-----------------------------------------------");
  }

  @SuppressWarnings("unused")
  private void uploadDummyData() {
    dummyDataUploadingService.uploadDailyPlaytimeDummyData();
    dummyDataUploadingService.uploadDailyAfkPlaytimeDummyData();
    dummyDataUploadingService.uploadDailyDiscordTicketsDummyData();
    dummyDataUploadingService.uploadDailyDiscordTicketsComparedDummyData();
    dummyDataUploadingService.uploadDailyDiscordMessagesDummyData();
    dummyDataUploadingService.uploadDailyDiscordMessagesComparedDummyData();
    dummyDataUploadingService.uploadDailyMinecraftTicketsDummyData();
    dummyDataUploadingService.uploadDailyMinecraftTicketsComparedDummyData();
    System.out.println("");
  }

  private String getCurrentTimestamp() {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }
}
