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

            Productivity existingRecord = entityManager.createQuery("SELECT p FROM Productivity p WHERE p.username = :username", Productivity.class)
                .setParameter("username", username)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);

            if (existingRecord == null) {
                Productivity newRecord = new Productivity();
                newRecord.setUsername(username);
                if (level != null && !level.isEmpty()) {
                    newRecord.setLevel(level);
                }
                entityManager.persist(newRecord);
            } else {
                if (level != null && !level.isEmpty()) {
                    existingRecord.setLevel(level);
                    entityManager.merge(existingRecord);
                }
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
}
