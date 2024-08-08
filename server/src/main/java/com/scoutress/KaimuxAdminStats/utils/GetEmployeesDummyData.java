// package com.scoutress.KaimuxAdminStats.utils;

// import java.time.LocalDate;
// import java.util.Arrays;
// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Component;

// import com.scoutress.KaimuxAdminStats.Repositories.EmployeeRepository;
// import com.scoutress.KaimuxAdminStats.entity.Employees.Employee;

// @Component
// public class GetEmployeesDummyData {

//     @Autowired
//     private EmployeeRepository employeeRepository;

//     public void createDummyEmployees() {
//         Employee employee1 = new Employee("JohnDoe", "Owner", "English", "John", "Doe", "john.doe@example.com", LocalDate.of(2020, 1, 15));
//         Employee employee2 = new Employee("JaneDoe", "Coder", "English", "Jane", "Doe", "jane.doe@example.com", LocalDate.of(2021, 2, 20));
//         Employee employee3 = new Employee("BobSmith", "Manager", "English", "Bob", "Smith", "bob.smith@example.com", LocalDate.of(2019, 3, 10));
//         Employee employee4 = new Employee("AliceJones", "Support", "English", "Alice", "Jones", "alice.jones@example.com", LocalDate.of(2018, 4, 5));
//         Employee employee5 = new Employee("CharlieBrown", "Helper", "English", "Charlie", "Brown", "charlie.brown@example.com", LocalDate.of(2022, 5, 25));

//         List<Employee> employees = Arrays.asList(employee1, employee2, employee3, employee4, employee5);
//         employeeRepository.saveAll(employees);
//     }
// }
