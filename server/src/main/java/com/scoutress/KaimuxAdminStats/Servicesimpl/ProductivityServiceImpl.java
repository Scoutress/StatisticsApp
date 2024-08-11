package com.scoutress.KaimuxAdminStats.Servicesimpl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Constants.CalculationConstants;
import com.scoutress.KaimuxAdminStats.Entity.Employee;
import com.scoutress.KaimuxAdminStats.Entity.Productivity;
import com.scoutress.KaimuxAdminStats.Entity.ProductivityCalc;
import com.scoutress.KaimuxAdminStats.Repositories.ComplainsRepository;
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
    private final ComplainsRepository complainsRepository;

    public ProductivityServiceImpl(ProductivityRepository productivityRepository,
            EmployeeRepository employeeRepository,
            PlaytimeRepository playtimeRepository,
            ProductivityCalcRepository productivityCalcRepository,
            DcTicketRepository dcTicketRepository,
            ComplainsRepository complainsRepository) {
        this.productivityRepository = productivityRepository;
        this.employeeRepository = employeeRepository;
        this.playtimeRepository = playtimeRepository;
        this.productivityCalcRepository = productivityCalcRepository;
        this.dcTicketRepository = dcTicketRepository;
        this.complainsRepository = complainsRepository;
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

            double serverTickets = 0.0;

            try {
                serverTickets = productivityRepository.findServerTicketsByEmployeeId(employee.getId());
            } catch (Exception e) {
                System.err.println("Klaida gaunant serverTickets darbuotojui ID: " + employee.getId() + ". Klaida: "
                        + e.getMessage());
            }

            double calculatedValue;

            switch (employee.getLevel()) {
                case "Support" -> {
                    if (serverTickets > 0.5) {
                        calculatedValue = 0.5 * CalculationConstants.SERVER_TICKETS_SUPPORT;
                    } else {
                        calculatedValue = serverTickets * CalculationConstants.SERVER_TICKETS_SUPPORT;
                    }
                }
                case "Chatmod" -> {
                    if (serverTickets > 1.0) {
                        calculatedValue = 1.0 * CalculationConstants.SERVER_TICKETS_CHATMOD;
                    } else {
                        calculatedValue = serverTickets * CalculationConstants.SERVER_TICKETS_CHATMOD;
                    }
                }
                case "Overseer" -> {
                    if (serverTickets > 2.0) {
                        calculatedValue = 2.0 * CalculationConstants.SERVER_TICKETS_OVERSEER;
                    } else {
                        calculatedValue = serverTickets * CalculationConstants.SERVER_TICKETS_OVERSEER;
                    }
                }
                case "Organizer" -> {
                    if (serverTickets > 2.0) {
                        calculatedValue = 2.0 * CalculationConstants.SERVER_TICKETS_ORGANIZER;
                    } else {
                        calculatedValue = serverTickets * CalculationConstants.SERVER_TICKETS_ORGANIZER;
                    }
                }
                case "Manager" -> {
                    if (serverTickets > 4.0) {
                        calculatedValue = 4.0 * CalculationConstants.SERVER_TICKETS_MANAGER;
                    } else {
                        calculatedValue = serverTickets * CalculationConstants.SERVER_TICKETS_MANAGER;
                    }
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

            double serverTicketsTaken = 0.0;

            try {
                serverTicketsTaken = productivityRepository.findServerTicketsTakenByEmployeeId(employee.getId());
            } catch (Exception e) {
                System.err.println("Error fetching serverTicketsTaken for employee ID: " + employee.getId()
                        + ". Error: " + e.getMessage());
            }

            double calculatedValue;

            switch (employee.getLevel()) {
                case "Support" -> {
                    if (serverTicketsTaken > 20.0) {
                        calculatedValue = 20.0 * CalculationConstants.SERVER_TICKETS_PERC_SUPPORT;
                    } else {
                        calculatedValue = serverTicketsTaken * CalculationConstants.SERVER_TICKETS_PERC_SUPPORT;
                    }
                }
                case "Chatmod" -> {
                    if (serverTicketsTaken > 40.0) {
                        calculatedValue = 40.0 * CalculationConstants.SERVER_TICKETS_PERC_CHATMOD;
                    } else {
                        calculatedValue = serverTicketsTaken * CalculationConstants.SERVER_TICKETS_PERC_CHATMOD;
                    }
                }
                case "Overseer" -> {
                    if (serverTicketsTaken > 85.0) {
                        calculatedValue = 85.0 * CalculationConstants.SERVER_TICKETS_PERC_OVERSEER;
                    } else {
                        calculatedValue = serverTicketsTaken * CalculationConstants.SERVER_TICKETS_PERC_OVERSEER;
                    }
                }
                case "Organizer" -> {
                    if (serverTicketsTaken > 85.0) {
                        calculatedValue = 85.0 * CalculationConstants.SERVER_TICKETS_PERC_ORGANIZER;
                    } else {
                        calculatedValue = serverTicketsTaken * CalculationConstants.SERVER_TICKETS_PERC_ORGANIZER;
                    }
                }
                case "Manager" -> {
                    if (serverTicketsTaken > 100.0) {
                        calculatedValue = 100.0 * CalculationConstants.SERVER_TICKETS_PERC_MANAGER;
                    } else {
                        calculatedValue = serverTicketsTaken * CalculationConstants.SERVER_TICKETS_PERC_MANAGER;
                    }
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

            Double playtime = productivityRepository.findPlaytimeByEmployeeId(employee.getId());
            double playtimeValue = (playtime != null) ? playtime : 0.0;
            double calculatedValue;

            switch (employee.getLevel()) {
                case "Helper" -> {
                    if (playtimeValue > 0.5) {
                        calculatedValue = 0.5 * CalculationConstants.PLAYTIME_HELPER;
                    } else {
                        calculatedValue = playtimeValue * CalculationConstants.PLAYTIME_SUPPORT;
                    }
                    break;
                }
                case "Support" -> {
                    if (playtimeValue > 1.0) {
                        calculatedValue = 1.0 * CalculationConstants.PLAYTIME_SUPPORT;
                    } else {
                        calculatedValue = playtimeValue * CalculationConstants.PLAYTIME_SUPPORT;
                    }
                    break;
                }
                case "Chatmod" -> {
                    if (playtimeValue > 2.0) {
                        calculatedValue = 2.0 * CalculationConstants.PLAYTIME_CHATMOD;
                    } else {
                        calculatedValue = playtimeValue * CalculationConstants.PLAYTIME_CHATMOD;
                    }
                    break;
                }
                case "Overseer" -> {
                    if (playtimeValue > 4.0) {
                        calculatedValue = 4.0 * CalculationConstants.PLAYTIME_OVERSEER;
                    } else {
                        calculatedValue = playtimeValue * CalculationConstants.PLAYTIME_OVERSEER;
                    }
                    break;
                }
                case "Organizer" -> {
                    if (playtimeValue > 4.0) {
                        calculatedValue = 4.0 * CalculationConstants.PLAYTIME_ORGANIZER;
                    } else {
                        calculatedValue = playtimeValue * CalculationConstants.PLAYTIME_ORGANIZER;
                    }
                    break;
                }
                case "Manager" -> {
                    if (playtimeValue > 8.0) {
                        calculatedValue = 8.0 * CalculationConstants.PLAYTIME_MANAGER;
                    } else {
                        calculatedValue = playtimeValue * CalculationConstants.PLAYTIME_MANAGER;
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
        List<Employee> employees = employeeRepository.findAll();
        List<LocalDate> dates = dcTicketRepository.findAllDates();

        for (Employee employee : employees) {
            double totalResult = 0.0;
            int daysWithTickets = 0;

            for (LocalDate date : dates) {
                double allDiscordTickets = dcTicketRepository.sumByDate(date);

                if (allDiscordTickets == 0) {
                    continue;
                }

                double discordTickets = dcTicketRepository
                        .findAnsweredDiscordTicketsByEmployeeIdAndDate(employee.getId(), date);

                double employeeLevelCoefficient;
                switch (employee.getLevel()) {
                    case "Support" -> employeeLevelCoefficient = CalculationConstants.DISCORD_TICKETS_SUPPORT;
                    case "Chatmod" -> employeeLevelCoefficient = CalculationConstants.DISCORD_TICKETS_CHATMOD;
                    case "Overseer" -> employeeLevelCoefficient = CalculationConstants.DISCORD_TICKETS_OVERSEER;
                    case "Organizer" -> employeeLevelCoefficient = CalculationConstants.DISCORD_TICKETS_ORGANIZER;
                    case "Manager" -> employeeLevelCoefficient = CalculationConstants.DISCORD_TICKETS_MANAGER;
                    default -> {
                        employeeLevelCoefficient = 0.0;
                    }
                }

                double allDailyTicketsThisLevel = allDiscordTickets * employeeLevelCoefficient;

                double result;
                if (allDailyTicketsThisLevel != 0 && allDiscordTickets != 0
                        && !Double.isInfinite(allDailyTicketsThisLevel)
                        && !Double.isNaN(allDailyTicketsThisLevel)) {
                    result = (discordTickets / allDailyTicketsThisLevel) * 100;
                    totalResult += result;
                    daysWithTickets++;
                }
            }

            double averageResult = (daysWithTickets > 0) ? totalResult / daysWithTickets : 0.0;

            ProductivityCalc productivityCalc = productivityCalcRepository.findByEmployeeId(employee.getId());

            if (productivityCalc == null) {
                productivityCalc = new ProductivityCalc();
                productivityCalc.setEmployee(employee);
            }

            productivityCalc.setDiscordTicketsCalc(averageResult);
            productivityCalcRepository.save(productivityCalc);
        }
    }

    @Override
    public void calculateAndSaveComplainsCalc() {
        List<Employee> employees = employeeRepository.findAll();

        for (Employee employee : employees) {
            Double totalComplaints = complainsRepository.sumComplaintsByEmployeeId(employee.getId());

            if (totalComplaints == null) {
                totalComplaints = 0.0;
            }

            ProductivityCalc productivityCalc = productivityCalcRepository.findByEmployeeId(employee.getId());

            if (productivityCalc == null) {
                productivityCalc = new ProductivityCalc();
                productivityCalc.setEmployee(employee);
            }

            productivityCalc.setComplainsCalc(totalComplaints);

            productivityCalcRepository.save(productivityCalc);
        }
    }

    @Override
    public void calculateAndSaveProductivity() {
        List<Employee> employees = employeeRepository.findAll();

        for (Employee employee : employees) {
            ProductivityCalc productivityCalc = null;
            System.out.println(productivityCalc);// fix this later
            try {
                productivityCalc = productivityCalcRepository.findByEmployeeId(employee.getId());
            } catch (Exception e) {
                System.err.println("Error fetching ProductivityCalc for employee ID: " + employee.getId() + ". Error: "
                        + e.getMessage());
                continue; // Skip this employee and continue with the next one
            }

            if (productivityCalc == null) {
                continue;
            }

            double discordTicketsCalc;
            double afkPlaytimeCalc;
            double playtimeCalc;
            double serverTicketsCalc;
            double serverTicketsTakingCalc;
            double complainsCalc;

            try {
                discordTicketsCalc = productivityCalc.getDiscordTicketsCalc();
                afkPlaytimeCalc = productivityCalc.getAfkPlaytimeCalc();
                playtimeCalc = productivityCalc.getPlaytimeCalc();
                serverTicketsCalc = productivityCalc.getServerTicketsCalc();
                serverTicketsTakingCalc = productivityCalc.getServerTicketsTakingCalc();
                complainsCalc = productivityCalc.getComplainsCalc();
            } catch (Exception e) {
                System.err.println(
                        "Error calculating values for employee ID: " + employee.getId() + ". Error: " + e.getMessage());
                continue; // Skip this employee if any of the calculations fail
            }

            double result;
            try {
                result = ((discordTicketsCalc
                        - afkPlaytimeCalc
                        + playtimeCalc
                        + serverTicketsCalc
                        + serverTicketsTakingCalc) / 5)
                        - complainsCalc;
            } catch (Exception e) {
                System.err.println("Error computing productivity result for employee ID: " + employee.getId()
                        + ". Error: " + e.getMessage());
                continue; // Skip this employee if the final calculation fails
            }

            Productivity productivity;
            try {
                productivity = productivityRepository.findByEmployeeId(employee.getId());
                if (productivity == null) {
                    productivity = new Productivity();
                    productivity.setEmployee(employee);
                }

                productivity.setProductivity(result);
                productivityRepository.save(productivity);
            } catch (Exception e) {
                System.err.println("Error saving productivity for employee ID: " + employee.getId() + ". Error: "
                        + e.getMessage());
                // Depending on your logic, decide whether to continue or handle the exception
                // differently
            }
        }
    }
}