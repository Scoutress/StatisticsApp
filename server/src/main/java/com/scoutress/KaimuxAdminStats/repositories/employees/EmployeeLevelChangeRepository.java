package com.scoutress.KaimuxAdminStats.repositories.employees;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeLevelChange;

public interface EmployeeLevelChangeRepository extends JpaRepository<EmployeeLevelChange, Long> {
}
