package com.scoutress.KaimuxAdminStats.Servicesimpl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Constants.CalculationConstants;
import com.scoutress.KaimuxAdminStats.Entity.DcTickets.DcTicket;
import com.scoutress.KaimuxAdminStats.Entity.Employees.Employee;
import com.scoutress.KaimuxAdminStats.Entity.McTickets.McTicketPercentage;
import com.scoutress.KaimuxAdminStats.Entity.Playtime.AfkPlaytime;
import com.scoutress.KaimuxAdminStats.Entity.Playtime.DailyPlaytime;
import com.scoutress.KaimuxAdminStats.Entity.Productivity;
import com.scoutress.KaimuxAdminStats.Entity.ProductivityCalc;
import com.scoutress.KaimuxAdminStats.Repositories.AfkPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.Repositories.ComplainsRepository;
import com.scoutress.KaimuxAdminStats.Repositories.DailyPlaytimeRepository;
import com.scoutress.KaimuxAdminStats.Repositories.DcTickets.DcTicketRepository;
import com.scoutress.KaimuxAdminStats.Repositories.EmployeePromotionsRepository;
import com.scoutress.KaimuxAdminStats.Repositories.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.Repositories.McTickets.McTicketPercentageRepository;
import com.scoutress.KaimuxAdminStats.Repositories.McTickets.McTicketRepository;
import com.scoutress.KaimuxAdminStats.Repositories.ProductivityCalcRepository;
import com.scoutress.KaimuxAdminStats.Repositories.ProductivityRepository;
import com.scoutress.KaimuxAdminStats.Services.ProductivityService;

import jakarta.transaction.Transactional;

@Service
public class ProductivityServiceImpl implements ProductivityService {

    private final ProductivityRepository productivityRepository;
    private final EmployeeRepository employeeRepository;
    private final AfkPlaytimeRepository afkPlaytimeRepository;
    private final DailyPlaytimeRepository dailyPlaytimeRepository;
    private final ProductivityCalcRepository productivityCalcRepository;
    private final McTicketRepository mcTicketRepository;
    private final McTicketPercentageRepository mcTicketPercentageRepository;
    private final EmployeePromotionsRepository employeePromotionsRepository;
    private final DcTicketRepository dcTicketRepository;
    private final ComplainsRepository complainsRepository;

    public ProductivityServiceImpl(ProductivityRepository productivityRepository,
            EmployeeRepository employeeRepository,
            AfkPlaytimeRepository playtimeRepository,
            ProductivityCalcRepository productivityCalcRepository,
            DcTicketRepository dcTicketRepository,
            ComplainsRepository complainsRepository,
            DailyPlaytimeRepository dailyPlaytimeRepository,
            McTicketPercentageRepository mcTicketPercentageRepository,
            EmployeePromotionsRepository employeePromotionsRepository, McTicketRepository mcTicketRepository) {
        this.productivityRepository = productivityRepository;
        this.employeeRepository = employeeRepository;
        this.afkPlaytimeRepository = playtimeRepository;
        this.productivityCalcRepository = productivityCalcRepository;
        this.mcTicketRepository = mcTicketRepository;
        this.mcTicketPercentageRepository = mcTicketPercentageRepository;
        this.employeePromotionsRepository = employeePromotionsRepository;
        this.dcTicketRepository = dcTicketRepository;
        this.complainsRepository = complainsRepository;
        this.dailyPlaytimeRepository = dailyPlaytimeRepository;
    }

    private List<Employee> employees;

    @Override
    public List<Productivity> findAll() {
        return productivityRepository.findAllWithEmployeeDetails();
    }

    @Override
    public void updateProductivity() {
        updateProductivityData();
        calculateAndSaveMcTicketsForAllEmployees();
        calculateAndUpdateDiscordTicketsForAllEmployees();
        calculateAndUpdateAfkPlaytimeForAllEmployees();
        updateAnnualPlaytimeForAllEmployees();
        calculateAndSavePlaytimeForAllEmployees();
        calculateAndSaveComplaintsForAllEmployees();
        calculateAndSaveProductivity();
    }

