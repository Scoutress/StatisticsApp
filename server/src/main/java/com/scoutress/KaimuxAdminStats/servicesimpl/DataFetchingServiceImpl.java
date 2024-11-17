package com.scoutress.KaimuxAdminStats.servicesImpl;

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
import com.scoutress.KaimuxAdminStats.entity.ProjectVisitorsRawData;
import com.scoutress.KaimuxAdminStats.entity.discordTickets.DiscordTicketsReactions;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.MinecraftTicketsAnswers;
import com.scoutress.KaimuxAdminStats.repositories.ProjectVisitorsRawDataRepository;
import com.scoutress.KaimuxAdminStats.repositories.discordTickets.DiscordTicketsReactionsRepository;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.MinecraftTicketsAnswersRepository;
import com.scoutress.KaimuxAdminStats.services.DataFetchingService;

@Service
public class DataFetchingServiceImpl implements DataFetchingService {

  private final RestTemplate restTemplate;
  private final KaimuxWebsiteConfig kaimuxWebsiteConfig;
  private final DiscordTicketsReactionsRepository discordTicketsReactionsRepository;
  private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
  private final MinecraftTicketsAnswersRepository minecraftTicketsAnswersRepository;
  private final ProjectVisitorsRawDataRepository projectVisitorsRawDataRepository;

  public DataFetchingServiceImpl(
      RestTemplate restTemplate,
      KaimuxWebsiteConfig kaimuxWebsiteConfig,
      MinecraftTicketsAnswersRepository minecraftTicketsAnswersRepository,
      ProjectVisitorsRawDataRepository projectVisitorsRawDataRepository,
      DiscordTicketsReactionsRepository discordTicketsReactionsRepository) {

    this.restTemplate = restTemplate;
    this.kaimuxWebsiteConfig = kaimuxWebsiteConfig;
    this.minecraftTicketsAnswersRepository = minecraftTicketsAnswersRepository;
    this.projectVisitorsRawDataRepository = projectVisitorsRawDataRepository;
    this.discordTicketsReactionsRepository = discordTicketsReactionsRepository;
  }

  @Override
  public void fetchAndSaveData(int level, String type) {
    executorService.submit(() -> {
      try {
        String jsonData = fetchDataFromApi(level, type);
        if (!isDataEmpty(jsonData)) {
          saveDataToDatabase(jsonData, type);
          executorService.schedule(() -> fetchAndSaveData(level + 1, type), 2, TimeUnit.SECONDS);
        }
      } catch (JSONException | HttpClientErrorException e) {
        System.err.println("Error fetching data for level " + level + " and type " + type + ": " + e);
      }
    });
  }

  public String fetchDataFromApi(int level, String type) throws JSONException {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    String api = kaimuxWebsiteConfig.getApiToken();
    String url = null;

    switch (type) {
      case "discord" -> url = kaimuxWebsiteConfig.getApiForDiscordTickets();
      case "minecraft" -> url = kaimuxWebsiteConfig.getApiForMinecraftTickets();
      case "visitors" -> url = kaimuxWebsiteConfig.getApiForServerVisitors();
    }

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
    boolean empty = dataArray.length() == 0;
    return empty;
  }

  private void saveDataToDatabase(String jsonData, String type) throws JSONException {
    JSONObject jsonObject = new JSONObject(jsonData);
    JSONArray dataArray = jsonObject.getJSONArray("data");

    for (int i = 0; i < dataArray.length(); i++) {
      JSONObject item = dataArray.getJSONObject(i);

      switch (type) {
        case "discord" -> {
          DiscordTicketsReactions entity = new DiscordTicketsReactions();
          entity.setDiscordId(item.getLong("player_id"));
          entity.setTicketId(item.getString("ticket_id"));

          String createdAt = item.getString("created_at");
          LocalDateTime dateTime = parseDate(createdAt);
          entity.setDateTime(dateTime);

          discordTicketsReactionsRepository.save(entity);
        }
        case "minecraft" -> {
          MinecraftTicketsAnswers entity = new MinecraftTicketsAnswers();
          entity.setMinecraftTicketId(item.getLong("player_id"));

          String createdAt = item.getString("created_at");
          LocalDateTime dateTime = parseDate(createdAt);
          entity.setDateTime(dateTime);

          minecraftTicketsAnswersRepository.save(entity);
        }
        case "visitors" -> {
          ProjectVisitorsRawData entity = new ProjectVisitorsRawData();
          entity.setIp(item.optString("ip", "unknown"));
          entity.setType(item.optInt("type", 0));

          int premiumInt = item.optInt("is_premium", 0);
          entity.setPremium(premiumInt == 1);

          String createdAt = item.optString("created_at", "1970-01-01T00:00:00");
          LocalDateTime dateTime = parseDate(createdAt);
          entity.setDateTime(dateTime);

          projectVisitorsRawDataRepository.save(entity);
        }
      }
    }
  }

  private LocalDateTime parseDate(String dateStr) {
    return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
  }

  public void fetchAndSaveOlderData(String type) {
    int totalRecords;

    totalRecords = switch (type) {
      case "discord" -> (int) discordTicketsReactionsRepository.count();
      case "minecraft" -> (int) minecraftTicketsAnswersRepository.count();
      case "visitors" -> (int) projectVisitorsRawDataRepository.count();
      default -> throw new IllegalArgumentException("Unknown type: " + type);
    };

    int recordsPerPage = 30;
    int startLevel = (totalRecords / recordsPerPage) + 1;

    fetchAndSaveData(startLevel, type);
  }

  public LocalDateTime findLatestDateTime(String type) {
    return switch (type) {
      case "discord" -> discordTicketsReactionsRepository
          .findAll()
          .stream()
          .map(DiscordTicketsReactions::getDateTime)
          .max(LocalDateTime::compareTo)
          .orElse(LocalDateTime.parse("1970-01-01T00:00:00"));

      case "minecraft" -> minecraftTicketsAnswersRepository
          .findAll()
          .stream()
          .map(MinecraftTicketsAnswers::getDateTime)
          .max(LocalDateTime::compareTo)
          .orElse(LocalDateTime.parse("1970-01-01T00:00:00"));

      case "visitors" -> projectVisitorsRawDataRepository
          .findAll()
          .stream()
          .map(ProjectVisitorsRawData::getDateTime)
          .max(LocalDateTime::compareTo)
          .orElse(LocalDateTime.parse("1970-01-01T00:00:00"));

      default -> throw new IllegalArgumentException("Unknown type: " + type);
    };
  }
}
