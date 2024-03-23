package lt.scoutress.StatisticsApp.servicesimpl;

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
}
