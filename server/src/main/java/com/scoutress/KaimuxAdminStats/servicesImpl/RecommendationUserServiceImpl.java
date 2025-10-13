package com.scoutress.KaimuxAdminStats.servicesImpl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scoutress.KaimuxAdminStats.constants.CalculationConstants;
import com.scoutress.KaimuxAdminStats.constants.RecommendationUserTexts;
import com.scoutress.KaimuxAdminStats.entity.FinalStats;
import com.scoutress.KaimuxAdminStats.entity.RecommendationUser;
import com.scoutress.KaimuxAdminStats.entity.complaints.Complaints;
import com.scoutress.KaimuxAdminStats.entity.employees.Employee;
import com.scoutress.KaimuxAdminStats.repositories.FinalStatsRepository;
import com.scoutress.KaimuxAdminStats.repositories.RecommendationUserRepository;
import com.scoutress.KaimuxAdminStats.repositories.complaints.ComplaintsRepository;
import com.scoutress.KaimuxAdminStats.repositories.employees.EmployeeRepository;
import com.scoutress.KaimuxAdminStats.services.RecommendationUserService;

@Service
public class RecommendationUserServiceImpl implements RecommendationUserService {

  private static final Logger log = LoggerFactory.getLogger(RecommendationUserServiceImpl.class);

  private final EmployeeRepository employeeRepository;
  private final FinalStatsRepository finalStatsRepository;
  private final ComplaintsRepository complaintsRepository;
  private final RecommendationUserRepository recommendationUserRepository;

  public RecommendationUserServiceImpl(
      EmployeeRepository employeeRepository,
      FinalStatsRepository finalStatsRepository,
      ComplaintsRepository complaintsRepository,
      RecommendationUserRepository recommendationUserRepository) {
    this.employeeRepository = employeeRepository;
    this.finalStatsRepository = finalStatsRepository;
    this.complaintsRepository = complaintsRepository;
    this.recommendationUserRepository = recommendationUserRepository;
  }

  @Transactional
  @Override
  public void handleUserRecommendations() {
    long start = System.currentTimeMillis();
    log.info("🧠 Starting user recommendation generation...");

    List<Employee> employees = employeeRepository
        .findAll();
    if (employees.isEmpty()) {
      log.warn("No employees found, skipping recommendation generation.");
      return;
    }

    Map<Short, FinalStats> statsMap = finalStatsRepository
        .findAll()
        .stream()
        .collect(Collectors.toMap(FinalStats::getEmployeeId, s -> s, (a, b) -> a));

    Map<Short, Long> complaintsCountMap = complaintsRepository
        .findAll()
        .stream()
        .collect(Collectors.groupingBy(Complaints::getEmployeeId, Collectors.counting()));

    Map<Short, RecommendationUser> existing = recommendationUserRepository
        .findAll()
        .stream()
        .collect(Collectors.toMap(RecommendationUser::getEmployeeId, r -> r, (a, b) -> a));

    List<RecommendationUser> toSave = new ArrayList<>();

    for (Employee e : employees) {
      short id = e.getId();
      FinalStats s = statsMap.get(id);

      String level = s != null ? s.getLevel() : e.getLevel();
      double productivity = s != null ? s.getProductivity() * 100 : 0.0;
      double annualPlaytime = s != null ? s.getAnnualPlaytime() : 0.0;
      int complaints = complaintsCountMap.getOrDefault(id, 0L).intValue();
      int daysSinceJoin = e.getJoinDate() != null
          ? (int) ChronoUnit.DAYS.between(e.getJoinDate(), LocalDate.now())
          : 0;

      String recommendation = generateRecommendation(level, complaints, daysSinceJoin, annualPlaytime, productivity);

      RecommendationUser record = existing.getOrDefault(id, new RecommendationUser());
      record.setEmployeeId(id);
      record.setText(recommendation);
      toSave.add(record);
    }

    recommendationUserRepository.saveAll(toSave);

    long duration = System.currentTimeMillis() - start;
    log.info("✅ Generated {} user recommendations in {} ms.", toSave.size(), duration);
  }

  private String generateRecommendation(String level, int complaints, int daysSinceJoin, double annualPlaytime,
      double productivity) {
    if (annualPlaytime < 10)
      return RecommendationUserTexts.LOW_ANNUAL_DISMISS;
    if (complaints > 15)
      return RecommendationUserTexts.COMPLAINTS;

    return switch (level) {
      case "Owner", "Operator" -> RecommendationUserTexts.GREAT_JOB;
      case "Helper" -> evaluateLevel(productivity, daysSinceJoin, CalculationConstants.WORK_TIME_HELPER);
      case "Support" -> evaluateLevel(productivity, daysSinceJoin, CalculationConstants.WORK_TIME_SUPPORT);
      case "ChatMod" -> evaluateLevel(productivity, daysSinceJoin, CalculationConstants.WORK_TIME_CHATMOD);
      case "Overseer" -> evaluateLevel(productivity, daysSinceJoin, CalculationConstants.WORK_TIME_OVERSEER);
      case "Manager" -> {
        if (productivity < 15)
          yield RecommendationUserTexts.LOW_PRODUCTIVITY_DEMOTE;
        if (productivity > 85)
          yield RecommendationUserTexts.GREAT_JOB;
        yield RecommendationUserTexts.OKAY_STATS;
      }
      default -> RecommendationUserTexts.OKAY_STATS;
    };
  }

  private String evaluateLevel(double productivity, int daysSinceJoin, int requiredDays) {
    if (productivity < 15)
      return RecommendationUserTexts.LOW_PRODUCTIVITY_DEMOTE;
    if (productivity > 85 && daysSinceJoin >= requiredDays)
      return RecommendationUserTexts.PROMOTE_PERC;
    return RecommendationUserTexts.OKAY_STATS;
  }

  @Override
  public Map<String, Object> getEmployeeRecommendation(Short employeeId) {
    RecommendationUser rec = recommendationUserRepository.findByEmployeeId(employeeId);
    Map<String, Object> result = new HashMap<>();
    result.put("text", rec != null ? rec.getText() : "No recommendation found");
    return result;
  }
}
