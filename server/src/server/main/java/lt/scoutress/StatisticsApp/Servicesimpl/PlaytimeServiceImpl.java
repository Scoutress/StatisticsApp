package lt.scoutress.StatisticsApp.Servicesimpl;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lt.scoutress.StatisticsApp.Repositories.DailyPlaytimeRepository;
import lt.scoutress.StatisticsApp.Repositories.PlaytimeRepository;
import lt.scoutress.StatisticsApp.Services.playtime.PlaytimeService;
import lt.scoutress.StatisticsApp.entity.Playtime.DailyPlaytime;
import lt.scoutress.StatisticsApp.entity.Playtime.Playtime;

@Service
public class PlaytimeServiceImpl implements PlaytimeService {

    @Autowired
    private EntityManager entityManager;

    private final PlaytimeRepository playtimeRepository;
    private final DailyPlaytimeRepository dailyPlaytimeRepository;

    public PlaytimeServiceImpl(PlaytimeRepository playtimeRepository, DailyPlaytimeRepository dailyPlaytimeRepository) {
        this.playtimeRepository = playtimeRepository;
        this.dailyPlaytimeRepository = dailyPlaytimeRepository;
    }

    @Override
    public List<Playtime> findAll() {
        return playtimeRepository.findAll();
    }

    // Migrating filtered data

    // Survival
    @Override
    @Transactional
    public void migrateSurvivalPlaytimeData() {
        Query query = entityManager.createQuery("SELECT p.username, p.survival FROM PlaytimeDBCodes p");

        @SuppressWarnings("unchecked")
        List<Object[]> usernamesAndSurvivalCodes = query.getResultList();

        for (Object[] usernameAndSurvivalCode : usernamesAndSurvivalCodes) {
            String username = (String) usernameAndSurvivalCode[0];
            String survivalCode = (String) usernameAndSurvivalCode[1];

            if (survivalCode == null || survivalCode.isEmpty()) {
                continue;
            }

            Query timeQuery = entityManager.createQuery(
                    "SELECT c.time, c.action FROM Survival c WHERE c.user = :survivalCode AND c.action IN (0, 1)");
            timeQuery.setParameter("survivalCode", survivalCode);

            @SuppressWarnings("unchecked")
            List<Object[]> timeAndActionValues = timeQuery.getResultList();

            createAndSaveSurvivalPlaytimeDataTable(username);

            // DEBUG
            int lastDisconnectTimeSurvival = getLastDisconnectTimeSurvival(username);
            int newConnectRowsCountSurvival = 0;
            int newDisconnectRowsCountSurvival = 0;

            for (Object[] timeAndAction : timeAndActionValues) {
                Integer time = (Integer) timeAndAction[0];
                Integer action = (Integer) timeAndAction[1];

                if (time != null && action != null) {
                    boolean exists = checkIfRecordExistsSurvival(username, time);

                    if (!exists) {
                        if (action == 1 && time > lastDisconnectTimeSurvival) {
                            saveTimeToSurvivalConnect(username, time);
                            newConnectRowsCountSurvival++;
                        } else if (action == 0 && time > lastDisconnectTimeSurvival) {
                            saveTimeToSurvivalDisconnect(username, time);
                            newDisconnectRowsCountSurvival++;
                        }
                    }
                }
            }

            System.out.println(
                    "Added " + username + " >>> " + newConnectRowsCountSurvival + "/" + newDisconnectRowsCountSurvival);
            if (newConnectRowsCountSurvival != newDisconnectRowsCountSurvival) {
                System.out.println("<> Error " + username);
            }
        }
    }

    private boolean checkIfRecordExistsSurvival(String username, int time) {
        String tableName = "pt_data_survival_" + username;
        String checkRecordQuery = "SELECT COUNT(*) FROM " + tableName + " WHERE connect = :time OR disconnect = :time";
        Long count = (Long) entityManager.createNativeQuery(checkRecordQuery)
                .setParameter("time", time)
                .getSingleResult();
        return count != null && count.intValue() > 0;
    }

    private void createAndSaveSurvivalPlaytimeDataTable(String username) {
        String tableName = "pt_data_survival_" + username;
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName
                + " (id INT AUTO_INCREMENT PRIMARY KEY, connect INT, disconnect INT)";
        entityManager.createNativeQuery(createTableQuery).executeUpdate();
    }

    private void saveTimeToSurvivalConnect(String username, int time) {
        String tableName = "pt_data_survival_" + username;
        String insertQuery = "INSERT INTO " + tableName + " (connect) VALUES (:time)";
        entityManager.createNativeQuery(insertQuery).setParameter("time", time).executeUpdate();
    }

    private void saveTimeToSurvivalDisconnect(String username, int time) {
        String tableName = "pt_data_survival_" + username;
        String updateQuery = "UPDATE " + tableName + " SET disconnect = :time WHERE disconnect IS NULL";
        entityManager.createNativeQuery(updateQuery).setParameter("time", time).executeUpdate();
    }

    private int getLastDisconnectTimeSurvival(String username) {
        String tableName = "pt_data_survival_" + username;
        String getLastDisconnectTimeQuery = "SELECT MAX(disconnect) FROM " + tableName;
        Integer lastDisconnectTime = (Integer) entityManager.createNativeQuery(getLastDisconnectTimeQuery)
                .getSingleResult();
        return lastDisconnectTime != null ? lastDisconnectTime : 0;
    }

