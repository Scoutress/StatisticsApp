package com.scoutress.KaimuxAdminStats.Servicesimpl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Constants.CalculationConstants;
import com.scoutress.KaimuxAdminStats.Entity.Employee;
import com.scoutress.KaimuxAdminStats.Entity.Playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.Entity.Productivity;
import com.scoutress.KaimuxAdminStats.Entity.ProductivityCalc;
import com.scoutress.KaimuxAdminStats.Repositories.ComplainsRepository;
import com.scoutress.KaimuxAdminStats.Repositories.DailyPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.Repositories.DcTickets.DcTicketRepository;
import com.scoutress.KaimuxAdminStats.Repositories.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.Repositories.PlaytimeRepository;
import com.scoutress.KaimuxAdminStats.Repositories.ProductivityCalcRepository;
import com.scoutress.KaimuxAdminStats.Repositories.ProductivityRepository;
import com.scoutress.KaimuxAdminStats.Services.ProductivityService;

import jakarta.transaction.Transactional;

@Service
public class ProductivityServiceImpl implements ProductivityService {

    private final ProductivityRepository productivityRepository;
    private final EmployeeRepository employeeRepository;
    private final PlaytimeRepository playtimeRepository;
    private final ProductivityCalcRepository productivityCalcRepository;
    private final DcTicketRepository dcTicketRepository;
    private final ComplainsRepository complainsRepository;
    private final DailyPlaytimeRepository dailyPlaytimeRepository;

    public ProductivityServiceImpl(ProductivityRepository productivityRepository,
            EmployeeRepository employeeRepository,
            PlaytimeRepository playtimeRepository,
            ProductivityCalcRepository productivityCalcRepository,
            DcTicketRepository dcTicketRepository,
            ComplainsRepository complainsRepository,
            DailyPlaytimeRepository dailyPlaytimeRepository) {
        this.productivityRepository = productivityRepository;
        this.employeeRepository = employeeRepository;
        this.playtimeRepository = playtimeRepository;
        this.productivityCalcRepository = productivityCalcRepository;
        this.dcTicketRepository = dcTicketRepository;
        this.complainsRepository = complainsRepository;
        this.dailyPlaytimeRepository = dailyPlaytimeRepository;
    }

    @Override
    public List<Productivity> findAll() {
        return productivityRepository.findAllWithEmployeeDetails();
    }

    @Override
    public void updateProductivityData() {
        List<Employee> employees = employeeRepository.findAll();

        employees.forEach(employee -> {
            Productivity existingProductivity = productivityRepository.findByEmployeeId(employee.getId());
            if (existingProductivity == null) {
                Productivity productivity = createDefaultProductivity(employee);
                productivityRepository.save(productivity);
            }
        });
    }

    private Productivity findOrCreateProductivity(Employee employee) {
        return Optional.ofNullable(productivityRepository.findByEmployeeId(employee.getId()))
                .orElseGet(() -> createDefaultProductivity(employee));
    }

    private Productivity createDefaultProductivity(Employee employee) {
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
        return productivity;
    }

    @Override
    public void updateAnnualPlaytimeForAllEmployees() {
        LocalDate endDate = LocalDate.now().minusDays(1);
        LocalDate startDate = endDate.minusDays(365);

        List<Integer> employeeIds = playtimeRepository.findAllDistinctEmployeeIds();

        employeeIds.forEach(employeeId -> {
            Employee employee = findEmployeeById(employeeId);
            Double totalPlaytime = playtimeRepository.sumPlaytimeByEmployeeAndDateRange(employeeId, startDate, endDate);
            Productivity productivity = findOrCreateProductivity(employee);
            productivity.setAnnualPlaytime(totalPlaytime != null ? totalPlaytime : 0.0);
            productivityRepository.save(productivity);
        });
    }

