package com.scoutress.KaimuxAdminStats.Servicesimpl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Constants.CalculationConstants;
import com.scoutress.KaimuxAdminStats.Entity.Employee;
import com.scoutress.KaimuxAdminStats.Entity.Productivity;
import com.scoutress.KaimuxAdminStats.Entity.ProductivityCalc;
import com.scoutress.KaimuxAdminStats.Repositories.DcTickets.DcTicketRepository;
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
    private final DcTicketRepository dcTicketRepository;

    public ProductivityServiceImpl(ProductivityRepository productivityRepository,
            EmployeeRepository employeeRepository,
            PlaytimeRepository playtimeRepository,
            ProductivityCalcRepository productivityCalcRepository,
            DcTicketRepository dcTicketRepository) {
        this.productivityRepository = productivityRepository;
        this.employeeRepository = employeeRepository;
        this.playtimeRepository = playtimeRepository;
        this.productivityCalcRepository = productivityCalcRepository;
        this.dcTicketRepository = dcTicketRepository;
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

            if (existingProductivity == null) {
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
                Double totalPlaytime = playtimeRepository.sumPlaytimeByEmployeeAndDateRange(employeeId, startDate,
                        endDate);

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
    public void calculateServerTicketsForAllEmployeesWithCoefs() {
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

    @Override
    public void calculateServerTicketsTakenForAllEmployeesWithCoefs() {
        List<Employee> employees = employeeRepository.findAll();

        for (Employee employee : employees) {
            ProductivityCalc productivityCalc = productivityCalcRepository.findByEmployeeId(employee.getId());

            if (productivityCalc == null) {
                productivityCalc = new ProductivityCalc();
                productivityCalc.setEmployee(employee);
            }

            double serverTicketsTaken = productivityRepository.findServerTicketsTakenByEmployeeId(employee.getId());
            double calculatedValue;

            switch (employee.getLevel()) {
                case "Support" -> {
                    if (serverTicketsTaken > 20.0) {
                        calculatedValue = 20.0 * CalculationConstants.SERVER_TICKETS_PERC_SUPPORT;
                    } else {
                        calculatedValue = serverTicketsTaken * CalculationConstants.SERVER_TICKETS_PERC_SUPPORT;
                    }
                    break;
                }
                case "Chatmod" -> {
                    if (serverTicketsTaken > 40.0) {
                        calculatedValue = 40.0 * CalculationConstants.SERVER_TICKETS_PERC_CHATMOD;
                    } else {
                        calculatedValue = serverTicketsTaken * CalculationConstants.SERVER_TICKETS_PERC_CHATMOD;
                    }
                    break;
                }
                case "Overseer" -> {
                    if (serverTicketsTaken > 85.0) {
                        calculatedValue = 85.0 * CalculationConstants.SERVER_TICKETS_PERC_OVERSEER;
                    } else {
                        calculatedValue = serverTicketsTaken * CalculationConstants.SERVER_TICKETS_PERC_OVERSEER;
                    }
                    break;
                }
                case "Organizer" -> {
                    if (serverTicketsTaken > 85.0) {
                        calculatedValue = 85.0 * CalculationConstants.SERVER_TICKETS_PERC_ORGANIZER;
                    } else {
                        calculatedValue = serverTicketsTaken * CalculationConstants.SERVER_TICKETS_PERC_ORGANIZER;
                    }
                    break;
                }
                case "Manager" -> {
                    if (serverTicketsTaken > 100.0) {
                        calculatedValue = 100.0 * CalculationConstants.SERVER_TICKETS_PERC_MANAGER;
                    } else {
                        calculatedValue = serverTicketsTaken * CalculationConstants.SERVER_TICKETS_PERC_MANAGER;
                    }
                    break;
                }
                default -> calculatedValue = 0.0;
            }

            productivityCalc.setServerTicketsTakingCalc(calculatedValue);
            productivityCalcRepository.save(productivityCalc);
        }
    }

    @Override
    public void calculatePlaytimeForAllEmployeesWithCoefs() {
        List<Employee> employees = employeeRepository.findAll();

        for (Employee employee : employees) {
            ProductivityCalc productivityCalc = productivityCalcRepository.findByEmployeeId(employee.getId());

            if (productivityCalc == null) {
                productivityCalc = new ProductivityCalc();
                productivityCalc.setEmployee(employee);
            }

            double playtime = productivityRepository.findPlaytimeByEmployeeId(employee.getId());
            double calculatedValue;

            switch (employee.getLevel()) {
                case "Helper" -> {
                    if (playtime > 0.5) {
                        calculatedValue = 0.5 * CalculationConstants.PLAYTIME_HELPER;
                    } else {
                        calculatedValue = playtime * CalculationConstants.PLAYTIME_SUPPORT;
                    }
                    break;
                }
                case "Support" -> {
                    if (playtime > 1.0) {
                        calculatedValue = 1.0 * CalculationConstants.PLAYTIME_SUPPORT;
                    } else {
                        calculatedValue = playtime * CalculationConstants.PLAYTIME_SUPPORT;
                    }
                    break;
                }
                case "Chatmod" -> {
                    if (playtime > 2.0) {
                        calculatedValue = 2.0 * CalculationConstants.PLAYTIME_CHATMOD;
                    } else {
                        calculatedValue = playtime * CalculationConstants.PLAYTIME_CHATMOD;
                    }
                    break;
                }
                case "Overseer" -> {
                    if (playtime > 4.0) {
                        calculatedValue = 4.0 * CalculationConstants.PLAYTIME_OVERSEER;
                    } else {
                        calculatedValue = playtime * CalculationConstants.PLAYTIME_OVERSEER;
                    }
                    break;
                }
                case "Organizer" -> {
                    if (playtime > 4.0) {
                        calculatedValue = 4.0 * CalculationConstants.PLAYTIME_ORGANIZER;
                    } else {
                        calculatedValue = playtime * CalculationConstants.PLAYTIME_ORGANIZER;
                    }
                    break;
                }
                case "Manager" -> {
                    if (playtime > 8.0) {
                        calculatedValue = 8.0 * CalculationConstants.PLAYTIME_MANAGER;
                    } else {
                        calculatedValue = playtime * CalculationConstants.PLAYTIME_MANAGER;
                    }
                    break;
                }
                default -> calculatedValue = 0.0;
            }

            productivityCalc.setPlaytimeCalc(calculatedValue);
            productivityCalcRepository.save(productivityCalc);
        }
    }

    @Override
    public void calculateAfkPlaytimeForAllEmployeesWithCoefs() {
        boolean isActive = false;

        List<Employee> employees = employeeRepository.findAll();

        for (Employee employee : employees) {
            ProductivityCalc productivityCalc = productivityCalcRepository.findByEmployeeId(employee.getId());

            if (productivityCalc == null) {
                productivityCalc = new ProductivityCalc();
                productivityCalc.setEmployee(employee);
            }

            double afkPlaytime = productivityRepository.findAfkPlaytimeByEmployeeId(employee.getId());
            double calculatedValue;

            switch (employee.getLevel()) {
                case "Helper" -> {
                    calculatedValue = afkPlaytime * CalculationConstants.AFK_PLAYTIME_SUPPORT;
                    break;
                }
                case "Support" -> {
                    calculatedValue = afkPlaytime * CalculationConstants.AFK_PLAYTIME_SUPPORT;
                    break;
                }
                case "Chatmod" -> {
                    calculatedValue = afkPlaytime * CalculationConstants.AFK_PLAYTIME_CHATMOD;
                    break;
                }
                case "Overseer" -> {
                    calculatedValue = afkPlaytime * CalculationConstants.AFK_PLAYTIME_OVERSEER;
                    break;
                }
                case "Organizer" -> {
                    calculatedValue = afkPlaytime * CalculationConstants.AFK_PLAYTIME_ORGANIZER;
                    break;
                }
                case "Manager" -> {
                    calculatedValue = afkPlaytime * CalculationConstants.AFK_PLAYTIME_MANAGER;
                    break;
                }
                default -> calculatedValue = 0.0;
            }

            if (calculatedValue > 100.0) {
                calculatedValue = 100.0;
            }

            if (!isActive) {
                calculatedValue = 1.0;
            }

            productivityCalc.setAfkPlaytimeCalc(calculatedValue);
            productivityCalcRepository.save(productivityCalc);
        }
    }

    @Override
    public void calculateAnsweredDiscordTicketsWithCoefs() {
        System.out.println("Starting calculation of average answered Discord tickets with coefficients.");

        List<Employee> employees = employeeRepository.findAll();
        List<LocalDate> dates = dcTicketRepository.findAllDates();

        for (Employee employee : employees) {
            System.out.println(
                    "----- Processing employee: " + employee.getUsername() + " (ID: " + employee.getId() + ") -----");

            double totalResult = 0.0;
            int daysWithTickets = 0;

            for (LocalDate date : dates) {
                System.out.println("Processing date: " + date);

                double allDiscordTickets = dcTicketRepository.sumByDate(date);
                System.out.println("Total Discord tickets for " + date + ": " + allDiscordTickets);

                if (allDiscordTickets == 0) {
                    System.out.println("No Discord tickets for this date. Skipping date: " + date);
                    continue;
                }

                double discordTickets = dcTicketRepository
                        .findAnsweredDiscordTicketsByEmployeeIdAndDate(employee.getId(), date);
                System.out.println("Answered Discord tickets for employee on " + date + ": " + discordTickets);

                double employeeLevelCoefficient;
                switch (employee.getLevel()) {
                    case "Support" -> employeeLevelCoefficient = CalculationConstants.DISCORD_TICKETS_SUPPORT;
                    case "Chatmod" -> employeeLevelCoefficient = CalculationConstants.DISCORD_TICKETS_CHATMOD;
                    case "Overseer" -> employeeLevelCoefficient = CalculationConstants.DISCORD_TICKETS_OVERSEER;
                    case "Organizer" -> employeeLevelCoefficient = CalculationConstants.DISCORD_TICKETS_ORGANIZER;
                    case "Manager" -> employeeLevelCoefficient = CalculationConstants.DISCORD_TICKETS_MANAGER;
                    default -> {
                        employeeLevelCoefficient = 0.0;
                        System.out.println(
                                "Unknown employee level for " + employee.getUsername() + ": " + employee.getLevel());
                    }
                }
                System.out.println("Employee level coefficient: " + employeeLevelCoefficient);

                double allDailyTicketsThisLevel = allDiscordTickets * employeeLevelCoefficient;
                System.out.println("Total daily tickets for this level: " + allDailyTicketsThisLevel);

                double result = 0.0;
                if (allDailyTicketsThisLevel != 0 && allDiscordTickets != 0
                        && !Double.isInfinite(allDailyTicketsThisLevel)
                        && !Double.isNaN(allDailyTicketsThisLevel)) {
                    result = (discordTickets / allDailyTicketsThisLevel) * 100;
                    totalResult += result;
                    daysWithTickets++;
                }
                System.out.println("Calculated result for " + date + " is: " + result + "%");
            }

            double averageResult = (daysWithTickets > 0) ? totalResult / daysWithTickets : 0.0;
            System.out.println("Average result for employee " + employee.getUsername() + ": " + averageResult + "%");

            ProductivityCalc productivityCalc = productivityCalcRepository.findByEmployeeId(employee.getId());

            if (productivityCalc == null) {
                System.out.println("No existing productivity calculation found for employee. Creating new entry.");
                productivityCalc = new ProductivityCalc();
                productivityCalc.setEmployee(employee);
            }

            productivityCalc.setDiscordTicketsCalc(averageResult);
            productivityCalcRepository.save(productivityCalc);
            System.out.println("Productivity calculation saved for employee " + employee.getUsername());
        }

        System.out.println("Finished calculation of average answered Discord tickets with coefficients.");
    }

}
