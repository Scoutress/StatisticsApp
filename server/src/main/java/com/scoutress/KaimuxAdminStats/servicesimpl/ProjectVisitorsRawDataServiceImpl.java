package com.scoutress.KaimuxAdminStats.servicesimpl;

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
import com.scoutress.KaimuxAdminStats.repositories.ProjectVisitorsRawDataRepository;
import com.scoutress.KaimuxAdminStats.services.ProjectVisitorsRawDataService;

@Service
public class ProjectVisitorsRawDataServiceImpl implements ProjectVisitorsRawDataService {

  private final RestTemplate restTemplate;
  private final KaimuxWebsiteConfig kaimuxWebsiteConfig;
  private final ProjectVisitorsRawDataRepository projectVisitorsRawDataRepository;
  private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

  public ProjectVisitorsRawDataServiceImpl(
      RestTemplate restTemplate,
      KaimuxWebsiteConfig kaimuxWebsiteConfig,
      ProjectVisitorsRawDataRepository projectVisitorsRawDataRepository) {

    this.restTemplate = restTemplate;
    this.kaimuxWebsiteConfig = kaimuxWebsiteConfig;
    this.projectVisitorsRawDataRepository = projectVisitorsRawDataRepository;
  }

  @Override
  public void fetchAndSaveData() {
    fetchDataWithDelay(1);
  }

  private void fetchDataWithDelay(int level) {
    System.out.println("DEBUG: Scheduling data fetch for level " + level);

    executorService.schedule(() -> {
      try {
        String jsonData = fetchDataFromApi(level);
        System.out.println("DEBUG: Fetched data from API at level " + level);

        if (!isDataEmpty(jsonData)) {
          System.out.println("DEBUG: Data is not empty at level " + level + ", saving to database...");
          saveDataToDatabase(jsonData);
          System.out.println("DEBUG: Data saved for level " + level);

          fetchDataWithDelay(level + 1); // Recursively fetch next level
        } else {
          System.out.println("DEBUG: No more data available at level " + level);
        }
      } catch (JSONException e) {
        System.err.println("JSON Parsing Error at level " + level + ": " + e.getMessage());
      } catch (HttpClientErrorException e) {
        System.err.println("HTTP Error at level " + level + ": " + e.getMessage());
      }
    }, 2, TimeUnit.SECONDS);
  }

  private String fetchDataFromApi(int level) throws JSONException {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    String api = kaimuxWebsiteConfig.getApiToken();
    String url = kaimuxWebsiteConfig.getApiForServerVisitors();

    JSONObject requestBody = new JSONObject();
    requestBody.put("api_token", api);

    HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

    try {
      System.out.println("DEBUG: Sending request to API for level " + level);
      ResponseEntity<String> response = restTemplate.exchange(
          url + level,
          HttpMethod.POST,
          entity,
          String.class);

      System.out.println("DEBUG: Received response from API for level " + level);
      return response.getBody();
    } catch (HttpClientErrorException e) {
      System.err.println("HTTP Error while fetching level " + level + ": " + e.getMessage());
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

    System.out.println("DEBUG: Saving data to database. Number of records to save: " + dataArray.length());

    for (int i = 0; i < dataArray.length(); i++) {
      JSONObject item = dataArray.getJSONObject(i);

      ProjectVisitorsRawData entity = new ProjectVisitorsRawData();
      entity.setIp(item.optString("ip", "unknown"));
      entity.setType(item.optInt("type", 0));

      int premiumInt = item.optInt("is_premium", 0);
      entity.setPremium(premiumInt == 1);

      String createdAt = item.optString("created_at", "1970-01-01T00:00:00");
      LocalDateTime dateTime = LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_DATE_TIME);
      entity.setDateTime(dateTime);

      projectVisitorsRawDataRepository.save(entity);
      System.out.println("DEBUG: Record saved for IP: " + entity.getIp() + ", DateTime: " + entity.getDateTime());
    }
  }

  public LocalDateTime findLatestDateTime() {
    return projectVisitorsRawDataRepository.findAll()
        .stream()
        .map(ProjectVisitorsRawData::getDateTime)
        .max(LocalDateTime::compareTo)
        .orElse(LocalDateTime.MIN);
  }

  @Override
  public void fetchAndSaveDataFromLastSavedPage() {
    int totalRecords = (int) projectVisitorsRawDataRepository.count(); // Get current record count
    int recordsPerPage = 30;
    int startLevel = (totalRecords / recordsPerPage) + 1; // Calculate the starting page level

    System.out.println("DEBUG: Total records in database: " + totalRecords);
    System.out.println("DEBUG: Starting data fetch from level: " + startLevel);

    fetchDataWithDelay(startLevel); // Start fetching from the calculated level
  }
}
