// package com.scoutress.KaimuxAdminStats.ServicesImpl;

// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.List;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertFalse;
// import static org.junit.jupiter.api.Assertions.assertTrue;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import static org.mockito.ArgumentMatchers.any;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import static org.mockito.Mockito.never;
// import static org.mockito.Mockito.verify;
// import org.mockito.MockitoAnnotations;

// import com.scoutress.KaimuxAdminStats.entity.playtime.SessionDataItem;
// import
// com.scoutress.KaimuxAdminStats.repositories.playtime.SanitazedDataRepository;
// import
// com.scoutress.KaimuxAdminStats.servicesImpl.playtime.DataSanitizationServiceImpl;

// class DataSanitizationServiceTest {

// @Mock
// private SanitazedDataRepository sanitazedDataRepository;

// @InjectMocks
// private DataSanitizationServiceImpl dataSanitizationService;

// private List<SessionDataItem> sessionDataItems;

// @BeforeEach
// void setUp() {
// MockitoAnnotations.openMocks(this);
// sessionDataItems = new ArrayList<>();
// }

// @Test
// void customTestWithManualSetUpInvocation() {
// setUp();
// }

// @Test
// void testSanitizeDataWithEmptyList() {
// dataSanitizationService.sanitizeData(sessionDataItems);
// verify(sanitazedDataRepository, never()).save(any());
// }

// @Test
// void testRemoveEarlyLogouts() {
// SessionDataItem login1 = new SessionDataItem(null, (short) 1, 1000L, true,
// "Server1");
// SessionDataItem logout1 = new SessionDataItem(null, (short) 1, 900L, false,
// "Server1");
// SessionDataItem logout2 = new SessionDataItem(null, (short) 1, 1100L, false,
// "Server1");
// SessionDataItem logout3 = new SessionDataItem(null, (short) 1, 950L, false,
// "Server1");

// List<SessionDataItem> loginSessions = List.of(login1);
// List<SessionDataItem> logoutSessions = List.of(logout1, logout2, logout3);

// List<SessionDataItem> result =
// dataSanitizationService.removeEarlyLogouts(loginSessions, logoutSessions);

// assertEquals(1, result.size());
// assertEquals(logout2, result.get(0));
// }

// @Test
// void testRemoveDuplicateLogouts() {
// SessionDataItem login1 = new SessionDataItem(null, (short) 1, 1000L, true,
// "Server1");
// SessionDataItem logout1 = new SessionDataItem(null, (short) 1, 1200L, false,
// "Server1");
// SessionDataItem logout2 = new SessionDataItem(null, (short) 1, 1400L, false,
// "Server1");
// SessionDataItem login2 = new SessionDataItem(null, (short) 1, 1600L, true,
// "Server1");
// SessionDataItem logout3 = new SessionDataItem(null, (short) 1, 1800L, false,
// "Server1");

// List<SessionDataItem> loginSessions = new ArrayList<>();
// loginSessions.add(login1);
// loginSessions.add(login2);

// List<SessionDataItem> logoutSessions = new ArrayList<>();
// logoutSessions.add(logout1);
// logoutSessions.add(logout2);
// logoutSessions.add(logout3);

// List<SessionDataItem> result =
// dataSanitizationService.removeDuplicateLogouts(loginSessions,
// logoutSessions);

// assertEquals(4, result.size());

// assertEquals(1000L, result.get(0).getTime(), "First should be login1");
// assertTrue(result.get(0).getActionAsBoolean(), "First should be a login");
// assertEquals(1400L, result.get(1).getTime(), "Second should be logout2");
// assertFalse(result.get(1).getActionAsBoolean(), "Second should be a logout");

// assertEquals(1600L, result.get(2).getTime(), "Third should be login2");
// assertTrue(result.get(2).getActionAsBoolean(), "Third should be a login");
// assertEquals(1800L, result.get(3).getTime(), "Fourth should be logout3");
// assertFalse(result.get(3).getActionAsBoolean(), "Fourth should be a logout");

// assertFalse(result.stream().anyMatch(item -> item.getTime() == 1200L),
// "Result should not contain logout1");
// }

// @Test
// void testRemoveDuplicateLogins() {
// SessionDataItem logout1 = new SessionDataItem(null, (short) 1, 1000L, false,
// "Server1");
// SessionDataItem logout2 = new SessionDataItem(null, (short) 1, 2000L, false,
// "Server1");
// SessionDataItem login1 = new SessionDataItem(null, (short) 1, 1200L, true,
// "Server1");
// SessionDataItem login2 = new SessionDataItem(null, (short) 1, 1400L, true,
// "Server1");
// SessionDataItem login3 = new SessionDataItem(null, (short) 1, 2200L, true,
// "Server1");

// List<SessionDataItem> sessions = new ArrayList<>(Arrays.asList(logout1,
// logout2, login1, login2, login3));

// List<SessionDataItem> result =
// dataSanitizationService.removeDuplicateLogins(sessions);

// assertEquals(4, result.size(), "Should contain 4 items: logout1, login1,
// logout2, login3");

// assertEquals(1000L, result.get(0).getTime(), "First should be logout1
// (1000L)");
// assertFalse(result.get(0).getActionAsBoolean(), "First should be a logout");

// assertEquals(1200L, result.get(1).getTime(), "Second should be login1
// (1200L)");
// assertTrue(result.get(1).getActionAsBoolean(), "Second should be a login");

// assertEquals(2000L, result.get(2).getTime(), "Third should be logout2
// (2000L)");
// assertFalse(result.get(2).getActionAsBoolean(), "Third should be a logout");

// assertEquals(2200L, result.get(3).getTime(), "Fourth should be login3
// (2200L)");
// assertTrue(result.get(3).getActionAsBoolean(), "Fourth should be a login");

// assertFalse(result.stream().anyMatch(item -> item.getTime() == 1400L),
// "Result should not contain login2");
// }

// @Test
// void testRemoveLateLogins() {
// SessionDataItem login1 = new SessionDataItem(null, (short) 1, 1000L, true,
// "Server1");
// SessionDataItem login2 = new SessionDataItem(null, (short) 1, 2000L, true,
// "Server1");
// SessionDataItem login3 = new SessionDataItem(null, (short) 1, 3000L, true,
// "Server1");
// SessionDataItem logout1 = new SessionDataItem(null, (short) 1, 2500L, false,
// "Server1");

// List<SessionDataItem> sessions = List.of(login1, login2, login3, logout1);

// List<SessionDataItem> result =
// dataSanitizationService.removeLateLogins(sessions);

// assertEquals(2, result.size());
// assertEquals(login1, result.get(0));
// assertEquals(login2, result.get(1));
// }
// }
