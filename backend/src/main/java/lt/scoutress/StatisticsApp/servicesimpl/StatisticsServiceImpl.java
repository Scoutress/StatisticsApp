package lt.scoutress.StatisticsApp.servicesimpl;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
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
    
    @Override
    @Scheduled(fixedRate = 3600000)
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
        System.out.println("Scheduled 1 is completed");
    }

    @Override
    @Transactional
    @Scheduled(fixedRate = 3600000)
    @DependsOn("calculateDaysSinceJoinAndSave")
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
        System.out.println("Scheduled 2 is completed");
    }

    @Override
    @Transactional
    @Scheduled(fixedRate = 3600000)
    @DependsOn("calculateTotalDailyMcTickets")
    public void calculateDailyTicketDifference() {
        @SuppressWarnings("unchecked")
        List<LocalDate> dates = entityManager.createNativeQuery(
                "SELECT DISTINCT date FROM mc_tickets_answered WHERE date > '2023-06-01'", LocalDate.class)
                .getResultList();

        for (int i = 1; i < dates.size(); i++) {
            LocalDate currentDay = dates.get(i);
            LocalDate previousDay = dates.get(i - 1);

            List<String> users = Arrays.asList("mboti212", "furija", "ernestasltu12", "d0fka", "melitalove",
                                                    "libete", "ariena", "sharans", "labashey", "everly", "richpica",
                                                    "shizo", "ievius", "bobsbuilder", "plrxq", "emsiukemiau");

            for (String user : users) {
                Long todayTickets = (Long) entityManager.createNativeQuery(
                        "SELECT COALESCE(" + user + "_mc_tickets, 0) FROM mc_tickets_answered WHERE date = :currentDay")
                        .setParameter("currentDay", currentDay)
                        .getSingleResult();
            
                Long yesterdayTickets = (Long) entityManager.createNativeQuery(
                        "SELECT COALESCE(" + user + "_mc_tickets, 0) FROM mc_tickets_answered WHERE date = :previousDay")
                        .setParameter("previousDay", previousDay)
                        .getSingleResult();
            
                Long ticketsDifference = todayTickets - yesterdayTickets;
            
                Number existingRecordCount = (Number) entityManager.createNativeQuery(
                    "SELECT COUNT(*) FROM mc_tickets_calculations WHERE date = :previousDay")
                    .setParameter("previousDay", previousDay)
                    .getSingleResult();

                if (existingRecordCount.intValue() == 0) {
                entityManager.createNativeQuery(
                        "INSERT INTO mc_tickets_calculations (date, " + user + "_daily) VALUES (:previousDay, :ticketsDifference)")
                        .setParameter("previousDay", previousDay)
                        .setParameter("ticketsDifference", ticketsDifference)
                        .executeUpdate();
                } else {
                entityManager.createNativeQuery(
                        "UPDATE mc_tickets_calculations SET " + user + "_daily = :ticketsDifference WHERE date = :previousDay")
                        .setParameter("ticketsDifference", ticketsDifference)
                        .setParameter("previousDay", previousDay)
                        .executeUpdate();
                }
            }
        }
        System.out.println("Scheduled 3 is completed");
    }

    @Override
    @Transactional
    @Scheduled(fixedRate = 3600000)
    @DependsOn("calculateDailyTicketDifference")
    public void calculateDailyTicketRatio(){

        @SuppressWarnings("unchecked")
        List<LocalDate> dates = entityManager.createNativeQuery(
                "SELECT DISTINCT date FROM mc_tickets_calculations WHERE date > '2023-06-01'", LocalDate.class)
                .getResultList();
        
        for (int i = 1; i < dates.size(); i++) {
            LocalDate currentDay = dates.get(i);

            List<String> users = Arrays.asList("mboti212", "furija", "ernestasltu12", "d0fka", "melitalove",
                                                    "libete", "ariena", "sharans", "labashey", "everly", "richpica",
                                                    "shizo", "ievius", "bobsbuilder", "plrxq", "emsiukemiau");

                for (String user : users) {
                    Long currentDayTicketsDaily = (Long) entityManager.createNativeQuery(
                        "SELECT COALESCE(" + user + "_daily, 0) FROM mc_tickets_calculations WHERE date = :currentDay")
                        .setParameter("currentDay", currentDay)
                        .getSingleResult();

                    Long currentDayTicketsSum = (Long) entityManager.createNativeQuery(
                        "SELECT COALESCE(daily_tickets_sum, 0) FROM mc_tickets_calculations WHERE date = :currentDay")
                        .setParameter("currentDay", currentDay)
                        .getSingleResult();

                    double roundedNumber = 0;

                    if (currentDayTicketsSum != 0) {
                        String ticketsRatioStr = Double.toString((double) currentDayTicketsDaily / currentDayTicketsSum);
                        if (ticketsRatioStr.contains(",")) {
                            ticketsRatioStr = ticketsRatioStr.replace(',', '.');
                        }

                        //DEBUG
                        if (ticketsRatioStr.contains(",")) {
                            System.out.println("");
                            System.out.println("Virs " + currentDayTicketsDaily);
                            System.out.println("Apac " + currentDayTicketsSum);
                            System.out.println(currentDay + " > " + ticketsRatioStr);
                            System.out.println("");
                        } else if (ticketsRatioStr.contains("-")){
                            System.out.println("");
                            System.out.println("Virs " + currentDayTicketsDaily);
                            System.out.println("Apac " + currentDayTicketsSum);
                            System.out.println(currentDay + " > " + ticketsRatioStr);
                            System.out.println("");
                        }
                        ////////////////////////////////////////////

                    //     double ticketsRatio = Double.parseDouble(ticketsRatioStr);
                    //     System.out.println(currentDay + " > " + ticketsRatio);//////////////////////

                    //     DecimalFormat df = new DecimalFormat("#.##");
                    //     roundedNumber = Double.parseDouble(df.format(ticketsRatio));
                    // }

                    // System.out.println(currentDay + " > " + roundedNumber);///////////////////////

                    // Double existingRecordCount = (Double) entityManager.createNativeQuery(
                    //     "SELECT COUNT(*) FROM mc_tickets_calculations WHERE date = :currentDay")
                    //     .setParameter("currentDay", currentDay)
                    //     .getSingleResult();

                    // if (existingRecordCount.intValue() == 0) {
                    // entityManager.createNativeQuery(
                    //     "INSERT INTO mc_tickets_calculations (date, " + user + "_ratio) VALUES (:currentDay, :roundedNumber)")
                    //     .setParameter("currentDay", currentDay)
                    //     .setParameter("roundedNumber", roundedNumber)
                    //     .executeUpdate();
                    // } else {
                    // entityManager.createNativeQuery(
                    //     "UPDATE mc_tickets_calculations SET " + user + "_ratio = :roundedNumber WHERE date = :currentDay")
                    //     .setParameter("roundedNumber", roundedNumber)
                    //     .setParameter("currentDay", currentDay)
                    //     .executeUpdate();
                    }
                }
        }
        System.out.println("Scheduled 4 is completed");
    }



}
