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

  public String fetchDataFromApi(int level) throws JSONException {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    String api = kaimuxWebsiteConfig.getApiToken();
    String url = kaimuxWebsiteConfig.getApiForServerVisitors();

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

    LocalDateTime latestDateTime = findLatestDateTime();

    for (int i = 0; i < dataArray.length(); i++) {
      JSONObject item = dataArray.getJSONObject(i);

      String createdAt = item.optString("created_at", "1970-01-01T00:00:00");
      LocalDateTime dateTime = LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_DATE_TIME);

      if (dateTime.isAfter(latestDateTime)) {
        ProjectVisitorsRawData entity = new ProjectVisitorsRawData();
        entity.setIp(item.optString("ip", "unknown"));
        entity.setType(item.optInt("type", 0));
        entity.setPremium(item.optInt("is_premium", 0) == 1);
        entity.setDateTime(dateTime);

        projectVisitorsRawDataRepository.save(entity);
      }
    }
  }

  public LocalDateTime findLatestDateTime() {
    return projectVisitorsRawDataRepository.findAll()
        .stream()
        .map(ProjectVisitorsRawData::getDateTime)
        .max(LocalDateTime::compareTo)
        .orElse(LocalDateTime.MIN);
  }
}
