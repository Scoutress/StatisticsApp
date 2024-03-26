package lt.scoutress.StatisticsApp.servicesimpl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lt.scoutress.StatisticsApp.entity.playtime.Playtime;
import lt.scoutress.StatisticsApp.repositories.PlaytimeRepository;
import lt.scoutress.StatisticsApp.services.playtime.PlaytimeService;

@Service
public class PlaytimeServiceImpl implements PlaytimeService{

    @Autowired
    private EntityManager entityManager;

    private final PlaytimeRepository playtimeRepository;

    public PlaytimeServiceImpl(PlaytimeRepository playtimeRepository) {
        this.playtimeRepository = playtimeRepository;
    }

    @Override
    public List<Playtime> findAll() {
        return playtimeRepository.findAll();
    }
    
    //  Survival
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

            Query timeQuery = entityManager.createQuery("SELECT c.time, c.action FROM Survival c WHERE c.user = :survivalCode AND c.action IN (0, 1)");
            timeQuery.setParameter("survivalCode", survivalCode);
    
            @SuppressWarnings("unchecked")
            List<Object[]> timeAndActionValues = timeQuery.getResultList();
    
            createAndSaveSurvivalPlaytimeDataTable(username);
    
            for (Object[] timeAndAction : timeAndActionValues) {
                Integer time = (Integer) timeAndAction[0];
                Integer action = (Integer) timeAndAction[1];
            
                if (time != null && action != null) {
                    if (action == 1) {
                        saveTimeToSurvivalConnect(username, time);
                    } else if (action == 0) {
                        saveTimeToSurvivalDisconnect(username, time);
                    }
                }
            }            
        }
    }

    private void createAndSaveSurvivalPlaytimeDataTable(String username) {
        String tableName = "pt_data_surv_" + username;
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName + " (id INT AUTO_INCREMENT PRIMARY KEY, connect INT, disconnect INT)";
        entityManager.createNativeQuery(createTableQuery).executeUpdate();
    }

    private void saveTimeToSurvivalConnect(String username, int time) {
        String tableName = "pt_data_surv_" + username;
        String insertQuery = "INSERT INTO " + tableName + " (connect) VALUES (:time)";
        entityManager.createNativeQuery(insertQuery).setParameter("time", time).executeUpdate();
    }

    private void saveTimeToSurvivalDisconnect(String username, int time) {
        String tableName = "pt_data_surv_" + username;
        String updateQuery = "UPDATE " + tableName + " SET disconnect = :time WHERE disconnect IS NULL";
        entityManager.createNativeQuery(updateQuery).setParameter("time", time).executeUpdate();
    }

        
    //  Skyblock
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

            Query timeQuery = entityManager.createQuery("SELECT c.time, c.action FROM Skyblock c WHERE c.user = :skyblockCode AND c.action IN (0, 1)");
            timeQuery.setParameter("skyblockCode", skyblockCode);
    
            @SuppressWarnings("unchecked")
            List<Object[]> timeAndActionValues = timeQuery.getResultList();
    
            createAndSaveSkyblockPlaytimeDataTable(username);
    
            for (Object[] timeAndAction : timeAndActionValues) {
                Integer time = (Integer) timeAndAction[0];
                Integer action = (Integer) timeAndAction[1];
            
                if (time != null && action != null) {
                    if (action == 1) {
                        saveTimeToSkyblockConnect(username, time);
                    } else if (action == 0) {
                        saveTimeToSkyblockDisconnect(username, time);
                    }
                }
            }
        }
    }

    private void createAndSaveSkyblockPlaytimeDataTable(String username) {
        String tableName = "pt_data_sky_" + username;
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName + " (id INT AUTO_INCREMENT PRIMARY KEY, connect INT, disconnect INT)";
        entityManager.createNativeQuery(createTableQuery).executeUpdate();
    }

    private void saveTimeToSkyblockConnect(String username, int time) {
        String tableName = "pt_data_sky_" + username;
        String insertQuery = "INSERT INTO " + tableName + " (connect) VALUES (:time)";
        entityManager.createNativeQuery(insertQuery).setParameter("time", time).executeUpdate();
    }

    private void saveTimeToSkyblockDisconnect(String username, int time) {
        String tableName = "pt_data_sky_" + username;
        String updateQuery = "UPDATE " + tableName + " SET disconnect = :time WHERE disconnect IS NULL";
        entityManager.createNativeQuery(updateQuery).setParameter("time", time).executeUpdate();
    }

    //  Creative
    @Override
    @Transactional
    public void migrateCreativePlaytimeData() {
        Query query = entityManager.createQuery("SELECT p.username, p.creative FROM PlaytimeDBCodes p");
        
        @SuppressWarnings("unchecked")
        List<Object[]> usernamesAndCreatieveCodes = query.getResultList();
    
        for (Object[] usernameAndCreativeCode : usernamesAndCreatieveCodes) {
            String username = (String) usernameAndCreativeCode[0];
            String creativeCode = (String) usernameAndCreativeCode[1];
    
            if (creativeCode == null || creativeCode.isEmpty()) {
                continue;
            }

            Query timeQuery = entityManager.createQuery("SELECT c.time, c.action FROM Creative c WHERE c.user = :creativeCode AND c.action IN (0, 1)");
            timeQuery.setParameter("creativeCode", creativeCode);
    
            @SuppressWarnings("unchecked")
            List<Object[]> timeAndActionValues = timeQuery.getResultList();
    
            createAndSaveCreativePlaytimeDataTable(username);
    
            for (Object[] timeAndAction : timeAndActionValues) {
                Integer time = (Integer) timeAndAction[0];
                Integer action = (Integer) timeAndAction[1];
            
                if (time != null && action != null) {
                    if (action == 1) {
                        saveTimeToCreativeConnect(username, time);
                    } else if (action == 0) {
                        saveTimeToCreativeDisconnect(username, time);
                    }
                }
            }
        }
    }

    private void createAndSaveCreativePlaytimeDataTable(String username) {
        String tableName = "pt_data_creat_" + username;
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName + " (id INT AUTO_INCREMENT PRIMARY KEY, connect INT, disconnect INT)";
        entityManager.createNativeQuery(createTableQuery).executeUpdate();
    }

    private void saveTimeToCreativeConnect(String username, int time) {
        String tableName = "pt_data_creat_" + username;
        String insertQuery = "INSERT INTO " + tableName + " (connect) VALUES (:time)";
        entityManager.createNativeQuery(insertQuery).setParameter("time", time).executeUpdate();
    }

    private void saveTimeToCreativeDisconnect(String username, int time) {
        String tableName = "pt_data_creat_" + username;
        String updateQuery = "UPDATE " + tableName + " SET disconnect = :time WHERE disconnect IS NULL";
        entityManager.createNativeQuery(updateQuery).setParameter("time", time).executeUpdate();
    }

    //  Boxpvp
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

            Query timeQuery = entityManager.createQuery("SELECT c.time, c.action FROM Boxpvp c WHERE c.user = :boxpvpCode AND c.action IN (0, 1)");
            timeQuery.setParameter("boxpvpCode", boxpvpCode);
    
            @SuppressWarnings("unchecked")
            List<Object[]> timeAndActionValues = timeQuery.getResultList();
    
            createAndSaveBoxpvpPlaytimeDataTable(username);
    
            for (Object[] timeAndAction : timeAndActionValues) {
                Integer time = (Integer) timeAndAction[0];
                Integer action = (Integer) timeAndAction[1];
            
                if (time != null && action != null) {
                    if (action == 1) {
                        saveTimeToBoxpvpConnect(username, time);
                    } else if (action == 0) {
                        saveTimeToBoxpvpDisconnect(username, time);
                    }
                }
            }
        }
    }

    private void createAndSaveBoxpvpPlaytimeDataTable(String username) {
        String tableName = "pt_data_box_" + username;
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName + " (id INT AUTO_INCREMENT PRIMARY KEY, connect INT, disconnect INT)";
        entityManager.createNativeQuery(createTableQuery).executeUpdate();
    }

    private void saveTimeToBoxpvpConnect(String username, int time) {
        String tableName = "pt_data_box_" + username;
        String insertQuery = "INSERT INTO " + tableName + " (connect) VALUES (:time)";
        entityManager.createNativeQuery(insertQuery).setParameter("time", time).executeUpdate();
    }

    private void saveTimeToBoxpvpDisconnect(String username, int time) {
        String tableName = "pt_data_box_" + username;
        String updateQuery = "UPDATE " + tableName + " SET disconnect = :time WHERE disconnect IS NULL";
        entityManager.createNativeQuery(updateQuery).setParameter("time", time).executeUpdate();
    }

    //  Prison
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

            Query timeQuery = entityManager.createQuery("SELECT c.time, c.action FROM Prison c WHERE c.user = :prisonCode AND c.action IN (0, 1)");
            timeQuery.setParameter("prisonCode", prisonCode);
    
            @SuppressWarnings("unchecked")
            List<Object[]> timeAndActionValues = timeQuery.getResultList();
    
            createAndSavePrisonPlaytimeDataTable(username);
    
            for (Object[] timeAndAction : timeAndActionValues) {
                Integer time = (Integer) timeAndAction[0];
                Integer action = (Integer) timeAndAction[1];
            
                if (time != null && action != null) {
                    if (action == 1) {
                        saveTimeToPrisonConnect(username, time);
                    } else if (action == 0) {
                        saveTimeToPrisonDisconnect(username, time);
                    }
                }
            }
        }
    }

    private void createAndSavePrisonPlaytimeDataTable(String username) {
        String tableName = "pt_data_pris_" + username;
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName + " (id INT AUTO_INCREMENT PRIMARY KEY, connect INT, disconnect INT)";
        entityManager.createNativeQuery(createTableQuery).executeUpdate();
    }

    private void saveTimeToPrisonConnect(String username, int time) {
        String tableName = "pt_data_pris_" + username;
        String insertQuery = "INSERT INTO " + tableName + " (connect) VALUES (:time)";
        entityManager.createNativeQuery(insertQuery).setParameter("time", time).executeUpdate();
    }

    private void saveTimeToPrisonDisconnect(String username, int time) {
        String tableName = "pt_data_pris_" + username;
        String updateQuery = "UPDATE " + tableName + " SET disconnect = :time WHERE disconnect IS NULL";
        entityManager.createNativeQuery(updateQuery).setParameter("time", time).executeUpdate();
    }

    //  Events
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

            Query timeQuery = entityManager.createQuery("SELECT c.time, c.action FROM Events c WHERE c.user = :eventsCode AND c.action IN (0, 1)");
            timeQuery.setParameter("eventsCode", eventsCode);
    
            @SuppressWarnings("unchecked")
            List<Object[]> timeAndActionValues = timeQuery.getResultList();
    
            createAndSaveEventsPlaytimeDataTable(username);
    
            for (Object[] timeAndAction : timeAndActionValues) {
                Integer time = (Integer) timeAndAction[0];
                Integer action = (Integer) timeAndAction[1];
            
                if (time != null && action != null) {
                    if (action == 1) {
                        saveTimeToEventsConnect(username, time);
                    } else if (action == 0) {
                        saveTimeToEventsDisconnect(username, time);
                    }
                }
            }
        }
    }

    private void createAndSaveEventsPlaytimeDataTable(String username) {
        String tableName = "pt_data_eve_" + username;
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName + " (id INT AUTO_INCREMENT PRIMARY KEY, connect INT, disconnect INT)";
        entityManager.createNativeQuery(createTableQuery).executeUpdate();
    }

    private void saveTimeToEventsConnect(String username, int time) {
        String tableName = "pt_data_eve_" + username;
        String insertQuery = "INSERT INTO " + tableName + " (connect) VALUES (:time)";
        entityManager.createNativeQuery(insertQuery).setParameter("time", time).executeUpdate();
    }

    private void saveTimeToEventsDisconnect(String username, int time) {
        String tableName = "pt_data_eve_" + username;
        String updateQuery = "UPDATE " + tableName + " SET disconnect = :time WHERE disconnect IS NULL";
        entityManager.createNativeQuery(updateQuery).setParameter("time", time).executeUpdate();
    }

    //  Survival
    @Override
    @Transactional
    public void convertTimestampToDateSurvival() {
        Query tablesQuery = entityManager.createNativeQuery("SELECT table_name FROM information_schema.tables WHERE table_name LIKE 'pt_data_surv_%'");

        @SuppressWarnings("unchecked")
        List<String> tableNames = tablesQuery.getResultList();

        for (String tableName : tableNames) {
            Query query = entityManager.createNativeQuery("SELECT connect, disconnect FROM " + tableName);

            @SuppressWarnings("unchecked")
            List<Object[]> results = query.getResultList();

            for (Object[] result : results) {
            Integer connectSeconds = (Integer) result[0];
            Integer disconnectSeconds = (Integer) result[1];

            LocalDateTime connectDateTime    = Instant.ofEpochSecond(connectSeconds)   .atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime disconnectDateTime = Instant.ofEpochSecond(disconnectSeconds).atZone(ZoneId.systemDefault()).toLocalDateTime();

            createTableIfNotExistsSurvival(tableName);
            updateTableSurvival(connectDateTime, disconnectDateTime, connectSeconds, disconnectSeconds, tableName);
            }
        }
    }

    private void createTableIfNotExistsSurvival(String tableName) {
        Query checkTableQuery = entityManager.createNativeQuery("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'kaimuxstatistics' AND table_name = 'pt_data_calc_surv_" + tableName + "'");
    
        Long tableExists = (Long) checkTableQuery.getSingleResult();
    
        if (tableExists == 0) {
            String createTableQuery = "CREATE TABLE pt_data_calc_surv_" + tableName + " (" +
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
                "disconnect_date DATE, " +
                "is_passed_midnight BOOLEAN, " +
                "midnight_epoch INT, " +
                "playtime INT, " +
                "playtime_before_midnight INT, " +
                "playtime_after_midnight INT, " +
                "all_playtime INT)";
    
            Query createTable = entityManager.createNativeQuery(createTableQuery);
    
            createTable.executeUpdate();
        }
    }
    
    private void updateTableSurvival(LocalDateTime connectDateTime, LocalDateTime disconnectDateTime, Integer connectSeconds, Integer disconnectSeconds, String tableName) {
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
    
        int playtime = (disconnectSeconds - connectSeconds) / 3600;
    
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
    
        String insertQuery = "INSERT INTO pt_data_calc_surv_" + tableName + " (" +
                "connect_year, connect_month, connect_day, connect_hour, connect_min, connect_sec, connect_datetime, connect_weeknum, " +
                "disconnect_year, disconnect_month, disconnect_day, disconnect_hour, disconnect_min, disconnect_sec, disconnect_date, " +
                "is_passed_midnight, midnight_epoch, playtime, playtime_before_midnight, playtime_after_midnight, all_playtime) " +
                "VALUES (:connectYear, :connectMonth, :connectDay, :connectHour, :connectMin, :connectSec, :connectDateTime, :connectWeekNum, " +
                ":disconnectYear, :disconnectMonth, :disconnectDay, :disconnectHour, :disconnectMin, :disconnectSec, :disconnectDateTime, " +
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
}
