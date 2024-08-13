package com.scoutress.KaimuxAdminStats.Servicesimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.Entity.Employees.EmployeePromotionsPlus;
import com.scoutress.KaimuxAdminStats.Repositories.EmployeePromotionsRepository;
import com.scoutress.KaimuxAdminStats.Services.EmployeePromotionsService;

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
