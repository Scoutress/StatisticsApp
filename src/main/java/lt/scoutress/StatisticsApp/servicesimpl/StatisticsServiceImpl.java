package lt.scoutress.StatisticsApp.servicesimpl;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lt.scoutress.StatisticsApp.entity.Employee;
import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsCalculations;
import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsCounting;
import lt.scoutress.StatisticsApp.repositories.EmployeeRepository;
import lt.scoutress.StatisticsApp.repositories.McTicketsRepository;
import lt.scoutress.StatisticsApp.services.EmployeeService;
import lt.scoutress.StatisticsApp.services.McTicketsService;
import lt.scoutress.StatisticsApp.services.StatisticsService;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private EntityManager entityManager;

    private final McTicketsRepository mcTicketsRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;
    private final McTicketsService mcTicketsService;

    public StatisticsServiceImpl(EntityManager entityManager, McTicketsRepository mcTicketsRepository,
            EmployeeRepository employeeRepository, EmployeeService employeeService, McTicketsService mcTicketsService) {
        this.entityManager = entityManager;
        this.mcTicketsRepository = mcTicketsRepository;
        this.employeeRepository = employeeRepository;
        this.employeeService = employeeService;
        this.mcTicketsService = mcTicketsService;
    }

    @Override
    public List<McTicketsCounting> findAllMcTickets() {
        return mcTicketsRepository.findAll();
    }

    @Override
    public void saveMcTickets(McTicketsCounting mcTickets) {
        mcTicketsRepository.save(mcTickets);
    }

    @Override
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
    @Transactional
    public void calculateTotalDailyMcTickets() {
        List<Employee> employees = employeeService.getAllEmployees();
        
        LocalDate oldestDate = mcTicketsService.getOldestDate();
        LocalDate newestDate = mcTicketsService.getNewestDate();
        LocalDate currentDate = oldestDate;

        while (!currentDate.isAfter(newestDate)) {
            List<String> usernames = new ArrayList<>();

            for (Employee employee : employees) {
                usernames.add(employee.getUsername());
            }

            LocalDate date = currentDate;

            double totalTicketsSum = 0.0;

            for (String username : usernames) {
                String lowercaseUsername = username.toLowerCase();

                if (mcTicketsService.columnExists(lowercaseUsername)) {
                    Double ticketsCount = mcTicketsService.getTicketsCountByUsernameAndDate(lowercaseUsername, currentDate);

                    if (ticketsCount != null) {
                        totalTicketsSum += ticketsCount;
                    }
                }
            }

            McTicketsCalculations mcTicketsCalculations = new McTicketsCalculations();
            mcTicketsCalculations.setDate(date);
            mcTicketsCalculations.setDailyTicketsSum(totalTicketsSum);

            mcTicketsService.saveMcTicketsCalculations(mcTicketsCalculations);

            currentDate = currentDate.plusDays(1);
        }
    }

    @Override
    @Transactional
    public void calculateDailyTicketRatio() {

        @SuppressWarnings("unchecked")
        List<LocalDate> dates = entityManager.createNativeQuery(
                "SELECT DISTINCT date FROM mc_tickets_calculations", LocalDate.class)
                .getResultList();

                for (int i = 0; i < dates.size(); i++){
                    LocalDate currentDay = dates.get(i);
        
                    List<Employee> employees = employeeService.getAllEmployees();

                    List<String> usernames = new ArrayList<>();

                    for (Employee employee : employees) {
                        usernames.add(employee.getUsername());
                    }

                    for (String username : usernames) {
                        Double currentDayTicketsDaily = (Double) entityManager.createNativeQuery(
                            "SELECT COALESCE(" + username + ", 0) FROM mc_tickets_count WHERE date = :currentDay")
                            .setParameter("currentDay", currentDay)
                            .getSingleResult();
                        Double currentDayTicketsSum = (Double) entityManager.createNativeQuery(
                            "SELECT COALESCE(daily_tickets_sum, 0) FROM mc_tickets_calculations WHERE date = :currentDay")
                            .setParameter("currentDay", currentDay)
                            .getSingleResult();
                        double ticketsRatioNumber = 0;

                double roundedTicketsRatio = 0;

                if (currentDayTicketsSum != null) {
                    double currentDayTicketsDailyDouble = currentDayTicketsDaily.doubleValue();
                    double currentDayTicketsSumDouble = currentDayTicketsSum.doubleValue();

                    ticketsRatioNumber = currentDayTicketsDailyDouble / currentDayTicketsSumDouble;

                    String roundedString = String.format(Locale.ENGLISH, "%.2f", ticketsRatioNumber);
                    roundedTicketsRatio = Double.parseDouble(roundedString);
                }

                Long existingRecordCount = (Long) entityManager.createNativeQuery(
                        "SELECT COUNT(*) FROM mc_tickets_calculations WHERE date = :currentDay")
                        .setParameter("currentDay", currentDay)
                        .getSingleResult();

                Double doubleExistingRecordCount = existingRecordCount.doubleValue();

                if (doubleExistingRecordCount.intValue() == 0) {
                    entityManager.createNativeQuery(
                            "INSERT INTO mc_tickets_calculations (date, " + username + "_ratio) VALUES (:currentDay, :roundedTicketsRatio)")
                            .setParameter("currentDay", currentDay)
                            .setParameter("roundedTicketsRatio", roundedTicketsRatio)
                            .executeUpdate();

                            if (roundedTicketsRatio > 1) {
                                System.out.println(username);
                                System.out.println(currentDay);
                                System.out.println(roundedTicketsRatio);
                            }
                            
                } else {
                    entityManager.createNativeQuery(
                            "UPDATE mc_tickets_calculations SET " + username + "_ratio = :roundedTicketsRatio WHERE date = :currentDay")
                            .setParameter("roundedTicketsRatio", roundedTicketsRatio)
                            .setParameter("currentDay", currentDay)
                            .executeUpdate();

                            if (roundedTicketsRatio > 1) {
                                System.out.println(username);
                                System.out.println(currentDay);
                                System.out.println(roundedTicketsRatio);
                            }
                }
            }
        }    
    }

    @Override
    @Transactional
    public void calculateTotalDailyDcMessages() {
        List<String> columnNames = Arrays.asList("mboti212_dc_messages", "furija_dc_messages", "ernestasltu12_dc_messages",
                "d0fka_dc_messages", "melitaLove_dc_messages", "libete_dc_messages", "ariena_dc_messages",
                "sharans_dc_messages", "labashey_dc_messages", "everly_dc_messages", "richPica_dc_messages",
                "shizo_dc_messages", "bobsBuilder_dc_messages", "plrxq_dc_messages",
                "emsiukemiau_dc_messages");
    
        StringBuilder queryBuilder = new StringBuilder("SELECT ");
        for (int i = 0; i < columnNames.size(); i++) {
            queryBuilder.append(columnNames.get(i));
            if (i < columnNames.size() - 1) {
                queryBuilder.append(", ");
            }
        }
        queryBuilder.append(", date FROM dc_messages_texted");
    
        @SuppressWarnings("unchecked")
        List<Object[]> results = entityManager.createNativeQuery(queryBuilder.toString()).getResultList();
    
        for (Object[] row : results) {
            Double sum = 0.0;
            for (int i = 0; i < row.length - 1; i++) {
                if (row[i] != null) {
                    sum += (Double) row[i];
                }
            }
    
            Date date = (Date) row[row.length - 1];
    
            Query checkQuery = entityManager.createNativeQuery("SELECT COUNT(*) FROM dc_messages_calc WHERE date = :date");
            checkQuery.setParameter("date", date);
            Long count = (Long) checkQuery.getSingleResult();

            if (count > 0) {
                entityManager.createNativeQuery(
                        "UPDATE dc_messages_calc SET daily_msg_sum = :sum WHERE date = :date")
                        .setParameter("sum", sum)
                        .setParameter("date", date)
                        .executeUpdate();
            } else {
                entityManager.createNativeQuery(
                        "INSERT INTO dc_messages_calc (daily_msg_sum, date) VALUES (:sum, :date)")
                        .setParameter("sum", sum)
                        .setParameter("date", date)
                        .executeUpdate();
            }
        }
    }

    @Override
    @Transactional
    public void calculateDailyDcMessagesRatio() {

        @SuppressWarnings("unchecked")
        List<LocalDate> dates = entityManager.createNativeQuery(
                "SELECT DISTINCT date FROM dc_messages_texted", LocalDate.class)
                .getResultList();

        for (int i = 0; i < dates.size(); i++) {
            LocalDate currentDay = dates.get(i);

            List<String> users = Arrays.asList("mboti212", "furija", "ernestasltu12", "d0fka", "melitalove",
                    "libete", "ariena", "sharans", "labashey", "everly", "richpica",
                    "shizo", "bobsbuilder", "plrxq", "emsiukemiau");

            for (String user : users) {
                Double currentDayDcMessagesDaily = (Double) entityManager.createNativeQuery(
                        "SELECT COALESCE(" + user + "_dc_messages, 0) FROM dc_messages_texted WHERE date = :currentDay")
                        .setParameter("currentDay", currentDay)
                        .getSingleResult();

                Double currentDayDcMessagesSum = (Double) entityManager.createNativeQuery(
                        "SELECT COALESCE(daily_msg_sum, 0) FROM dc_messages_calc WHERE date = :currentDay")
                        .setParameter("currentDay", currentDay)
                        .getSingleResult();

                double dcMessagesRatioNumber = 0;
                double roundedDcMessagesRatio = 0;

                if (currentDayDcMessagesSum != 0) {
                    double currentDayDcMessagesDailyDouble = currentDayDcMessagesDaily.doubleValue();
                    double currentDayDcMessagesSumDouble = currentDayDcMessagesSum.doubleValue();

                    dcMessagesRatioNumber = currentDayDcMessagesDailyDouble / currentDayDcMessagesSumDouble;

                    String roundedString = String.format(Locale.ENGLISH, "%.2f", dcMessagesRatioNumber);
                    roundedDcMessagesRatio = Double.parseDouble(roundedString);
                } else {
                    dcMessagesRatioNumber = 0;
                }

                Long existingRecordCount = (Long) entityManager.createNativeQuery(
                        "SELECT COUNT(*) FROM dc_messages_calc WHERE date = :currentDay")
                        .setParameter("currentDay", currentDay)
                        .getSingleResult();

                Double doubleExistingRecordCount = existingRecordCount.doubleValue();

                if (doubleExistingRecordCount.intValue() == 0) {
                    entityManager.createNativeQuery(
                            "INSERT INTO dc_messages_calc (date, " + user + "_dc_msg_calc) VALUES (:currentDay, :roundedDcMessagesRatio)")
                            .setParameter("currentDay", currentDay)
                            .setParameter("roundedDcMessagesRatio", roundedDcMessagesRatio)
                            .executeUpdate();
                } else {
                    entityManager.createNativeQuery(
                            "UPDATE dc_messages_calc SET " + user + "_dc_msg_calc = :roundedDcMessagesRatio WHERE date = :currentDay")
                            .setParameter("roundedDcMessagesRatio", roundedDcMessagesRatio)
                            .setParameter("currentDay", currentDay)
                            .executeUpdate();
                }
            }
        }
    }

    @Override
    @Transactional
    public void calculateAvgDailyDcMessages() {
        Query sumQuery = entityManager.createNativeQuery(
            "SELECT " +
                "SUM(COALESCE(mboti212_dc_messages, 0)) AS sum_mboti212_dc_messages, " +
                "SUM(COALESCE(furija_dc_messages, 0)) AS sum_furija_dc_messages, " +
                "SUM(COALESCE(ernestasltu12_dc_messages, 0)) AS sum_ernestasltu12_dc_messages, " +
                "SUM(COALESCE(d0fka_dc_messages, 0)) AS sum_d0fka_dc_messages, " +
                "SUM(COALESCE(melitalove_dc_messages, 0)) AS sum_melitalove_dc_messages, " +
                "SUM(COALESCE(libete_dc_messages, 0)) AS sum_libete_dc_messages, " +
                "SUM(COALESCE(ariena_dc_messages, 0)) AS sum_ariena_dc_messages, " +
                "SUM(COALESCE(sharans_dc_messages, 0)) AS sum_sharans_dc_messages, " +
                "SUM(COALESCE(labashey_dc_messages, 0)) AS sum_labashey_dc_messages, " +
                "SUM(COALESCE(everly_dc_messages, 0)) AS sum_everly_dc_messages, " +
                "SUM(COALESCE(richpica_dc_messages, 0)) AS sum_richpica_dc_messages, " +
                "SUM(COALESCE(shizo_dc_messages, 0)) AS sum_shizo_dc_messages, " +
                "SUM(COALESCE(bobsbuilder_dc_messages, 0)) AS sum_bobsbuilder_dc_messages, " +
                "SUM(COALESCE(plrxq_dc_messages, 0)) AS sum_plrxq_dc_messages, " +
                "SUM(COALESCE(emsiukemiau_dc_messages, 0)) AS sum_emsiukemiau_dc_messages " +
            "FROM dc_messages_texted"
        );

        Query countQuery = entityManager.createNativeQuery(
            "SELECT " +
                "COUNT(mboti212_dc_messages) AS total_count_mboti212, " +
                "COUNT(furija_dc_messages) AS total_count_furija_dc, " +
                "COUNT(ernestasltu12_dc_messages) AS total_count_ernestasltu12, " +
                "COUNT(d0fka_dc_messages) AS total_count_d0fka, " +
                "COUNT(melitalove_dc_messages) AS total_count_melitalove, " +
                "COUNT(libete_dc_messages) AS total_count_libete, " +
                "COUNT(ariena_dc_messages) AS total_count_ariena, " +
                "COUNT(sharans_dc_messages) AS total_count_sharans, " +
                "COUNT(labashey_dc_messages) AS total_count_labashey, " +
                "COUNT(everly_dc_messages) AS total_count_everly, " +
                "COUNT(richpica_dc_messages) AS total_count_richpica, " +
                "COUNT(shizo_dc_messages) AS total_count_shizo, " +
                "COUNT(bobsbuilder_dc_messages) AS total_count_bobsbuilder, " +
                "COUNT(plrxq_dc_messages) AS total_count_plrxq, " +
                "COUNT(emsiukemiau_dc_messages) AS total_count_emsiukemiau " +
            "FROM dc_messages_texted"
        );

        Object[] sums = (Object[]) sumQuery.getSingleResult();
        Object[] counts = (Object[])countQuery.getSingleResult();

        double[] longAverages = new double[sums.length];

        for (int i = 0; i < sums.length; i++) {
            if (counts[i] != null && (long) counts[i] != 0) {
                longAverages[i] = ((Number) sums[i]).doubleValue() / (long) counts[i];
            } else {
                longAverages[i] = 0;
            }
        }

        DecimalFormat df = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.ENGLISH));

        for (int i = 0; i < longAverages.length; i++) {
            double average = longAverages[i];
            String formattedAverage = df.format(average);
            longAverages[i] = Double.parseDouble(formattedAverage);
        }

        Query updateQuery = entityManager.createNativeQuery(
            "UPDATE productivity " +
            "SET dc_messages = " +
                "CASE " +
                    "WHEN username = 'Mboti212' THEN :avg_mboti212_dc_messages " +
                    "WHEN username = 'Furija' THEN :avg_furija_dc_messages " +
                    "WHEN username = 'Ernestasltu12' THEN :avg_ernestasltu12_dc_messages " +
                    "WHEN username = 'D0fka' THEN :avg_d0fka_dc_messages " +
                    "WHEN username = 'MelitaLove' THEN :avg_melitalove_dc_messages " +
                    "WHEN username = 'Libete' THEN :avg_libete_dc_messages " +
                    "WHEN username = 'Ariena' THEN :avg_ariena_dc_messages " +
                    "WHEN username = 'Sharans' THEN :avg_sharans_dc_messages " +
                    "WHEN username = 'labashey' THEN :avg_labashey_dc_messages " +
                    "WHEN username = 'everly' THEN :avg_everly_dc_messages " +
                    "WHEN username = 'RichPica' THEN :avg_richpica_dc_messages " +
                    "WHEN username = 'Shizo' THEN :avg_shizo_dc_messages " +
                    "WHEN username = 'BobsBuilder' THEN :avg_bobsbuilder_dc_messages " +
                    "WHEN username = 'plrxq' THEN :avg_plrxq_dc_messages " +
                    "WHEN username = 'Emsiukemiau' THEN :avg_emsiukemiau_dc_messages " +
                "END"
        );

        updateQuery.setParameter("avg_mboti212_dc_messages",        longAverages[0]);
        updateQuery.setParameter("avg_furija_dc_messages",          longAverages[1]);
        updateQuery.setParameter("avg_ernestasltu12_dc_messages",   longAverages[2]);
        updateQuery.setParameter("avg_d0fka_dc_messages",           longAverages[3]);
        updateQuery.setParameter("avg_melitalove_dc_messages",      longAverages[4]);
        updateQuery.setParameter("avg_libete_dc_messages",          longAverages[5]);
        updateQuery.setParameter("avg_ariena_dc_messages",          longAverages[6]);
        updateQuery.setParameter("avg_sharans_dc_messages",         longAverages[7]);
        updateQuery.setParameter("avg_labashey_dc_messages",        longAverages[8]);
        updateQuery.setParameter("avg_everly_dc_messages",          longAverages[9]);
        updateQuery.setParameter("avg_richpica_dc_messages",        longAverages[10]);
        updateQuery.setParameter("avg_shizo_dc_messages",           longAverages[11]);
        updateQuery.setParameter("avg_bobsbuilder_dc_messages",     longAverages[12]);
        updateQuery.setParameter("avg_plrxq_dc_messages",           longAverages[13]);
        updateQuery.setParameter("avg_emsiukemiau_dc_messages",     longAverages[14]);

        updateQuery.executeUpdate();
    }

    @Override
    @Transactional
    public void calculateAvgDailyDcMessagesRatio() {
        LocalDate oldestDate = LocalDate.of(2016, Month.JANUARY, 1);

        List<String> usernameList = Arrays.asList(
            "Mboti212", "Furija", "Ernestasltu12", "D0fka", "MelitaLove",
            "Libete", "Ariena", "Sharans", "labashey", "everly",
            "RichPica", "Shizo", "BobsBuilder", "plrxq",
            "Emsiukemiau"
        );

        for (String username : usernameList) {
            String usernameLower = username.toLowerCase();
            String avgQuerySql = String.format("SELECT AVG(COALESCE(%s_dc_msg_calc, 0)) AS avg_%s_dc_msg_calc FROM dc_messages_calc WHERE date >= :date", usernameLower, usernameLower);

            Query avgQuery = entityManager.createNativeQuery(avgQuerySql);
            avgQuery.setParameter("date", oldestDate);
            Double avgResultUnForm = ((Number) avgQuery.getSingleResult()).doubleValue();

            String formattedAvgResult = String.format(Locale.ENGLISH, "%.2f", avgResultUnForm * 100);

            if (formattedAvgResult.matches("\\d+\\.\\d{2}")) {
                Double avgResult = Double.parseDouble(formattedAvgResult);

                Query countQuery = entityManager.createNativeQuery("SELECT COUNT(*) FROM dc_messages_calc WHERE date >= :date");
                countQuery.setParameter("date", oldestDate);
                Long countResult = ((Number) countQuery.getSingleResult()).longValue();

                if (countResult.intValue() > 0) {
                    Query updateQuery = entityManager.createQuery(
                            "UPDATE Productivity p " +
                                    "SET p.dcMessagesComp = :avgDcMessagesComp " +
                                    "WHERE p.username = :username"
                    );

                    updateQuery.setParameter("avgDcMessagesComp", avgResult);
                    updateQuery.setParameter("username", username);
                    updateQuery.executeUpdate();
                } else {
                    System.out.println("There is not newer data than " + oldestDate + " from user " + username);
                }
            } else {
                System.out.println("Invalid average result format for user " + username + ": " + formattedAvgResult);
                continue;
            }
        }
    }

    @Override
    @Transactional
    public void calculateAvgDailyMcTickets() {
        Query usernameQuery = entityManager.createNativeQuery("SELECT username, join_date FROM employee");
        
        @SuppressWarnings("unchecked")
        List<Object[]> usernamesAndJoinDates = usernameQuery.getResultList();

        for (Object[] result : usernamesAndJoinDates) {
            String username = (String) result[0];
            java.sql.Date sqlDate = (java.sql.Date) result[1];
            LocalDate joinDate = sqlDate.toLocalDate();

            Query sumQuery = entityManager.createNativeQuery(
                "SELECT SUM(COALESCE(" + username.toLowerCase() + ", 0)) " +
                "FROM mc_tickets_count"
            );
            Double sum = (Double) sumQuery.getSingleResult();
            int sumValue = sum.intValue();

            LocalDate startDate = LocalDate.of(2023, Month.JUNE, 2);
            LocalDate endDate = LocalDate.now();

            if (joinDate.isAfter(startDate)) {
                startDate = joinDate;
            }

            long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);

            double average = (double) sumValue / daysBetween;

            String formattedAverage = String.format(Locale.ENGLISH, "%.2f", average);
            double finalAverage = Double.parseDouble(formattedAverage);

            Query updateQuery = entityManager.createNativeQuery(
                "UPDATE productivity " +
                "SET mc_tickets = :average " +
                "WHERE username = :username"
            );
            updateQuery.setParameter("average", finalAverage);
            updateQuery.setParameter("username", username);

            updateQuery.executeUpdate();
        }
    }

    //
}
