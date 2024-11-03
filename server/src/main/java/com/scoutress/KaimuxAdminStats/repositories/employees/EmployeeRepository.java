package com.scoutress.KaimuxAdminStats.repositories.employees;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scoutress.KaimuxAdminStats.entity.employees.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
