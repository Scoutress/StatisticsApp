package com.scoutress.KaimuxAdminStats.Servicesimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.Employees.Employee;
import com.scoutress.KaimuxAdminStats.Entity.Productivity;
import com.scoutress.KaimuxAdminStats.Repositories.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.Repositories.ProductivityRepository;
import com.scoutress.KaimuxAdminStats.Services.EmployeeService;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ProductivityRepository productivityRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, ProductivityRepository productivityRepository) {
        this.employeeRepository = employeeRepository;
        this.productivityRepository = productivityRepository;
    }

    @Override
    public List<Employee> findAll() {
        return employeeRepository.findAllByOrderByLevel();
    }

    @Override
    public Employee save(Employee employee) {
        Employee savedEmployee = employeeRepository.save(employee);
        Productivity newProductivity = new Productivity();
        newProductivity.setEmployee(savedEmployee);
        productivityRepository.save(newProductivity);
        return savedEmployee;
    }
}