    // Skyblock
    @Override
    @Transactional
    public void migrateSkyblockPlaytimeData() {
        Query query = entityManager.createQuery("SELECT p.username, p.skyblock FROM PlaytimeDBCodes p");

        @SuppressWarnings("unchecked")
        List<Object[]> usernamesAndSkyblockCodes = query.getResultList();

        for (Object[] usernameAndSkyblockCode : usernamesAndSkyblockCodes) {
            String username = (String) usernameAndSkyblockCode[0];
            String skyblockCode = (String) usernameAndSkyblockCode[1];

            if (skyblockCode == null || skyblockCode.isEmpty()) {
                continue;
            }

            Query timeQuery = entityManager.createQuery(
                    "SELECT c.time, c.action FROM Skyblock c WHERE c.user = :skyblockCode AND c.action IN (0, 1)");
            timeQuery.setParameter("skyblockCode", skyblockCode);

            @SuppressWarnings("unchecked")
            List<Object[]> timeAndActionValues = timeQuery.getResultList();

            createAndSaveSkyblockPlaytimeDataTable(username);

            int lastDisconnectTimeSkyblock = getLastDisconnectTimeSkyblock(username);
            int newConnectRowsCountSkyblock = 0;
            int newDisconnectRowsCountSkyblock = 0;

            for (Object[] timeAndAction : timeAndActionValues) {
                Integer time = (Integer) timeAndAction[0];
                Integer action = (Integer) timeAndAction[1];

                if (time != null && action != null) {
                    boolean exists = checkIfRecordExistsSkyblock(username, time);

                    if (!exists) {
                        if (action == 1 && time > lastDisconnectTimeSkyblock) {
                            saveTimeToSkyblockConnect(username, time);
                            newConnectRowsCountSkyblock++;
                        } else if (action == 0 && time > lastDisconnectTimeSkyblock) {
                            saveTimeToSkyblockDisconnect(username, time);
                            newDisconnectRowsCountSkyblock++;
                        }
                    }
                }
            }

            System.out.println(
                    "Added " + username + " >>> " + newConnectRowsCountSkyblock + "/" + newDisconnectRowsCountSkyblock);
            if (newConnectRowsCountSkyblock != newDisconnectRowsCountSkyblock) {
                System.out.println("<> Error " + username);
            }
        }
    }

    private boolean checkIfRecordExistsSkyblock(String username, int time) {
        String tableName = "pt_data_skyblock_" + username;
        String checkRecordQuery = "SELECT COUNT(*) FROM " + tableName + " WHERE connect = :time OR disconnect = :time";
        Long count = (Long) entityManager.createNativeQuery(checkRecordQuery)
                .setParameter("time", time)
                .getSingleResult();
        return count != null && count.intValue() > 0;
    }

    private void createAndSaveSkyblockPlaytimeDataTable(String username) {
        String tableName = "pt_data_skyblock_" + username;
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName
                + " (id INT AUTO_INCREMENT PRIMARY KEY, connect INT, disconnect INT)";
        entityManager.createNativeQuery(createTableQuery).executeUpdate();
    }

    private void saveTimeToSkyblockConnect(String username, int time) {
        String tableName = "pt_data_skyblock_" + username;
        String insertQuery = "INSERT INTO " + tableName + " (connect) VALUES (:time)";
        entityManager.createNativeQuery(insertQuery).setParameter("time", time).executeUpdate();
    }

    private void saveTimeToSkyblockDisconnect(String username, int time) {
        String tableName = "pt_data_skyblock_" + username;
        String updateQuery = "UPDATE " + tableName + " SET disconnect = :time WHERE disconnect IS NULL";
        entityManager.createNativeQuery(updateQuery).setParameter("time", time).executeUpdate();
    }

    private int getLastDisconnectTimeSkyblock(String username) {
        String tableName = "pt_data_skyblock_" + username;
        String getLastDisconnectTimeQuery = "SELECT MAX(disconnect) FROM " + tableName;
        Integer lastDisconnectTime = (Integer) entityManager.createNativeQuery(getLastDisconnectTimeQuery)
                .getSingleResult();
        return lastDisconnectTime != null ? lastDisconnectTime : 0;
    }

