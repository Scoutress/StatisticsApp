package lt.scoutress.StatisticsApp.servicesimpl;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import lt.scoutress.StatisticsApp.entity.Employee;
import lt.scoutress.StatisticsApp.repositories.EmployeeRepository;
import lt.scoutress.StatisticsApp.services.EmployeeService;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private EmployeeRepository employeeRepository;
    private EntityManager entityManager;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> findAll() {
        return employeeRepository.findAllByOrderByLevel();
    }

    @Override
    public void addEmployee(Employee employee) {
        employee.setLevel("Helper");
        employee.setJoinDate(LocalDate.now());
        employeeRepository.save(employee);
    }

    @Override
    public Employee findById(int employeeId) {
        Employee theEmployee = entityManager.find(Employee.class, employeeId);
        return theEmployee;
    }
}
