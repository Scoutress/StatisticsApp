package lt.scoutress.StatisticsApp.Servicesimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lt.scoutress.StatisticsApp.Repositories.EmployeeRepository;
import lt.scoutress.StatisticsApp.Repositories.ProductivityRepository;
import lt.scoutress.StatisticsApp.Services.ProductivityService;
import lt.scoutress.StatisticsApp.entity.Productivity;
import lt.scoutress.StatisticsApp.entity.Employees.Employee;

@Service
public class ProductivityServiceImpl implements ProductivityService {

    @PersistenceContext
    private EntityManager entityManager;

    private final EmployeeRepository employeeRepository;
    private final ProductivityRepository productivityRepository;

    public ProductivityServiceImpl(EmployeeRepository employeeRepository,
            ProductivityRepository productivityRepository) {
        this.employeeRepository = employeeRepository;
        this.productivityRepository = productivityRepository;
    }

    @Override
    public List<Productivity> findAll() {
        return entityManager.createQuery(
                "SELECT p FROM Productivity p JOIN FETCH p.employee e ORDER BY CASE e.level WHEN 'Owner' THEN 1 WHEN 'Coder' THEN 2 WHEN 'Operator' THEN 3 WHEN 'Manager' THEN 4 WHEN 'Organizer' THEN 5 WHEN 'Overseer' THEN 6 WHEN 'ChatMod' THEN 7 WHEN 'Support' THEN 8 WHEN 'Helper' THEN 9 ELSE 10 END",
                Productivity.class).getResultList();
    }

    @Override
    public void createOrUpdateProductivityForAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();

        for (Employee employee : employees) {
            Productivity productivity = productivityRepository.findByEmployeeId(employee.getId());

            if (productivity == null) {
                productivity = new Productivity();
                productivity.setEmployee(employee);
                productivityRepository.save(productivity);
            }
        }
    }
}
