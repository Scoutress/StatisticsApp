package com.scoutress.KaimuxAdminStats.config;

import org.springframework.context.annotation.Configuration;

import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class DcBotConfig {

  private final String dcBotApi;

  public DcBotConfig() {
    Dotenv dotenv = Dotenv.load();
    this.dcBotApi = dotenv.get("BOT_API_URL");
  }

  public String getDcBotApi() {
    return dcBotApi;
  }
}
