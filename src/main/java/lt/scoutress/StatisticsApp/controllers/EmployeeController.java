package lt.scoutress.StatisticsApp.Controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lt.scoutress.StatisticsApp.Repositories.EmployeeRepository;
import lt.scoutress.StatisticsApp.Services.EmployeeService;
import lt.scoutress.StatisticsApp.entity.Employees.Employee;

@Controller
@RequestMapping("/employees")
public class EmployeeController {
    
    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeService employeeService, EmployeeRepository employeeRepository) {
        this.employeeService = employeeService;
        this.employeeRepository = employeeRepository;
    }
    
    @GetMapping("/list")
    public String getAllEmployees(Model model) {
        List<Employee> employees = employeeService.findAll();
        model.addAttribute("employees", employees);
        return "employees/employee-list";
    }

    @GetMapping("/add")
    public String showAddEmployeeForm(Model model) {
        Employee employee = new Employee();
        model.addAttribute("employee", employee);
        return "employees/employee-add";
    }

    @PostMapping("/add")
    public String addEmployee(@ModelAttribute("employee") Employee employee) {
        employeeService.save(employee);
        return "redirect:/employees/personal";
    }

    @GetMapping("/personal")
    public String getAllEmployeesPersonalData(Model model) {
        List<Employee> employees = employeeService.findAll();
        model.addAttribute("employees", employees);
        return "employees/employee-personal-data";
    }

    @GetMapping("/edit/{id}")
    public String editEmployee(@PathVariable("id") Long id, Model model) {
        Employee employee = employeeRepository.findById(id).orElseThrow();
        model.addAttribute("employee", employee);
        return "employees/employee-form-edit";
    }

    @PostMapping("/edit/{id}")
    public String editEmployee(
            @PathVariable("id") long id,
            @RequestParam("username") String username,
            @RequestParam("level") String level,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("joinDate") LocalDate joinDate,
            @RequestParam("language") String language) {

        Employee employee = employeeRepository.findById(id).orElseThrow();
        employee.setUsername(username);
        employee.setLevel(level);
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setEmail(email);
        employee.setJoinDate(joinDate);
        employee.setLanguage(language);
        employeeRepository.save(employee);

        return "redirect:/employees/personal";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") int id){
        employeeService.deleteById(id);
        return "redirect:/employees/personal";
    }

    @GetMapping("/codes")
    public String getEmployeeCodes(ModelMap model) {
        List<Employee> employees = employeeRepository.findAll();
        model.addAttribute("employees", employees);
        return "employees/employee-db-codes";
    }

    @GetMapping("/codesUpdateForm")
    public String getEmployeeCodesForEdit(@RequestParam("id") Integer id, Model model) {
        Employee employee = employeeRepository.findById(id).orElseThrow();
        model.addAttribute("employee", employee);
        return "employees/employee-db-codes-edit";
    }

    @PostMapping("/employees/{id}/edit-codes")
    public String saveEditedEmployeeCodes(@ModelAttribute("employee") Employee employee, @PathVariable("id") Integer id) {
        employeeService.saveEmployeeCodes(employee);
        return "redirect:/employees/" + id;
    }
}
