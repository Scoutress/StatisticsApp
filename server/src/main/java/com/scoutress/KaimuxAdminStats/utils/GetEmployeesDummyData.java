package com.scoutress.KaimuxAdminStats.Utils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.scoutress.KaimuxAdminStats.Entity.Employees.Employee;
import com.scoutress.KaimuxAdminStats.Repositories.EmployeeRepository;

import jakarta.annotation.PostConstruct;

public class GetEmployeesDummyData {

    @Autowired
    private EmployeeRepository employeeRepository;

    @PostConstruct
    public void createDummyEmployees() {
        Employee employee1 = new Employee();
        employee1.setUsername("JohnDoe");
        employee1.setLevel("Owner");
        employee1.setLanguage("English");
        employee1.setFirstName("John");
        employee1.setLastName("Doe");
        employee1.setEmail("john.doe@example.com");
        employee1.setJoinDate(LocalDate.of(2020, 1, 15));

        Employee employee2 = new Employee();
        employee2.setUsername("JaneDoe");
        employee2.setLevel("Coder");
        employee2.setLanguage("English");
        employee2.setFirstName("Jane");
        employee2.setLastName("Doe");
        employee2.setEmail("jane.doe@example.com");
        employee2.setJoinDate(LocalDate.of(2021, 2, 20));

        Employee employee3 = new Employee();
        employee3.setUsername("BobSmith");
        employee3.setLevel("Manager");
        employee3.setLanguage("English");
        employee3.setFirstName("Bob");
        employee3.setLastName("Smith");
        employee3.setEmail("bob.smith@example.com");
        employee3.setJoinDate(LocalDate.of(2019, 3, 10));

        Employee employee4 = new Employee();
        employee4.setUsername("AliceJones");
        employee4.setLevel("Support");
        employee4.setLanguage("English");
        employee4.setFirstName("Alice");
        employee4.setLastName("Jones");
        employee4.setEmail("alice.jones@example.com");
        employee4.setJoinDate(LocalDate.of(2018, 4, 5));

        Employee employee5 = new Employee();
        employee5.setUsername("CharlieBrown");
        employee5.setLevel("Helper");
        employee5.setLanguage("English");
        employee5.setFirstName("Charlie");
        employee5.setLastName("Brown");
        employee5.setEmail("charlie.brown@example.com");
        employee5.setJoinDate(LocalDate.of(2022, 5, 25));

        List<Employee> employees = Arrays.asList(employee1, employee2, employee3, employee4, employee5);
        employeeRepository.saveAll(employees);
    }
}
