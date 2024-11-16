package com.scoutress.KaimuxAdminStats.servicesimpl.productivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.productivity.ObjectiveProductivity;
import com.scoutress.KaimuxAdminStats.entity.productivity.Productivity;
import com.scoutress.KaimuxAdminStats.entity.productivity.SubjectiveProductivity;
import com.scoutress.KaimuxAdminStats.repositories.productivity.ObjectiveProductivityRepository;
import com.scoutress.KaimuxAdminStats.repositories.productivity.ProductivityRepository;
import com.scoutress.KaimuxAdminStats.repositories.productivity.SubjectiveProductivityRepository;
import com.scoutress.KaimuxAdminStats.services.productivity.ProductivityService;

@Service
public class ProductivityServiceImpl implements ProductivityService {

  private final ObjectiveProductivityRepository objectiveProductivityRepository;
  private final SubjectiveProductivityRepository subjectiveProductivityRepository;
  private final ProductivityRepository productivityRepository;

  public ProductivityServiceImpl(
      ObjectiveProductivityRepository objectiveProductivityRepository,
      SubjectiveProductivityRepository subjectiveProductivityRepository,
      ProductivityRepository productivityRepository) {
    this.objectiveProductivityRepository = objectiveProductivityRepository;
    this.subjectiveProductivityRepository = subjectiveProductivityRepository;
    this.productivityRepository = productivityRepository;
  }

  @Override
  public void calculateProductivity() {
    List<ObjectiveProductivity> objProd = objectiveProductivityRepository.findAll();
    List<SubjectiveProductivity> subjProd = subjectiveProductivityRepository.findAll();

    Map<Short, List<Double>> objectiveValues = objProd.stream()
        .collect(Collectors.groupingBy(
            ObjectiveProductivity::getAid,
            Collectors.mapping(ObjectiveProductivity::getValue, Collectors.toList())));

    Map<Short, List<Double>> subjectiveValues = subjProd.stream()
        .collect(Collectors.groupingBy(
            SubjectiveProductivity::getAid,
            Collectors.mapping(SubjectiveProductivity::getValue, Collectors.toList())));

    Set<Short> allAids = new HashSet<>();
    allAids.addAll(objectiveValues.keySet());
    allAids.addAll(subjectiveValues.keySet());

    List<Productivity> productivityResults = new ArrayList<>();

    for (Short aid : allAids) {
      List<Double> objValues = objectiveValues.getOrDefault(aid, Collections.emptyList());
      List<Double> subjValues = subjectiveValues.getOrDefault(aid, Collections.emptyList());

      double objAvg = objValues.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
      double subjAvg = subjValues.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

      double finalValue = (objAvg + subjAvg) / 2;

      Productivity productivity = new Productivity();
      productivity.setAid(aid);
      productivity.setValue(finalValue);
      productivityResults.add(productivity);
    }

    productivityRepository.saveAll(productivityResults);
  }
}
