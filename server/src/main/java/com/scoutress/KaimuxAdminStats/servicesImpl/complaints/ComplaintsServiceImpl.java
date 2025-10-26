package com.scoutress.KaimuxAdminStats.servicesImpl.complaints;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.complaints.Complaints;
import com.scoutress.KaimuxAdminStats.entity.complaints.ComplaintsSum;
import com.scoutress.KaimuxAdminStats.repositories.complaints.ComplaintsRepository;
import com.scoutress.KaimuxAdminStats.repositories.complaints.ComplaintsSumRepository;
import com.scoutress.KaimuxAdminStats.services.complaints.ComplaintsService;

@Service
public class ComplaintsServiceImpl implements ComplaintsService {

  private static final Logger log = LoggerFactory.getLogger(ComplaintsServiceImpl.class);

  private final ComplaintsRepository complaintsRepository;
  private final ComplaintsSumRepository complaintsSumRepository;

  public ComplaintsServiceImpl(
      ComplaintsRepository complaintsRepository,
      ComplaintsSumRepository complaintsSumRepository) {
    this.complaintsRepository = complaintsRepository;
    this.complaintsSumRepository = complaintsSumRepository;
  }

  @Override
  public void handleComplaints() {
    log.info("=== Starting complaints processing ===");

    List<Complaints> rawData = getAllComplaints();
    log.debug("Fetched {} raw complaints records from database.", rawData != null ? rawData.size() : 0);

    if (rawData == null || rawData.isEmpty()) {
      log.warn("No complaints data found. Skipping processing.");
      return;
    }

    List<Short> allEmployees = getAllEmployeesFromComplaintsData(rawData);
    log.debug("Found {} distinct employees in complaints data.", allEmployees.size());

    if (allEmployees.isEmpty()) {
      log.warn("No employees found in complaints data. Skipping processing.");
      return;
    }

    for (Short employee : allEmployees) {
      try {
        int allComplaintsForThisEmployee = calculateAllComplaintsForThisEmployee(rawData, employee);
        log.debug("Employee ID {} has {} complaints.", employee, allComplaintsForThisEmployee);

        saveComplaintsSumForThisEmployee(allComplaintsForThisEmployee, employee);
      } catch (Exception e) {
        log.error("❌ Error processing complaints for employee ID {}: {}", employee, e.getMessage(), e);
      }
    }

    log.info("✅ Complaints processing completed successfully.");
  }

  private List<Complaints> getAllComplaints() {
    return complaintsRepository.findAll();
  }

  private List<Short> getAllEmployeesFromComplaintsData(List<Complaints> rawData) {
    return rawData
        .stream()
        .map(Complaints::getEmployeeId)
        .distinct()
        .sorted()
        .toList();
  }

  private int calculateAllComplaintsForThisEmployee(List<Complaints> rawData, Short employee) {
    return (int) rawData
        .stream()
        .filter(messages -> messages.getEmployeeId().equals(employee))
        .count();
  }

  private void saveComplaintsSumForThisEmployee(int allComplaintsForThisEmployee, Short employeeId) {
    ComplaintsSum existingRecord = complaintsSumRepository.findByEmployeeId(employeeId);

    if (existingRecord != null) {
      existingRecord.setValue(allComplaintsForThisEmployee);
      complaintsSumRepository.save(existingRecord);
      log.debug("Updated complaint summary for employee ID {} ({} complaints).", employeeId,
          allComplaintsForThisEmployee);
    } else {
      ComplaintsSum newRecord = new ComplaintsSum();
      newRecord.setEmployeeId(employeeId);
      newRecord.setValue(allComplaintsForThisEmployee);
      complaintsSumRepository.save(newRecord);
      log.debug("Inserted new complaint summary for employee ID {} ({} complaints).", employeeId,
          allComplaintsForThisEmployee);
    }
  }
}
