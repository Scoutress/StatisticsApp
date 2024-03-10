package lt.scoutress.StatisticsApp.servicesimpl;

import org.springframework.stereotype.Service;

import jakarta.persistence.*;
import lt.scoutress.StatisticsApp.entity.Employee;
import lt.scoutress.StatisticsApp.repositories.EmployeeRepository;
import lt.scoutress.StatisticsApp.services.EmployeeService;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private EmployeeRepository employeeRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> findAll() {
        return employeeRepository.findAllByOrderByLevel();
    }

    @SuppressWarnings("null")
    @Override
    public void addEmployee(Employee employee) {
        employeeRepository.save(employee);
    }

    @Override
    public Employee findById(int employeeId) {
        Employee employee = entityManager.find(Employee.class, employeeId);
        return employee;
    }

    public void deleteById(int id) {
        employeeRepository.deleteById(id);
    }

    // @Override
    // public void createEmployeeTable(Employee employee) {
    //     String tableName = "employee_" + employee.getUsername();

    //     String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
    //             "id INT AUTO_INCREMENT PRIMARY KEY, " +
    //             "date DATE, " +
    //             "dc_tickets INT, " +
    //             "dc_comparison_percent INT, " +
    //             "help_count INT, " +
    //             "help_minus INT, " +
    //             "help_comparison INT, " +
    //             "help_comparison_percent INT, " +
    //             "playtime INT" +
    //             ")";
    //     Query query = entityManager.createNativeQuery(sql);
    //     query.executeUpdate();
    // }

}
