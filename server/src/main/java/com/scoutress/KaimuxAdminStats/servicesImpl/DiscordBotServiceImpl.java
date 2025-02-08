package com.scoutress.KaimuxAdminStats.servicesImpl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.scoutress.KaimuxAdminStats.config.DcBotConfig;
import com.scoutress.KaimuxAdminStats.services.DiscordBotService;

@Service
public class DiscordBotServiceImpl implements DiscordBotService {

  private final static DcBotConfig dcBotConfig = new DcBotConfig();

  @Override
  public void processDiscordMessagesCount(String[] args) {
    RestTemplate restTemplate = new RestTemplate();
    String botApiUrl = dcBotConfig.getDcBotApi();

    if (botApiUrl == null || !botApiUrl.startsWith("http")) {
      System.err.println("Invalid bot API URL: " + botApiUrl);
      return;
    }

    // Example data for the loop
    long[] userIds = { 508674128006479872L, 123456789012345678L };
    String[] dates = { "2025-01-19", "2025-01-20" };

    for (long userId : userIds) {
      for (String date : dates) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("user_id", userId);
        requestBody.put("message_date", date);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(botApiUrl, requestEntity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
          System.out.println("Response: " + response.getBody());
        } else {
          System.err.println("Error: " + response.getBody());
        }
      }
    }
  }
}
