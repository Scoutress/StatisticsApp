package com.scoutress.KaimuxAdminStats.Services.old;

import java.util.List;

import com.scoutress.KaimuxAdminStats.Entity.old.Employees.EmployeePromotionsPlus;

public interface EmployeePromotionsService {

  List<EmployeePromotionsPlus> getAllEmployeePromotionsWithEmployeeData();
}
