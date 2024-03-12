package lt.scoutress.StatisticsApp.servicesimpl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lt.scoutress.StatisticsApp.entity.Calculations;
import lt.scoutress.StatisticsApp.entity.Employee;
import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsAnswered;
import lt.scoutress.StatisticsApp.repositories.CalculationsRepository;
import lt.scoutress.StatisticsApp.repositories.EmployeeRepository;
import lt.scoutress.StatisticsApp.repositories.McTicketsRepository;
import lt.scoutress.StatisticsApp.services.StatisticsService;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private EntityManager entityManager;

    private final CalculationsRepository calculationsRepository;
    private final McTicketsRepository mcTicketsRepository;
    private final EmployeeRepository employeeRepository;

    public StatisticsServiceImpl(CalculationsRepository calculationsRepository, McTicketsRepository mcTicketsRepository, EmployeeRepository employeeRepository){
        this.calculationsRepository = calculationsRepository;
        this.mcTicketsRepository = mcTicketsRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public String showForm() {
        return null;
    }

    @Override
    public List<McTicketsAnswered> findAllMcTickets() {
        return mcTicketsRepository.findAll();
    }

    @SuppressWarnings("null")
    @Override
    public void saveMcTickets(McTicketsAnswered mcTickets) {
        mcTicketsRepository.save(mcTickets);
    }

    @Override
    public List<Calculations> findCalculations() {
        return calculationsRepository.findAll();
    }

    @Scheduled(fixedRate = 3600000)
    @Override
    @Transactional
    public void calculateTotalDailyMcTickets() {
        List<String> columnNames = Arrays.asList("mboti212_daily", "furija_daily", "ernestasltu12_daily", 
                                                "d0fka_daily", "melitaLove_daily", "libete_daily", 
                                                "ariena_daily", "sharans_daily", "labashey_daily", 
                                                "everly_daily", "richPica_daily", "shizo_daily", 
                                                "ievius_daily", "bobsBuilder_daily", "plrxq_daily", 
                                                "emsiukemiau_daily");
    
        StringBuilder queryBuilder = new StringBuilder("SELECT ");
        for (int i = 0; i < columnNames.size(); i++) {
            queryBuilder.append(columnNames.get(i));
            if (i < columnNames.size() - 1) {
                queryBuilder.append(", ");
            }
        }
        queryBuilder.append(", date FROM mc_tickets_calculations");
    
        @SuppressWarnings("unchecked")
        List<Object[]> results = entityManager.createNativeQuery(queryBuilder.toString()).getResultList();
    
        for (Object[] row : results) {
            Integer sum = 0;
            for (int i = 0; i < row.length - 1; i++) {
                if (row[i] != null) {
                    sum += (Integer) row[i];
                }
            }
          
            entityManager.createNativeQuery(
                "UPDATE mc_tickets_calculations SET daily_tickets_sum = :sum WHERE date = :date"
            )
            .setParameter("sum", sum)
            .setParameter("date", row[row.length - 1])
            .executeUpdate();
    
        }
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
