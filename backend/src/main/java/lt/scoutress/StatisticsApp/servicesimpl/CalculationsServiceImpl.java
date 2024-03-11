package lt.scoutress.StatisticsApp.servicesimpl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lt.scoutress.StatisticsApp.entity.Calculations;
import lt.scoutress.StatisticsApp.entity.Employee;
import lt.scoutress.StatisticsApp.entity.McTickets;
import lt.scoutress.StatisticsApp.repositories.CalculationsRepository;
import lt.scoutress.StatisticsApp.repositories.EmployeeRepository;
import lt.scoutress.StatisticsApp.repositories.McTicketsRepository;
import lt.scoutress.StatisticsApp.services.CalculationsService;

@Service
public class CalculationsServiceImpl implements CalculationsService{

    private final CalculationsRepository calculationsRepository;
    private final EmployeeRepository employeeRepository;
    private final McTicketsRepository mcTicketsRepository;

    public CalculationsServiceImpl(CalculationsRepository calculationsRepository, EmployeeRepository employeeRepository, McTicketsRepository mcTicketsRepository){
        this.calculationsRepository = calculationsRepository;
        this.employeeRepository = employeeRepository;
        this.mcTicketsRepository = mcTicketsRepository;
    }

    @Override
    public List<Calculations> findCalculations() {
        return calculationsRepository.findAll();
    }

    @Override
    @Scheduled(fixedRate = 3600000) //Reloads once in hour
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

    @Override
    @Scheduled(fixedRate = 3600000) //Reloads once in hour
    public void calculateMcTicketsPerDay(){
        List<McTickets> mcTicketsWithNullInCount = mcTicketsRepository.findAll();
        for (McTickets mcTickets : mcTicketsWithNullInCount) {
            LocalDate tableDate = mcTickets.getDate();
            McTickets previousTableDay = mcTicketsRepository.findByDate(tableDate.minusDays(1));

            if (previousTableDay != null) {
                Integer mcTicketsAmount = mcTickets.getAmountOfTickets();
                Integer mcTicketsAmountDayBefore = previousTableDay.getAmountOfTickets();

                if (mcTicketsAmountDayBefore != null) {
                    mcTickets.setCountOfTickets(mcTicketsAmount - mcTicketsAmountDayBefore);
                    mcTicketsRepository.save(mcTickets);
                }
            }
        }
    }
    
}
