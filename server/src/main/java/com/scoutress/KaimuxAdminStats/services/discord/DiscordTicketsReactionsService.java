package com.scoutress.KaimuxAdminStats.services.discord;

import org.springframework.boot.configurationprocessor.json.JSONException;

public interface DiscordTicketsReactionsService {

  String fetchDataFromApi() throws JSONException;

  void fetchAndSaveData() throws JSONException;
}
