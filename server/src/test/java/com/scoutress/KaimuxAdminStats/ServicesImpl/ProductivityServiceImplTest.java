package com.scoutress.KaimuxAdminStats.ServicesImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.scoutress.KaimuxAdminStats.servicesImpl.productivity.ProductivityServiceImpl;

class ProductivityServiceImplTest {

  @InjectMocks
  private ProductivityServiceImpl service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void customTestWithManualSetUpInvocation() {
    setUp();
  }

  @Test
  void testMaxOrCurrentValueOfDiscordMessages_HelperLevel_MaxValue() {
    double result = service
        .getMaxOrCurrentValueOfDiscordMessages(1.5, "Helper");

    assertEquals(1.5, result);
  }

  @Test
  void testMaxOrCurrentValueOfDiscordMessages_HelperLevel_LowerValue() {
    double result = service
        .getMaxOrCurrentValueOfDiscordMessages(0.99, "Helper");

    assertEquals(0.99, result);
  }

  @Test
  void testMaxOrCurrentValueOfDiscordMessages_HelperLevel_TooHighValue() {
    double result = service
        .getMaxOrCurrentValueOfDiscordMessages(2.75, "Helper");

    assertEquals(1.5, result);
  }

  @Test
  void testMaxOrCurrentValueOfDiscordMessages_SupportLevel_MaxValue() {
    double result = service
        .getMaxOrCurrentValueOfDiscordMessages(3, "Support");

    assertEquals(3, result);
  }

  @Test
  void testMaxOrCurrentValueOfDiscordMessages_SupportLevel_LowerValue() {
    double result = service
        .getMaxOrCurrentValueOfDiscordMessages(2.5, "Support");

    assertEquals(2.5, result);
  }

  @Test
  void testMaxOrCurrentValueOfDiscordMessages_SupportLevel_TooHighValue() {
    double result = service
        .getMaxOrCurrentValueOfDiscordMessages(7.75, "Support");

    assertEquals(3, result);
  }

  @Test
  void testMaxOrCurrentValueOfDiscordMessages_ChatModLevel_MaxValue() {
    double result = service
        .getMaxOrCurrentValueOfDiscordMessages(6, "ChatMod");

    assertEquals(6, result);
  }

  @Test
  void testMaxOrCurrentValueOfDiscordMessages_ChatModLevel_LowerValue() {
    double result = service
        .getMaxOrCurrentValueOfDiscordMessages(5.2, "ChatMod");

    assertEquals(5.2, result);
  }

  @Test
  void testMaxOrCurrentValueOfDiscordMessages_ChatModLevel_TooHighValue() {
    double result = service
        .getMaxOrCurrentValueOfDiscordMessages(7.75, "ChatMod");

    assertEquals(6, result);
  }

  @Test
  void testMaxOrCurrentValueOfDiscordMessages_OverseerLevel_MaxValue() {
    double result = service
        .getMaxOrCurrentValueOfDiscordMessages(12, "Overseer");

    assertEquals(12, result);
  }

  @Test
  void testMaxOrCurrentValueOfDiscordMessages_OverseerLevel_LowerValue() {
    double result = service
        .getMaxOrCurrentValueOfDiscordMessages(7.1, "Overseer");

    assertEquals(7.1, result);
  }

  @Test
  void testMaxOrCurrentValueOfDiscordMessages_OverseerLevel_TooHighValue() {
    double result = service
        .getMaxOrCurrentValueOfDiscordMessages(25, "Overseer");

    assertEquals(12, result);
  }

  @Test
  void testMaxOrCurrentValueOfDiscordMessages_ManagerLevel_MaxValue() {
    double result = service
        .getMaxOrCurrentValueOfDiscordMessages(24, "Manager");

    assertEquals(24, result);
  }

  @Test
  void testMaxOrCurrentValueOfDiscordMessages_ManagerLevel_LowerValue() {
    double result = service
        .getMaxOrCurrentValueOfDiscordMessages(11.7, "Manager");

    assertEquals(11.7, result);
  }

  @Test
  void testMaxOrCurrentValueOfDiscordMessages_ManagerLevel_TooHighValue() {
    double result = service
        .getMaxOrCurrentValueOfDiscordMessages(99, "Manager");

    assertEquals(24, result);
  }

  @Test
  void testCalculateAverageValueOfDiscordMessagesWithCoef_HelperLevel_MaxValue() {
    double result = service
        .calculateAverageValueOfDiscordMessagesWithCoef(1.5, "Helper");

    assertEquals((1.5 * 0.666), result);
  }

  @Test
  void testCalculateAverageValueOfDiscordMessagesWithCoef_HelperLevel_LowerValue() {
    double result = service
        .calculateAverageValueOfDiscordMessagesWithCoef(1.25, "Helper");

    assertEquals((1.25 * 0.666), result);
  }

  @Test
  void testCalculateAverageValueOfDiscordMessagesWithCoef_SupportLevel_MaxValue() {
    double result = service
        .calculateAverageValueOfDiscordMessagesWithCoef(3, "Support");

    assertEquals((3 * 0.333), result);
  }

  @Test
  void testCalculateAverageValueOfDiscordMessagesWithCoef_SupportLevel_LowerValue() {
    double result = service
        .calculateAverageValueOfDiscordMessagesWithCoef(2.75, "Support");

    assertEquals((2.75 * 0.333), result);
  }

