package lt.scoutress.StatisticsApp.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lt.scoutress.StatisticsApp.entity.Employee;
import lt.scoutress.StatisticsApp.servicesimpl.EmployeeServiceImpl;

@Controller
@RequestMapping("/employee")
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

    @GetMapping("/new")
    public String showEmployeeForm(Model model) {
        model.addAttribute("newEmployee", new Employee());
        return "employee-new-form";
    }

    @PostMapping("/new")
    public String addEmployee(@ModelAttribute("employee") Employee employee) {
        employeeService.addEmployee(employee);
        return "redirect:/employee/list";
    }

}
