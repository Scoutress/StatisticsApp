package com.scoutress.KaimuxAdminStats.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DcBotConfig {

  private final String dcBotApi;

  public DcBotConfig(@Value("${bot.api.url}") String dcBotApi) {
    this.dcBotApi = dcBotApi;
  }

  public String getDcBotApi() {
    return dcBotApi;
  }
}