  @Test
  void testCalculateAverageValueOfDiscordMessagesWithCoef_ChatModLevel_MaxValue() {
    double result = service
        .calculateAverageValueOfDiscordMessagesWithCoef(6, "ChatMod");

    assertEquals((6 * 0.1666), result);
  }

  @Test
  void testCalculateAverageValueOfDiscordMessagesWithCoef_ChatModLevel_LowerValue() {
    double result = service
        .calculateAverageValueOfDiscordMessagesWithCoef(5.5, "ChatMod");

    assertEquals((5.5 * 0.1666), result);
  }

  @Test
  void testCalculateAverageValueOfDiscordMessagesWithCoef_OverseerLevel_MaxValue() {
    double result = service
        .calculateAverageValueOfDiscordMessagesWithCoef(12, "Overseer");

    assertEquals((12 * 0.0832), result);
  }

  @Test
  void testCalculateAverageValueOfDiscordMessagesWithCoef_OverseerLevel_LowerValue() {
    double result = service
        .calculateAverageValueOfDiscordMessagesWithCoef(10, "Overseer");

    assertEquals((10 * 0.0832), result);
  }

  @Test
  void testCalculateAverageValueOfDiscordMessagesWithCoef_ManagerLevel_MaxValue() {
    double result = service
        .calculateAverageValueOfDiscordMessagesWithCoef(24, "Manager");

    assertEquals((24 * 0.0416), result);
  }

  @Test
  void testCalculateAverageValueOfDiscordMessagesWithCoef_ManagerLevel_LowerValue() {
    double result = service
        .calculateAverageValueOfDiscordMessagesWithCoef(17, "Manager");

    assertEquals((17 * 0.0416), result);
  }

  @Test
  void testMaxOrCurrentValueOfComparedDiscordMessages_HelperLevel_MaxValue() {
    double result = service
        .getMaxOrCurrentValueOfComparedDiscordMessages(0.0625, "Helper");

    assertEquals(0.0625, result);
  }

  @Test
  void testMaxOrCurrentValueOfComparedDiscordMessages_HelperLevel_LowerValue() {
    double result = service
        .getMaxOrCurrentValueOfComparedDiscordMessages(0.01, "Helper");

    assertEquals(0.01, result);
  }

  @Test
  void testMaxOrCurrentValueOfComparedDiscordMessages_HelperLevel_TooHighValue() {
    double result = service
        .getMaxOrCurrentValueOfComparedDiscordMessages(2.75, "Helper");

    assertEquals(0.0625, result);
  }

  @Test
  void testMaxOrCurrentValueOfComparedDiscordMessages_SupportLevel_MaxValue() {
    double result = service
        .getMaxOrCurrentValueOfComparedDiscordMessages(0.125, "Support");

    assertEquals(0.125, result);
  }

  @Test
  void testMaxOrCurrentValueOfComparedDiscordMessages_SupportLevel_LowerValue() {
    double result = service
        .getMaxOrCurrentValueOfComparedDiscordMessages(0.1, "Support");

    assertEquals(0.1, result);
  }

  @Test
  void testMaxOrCurrentValueOfComparedDiscordMessages_SupportLevel_TooHighValue() {
    double result = service
        .getMaxOrCurrentValueOfComparedDiscordMessages(7.75, "Support");

    assertEquals(0.125, result);
  }

  @Test
  void testMaxOrCurrentValueOfComparedDiscordMessages_ChatModLevel_MaxValue() {
    double result = service
        .getMaxOrCurrentValueOfComparedDiscordMessages(0.25, "ChatMod");

    assertEquals(0.25, result);
  }

  @Test
  void testMaxOrCurrentValueOfComparedDiscordMessages_ChatModLevel_LowerValue() {
    double result = service
        .getMaxOrCurrentValueOfComparedDiscordMessages(0.1, "ChatMod");

    assertEquals(0.1, result);
  }

  @Test
  void testMaxOrCurrentValueOfComparedDiscordMessages_ChatModLevel_TooHighValue() {
    double result = service
        .getMaxOrCurrentValueOfComparedDiscordMessages(7.75, "ChatMod");

    assertEquals(0.25, result);
  }

  @Test
  void testMaxOrCurrentValueOfComparedDiscordMessages_OverseerLevel_MaxValue() {
    double result = service
        .getMaxOrCurrentValueOfComparedDiscordMessages(0.5, "Overseer");

    assertEquals(0.5, result);
  }

  @Test
  void testMaxOrCurrentValueOfComparedDiscordMessages_OverseerLevel_LowerValue() {
    double result = service
        .getMaxOrCurrentValueOfComparedDiscordMessages(0.4, "Overseer");

    assertEquals(0.4, result);
  }

  @Test
  void testMaxOrCurrentValueOfComparedDiscordMessages_OverseerLevel_TooHighValue() {
    double result = service
        .getMaxOrCurrentValueOfComparedDiscordMessages(25, "Overseer");

    assertEquals(0.5, result);
  }

  @Test
  void testMaxOrCurrentValueOfComparedDiscordMessages_ManagerLevel_MaxValue() {
    double result = service
        .getMaxOrCurrentValueOfComparedDiscordMessages(1, "Manager");

    assertEquals(1, result);
  }

