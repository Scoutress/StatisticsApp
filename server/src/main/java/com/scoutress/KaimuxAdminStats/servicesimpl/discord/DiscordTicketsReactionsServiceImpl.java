package com.scoutress.KaimuxAdminStats.servicesimpl.discord;

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
import com.scoutress.KaimuxAdminStats.entity.discord.DiscordTicketsReactions;
import com.scoutress.KaimuxAdminStats.repositories.discord.DiscordTicketsReactionsRepository;
import com.scoutress.KaimuxAdminStats.services.discord.DiscordTicketsReactionsService;

@Service
public class DiscordTicketsReactionsServiceImpl implements DiscordTicketsReactionsService {

  private final RestTemplate restTemplate;
  private final KaimuxWebsiteConfig kaimuxWebsiteConfig;
  private final DiscordTicketsReactionsRepository discordTicketsReactionsRepository;

  public DiscordTicketsReactionsServiceImpl(
      RestTemplate restTemplate,
      KaimuxWebsiteConfig kaimuxWebsiteConfig,
      DiscordTicketsReactionsRepository discordTicketsReactionsRepository) {

    this.restTemplate = restTemplate;
    this.kaimuxWebsiteConfig = kaimuxWebsiteConfig;
    this.discordTicketsReactionsRepository = discordTicketsReactionsRepository;
  }

  @Override
  public void fetchAndSaveData() throws JSONException {
    String jsonData = fetchDataFromApi();
    saveDataToDatabase(jsonData);
  }

  @Override
  public String fetchDataFromApi() throws JSONException {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    String api = kaimuxWebsiteConfig.getApiToken();
    String url = kaimuxWebsiteConfig.getApiForDiscordTickets();
    int level = 1;

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
