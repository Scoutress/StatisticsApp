package com.scoutress.KaimuxAdminStats.Servicesimpl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Constants.CalculationConstants;
import com.scoutress.KaimuxAdminStats.Entity.Employee;
import com.scoutress.KaimuxAdminStats.Entity.Productivity;
import com.scoutress.KaimuxAdminStats.Entity.ProductivityCalc;
import com.scoutress.KaimuxAdminStats.Repositories.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.Repositories.PlaytimeRepository;
import com.scoutress.KaimuxAdminStats.Repositories.ProductivityCalcRepository;
import com.scoutress.KaimuxAdminStats.Repositories.ProductivityRepository;
import com.scoutress.KaimuxAdminStats.Services.ProductivityService;

@Service
public class ProductivityServiceImpl implements ProductivityService {

    private final ProductivityRepository productivityRepository;
    private final EmployeeRepository employeeRepository;
    private final PlaytimeRepository playtimeRepository;
    private final ProductivityCalcRepository productivityCalcRepository;

    public ProductivityServiceImpl(ProductivityRepository productivityRepository, 
                                    EmployeeRepository employeeRepository, 
                                    PlaytimeRepository playtimeRepository,
                                    ProductivityCalcRepository productivityCalcRepository) {
        this.productivityRepository = productivityRepository;
        this.employeeRepository = employeeRepository;
        this.playtimeRepository = playtimeRepository;
        this.productivityCalcRepository = productivityCalcRepository;
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

    @Override
    public void calculateServerTicketsForAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();

        for (Employee employee : employees) {
            ProductivityCalc productivityCalc = productivityCalcRepository.findByEmployeeId(employee.getId());

            if (productivityCalc == null) {
                productivityCalc = new ProductivityCalc();
                productivityCalc.setEmployee(employee);
            }

            double serverTickets = productivityRepository.findServerTicketsByEmployeeId(employee.getId());
            double calculatedValue;

            switch (employee.getLevel()) {
                case "Helper" -> {
                    calculatedValue = 0.0;
                    break;
                }
                case "Support" -> {
                    if (serverTickets > 0.5) {
                        calculatedValue = 0.5 * CalculationConstants.SERVER_TICKETS_SUPPORT;
                    } else {
                        calculatedValue = serverTickets * CalculationConstants.SERVER_TICKETS_SUPPORT;
                    }
                    break;
                }
                case "Chatmod" -> {
                    if (serverTickets > 1.0) {
                        calculatedValue = 1.0 * CalculationConstants.SERVER_TICKETS_CHATMOD;
                    } else {
                        calculatedValue = serverTickets * CalculationConstants.SERVER_TICKETS_CHATMOD;
                    }
                    break;
                }
                case "Overseer" -> {
                    if (serverTickets > 2.0) {
                        calculatedValue = 2.0 * CalculationConstants.SERVER_TICKETS_OVERSEER;
                    } else {
                        calculatedValue = serverTickets * CalculationConstants.SERVER_TICKETS_OVERSEER;
                    }
                    break;
                }
                case "Organizer" -> {
                    if (serverTickets > 2.0) {
                        calculatedValue = 2.0 * CalculationConstants.SERVER_TICKETS_ORGANIZER;
                    } else {
                        calculatedValue = serverTickets * CalculationConstants.SERVER_TICKETS_ORGANIZER;
                    }
                    break;
                }
                case "Manager" -> {
                    if (serverTickets > 4.0) {
                        calculatedValue = 4.0 * CalculationConstants.SERVER_TICKETS_MANAGER;
                    } else {
                        calculatedValue = serverTickets * CalculationConstants.SERVER_TICKETS_MANAGER;
                    }
                    break;
                }
                default -> calculatedValue = 0.0;
            }
            
            productivityCalc.setServerTicketsCalc(calculatedValue);
            productivityCalcRepository.save(productivityCalc);
        }
    }
    
}
