package com.scoutress.KaimuxAdminStats.Services;

import java.util.List;

import com.scoutress.KaimuxAdminStats.Entity.Employee;

public interface EmployeeService {

    List<Employee> findAll();

    Employee save(Employee employee);
}
