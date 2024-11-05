package com.scoutress.KaimuxAdminStats.servicesimpl.discordTickets;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.configurationprocessor.json.JSONArray;
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
import com.scoutress.KaimuxAdminStats.entity.discordTickets.DiscordTicketsReactions;
import com.scoutress.KaimuxAdminStats.repositories.discordTickets.DiscordTicketsReactionsRepository;
import com.scoutress.KaimuxAdminStats.services.discordTickets.DiscordTicketsReactionsService;

@Service
public class DiscordTicketsReactionsServiceImpl implements DiscordTicketsReactionsService {

  private final RestTemplate restTemplate;
  private final KaimuxWebsiteConfig kaimuxWebsiteConfig;
  private final DiscordTicketsReactionsRepository discordTicketsReactionsRepository;
  private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

  public DiscordTicketsReactionsServiceImpl(
      RestTemplate restTemplate,
      KaimuxWebsiteConfig kaimuxWebsiteConfig,
      DiscordTicketsReactionsRepository discordTicketsReactionsRepository) {

    this.restTemplate = restTemplate;
    this.kaimuxWebsiteConfig = kaimuxWebsiteConfig;
    this.discordTicketsReactionsRepository = discordTicketsReactionsRepository;
  }

  // TODO: Atm DB table are filled with data
  // Still need to add functionality, that requests only newer data
  // (not old dublicates)

  @Override
  public void fetchAndSaveData() {
    fetchDataWithDelay(1);
  }

  private void fetchDataWithDelay(int level) {
    executorService.schedule(() -> {
      try {
        String jsonData = fetchDataFromApi(level);
        if (!isDataEmpty(jsonData)) {
          saveDataToDatabase(jsonData);
          fetchDataWithDelay(level + 1);
        }
      } catch (JSONException e) {
        System.err.println("JSON Parsing Error: " + e.getMessage());
      } catch (HttpClientErrorException e) {
        System.err.println("HTTP Error: " + e.getMessage());
      }
    }, 2, TimeUnit.SECONDS);
  }

  @Override
  public String fetchDataFromApi(int level) throws JSONException {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    String api = kaimuxWebsiteConfig.getApiToken();
    String url = kaimuxWebsiteConfig.getApiForDiscordTickets();

    JSONObject requestBody = new JSONObject();
    requestBody.put("api_token", api);

    HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

    try {
      ResponseEntity<String> response = restTemplate.exchange(
          url + level,
          HttpMethod.POST,
          entity,
          String.class);
      return response.getBody();
    } catch (HttpClientErrorException e) {
      throw e;
    }
  }

  private boolean isDataEmpty(String jsonData) throws JSONException {
    JSONObject jsonObject = new JSONObject(jsonData);
    JSONArray dataArray = jsonObject.getJSONArray("data");
    return dataArray.length() == 0;
  }

  private void saveDataToDatabase(String jsonData) throws JSONException {
    JSONObject jsonObject = new JSONObject(jsonData);
    JSONArray dataArray = jsonObject.getJSONArray("data");

    for (int i = 0; i < dataArray.length(); i++) {
      JSONObject item = dataArray.getJSONObject(i);

      DiscordTicketsReactions entity = new DiscordTicketsReactions();
      entity.setDiscordId(item.getLong("player_id"));
      entity.setTicketId(item.getString("ticket_id"));

      String createdAt = item.getString("created_at");
      LocalDateTime dateTime = LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_DATE_TIME);
      entity.setDateTime(dateTime);

      discordTicketsReactionsRepository.save(entity);
    }
  }
}
