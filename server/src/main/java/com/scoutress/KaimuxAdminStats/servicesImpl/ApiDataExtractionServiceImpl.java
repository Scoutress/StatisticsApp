package com.scoutress.KaimuxAdminStats.servicesImpl;

import java.time.LocalDate;
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
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.DailyMinecraftTickets;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.MinecraftTicketsAnswers;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.DailyMinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.MinecraftTicketsAnswersRepository;
import com.scoutress.KaimuxAdminStats.services.ApiDataExtractionService;

@Service
public class ApiDataExtractionServiceImpl implements ApiDataExtractionService {

  private final RestTemplate restTemplate;
  private final KaimuxWebsiteConfig kaimuxWebsiteConfig;
  private final MinecraftTicketsAnswersRepository minecraftTicketsAnswersRepository;
  private final DailyMinecraftTicketsRepository dailyMinecraftTicketsRepository;
  private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

  public ApiDataExtractionServiceImpl(
      RestTemplate restTemplate,
      KaimuxWebsiteConfig kaimuxWebsiteConfig,
      MinecraftTicketsAnswersRepository minecraftTicketsAnswersRepository,
      DailyMinecraftTicketsRepository dailyMinecraftTicketsRepository) {
    this.restTemplate = restTemplate;
    this.kaimuxWebsiteConfig = kaimuxWebsiteConfig;
    this.minecraftTicketsAnswersRepository = minecraftTicketsAnswersRepository;
    this.dailyMinecraftTicketsRepository = dailyMinecraftTicketsRepository;
  }

  @Override
  public void handleMinecraftTicketsRawData() {
    removeDataFromMinecraftTicketsAnswers();
    fetchAndSaveData(1);
  }

  private void removeDataFromMinecraftTicketsAnswers() {
    minecraftTicketsAnswersRepository.truncateTable();
  }

  private void fetchAndSaveData(int level) {
    executorService.submit(() -> {
      try {
        LocalDate newestDateFromProcessedData = getNewestDateFromProcessedData();
        String jsonData = fetchDataFromApi(level);
        if (!isDataEmpty(jsonData)) {
          boolean moreData = saveDataToDatabase(jsonData, newestDateFromProcessedData);
          if (moreData) {
            executorService.schedule(() -> fetchAndSaveData(level + 1), 2, TimeUnit.SECONDS);
          }
        }
      } catch (JSONException | HttpClientErrorException e) {
        System.err.println("Error fetching data for level " + level + ": " + e);
      }
    });
  }

  private String fetchDataFromApi(int level) throws JSONException {
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
      System.err.println("Error during API request for level " + level + ": " + e);
      throw e;
    }
  }

  private boolean isDataEmpty(String jsonData) throws JSONException {
    JSONObject jsonObject = new JSONObject(jsonData);
    JSONArray dataArray = jsonObject.getJSONArray("data");
    return dataArray.length() == 0;
  }

  private boolean saveDataToDatabase(String jsonData, LocalDate newestDateFromProcessedData) throws JSONException {
    JSONObject jsonObject = new JSONObject(jsonData);
    JSONArray dataArray = jsonObject.getJSONArray("data");
    boolean moreData = true;

    for (int i = 0; i < dataArray.length(); i++) {
      JSONObject item = dataArray.getJSONObject(i);
      LocalDateTime dateTime = parseDate(item.getString("created_at"));

      if (dateTime.toLocalDate().isBefore(newestDateFromProcessedData.minusDays(1))) {
        moreData = false;
        break;
      }

      MinecraftTicketsAnswers entity = new MinecraftTicketsAnswers();
      entity.setMinecraftTicketId((short) item.getInt("player_id"));
      entity.setDateTime(dateTime);

      minecraftTicketsAnswersRepository.save(entity);
    }

    return moreData;
  }

  private LocalDateTime parseDate(String dateStr) {
    return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
  }

  private LocalDate getNewestDateFromProcessedData() {
    return dailyMinecraftTicketsRepository
        .findAll()
        .stream()
        .map(DailyMinecraftTickets::getDate)
        .max(LocalDate::compareTo)
        .orElse(LocalDate.parse("1970-01-01"));
  }
}
