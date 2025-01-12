package com.scoutress.KaimuxAdminStats.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scoutress.KaimuxAdminStats.entity.complaints.Complaints;
import com.scoutress.KaimuxAdminStats.entity.complaints.ComplaintsSum;
import com.scoutress.KaimuxAdminStats.repositories.complaints.ComplaintsRepository;
import com.scoutress.KaimuxAdminStats.repositories.complaints.ComplaintsSumRepository;

@RestController
@RequestMapping("/complaints")
public class ComplainController {

  private final ComplaintsSumRepository complaintsSumRepository;
  private final ComplaintsRepository complaintsRepository;

  public ComplainController(
      ComplaintsSumRepository complaintsSumRepository,
      ComplaintsRepository complaintsRepository) {
    this.complaintsSumRepository = complaintsSumRepository;
    this.complaintsRepository = complaintsRepository;
  }

  @GetMapping("/all-sums")
  public List<ComplaintsSum> getAllComplaintsSum() {
    return complaintsSumRepository.findAll();
  }

  @GetMapping("/all-data")
  public List<Complaints> getAllComplaintsData() {
    return complaintsRepository.findAll();
  }

  @PutMapping("/{id}")
  public ResponseEntity<Complaints> updateComplain(
      @PathVariable Long id, @RequestBody Complaints complaintDetails) {
    Optional<Complaints> optionalComplaint = complaintsRepository.findById(id);

    if (!optionalComplaint.isPresent()) {
      return ResponseEntity.notFound().build();
    }

    Complaints complaint = optionalComplaint.get();
    complaint.setEmployeeId(complaintDetails.getEmployeeId());
    complaint.setDate(complaintDetails.getDate());
    complaint.setText(complaintDetails.getText());
    Complaints updatedComplain = complaintsRepository.save(complaint);

    return ResponseEntity.ok(updatedComplain);
  }

  @PostMapping("/add")
  public ResponseEntity<Complaints> addComplaint(@RequestBody Complaints complaint) {
    try {
      Complaints savedComplaint = complaintsRepository.save(complaint);
      return ResponseEntity.ok(savedComplaint);
    } catch (Exception e) {
      return ResponseEntity.status(500).body(null);
    }
  }
}
