package lt.scoutress.StatisticsApp.servicesimpl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lt.scoutress.StatisticsApp.entity.Calculations;
import lt.scoutress.StatisticsApp.entity.Employee;
import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsAnswered;
import lt.scoutress.StatisticsApp.repositories.CalculationsRepository;
import lt.scoutress.StatisticsApp.repositories.EmployeeRepository;
import lt.scoutress.StatisticsApp.repositories.McTicketsRepository;
import lt.scoutress.StatisticsApp.services.StatisticsService;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final CalculationsRepository calculationsRepository;
    private final EmployeeRepository employeeRepository;
    private final McTicketsRepository mcTicketsRepository;

    public StatisticsServiceImpl(CalculationsRepository calculationsRepository, EmployeeRepository employeeRepository, McTicketsRepository mcTicketsRepository){
        this.calculationsRepository = calculationsRepository;
        this.employeeRepository = employeeRepository;
        this.mcTicketsRepository = mcTicketsRepository;
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

    // @Override
    // @Scheduled(fixedRate = 3600000) //Reloads once in hour
    // public void calculateMcTicketsPerDay() {
    //     List<McTicketsAnswered> mcTicketsWithNullInCount = mcTicketsRepository.findAll();
    //     for (McTicketsAnswered mcTickets : mcTicketsWithNullInCount) {
    //         LocalDate tableDate = mcTickets.getDate();
    //         McTicketsAnswered previousTableDay = mcTicketsRepository.findByDate(tableDate.minusDays(1));

    //         if (previousTableDay != null) {
    //             Integer mcTicketsAmount = mcTickets.getAmountOfTickets();
    //             Integer mcTicketsAmountDayBefore = previousTableDay.getAmountOfTickets();

    //             if (mcTicketsAmountDayBefore != null) {
    //                 mcTickets.setCountOfTickets(mcTicketsAmount - mcTicketsAmountDayBefore);
    //                 mcTicketsRepository.save(mcTickets);
    //             }
    //         }
    //     }
    // }

    // @Scheduled(fixedRate = 3600000)
    // public void calculateAndSaveTotals() {
    //     List<McTicketsAnswered> tickets = findAllMcTickets();
        
    //     for (McTicketsAnswered ticket : tickets) {
    //         int total = ticket.getMboti212McTickets() + ticket.getFurijaMcTickets() + ticket.getErnestasltu12McTickets()
    //                 + ticket.getD0fkaMcTickets() + ticket.getMelitaLoveMcTickets() + ticket.getLibeteMcTickets()
    //                 + ticket.getArienaMcTickets() + ticket.getSharansMcTickets() + ticket.getLabasheyMcTickets()
    //                 + ticket.getEverlyMcTickets() + ticket.getRichPicaMcTickets() + ticket.getShizoMcTickets()
    //                 + ticket.getIeviusMcTickets() + ticket.getBobsBuilderMcTickets() + ticket.getPlrxqMcTickets()
    //                 + ticket.getEmsiukemiauMcTickets();
            
    //         ticket.setMcTicketsSum(total);
    //         saveMcTickets(ticket);
    //     }
    // }
}
