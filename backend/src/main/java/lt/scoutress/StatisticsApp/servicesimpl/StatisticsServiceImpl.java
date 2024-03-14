package lt.scoutress.StatisticsApp.servicesimpl;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
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
            Double sum = 0.0;
            for (int i = 0; i < row.length - 1; i++) {
                if (row[i] != null) {
                    sum += (Double) row[i];
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
    @Transactional
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
                Double todayTickets = (Double) entityManager.createNativeQuery(
                    "SELECT COALESCE(" + user + "_mc_tickets, 0) FROM mc_tickets_answered WHERE date = :currentDay")
                    .setParameter("currentDay", currentDay)
                    .getSingleResult();
            
                Double yesterdayTickets = (Double) entityManager.createNativeQuery(
                    "SELECT COALESCE(" + user + "_mc_tickets, 0) FROM mc_tickets_answered WHERE date = :previousDay")
                    .setParameter("previousDay", previousDay)
                    .getSingleResult();
            
                Double ticketsDifference = todayTickets - yesterdayTickets;
            
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
    }

    @Override
    @Transactional
    public void calculateDailyTicketRatio() {

        @SuppressWarnings("unchecked")
        List<LocalDate> dates = entityManager.createNativeQuery(
                "SELECT DISTINCT date FROM mc_tickets_calculations", LocalDate.class)
                .getResultList();

        for (int i = 0; i < dates.size(); i++) {
            LocalDate currentDay = dates.get(i);

            List<String> users = Arrays.asList("mboti212", "furija", "ernestasltu12", "d0fka", "melitalove",
                    "libete", "ariena", "sharans", "labashey", "everly", "richpica",
                    "shizo", "ievius", "bobsbuilder", "plrxq", "emsiukemiau");

            for (String user : users) {
                Double currentDayTicketsDaily = (Double) entityManager.createNativeQuery(
                        "SELECT COALESCE(" + user + "_daily, 0) FROM mc_tickets_calculations WHERE date = :currentDay")
                        .setParameter("currentDay", currentDay)
                        .getSingleResult();

                Double currentDayTicketsSum = (Double) entityManager.createNativeQuery(
                        "SELECT COALESCE(daily_tickets_sum, 0) FROM mc_tickets_calculations WHERE date = :currentDay")
                        .setParameter("currentDay", currentDay)
                        .getSingleResult();

                double ticketsRatioNumber = 0;
                double roundedTicketsRatio = 0;

                if (currentDayTicketsSum != 0) {
                    double currentDayTicketsDailyDouble = currentDayTicketsDaily.doubleValue();
                    double currentDayTicketsSumDouble = currentDayTicketsSum.doubleValue();

                    ticketsRatioNumber = currentDayTicketsDailyDouble / currentDayTicketsSumDouble;

                    String roundedString = String.format(Locale.ENGLISH, "%.2f", ticketsRatioNumber);
                    roundedTicketsRatio = Double.parseDouble(roundedString);
                } else {
                    ticketsRatioNumber = 0;
                }

                Long existingRecordCount = (Long) entityManager.createNativeQuery(
                        "SELECT COUNT(*) FROM mc_tickets_calculations WHERE date = :currentDay")
                        .setParameter("currentDay", currentDay)
                        .getSingleResult();

                Double doubleExistingRecordCount = existingRecordCount.doubleValue();

                if (doubleExistingRecordCount.intValue() == 0) {
                    entityManager.createNativeQuery(
                            "INSERT INTO mc_tickets_calculations (date, " + user + "_ratio) VALUES (:currentDay, :roundedTicketsRatio)")
                            .setParameter("currentDay", currentDay)
                            .setParameter("roundedTicketsRatio", roundedTicketsRatio)
                            .executeUpdate();
                } else {
                    entityManager.createNativeQuery(
                            "UPDATE mc_tickets_calculations SET " + user + "_ratio = :roundedTicketsRatio WHERE date = :currentDay")
                            .setParameter("roundedTicketsRatio", roundedTicketsRatio)
                            .setParameter("currentDay", currentDay)
                            .executeUpdate();
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
                "shizo_dc_messages", "ievius_dc_messages", "bobsBuilder_dc_messages", "plrxq_dc_messages",
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
                    "shizo", "ievius", "bobsbuilder", "plrxq", "emsiukemiau");

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
                "SUM(COALESCE(ievius_dc_messages, 0)) AS sum_ievius_dc_messages, " +
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
                "COUNT(ievius_dc_messages) AS total_count_ievius, " +
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
                    "WHEN username = 'Ievius' THEN :avg_ievius_dc_messages " +
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
        updateQuery.setParameter("avg_ievius_dc_messages",          longAverages[12]);
        updateQuery.setParameter("avg_bobsbuilder_dc_messages",     longAverages[13]);
        updateQuery.setParameter("avg_plrxq_dc_messages",           longAverages[14]);
        updateQuery.setParameter("avg_emsiukemiau_dc_messages",     longAverages[15]);

        updateQuery.executeUpdate();
    }

    //dc activity comp. - average daily messages ratio
}