    private Employee findEmployeeById(Integer employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    @Override
    public void updateAveragePlaytimeForAllEmployees() {
        List<Integer> employeeIds = playtimeRepository.findAllEmployeeIds();

        employeeIds.forEach(employeeId -> {
            LocalDate startDate = playtimeRepository.findEarliestPlaytimeDateByEmployeeId(employeeId);
            LocalDate endDate = playtimeRepository.findLatestPlaytimeDateByEmployeeId(employeeId);

            if (startDate != null && endDate != null) {
                calculateAndUpdateAveragePlaytime(employeeId, startDate, endDate);
            }
        });
    }

    private void calculateAndUpdateAveragePlaytime(Integer employeeId, LocalDate startDate, LocalDate endDate) {
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        Double totalPlaytime = playtimeRepository.sumPlaytimeByEmployeeAndDateRange(employeeId, startDate, endDate);
        if (totalPlaytime != null && daysBetween > 0) {
            double averagePlaytime = totalPlaytime / daysBetween;
            Productivity productivity = findOrCreateProductivity(findEmployeeById(employeeId));
            productivity.setPlaytime(averagePlaytime);
            productivityRepository.save(productivity);
        }
    }

    @Override
    public void updateAfkPlaytimeForAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();

        employees.forEach(employee -> {
            Double totalPlaytime = playtimeRepository.getTotalPlaytimeByEmployeeId(employee.getId());
            Double totalAfkPlaytime = playtimeRepository.getTotalAfkPlaytimeByEmployeeId(employee.getId());

            double afkPercentage = calculateAfkPercentage(totalPlaytime, totalAfkPlaytime);
            Productivity productivity = findOrCreateProductivity(employee);
            productivity.setAfkPlaytime(afkPercentage);
            productivityRepository.save(productivity);
        });
    }

    private double calculateAfkPercentage(Double totalPlaytime, Double totalAfkPlaytime) {
        if (totalPlaytime == null || totalPlaytime <= 0) {
            return 0.0;
        }
        if (totalAfkPlaytime == null) {
            totalAfkPlaytime = 0.0;
        }
        return (totalAfkPlaytime / totalPlaytime) * 100;
    }

    @Override
    public void calculateServerTicketsForAllEmployeesWithCoefs() {
        List<Employee> employees = employeeRepository.findAll();

        employees.forEach(employee -> {
            ProductivityCalc productivityCalc = findOrCreateProductivityCalc(employee);
            double serverTickets = fetchServerTickets(employee);
            double calculatedValue = calculateServerTickets(serverTickets, employee.getLevel());
            productivityCalc.setServerTicketsCalc(calculatedValue);
            productivityCalcRepository.save(productivityCalc);
        });
    }

    private double fetchServerTickets(Employee employee) {
        try {
            return productivityRepository.findServerTicketsByEmployeeId(employee.getId());
        } catch (Exception e) {
            System.err.println(
                    "Error fetching serverTickets for employee ID: " + employee.getId() + ". Error: " + e.getMessage());
            return 0.0;
        }
    }

    private double calculateServerTickets(double serverTickets, String level) {
        return switch (level) {
            case "Support" -> serverTickets > 0.5 ? 0.5 * CalculationConstants.SERVER_TICKETS_SUPPORT
                    : serverTickets * CalculationConstants.SERVER_TICKETS_SUPPORT;
            case "Chatmod" -> serverTickets > 1.0 ? 1.0 * CalculationConstants.SERVER_TICKETS_CHATMOD
                    : serverTickets * CalculationConstants.SERVER_TICKETS_CHATMOD;
            case "Overseer" -> serverTickets > 2.0 ? 2.0 * CalculationConstants.SERVER_TICKETS_OVERSEER
                    : serverTickets * CalculationConstants.SERVER_TICKETS_OVERSEER;
            case "Organizer" -> serverTickets > 2.0 ? 2.0 * CalculationConstants.SERVER_TICKETS_ORGANIZER
                    : serverTickets * CalculationConstants.SERVER_TICKETS_ORGANIZER;
            case "Manager" -> serverTickets > 4.0 ? 4.0 * CalculationConstants.SERVER_TICKETS_MANAGER
                    : serverTickets * CalculationConstants.SERVER_TICKETS_MANAGER;
            default -> 0.0;
        };
    }

    private ProductivityCalc findOrCreateProductivityCalc(Employee employee) {
        return Optional.ofNullable(productivityCalcRepository.findByEmployeeId(employee.getId()))
                .orElseGet(() -> createNewProductivityCalc(employee));
    }

    private ProductivityCalc createNewProductivityCalc(Employee employee) {
        ProductivityCalc productivityCalc = new ProductivityCalc();
        productivityCalc.setEmployee(employee);
        return productivityCalc;
    }