  @Test
  void testMaxOrCurrentValueOfComparedDiscordMessages_ManagerLevel_LowerValue() {
    double result = service
        .getMaxOrCurrentValueOfComparedDiscordMessages(0.9, "Manager");

    assertEquals(0.9, result);
  }

  @Test
  void testMaxOrCurrentValueOfComparedDiscordMessages_ManagerLevel_TooHighValue() {
    double result = service
        .getMaxOrCurrentValueOfComparedDiscordMessages(99, "Manager");

    assertEquals(1, result);
  }

  @Test
  void testCalculateAverageValueOfComparedDiscordMessagesWithCoef_HelperLevel_1() {
    double result = service
        .calculateAverageValueOfComparedDiscordMessagesWithCoef(0.1, "Helper");

    assertEquals((0.1 * 16), result);
  }

  @Test
  void testCalculateAverageValueOfComparedDiscordMessagesWithCoef_HelperLevel_2() {
    double result = service
        .calculateAverageValueOfComparedDiscordMessagesWithCoef(0.25, "Helper");

    assertEquals((0.25 * 16), result);
  }

  @Test
  void testCalculateAverageValueOfComparedDiscordMessagesWithCoef_SupportLevel_1() {
    double result = service
        .calculateAverageValueOfComparedDiscordMessagesWithCoef(0.1, "Support");

    assertEquals((0.1 * 8), result);
  }

  @Test
  void testCalculateAverageValueOfComparedDiscordMessagesWithCoef_SupportLevel_2() {
    double result = service
        .calculateAverageValueOfComparedDiscordMessagesWithCoef(0.25, "Support");

    assertEquals((0.25 * 8), result);
  }

  @Test
  void testCalculateAverageValueOfComparedDiscordMessagesWithCoef_ChatModLevel_1() {
    double result = service
        .calculateAverageValueOfComparedDiscordMessagesWithCoef(0.1, "ChatMod");

    assertEquals((0.1 * 4), result);
  }

  @Test
  void testCalculateAverageValueOfComparedDiscordMessagesWithCoef_ChatModLevel_2() {
    double result = service
        .calculateAverageValueOfComparedDiscordMessagesWithCoef(0.25, "ChatMod");

    assertEquals((0.25 * 4), result);
  }

  @Test
  void testCalculateAverageValueOfComparedDiscordMessagesWithCoef_OverseerLevel_1() {
    double result = service
        .calculateAverageValueOfComparedDiscordMessagesWithCoef(0.1, "Overseer");

    assertEquals((0.1 * 2), result);
  }

  @Test
  void testCalculateAverageValueOfComparedDiscordMessagesWithCoef_OverseerLevel_2() {
    double result = service
        .calculateAverageValueOfComparedDiscordMessagesWithCoef(0.25, "Overseer");

    assertEquals((0.25 * 2), result);
  }

  @Test
  void testCalculateAverageValueOfComparedDiscordMessagesWithCoef_ManagerLevel_1() {
    double result = service
        .calculateAverageValueOfComparedDiscordMessagesWithCoef(0.1, "Manager");

    assertEquals((0.1 * 1), result);
  }

  @Test
  void testCalculateAverageValueOfComparedDiscordMessagesWithCoef_ManagerLevel_2() {
    double result = service
        .calculateAverageValueOfComparedDiscordMessagesWithCoef(0.25, "Manager");

    assertEquals((0.25 * 1), result);
  }

  @Test
  void testMaxOrCurrentValueOfMinecraftTickets_HelperLevel_AnyValue() {
    double result = service
        .getMaxOrCurrentValueOfMinecraftTickets(1.5, "Helper");

    assertEquals(0, result);
  }

  @Test
  void testMaxOrCurrentValueOfMinecraftTickets_SupportLevel_MaxValue() {
    double result = service
        .getMaxOrCurrentValueOfMinecraftTickets(0.5, "Support");

    assertEquals(0.5, result);
  }

  @Test
  void testMaxOrCurrentValueOfMinecraftTickets_SupportLevel_LowerValue() {
    double result = service
        .getMaxOrCurrentValueOfMinecraftTickets(0.25, "Support");

    assertEquals(0.25, result);
  }

  @Test
  void testMaxOrCurrentValueOfMinecraftTickets_SupportLevel_TooHighValue() {
    double result = service
        .getMaxOrCurrentValueOfMinecraftTickets(0.75, "Support");

    assertEquals(0.5, result);
  }

  @Test
  void testMaxOrCurrentValueOfMinecraftTickets_ChatModLevel_MaxValue() {
    double result = service
        .getMaxOrCurrentValueOfMinecraftTickets(1, "ChatMod");

    assertEquals(1, result);
  }

  @Test
  void testMaxOrCurrentValueOfMinecraftTickets_ChatModLevel_LowerValue() {
    double result = service
        .getMaxOrCurrentValueOfMinecraftTickets(0.8, "ChatMod");

    assertEquals(0.8, result);
  }

  @Test
  void testMaxOrCurrentValueOfMinecraftTickets_ChatModLevel_TooHighValue() {
    double result = service
        .getMaxOrCurrentValueOfMinecraftTickets(1.75, "ChatMod");

    assertEquals(1, result);
  }

