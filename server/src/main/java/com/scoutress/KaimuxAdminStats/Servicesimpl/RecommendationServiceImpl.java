package com.scoutress.KaimuxAdminStats.Servicesimpl;

import java.time.LocalDate;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Constants.CalculationConstants;
import com.scoutress.KaimuxAdminStats.Entity.Employees.Employee;
import com.scoutress.KaimuxAdminStats.Entity.Productivity;
import com.scoutress.KaimuxAdminStats.Repositories.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.Repositories.PlaytimeRepository;
import com.scoutress.KaimuxAdminStats.Repositories.ProductivityRepository;
import com.scoutress.KaimuxAdminStats.Services.RecommendationService;

@Service
public class RecommendationServiceImpl implements RecommendationService {

  private final EmployeeRepository employeeRepository;
  private final ProductivityRepository productivityRepository;
  private final PlaytimeRepository playtimeRepository;

  public RecommendationServiceImpl(EmployeeRepository employeeRepository,
      ProductivityRepository productivityRepository, PlaytimeRepository playtimeRepository) {
    this.employeeRepository = employeeRepository;
    this.productivityRepository = productivityRepository;
    this.playtimeRepository = playtimeRepository;
  }

  public String PROMOTE = "Promote";
  public String DEMOTE = "Demote";
  public String STAY = "-";
  public String DISMISS = "Dismiss";

  @Override
  public void evaluateEmployees() {
    List<Employee> employees = employeeRepository.findAll();
    LocalDate currentDate = LocalDate.now();

    for (Employee employee : employees) {
      try {
        Productivity productivity = productivityRepository.findByEmployeeId(employee.getId());

        double productivityValue = Optional.ofNullable(productivity)
            .map(Productivity::getProductivity)
            .orElse(0.0);

        String result = evaluateEmployeeByLevel(employee, productivityValue, currentDate);

        saveEvaluationResult(employee, result);

      } catch (Exception e) {
      }
    }
  }

  public String evaluateEmployeeByLevel(Employee employee, double productivityValue, LocalDate currentDate) {
    String result;
    switch (employee.getLevel()) {
      case "Helper" -> result = evaluateHelper(employee, productivityValue, currentDate);
      case "Support" -> result = evaluateSupport(employee, productivityValue, currentDate);
      case "ChatMod" -> result = evaluateChatMod(employee, productivityValue, currentDate);
      case "Overseer" -> result = evaluateOverseer(employee, productivityValue, currentDate);
      case "Manager" -> result = evaluateManager(employee, productivityValue, currentDate);
      default -> result = STAY;
    }
    return result;
  }

  @Override
  public boolean checkPlaytime(int employeeId, LocalDate startDate, LocalDate endDate) {
    double playtimeHours = playtimeRepository.sumPlaytimeByEmployeeAndDateRange(employeeId, startDate, endDate);
    return playtimeHours > CalculationConstants.MIN_ANNUAL_PLAYTIME;
  }

  @Override
  public String evaluateHelper(Employee employee, double productivityValue, LocalDate currentDate) {
    long daysSinceJoined = DAYS.between(employee.getJoinDate(), currentDate);

    if (!checkPlaytime(employee.getId(), currentDate.minusYears(1), currentDate)) {
      return DISMISS;
    }

    if (daysSinceJoined >= CalculationConstants.WORK_TIME_HELPER) {
      if (productivityValue >= CalculationConstants.PROMOTION_VALUE) {
        return PROMOTE;
      }
    }

    return STAY;
  }

  @Override
  public String evaluateSupport(Employee employee, double productivityValue, LocalDate currentDate) {
    long daysSinceJoined = DAYS.between(employee.getJoinDate(), currentDate);

    if (!checkPlaytime(employee.getId(), currentDate.minusYears(1), currentDate)) {
      return DISMISS;
    }

    if (daysSinceJoined >= CalculationConstants.WORK_TIME_SUPPORT) {
      if (productivityValue >= CalculationConstants.PROMOTION_VALUE) {
        return PROMOTE;
      } else if (productivityValue < CalculationConstants.DEMOTION_VALUE) {
        return DEMOTE;
      }
    }

    return STAY;
  }

  public String evaluateChatMod(Employee employee, double productivityValue, LocalDate currentDate) {
    long daysSinceJoined = DAYS.between(employee.getJoinDate(), currentDate);

    if (!checkPlaytime(employee.getId(), currentDate.minusYears(1), currentDate)) {
      return DISMISS;
    }

    if (daysSinceJoined >= CalculationConstants.WORK_TIME_CHATMOD) {
      if (productivityValue >= CalculationConstants.PROMOTION_VALUE) {
        return PROMOTE;
      } else if (productivityValue < CalculationConstants.DEMOTION_VALUE) {
        return DEMOTE;
      }
    }

    return STAY;
  }

  public String evaluateOverseer(Employee employee, double productivityValue, LocalDate currentDate) {
    long daysSinceJoined = DAYS.between(employee.getJoinDate(), currentDate);

    if (!checkPlaytime(employee.getId(), currentDate.minusYears(1), currentDate)) {
      return DISMISS;
    }

    if (daysSinceJoined >= CalculationConstants.WORK_TIME_OVERSEER) {
      if (productivityValue >= CalculationConstants.PROMOTION_VALUE) {
        return PROMOTE;
      } else if (productivityValue < CalculationConstants.DEMOTION_VALUE) {
        return DEMOTE;
      }
    }

    return STAY;
  }

  public String evaluateManager(Employee employee, double productivityValue, LocalDate currentDate) {

    if (!checkPlaytime(employee.getId(), currentDate.minusYears(1), currentDate)) {
      return DISMISS;
    }

    if (productivityValue < CalculationConstants.DEMOTION_VALUE) {
      return DEMOTE;
    }

    return STAY;
  }

  public void saveEvaluationResult(Employee employee, String result) {
    try {
      Productivity productivity = productivityRepository.findByEmployeeId(employee.getId());

      if (productivity == null) {
        productivity = new Productivity();
        productivity.setEmployee(employee);
      }

      productivity.setRecommendation(result);

      productivityRepository.save(productivity);

    } catch (Exception e) {
    }
  }
}