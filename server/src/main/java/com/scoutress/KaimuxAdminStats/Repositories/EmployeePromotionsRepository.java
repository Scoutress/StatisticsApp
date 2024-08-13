package com.scoutress.KaimuxAdminStats.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.Entity.Employees.EmployeePromotions;

public interface EmployeePromotionsRepository extends JpaRepository<EmployeePromotions, Long> {
  EmployeePromotions findByEmployeeId(Integer employeeId);
}