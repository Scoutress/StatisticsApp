package com.scoutress.KaimuxAdminStats.Servicesimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.Complains;
import com.scoutress.KaimuxAdminStats.Repositories.ComplainsRepository;
import com.scoutress.KaimuxAdminStats.Services.ComplainsService;

@Service
public class ComplainsServiceImpl implements ComplainsService {

  private final ComplainsRepository complainsRepository;

  public ComplainsServiceImpl(ComplainsRepository complainsRepository) {
    this.complainsRepository = complainsRepository;
  }

  @Override
  public List<Complains> getAllComplains() {
    return complainsRepository.findAll();
  }
}