    @Override
    public void calculateServerTicketsTakenForAllEmployeesWithCoefs() {
        List<Employee> employees = employeeRepository.findAll();

        employees.forEach(employee -> {
            ProductivityCalc productivityCalc = findOrCreateProductivityCalc(employee);
            double serverTicketsTaken = fetchServerTicketsTaken(employee);
            double calculatedValue = calculateServerTicketsTaken(serverTicketsTaken, employee.getLevel());
            productivityCalc.setServerTicketsTakingCalc(calculatedValue);
            productivityCalcRepository.save(productivityCalc);
        });
    }

    private double fetchServerTicketsTaken(Employee employee) {
        try {
            return productivityRepository.findServerTicketsTakenByEmployeeId(employee.getId());
        } catch (Exception e) {
            System.err.println("Error fetching serverTicketsTaken for employee ID: " + employee.getId() + ". Error: "
                    + e.getMessage());
            return 0.0;
        }
    }

    private double calculateServerTicketsTaken(double serverTicketsTaken, String level) {
        return switch (level) {
            case "Support" -> serverTicketsTaken > 20.0 ? 20.0 * CalculationConstants.SERVER_TICKETS_PERC_SUPPORT
                    : serverTicketsTaken * CalculationConstants.SERVER_TICKETS_PERC_SUPPORT;
            case "Chatmod" -> serverTicketsTaken > 40.0 ? 40.0 * CalculationConstants.SERVER_TICKETS_PERC_CHATMOD
                    : serverTicketsTaken * CalculationConstants.SERVER_TICKETS_PERC_CHATMOD;
            case "Overseer" -> serverTicketsTaken > 85.0 ? 85.0 * CalculationConstants.SERVER_TICKETS_PERC_OVERSEER
                    : serverTicketsTaken * CalculationConstants.SERVER_TICKETS_PERC_OVERSEER;
            case "Organizer" -> serverTicketsTaken > 85.0 ? 85.0 * CalculationConstants.SERVER_TICKETS_PERC_ORGANIZER
                    : serverTicketsTaken * CalculationConstants.SERVER_TICKETS_PERC_ORGANIZER;
            case "Manager" -> serverTicketsTaken > 100.0 ? 100.0 * CalculationConstants.SERVER_TICKETS_PERC_MANAGER
                    : serverTicketsTaken * CalculationConstants.SERVER_TICKETS_PERC_MANAGER;
            default -> 0.0;
        };
    }

    @Override
    public void calculatePlaytimeForAllEmployeesWithCoefs() {
        List<Employee> employees = employeeRepository.findAll();

        employees.forEach(employee -> {
            ProductivityCalc productivityCalc = findOrCreateProductivityCalc(employee);
            double playtimeValue = fetchPlaytime(employee);
            double calculatedValue = calculatePlaytime(playtimeValue, employee.getLevel());
            productivityCalc.setPlaytimeCalc(calculatedValue);
            productivityCalcRepository.save(productivityCalc);
        });
    }

    private double fetchPlaytime(Employee employee) {
        try {
            Double playtime = productivityRepository.findPlaytimeByEmployeeId(employee.getId());
            return (playtime != null) ? playtime : 0.0;
        } catch (Exception e) {
            System.err.println(
                    "Error fetching playtime for employee ID: " + employee.getId() + ". Error: " + e.getMessage());
            return 0.0;
        }
    }

    private double calculatePlaytime(double playtimeValue, String level) {
        return switch (level) {
            case "Helper" -> playtimeValue > 0.5 ? 0.5 * CalculationConstants.PLAYTIME_HELPER
                    : playtimeValue * CalculationConstants.PLAYTIME_SUPPORT;
            case "Support" -> playtimeValue > 1.0 ? 1.0 * CalculationConstants.PLAYTIME_SUPPORT
                    : playtimeValue * CalculationConstants.PLAYTIME_SUPPORT;
            case "Chatmod" -> playtimeValue > 2.0 ? 2.0 * CalculationConstants.PLAYTIME_CHATMOD
                    : playtimeValue * CalculationConstants.PLAYTIME_CHATMOD;
            case "Overseer" -> playtimeValue > 4.0 ? 4.0 * CalculationConstants.PLAYTIME_OVERSEER
                    : playtimeValue * CalculationConstants.PLAYTIME_OVERSEER;
            case "Organizer" -> playtimeValue > 4.0 ? 4.0 * CalculationConstants.PLAYTIME_ORGANIZER
                    : playtimeValue * CalculationConstants.PLAYTIME_ORGANIZER;
            case "Manager" -> playtimeValue > 8.0 ? 8.0 * CalculationConstants.PLAYTIME_MANAGER
                    : playtimeValue * CalculationConstants.PLAYTIME_MANAGER;
            default -> 0.0;
        };
    }

