package com.scoutress.KaimuxAdminStats.Servicesimpl.old;
// package com.scoutress.KaimuxAdminStats.Servicesimpl;

// import java.time.LocalDate;
// import static java.time.temporal.ChronoUnit.DAYS;
// import java.util.List;

// import org.springframework.stereotype.Service;

// import com.scoutress.KaimuxAdminStats.Constants.CalculationConstants;
// import com.scoutress.KaimuxAdminStats.Entity.old.Employees.Employee;
// import com.scoutress.KaimuxAdminStats.Entity.old.Productivity;
// import
// com.scoutress.KaimuxAdminStats.Repositories.old.DailyPlaytimeRepository;
// import com.scoutress.KaimuxAdminStats.Repositories.old.EmployeeRepository;
// import
// com.scoutress.KaimuxAdminStats.Repositories.old.ProductivityRepository;
// import com.scoutress.KaimuxAdminStats.Services.old.RecommendationService;

// @Service
// public class RecommendationServiceImpl implements RecommendationService {

// private final EmployeeRepository employeeRepository;
// private final ProductivityRepository productivityRepository;
// private final DailyPlaytimeRepository dailyPlaytimeRepository;

// public RecommendationServiceImpl(EmployeeRepository employeeRepository,
// ProductivityRepository productivityRepository,
// DailyPlaytimeRepository dailyPlaytimeRepository) {
// this.employeeRepository = employeeRepository;
// this.productivityRepository = productivityRepository;
// this.dailyPlaytimeRepository = dailyPlaytimeRepository;
// }

// public String PROMOTE = "Promote";
// public String DEMOTE = "Demote";
// public String STAY = "-";
// public String DISMISS = "Dismiss";

// @Override
// public void evaluateEmployees() {
// List<Employee> employees = employeeRepository.findAll();
// LocalDate currentDate = LocalDate.now();

// for (Employee employee : employees) {
// try {
// double playtimeHours =
// dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(employee.getId(),
// currentDate.minusYears(1), currentDate);
// System.out.println("Employee ID: " + employee.getId() + " Playtime Hours: " +
// playtimeHours);

// if (playtimeHours < CalculationConstants.MIN_ANNUAL_PLAYTIME) {
// saveEvaluationResult(employee, DISMISS);
// }
// } catch (Exception e) {
// e.printStackTrace(System.out);
// }
// }
// }

// public String evaluateEmployeeByLevel(Employee employee, double
// productivityValue, LocalDate currentDate) {
// switch (employee.getLevel()) {
// case "Helper" -> {
// return evaluateHelper(employee, productivityValue, currentDate);
// }
// case "Support" -> {
// return evaluateSupport(employee, productivityValue, currentDate);
// }
// case "ChatMod" -> {
// return evaluateChatMod(employee, productivityValue, currentDate);
// }
// case "Overseer" -> {
// return evaluateOverseer(employee, productivityValue, currentDate);
// }
// case "Manager" -> {
// return evaluateManager(employee, productivityValue, currentDate);
// }
// default -> {
// return STAY;
// }
// }
// }

// @Override
// public boolean checkPlaytime(int employeeId, LocalDate startDate, LocalDate
// endDate) {
// double playtimeHours =
// dailyPlaytimeRepository.sumPlaytimeByEmployeeAndDateRange(employeeId,
// startDate, endDate);
// return playtimeHours > CalculationConstants.MIN_ANNUAL_PLAYTIME;
// }

// @Override
// public String evaluateHelper(Employee employee, double productivityValue,
// LocalDate currentDate) {
// long daysSinceJoined = DAYS.between(employee.getJoinDate(), currentDate);

// if (daysSinceJoined < CalculationConstants.WORK_TIME_HELPER) {
// // Jei darbuotojas dar nepakankamai ilgai dirba, grąžiname "-"
// System.out.println("Decision: Stay (not enough work time yet).");
// return STAY;
// }

// // Jei darbuotojas dirba pakankamai ilgai, vertiname pagal produktyvumą
// if (productivityValue >= CalculationConstants.PROMOTION_VALUE) {
// System.out.println("Decision: Promote.");
// return PROMOTE;
// } else if (productivityValue < CalculationConstants.DEMOTION_VALUE) {
// System.out.println("Decision: Demote.");
// return DEMOTE;
// }

// // Jei nei vienas iš aukščiau paminėtų atvejų neįvyksta, darbuotojas lieka
// // esamame lygyje
// System.out.println("Decision: Stay.");
// return STAY;
// }

// @Override
// public String evaluateSupport(Employee employee, double productivityValue,
// LocalDate currentDate) {
// long daysSinceJoined = DAYS.between(employee.getJoinDate(), currentDate);

// if (!checkPlaytime(employee.getId(), currentDate.minusYears(1), currentDate))
// {
// return DISMISS;
// }

// if (daysSinceJoined >= CalculationConstants.WORK_TIME_SUPPORT) {
// if (productivityValue >= CalculationConstants.PROMOTION_VALUE) {
// return PROMOTE;
// } else if (productivityValue < CalculationConstants.DEMOTION_VALUE) {
// return DEMOTE;
// }
// }

// return STAY;
// }

// public String evaluateChatMod(Employee employee, double productivityValue,
// LocalDate currentDate) {
// long daysSinceJoined = DAYS.between(employee.getJoinDate(), currentDate);

// if (!checkPlaytime(employee.getId(), currentDate.minusYears(1), currentDate))
// {
// return DISMISS;
// }

// if (daysSinceJoined >= CalculationConstants.WORK_TIME_CHATMOD) {
// if (productivityValue >= CalculationConstants.PROMOTION_VALUE) {
// return PROMOTE;
// } else if (productivityValue < CalculationConstants.DEMOTION_VALUE) {
// return DEMOTE;
// }
// }

// return STAY;
// }

// public String evaluateOverseer(Employee employee, double productivityValue,
// LocalDate currentDate) {
// long daysSinceJoined = DAYS.between(employee.getJoinDate(), currentDate);

// if (!checkPlaytime(employee.getId(), currentDate.minusYears(1), currentDate))
// {
// return DISMISS;
// }

// if (daysSinceJoined >= CalculationConstants.WORK_TIME_OVERSEER) {
// if (productivityValue >= CalculationConstants.PROMOTION_VALUE) {
// return PROMOTE;
// } else if (productivityValue < CalculationConstants.DEMOTION_VALUE) {
// return DEMOTE;
// }
// }

// return STAY;
// }

// public String evaluateManager(Employee employee, double productivityValue,
// LocalDate currentDate) {

// if (!checkPlaytime(employee.getId(), currentDate.minusYears(1), currentDate))
// {
// return DISMISS;
// }

// if (productivityValue < CalculationConstants.DEMOTION_VALUE) {
// return DEMOTE;
// }

// return STAY;
// }

// public void saveEvaluationResult(Employee employee, String result) {
// try {
// Productivity productivity =
// productivityRepository.findByEmployeeId(employee.getId());

// if (productivity == null) {
// productivity = new Productivity();
// productivity.setEmployee(employee);
// }

// productivity.setRecommendation(result);

// productivityRepository.save(productivity);

// } catch (Exception e) {
// }
// }
// }