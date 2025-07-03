package com.scoutress.KaimuxAdminStats.servicesImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.MinecraftTicketsAnswers;
import com.scoutress.KaimuxAdminStats.repositories.discordTickets.DiscordTicketsReactionsRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.MinecraftTicketsAnswersRepository;
import com.scoutress.KaimuxAdminStats.services.ApiDataExtractionService;

@Service
public class ApiDataExtractionServiceImpl implements ApiDataExtractionService {

  private final RestTemplate restTemplate;
  private final KaimuxWebsiteConfig kaimuxWebsiteConfig;
  private final MinecraftTicketsAnswersRepository minecraftTicketsAnswersRepository;
  private final DiscordTicketsReactionsRepository discordTicketsReactionsRepository;

  public ApiDataExtractionServiceImpl(
      RestTemplate restTemplate,
      KaimuxWebsiteConfig kaimuxWebsiteConfig,
      MinecraftTicketsAnswersRepository minecraftTicketsAnswersRepository,
      DiscordTicketsReactionsRepository discordTicketsReactionsRepository) {
    this.restTemplate = restTemplate;
    this.kaimuxWebsiteConfig = kaimuxWebsiteConfig;
    this.minecraftTicketsAnswersRepository = minecraftTicketsAnswersRepository;
    this.discordTicketsReactionsRepository = discordTicketsReactionsRepository;
  }

  @Override
  public void extractMinecraftTicketsFromAPI(LocalDate newestDateFromDailyMcTickets) {
    fetchAndSaveMcTicketsData(1, newestDateFromDailyMcTickets);
  }

  private void fetchAndSaveMcTicketsData(int level, LocalDate newestDateFromDailyMcTickets) {
    try {
      String jsonData = fetchDataFromMcTicketsApi(level);
      if (!isMcTicketsDataEmpty(jsonData)) {
        boolean moreData = saveMcTicketsDataToDatabase(jsonData, newestDateFromDailyMcTickets);
        if (moreData) {
          Thread.sleep(2000);
          fetchAndSaveMcTicketsData(level + 1, newestDateFromDailyMcTickets);
        }
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      System.err.println("Thread was interrupted: " + e);
    } catch (JSONException | HttpClientErrorException e) {
      System.err.println("Error fetching data for level " + level + ": " + e);
    }
  }

  private String fetchDataFromMcTicketsApi(int level) throws JSONException {
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

  private boolean isMcTicketsDataEmpty(String jsonData) throws JSONException {
    JSONObject jsonObject = new JSONObject(jsonData);
    JSONArray dataArray = jsonObject.getJSONArray("data");
    return dataArray.length() == 0;
  }

  private boolean saveMcTicketsDataToDatabase(String jsonData, LocalDate newestDateFromDailyMcTickets)
      throws JSONException {
    JSONObject jsonObject = new JSONObject(jsonData);
    JSONArray dataArray = jsonObject.getJSONArray("data");
    boolean moreData = true;

    for (int i = 0; i < dataArray.length(); i++) {
      JSONObject item = dataArray.getJSONObject(i);
      LocalDateTime dateTime = parseMcTicketsDate(item.getString("created_at"));
      LocalDate date = dateTime.toLocalDate();

      if (!date.isAfter(newestDateFromDailyMcTickets)) {
        moreData = false;
        break;
      }

      MinecraftTicketsAnswers entity = new MinecraftTicketsAnswers();
      entity.setKmxWebApiMcTickets((short) item.getInt("player_id"));
      entity.setDateTime(dateTime);

      minecraftTicketsAnswersRepository.save(entity);
    }

    return moreData;
  }

  private LocalDateTime parseMcTicketsDate(String dateStr) {
    return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
  }

  @Override
  public void extractDiscordTicketsFromAPI(LocalDate newestDateFromDcTicketsRawData) {
    fetchAndSaveDcTicketsData(1, newestDateFromDcTicketsRawData);
  }

  private void fetchAndSaveDcTicketsData(int level, LocalDate newestDateFromDcTicketsRawData) {
    try {
      String jsonData = fetchDataFromDcTicketsApi(level);
      if (!isDcTicketsDataEmpty(jsonData)) {
        boolean moreData = saveDcTicketsDataToDatabase(jsonData, newestDateFromDcTicketsRawData);
        if (moreData) {
          Thread.sleep(2000);
          fetchAndSaveDcTicketsData(level + 1, newestDateFromDcTicketsRawData);
        }
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      System.err.println("Thread was interrupted: " + e);
    } catch (JSONException | HttpClientErrorException e) {
      System.err.println("Error fetching data for level " + level + ": " + e);
    }
  }

  private String fetchDataFromDcTicketsApi(int level) throws JSONException {
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
      System.err.println("Error during API request for level " + level + ": " + e);
      throw e;
    }
  }

  private boolean isDcTicketsDataEmpty(String jsonData) throws JSONException {
    JSONObject jsonObject = new JSONObject(jsonData);
    JSONArray dataArray = jsonObject.getJSONArray("data");
    return dataArray.length() == 0;
  }

  private boolean saveDcTicketsDataToDatabase(String jsonData, LocalDate newestDateFromDailyDcTickets)
      throws JSONException {
    JSONObject jsonObject = new JSONObject(jsonData);
    JSONArray dataArray = jsonObject.getJSONArray("data");
    boolean moreData = true;

    for (int i = 0; i < dataArray.length(); i++) {
      JSONObject item = dataArray.getJSONObject(i);
      LocalDateTime dateTime = parseDcTicketsDate(item.getString("created_at"));
      LocalDate date = dateTime.toLocalDate();

      if (!date.isAfter(newestDateFromDailyDcTickets)) {
        moreData = false;
        break;
      }

      DiscordTicketsReactions entity = new DiscordTicketsReactions();
      entity.setDiscordId(item.getLong("player_id"));
      entity.setTicketId(item.getString("ticket_id"));
      entity.setDateTime(dateTime);

      discordTicketsReactionsRepository.save(entity);
    }

    return moreData;
  }

  private LocalDateTime parseDcTicketsDate(String dateStr) {
    return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
  }
}
