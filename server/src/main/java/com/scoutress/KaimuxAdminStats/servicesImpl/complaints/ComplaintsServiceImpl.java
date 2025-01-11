package com.scoutress.KaimuxAdminStats.servicesImpl.complaints;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.complaints.Complaints;
import com.scoutress.KaimuxAdminStats.entity.complaints.ComplaintsSum;
import com.scoutress.KaimuxAdminStats.repositories.complaints.ComplaintsRepository;
import com.scoutress.KaimuxAdminStats.repositories.complaints.ComplaintsSumRepository;
import com.scoutress.KaimuxAdminStats.services.complaints.ComplaintsService;

@Service
public class ComplaintsServiceImpl implements ComplaintsService {

  private final ComplaintsRepository complaintsRepository;
  private final ComplaintsSumRepository complaintsSumRepository;

  public ComplaintsServiceImpl(
      ComplaintsRepository complaintsRepository,
      ComplaintsSumRepository complaintsSumRepository) {
    this.complaintsRepository = complaintsRepository;
    this.complaintsSumRepository = complaintsSumRepository;
  }

  @Override
  public void calculateComplaintsPerEachEmployee() {
    List<Complaints> rawData = getAllComplaints();

    if (rawData != null && !rawData.isEmpty()) {
      List<Short> allEmployees = getAllEmployeesFromComplaintsData(rawData);

      if (allEmployees != null && !allEmployees.isEmpty()) {

        for (Short employee : allEmployees) {
          int allComplaintsForThisEmployee = calculateAllComplaintsForThisEmployee(rawData, employee);
          saveComplaintsSumForThisEmployee(allComplaintsForThisEmployee, employee);
        }
      }
    }
  }

  public List<Complaints> getAllComplaints() {
    return complaintsRepository.findAll();
  }

  public List<Short> getAllEmployeesFromComplaintsData(List<Complaints> rawData) {
    return rawData
        .stream()
        .map(Complaints::getEmployeeId)
        .distinct()
        .sorted()
        .toList();
  }

  public int calculateAllComplaintsForThisEmployee(List<Complaints> rawData, Short employee) {
    return (int) rawData
        .stream()
        .filter(messages -> messages.getEmployeeId().equals(employee))
        .count();
  }

  public void saveComplaintsSumForThisEmployee(int allComplaintsForThisEmployee, Short employeeId) {
    ComplaintsSum existingRecord = complaintsSumRepository.findByEmployeeId(employeeId);

    if (existingRecord != null) {
      existingRecord.setValue(allComplaintsForThisEmployee);
      complaintsSumRepository.save(existingRecord);
    } else {
      ComplaintsSum newRecord = new ComplaintsSum();
      newRecord.setEmployeeId(employeeId);
      newRecord.setValue(allComplaintsForThisEmployee);
      complaintsSumRepository.save(newRecord);
    }
  }
}
