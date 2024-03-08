package lt.scoutress.StatisticsApp.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lt.scoutress.StatisticsApp.entity.Employee;
import lt.scoutress.StatisticsApp.services.EmployeeServiceImpl;

@Controller
@RequestMapping("/api/employees")
public class EmployeeController {
    
    private final EmployeeServiceImpl employeeService;
    
    public EmployeeController(EmployeeServiceImpl employeeService) {
        this.employeeService = employeeService;
    }
    
    @GetMapping("/list")
    public String getAllEmployees(Model model) {
        List<Employee> employees = employeeService.getAllEmployees();
        model.addAttribute("employees", employees);
        return "employee-list";
    }
}
