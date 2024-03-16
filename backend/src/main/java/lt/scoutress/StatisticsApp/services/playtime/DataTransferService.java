// package lt.scoutress.StatisticsApp.services.playtime;

// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import jakarta.persistence.*;

// import java.sql.Timestamp;
// import java.util.List;

// @Service
// public class DataTransferService {

//     @PersistenceContext(unitName = "sqliteEntityManager")
//     private EntityManager sqliteEntityManager;

//     @PersistenceContext(unitName = "mysqlEntityManager")
//     private EntityManager mysqlEntityManager;

//     @Transactional(transactionManager = "transactionManager", readOnly = true)
//     public void transferData() {
//         List<Object[]> user1aData = fetchDataFromSQLite("123", "1");
//         List<Object[]> user1bData = fetchDataFromSQLite("123", "0");

//         saveDataToMySQL(user1aData, "user1a");
//         saveDataToMySQL(user1bData, "user1b");
//     }

//     @SuppressWarnings("unchecked")
//     private List<Object[]> fetchDataFromSQLite(String user, String action) {
//         Query query = sqliteEntityManager.createNativeQuery(
//                 "SELECT time FROM co_session WHERE user = :user AND action = :action");
//         query.setParameter("user", user);
//         query.setParameter("action", action);
//         return query.getResultList();
//     }

//     @Transactional(transactionManager = "mysqlTransactionManager")
//     private void saveDataToMySQL(List<Object[]> data, String column) {
//         for (Object[] row : data) {
//             Timestamp time = new Timestamp((long) row[0] * 1000);
//             mysqlEntityManager.createNativeQuery(
//                     "INSERT INTO playtime_data_all_survival (" + column + ") VALUES (:time)")
//                     .setParameter("time", time)
//                     .executeUpdate();
//         }
//     }
// }
