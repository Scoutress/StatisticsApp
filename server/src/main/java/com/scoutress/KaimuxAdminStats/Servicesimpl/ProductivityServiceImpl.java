package com.scoutress.KaimuxAdminStats.Servicesimpl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.Employee;
import com.scoutress.KaimuxAdminStats.Entity.Productivity;
import com.scoutress.KaimuxAdminStats.Repositories.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.Repositories.PlaytimeRepository;
import com.scoutress.KaimuxAdminStats.Repositories.ProductivityRepository;
import com.scoutress.KaimuxAdminStats.Services.ProductivityService;

@Service
public class ProductivityServiceImpl implements ProductivityService {

    private final ProductivityRepository productivityRepository;
    private final EmployeeRepository employeeRepository;
    private final PlaytimeRepository playtimeRepository;

    public ProductivityServiceImpl(ProductivityRepository productivityRepository, EmployeeRepository employeeRepository, PlaytimeRepository playtimeRepository) {
        this.productivityRepository = productivityRepository;
        this.employeeRepository = employeeRepository;
        this.playtimeRepository = playtimeRepository;
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

    @Override
    public void updateAnnualPlaytimeForAllEmployees() {
        LocalDate endDate = LocalDate.now().minusDays(1);
        LocalDate startDate = endDate.minusDays(365);

        List<Long> employeeIds = playtimeRepository.findAllDistinctEmployeeIds();

        for (Long employeeId : employeeIds) {
            Double totalPlaytime = playtimeRepository.sumPlaytimeByEmployeeAndDateRange(employeeId, startDate, endDate);
            
            Employee employee = employeeRepository.findById(employeeId.intValue())
                    .orElseThrow(() -> new RuntimeException("Employee not found"));

            Productivity productivity = productivityRepository.findByEmployeeId(employeeId)
                    .orElse(new Productivity(employee));

            productivity.setAnnualPlaytime(totalPlaytime != null ? totalPlaytime : 0.0);
            productivityRepository.save(productivity);
        }
    }
}
