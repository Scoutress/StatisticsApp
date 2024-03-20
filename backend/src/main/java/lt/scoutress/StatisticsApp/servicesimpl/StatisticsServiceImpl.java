package lt.scoutress.StatisticsApp.servicesimpl;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.Month;
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
import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsCalculations;
import lt.scoutress.StatisticsApp.entity.McTickets.McTicketsCounting;
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
    public List<Calculations> findCalculations() {
        return calculationsRepository.findAll();
    }

    @Override
    public List<McTicketsCounting> findAllMcTickets() {
        return mcTicketsRepository.findAll();
    }

    @SuppressWarnings("null")
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
        Query query = entityManager.createQuery("SELECT m FROM McTicketsCounting m");

        @SuppressWarnings("unchecked")
        List<McTicketsCounting> mcTicketsCounts = query.getResultList();

        for (McTicketsCounting mcTicketsCount : mcTicketsCounts) {
            Double sum = 0.0;

            sum += (mcTicketsCount.getMboti212() != null) ? mcTicketsCount.getMboti212() : 0.0;
            sum += (mcTicketsCount.getFurija() != null) ? mcTicketsCount.getFurija() : 0.0;
            sum += (mcTicketsCount.getErnestasltu12() != null) ? mcTicketsCount.getErnestasltu12() : 0.0;
            sum += (mcTicketsCount.getD0fka() != null) ? mcTicketsCount.getD0fka() : 0.0;
            sum += (mcTicketsCount.getMelitalove() != null) ? mcTicketsCount.getMelitalove() : 0.0;
            sum += (mcTicketsCount.getLibete() != null) ? mcTicketsCount.getLibete() : 0.0;
            sum += (mcTicketsCount.getAriena() != null) ? mcTicketsCount.getAriena() : 0.0;
            sum += (mcTicketsCount.getSharans() != null) ? mcTicketsCount.getSharans() : 0.0;
            sum += (mcTicketsCount.getLabashey() != null) ? mcTicketsCount.getLabashey() : 0.0;
            sum += (mcTicketsCount.getEverly() != null) ? mcTicketsCount.getEverly() : 0.0;
            sum += (mcTicketsCount.getRichpica() != null) ? mcTicketsCount.getRichpica() : 0.0;
            sum += (mcTicketsCount.getShizo() != null) ? mcTicketsCount.getShizo() : 0.0;
            sum += (mcTicketsCount.getIevius() != null) ? mcTicketsCount.getIevius() : 0.0;
            sum += (mcTicketsCount.getBobsbuilder() != null) ? mcTicketsCount.getBobsbuilder() : 0.0;
            sum += (mcTicketsCount.getPlrxq() != null) ? mcTicketsCount.getPlrxq() : 0.0;
            sum += (mcTicketsCount.getEmsiukemiau() != null) ? mcTicketsCount.getEmsiukemiau() : 0.0;

            McTicketsCalculations mcTicketsCalculations = new McTicketsCalculations();
            mcTicketsCalculations.setId(mcTicketsCount.getId());
            mcTicketsCalculations.setDailyTicketsSum(sum);

            McTicketsCalculations existingCalculations = entityManager.find(McTicketsCalculations.class, mcTicketsCount.getId());
            if (existingCalculations != null) {
                existingCalculations.setDailyTicketsSum(sum);
                existingCalculations.setDate(mcTicketsCount.getDate());
            } else {
                entityManager.merge(mcTicketsCalculations);
            }
        }
    }

    @Override
    @Transactional
    public void calculateDailyTicketRatio() {
        Query countQuery = entityManager.createQuery("SELECT m FROM McTicketsCounting m");
        Query calculationsQuery = entityManager.createQuery("SELECT m FROM McTicketsCalculations m");

        @SuppressWarnings("unchecked")
        List<McTicketsCounting> mcTicketsCountings = countQuery.getResultList();

        @SuppressWarnings("unchecked")
        List<McTicketsCalculations> mcTicketsCalculations = calculationsQuery.getResultList();

        for (McTicketsCounting mcTicketsCounting : mcTicketsCountings) {
            for (McTicketsCalculations mcTicketsCalculation : mcTicketsCalculations) {
                double dailyTicketsSum = mcTicketsCalculation.getDailyTicketsSum();

                if (dailyTicketsSum != 0) {
                    double mboti212Ratio = mcTicketsCounting.getMboti212() / dailyTicketsSum;
                    double furijaRatio = mcTicketsCounting.getFurija() / dailyTicketsSum;
                    double ernestasltu12Ratio = mcTicketsCounting.getErnestasltu12() / dailyTicketsSum;
                    double d0fkaRatio = mcTicketsCounting.getD0fka() / dailyTicketsSum;
                    double melitaLoveRatio = mcTicketsCounting.getMelitalove() / dailyTicketsSum;
                    double libeteRatio = mcTicketsCounting.getLibete() / dailyTicketsSum;
                    double arienaRatio = mcTicketsCounting.getAriena() / dailyTicketsSum;
                    double sharansRatio = mcTicketsCounting.getSharans() / dailyTicketsSum;
                    double labasheyRatio = mcTicketsCounting.getLabashey() / dailyTicketsSum;
                    double everlyRatio = mcTicketsCounting.getEverly() / dailyTicketsSum;
                    double richPicaRatio = mcTicketsCounting.getRichpica() / dailyTicketsSum;
                    double shizoRatio = mcTicketsCounting.getShizo() / dailyTicketsSum;
                    double ieviusRatio = mcTicketsCounting.getIevius() / dailyTicketsSum;
                    double bobsBuilderRatio = mcTicketsCounting.getBobsbuilder() / dailyTicketsSum;
                    double plrxqRatio = mcTicketsCounting.getPlrxq() / dailyTicketsSum;
                    double emsiukemiauRatio = mcTicketsCounting.getEmsiukemiau() / dailyTicketsSum;

                    mboti212Ratio = Math.round(mboti212Ratio * 100.0) / 100.0;
                    furijaRatio = Math.round(furijaRatio * 100.0) / 100.0;
                    ernestasltu12Ratio = Math.round(ernestasltu12Ratio * 100.0) / 100.0;
                    d0fkaRatio = Math.round(d0fkaRatio * 100.0) / 100.0;
                    melitaLoveRatio = Math.round(melitaLoveRatio * 100.0) / 100.0;
                    libeteRatio = Math.round(libeteRatio * 100.0) / 100.0;
                    arienaRatio = Math.round(arienaRatio * 100.0) / 100.0;
                    sharansRatio = Math.round(sharansRatio * 100.0) / 100.0;
                    labasheyRatio = Math.round(labasheyRatio * 100.0) / 100.0;
                    everlyRatio = Math.round(everlyRatio * 100.0) / 100.0;
                    richPicaRatio = Math.round(richPicaRatio * 100.0) / 100.0;
                    shizoRatio = Math.round(shizoRatio * 100.0) / 100.0;
                    ieviusRatio = Math.round(ieviusRatio * 100.0) / 100.0;
                    bobsBuilderRatio = Math.round(bobsBuilderRatio * 100.0) / 100.0;
                    plrxqRatio = Math.round(plrxqRatio * 100.0) / 100.0;
                    emsiukemiauRatio = Math.round(emsiukemiauRatio * 100.0) / 100.0;

                    mcTicketsCalculation.setMboti212Ratio(mboti212Ratio);
                    mcTicketsCalculation.setFurijaRatio(furijaRatio);
                    mcTicketsCalculation.setErnestasltu12Ratio(ernestasltu12Ratio);
                    mcTicketsCalculation.setD0fkaRatio(d0fkaRatio);
                    mcTicketsCalculation.setMelitaLoveRatio(melitaLoveRatio);
                    mcTicketsCalculation.setLibeteRatio(libeteRatio);
                    mcTicketsCalculation.setArienaRatio(arienaRatio);
                    mcTicketsCalculation.setSharansRatio(sharansRatio);
                    mcTicketsCalculation.setLabasheyRatio(labasheyRatio);
                    mcTicketsCalculation.setEverlyRatio(everlyRatio);
                    mcTicketsCalculation.setRichPicaRatio(richPicaRatio);
                    mcTicketsCalculation.setShizoRatio(shizoRatio);
                    mcTicketsCalculation.setIeviusRatio(ieviusRatio);
                    mcTicketsCalculation.setBobsBuilderRatio(bobsBuilderRatio);
                    mcTicketsCalculation.setPlrxqRatio(plrxqRatio);
                    mcTicketsCalculation.setEmsiukemiauRatio(emsiukemiauRatio);
                    
                    entityManager.merge(mcTicketsCalculation);
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

    @Override
    @Transactional
    public void calculateAvgDailyDcMessagesRatio() {
        LocalDate oldestDate = LocalDate.of(2016, Month.JANUARY, 1);

        List<String> usernameList = Arrays.asList(
            "Mboti212", "Furija", "Ernestasltu12", "D0fka", "MelitaLove",
            "Libete", "Ariena", "Sharans", "labashey", "everly",
            "RichPica", "Shizo", "Ievius", "BobsBuilder", "plrxq",
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
}
