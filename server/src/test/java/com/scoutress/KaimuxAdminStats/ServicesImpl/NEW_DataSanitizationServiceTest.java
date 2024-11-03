package com.scoutress.KaimuxAdminStats.ServicesImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import org.mockito.MockitoAnnotations;

import com.scoutress.KaimuxAdminStats.Entity.playtime.NEW_SessionDataItem;
import com.scoutress.KaimuxAdminStats.Repositories.playtime.NEW_SanitazedDataRepository;
import com.scoutress.KaimuxAdminStats.Services.playtime.NEW_DataSanitizationService;

class NEW_DataSanitizationServiceTest {

  @Mock
  private NEW_SanitazedDataRepository sanitazedDataRepository;

  @InjectMocks
  private NEW_DataSanitizationService dataSanitizationService;

  private List<NEW_SessionDataItem> sessionDataItems;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    sessionDataItems = new ArrayList<>();
  }

  @Test
  void customTestWithManualSetUpInvocation() {
    setUp();
  }

  @Test
  void testSanitizeDataWithEmptyList() {
    dataSanitizationService.sanitizeData(sessionDataItems);
    verify(sanitazedDataRepository, never()).save(any());
  }

  @Test
  void testRemoveEarlyLogouts() {
    NEW_SessionDataItem login1 = new NEW_SessionDataItem(null, (short) 1, 1000L, true, "Server1");
    NEW_SessionDataItem logout1 = new NEW_SessionDataItem(null, (short) 1, 900L, false, "Server1");
    NEW_SessionDataItem logout2 = new NEW_SessionDataItem(null, (short) 1, 1100L, false, "Server1");
    NEW_SessionDataItem logout3 = new NEW_SessionDataItem(null, (short) 1, 950L, false, "Server1");

    List<NEW_SessionDataItem> loginSessions = List.of(login1);
    List<NEW_SessionDataItem> logoutSessions = List.of(logout1, logout2, logout3);

    List<NEW_SessionDataItem> result = dataSanitizationService.removeEarlyLogouts(loginSessions, logoutSessions);

    assertEquals(1, result.size());
    assertEquals(logout2, result.get(0));
  }

  @Test
  void testRemoveDuplicateLogouts() {
    NEW_SessionDataItem login1 = new NEW_SessionDataItem(null, (short) 1, 1000L, true, "Server1");
    NEW_SessionDataItem logout1 = new NEW_SessionDataItem(null, (short) 1, 1200L, false, "Server1");
    NEW_SessionDataItem logout2 = new NEW_SessionDataItem(null, (short) 1, 1400L, false, "Server1");
    NEW_SessionDataItem login2 = new NEW_SessionDataItem(null, (short) 1, 1600L, true, "Server1");
    NEW_SessionDataItem logout3 = new NEW_SessionDataItem(null, (short) 1, 1800L, false, "Server1");

    List<NEW_SessionDataItem> loginSessions = new ArrayList<>();
    loginSessions.add(login1);
    loginSessions.add(login2);

    List<NEW_SessionDataItem> logoutSessions = new ArrayList<>();
    logoutSessions.add(logout1);
    logoutSessions.add(logout2);
    logoutSessions.add(logout3);

    List<NEW_SessionDataItem> result = dataSanitizationService.removeDuplicateLogouts(loginSessions, logoutSessions);

    assertEquals(4, result.size());

    assertEquals(1000L, result.get(0).getTime(), "First should be login1");
    assertTrue(result.get(0).getActionAsBoolean(), "First should be a login");
    assertEquals(1400L, result.get(1).getTime(), "Second should be logout2");
    assertFalse(result.get(1).getActionAsBoolean(), "Second should be a logout");

    assertEquals(1600L, result.get(2).getTime(), "Third should be login2");
    assertTrue(result.get(2).getActionAsBoolean(), "Third should be a login");
    assertEquals(1800L, result.get(3).getTime(), "Fourth should be logout3");
    assertFalse(result.get(3).getActionAsBoolean(), "Fourth should be a logout");

    assertFalse(result.stream().anyMatch(item -> item.getTime() == 1200L),
        "Result should not contain logout1");
  }

  @Test
  void testRemoveDuplicateLogins() {
    NEW_SessionDataItem logout1 = new NEW_SessionDataItem(null, (short) 1, 1000L, false, "Server1");
    NEW_SessionDataItem logout2 = new NEW_SessionDataItem(null, (short) 1, 2000L, false, "Server1");
    NEW_SessionDataItem login1 = new NEW_SessionDataItem(null, (short) 1, 1200L, true, "Server1");
    NEW_SessionDataItem login2 = new NEW_SessionDataItem(null, (short) 1, 1400L, true, "Server1");
    NEW_SessionDataItem login3 = new NEW_SessionDataItem(null, (short) 1, 2200L, true, "Server1");

    List<NEW_SessionDataItem> sessions = new ArrayList<>(Arrays.asList(logout1, logout2, login1, login2, login3));

    List<NEW_SessionDataItem> result = dataSanitizationService.removeDuplicateLogins(sessions);

    assertEquals(4, result.size(), "Should contain 4 items: logout1, login1, logout2, login3");

    assertEquals(1000L, result.get(0).getTime(), "First should be logout1 (1000L)");
    assertFalse(result.get(0).getActionAsBoolean(), "First should be a logout");

    assertEquals(1200L, result.get(1).getTime(), "Second should be login1 (1200L)");
    assertTrue(result.get(1).getActionAsBoolean(), "Second should be a login");

    assertEquals(2000L, result.get(2).getTime(), "Third should be logout2 (2000L)");
    assertFalse(result.get(2).getActionAsBoolean(), "Third should be a logout");

    assertEquals(2200L, result.get(3).getTime(), "Fourth should be login3 (2200L)");
    assertTrue(result.get(3).getActionAsBoolean(), "Fourth should be a login");

    assertFalse(result.stream().anyMatch(item -> item.getTime() == 1400L),
        "Result should not contain login2");
  }

  @Test
  void testRemoveLateLogins() {
    NEW_SessionDataItem login1 = new NEW_SessionDataItem(null, (short) 1, 1000L, true, "Server1");
    NEW_SessionDataItem login2 = new NEW_SessionDataItem(null, (short) 1, 2000L, true, "Server1");
    NEW_SessionDataItem login3 = new NEW_SessionDataItem(null, (short) 1, 3000L, true, "Server1");
    NEW_SessionDataItem logout1 = new NEW_SessionDataItem(null, (short) 1, 2500L, false, "Server1");

    List<NEW_SessionDataItem> sessions = List.of(login1, login2, login3, logout1);

    List<NEW_SessionDataItem> result = dataSanitizationService.removeLateLogins(sessions);

    assertEquals(2, result.size());
    assertEquals(login1, result.get(0));
    assertEquals(login2, result.get(1));
  }
}
