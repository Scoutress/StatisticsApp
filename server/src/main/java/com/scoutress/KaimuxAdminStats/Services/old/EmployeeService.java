package com.scoutress.KaimuxAdminStats.Services.old;

import java.util.List;

import com.scoutress.KaimuxAdminStats.Entity.old.Employees.Employee;

public interface EmployeeService {

    List<Employee> findAll();

    Employee save(Employee employee);
}
