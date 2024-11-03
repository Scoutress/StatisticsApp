package com.scoutress.KaimuxAdminStats.services.discord;

import org.springframework.boot.configurationprocessor.json.JSONException;

public interface DiscordTicketsReactionsService {

  void fetchAndSaveData() throws JSONException;

  String fetchDataFromApi(int level) throws JSONException;
}