  @Test
  void testMaxOrCurrentValueOfMinecraftTickets_OverseerLevel_MaxValue() {
    double result = service
        .getMaxOrCurrentValueOfMinecraftTickets(2, "Overseer");

    assertEquals(2, result);
  }

  @Test
  void testMaxOrCurrentValueOfMinecraftTickets_OverseerLevel_LowerValue() {
    double result = service
        .getMaxOrCurrentValueOfMinecraftTickets(1.5, "Overseer");

    assertEquals(1.5, result);
  }

  @Test
  void testMaxOrCurrentValueOfMinecraftTickets_OverseerLevel_TooHighValue() {
    double result = service
        .getMaxOrCurrentValueOfMinecraftTickets(7, "Overseer");

    assertEquals(2, result);
  }

  @Test
  void testMaxOrCurrentValueOfMinecraftTickets_ManagerLevel_MaxValue() {
    double result = service
        .getMaxOrCurrentValueOfMinecraftTickets(4, "Manager");

    assertEquals(4, result);
  }

  @Test
  void testMaxOrCurrentValueOfMinecraftTickets_ManagerLevel_LowerValue() {
    double result = service
        .getMaxOrCurrentValueOfMinecraftTickets(3, "Manager");

    assertEquals(3, result);
  }

  @Test
  void testMaxOrCurrentValueOfMinecraftTickets_ManagerLevel_TooHighValue() {
    double result = service
        .getMaxOrCurrentValueOfMinecraftTickets(8.2, "Manager");

    assertEquals(4, result);
  }

  @Test
  void testCalculateAverageValueOfMinecraftTicketsWithCoef_HelperLevel_AnyValue() {
    double result = service
        .calculateAverageValueOfMinecraftTicketsWithCoef(1.5, "Helper");

    assertEquals(0, result);
  }

  @Test
  void testCalculateAverageValueOfMinecraftTicketsWithCoef_SupportLevel_MaxValue() {
    double result = service
        .calculateAverageValueOfMinecraftTicketsWithCoef(0.5, "Support");

    assertEquals((2 * 0.5), result);
  }

  @Test
  void testCalculateAverageValueOfMinecraftTicketsWithCoef_SupportLevel_LowerValue() {
    double result = service
        .calculateAverageValueOfMinecraftTicketsWithCoef(0.25, "Support");

    assertEquals((2 * 0.25), result);
  }

  @Test
  void testCalculateAverageValueOfMinecraftTicketsWithCoef_ChatModLevel_MaxValue() {
    double result = service
        .calculateAverageValueOfMinecraftTicketsWithCoef(1, "ChatMod");

    assertEquals(1, result);
  }

  @Test
  void testCalculateAverageValueOfMinecraftTicketsWithCoef_ChatModLevel_LowerValue() {
    double result = service
        .calculateAverageValueOfMinecraftTicketsWithCoef(0.8, "ChatMod");

    assertEquals(0.8, result);
  }

  @Test
  void testCalculateAverageValueOfMinecraftTicketsWithCoef_OverseerLevel_MaxValue() {
    double result = service
        .calculateAverageValueOfMinecraftTicketsWithCoef(2, "Overseer");

    assertEquals((0.5 * 2), result);
  }

  @Test
  void testCalculateAverageValueOfMinecraftTicketsWithCoef_OverseerLevel_LowerValue() {
    double result = service
        .calculateAverageValueOfMinecraftTicketsWithCoef(1.8, "Overseer");

    assertEquals((0.5 * 1.8), result);
  }

  @Test
  void testCalculateAverageValueOfMinecraftTicketsWithCoef_ManagerLevel_MaxValue() {
    double result = service
        .calculateAverageValueOfMinecraftTicketsWithCoef(4, "Manager");

    assertEquals((4 * 0.25), result);
  }

  @Test
  void testCalculateAverageValueOfMinecraftTicketsWithCoef_ManagerLevel_LowerValue() {
    double result = service
        .calculateAverageValueOfMinecraftTicketsWithCoef(3.25, "Manager");

    assertEquals((3.25 * 0.25), result);
  }

  @Test
  void testMaxOrCurrentValueOfMinecraftTicketsCompared_HelperLevel_AnyValue() {
    double result = service
        .getMaxOrCurrentValueOfMinecraftTicketsCompared(1.5, "Helper");

    assertEquals(0, result);
  }

  @Test
  void testMaxOrCurrentValueOfMinecraftTicketsCompared_SupportLevel_MaxValue() {
    double result = service
        .getMaxOrCurrentValueOfMinecraftTicketsCompared(0.2, "Support");

    assertEquals(0.2, result);
  }

  @Test
  void testMaxOrCurrentValueOfMinecraftTicketsCompared_SupportLevel_LowerValue() {
    double result = service
        .getMaxOrCurrentValueOfMinecraftTicketsCompared(0.15, "Support");

    assertEquals(0.15, result);
  }

  @Test
  void testMaxOrCurrentValueOfMinecraftTicketsCompared_SupportLevel_TooHighValue() {
    double result = service
        .getMaxOrCurrentValueOfMinecraftTicketsCompared(0.35, "Support");

    assertEquals(0.2, result);
  }

