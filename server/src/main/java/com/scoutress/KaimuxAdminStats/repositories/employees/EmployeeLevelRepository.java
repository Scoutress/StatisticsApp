package com.scoutress.KaimuxAdminStats.repositories.employees;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeLevel;

public interface EmployeeLevelRepository extends JpaRepository<EmployeeLevel, Long> {

  EmployeeLevel findByAid(Short aid);
}
