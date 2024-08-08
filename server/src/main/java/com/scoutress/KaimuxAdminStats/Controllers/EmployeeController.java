package com.scoutress.KaimuxAdminStats.Controllers;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scoutress.KaimuxAdminStats.Services.EmployeeService;
import com.scoutress.KaimuxAdminStats.entity.Employees.Employee;

@RestController
@RequestMapping("/employee")
@CrossOrigin(origins = "http://localhost:5173")
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