  @Test
  void testMaxOrCurrentValueOfMinecraftTicketsCompared_ChatModLevel_MaxValue() {
    double result = service
        .getMaxOrCurrentValueOfMinecraftTicketsCompared(0.4, "ChatMod");

    assertEquals(0.4, result);
  }

  @Test
  void testMaxOrCurrentValueOfMinecraftTicketsCompared_ChatModLevel_LowerValue() {
    double result = service
        .getMaxOrCurrentValueOfMinecraftTicketsCompared(0.3, "ChatMod");

    assertEquals(0.3, result);
  }

  @Test
  void testMaxOrCurrentValueOfMinecraftTicketsCompared_ChatModLevel_TooHighValue() {
    double result = service
        .getMaxOrCurrentValueOfMinecraftTicketsCompared(0.5, "ChatMod");

    assertEquals(0.4, result);
  }

  @Test
  void testMaxOrCurrentValueOfMinecraftTicketsCompared_OverseerLevel_MaxValue() {
    double result = service
        .getMaxOrCurrentValueOfMinecraftTicketsCompared(0.85, "Overseer");

    assertEquals(0.85, result);
  }

  @Test
  void testMaxOrCurrentValueOfMinecraftTicketsCompared_OverseerLevel_LowerValue() {
    double result = service
        .getMaxOrCurrentValueOfMinecraftTicketsCompared(0.7, "Overseer");

    assertEquals(0.7, result);
  }

  @Test
  void testMaxOrCurrentValueOfMinecraftTicketsCompared_OverseerLevel_TooHighValue() {
    double result = service
        .getMaxOrCurrentValueOfMinecraftTicketsCompared(0.9, "Overseer");

    assertEquals(0.85, result);
  }

  @Test
  void testMaxOrCurrentValueOfMinecraftTicketsCompared_ManagerLevel_MaxValue() {
    double result = service
        .getMaxOrCurrentValueOfMinecraftTicketsCompared(1, "Manager");

    assertEquals(1, result);
  }

  @Test
  void testMaxOrCurrentValueOfMinecraftTicketsCompared_ManagerLevel_LowerValue() {
    double result = service
        .getMaxOrCurrentValueOfMinecraftTicketsCompared(0.75, "Manager");

    assertEquals(0.75, result);
  }

  @Test
  void testMaxOrCurrentValueOfMinecraftTicketsCompared_ManagerLevel_TooHighValue() {
    double result = service
        .getMaxOrCurrentValueOfMinecraftTicketsCompared(1.25, "Manager");

    assertEquals(1, result);
  }

  @Test
  void testCalculateAverageValueOfMinecraftTicketsComparedWithCoef_HelperLevel_AnyValue() {
    double result = service
        .calculateAverageValueOfMinecraftTicketsComparedWithCoef(1.5, "Helper");

    assertEquals(0, result);
  }

  @Test
  void testCalculateAverageValueOfMinecraftTicketsComparedWithCoef_SupportLevel_MaxValue() {
    double result = service
        .calculateAverageValueOfMinecraftTicketsComparedWithCoef(0.2, "Support");

    assertEquals((0.2 * 5), result);
  }

  @Test
  void testCalculateAverageValueOfMinecraftTicketsComparedWithCoef_SupportLevel_LowerValue() {
    double result = service
        .calculateAverageValueOfMinecraftTicketsComparedWithCoef(0.15, "Support");

    assertEquals((0.15 * 5), result);
  }

  @Test
  void testCalculateAverageValueOfMinecraftTicketsComparedWithCoef_ChatModLevel_MaxValue() {
    double result = service
        .calculateAverageValueOfMinecraftTicketsComparedWithCoef(0.4, "ChatMod");

    assertEquals((0.4 * 2.5), result);
  }

  @Test
  void testCalculateAverageValueOfMinecraftTicketsComparedWithCoef_ChatModLevel_LowerValue() {
    double result = service
        .calculateAverageValueOfMinecraftTicketsComparedWithCoef(0.3, "ChatMod");

    assertEquals((0.3 * 2.5), result);
  }

  @Test
  void testCalculateAverageValueOfMinecraftTicketsComparedWithCoef_OverseerLevel_MaxValue() {
    double result = service
        .calculateAverageValueOfMinecraftTicketsComparedWithCoef(0.85, "Overseer");

    assertEquals((0.85 * 1.17), result);
  }

  @Test
  void testCalculateAverageValueOfMinecraftTicketsComparedWithCoef_OverseerLevel_LowerValue() {
    double result = service
        .calculateAverageValueOfMinecraftTicketsComparedWithCoef(0.7, "Overseer");

    assertEquals((0.7 * 1.17), result);
  }

  @Test
  void testCalculateAverageValueOfMinecraftTicketsComparedWithCoef_ManagerLevel_MaxValue() {
    double result = service
        .calculateAverageValueOfMinecraftTicketsComparedWithCoef(1, "Manager");

    assertEquals(1, result);
  }

  @Test
  void testCalculateAverageValueOfMinecraftTicketsComparedWithCoef_ManagerLevel_LowerValue() {
    double result = service
        .calculateAverageValueOfMinecraftTicketsComparedWithCoef(0.9, "Manager");

    assertEquals(0.9, result);
  }

  @Test
  void testMaxOrCurrentValueOfPlaytime_HelperLevel_MaxValue() {
    double result = service
        .getMaxOrCurrentValueOfPlaytime(0.5, "Helper");

    assertEquals(0.5, result);
  }

