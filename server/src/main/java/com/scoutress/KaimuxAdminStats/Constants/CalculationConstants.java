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

  public static final double AFK_PLAYTIME_HELPER = 2;
  public static final double AFK_PLAYTIME_SUPPORT = 5;
  public static final double AFK_PLAYTIME_CHATMOD = 10;
  public static final double AFK_PLAYTIME_OVERSEER = 20;
  public static final double AFK_PLAYTIME_MANAGER = 50;

  // Coeficients for average Discord messages per day per level
  public static final double DISCORD_MESSAGES_HELPER = 0.66;
  public static final double DISCORD_MESSAGES_SUPPORT = 0.33;
  public static final double DISCORD_MESSAGES_CHATMOD = 0.17;
  public static final double DISCORD_MESSAGES_OVERSEER = 0.085;
  public static final double DISCORD_MESSAGES_MANAGER = 0.042;

  // Max values of average Discord messages per day per level
  public static final double DISCORD_MESSAGES_MAX_HELPER = 1.5;
  public static final double DISCORD_MESSAGES_MAX_SUPPORT = 3;
  public static final double DISCORD_MESSAGES_MAX_CHATMOD = 6;
  public static final double DISCORD_MESSAGES_MAX_OVERSEER = 12;
  public static final double DISCORD_MESSAGES_MAX_MANAGER = 24;

  // Coeficients for average compared Discord messages per day per level
  public static final double DISCORD_MESSAGES_COMPARED_HELPER = 66;
  public static final double DISCORD_MESSAGES_COMPARED_SUPPORT = 33;
  public static final double DISCORD_MESSAGES_COMPARED_CHATMOD = 17;
  public static final double DISCORD_MESSAGES_COMPARED_OVERSEER = 8.5;
  public static final double DISCORD_MESSAGES_COMPARED_MANAGER = 4.2;

  public static final double DISCORD_TICKETS_SUPPORT = 0.3275;
  public static final double DISCORD_TICKETS_CHATMOD = 0.4803;
  public static final double DISCORD_TICKETS_OVERSEER = 0.4803;
  public static final double DISCORD_TICKETS_MANAGER = 0.9520;

  public static final double DISCORD_TICKETS_COMPARED_SUPPORT = 1; // TODO
  public static final double DISCORD_TICKETS_COMPARED_CHATMOD = 1; // TODO
  public static final double DISCORD_TICKETS_COMPARED_OVERSEER = 1; // TODO
  public static final double DISCORD_TICKETS_COMPARED_MANAGER = 1; // TODO

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
  public static final double MINECRAFT_TICKETS_COMPARED_MAX_SUPPORT = 20;
  public static final double MINECRAFT_TICKETS_COMPARED_MAX_CHATMOD = 40;
  public static final double MINECRAFT_TICKETS_COMPARED_MAX_OVERSEER = 85;
  public static final double MINECRAFT_TICKETS_COMPARED_MAX_MANAGER = 100;

  // Minimum days to be promoted
  public static final int WORK_TIME_HELPER = 30;
  public static final int WORK_TIME_SUPPORT = 90;
  public static final int WORK_TIME_CHATMOD = 210;
  public static final int WORK_TIME_OVERSEER = 570;

  public static final int MIN_ANNUAL_PLAYTIME = 4;
  public static final int PROMOTION_VALUE = 90;
  public static final int DEMOTION_VALUE = 10;

  private CalculationConstants() {
  }
}