    @Transactional
    public void updateProductivityData() {
        this.employees = employeeRepository.findAll();
        employees.forEach(employee -> {
            Productivity existingProductivity = productivityRepository.findByEmployeeId(employee.getId());
            if (existingProductivity == null) {
                Productivity productivity = createDefaultProductivity(employee);
                productivityRepository.save(productivity);
            }
        });
    }

    @Transactional
    public void calculateAndSaveMcTicketsForAllEmployees() {
        employees.forEach(employee -> {
            LocalDate startDate = findReferenceDateForEmployeeMcTickets(employee);
            LocalDate endDate = LocalDate.now().minusDays(1);

            resetProductivityValues(employee);

            double totalTickets = calculateTotalTickets(employee, startDate, endDate);
            double daysBetween = calculateDaysBetween(startDate, endDate);
            double averageTicketsPerDay = calculateAveragePerDay(totalTickets, daysBetween);
            saveServerTicketsToProductivity(employee, averageTicketsPerDay);

            double averageTicketsTakingPerDay = calculateAndSaveServerTicketsTaking(employee, startDate, endDate,
                    daysBetween);

            saveServerTicketsTakingToProductivity(employee, averageTicketsTakingPerDay);

            double totalTicketsPercentage = calculateTotalTicketsPercentage(employee, startDate, endDate);
            double averageTicketsPercentagePerDay = calculateAveragePerDay(totalTicketsPercentage, daysBetween);
            saveServerTicketsToProductivityCalc(employee, averageTicketsPerDay);
            saveServerTicketsTakingToProductivityCalc(employee, averageTicketsPercentagePerDay);
        });
    }

    private double calculateAndSaveServerTicketsTaking(Employee employee, LocalDate startDate, LocalDate endDate,
            double daysBetween) {
        double totalTakingScore = 0.0;
        List<LocalDate> dates = getDatesInRange(startDate, endDate);

        for (LocalDate date : dates) {
            double playtimeThatDay = calculatePlaytimeThatDay(employee, date);
            double percentageValue = getPercentageValueForDate(employee, date);

            if (playtimeThatDay <= 5) {
                percentageValue += 25;
            }

            totalTakingScore += percentageValue;
        }

        return calculateAveragePerDay(totalTakingScore, daysBetween);
    }

    private double calculatePlaytimeThatDay(Employee employee, LocalDate date) {
        DailyPlaytime dailyPlaytime = dailyPlaytimeRepository.findByEmployeeIdAndDate(employee.getId(), date);
        return Optional.ofNullable(dailyPlaytime)
                .map(DailyPlaytime::getTotalPlaytime)
                .orElse(0.0);
    }

    private double getPercentageValueForDate(Employee employee, LocalDate date) {
        Optional<McTicketPercentage> mcTicketPercentage = mcTicketPercentageRepository
                .findFirstByEmployeeIdAndDate(employee.getId(), date);
        return mcTicketPercentage.map(McTicketPercentage::getPercentage).orElse(0.0);
    }

    private void saveServerTicketsTakingToProductivity(Employee employee, double value) {
        Productivity productivity = findOrCreateProductivity(employee);
        productivity.setServerTicketsTaking(value);
        productivityRepository.save(productivity);
    }

    public List<LocalDate> getDatesInRange(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dates = new ArrayList<>();

        if (!startDate.isAfter(endDate)) {
            long numOfDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;

            for (int i = 0; i < numOfDays; i++) {
                dates.add(startDate.plusDays(i));
            }
        }

        return dates;
    }

