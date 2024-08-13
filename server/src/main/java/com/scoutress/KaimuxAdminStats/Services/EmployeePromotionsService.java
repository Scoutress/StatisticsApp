package com.scoutress.KaimuxAdminStats.Services;

import java.util.List;

import com.scoutress.KaimuxAdminStats.Entity.Employees.EmployeePromotionsPlus;

public interface EmployeePromotionsService {

  List<EmployeePromotionsPlus> getAllEmployeePromotionsWithEmployeeData();
}
