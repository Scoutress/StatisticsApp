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
@RequestMapping("/complains")
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
  public List<ComplaintsSum> getAllComplainsSum() {
    return complaintsSumRepository.findAll();
  }

  @GetMapping("/all-data")
  public List<Complaints> getAllComplainsData() {
    return complaintsRepository.findAll();
  }

  @PostMapping("/add")
  public Complaints addComplain(@RequestBody Complaints complain) {
    return complaintsRepository.save(complain);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Complaints> updateComplain(
      @PathVariable Long id, @RequestBody Complaints complainDetails) {
    Optional<Complaints> optionalComplain = complaintsRepository.findById(id);

    if (!optionalComplain.isPresent()) {
      return ResponseEntity.notFound().build();
    }

    Complaints complain = optionalComplain.get();
    complain.setEmployeeId(complainDetails.getEmployeeId());
    complain.setDate(complainDetails.getDate());
    complain.setText(complainDetails.getText());
    Complaints updatedComplain = complaintsRepository.save(complain);

    return ResponseEntity.ok(updatedComplain);
  }
}
