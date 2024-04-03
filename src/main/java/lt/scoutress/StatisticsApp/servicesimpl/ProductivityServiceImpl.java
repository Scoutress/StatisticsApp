package lt.scoutress.StatisticsApp.servicesimpl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lt.scoutress.StatisticsApp.entity.Employee;
import lt.scoutress.StatisticsApp.entity.Productivity;
import lt.scoutress.StatisticsApp.entity.ProductivityCalc;
import lt.scoutress.StatisticsApp.repositories.EmployeeRepository;
import lt.scoutress.StatisticsApp.repositories.ProductivityRepository;
import lt.scoutress.StatisticsApp.services.ProductivityService;

@Service
public class ProductivityServiceImpl implements ProductivityService{

    private final ProductivityRepository productivityRepository;
    private final EmployeeRepository employeeRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    public ProductivityServiceImpl(ProductivityRepository productivityRepository, EmployeeRepository employeeRepository){
        this.productivityRepository = productivityRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<Productivity> findAll() {
        return productivityRepository.findAllByLevel();
    }

    @Override
    public List<Employee> findAllByOrderByLevel(){
        return employeeRepository.findAllByOrderByLevel();
    }
    
    @Override
    @Transactional
    public void copyUsernamesAndLevels() {
        List<Employee> employees = entityManager.createQuery("SELECT e FROM Employee e", Employee.class).getResultList();

        for (Employee employee : employees) {
            String username = employee.getUsername();
            String level = employee.getLevel();

            ProductivityCalc existingProductivityCalcRecord = entityManager.createQuery("SELECT pc FROM ProductivityCalc pc WHERE pc.username = :username", ProductivityCalc.class)
                .setParameter("username", username)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);

            if (existingProductivityCalcRecord == null) {
                ProductivityCalc newProductivityCalcRecord = new ProductivityCalc();
                newProductivityCalcRecord.setUsername(username);
                newProductivityCalcRecord.setLevel(level);
                entityManager.persist(newProductivityCalcRecord);
            } else {
                existingProductivityCalcRecord.setLevel(level);
                entityManager.merge(existingProductivityCalcRecord);
            }

            Productivity existingProductivityRecord = entityManager.createQuery("SELECT p FROM Productivity p WHERE p.username = :username", Productivity.class)
                .setParameter("username", username)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);

            if (existingProductivityRecord == null) {
                Productivity newProductivityRecord = new Productivity();
                newProductivityRecord.setUsername(username);
                newProductivityRecord.setLevel(level);
                entityManager.persist(newProductivityRecord);
            } else {
                existingProductivityRecord.setLevel(level);
                entityManager.merge(existingProductivityRecord);
            }
        }
    }

    @Override
    @Transactional
    public void calculateActivityPerHalfYear() {
        List<String> usernames = getAllUsernames();

        for (String username : usernames) {
            int totalActivity = getTotalActivityForUsername(username);
            double formattedActivity = calculateTimeInHours(totalActivity);
            saveActivityPerHalfYear(username, formattedActivity);
        }
    }

    private double calculateTimeInHours(long seconds) {
        return seconds / 3600.0;
    }

    @SuppressWarnings("unchecked")
    private List<String> getAllUsernames() {
        Query query = entityManager.createNativeQuery("SELECT username FROM productivity");
        return query.getResultList();
    }

    private int getTotalActivityForUsername(String username) {
        int totalActivity = 0;
        List<String> tableNames = getTableNames(username);
        
        for (String tableName : tableNames) {
            if (doesTableExist(tableName)) {
                try {
                    Query query = entityManager.createNativeQuery("SELECT COALESCE(SUM(all_playtime), 0) FROM " + tableName + " WHERE connect_datetime >= :dateThreshold OR disconnect_datetime >= :dateThreshold");
                    LocalDateTime dateThreshold = LocalDateTime.now().minus(180, ChronoUnit.DAYS);
                    query.setParameter("dateThreshold", dateThreshold);
                    BigDecimal result = (BigDecimal) query.getSingleResult();
                    totalActivity += (result != null) ? result.intValue() : 0;
                } catch (Exception e) {
                    totalActivity += 0;
                }
            }
            continue;
        }
        
        return totalActivity;
    }

    private boolean doesTableExist(String tableName) {
        Query query = entityManager.createNativeQuery("SELECT COUNT(*) FROM information_schema.tables WHERE table_name = :tableName");
        query.setParameter("tableName", tableName);
        int count = ((Number) query.getSingleResult()).intValue();
        return count > 0;
    }

    private List<String> getTableNames(String username) {
        List<String> tableNames = new ArrayList<>();
        tableNames.add("pt_data_calc_surv_" + username);
        tableNames.add("pt_data_calc_sky_" + username);
        tableNames.add("pt_data_calc_creat_" + username);
        tableNames.add("pt_data_calc_box_" + username);
        tableNames.add("pt_data_calc_pris_" + username);
        tableNames.add("pt_data_calc_eve_" + username);
        return tableNames;
    }

    private void saveActivityPerHalfYear(String username, double totalActivity) {
        DecimalFormat df = new DecimalFormat("#.##");
        String formattedTotalActivity = df.format(totalActivity);
        double parsedTotalActivity = Double.parseDouble(formattedTotalActivity.replace(",", "."));

        Query query = entityManager.createNativeQuery("UPDATE productivity SET activity_per_half_year = :totalActivity WHERE username = :username");
        query.setParameter("username", username);
        query.setParameter("totalActivity", parsedTotalActivity);
        query.executeUpdate();
    }

    @Override
    @Transactional
    public void checkIfEmployeeHasEnoughDaysForPromotion() {
        Query query = entityManager.createQuery("SELECT pc.username, pc.level FROM ProductivityCalc pc");

        @SuppressWarnings("unchecked")
        List<Object[]> resultList = query.getResultList();
        
        for (Object[] result : resultList) {
            String username = (String) result[0];
            String level = (String) result[1];
            
            boolean hasEnoughDaysForPromotion = isEnoughDaysForPromotion(username, level);
            
            Query updateQuery = entityManager.createQuery("UPDATE ProductivityCalc pc SET pc.isEnoughDaysForPromotion = :hasEnoughDaysForPromotion WHERE pc.username = :username");
            updateQuery.setParameter("hasEnoughDaysForPromotion", hasEnoughDaysForPromotion);
            updateQuery.setParameter("username", username);
            updateQuery.executeUpdate();
        }
    }

    private boolean isEnoughDaysForPromotion(String username, String level) {
        Query daysSinceJoinQuery = entityManager.createQuery("SELECT e.daysSinceJoin FROM Employee e WHERE e.username = :username");
        daysSinceJoinQuery.setParameter("username", username);
        int daysSinceJoin = (int) daysSinceJoinQuery.getSingleResult();

        boolean enoughDaysForPromotion = false;

        if (level.equals("Helper")) {
            enoughDaysForPromotion = daysSinceJoin > 30;
        } else if (level.equals("Support")) {
            enoughDaysForPromotion = daysSinceJoin > 90;
        } else if (level.equals("ChatMod")) {
            enoughDaysForPromotion = daysSinceJoin > 210;
        } else if (level.equals("Overseer")) {
            enoughDaysForPromotion = daysSinceJoin > 570;
        }
        
        return enoughDaysForPromotion;
    }
}
