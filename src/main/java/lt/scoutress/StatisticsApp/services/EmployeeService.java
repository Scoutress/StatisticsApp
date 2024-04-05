package lt.scoutress.StatisticsApp.services;

import lt.scoutress.StatisticsApp.entity.Employee;

import java.util.List;

public interface EmployeeService {

    List<Employee> findAll();

    List<Employee> getAllEmployees();

    void addEmployee(Employee employee);

    Employee findById(int employeeId);

    Employee save(Employee employee);

}