  @Test
  void testMaxOrCurrentValueOfPlaytime_HelperLevel_LowerValue() {
    double result = service
        .getMaxOrCurrentValueOfPlaytime(0.25, "Helper");

    assertEquals(0.25, result);
  }

  @Test
  void testMaxOrCurrentValueOfPlaytime_HelperLevel_TooHighValue() {
    double result = service
        .getMaxOrCurrentValueOfPlaytime(2.75, "Helper");

    assertEquals(0.5, result);
  }

  @Test
  void testMaxOrCurrentValueOfPlaytime_SupportLevel_MaxValue() {
    double result = service
        .getMaxOrCurrentValueOfPlaytime(1, "Support");

    assertEquals(1, result);
  }

  @Test
  void testMaxOrCurrentValueOfPlaytime_SupportLevel_LowerValue() {
    double result = service
        .getMaxOrCurrentValueOfPlaytime(0.8, "Support");

    assertEquals(0.8, result);
  }

  @Test
  void testMaxOrCurrentValueOfPlaytime_SupportLevel_TooHighValue() {
    double result = service
        .getMaxOrCurrentValueOfPlaytime(7.75, "Support");

    assertEquals(1, result);
  }

  @Test
  void testMaxOrCurrentValueOfPlaytime_ChatModLevel_MaxValue() {
    double result = service
        .getMaxOrCurrentValueOfPlaytime(2, "ChatMod");

    assertEquals(2, result);
  }

  @Test
  void testMaxOrCurrentValueOfPlaytime_ChatModLevel_LowerValue() {
    double result = service
        .getMaxOrCurrentValueOfPlaytime(1.5, "ChatMod");

    assertEquals(1.5, result);
  }

  @Test
  void testMaxOrCurrentValueOfPlaytime_ChatModLevel_TooHighValue() {
    double result = service
        .getMaxOrCurrentValueOfPlaytime(7.75, "ChatMod");

    assertEquals(2, result);
  }

  @Test
  void testMaxOrCurrentValueOfPlaytime_OverseerLevel_MaxValue() {
    double result = service
        .getMaxOrCurrentValueOfPlaytime(4, "Overseer");

    assertEquals(4, result);
  }

  @Test
  void testMaxOrCurrentValueOfPlaytime_OverseerLevel_LowerValue() {
    double result = service
        .getMaxOrCurrentValueOfPlaytime(3, "Overseer");

    assertEquals(3, result);
  }

  @Test
  void testMaxOrCurrentValueOfPlaytime_OverseerLevel_TooHighValue() {
    double result = service
        .getMaxOrCurrentValueOfPlaytime(25, "Overseer");

    assertEquals(4, result);
  }

  @Test
  void testMaxOrCurrentValueOfPlaytime_ManagerLevel_MaxValue() {
    double result = service
        .getMaxOrCurrentValueOfPlaytime(8, "Manager");

    assertEquals(8, result);
  }

  @Test
  void testMaxOrCurrentValueOfPlaytime_ManagerLevel_LowerValue() {
    double result = service
        .getMaxOrCurrentValueOfPlaytime(7, "Manager");

    assertEquals(7, result);
  }

  @Test
  void testMaxOrCurrentValueOfPlaytime_ManagerLevel_TooHighValue() {
    double result = service
        .getMaxOrCurrentValueOfPlaytime(99, "Manager");

    assertEquals(8, result);
  }

  @Test
  void testCalculateAverageValueOfPlaytimeWithCoef_HelperLevel_MaxValue() {
    double result = service
        .calculateAverageValueOfPlaytimeWithCoef(0.5, "Helper");

    assertEquals((0.5 * 2), result);
  }

  @Test
  void testCalculateAverageValueOfPlaytimeWithCoef_HelperLevel_LowerValue() {
    double result = service
        .calculateAverageValueOfPlaytimeWithCoef(0.25, "Helper");

    assertEquals((0.25 * 2), result);
  }

  @Test
  void testCalculateAverageValueOfPlaytimeWithCoef_SupportLevel_MaxValue() {
    double result = service
        .calculateAverageValueOfPlaytimeWithCoef(1, "Support");

    assertEquals((1 * 1), result);
  }

  @Test
  void testCalculateAverageValueOfPlaytimeWithCoef_SupportLevel_LowerValue() {
    double result = service
        .calculateAverageValueOfPlaytimeWithCoef(0.9, "Support");

    assertEquals((0.9 * 1), result);
  }

  @Test
  void testCalculateAverageValueOfPlaytimeWithCoef_ChatModLevel_MaxValue() {
    double result = service
        .calculateAverageValueOfPlaytimeWithCoef(2, "ChatMod");

    assertEquals((2 * 0.5), result);
  }

  @Test
  void testCalculateAverageValueOfPlaytimeWithCoef_ChatModLevel_LowerValue() {
    double result = service
        .calculateAverageValueOfPlaytimeWithCoef(1.7, "ChatMod");

    assertEquals((1.7 * 0.5), result);
  }

  @Test
  void testCalculateAverageValueOfPlaytimeWithCoef_OverseerLevel_MaxValue() {
    double result = service
        .calculateAverageValueOfPlaytimeWithCoef(4, "Overseer");

    assertEquals((4 * 0.25), result);
  }