    @Override
    public void calculateAfkPlaytimeForAllEmployeesWithCoefs() {
        List<Employee> employees = employeeRepository.findAll();

        employees.forEach(employee -> {
            try {
                ProductivityCalc productivityCalc = findOrCreateProductivityCalc(employee);
                double afkPlaytime = fetchAfkPlaytime(employee);
                double calculatedValue = calculateAfkPlaytime(afkPlaytime, employee.getLevel());
                productivityCalc.setAfkPlaytimeCalc(calculatedValue);
                productivityCalcRepository.save(productivityCalc);
            } catch (RuntimeException e) {
                System.err.println("Error calculating AFK playtime for employee ID: " + employee.getId() + ". Error: "
                        + e.getMessage());
            }
        });
    }

    private double fetchAfkPlaytime(Employee employee) {
        return productivityRepository.findAfkPlaytimeByEmployeeId(employee.getId());
    }

    private double calculateAfkPlaytime(double afkPlaytime, String level) {
        double calculatedValue;
        calculatedValue = switch (level) {
            case "Helper", "Support" -> afkPlaytime * CalculationConstants.AFK_PLAYTIME_SUPPORT;
            case "Chatmod" -> afkPlaytime * CalculationConstants.AFK_PLAYTIME_CHATMOD;
            case "Overseer" -> afkPlaytime * CalculationConstants.AFK_PLAYTIME_OVERSEER;
            case "Organizer" -> afkPlaytime * CalculationConstants.AFK_PLAYTIME_ORGANIZER;
            case "Manager" -> afkPlaytime * CalculationConstants.AFK_PLAYTIME_MANAGER;
            default -> 0.0;
        };
        return Math.min(calculatedValue, 100.0);
    }

    @Override
    public void calculateAnsweredDiscordTicketsWithCoefs() {
        List<Employee> employees = employeeRepository.findAll();
        List<LocalDate> dates = dcTicketRepository.findAllDates();

        employees.forEach((Employee employee) -> {
            double totalResult = calculateTotalDiscordTicketResult(employee, dates);
            double averageResult = calculateAverageResult(totalResult, dates.size());

            if (!dates.isEmpty() && averageResult != 0.0) { // Patikrinimas, kad neįrašytų kai nėra bilietų
                ProductivityCalc productivityCalc = findOrCreateProductivityCalc(employee);
                productivityCalc.setDiscordTicketsCalc(averageResult);
                productivityCalcRepository.save(productivityCalc);
            }
        });
    }

    private double calculateTotalDiscordTicketResult(Employee employee, List<LocalDate> dates) {
        double totalResult = 0.0;
        for (LocalDate date : dates) {
            double allDiscordTickets = dcTicketRepository.sumByDate(date);
            if (allDiscordTickets == 0) {
                continue;
            }
            double discordTickets = dcTicketRepository.findAnsweredDiscordTicketsByEmployeeIdAndDate(employee.getId(),
                    date);
            double result = calculateDiscordTicketResult(discordTickets, allDiscordTickets, employee.getLevel());
            totalResult += result;
        }
        return totalResult;
    }

    private double calculateDiscordTicketResult(double discordTickets, double allDiscordTickets, String level) {
        double employeeLevelCoefficient = getEmployeeLevelCoefficient(level);
        double allDailyTicketsThisLevel = allDiscordTickets * employeeLevelCoefficient;
        return (!Double.isNaN(allDailyTicketsThisLevel)
                && allDailyTicketsThisLevel != 0 && allDiscordTickets != 0
                && !Double.isInfinite(allDailyTicketsThisLevel))
                        ? (discordTickets / allDailyTicketsThisLevel) * 100
                        : 0.0;
    }

