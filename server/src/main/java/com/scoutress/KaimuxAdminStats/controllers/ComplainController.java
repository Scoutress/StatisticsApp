package com.scoutress.KaimuxAdminStats.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
}