    private LocalDate findReferenceDateForEmployeeMcTickets(Employee employee) {
        LocalDate joinDate = employee.getJoinDate();

        LocalDate promotionDate = employeePromotionsRepository
                .findToSupportPromotionDateByEmployeeId(employee.getId())
                .orElse(null);

        LocalDate earliestTicketDate = mcTicketRepository
                .findEarliestTicketDateByEmployeeId(employee.getId())
                .orElse(null);

        return Stream.of(joinDate, promotionDate, earliestTicketDate)
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());
    }

    private LocalDate findReferenceDateForEmployeePlaytime(Employee employee) {
        LocalDate joinDate = employee.getJoinDate();

        LocalDate promotionDate = employeePromotionsRepository
                .findToSupportPromotionDateByEmployeeId(employee.getId())
                .orElse(null);

        LocalDate earliestPlaytimeDate = dailyPlaytimeRepository
                .findEarliestPlaytimeDateByEmployeeId(employee.getId());

        return Stream.of(joinDate, promotionDate, earliestPlaytimeDate)
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());
    }

    private double calculateTotalTickets(Employee employee, LocalDate startDate, LocalDate endDate) {
        return mcTicketRepository.sumTicketsByEmployeeIdAndDateRange(employee.getId(), startDate, endDate)
                .orElse(0.0);
    }

    private double calculateDaysBetween(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    private double calculateAveragePerDay(double totalValue, double days) {
        return days > 0 ? totalValue / days : 0.0;
    }

    private void resetProductivityValues(Employee employee) {
        Productivity productivity = findOrCreateProductivity(employee);
        productivity.setServerTickets(0.0);
        productivity.setServerTicketsTaking(0.0);
        productivityRepository.save(productivity);

        ProductivityCalc productivityCalc = findOrCreateProductivityCalc(employee);
        productivityCalc.setServerTicketsCalc(0.0);
        productivityCalc.setServerTicketsTakingCalc(0.0);
        productivityCalcRepository.save(productivityCalc);
    }

    private void saveServerTicketsToProductivity(Employee employee, double averageTicketsPerDay) {
        Productivity productivity = findOrCreateProductivity(employee);
        productivity.setServerTickets(averageTicketsPerDay);
        productivityRepository.save(productivity);
    }

    private double calculateTotalTicketsPercentage(Employee employee, LocalDate startDate, LocalDate endDate) {
        return mcTicketPercentageRepository.sumTicketsByEmployeeIdAndDateRange(employee.getId(), startDate, endDate)
                .orElse(0.0);
    }

    private void saveServerTicketsToProductivityCalc(Employee employee, double averageTicketsPerDay) {
        double calculatedValue = calculateServerTickets(averageTicketsPerDay, employee.getLevel());
        ProductivityCalc productivityCalc = findOrCreateProductivityCalc(employee);
        productivityCalc.setServerTicketsCalc(calculatedValue);
        productivityCalcRepository.save(productivityCalc);
    }

    private double calculateServerTickets(double serverTickets, String level) {
        double maxSTForSupport = CalculationConstants.SERVER_TICKETS_MAX_SUPPORT;
        double maxSTForChatMod = CalculationConstants.SERVER_TICKETS_MAX_CHATMOD;
        double maxSTForOverseer = CalculationConstants.SERVER_TICKETS_MAX_OVERSEER;
        double maxSTForOrganizer = CalculationConstants.SERVER_TICKETS_MAX_ORGANIZER;
        double maxSTForManager = CalculationConstants.SERVER_TICKETS_MAX_MANAGER;

        double stForSupport = CalculationConstants.SERVER_TICKETS_SUPPORT;
        double stForChatMod = CalculationConstants.SERVER_TICKETS_CHATMOD;
        double stForOverseer = CalculationConstants.SERVER_TICKETS_OVERSEER;
        double stForOrganizer = CalculationConstants.SERVER_TICKETS_ORGANIZER;
        double stForManager = CalculationConstants.SERVER_TICKETS_MANAGER;

        return switch (level) {
            case "Support" ->
                serverTickets > maxSTForSupport ? maxSTForSupport * stForSupport : serverTickets * stForSupport;
            case "Chatmod" ->
                serverTickets > maxSTForChatMod ? maxSTForChatMod * stForChatMod : serverTickets * stForChatMod;
            case "Overseer" ->
                serverTickets > maxSTForOverseer ? maxSTForOverseer * stForOverseer : serverTickets * stForOverseer;
            case "Organizer" ->
                serverTickets > maxSTForOrganizer ? maxSTForOrganizer * stForOrganizer : serverTickets * stForOrganizer;
            case "Manager" ->
                serverTickets > maxSTForManager ? maxSTForManager * stForManager : serverTickets * stForManager;
            default -> 0.0;
        };
    }

    private void saveServerTicketsTakingToProductivityCalc(Employee employee, double averageTicketsPercentagePerDay) {
        double calculatedValue = calculateServerTicketsTaken(averageTicketsPercentagePerDay, employee.getLevel());
        ProductivityCalc productivityCalc = findOrCreateProductivityCalc(employee);
        productivityCalc.setServerTicketsTakingCalc(calculatedValue);
        productivityCalcRepository.save(productivityCalc);
    }

    private double calculateServerTicketsTaken(double serverTicketsTaken, String level) {
        double maxSttForSupport = CalculationConstants.SERVER_TICKETS_MAX_SUPPORT;
        double maxSttForChatMod = CalculationConstants.SERVER_TICKETS_MAX_CHATMOD;
        double maxStForOverseer = CalculationConstants.SERVER_TICKETS_MAX_OVERSEER;
        double maxSttForOrganizer = CalculationConstants.SERVER_TICKETS_MAX_ORGANIZER;
        double maxSttForManager = CalculationConstants.SERVER_TICKETS_MAX_MANAGER;

        double sttForSupport = CalculationConstants.SERVER_TICKETS_PERC_SUPPORT;
        double sttForChatMod = CalculationConstants.SERVER_TICKETS_PERC_CHATMOD;
        double sttForOverseer = CalculationConstants.SERVER_TICKETS_PERC_OVERSEER;
        double sttForOrganizer = CalculationConstants.SERVER_TICKETS_PERC_ORGANIZER;
        double sttForManager = CalculationConstants.SERVER_TICKETS_PERC_MANAGER;

        return switch (level) {
            case "Support" -> serverTicketsTaken > maxSttForSupport ? maxSttForSupport * sttForSupport
                    : serverTicketsTaken * sttForSupport;
            case "Chatmod" -> serverTicketsTaken > maxSttForChatMod ? maxSttForChatMod * sttForChatMod
                    : serverTicketsTaken * sttForChatMod;
            case "Overseer" -> serverTicketsTaken > maxStForOverseer ? maxStForOverseer * sttForOverseer
                    : serverTicketsTaken * sttForOverseer;
            case "Organizer" -> serverTicketsTaken > maxSttForOrganizer ? maxSttForOrganizer * sttForOrganizer
                    : serverTicketsTaken * sttForOrganizer;
            case "Manager" -> serverTicketsTaken > maxSttForManager ? maxSttForManager * sttForManager
                    : serverTicketsTaken * sttForManager;
            default -> 0.0;
        };
    }

    @Transactional
    public void calculateAndUpdateDiscordTicketsForAllEmployees() {
        employees.forEach(employee -> {
            List<DcTicket> employeeTickets = fetchEmployeeTickets(employee);
            double totalTickets = calculateTotalTickets(employeeTickets);
            LocalDate startDate = determineStartDate(employee, employeeTickets);
            long daysBetween = calculateDaysBetween(startDate);
            double averageTicketsPerDay = calculateAverageTicketsPerDay(totalTickets, daysBetween);

            saveDiscordTicketsToProductivity(employee, averageTicketsPerDay);

            double ticketsWithCoef = applyDiscordTicketsCoefficient(employee.getLevel(), averageTicketsPerDay);
            saveDiscordTicketsWithCoefToProductivityCalc(employee, ticketsWithCoef);
        });
    }

    private List<DcTicket> fetchEmployeeTickets(Employee employee) {
        return dcTicketRepository.findTicketsByEmployeeId(employee.getId());
    }

    private double calculateTotalTickets(List<DcTicket> tickets) {
        return tickets.size();
    }

    private LocalDate determineStartDate(Employee employee, List<DcTicket> tickets) {
        LocalDate joinDate = employee.getJoinDate();

        LocalDate earliestTicketDate = tickets.stream()
                .map(DcTicket::getDate)
                .min(LocalDate::compareTo)
                .orElse(joinDate);

        LocalDate promotionDate = employeePromotionsRepository
                .findToSupportPromotionDateByEmployeeId(employee.getId())
                .orElse(null);

        return Stream.of(joinDate, promotionDate, earliestTicketDate)
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());
    }

    private long calculateDaysBetween(LocalDate startDate) {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return ChronoUnit.DAYS.between(startDate, yesterday) + 1;
    }

    private double calculateAverageTicketsPerDay(double totalTickets, long daysBetween) {
        return totalTickets / daysBetween;
    }

    private void saveDiscordTicketsToProductivity(Employee employee, double averageTicketsPerDay) {
        Productivity productivity = findOrCreateProductivity(employee);
        productivity.setDiscordTickets(averageTicketsPerDay);
        productivityRepository.save(productivity);
    }

    private double applyDiscordTicketsCoefficient(String level, double averageTicketsPerDay) {
        double coefficient = getEmployeeLevelCoefficient(level);
        return averageTicketsPerDay * coefficient;
    }

    private void saveDiscordTicketsWithCoefToProductivityCalc(Employee employee, double ticketsWithCoef) {
        ProductivityCalc productivityCalc = findOrCreateProductivityCalc(employee);
        productivityCalc.setDiscordTicketsCalc(ticketsWithCoef);
        productivityCalcRepository.save(productivityCalc);
    }

    private double getEmployeeLevelCoefficient(String level) {
        return switch (level) {
            case "Support" -> CalculationConstants.DISCORD_TICKETS_SUPPORT;
            case "Chatmod" -> CalculationConstants.DISCORD_TICKETS_CHATMOD;
            case "Overseer" -> CalculationConstants.DISCORD_TICKETS_OVERSEER;
            case "Organizer" -> CalculationConstants.DISCORD_TICKETS_ORGANIZER;
            case "Manager" -> CalculationConstants.DISCORD_TICKETS_MANAGER;
            default -> 1.0;
        };
    }

    @Transactional
    public void calculateAndUpdateAfkPlaytimeForAllEmployees() {
        employees.forEach(employee -> {
            LocalDate referenceDate = findReferenceDateForEmployeePlaytime(employee);
            LocalDate today = LocalDate.now();

            double averageAfkTimePerDay = calculateAverageAfkTimePerDay(employee, referenceDate, today);

            double averagePlaytimePerDay = calculateAveragePlaytimePerDay(employee, referenceDate, today);

            double afkPercentage = calculateAfkPercentage(averageAfkTimePerDay, averagePlaytimePerDay);

            saveAfkPlaytimeToProductivity(employee, afkPercentage);

            double afkPlaytimeWithCoef = applyAfkPlaytimeCoefficient(employee.getLevel(), afkPercentage);
            saveAfkPlaytimeWithCoefToProductivityCalc(employee, afkPlaytimeWithCoef);
        });
    }

    private double calculateAverageAfkTimePerDay(Employee employee, LocalDate startDate, LocalDate endDate) {
        List<AfkPlaytime> afkPlaytimes = afkPlaytimeRepository.findByEmployeeIdAndDateRange(employee.getId(), startDate,
                endDate);

        if (afkPlaytimes.isEmpty()) {
            return 0.0;
        }

        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        double totalAfkTime = afkPlaytimes.stream()
                .mapToDouble(AfkPlaytime::getAfkPlaytime)
                .sum();

        return totalAfkTime / totalDays;
    }

    private double calculateAveragePlaytimePerDay(Employee employee, LocalDate startDate, LocalDate endDate) {
        List<DailyPlaytime> playtimes = dailyPlaytimeRepository.findByEmployeeIdAndDateRange(employee.getId(),
                startDate, endDate);

        if (playtimes.isEmpty()) {
            return 0.0;
        }

        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        double totalPlaytime = playtimes.stream()
                .mapToDouble(DailyPlaytime::getTotalPlaytime)
                .sum();

        return totalPlaytime / totalDays;
    }

    private double calculateAfkPercentage(double averageAfkTimePerDay, double averagePlaytimePerDay) {
        if (averagePlaytimePerDay == 0) {
            return 0.0;
        }
        return (averageAfkTimePerDay / averagePlaytimePerDay) * 100;
    }

    private double applyAfkPlaytimeCoefficient(String level, double afkPercentage) {
        double coefficient = switch (level) {
            case "Helper" -> CalculationConstants.AFK_PLAYTIME_HELPER;
            case "Support" -> CalculationConstants.AFK_PLAYTIME_SUPPORT;
            case "Chatmod" -> CalculationConstants.AFK_PLAYTIME_CHATMOD;
            case "Overseer" -> CalculationConstants.AFK_PLAYTIME_OVERSEER;
            case "Organizer" -> CalculationConstants.AFK_PLAYTIME_ORGANIZER;
            case "Manager" -> CalculationConstants.AFK_PLAYTIME_MANAGER;
            default -> 1.0;
        };

        return afkPercentage * coefficient;
    }

    private void saveAfkPlaytimeToProductivity(Employee employee, double afkPercentage) {
        Productivity productivity = findOrCreateProductivity(employee);
        productivity.setAfkPlaytime(afkPercentage);
        productivityRepository.save(productivity);
    }

    private void saveAfkPlaytimeWithCoefToProductivityCalc(Employee employee, double afkPlaytimeWithCoef) {
        ProductivityCalc productivityCalc = findOrCreateProductivityCalc(employee);
        productivityCalc.setAfkPlaytimeCalc(afkPlaytimeWithCoef);
        productivityCalcRepository.save(productivityCalc);
    }

    @Transactional
    public void updateAnnualPlaytimeForAllEmployees() {
        LocalDate endDate = LocalDate.now().minusDays(1);

        List<Integer> employeeIds = dailyPlaytimeRepository.findAllDistinctEmployeeIds();

        resetAnnualPlaytimeForAllEmployees(employeeIds);

        employeeIds.forEach(employeeId -> {
            Employee employee = findEmployeeById(employeeId);

            LocalDate startDate = findReferenceDateForEmployeePlaytime(employee);

            calculateAndSaveAnnualPlaytimeForEmployee(employee, startDate, endDate);
        });
    }

    @Transactional
    private void resetAnnualPlaytimeForAllEmployees(List<Integer> employeeIds) {
        employeeIds.forEach(employeeId -> {
            Employee employee = findEmployeeById(employeeId);
            Productivity productivity = findOrCreateProductivity(employee);
            resetAnnualPlaytime(productivity);
        });
    }

    @Transactional
    private void resetAnnualPlaytime(Productivity productivity) {
        productivity.setAnnualPlaytime(0.0);
        productivityRepository.save(productivity);
    }

    @Transactional
    private void calculateAndSaveAnnualPlaytimeForEmployee(Employee employee, LocalDate startDate, LocalDate endDate) {
        Double totalPlaytime = dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(employee.getId(), startDate,
                endDate);
        Productivity productivity = findOrCreateProductivity(employee);
        productivity.setAnnualPlaytime(totalPlaytime != null ? totalPlaytime : 0.0);
        productivityRepository.save(productivity);
    }

    @Transactional
    public void calculateAndSavePlaytimeForAllEmployees() {
        employees.forEach(employee -> {
            List<DailyPlaytime> playtimes = dailyPlaytimeRepository.findByEmployeeId(employee.getId());

            if (!playtimes.isEmpty()) {
                calculateAndSaveAveragePlaytime(employee, playtimes);

                calculateAndSavePlaytimeWithCoefs(employee);
            }
        });
    }

    @Transactional
    private void calculateAndSaveAveragePlaytime(Employee employee, List<DailyPlaytime> playtimes) {
        LocalDate joinDate = employee.getJoinDate();
        LocalDate earliestPlaytimeDate = playtimes.stream()
                .map(DailyPlaytime::getDate)
                .min(LocalDate::compareTo)
                .orElse(LocalDate.now());

        LocalDate startDate = (joinDate.isBefore(earliestPlaytimeDate)) ? earliestPlaytimeDate : joinDate;

        LocalDate yesterday = LocalDate.now().minusDays(1);

        long daysBetween = ChronoUnit.DAYS.between(startDate, yesterday) + 1;

        double totalPlaytime = playtimes.stream()
                .mapToDouble(DailyPlaytime::getTotalPlaytime)
                .sum();

        double averagePlaytime = totalPlaytime / daysBetween;

        Productivity productivity = findOrCreateProductivity(employee);
        productivity.setPlaytime(averagePlaytime);
        productivityRepository.save(productivity);
    }

    @Transactional
    private void calculateAndSavePlaytimeWithCoefs(Employee employee) {
        ProductivityCalc productivityCalc = findOrCreateProductivityCalc(employee);
        Productivity productivity = findOrCreateProductivity(employee);

        double calculatedPlaytimeValue = calculatePlaytime(productivity.getPlaytime(), employee.getLevel());

        productivityCalc.setPlaytimeCalc(calculatedPlaytimeValue);
        productivityCalcRepository.save(productivityCalc);
    }

    @Transactional
    private double calculatePlaytime(double playtimeValue, String level) {
        double maxPTForHelper = CalculationConstants.PLAYTIME_MAX_HELPER;
        double maxPTForSupport = CalculationConstants.PLAYTIME_MAX_SUPPORT;
        double maxPTForChatMod = CalculationConstants.PLAYTIME_MAX_CHATMOD;
        double maxPTForOverseer = CalculationConstants.PLAYTIME_MAX_OVERSEER;
        double maxPTForOrganizer = CalculationConstants.PLAYTIME_MAX_ORGANIZER;
        double maxPTForManager = CalculationConstants.PLAYTIME_MAX_MANAGER;

        double ptForHelper = CalculationConstants.PLAYTIME_HELPER;
        double ptForSupport = CalculationConstants.PLAYTIME_SUPPORT;
        double ptForChatMod = CalculationConstants.PLAYTIME_CHATMOD;
        double ptForOverseer = CalculationConstants.PLAYTIME_OVERSEER;
        double ptForOrganizer = CalculationConstants.PLAYTIME_ORGANIZER;
        double ptForManager = CalculationConstants.PLAYTIME_MANAGER;

        return switch (level) {
            case "Helper" ->
                playtimeValue > maxPTForHelper ? maxPTForHelper * ptForHelper : playtimeValue * ptForHelper;
            case "Support" ->
                playtimeValue > maxPTForSupport ? maxPTForSupport * ptForSupport : playtimeValue * ptForSupport;
            case "Chatmod" ->
                playtimeValue > maxPTForChatMod ? maxPTForChatMod * ptForChatMod : playtimeValue * ptForChatMod;
            case "Overseer" ->
                playtimeValue > maxPTForOverseer ? maxPTForOverseer * ptForOverseer : playtimeValue * ptForOverseer;
            case "Organizer" ->
                playtimeValue > maxPTForOrganizer ? maxPTForOrganizer * ptForOrganizer : playtimeValue * ptForOrganizer;
            case "Manager" ->
                playtimeValue > maxPTForManager ? maxPTForManager * ptForManager : playtimeValue * ptForManager;
            default -> 0.0;
        };
    }

    // Complains

    @Transactional
    public void calculateAndSaveComplaintsForAllEmployees() {
        employees.forEach(employee -> {
            ProductivityCalc productivityCalc = findOrCreateProductivityCalc(employee);

            double totalComplaints = calculateTotalComplaintsForEmployee(employee);
            saveComplaintsInProductivityCalc(productivityCalc, totalComplaints);
        });
    }

    @Transactional
    private double calculateTotalComplaintsForEmployee(Employee employee) {
        Double totalComplaints = fetchTotalComplaints(employee);

        return totalComplaints != null ? totalComplaints : 0.0;
    }

    @Transactional
    private Double fetchTotalComplaints(Employee employee) {
        return complainsRepository.sumComplaintsByEmployeeId(employee.getId());
    }

    @Transactional
    private void saveComplaintsInProductivityCalc(ProductivityCalc productivityCalc, double totalComplaints) {
        productivityCalc.setComplainsCalc(totalComplaints);
        productivityCalcRepository.save(productivityCalc);
    }

    // Other Methods

    // permest i apacia visus 4
    @Transactional
    public void calculateAndSaveProductivity() {
        for (Employee employee : employees) {
            ProductivityCalc productivityCalc = productivityCalcRepository.findByEmployeeId(employee.getId());

            if (productivityCalc == null) {
                continue;
            }

            if ("Owner".equals(employee.getLevel()) || "Operator".equals(employee.getLevel())) {
                continue;
            }

            try {
                resetProductivityToZero(employee);

                double result = calculateProductivity(productivityCalc);

                if ("Organizer".equals(employee.getLevel())) {
                    double modifiedResult = result + 10;
                    saveProductivity(employee, modifiedResult);
                } else {
                    saveProductivity(employee, result);
                }

            } catch (Exception e) {
                throw e;
            }
        }
    }

    private void resetProductivityToZero(Employee employee) {
        Productivity productivity = productivityRepository.findByEmployeeId(employee.getId());
        if (productivity != null) {
            productivity.setProductivity(0.0);
            productivityRepository.save(productivity);
        }
    }

    @Transactional
    private double calculateProductivity(ProductivityCalc productivityCalc) {
        double serverTicketsCalc = productivityCalc.getServerTicketsCalc();
        double serverTicketsTakingCalc = productivityCalc.getServerTicketsTakingCalc();
        double discordTicketsCalc = productivityCalc.getDiscordTicketsCalc();
        double afkPlaytimeCalc = productivityCalc.getAfkPlaytimeCalc();
        double playtimeCalc = productivityCalc.getPlaytimeCalc();
        double complainsCalc = productivityCalc.getComplainsCalc();

        return ((discordTicketsCalc
                - afkPlaytimeCalc
                + playtimeCalc
                + serverTicketsCalc
                + serverTicketsTakingCalc) / 5)
                - complainsCalc;
    }

    @Transactional
    private void saveProductivity(Employee employee, double result) {
        Productivity productivity = productivityRepository.findByEmployeeId(employee.getId());
        if (productivity == null) {
            productivity = new Productivity();
            productivity.setEmployee(employee);
        }
        productivity.setProductivity(result);
        productivityRepository.save(productivity);
    }

    @Transactional
    private Employee findEmployeeById(Integer employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    @Transactional
    private Productivity findOrCreateProductivity(Employee employee) {
        return Optional.ofNullable(productivityRepository.findByEmployeeId(employee.getId()))
                .orElseGet(() -> createDefaultProductivity(employee));
    }

    @Transactional
    private Productivity createDefaultProductivity(Employee employee) {
        Productivity productivity = new Productivity();
        productivity.setEmployee(employee);
        productivity.setAnnualPlaytime(0.0);
        productivity.setServerTickets(0.0);
        productivity.setServerTicketsTaking(0.0);
        productivity.setDiscordTickets(0.0);
        productivity.setDiscordTicketsTaking(0.0);
        productivity.setPlaytime(0.0);
        productivity.setAfkPlaytime(0.0);
        productivity.setProductivity(0.0);
        productivity.setRecommendation("-");
        return productivity;
    }

    @Transactional
    private ProductivityCalc findOrCreateProductivityCalc(Employee employee) {
        return Optional.ofNullable(productivityCalcRepository.findByEmployeeId(employee.getId()))
                .orElseGet(() -> createNewProductivityCalc(employee));
    }

    @Transactional
    private ProductivityCalc createNewProductivityCalc(Employee employee) {
        ProductivityCalc productivityCalc = new ProductivityCalc();
        productivityCalc.setEmployee(employee);
        return productivityCalc;
    }
}
