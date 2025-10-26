package com.scoutress.KaimuxAdminStats.services;

import java.util.List;

public interface EmployeeDataService {

  List<Short> checkNessesaryEmployeeData();

  void removeNotEmployeesData();
}
