package com.scoutress.KaimuxAdminStats.DummyData;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.Employees.Employee;
import com.scoutress.KaimuxAdminStats.Entity.McTickets.McTickets;
import com.scoutress.KaimuxAdminStats.Repositories.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.Repositories.McTickets.McTicketsRepository;

import jakarta.transaction.Transactional;

@Service
public class DummyDataServiceImpl implements DummyDataService{

    private final EmployeeRepository employeeRepository;
    private final McTicketsRepository mcTicketsRepository;
    
    public DummyDataServiceImpl(EmployeeRepository employeeRepository, McTicketsRepository mcTicketsRepository) {
        this.employeeRepository = employeeRepository;
        this.mcTicketsRepository = mcTicketsRepository;
    }

    @Override
    @Transactional
    public void insertDummyMcTicketsData() {
        List<Employee> employees = employeeRepository.findAll();
        LocalDate today = LocalDate.now();
        for (Employee employee : employees) {
            Employee existingEmployee = employeeRepository.findById(employee.getId()).orElseThrow(() -> new RuntimeException("Employee not found: " + employee.getId()));
            for (int i = 0; i < 7; i++) {
                LocalDate date = today.minusDays(i);
                int mcTicketsCount = ThreadLocalRandom.current().nextInt(0, 15);
                McTickets mcTickets = new McTickets(existingEmployee, date, mcTicketsCount);
                mcTicketsRepository.save(mcTickets);
            }
        }
    }
}
