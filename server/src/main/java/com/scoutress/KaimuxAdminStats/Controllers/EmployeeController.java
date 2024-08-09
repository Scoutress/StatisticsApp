package com.scoutress.KaimuxAdminStats.Controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scoutress.KaimuxAdminStats.Entity.Employees.Employee;
import com.scoutress.KaimuxAdminStats.Services.EmployeeService;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
    
    @GetMapping("/all")
    public List<Employee> getAllEmployees(){
        return employeeService.findAll();
    }
}
