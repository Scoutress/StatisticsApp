package lt.scoutress.StatisticsApp.servicesimpl;

import org.springframework.stereotype.Service;

import lt.scoutress.StatisticsApp.entity.Employee;
import lt.scoutress.StatisticsApp.repositories.EmployeeRepository;
import lt.scoutress.StatisticsApp.services.EmployeeService;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public void addEmployee(Employee employee) {
        employee.setLevel("Helper");
        employee.setJoinDate(LocalDate.now());
        employeeRepository.save(employee);
    } 

}
