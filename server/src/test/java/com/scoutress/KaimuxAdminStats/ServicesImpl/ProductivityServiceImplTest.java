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

    assertEquals((1.5 * 0.66), result);
  }

  @Test
  void testCalculateAverageValueOfDiscordMessagesWithCoef_HelperLevel_LowerValue() {
    double result = service
        .calculateAverageValueOfDiscordMessagesWithCoef(1.25, "Helper");

    assertEquals((1.25 * 0.66), result);
  }

  @Test
  void testCalculateAverageValueOfDiscordMessagesWithCoef_SupportLevel_MaxValue() {
    double result = service
        .calculateAverageValueOfDiscordMessagesWithCoef(3, "Support");

    assertEquals((3 * 0.33), result);
  }

  @Test
  void testCalculateAverageValueOfDiscordMessagesWithCoef_SupportLevel_LowerValue() {
    double result = service
        .calculateAverageValueOfDiscordMessagesWithCoef(2.75, "Support");

    assertEquals((2.75 * 0.33), result);
  }

  @Test
  void testCalculateAverageValueOfDiscordMessagesWithCoef_ChatModLevel_MaxValue() {
    double result = service
        .calculateAverageValueOfDiscordMessagesWithCoef(6, "ChatMod");

    assertEquals((6 * 0.17), result);
  }

  @Test
  void testCalculateAverageValueOfDiscordMessagesWithCoef_ChatModLevel_LowerValue() {
    double result = service
        .calculateAverageValueOfDiscordMessagesWithCoef(5.5, "ChatMod");

    assertEquals((5.5 * 0.17), result);
  }

  @Test
  void testCalculateAverageValueOfDiscordMessagesWithCoef_OverseerLevel_MaxValue() {
    double result = service
        .calculateAverageValueOfDiscordMessagesWithCoef(12, "Overseer");

    assertEquals((12 * 0.085), result);
  }

  @Test
  void testCalculateAverageValueOfDiscordMessagesWithCoef_OverseerLevel_LowerValue() {
    double result = service
        .calculateAverageValueOfDiscordMessagesWithCoef(10, "Overseer");

    assertEquals((10 * 0.085), result);
  }

  @Test
  void testCalculateAverageValueOfDiscordMessagesWithCoef_ManagerLevel_MaxValue() {
    double result = service
        .calculateAverageValueOfDiscordMessagesWithCoef(24, "Manager");

    assertEquals((24 * 0.042), result);
  }

  @Test
  void testCalculateAverageValueOfDiscordMessagesWithCoef_ManagerLevel_LowerValue() {
    double result = service
        .calculateAverageValueOfDiscordMessagesWithCoef(17, "Manager");

    assertEquals((17 * 0.042), result);
  }

  @Test
  void testCalculateAverageValueOfComparedDiscordMessagesWithCoef_HelperLevel_1() {
    double result = service
        .calculateAverageValueOfComparedDiscordMessagesWithCoef(0.1, "Helper");

    assertEquals((0.1 * 66), result);
  }

  @Test
  void testCalculateAverageValueOfComparedDiscordMessagesWithCoef_HelperLevel_2() {
    double result = service
        .calculateAverageValueOfComparedDiscordMessagesWithCoef(0.25, "Helper");

    assertEquals((0.25 * 66), result);
  }

  @Test
  void testCalculateAverageValueOfComparedDiscordMessagesWithCoef_SupportLevel_1() {
    double result = service
        .calculateAverageValueOfComparedDiscordMessagesWithCoef(0.1, "Support");

    assertEquals((0.1 * 33), result);
  }

  @Test
  void testCalculateAverageValueOfComparedDiscordMessagesWithCoef_SupportLevel_2() {
    double result = service
        .calculateAverageValueOfComparedDiscordMessagesWithCoef(0.25, "Support");

    assertEquals((0.25 * 33), result);
  }

  @Test
  void testCalculateAverageValueOfComparedDiscordMessagesWithCoef_ChatModLevel_1() {
    double result = service
        .calculateAverageValueOfComparedDiscordMessagesWithCoef(0.1, "ChatMod");

    assertEquals((0.1 * 17), result);
  }

  @Test
  void testCalculateAverageValueOfComparedDiscordMessagesWithCoef_ChatModLevel_2() {
    double result = service
        .calculateAverageValueOfComparedDiscordMessagesWithCoef(0.25, "ChatMod");

    assertEquals((0.25 * 17), result);
  }

  @Test
  void testCalculateAverageValueOfComparedDiscordMessagesWithCoef_OverseerLevel_1() {
    double result = service
        .calculateAverageValueOfComparedDiscordMessagesWithCoef(0.1, "Overseer");

    assertEquals((0.1 * 8.5), result);
  }

  @Test
  void testCalculateAverageValueOfComparedDiscordMessagesWithCoef_OverseerLevel_2() {
    double result = service
        .calculateAverageValueOfComparedDiscordMessagesWithCoef(0.25, "Overseer");

    assertEquals((0.25 * 8.5), result);
  }

  @Test
  void testCalculateAverageValueOfComparedDiscordMessagesWithCoef_ManagerLevel_1() {
    double result = service
        .calculateAverageValueOfComparedDiscordMessagesWithCoef(0.1, "Manager");

    assertEquals((0.1 * 4.2), result);
  }

  @Test
  void testCalculateAverageValueOfComparedDiscordMessagesWithCoef_ManagerLevel_2() {
    double result = service
        .calculateAverageValueOfComparedDiscordMessagesWithCoef(0.25, "Manager");

    assertEquals((0.25 * 4.2), result);
  }
}