  @Test
  void testCalculateAverageValueOfPlaytimeWithCoef_OverseerLevel_LowerValue() {
    double result = service
        .calculateAverageValueOfPlaytimeWithCoef(3, "Overseer");

    assertEquals((3 * 0.25), result);
  }

  @Test
  void testCalculateAverageValueOfPlaytimeWithCoef_ManagerLevel_MaxValue() {
    double result = service
        .calculateAverageValueOfPlaytimeWithCoef(8, "Manager");

    assertEquals((8 * 0.125), result);
  }

  @Test
  void testCalculateAverageValueOfPlaytimeWithCoef_ManagerLevel_LowerValue() {
    double result = service
        .calculateAverageValueOfPlaytimeWithCoef(7, "Manager");

    assertEquals((7 * 0.125), result);
  }

  @Test
  void testCalculateAverageValueOdAllFinals_HelperLevel_1() {
    double afkPlaytimeFinalValue = 0.1;
    double discordMessagesFinalValue = 0.2;
    double discordMessagesComparedFinalValue = 0.3;
    double minecraftTicketsFinalValue = 0.4;
    double minecraftTicketsComparedFinalValue = 0.5;
    double playtimeFinalValue = 0.6;
    String employeeLevel = "Helper";

    double result = service.calculateAverageValueOfAllFinals(
        afkPlaytimeFinalValue,
        discordMessagesFinalValue,
        discordMessagesComparedFinalValue,
        minecraftTicketsFinalValue,
        minecraftTicketsComparedFinalValue,
        playtimeFinalValue,
        employeeLevel);

    assertEquals(((0.1 + 0.2 + 0.3 + 0.6) / 4), result);
  }

  @Test
  void testCalculateAverageValueOdAllFinals_HelperLevel_2() {
    double afkPlaytimeFinalValue = 0.2;
    double discordMessagesFinalValue = 0.5;
    double discordMessagesComparedFinalValue = 0.8;
    double minecraftTicketsFinalValue = 0.1;
    double minecraftTicketsComparedFinalValue = 0.3;
    double playtimeFinalValue = 0.9;
    String employeeLevel = "Helper";

    double result = service.calculateAverageValueOfAllFinals(
        afkPlaytimeFinalValue,
        discordMessagesFinalValue,
        discordMessagesComparedFinalValue,
        minecraftTicketsFinalValue,
        minecraftTicketsComparedFinalValue,
        playtimeFinalValue,
        employeeLevel);

    assertEquals(((0.2 + 0.5 + 0.8 + 0.9) / 4), result);
  }

  @Test
  void testCalculateAverageValueOdAllFinals_SupportLevel_1() {
    double afkPlaytimeFinalValue = 0.1;
    double discordMessagesFinalValue = 0.2;
    double discordMessagesComparedFinalValue = 0.3;
    double minecraftTicketsFinalValue = 0.4;
    double minecraftTicketsComparedFinalValue = 0.5;
    double playtimeFinalValue = 0.6;
    String employeeLevel = "Support";

    double result = service.calculateAverageValueOfAllFinals(
        afkPlaytimeFinalValue,
        discordMessagesFinalValue,
        discordMessagesComparedFinalValue,
        minecraftTicketsFinalValue,
        minecraftTicketsComparedFinalValue,
        playtimeFinalValue,
        employeeLevel);

    assertEquals(((0.1 + 0.2 + 0.3 + 0.4 + 0.5 + 0.6) / 6), result);
  }

  @Test
  void testCalculateAverageValueOdAllFinals_SupportLevel_2() {
    double afkPlaytimeFinalValue = 0.2;
    double discordMessagesFinalValue = 0.5;
    double discordMessagesComparedFinalValue = 0.8;
    double minecraftTicketsFinalValue = 0.1;
    double minecraftTicketsComparedFinalValue = 0.3;
    double playtimeFinalValue = 0.9;
    String employeeLevel = "Support";

    double result = service.calculateAverageValueOfAllFinals(
        afkPlaytimeFinalValue,
        discordMessagesFinalValue,
        discordMessagesComparedFinalValue,
        minecraftTicketsFinalValue,
        minecraftTicketsComparedFinalValue,
        playtimeFinalValue,
        employeeLevel);

    assertEquals(((0.2 + 0.5 + 0.8 + 0.1 + 0.3 + 0.9) / 6), result);
  }

  @Test
  void testCalculateAverageValueOdAllFinals_ChatModLevel_1() {
    double afkPlaytimeFinalValue = 0.9;
    double discordMessagesFinalValue = 0.8;
    double discordMessagesComparedFinalValue = 0.7;
    double minecraftTicketsFinalValue = 0.6;
    double minecraftTicketsComparedFinalValue = 0.5;
    double playtimeFinalValue = 0.4;
    String employeeLevel = "ChatMod";

    double result = service.calculateAverageValueOfAllFinals(
        afkPlaytimeFinalValue,
        discordMessagesFinalValue,
        discordMessagesComparedFinalValue,
        minecraftTicketsFinalValue,
        minecraftTicketsComparedFinalValue,
        playtimeFinalValue,
        employeeLevel);

    assertEquals(((0.9 + 0.8 + 0.7 + 0.6 + 0.5 + 0.4) / 6), result);
  }

