package lt.scoutress.StatisticsApp.servicesimpl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lt.scoutress.StatisticsApp.entity.Calculations;
import lt.scoutress.StatisticsApp.entity.Employee;
import lt.scoutress.StatisticsApp.repositories.CalculationsRepository;
import lt.scoutress.StatisticsApp.repositories.EmployeeRepository;
import lt.scoutress.StatisticsApp.services.CalculationsService;

@Service
public class CalculationsServiceImpl implements CalculationsService{

    private final CalculationsRepository calculationsRepository;
    private final EmployeeRepository employeeRepository;

    public CalculationsServiceImpl(CalculationsRepository calculationsRepository, EmployeeRepository employeeRepository){
        this.calculationsRepository = calculationsRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<Calculations> findCalculations() {
        return calculationsRepository.findAll();
    }

    @Override
    @Scheduled(fixedRate = 60000)
    public void calculateDaysSinceJoinAndSave() {
        LocalDate today = LocalDate.now();
        List<Employee> employees = employeeRepository.findAll();

        for (Employee employee : employees) {
            LocalDate joinDate = employee.getJoinDate();
            Long daysSinceJoinLong = ChronoUnit.DAYS.between(joinDate, today);
            int daysSinceJoin = Math.toIntExact(daysSinceJoinLong);
            employee.setDaysSinceJoin(daysSinceJoin);
            employeeRepository.save(employee);
        }
    }
    
}
