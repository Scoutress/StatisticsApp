package com.scoutress.KaimuxAdminStats.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KaimuxWebsiteConfig {

  @Value("${kaimux-api-token}")
  private String apiToken;

  @Value("${kaimux-website-api-for-discord-tickets}")
  private String apiForDiscordTickets;

  @Value("${kaimux-website-api-for-minecraft-tickets}")
  private String apiForMinecraftTickets;

  @Value("${kaimux-website-api-for-server-visitors}")
  private String apiForServerVisitors;

  public String getApiToken() {
    return apiToken;
  }

  public String getApiForDiscordTickets() {
    return apiForDiscordTickets;
  }

  public String getApiForMinecraftTickets() {
    return apiForMinecraftTickets;
  }

  public String getApiForServerVisitors() {
    return apiForServerVisitors;
  }
}