    private double getEmployeeLevelCoefficient(String level) {
        return switch (level) {
            case "Support" -> CalculationConstants.DISCORD_TICKETS_SUPPORT;
            case "Chatmod" -> CalculationConstants.DISCORD_TICKETS_CHATMOD;
            case "Overseer" -> CalculationConstants.DISCORD_TICKETS_OVERSEER;
            case "Organizer" -> CalculationConstants.DISCORD_TICKETS_ORGANIZER;
            case "Manager" -> CalculationConstants.DISCORD_TICKETS_MANAGER;
            default -> 0.0;
        };
    }

    private double calculateAverageResult(double totalResult, int daysWithTickets) {
        return (daysWithTickets > 0) ? totalResult / daysWithTickets : 0.0;
    }

    @Override
    public void calculateAndSaveComplainsCalc() {
        List<Employee> employees = employeeRepository.findAll();

        employees.forEach(employee -> {
            double totalComplaints = fetchTotalComplaints(employee);
            ProductivityCalc productivityCalc = findOrCreateProductivityCalc(employee);
            productivityCalc.setComplainsCalc(totalComplaints);
            productivityCalcRepository.save(productivityCalc);
        });
    }

    private double fetchTotalComplaints(Employee employee) {
        Double totalComplaints = complainsRepository.sumComplaintsByEmployeeId(employee.getId());
        return totalComplaints != null ? totalComplaints : 0.0;
    }

    @Override
    public void calculateAndSaveProductivity() {
        List<Employee> employees = employeeRepository.findAll();

        for (Employee employee : employees) {
            ProductivityCalc productivityCalc = productivityCalcRepository.findByEmployeeId(employee.getId());

            if (productivityCalc == null) {
                System.err.println("ProductivityCalc not found for employee ID: " + employee.getId());
                return;
            }

            try {
                double result = calculateProductivity(productivityCalc);
                saveProductivity(employee, result);
            } catch (Exception e) {
                System.err.println("Error processing productivity for employee ID: " + employee.getId() + ". Error: "
                        + e.getMessage());
                throw e;
            }
        }
    }

    private double calculateProductivity(ProductivityCalc productivityCalc) {
        double discordTicketsCalc = productivityCalc.getDiscordTicketsCalc();
        double afkPlaytimeCalc = productivityCalc.getAfkPlaytimeCalc();
        double playtimeCalc = productivityCalc.getPlaytimeCalc();
        double serverTicketsCalc = productivityCalc.getServerTicketsCalc();
        double serverTicketsTakingCalc = productivityCalc.getServerTicketsTakingCalc();
        double complainsCalc = productivityCalc.getComplainsCalc();

        return ((discordTicketsCalc - afkPlaytimeCalc + playtimeCalc + serverTicketsCalc + serverTicketsTakingCalc) / 5)
                - complainsCalc;
    }

    private void saveProductivity(Employee employee, double result) {
        Productivity productivity = productivityRepository.findByEmployeeId(employee.getId());
        if (productivity == null) {
            productivity = new Productivity();
            productivity.setEmployee(employee);
        }
        productivity.setProductivity(result);
        productivityRepository.save(productivity);
    }

    @Override
    @Transactional
    public void calculateAveragePlaytime() {
        List<Employee> employees = employeeRepository.findAll();

        employees.forEach(employee -> {
            List<DailyPlaytime> playtimes = dailyPlaytimeRepository.findByEmployeeId(employee.getId());

            if (playtimes.isEmpty()) {
                return;
            }

            double averagePlaytimePerDay = calculateAveragePlaytimePerDay(playtimes);

            Productivity productivity = findOrCreateProductivity(employee);
            productivity.setPlaytime(averagePlaytimePerDay);
            productivityRepository.save(productivity);
        });
    }

    private double calculateAveragePlaytimePerDay(List<DailyPlaytime> playtimes) {
        LocalDate earliestDate = playtimes.stream()
                .map(DailyPlaytime::getDate)
                .min(LocalDate::compareTo)
                .orElse(LocalDate.now());

        LocalDate yesterday = LocalDate.now().minusDays(1);

        long daysBetween = ChronoUnit.DAYS.between(earliestDate, yesterday) + 1;

        double totalPlaytime = playtimes.stream()
                .mapToDouble(DailyPlaytime::getTotalPlaytime)
                .sum();

        return totalPlaytime / daysBetween;
    }
}