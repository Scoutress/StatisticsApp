package lt.scoutress.StatisticsApp.services;

import java.util.List;

import lt.scoutress.StatisticsApp.entity.Employee;

public interface EmployeeService {

    List<Employee> findAll();

    void addEmployee(Employee employee);

    Employee findById(int employeeId);
}