    // Creative
    @Override
    @Transactional
    public void migrateCreativePlaytimeData() {
        Query query = entityManager.createQuery("SELECT p.username, p.creative FROM PlaytimeDBCodes p");

        @SuppressWarnings("unchecked")
        List<Object[]> usernamesAndCreativeCodes = query.getResultList();

        for (Object[] usernameAndCreativeCode : usernamesAndCreativeCodes) {
            String username = (String) usernameAndCreativeCode[0];
            String creativeCode = (String) usernameAndCreativeCode[1];

            if (creativeCode == null || creativeCode.isEmpty()) {
                continue;
            }

            Query timeQuery = entityManager.createQuery(
                    "SELECT c.time, c.action FROM Creative c WHERE c.user = :creativeCode AND c.action IN (0, 1)");
            timeQuery.setParameter("creativeCode", creativeCode);

            @SuppressWarnings("unchecked")
            List<Object[]> timeAndActionValues = timeQuery.getResultList();

            createAndSaveCreativePlaytimeDataTable(username);

            // DEBUG
            int lastDisconnectTimeCreative = getLastDisconnectTimeCreative(username);
            int newConnectRowsCountCreative = 0;
            int newDisconnectRowsCountCreative = 0;

            for (Object[] timeAndAction : timeAndActionValues) {
                Integer time = (Integer) timeAndAction[0];
                Integer action = (Integer) timeAndAction[1];

                if (time != null && action != null) {
                    boolean exists = checkIfRecordExistsCreative(username, time);

                    if (!exists) {
                        if (action == 1 && time > lastDisconnectTimeCreative) {
                            saveTimeToCreativeConnect(username, time);
                            newConnectRowsCountCreative++;
                        } else if (action == 0 && time > lastDisconnectTimeCreative) {
                            saveTimeToCreativeDisconnect(username, time);
                            newDisconnectRowsCountCreative++;
                        }
                    }
                }
            }

            System.out.println(
                    "Added " + username + " >>> " + newConnectRowsCountCreative + "/" + newDisconnectRowsCountCreative);
            if (newConnectRowsCountCreative != newDisconnectRowsCountCreative) {
                System.out.println("<> Error " + username);
            }
        }
    }

    private boolean checkIfRecordExistsCreative(String username, int time) {
        String tableName = "pt_data_creative_" + username;
        String checkRecordQuery = "SELECT COUNT(*) FROM " + tableName + " WHERE connect = :time OR disconnect = :time";
        Long count = (Long) entityManager.createNativeQuery(checkRecordQuery)
                .setParameter("time", time)
                .getSingleResult();
        return count != null && count.intValue() > 0;
    }

    private void createAndSaveCreativePlaytimeDataTable(String username) {
        String tableName = "pt_data_creative_" + username;
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName
                + " (id INT AUTO_INCREMENT PRIMARY KEY, connect INT, disconnect INT)";
        entityManager.createNativeQuery(createTableQuery).executeUpdate();
    }

    private void saveTimeToCreativeConnect(String username, int time) {
        String tableName = "pt_data_creative_" + username;
        String insertQuery = "INSERT INTO " + tableName + " (connect) VALUES (:time)";
        entityManager.createNativeQuery(insertQuery).setParameter("time", time).executeUpdate();
    }

    private void saveTimeToCreativeDisconnect(String username, int time) {
        String tableName = "pt_data_creative_" + username;
        String updateQuery = "UPDATE " + tableName + " SET disconnect = :time WHERE disconnect IS NULL";
        entityManager.createNativeQuery(updateQuery).setParameter("time", time).executeUpdate();
    }

    private int getLastDisconnectTimeCreative(String username) {
        String tableName = "pt_data_creative_" + username;
        String getLastDisconnectTimeQuery = "SELECT MAX(disconnect) FROM " + tableName;
        Integer lastDisconnectTime = (Integer) entityManager.createNativeQuery(getLastDisconnectTimeQuery)
                .getSingleResult();
        return lastDisconnectTime != null ? lastDisconnectTime : 0;
    }

    // Boxpvp
    @Override
    @Transactional
    public void migrateBoxpvpPlaytimeData() {
        Query query = entityManager.createQuery("SELECT p.username, p.boxpvp FROM PlaytimeDBCodes p");

        @SuppressWarnings("unchecked")
        List<Object[]> usernamesAndBoxpvpCodes = query.getResultList();

        for (Object[] usernameAndBoxpvpCode : usernamesAndBoxpvpCodes) {
            String username = (String) usernameAndBoxpvpCode[0];
            String boxpvpCode = (String) usernameAndBoxpvpCode[1];

            if (boxpvpCode == null || boxpvpCode.isEmpty()) {
                continue;
            }

            Query timeQuery = entityManager.createQuery(
                    "SELECT c.time, c.action FROM Boxpvp c WHERE c.user = :boxpvpCode AND c.action IN (0, 1)");
            timeQuery.setParameter("boxpvpCode", boxpvpCode);

            @SuppressWarnings("unchecked")
            List<Object[]> timeAndActionValues = timeQuery.getResultList();

            createAndSaveBoxpvpPlaytimeDataTable(username);

            // DEBUG
            int lastDisconnectTimeBoxpvp = getLastDisconnectTimeBoxpvp(username);
            int newConnectRowsCountBoxpvp = 0;
            int newDisconnectRowsCountBoxpvp = 0;

            for (Object[] timeAndAction : timeAndActionValues) {
                Integer time = (Integer) timeAndAction[0];
                Integer action = (Integer) timeAndAction[1];

                if (time != null && action != null) {
                    boolean exists = checkIfRecordExistsBoxpvp(username, time);

                    if (!exists) {
                        if (action == 1 && time > lastDisconnectTimeBoxpvp) {
                            saveTimeToBoxpvpConnect(username, time);
                            newConnectRowsCountBoxpvp++;
                        } else if (action == 0 && time > lastDisconnectTimeBoxpvp) {
                            saveTimeToBoxpvpDisconnect(username, time);
                            newDisconnectRowsCountBoxpvp++;
                        }
                    }
                }
            }

            System.out.println(
                    "Added " + username + " >>> " + newConnectRowsCountBoxpvp + "/" + newDisconnectRowsCountBoxpvp);
            if (newConnectRowsCountBoxpvp != newDisconnectRowsCountBoxpvp) {
                System.out.println("<> Error " + username);
            }
        }
    }

