package com.scoutress.KaimuxAdminStats.services.minecraftTickets;

import org.springframework.boot.configurationprocessor.json.JSONException;

public interface MinecraftTicketsAnswersService {

  void fetchAndSaveData();

  String fetchDataFromApi(int level) throws JSONException;
}
