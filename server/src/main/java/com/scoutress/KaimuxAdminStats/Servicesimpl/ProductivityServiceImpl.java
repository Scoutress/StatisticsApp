package com.scoutress.KaimuxAdminStats.Servicesimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.Employee;
import com.scoutress.KaimuxAdminStats.Entity.Productivity;
import com.scoutress.KaimuxAdminStats.Repositories.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.Repositories.ProductivityRepository;
import com.scoutress.KaimuxAdminStats.Services.ProductivityService;

@Service
public class ProductivityServiceImpl implements ProductivityService {

    private final ProductivityRepository productivityRepository;
    private final EmployeeRepository employeeRepository;

    public ProductivityServiceImpl(ProductivityRepository productivityRepository, EmployeeRepository employeeRepository) {
        this.productivityRepository = productivityRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<Productivity> findAll() {
        return productivityRepository.findAllWithEmployeeDetails();
    }

    @Override
    public void updateProductivityData() {
        List<Employee> employees = employeeRepository.findAll();

        for (Employee employee : employees) {
            Productivity existingProductivity = productivityRepository.findByEmployeeId(employee.getId());

            if(existingProductivity == null){
                Productivity productivity = new Productivity();
            productivity.setEmployee(employee);
            productivity.setAnnualPlaytime(null);
            productivity.setServerTickets(null);
            productivity.setServerTicketsTaking(null);
            productivity.setDiscordTickets(null);
            productivity.setDiscordTicketsTaking(null);
            productivity.setPlaytime(null);
            productivity.setAfkPlaytime(null);
            productivity.setProductivity(null);
            productivity.setRecommendation(null);
            productivityRepository.save(productivity);
            }           
        }
    }
}
