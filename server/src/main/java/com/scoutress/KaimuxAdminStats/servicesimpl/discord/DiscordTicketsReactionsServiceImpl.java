package com.scoutress.KaimuxAdminStats.servicesimpl.discord;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.scoutress.KaimuxAdminStats.config.KaimuxWebsiteConfig;
import com.scoutress.KaimuxAdminStats.services.discord.DiscordTicketsReactionsService;

@Service
public class DiscordTicketsReactionsServiceImpl implements DiscordTicketsReactionsService {

  private final RestTemplate restTemplate;
  private final KaimuxWebsiteConfig kaimuxWebsiteConfig;

  public DiscordTicketsReactionsServiceImpl(
      RestTemplate restTemplate,
      KaimuxWebsiteConfig kaimuxWebsiteConfig) {

    this.restTemplate = restTemplate;
    this.kaimuxWebsiteConfig = kaimuxWebsiteConfig;
  }

  public String fetchDataFromApi() throws JSONException {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    String api = kaimuxWebsiteConfig.getApiToken();
    String url = kaimuxWebsiteConfig.getApiForDiscordTickets();
    int level = 1;

    JSONObject requestBody = new JSONObject();
    requestBody.put("api_token", api);

    System.out.println("Debug - URL: " + url + level);
    System.out.println("Debug - Request Body: " + requestBody.toString());

    HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

    try {
      ResponseEntity<String> response = restTemplate.exchange(
          url + level,
          HttpMethod.POST,
          entity,
          String.class);
      return response.getBody();
    } catch (HttpClientErrorException e) {
      System.out.println("Error status code: " + e.getStatusCode());
      System.out.println("Error response body: " + e.getResponseBodyAsString());
      System.out.println("Request URL: " + url + level);
      System.out.println("Request body: " + requestBody.toString());
      throw e;
    }
  }
}
