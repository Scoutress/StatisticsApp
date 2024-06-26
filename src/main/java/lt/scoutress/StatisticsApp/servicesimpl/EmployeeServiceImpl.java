package lt.scoutress.StatisticsApp.Servicesimpl;

import org.springframework.stereotype.Service;

import jakarta.persistence.*;
import lt.scoutress.StatisticsApp.Repositories.EmployeeRepository;
import lt.scoutress.StatisticsApp.Services.EmployeeService;
import lt.scoutress.StatisticsApp.entity.Employees.Employee;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private EmployeeRepository employeeRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<Employee> findAll() {
        return employeeRepository.findAllByOrderByLevel();
    }

    @Override
    public void addEmployee(Employee employee) {
        employeeRepository.save(employee);
    }

    @Override
    public Employee findById(int employeeId) {
        Employee employee = entityManager.find(Employee.class, employeeId);
        return employee;
    }

    @Override
    public void deleteById(int id) {
        employeeRepository.deleteById(id);
    }

    @Override
    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Employee> getAllEmployees() {
        Query query = entityManager.createQuery("SELECT e FROM Employee e");
        return query.getResultList();
    }

    @Override
    public void updateEmployeeCodes(Long id, Employee employee) {
        Employee existingEmployee = employeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Employee not found"));
        existingEmployee.setEmployeeCodes(employee.getEmployeeCodes());
        employeeRepository.save(existingEmployee);
    }

    @Override
    public void saveEmployeeCodes(Employee employee) {
        employeeRepository.save(employee);
    }
}