    private boolean checkIfRecordExistsBoxpvp(String username, int time) {
        String tableName = "pt_data_boxpvp_" + username;
        String checkRecordQuery = "SELECT COUNT(*) FROM " + tableName + " WHERE connect = :time OR disconnect = :time";
        Long count = (Long) entityManager.createNativeQuery(checkRecordQuery)
                .setParameter("time", time)
                .getSingleResult();
        return count != null && count.intValue() > 0;
    }

    private void createAndSaveBoxpvpPlaytimeDataTable(String username) {
        String tableName = "pt_data_boxpvp_" + username;
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName
                + " (id INT AUTO_INCREMENT PRIMARY KEY, connect INT, disconnect INT)";
        entityManager.createNativeQuery(createTableQuery).executeUpdate();
    }

    private void saveTimeToBoxpvpConnect(String username, int time) {
        String tableName = "pt_data_boxpvp_" + username;
        String insertQuery = "INSERT INTO " + tableName + " (connect) VALUES (:time)";
        entityManager.createNativeQuery(insertQuery).setParameter("time", time).executeUpdate();
    }

    private void saveTimeToBoxpvpDisconnect(String username, int time) {
        String tableName = "pt_data_boxpvp_" + username;
        String updateQuery = "UPDATE " + tableName + " SET disconnect = :time WHERE disconnect IS NULL";
        entityManager.createNativeQuery(updateQuery).setParameter("time", time).executeUpdate();
    }

    private int getLastDisconnectTimeBoxpvp(String username) {
        String tableName = "pt_data_boxpvp_" + username;
        String getLastDisconnectTimeQuery = "SELECT MAX(disconnect) FROM " + tableName;
        Integer lastDisconnectTime = (Integer) entityManager.createNativeQuery(getLastDisconnectTimeQuery)
                .getSingleResult();
        return lastDisconnectTime != null ? lastDisconnectTime : 0;
    }

    // Prison
    @Override
    @Transactional
    public void migratePrisonPlaytimeData() {
        Query query = entityManager.createQuery("SELECT p.username, p.prison FROM PlaytimeDBCodes p");

        @SuppressWarnings("unchecked")
        List<Object[]> usernamesAndPrisonCodes = query.getResultList();

        for (Object[] usernameAndPrisonCode : usernamesAndPrisonCodes) {
            String username = (String) usernameAndPrisonCode[0];
            String prisonCode = (String) usernameAndPrisonCode[1];

            if (prisonCode == null || prisonCode.isEmpty()) {
                continue;
            }

            Query timeQuery = entityManager.createQuery(
                    "SELECT c.time, c.action FROM Prison c WHERE c.user = :prisonCode AND c.action IN (0, 1)");
            timeQuery.setParameter("prisonCode", prisonCode);

            @SuppressWarnings("unchecked")
            List<Object[]> timeAndActionValues = timeQuery.getResultList();

            createAndSavePrisonPlaytimeDataTable(username);

            // DEBUG
            int lastDisconnectTimePrison = getLastDisconnectTimePrison(username);
            int newConnectRowsCountPrison = 0;
            int newDisconnectRowsCountPrison = 0;

            for (Object[] timeAndAction : timeAndActionValues) {
                Integer time = (Integer) timeAndAction[0];
                Integer action = (Integer) timeAndAction[1];

                if (time != null && action != null) {
                    boolean exists = checkIfRecordExistsPrison(username, time);

                    if (!exists) {
                        if (action == 1 && time > lastDisconnectTimePrison) {
                            saveTimeToPrisonConnect(username, time);
                            newConnectRowsCountPrison++;
                        } else if (action == 0 && time > lastDisconnectTimePrison) {
                            saveTimeToPrisonDisconnect(username, time);
                            newDisconnectRowsCountPrison++;
                        }
                    }
                }
            }

            System.out.println(
                    "Added " + username + " >>> " + newConnectRowsCountPrison + "/" + newDisconnectRowsCountPrison);
            if (newConnectRowsCountPrison != newDisconnectRowsCountPrison) {
                System.out.println("<> Error " + username);
            }
        }
    }

    private boolean checkIfRecordExistsPrison(String username, int time) {
        String tableName = "pt_data_prison_" + username;
        String checkRecordQuery = "SELECT COUNT(*) FROM " + tableName + " WHERE connect = :time OR disconnect = :time";
        Long count = (Long) entityManager.createNativeQuery(checkRecordQuery)
                .setParameter("time", time)
                .getSingleResult();
        return count != null && count.intValue() > 0;
    }

    private void createAndSavePrisonPlaytimeDataTable(String username) {
        String tableName = "pt_data_prison_" + username;
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName
                + " (id INT AUTO_INCREMENT PRIMARY KEY, connect INT, disconnect INT)";
        entityManager.createNativeQuery(createTableQuery).executeUpdate();
    }

    private void saveTimeToPrisonConnect(String username, int time) {
        String tableName = "pt_data_prison_" + username;
        String insertQuery = "INSERT INTO " + tableName + " (connect) VALUES (:time)";
        entityManager.createNativeQuery(insertQuery).setParameter("time", time).executeUpdate();
    }

