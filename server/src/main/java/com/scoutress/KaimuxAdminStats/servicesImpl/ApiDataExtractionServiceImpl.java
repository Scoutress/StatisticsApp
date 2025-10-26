package com.scoutress.KaimuxAdminStats.servicesImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger log = LoggerFactory.getLogger(ApiDataExtractionServiceImpl.class);
  private static final int REQUEST_DELAY_MS = 2000;

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

  // ===========================================================
  // MINECRAFT TICKETS EXTRACTION
  // ===========================================================

  @Override
  public void extractMinecraftTicketsFromAPI(LocalDate newestDateFromDailyMcTickets) {
    log.info("=== [START] Fetching Minecraft tickets from API ===");
    long start = System.currentTimeMillis();

    fetchAndSaveMcTicketsData(1, newestDateFromDailyMcTickets);

    log.info("✅ Minecraft ticket extraction completed in {} ms", System.currentTimeMillis() - start);
  }

  private void fetchAndSaveMcTicketsData(int level, LocalDate newestDateFromDailyMcTickets) {
    try {
      log.info("Fetching Minecraft ticket batch level {}", level);
      String jsonData = fetchDataFromMcTicketsApi(level);

      if (isJsonDataEmpty(jsonData)) {
        log.info("No more Minecraft ticket data at level {}", level);
        return;
      }

      boolean hasNewerData = saveMcTicketsDataToDatabase(jsonData, newestDateFromDailyMcTickets, level);

      if (hasNewerData) {
        Thread.sleep(REQUEST_DELAY_MS);
        fetchAndSaveMcTicketsData(level + 1, newestDateFromDailyMcTickets);
      }

    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.error("Thread interrupted while processing Minecraft tickets: {}", e.getMessage());
    } catch (JSONException | HttpClientErrorException e) {
      log.error("Error fetching Minecraft tickets (level {}): {}", level, e.getMessage(), e);
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

    ResponseEntity<String> response = restTemplate.exchange(url + level, HttpMethod.POST, entity, String.class);
    return response.getBody();
  }

  private boolean saveMcTicketsDataToDatabase(String jsonData, LocalDate newestDateFromDailyMcTickets, int level)
      throws JSONException {

    JSONObject jsonObject = new JSONObject(jsonData);
    JSONArray dataArray = jsonObject.getJSONArray("data");
    boolean hasNewerData = false;
    int savedCount = 0;

    for (int i = 0; i < dataArray.length(); i++) {
      JSONObject item = dataArray.getJSONObject(i);
      LocalDateTime dateTime = parseApiDate(item.getString("created_at"));
      LocalDate date = dateTime.toLocalDate();

      if (date.isAfter(newestDateFromDailyMcTickets)) {
        hasNewerData = true;
        MinecraftTicketsAnswers entity = new MinecraftTicketsAnswers();
        entity.setKmxWebApiMcTickets((short) item.getInt("player_id"));
        entity.setDateTime(dateTime);
        minecraftTicketsAnswersRepository.save(entity);
        savedCount++;
      }
    }

    log.info("Saved {} new Minecraft ticket entries (batch level {})", savedCount, level);
    return hasNewerData;
  }

  // ===========================================================
  // DISCORD TICKETS EXTRACTION
  // ===========================================================

  @Override
  public void extractDiscordTicketsFromAPI(LocalDate newestDateFromDcTicketsRawData) {
    log.info("=== [START] Fetching Discord tickets from API ===");
    long start = System.currentTimeMillis();

    fetchAndSaveDcTicketsData(1, newestDateFromDcTicketsRawData);

    log.info("✅ Discord ticket extraction completed in {} ms", System.currentTimeMillis() - start);
  }

  private void fetchAndSaveDcTicketsData(int level, LocalDate newestDateFromDcTicketsRawData) {
    try {
      log.info("Fetching Discord ticket batch level {}", level);
      String jsonData = fetchDataFromDcTicketsApi(level);

      if (isJsonDataEmpty(jsonData)) {
        log.info("No more Discord ticket data at level {}", level);
        return;
      }

      boolean hasNewerData = saveDcTicketsDataToDatabase(jsonData, newestDateFromDcTicketsRawData, level);

      if (hasNewerData) {
        Thread.sleep(REQUEST_DELAY_MS);
        fetchAndSaveDcTicketsData(level + 1, newestDateFromDcTicketsRawData);
      }

    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.error("Thread interrupted while processing Discord tickets: {}", e.getMessage());
    } catch (JSONException | HttpClientErrorException e) {
      log.error("Error fetching Discord tickets (level {}): {}", level, e.getMessage(), e);
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

    ResponseEntity<String> response = restTemplate.exchange(url + level, HttpMethod.POST, entity, String.class);
    return response.getBody();
  }

  private boolean saveDcTicketsDataToDatabase(String jsonData, LocalDate newestDateFromDailyDcTickets, int level)
      throws JSONException {

    JSONObject jsonObject = new JSONObject(jsonData);
    JSONArray dataArray = jsonObject.getJSONArray("data");
    boolean hasNewerData = false;
    int savedCount = 0;

    for (int i = 0; i < dataArray.length(); i++) {
      JSONObject item = dataArray.getJSONObject(i);
      LocalDateTime dateTime = parseApiDate(item.getString("created_at"));
      LocalDate date = dateTime.toLocalDate();

      if (date.isAfter(newestDateFromDailyDcTickets)) {
        hasNewerData = true;
        DiscordTicketsReactions entity = new DiscordTicketsReactions();
        entity.setDiscordId(item.getLong("player_id"));
        entity.setTicketId(item.getString("ticket_id"));
        entity.setDateTime(dateTime);
        discordTicketsReactionsRepository.save(entity);
        savedCount++;
      }
    }

    log.info("Saved {} new Discord ticket entries (batch level {})", savedCount, level);
    return hasNewerData;
  }

  // ===========================================================
  // COMMON UTILITIES
  // ===========================================================

  private LocalDateTime parseApiDate(String dateStr) {
    return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
  }

  private boolean isJsonDataEmpty(String jsonData) {
    if (jsonData == null || jsonData.isBlank()) {
      return true;
    }

    try {
      JSONObject jsonObject = new JSONObject(jsonData);
      if (!jsonObject.has("data")) {
        return true;
      }

      JSONArray dataArray = jsonObject.getJSONArray("data");
      return dataArray.length() == 0;

    } catch (JSONException e) {
      log.warn("⚠️ Invalid JSON format while checking API data: {}", e.getMessage());
      return true;
    }
  }
}
