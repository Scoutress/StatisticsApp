package com.scoutress.KaimuxAdminStats.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scoutress.KaimuxAdminStats.entity.complaints.ComplaintsSum;
import com.scoutress.KaimuxAdminStats.repositories.complaints.ComplaintsSumRepository;

@RestController
@RequestMapping("/complains")
public class ComplainController {

  private final ComplaintsSumRepository complaintsSumRepository;

  public ComplainController(
      ComplaintsSumRepository complaintsSumRepository) {
    this.complaintsSumRepository = complaintsSumRepository;
  }

  @GetMapping("/all")
  public List<ComplaintsSum> getAllComplains() {
    return complaintsSumRepository.findAll();
  }

  @PostMapping("/add")
  public ComplaintsSum addComplain(@RequestBody ComplaintsSum complain) {
    return complaintsSumRepository.save(complain);
  }
}
