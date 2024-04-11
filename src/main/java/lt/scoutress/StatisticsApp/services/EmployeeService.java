package lt.scoutress.StatisticsApp.Services;

import java.util.List;

import lt.scoutress.StatisticsApp.entity.Employees.Employee;

public interface EmployeeService {

    List<Employee> findAll();

    List<Employee> getAllEmployees();

    void addEmployee(Employee employee);

    Employee findById(int employeeId);

    Employee save(Employee employee);

    void updateEmployeeCodes(Long id, Employee employee);

    void deleteById(int id);

    void saveEmployeeCodes(Employee employee);
}