  @Test
  void testCalculateAverageValueOdAllFinals_ChatModLevel_2() {
    double afkPlaytimeFinalValue = 0.11;
    double discordMessagesFinalValue = 0.33;
    double discordMessagesComparedFinalValue = 0.55;
    double minecraftTicketsFinalValue = 0.77;
    double minecraftTicketsComparedFinalValue = 0.99;
    double playtimeFinalValue = 0.22;
    String employeeLevel = "ChatMod";

    double result = service.calculateAverageValueOfAllFinals(
        afkPlaytimeFinalValue,
        discordMessagesFinalValue,
        discordMessagesComparedFinalValue,
        minecraftTicketsFinalValue,
        minecraftTicketsComparedFinalValue,
        playtimeFinalValue,
        employeeLevel);

    assertEquals(((0.11 + 0.33 + 0.55 + 0.77 + 0.99 + 0.22) / 6), result);
  }

  @Test
  void testCalculateAverageValueOdAllFinals_OverseerLevel_1() {
    double afkPlaytimeFinalValue = 0.123;
    double discordMessagesFinalValue = 0.456;
    double discordMessagesComparedFinalValue = 0.789;
    double minecraftTicketsFinalValue = 0.147;
    double minecraftTicketsComparedFinalValue = 0.258;
    double playtimeFinalValue = 0.369;
    String employeeLevel = "Overseer";

    double result = service.calculateAverageValueOfAllFinals(
        afkPlaytimeFinalValue,
        discordMessagesFinalValue,
        discordMessagesComparedFinalValue,
        minecraftTicketsFinalValue,
        minecraftTicketsComparedFinalValue,
        playtimeFinalValue,
        employeeLevel);

    assertEquals(((0.123 + 0.456 + 0.789 + 0.147 + 0.258 + 0.369) / 6), result);
  }

  @Test
  void testCalculateAverageValueOdAllFinals_OverseerLevel_2() {
    double afkPlaytimeFinalValue = 0.963;
    double discordMessagesFinalValue = 0.852;
    double discordMessagesComparedFinalValue = 0.741;
    double minecraftTicketsFinalValue = 0.987;
    double minecraftTicketsComparedFinalValue = 0.654;
    double playtimeFinalValue = 0.321;
    String employeeLevel = "Overseer";

    double result = service.calculateAverageValueOfAllFinals(
        afkPlaytimeFinalValue,
        discordMessagesFinalValue,
        discordMessagesComparedFinalValue,
        minecraftTicketsFinalValue,
        minecraftTicketsComparedFinalValue,
        playtimeFinalValue,
        employeeLevel);

    assertEquals(((0.963 + 0.852 + 0.741 + 0.987 + 0.654 + 0.321) / 6), result);
  }

  @Test
  void testCalculateAverageValueOdAllFinals_ManagerLevel_1() {
    double afkPlaytimeFinalValue = 0.11;
    double discordMessagesFinalValue = 0.22;
    double discordMessagesComparedFinalValue = 0.33;
    double minecraftTicketsFinalValue = 0.44;
    double minecraftTicketsComparedFinalValue = 0.55;
    double playtimeFinalValue = 0.66;
    String employeeLevel = "Manager";

    double result = service.calculateAverageValueOfAllFinals(
        afkPlaytimeFinalValue,
        discordMessagesFinalValue,
        discordMessagesComparedFinalValue,
        minecraftTicketsFinalValue,
        minecraftTicketsComparedFinalValue,
        playtimeFinalValue,
        employeeLevel);

    assertEquals(((0.11 + 0.22 + 0.33 + 0.44 + 0.55 + 0.66) / 6), result);
  }

  @Test
  void testCalculateAverageValueOdAllFinals_ManagerLevel_2() {
    double afkPlaytimeFinalValue = 0.99;
    double discordMessagesFinalValue = 0.88;
    double discordMessagesComparedFinalValue = 0.77;
    double minecraftTicketsFinalValue = 0.66;
    double minecraftTicketsComparedFinalValue = 0.55;
    double playtimeFinalValue = 0.44;
    String employeeLevel = "Manager";

    double result = service.calculateAverageValueOfAllFinals(
        afkPlaytimeFinalValue,
        discordMessagesFinalValue,
        discordMessagesComparedFinalValue,
        minecraftTicketsFinalValue,
        minecraftTicketsComparedFinalValue,
        playtimeFinalValue,
        employeeLevel);

    assertEquals(((0.99 + 0.88 + 0.77 + 0.66 + 0.55 + 0.44) / 6), result);
  }

  @Test
  void testCalculateFinalProductivityValue_1() {
    double averageValueOfAllFinals = 0.85;
    double complaintsFinalValue = 5;

    double result = service.calculateFinalProductivityValue(
        averageValueOfAllFinals, complaintsFinalValue);

    assertEquals((0.85 - (5 * 0.01)), result);
  }

  @Test
  void testCalculateFinalProductivityValue_2() {
    double averageValueOfAllFinals = 0.53;
    double complaintsFinalValue = 7;

    double result = service.calculateFinalProductivityValue(
        averageValueOfAllFinals, complaintsFinalValue);

    assertEquals((0.53 - (7 * 0.01)), result);
  }
}
