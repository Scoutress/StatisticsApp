package lt.scoutress.StatisticsApp.servicesimpl;

import java.util.List;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
        return productivityRepository.findAll();
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
}
