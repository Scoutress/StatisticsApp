package com.scoutress.KaimuxAdminStats.Servicesimpl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
    
        List<Integer> employeeIds = playtimeRepository.findAllDistinctEmployeeIds();
    
        for (Integer employeeId : employeeIds) {
            Double totalPlaytime = playtimeRepository.sumPlaytimeByEmployeeAndDateRange(employeeId, startDate, endDate);
            
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
    
            Productivity productivity;
            productivity = productivityRepository.findByEmployeeId(employeeId);
    
            if (productivity == null) {
                productivity = new Productivity();
                productivity.setEmployee(employee);
            }
            
            productivity.setAnnualPlaytime(totalPlaytime != null ? totalPlaytime : 0.0);
            productivityRepository.save(productivity);
        }
    }

    @Override
    public void updateAveragePlaytimeForAllEmployees() {
        List<Integer> employeeIds = playtimeRepository.findAllEmployeeIds();

        for (Integer employeeId : employeeIds) {
            LocalDate startDate = playtimeRepository.findEarliestPlaytimeDateByEmployeeId(employeeId);
            LocalDate endDate = playtimeRepository.findLatestPlaytimeDateByEmployeeId(employeeId);

            if (startDate != null && endDate != null) {
                long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
                Double totalPlaytime = playtimeRepository.sumPlaytimeByEmployeeAndDateRange(employeeId, startDate, endDate);

                if (totalPlaytime != null && daysBetween > 0) {
                    double averagePlaytime = totalPlaytime / daysBetween;

                    Productivity productivity = productivityRepository.findByEmployeeId(employeeId);

                    if (productivity == null) {
                        productivity = new Productivity(employeeRepository.findById(employeeId)
                                .orElseThrow(() -> new RuntimeException("Employee not found")));
                    }

                    productivity.setPlaytime(averagePlaytime);
                    productivityRepository.save(productivity);
                }
            }
        }
    }

    @Override
    public void updateAfkPlaytimeForAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        
        for (Employee employee : employees) {
            Integer employeeId = employee.getId();
            
            Double totalPlaytime = playtimeRepository.getTotalPlaytimeByEmployeeId(employeeId);
            Double totalAfkPlaytime = playtimeRepository.getTotalAfkPlaytimeByEmployeeId(employeeId);
    
            double afkPercentage = (totalPlaytime != null && totalPlaytime > 0) 
                                   ? (totalAfkPlaytime / totalPlaytime) * 100 
                                   : 0.0;
    
            Productivity productivity = productivityRepository.findByEmployeeId(employeeId);
            
            if (productivity == null) {
                productivity = new Productivity(employee);
            }
    
            productivity.setAfkPlaytime(afkPercentage);
    
            productivityRepository.save(productivity);
        }
    }
}
