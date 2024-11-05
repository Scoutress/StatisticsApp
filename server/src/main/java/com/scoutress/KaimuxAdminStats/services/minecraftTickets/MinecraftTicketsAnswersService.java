package com.scoutress.KaimuxAdminStats.services.minecraftTickets;

import org.springframework.boot.configurationprocessor.json.JSONException;

public interface MinecraftTicketsAnswersService {

  void fetchAndSaveData() throws JSONException;

  String fetchDataFromApi(int level) throws JSONException;
}