    private void saveTimeToPrisonDisconnect(String username, int time) {
        String tableName = "pt_data_prison_" + username;
        String updateQuery = "UPDATE " + tableName + " SET disconnect = :time WHERE disconnect IS NULL";
        entityManager.createNativeQuery(updateQuery).setParameter("time", time).executeUpdate();
    }

    private int getLastDisconnectTimePrison(String username) {
        String tableName = "pt_data_prison_" + username;
        String getLastDisconnectTimeQuery = "SELECT MAX(disconnect) FROM " + tableName;
        Integer lastDisconnectTime = (Integer) entityManager.createNativeQuery(getLastDisconnectTimeQuery)
                .getSingleResult();
        return lastDisconnectTime != null ? lastDisconnectTime : 0;
    }

    // Events
    @Override
    @Transactional
    public void migrateEventsPlaytimeData() {
        Query query = entityManager.createQuery("SELECT p.username, p.events FROM PlaytimeDBCodes p");

        @SuppressWarnings("unchecked")
        List<Object[]> usernamesAndEventsCodes = query.getResultList();

        for (Object[] usernameAndEventsCode : usernamesAndEventsCodes) {
            String username = (String) usernameAndEventsCode[0];
            String eventsCode = (String) usernameAndEventsCode[1];

            if (eventsCode == null || eventsCode.isEmpty()) {
                continue;
            }

            Query timeQuery = entityManager.createQuery(
                    "SELECT c.time, c.action FROM Events c WHERE c.user = :eventsCode AND c.action IN (0, 1)");
            timeQuery.setParameter("eventsCode", eventsCode);

            @SuppressWarnings("unchecked")
            List<Object[]> timeAndActionValues = timeQuery.getResultList();

            createAndSaveEventsPlaytimeDataTable(username);

            // DEBUG
            int lastDisconnectTimeEvents = getLastDisconnectTimeEvents(username);
            int newConnectRowsCountEvents = 0;
            int newDisconnectRowsCountEvents = 0;

            for (Object[] timeAndAction : timeAndActionValues) {
                Integer time = (Integer) timeAndAction[0];
                Integer action = (Integer) timeAndAction[1];

                if (time != null && action != null) {
                    boolean exists = checkIfRecordExistsEvents(username, time);

                    if (!exists) {
                        if (action == 1 && time > lastDisconnectTimeEvents) {
                            saveTimeToEventsConnect(username, time);
                            newConnectRowsCountEvents++;
                        } else if (action == 0 && time > lastDisconnectTimeEvents) {
                            saveTimeToEventsDisconnect(username, time);
                            newDisconnectRowsCountEvents++;
                        }
                    }
                }
            }

            System.out.println(
                    "Added " + username + " >>> " + newConnectRowsCountEvents + "/" + newDisconnectRowsCountEvents);
            if (newConnectRowsCountEvents != newDisconnectRowsCountEvents) {
                System.out.println("<> Error " + username);
            }
        }
    }

    private boolean checkIfRecordExistsEvents(String username, int time) {
        String tableName = "pt_data_events_" + username;
        String checkRecordQuery = "SELECT COUNT(*) FROM " + tableName + " WHERE connect = :time OR disconnect = :time";
        Long count = (Long) entityManager.createNativeQuery(checkRecordQuery)
                .setParameter("time", time)
                .getSingleResult();
        return count != null && count.intValue() > 0;
    }

    private void createAndSaveEventsPlaytimeDataTable(String username) {
        String tableName = "pt_data_events_" + username;
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName
                + " (id INT AUTO_INCREMENT PRIMARY KEY, connect INT, disconnect INT)";
        entityManager.createNativeQuery(createTableQuery).executeUpdate();
    }

    private void saveTimeToEventsConnect(String username, int time) {
        String tableName = "pt_data_events_" + username;
        String insertQuery = "INSERT INTO " + tableName + " (connect) VALUES (:time)";
        entityManager.createNativeQuery(insertQuery).setParameter("time", time).executeUpdate();
    }

    private void saveTimeToEventsDisconnect(String username, int time) {
        String tableName = "pt_data_events_" + username;
        String updateQuery = "UPDATE " + tableName + " SET disconnect = :time WHERE disconnect IS NULL";
        entityManager.createNativeQuery(updateQuery).setParameter("time", time).executeUpdate();
    }

