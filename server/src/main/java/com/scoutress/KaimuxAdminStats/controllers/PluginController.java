package com.scoutress.KaimuxAdminStats.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scoutress.KaimuxAdminStats.entity.playtime.SessionDataPlugin;
import com.scoutress.KaimuxAdminStats.services.playtime.SessionsDataFromPluginService;

@RestController
@RequestMapping("/plugin")
public class PluginController {

  private final SessionsDataFromPluginService sessionsDataFromPluginService;

  public PluginController(SessionsDataFromPluginService sessionsDataFromPluginService) {
    this.sessionsDataFromPluginService = sessionsDataFromPluginService;
  }

  @GetMapping("/latestDate")
  public LocalDate getLatestDate() {
    return sessionsDataFromPluginService.getLatestDate();
  }

  @PostMapping("/receiveSessionData")
  public ResponseEntity<String> postSessionData(@RequestBody List<SessionDataPlugin> sessionData) {
    try {
      sessionsDataFromPluginService.saveSessionData(sessionData);
      return ResponseEntity.ok("Data saved successfully.");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error saving session data: " + e.getMessage());
    }
  }
}
