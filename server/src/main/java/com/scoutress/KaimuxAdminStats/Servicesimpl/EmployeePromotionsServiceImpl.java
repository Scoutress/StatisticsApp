package com.scoutress.KaimuxAdminStats.Servicesimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.old.Employees.EmployeePromotionsPlus;
import com.scoutress.KaimuxAdminStats.Repositories.old.EmployeePromotionsRepository;
import com.scoutress.KaimuxAdminStats.Services.old.EmployeePromotionsService;

@Service
public class EmployeePromotionsServiceImpl implements EmployeePromotionsService {

  private final EmployeePromotionsRepository employeePromotionsRepository;

  public EmployeePromotionsServiceImpl(EmployeePromotionsRepository employeePromotionsRepository) {
    this.employeePromotionsRepository = employeePromotionsRepository;
  }

  @Override
  public List<EmployeePromotionsPlus> getAllEmployeePromotionsWithEmployeeData() {
    return employeePromotionsRepository.findAllEmployeePromotionsWithEmployeeData();
  }
}
