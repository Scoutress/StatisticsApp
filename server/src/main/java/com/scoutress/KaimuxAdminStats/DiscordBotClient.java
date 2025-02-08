package com.scoutress.KaimuxAdminStats;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class DiscordBotClient {

  private static final String BOT_API_URL = "http://localhost:8085/check-messages";

  public static void main(String[] args) {
    RestTemplate restTemplate = new RestTemplate();

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

        ResponseEntity<String> response = restTemplate.postForEntity(BOT_API_URL, requestEntity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
          System.out.println("Response: " + response.getBody());
        } else {
          System.err.println("Error: " + response.getBody());
        }
      }
    }
  }
}