    private int getLastDisconnectTimeEvents(String username) {
        String tableName = "pt_data_events_" + username;
        String getLastDisconnectTimeQuery = "SELECT MAX(disconnect) FROM " + tableName;
        Integer lastDisconnectTime = (Integer) entityManager.createNativeQuery(getLastDisconnectTimeQuery)
                .getSingleResult();
        return lastDisconnectTime != null ? lastDisconnectTime : 0;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    // Timestamp to Date convertion

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public void convertTimestampToDate() {
        List<List<String>> allTablesNamesLists = new ArrayList<>();

        allTablesNamesLists.add(entityManager
                .createNativeQuery(
                        "SELECT table_name FROM information_schema.tables WHERE table_name LIKE 'pt_data_survival_%'")
                .getResultList());
        allTablesNamesLists.add(entityManager
                .createNativeQuery(
                        "SELECT table_name FROM information_schema.tables WHERE table_name LIKE 'pt_data_skyblock_%'")
                .getResultList());
        allTablesNamesLists.add(entityManager
                .createNativeQuery(
                        "SELECT table_name FROM information_schema.tables WHERE table_name LIKE 'pt_data_creative_%'")
                .getResultList());
        allTablesNamesLists.add(entityManager
                .createNativeQuery(
                        "SELECT table_name FROM information_schema.tables WHERE table_name LIKE 'pt_data_boxpvp_%'")
                .getResultList());
        allTablesNamesLists.add(entityManager
                .createNativeQuery(
                        "SELECT table_name FROM information_schema.tables WHERE table_name LIKE 'pt_data_prison_%'")
                .getResultList());
        allTablesNamesLists.add(entityManager
                .createNativeQuery(
                        "SELECT table_name FROM information_schema.tables WHERE table_name LIKE 'pt_data_events_%'")
                .getResultList());

        List<String> allTablesNames = new ArrayList<>();
        for (List<String> tablesNames : allTablesNamesLists) {
            allTablesNames.addAll(tablesNames);
        }

        for (String tableName : allTablesNames) {
            Query query = entityManager.createNativeQuery("SELECT connect, disconnect FROM " + tableName);

            List<Object[]> results = query.getResultList();

            for (Object[] result : results) {
                Integer connectSeconds = (Integer) result[0];
                Integer disconnectSeconds = (Integer) result[1];

                LocalDateTime connectDateTime = Instant.ofEpochSecond(connectSeconds).atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
                LocalDateTime disconnectDateTime = Instant.ofEpochSecond(disconnectSeconds)
                        .atZone(ZoneId.systemDefault()).toLocalDateTime();

                createTableIfNotExists(tableName);
                updateTable(connectDateTime, disconnectDateTime, connectSeconds, disconnectSeconds, tableName);
            }
        }
    }

    private void createTableIfNotExists(String tableName) {
        String partialTableName = tableName.substring("pt_data_".length());
        String newTableName = "pt_calc_" + partialTableName;

        boolean tableExists = entityManager.createNativeQuery(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'kaimuxstatistics' AND table_name = '"
                        + newTableName + "'")
                .getSingleResult()
                .equals(1L);

        if (!tableExists) {
            String createTableQuery = "CREATE TABLE " + newTableName + " (" +
                    "connect_year INT, " +
                    "connect_month INT, " +
                    "connect_day INT, " +
                    "connect_hour INT, " +
                    "connect_min INT, " +
                    "connect_sec INT, " +
                    "connect_datetime DATETIME, " +
                    "connect_weeknum INT, " +
                    "disconnect_year INT, " +
                    "disconnect_month INT, " +
                    "disconnect_day INT, " +
                    "disconnect_hour INT, " +
                    "disconnect_min INT, " +
                    "disconnect_sec INT, " +
                    "disconnect_datetime DATETIME, " +
                    "is_passed_midnight BOOLEAN, " +
                    "midnight_epoch INT, " +
                    "playtime DOUBLE, " +
                    "playtime_before_midnight INT, " +
                    "playtime_after_midnight INT, " +
                    "all_playtime INT)";

            entityManager.createNativeQuery(createTableQuery).executeUpdate();
        }
    }

    private void updateTable(LocalDateTime connectDateTime, LocalDateTime disconnectDateTime, Integer connectSeconds,
            Integer disconnectSeconds, String tableName) {
        int connectYear = connectDateTime.getYear();
        int connectMonth = connectDateTime.getMonthValue();
        int connectDay = connectDateTime.getDayOfMonth();
        int connectHour = connectDateTime.getHour();
        int connectMin = connectDateTime.getMinute();
        int connectSec = connectDateTime.getSecond();
        int connectWeekNum = connectDateTime.get(WeekFields.ISO.weekOfWeekBasedYear());

        int disconnectYear = disconnectDateTime.getYear();
        int disconnectMonth = disconnectDateTime.getMonthValue();
        int disconnectDay = disconnectDateTime.getDayOfMonth();
        int disconnectHour = disconnectDateTime.getHour();
        int disconnectMin = disconnectDateTime.getMinute();
        int disconnectSec = disconnectDateTime.getSecond();

        LocalDateTime midnightEpochDate = connectDateTime.toLocalDate().plusDays(1).atStartOfDay();
        int midnightEpoch = (int) midnightEpochDate.toEpochSecond(java.time.OffsetDateTime.now().getOffset());

        double playtime = ((double) (disconnectSeconds - connectSeconds)) / 3600.0;

        boolean isPassedMidnight;
        int playtimeBeforeMidnight;
        int playtimeAfterMidnight;
        int allPlaytime;

        if (midnightEpoch > connectSeconds && midnightEpoch < disconnectSeconds) {
            isPassedMidnight = true;
            playtimeBeforeMidnight = midnightEpoch - connectSeconds;
            playtimeAfterMidnight = disconnectSeconds - midnightEpoch;
            allPlaytime = playtimeBeforeMidnight + playtimeAfterMidnight;
        } else {
            isPassedMidnight = false;
            playtimeBeforeMidnight = 0;
            playtimeAfterMidnight = 0;
            allPlaytime = disconnectSeconds - connectSeconds;
        }

        String partialTableName = tableName.substring("pt_data_".length());

        String checkDuplicateQuery = "SELECT COUNT(*) FROM pt_calc_" + partialTableName
                + " WHERE connect_datetime = :connectDateTime AND disconnect_datetime = :disconnectDateTime";

        Number duplicateCount = (Number) entityManager.createNativeQuery(checkDuplicateQuery)
                .setParameter("connectDateTime", connectDateTime)
                .setParameter("disconnectDateTime", disconnectDateTime)
                .getSingleResult();

        if (duplicateCount.intValue() > 0) {
            return;
        }

        String insertQuery = "INSERT INTO pt_calc_" + partialTableName + " (" +
                "connect_year, connect_month, connect_day, connect_hour, connect_min, connect_sec, connect_datetime, connect_weeknum, "
                +
                "disconnect_year, disconnect_month, disconnect_day, disconnect_hour, disconnect_min, disconnect_sec, disconnect_datetime, "
                +
                "is_passed_midnight, midnight_epoch, playtime, playtime_before_midnight, playtime_after_midnight, all_playtime) "
                +
                "VALUES (:connectYear, :connectMonth, :connectDay, :connectHour, :connectMin, :connectSec, :connectDateTime, :connectWeekNum, "
                +
                ":disconnectYear, :disconnectMonth, :disconnectDay, :disconnectHour, :disconnectMin, :disconnectSec, :disconnectDateTime, "
                +
                ":isPassedMidnight, :midnightEpoch, :playtime, :playtimeBeforeMidnight, :playtimeAfterMidnight, :allPlaytime)";

        entityManager.createNativeQuery(insertQuery)
                .setParameter("connectYear", connectYear)
                .setParameter("connectMonth", connectMonth)
                .setParameter("connectDay", connectDay)
                .setParameter("connectHour", connectHour)
                .setParameter("connectMin", connectMin)
                .setParameter("connectSec", connectSec)
                .setParameter("connectDateTime", connectDateTime)
                .setParameter("connectWeekNum", connectWeekNum)
                .setParameter("disconnectYear", disconnectYear)
                .setParameter("disconnectMonth", disconnectMonth)
                .setParameter("disconnectDay", disconnectDay)
                .setParameter("disconnectHour", disconnectHour)
                .setParameter("disconnectMin", disconnectMin)
                .setParameter("disconnectSec", disconnectSec)
                .setParameter("disconnectDateTime", disconnectDateTime)
                .setParameter("isPassedMidnight", isPassedMidnight)
                .setParameter("midnightEpoch", midnightEpoch)
                .setParameter("playtime", playtime)
                .setParameter("playtimeBeforeMidnight", playtimeBeforeMidnight)
                .setParameter("playtimeAfterMidnight", playtimeAfterMidnight)
                .setParameter("allPlaytime", allPlaytime)
                .executeUpdate();
    }
    //////////////////////////////////////////////////////////////////////////////////////////////

    // Daily playtime per employee per server
    @Override
    @Transactional
    public void calculateDailyPlaytimePerServerPerEmployee() {
        entityManager.createNativeQuery(
                "UPDATE daily_playtime SET ariena_boxpvp = NULL, ariena_creative = NULL, ariena_events = NULL, ariena_prison = NULL, "
                        +
                        "ariena_skyblock = NULL, ariena_survival = NULL, bobsbuilder_boxpvp = NULL, bobsbuilder_creative = NULL, bobsbuilder_events = NULL, "
                        +
                        "bobsbuilder_prison = NULL, bobsbuilder_skyblock = NULL, bobsbuilder_survival = NULL, d0fka_boxpvp = NULL, d0fka_creative = NULL, "
                        +
                        "d0fka_events = NULL, d0fka_prison = NULL, d0fka_skyblock = NULL, d0fka_survival = NULL, emsiukemiau_boxpvp = NULL, emsiukemiau_creative = NULL, "
                        +
                        "emsiukemiau_events = NULL, emsiukemiau_prison = NULL, emsiukemiau_skyblock = NULL, emsiukemiau_survival = NULL, ernestasltu12_boxpvp = NULL, "
                        +
                        "ernestasltu12_creative = NULL, ernestasltu12_events = NULL, ernestasltu12_prison = NULL, ernestasltu12_skyblock = NULL, ernestasltu12_survival = NULL, "
                        +
                        "everly_boxpvp = NULL, everly_creative = NULL, everly_events = NULL, everly_prison = NULL, everly_skyblock = NULL, everly_survival = NULL, furija_boxpvp = NULL, "
                        +
                        "furija_creative = NULL, furija_events = NULL, furija_prison = NULL, furija_skyblock = NULL, furija_survival = NULL, labashey_boxpvp = NULL, labashey_creative = NULL, "
                        +
                        "labashey_events = NULL, labashey_prison = NULL, labashey_skyblock = NULL, labashey_survival = NULL, libete_boxpvp = NULL, libete_creative = NULL, libete_events = NULL, "
                        +
                        "libete_prison = NULL, libete_skyblock = NULL, libete_survival = NULL, mboti212_boxpvp = NULL, mboti212_creative = NULL, mboti212_events = NULL, mboti212_prison = NULL, "
                        +
                        "mboti212_skyblock = NULL, mboti212_survival = NULL, melitalove_boxpvp = NULL, melitalove_creative = NULL, melitalove_events = NULL, melitalove_prison = NULL, "
                        +
                        "melitalove_skyblock = NULL, melitalove_survival = NULL, plrxq_boxpvp = NULL, plrxq_creative = NULL, plrxq_events = NULL, plrxq_prison = NULL, plrxq_skyblock = NULL, "
                        +
                        "plrxq_survival = NULL, richpica_boxpvp = NULL, richpica_creative = NULL, richpica_events = NULL, richpica_prison = NULL, richpica_skyblock = NULL, richpica_survival = NULL, "
                        +
                        "sharans_boxpvp = NULL, sharans_creative = NULL, sharans_events = NULL, sharans_prison = NULL, sharans_skyblock = NULL, sharans_survival = NULL, shizo_boxpvp = NULL, "
                        +
                        "shizo_creative = NULL, shizo_events = NULL, shizo_prison = NULL, shizo_skyblock = NULL, shizo_survival = NULL")
                .executeUpdate();

        Query tablesQuery = entityManager.createNativeQuery(
                "SELECT table_name FROM information_schema.tables WHERE table_name LIKE 'pt_calc_%'");

        @SuppressWarnings("unchecked")
        List<String> tableNames = tablesQuery.getResultList();

        for (String tableName : tableNames) {
            String username = getUsernameFromTableName(tableName);
            String server = getServerFromTableName(tableName);

            Query query = entityManager.createNativeQuery(
                    "SELECT connect_datetime, disconnect_datetime, is_passed_midnight, playtime_before_midnight, playtime_after_midnight, all_playtime FROM pt_calc_"
                            + server + "_" + username);

            @SuppressWarnings("unchecked")
            List<Object[]> results = query.getResultList();

            for (Object[] result : results) {
                Timestamp connectTimestamp = (Timestamp) result[0];
                Timestamp disconnectTimestamp = (Timestamp) result[1];
                LocalDateTime connectDatetime = connectTimestamp.toLocalDateTime();
                LocalDateTime disconnectDatetime = disconnectTimestamp.toLocalDateTime();

                Boolean isPassedMidnight = (Boolean) result[2];
                Integer playtimeBeforeMidnight = (Integer) result[3];
                Integer playtimeAfterMidnight = (Integer) result[4];
                Integer allPlaytime = (Integer) result[5];

                updateDailyPlaytimeTable(username, connectDatetime, disconnectDatetime, isPassedMidnight,
                        playtimeBeforeMidnight, playtimeAfterMidnight, allPlaytime, server);
            }
        }
    }

    private String getServerFromTableName(String tableName) {
        return tableName.split("_")[2];
    }

    private String getUsernameFromTableName(String tableName) {
        return tableName.split("_")[3];
    }

    private void updateDailyPlaytimeTable(String username, LocalDateTime connectDatetime,
            LocalDateTime disconnectDatetime,
            Boolean isPassedMidnight, Integer playtimeBeforeMidnight, Integer playtimeAfterMidnight,
            Integer allPlaytime, String server) {
        LocalDate connectDate = connectDatetime.toLocalDate();
        LocalDate disconnectDate = disconnectDatetime.toLocalDate();

        if (!connectDate.equals(disconnectDate)) {
            int firstDayPlaytime = (int) Duration.between(connectDatetime, connectDate.atStartOfDay().plusDays(1))
                    .getSeconds();
            updateDailyPlaytime(username, connectDate, firstDayPlaytime, server);

            int secondDayPlaytime = (int) Duration.between(disconnectDate.atStartOfDay(), disconnectDatetime)
                    .getSeconds();
            updateDailyPlaytime(username, disconnectDate, secondDayPlaytime, server);
        } else {
            updateDailyPlaytime(username, connectDate, allPlaytime, server);
        }
    }

    private void updateDailyPlaytime(String username, LocalDate date, Integer playtime, String server) {
        DailyPlaytime dailyPlaytimeDate = dailyPlaytimeRepository.findByDate(date);

        if (dailyPlaytimeDate == null) {
            dailyPlaytimeDate = new DailyPlaytime();
            dailyPlaytimeDate.setDate(date);
        }

        String column = username + "_" + server;

        setPlaytimeInColumn(dailyPlaytimeDate, column, playtime);

        dailyPlaytimeRepository.save(dailyPlaytimeDate);
    }

    private void setPlaytimeInColumn(DailyPlaytime dailyPlaytimeDate, String column, Integer playtime) {
        double playtimeDouble = (double) playtime / 3600.0;

        // String updateQuery = "UPDATE daily_playtime SET " + column + " = :playtime
        // WHERE date = :dailyPlaytimeDate";
        @SuppressWarnings("unused")
        String sqlQuery = "INSERT INTO daily_playtime (date, " + column + ") VALUES (:dailyPlaytimeDate, :playtime)"
                + " ON DUPLICATE KEY UPDATE " + column + " = :playtime";

        Date date = java.sql.Date.valueOf(dailyPlaytimeDate.getDate());

        // entityManager.createNativeQuery(updateQuery)
        // .setParameter("playtime", playtimeDouble)
        // .setParameter("dailyPlaytimeDate", date)
        // .executeUpdate();

        // DEBUG
        System.out.println(column + " >> " + date + " >> " + playtimeDouble + " (" + playtime + ")");
    }
}
