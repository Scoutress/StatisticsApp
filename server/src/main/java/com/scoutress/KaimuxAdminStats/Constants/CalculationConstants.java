package com.scoutress.KaimuxAdminStats.constants;

public class CalculationConstants {

  // Coeficients for average playtime per day per level
  public static final double PLAYTIME_HELPER = 2;
  public static final double PLAYTIME_SUPPORT = 1;
  public static final double PLAYTIME_CHATMOD = 0.5;
  public static final double PLAYTIME_OVERSEER = 0.25;
  public static final double PLAYTIME_MANAGER = 0.125;

  // Max values of average playtime per day per level
  public static final double PLAYTIME_MAX_HELPER = 0.5;
  public static final double PLAYTIME_MAX_SUPPORT = 1;
  public static final double PLAYTIME_MAX_CHATMOD = 2;
  public static final double PLAYTIME_MAX_OVERSEER = 4;
  public static final double PLAYTIME_MAX_MANAGER = 8;

  // Coeficients for average Discord messages per day per level
  public static final double DISCORD_MESSAGES_HELPER = 0.666;
  public static final double DISCORD_MESSAGES_SUPPORT = 0.333;
  public static final double DISCORD_MESSAGES_CHATMOD = 0.1666;
  public static final double DISCORD_MESSAGES_OVERSEER = 0.0832;
  public static final double DISCORD_MESSAGES_MANAGER = 0.0416;

  // Max values of average Discord messages per day per level
  public static final double DISCORD_MESSAGES_MAX_HELPER = 1.5;
  public static final double DISCORD_MESSAGES_MAX_SUPPORT = 3;
  public static final double DISCORD_MESSAGES_MAX_CHATMOD = 6;
  public static final double DISCORD_MESSAGES_MAX_OVERSEER = 12;
  public static final double DISCORD_MESSAGES_MAX_MANAGER = 24;

  // Coeficients for average compared Discord messages per day per level
  public static final double DISCORD_MESSAGES_COMPARED_HELPER = 16;
  public static final double DISCORD_MESSAGES_COMPARED_SUPPORT = 8;
  public static final double DISCORD_MESSAGES_COMPARED_CHATMOD = 4;
  public static final double DISCORD_MESSAGES_COMPARED_OVERSEER = 2;
  public static final double DISCORD_MESSAGES_COMPARED_MANAGER = 1;

  // Max values of average compared Discord messages per day per level
  public static final double DISCORD_MESSAGES_COMPARED_MAX_HELPER = 0.0625;
  public static final double DISCORD_MESSAGES_COMPARED_MAX_SUPPORT = 0.125;
  public static final double DISCORD_MESSAGES_COMPARED_MAX_CHATMOD = 0.25;
  public static final double DISCORD_MESSAGES_COMPARED_MAX_OVERSEER = 0.5;
  public static final double DISCORD_MESSAGES_COMPARED_MAX_MANAGER = 1;

  // Coeficients for average Minecraft tickets per day per level
  public static final double MINECRAFT_TICKETS_SUPPORT = 2;
  public static final double MINECRAFT_TICKETS_CHATMOD = 1;
  public static final double MINECRAFT_TICKETS_OVERSEER = 0.5;
  public static final double MINECRAFT_TICKETS_MANAGER = 0.25;

  // Max values of average Minecraft tickets per day per level
  public static final double MINECRAFT_TICKETS_MAX_SUPPORT = 0.5;
  public static final double MINECRAFT_TICKETS_MAX_CHATMOD = 1;
  public static final double MINECRAFT_TICKETS_MAX_OVERSEER = 2;
  public static final double MINECRAFT_TICKETS_MAX_MANAGER = 4;

  // Coeficients for average compared Minecraft tickets per day per level
  public static final double MINECRAFT_TICKETS_COMPARED_SUPPORT = 5;
  public static final double MINECRAFT_TICKETS_COMPARED_CHATMOD = 2.5;
  public static final double MINECRAFT_TICKETS_COMPARED_OVERSEER = 1.17;
  public static final double MINECRAFT_TICKETS_COMPARED_MANAGER = 1;

  // Max values of average compared Minecraft tickets per day per level
  public static final double MINECRAFT_TICKETS_COMPARED_MAX_SUPPORT = 0.2;
  public static final double MINECRAFT_TICKETS_COMPARED_MAX_CHATMOD = 0.4;
  public static final double MINECRAFT_TICKETS_COMPARED_MAX_OVERSEER = 0.85;
  public static final double MINECRAFT_TICKETS_COMPARED_MAX_MANAGER = 1;

  // Minimum days to be promoted
  public static final int WORK_TIME_HELPER = 30;
  public static final int WORK_TIME_SUPPORT = 90;
  public static final int WORK_TIME_CHATMOD = 210;
  public static final int WORK_TIME_OVERSEER = 570;

  public static final int MIN_ANNUAL_PLAYTIME = 4;
  public static final double PROMOTION_VALUE = 0.9;
  public static final double DEMOTION_VALUE = 0.1;

  private CalculationConstants() {
  }
}