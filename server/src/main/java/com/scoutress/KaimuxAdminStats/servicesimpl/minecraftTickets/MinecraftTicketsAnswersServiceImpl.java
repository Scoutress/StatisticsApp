package com.scoutress.KaimuxAdminStats.servicesimpl.minecraftTickets;

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
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.MinecraftTicketsAnswers;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.MinecraftTicketsAnswersRepository;
import com.scoutress.KaimuxAdminStats.services.minecraftTickets.MinecraftTicketsAnswersService;

@Service
public class MinecraftTicketsAnswersServiceImpl implements MinecraftTicketsAnswersService {

  private final RestTemplate restTemplate;
  private final KaimuxWebsiteConfig kaimuxWebsiteConfig;
  private final MinecraftTicketsAnswersRepository minecraftTicketsAnswersRepository;
  private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

  public MinecraftTicketsAnswersServiceImpl(
      RestTemplate restTemplate,
      KaimuxWebsiteConfig kaimuxWebsiteConfig,
      MinecraftTicketsAnswersRepository minecraftTicketsAnswersRepository) {

    this.restTemplate = restTemplate;
    this.kaimuxWebsiteConfig = kaimuxWebsiteConfig;
    this.minecraftTicketsAnswersRepository = minecraftTicketsAnswersRepository;
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
    String url = kaimuxWebsiteConfig.getApiForMinecraftTickets();

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

      MinecraftTicketsAnswers entity = new MinecraftTicketsAnswers();
      entity.setMinecraftTicketId(item.getLong("player_id"));

      String createdAt = item.getString("created_at");
      LocalDateTime dateTime = LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_DATE_TIME);
      entity.setDateTime(dateTime);

      minecraftTicketsAnswersRepository.save(entity);
    }
  }
}
