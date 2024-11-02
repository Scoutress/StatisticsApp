package com.scoutress.KaimuxAdminStats.Controllers;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scoutress.KaimuxAdminStats.Entity.old.Complains;
import com.scoutress.KaimuxAdminStats.Repositories.old.ComplainsRepository;

@RestController
@RequestMapping("/complains")
@CrossOrigin(origins = "http://localhost:5173")
public class ComplainsController {

  private final ComplainsRepository complainsRepository;

  public ComplainsController(ComplainsRepository complainsRepository) {
    this.complainsRepository = complainsRepository;
  }

  @GetMapping("/all")
  public List<Complains> getAllComplains() {
    return complainsRepository.findAll();
  }
}
